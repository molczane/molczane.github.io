package org.dotnet.app.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.browser.window
import org.dotnet.app.viewModel.CarRentalAppViewModel
//import org.dotnet.app.dataSource.cars
import org.dotnet.app.model.Car

@Composable
fun RentCarScreen(viewModel: CarRentalAppViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val isSignedIn = viewModel.isUserLoggedIn.collectAsState()

    var cars by remember { mutableStateOf<List<Car>>(emptyList()) }

    var isLoginDialogShown by remember { mutableStateOf(false) }

    var isValuationDialogShown by remember { mutableStateOf(false) }

    //var areCarsLoaded by remember { mutableStateOf(false) }

    var selectedCar : Car? by remember { mutableStateOf(null) }

    val valuationResult by viewModel.valuationResult.collectAsState()

    var isCarRented by remember { mutableStateOf(false) }

    var currentUrl by remember { mutableStateOf(window.location.href) }

    val currentCarPage = viewModel.currentCarPage.collectAsState()

    val areCarsLoaded = viewModel.areCarsLoaded.collectAsState()

    // Filter states
    var selectedBrand by remember { mutableStateOf<String?>(null) }
    var selectedModel by remember { mutableStateOf<String?>(null) }
    var selectedYear by remember { mutableStateOf<String?>(null) }
    var selectedType by remember { mutableStateOf<String?>(null) }
    var selectedLocation by remember { mutableStateOf<String?>(null) }

//    LaunchedEffect(Unit) {
//        viewModel.updateCars()
//    }

    // observe changes in user login status
    LaunchedEffect(currentUrl) {
        if (currentUrl.contains("code=")) {
            viewModel.isDuringServerCheck.value = true
            val code = window.location.search
                .substringAfter("code=")
                .substringBefore("&")
            viewModel.sendAuthCodeToBackend(code)
            window.history.replaceState(null, "", window.location.pathname)
        }
    }

    // Observe changes in cars list

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wypożyczalnia Samochodów") },
                backgroundColor = MaterialTheme.colors.primaryVariant,
                actions = {
                    if(!isSignedIn.value) {
                        Button(
                            onClick = { isLoginDialogShown = true },
                            elevation = ButtonDefaults.elevation(defaultElevation = 15.dp),
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text("Zaloguj się")
                        }
                    }
                    if(isSignedIn.value) {
                        Button(
                            onClick = { viewModel.logout() },
                            elevation = ButtonDefaults.elevation(defaultElevation = 15.dp),
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text("Wyloguj się")
                        }
                    }
//                    Button(
//                        onClick = { isValuationDialogShown = true },
//                        elevation = ButtonDefaults.elevation(defaultElevation = 15.dp),
//                        modifier = Modifier.padding(12.dp)
//                    ) {
//                        Text("Show Valuation Dialog")
//                    }
                }
            )
        },
        content = { innerPadding ->
            Row (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Filtering Sidebar (20% width)
                Card(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.2f)  // Changed to use fillMaxWidth with fraction
                        .padding(8.dp),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            "Filtry",
                            style = MaterialTheme.typography.h6,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Brand Filter
                        FilterSection(
                            title = "Marka",
                            options = uiState.listOfCars.map { it.producer }.distinct().sorted(),
                            selectedOption = selectedBrand,
                            onOptionSelected = {
                                selectedBrand = it
                                selectedModel = null // Reset model when brand changes
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Model Filter (dependent on selected brand)
                        if (selectedBrand != null) {
                            FilterSection(
                                title = "Model",
                                options = uiState.listOfCars
                                    .filter { it.producer == selectedBrand }
                                    .map { it.model }
                                    .distinct()
                                    .sorted(),
                                selectedOption = selectedModel,
                                onOptionSelected = { selectedModel = it }
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Type Filter
                        FilterSection(
                            title = "Typ",
                            options = uiState.listOfCars.map { it.type }.distinct().sorted(),
                            selectedOption = selectedType,
                            onOptionSelected = { selectedType = it }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Year Filter
                        FilterSection(
                            title = "Rok produkcji",
                            options = uiState.listOfCars.map { it.yearOfProduction }.distinct().sorted(),
                            selectedOption = selectedYear,
                            onOptionSelected = { selectedYear = it }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Location Filter
                        FilterSection(
                            title = "Lokalizacja",
                            options = uiState.listOfCars.map { it.location }.distinct().sorted(),
                            selectedOption = selectedLocation,
                            onOptionSelected = { selectedLocation = it }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Reset Filters Button
                        Button(
                            onClick = {
                                selectedBrand = null
                                selectedModel = null
                                selectedYear = null
                                selectedType = null
                                selectedLocation = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Resetuj filtry")
                        }
                    }
                }

                Column (
                    modifier = Modifier
                        .weight(1f)  // This will take up the remaining space
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!areCarsLoaded.value) { // time
                        Column(Modifier.padding(innerPadding)) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colors.onPrimary
                            )
                            Footer()
                        }
                    } else {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (viewModel.currentCarPage.value.isNotEmpty()) {
                                viewModel.currentCarPage.value.forEach { car ->
                                    CarDetailsCard(
                                        car = car,
                                        modifier = Modifier.fillMaxWidth(0.5f)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                }

                                PaginationControls(
                                    currentPage = viewModel.currentPageNumber.collectAsState().value,
                                    totalPages = viewModel.pagesCount.collectAsState().value,
                                    onPageSelected = { newPage ->
                                        viewModel.updatePageNumber(newPage)
                                        viewModel.getPage(newPage)
                                    }
                                )
                            } else {
                                Text("Brak dostępnych samochodów")
                            }

//                    var selectedBrand by remember { mutableStateOf<String?>(null) }
//                    var selectedModel by remember { mutableStateOf<String?>(null) }
//                    val brands = cars.map { it.producer }.distinct().sorted()
//                    val models = cars.filter { it.producer == selectedBrand }.map { it.model }.distinct().sorted()
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    // Dropdown for car brand selection
//                    DropdownMenu(
//                        label = "Wybierz markę",
//                        options = brands,
//                        selectedOption = selectedBrand,
//                        onOptionSelected = { brand ->
//                            selectedBrand = brand
//                            selectedModel = null  // Reset model selection when brand changes
//                        },
//                        modifier = Modifier.fillMaxWidth(0.5f) // Set dropdown width to half of the screen
//                    )
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    // Dropdown for car model selection
//                    if (selectedBrand != null) {
//                        DropdownMenu(
//                            label = "Wybierz model",
//                            options = models,
//                            selectedOption = selectedModel,
//                            onOptionSelected = { model -> selectedModel = model },
//                            modifier = Modifier.fillMaxWidth(0.5f) // Set dropdown width to half of the screen
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    // Display selected car details as Card only when model is selected
//                    selectedCar = cars.find { it.producer == selectedBrand && it.model == selectedModel }
//                    if (selectedCar != null) {
//                        CarDetailsCard(
//                            car = selectedCar!!,
//                            modifier = Modifier.fillMaxWidth(0.5f)
//                        ) // Set Card width to half of the screen
//
//                        Spacer(modifier = Modifier.height(16.dp))
//
//                        // "Wypożycz Samochód" button
//                        Button(
//                            onClick = {
//                                isValuationDialogShown = true
//                            },
//                            modifier = Modifier.fillMaxWidth(.25f) // Set button width to half of the screen
//                        ) {
//                            Text("Wyceń wypożyczenie")
//                        }
//                    } else {
//                        Text("Brak wyników", style = MaterialTheme.typography.body1)
//                    }
//
//                    Spacer(modifier = Modifier.height(16.dp))

//                    if(valuationResult != null) {
//                        OfferView(
//                            valuationResult!!,
//                            onTimerEnd = { viewModel.resetValuationResult() },
//                            onRent = { isCarRented = it },
//                            onRentClick = {
//                                if(viewModel.user != null) {
//                                    viewModel.user?.let { user ->
//                                        viewModel.requestRent(
//                                            car = selectedCar!!,
//                                            user = user,
//                                            onRent = { isCarRented = it },
//                                            startDate = "2024-12-10",
//                                            endDate = "2024-12-12"
//                                        )
//                                    }
//                                }
//                                else {
//                                    isLoginDialogShown = true
//                                }
//                            },
//                        )
//                    }

//                    if (isCarRented) {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(innerPadding),
//                            contentAlignment = Alignment.Center // Centers the content within the Box
//                        ) {
//                            Card(
//                                modifier = Modifier
//                                    .fillMaxWidth(0.5f) // 50% of the screen width
//                                    .padding(16.dp),
//                                elevation = 8.dp
//                            ) {
//                                Column(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .padding(16.dp),
//                                    horizontalAlignment = Alignment.CenterHorizontally
//                                ) {
//                                    Text(
//                                        text = "Samochód wynajęty!",
//                                        style = MaterialTheme.typography.h6,
//                                        color = MaterialTheme.colors.primary
//                                    )
//                                    Spacer(modifier = Modifier.height(8.dp))
//                                    Text(
//                                        text = "Dziękujemy za wynajęcie ${selectedCar!!.producer} ${selectedCar!!.model}.",
//                                        style = MaterialTheme.typography.body1,
//                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
//                                        textAlign = TextAlign.Center
//                                    )
//                                    Spacer(modifier = Modifier.height(16.dp))
//                                    Button(
//                                        onClick = {
//                                            // Handle additional actions, e.g., navigate to home or details
//                                        },
//                                        modifier = Modifier.fillMaxWidth()
//                                    ) {
//                                        Text("Powrót do Strony Głównej")
//                                    }
//                                }
//                            }
//                        }
//                    }

                            // Footer at the bottom
                        }
                        Footer()
                    }
                }
            }
        }
    )
    if(isLoginDialogShown) {
        AlertDialog(
            onDismissRequest = { isLoginDialogShown = false },
            title = {
            },
            text = {
                LoginScreen(
                    onLoginSuccess = {},
                    viewModel = viewModel
                )
            },
            confirmButton = {
                // Optional: Add a custom confirm button if needed
            },
            dismissButton = {
                Button(onClick = { isLoginDialogShown = false }) {
                    Text("Anuluj")
                }
            }
        )
    }
    if(viewModel.isDuringServerCheck.value) {
        AlertDialog(
            onDismissRequest = {  },
            title = {
            },
            text = {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colors.onPrimary
                )
            },
            confirmButton = {
            },
            dismissButton = {
            }
        )
    }
    if(isValuationDialogShown) {
        AlertDialog(
            onDismissRequest = { isValuationDialogShown = false },
            title = {
            },
            text = {
                selectedCar?.let { car ->
                    ValuationScreen(
                        {   startDate, endDate, carB ->
                            selectedCar?.let { viewModel.requestValuation(startDate, endDate, it) }
                        },
                        valuationResult = "",
                        car = car
                    )
                }
            },
            confirmButton = {
                // Optional: Add a custom confirm button if needed
            },
            dismissButton = {
                Button(onClick = { isValuationDialogShown = false }) {
                    Text("Anuluj")
                }
            }
        )
    }
}

@Composable
fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPageSelected: (Int) -> Unit
) {
    if (totalPages <= 1) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous page button
        Button(
            onClick = { onPageSelected(currentPage - 1) },
            enabled = currentPage > 1,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Text("<-")
        }

        // Page numbers
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val visiblePages = calculateVisiblePages(currentPage, totalPages)

            visiblePages.forEach { pageNum ->
                if (pageNum == -1) {
                    // Show ellipsis
                    Text(
                        "...",
                        modifier = Modifier.padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.body1
                    )
                } else {
                    Button(
                        onClick = { onPageSelected(pageNum) },
                        colors = if (pageNum == currentPage) {
                            ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.primary,
                                contentColor = MaterialTheme.colors.onPrimary
                            )
                        } else {
                            ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.surface,
                                contentColor = MaterialTheme.colors.onSurface
                            )
                        },
                        modifier = Modifier.width(48.dp)
                    ) {
                        Text(pageNum.toString())
                    }
                }
            }
        }

        // Next page button
        Button(
            onClick = { onPageSelected(currentPage + 1) },
            enabled = currentPage < totalPages,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Text("->")
        }
    }
}

private fun calculateVisiblePages(currentPage: Int, totalPages: Int): List<Int> {
    if (totalPages <= 7) {
        return (1..totalPages).toList()
    }

    val visiblePages = mutableListOf<Int>()

    // Always show first page
    visiblePages.add(1)

    if (currentPage > 3) {
        visiblePages.add(-1) // Add ellipsis
    }

    // Add pages around current page
    val start = maxOf(2, currentPage - 1)
    val end = minOf(totalPages - 1, currentPage + 1)

    for (i in start..end) {
        visiblePages.add(i)
    }

    if (currentPage < totalPages - 2) {
        visiblePages.add(-1) // Add ellipsis
    }

    // Always show last page
    visiblePages.add(totalPages)

    return visiblePages
}

@Composable
private fun FilterSection(
    title: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        var expanded by remember { mutableStateOf(false) }

        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = selectedOption ?: "Wybierz $title",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                ) {
                    Text(option)
                }
            }
        }
    }
}