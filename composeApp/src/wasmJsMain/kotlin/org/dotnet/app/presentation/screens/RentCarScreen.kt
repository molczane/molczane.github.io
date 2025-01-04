package org.dotnet.app.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dotnetwebapp.composeapp.generated.resources.*
import dotnetwebapp.composeapp.generated.resources.Res
import kotlinx.browser.window
import org.dotnet.app.viewModel.CarRentalAppViewModel
import org.dotnet.app.domain.Car
import org.dotnet.app.presentation.components.CarDetailsCard
import org.dotnet.app.presentation.components.FilterSection
import org.dotnet.app.presentation.components.Footer
import org.dotnet.app.presentation.components.PaginationControls
import org.jetbrains.compose.resources.*

@Composable
fun RentCarScreen(viewModel: CarRentalAppViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val isSignedIn = viewModel.isUserLoggedIn.collectAsState()

    var isValuationDialogShown by remember { mutableStateOf(false) }

    val selectedCar : Car? by remember { mutableStateOf(null) }

    val valuationResult by viewModel.valuationResult.collectAsState()

    var isCarRented by remember { mutableStateOf(false) }

    val currentUrl by remember { mutableStateOf(window.location.href) }

    // observe changes in user login status - TODO() - I think this should be moved out of this composable
    LaunchedEffect(currentUrl) {
        if (currentUrl.contains("code=")) {
            println("sending authentication code to backend!")

            viewModel.isDuringServerCheck.value = true
            val code = window.location.search
                .substringAfter("code=")
                .substringBefore("&")
            println("code:")
            println(code)
            viewModel.sendAuthCodeToBackend(code)
            window.history.replaceState(null, "", window.location.pathname)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wypożyczalnia Samochodów") },
                backgroundColor = MaterialTheme.colors.primaryVariant,
                actions = {
                    IconButton(
                        onClick = { /* DO NOTHING */ },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        val carIcon = Res.drawable.directions_car
                        Icon(
                            painter = painterResource(carIcon),
                            contentDescription = "Profil użytkownika"
                        )
                    }

                    IconButton(
                        onClick = { /* DO NOTHING */ },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        val userIcon = Res.drawable.account_circle
                        Icon(
                            painter = painterResource(userIcon),
                            contentDescription = "Profil użytkownika"
                        )
                    }
                    if(!uiState.isUserLoggedIn) {
                        Button(
                            onClick = { viewModel.toggleLoginDialog(true) },
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
                    FilterSection(
                        uiState = uiState,
                        onBrandSelected = { viewModel.updateSelectedBrand(it) },
                        onModelSelected = { viewModel.updateSelectedModel(it) },
                        onYearSelected = { viewModel.updateSelectedYear(it) },
                        onTypeSelected = { viewModel.updateSelectedType(it) },
                        onLocationSelected = { viewModel.updateSelectedLocation(it) },
                        onResetFilters = { viewModel.resetFilters() }
                    )
                }

                Column (
                    modifier = Modifier
                        .weight(1f)  // This will take up the remaining space
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (uiState.isLoading) { // time
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colors.primary
                            )
                            PaginationControls(
                                currentPage = uiState.currentPageNumber,
                                totalPages = uiState.totalPages,
                                onPageSelected = {
                                    /* DO NOTHING */
                                }
                            )
                            Footer()
                        }
                    } else {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally

                        ) {
                            if (uiState.currentCarPage.isNotEmpty()) {
                                uiState.currentCarPage.forEach { car ->
                                    CarDetailsCard(
                                        car = car,
                                        modifier = Modifier.fillMaxWidth(0.5f)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                }

                                PaginationControls(
                                    currentPage = uiState.currentPageNumber,
                                    totalPages = uiState.totalPages,
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
                            Footer()
                        }
                    }
                }
            }
        }
    )

    // Login dialog window
    if(uiState.isLoginDialogShown) {
        AlertDialog(
            onDismissRequest = { viewModel.toggleLoginDialog(false) },
            title = { Text("Login") },
            text = {
                LoginScreen(viewModel = viewModel)
            },
            confirmButton = {
                Button(onClick = { viewModel.toggleLoginDialog(false) }) {
                    Text("Close")
                }
            }
        )
    }

    // Valuation Dialog Window
    if(uiState.isValuationDialogShown) {
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