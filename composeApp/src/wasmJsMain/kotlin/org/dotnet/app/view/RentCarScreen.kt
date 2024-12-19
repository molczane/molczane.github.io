package org.dotnet.app.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    var areCarsLoaded by remember { mutableStateOf(false) }

    var selectedCar : Car? by remember { mutableStateOf(null) }

    val valuationResult by viewModel.valuationResult.collectAsState()

    var isCarRented by remember { mutableStateOf(false) }

    var currentUrl by remember { mutableStateOf(window.location.href) }

    LaunchedEffect(Unit) {
        viewModel.updateCars()
    }

    LaunchedEffect(currentUrl) {
        viewModel.isDuringServerCheck.value = true
        if (currentUrl.contains("code=")) {
            val code = window.location.search
                .substringAfter("code=")
                .substringBefore("&")
            viewModel.sendAuthCodeToBackend(code)
            window.history.replaceState(null, "", window.location.pathname)
        }
    }

    // Observe changes in cars list
    LaunchedEffect(uiState.listOfCars) {
        cars = uiState.listOfCars
        areCarsLoaded = cars.isNotEmpty()
    }

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
            if(!areCarsLoaded) { // time
                Column(Modifier.padding(innerPadding)) {
                    Footer()
                }
            }
            else {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    var selectedBrand by remember { mutableStateOf<String?>(null) }
                    var selectedModel by remember { mutableStateOf<String?>(null) }
                    val brands = cars.map { it.producer }.distinct().sorted()
                    val models = cars.filter { it.producer == selectedBrand }.map { it.model }.distinct().sorted()

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dropdown for car brand selection
                    DropdownMenu(
                        label = "Wybierz markę",
                        options = brands,
                        selectedOption = selectedBrand,
                        onOptionSelected = { brand ->
                            selectedBrand = brand
                            selectedModel = null  // Reset model selection when brand changes
                        },
                        modifier = Modifier.fillMaxWidth(0.5f) // Set dropdown width to half of the screen
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Dropdown for car model selection
                    if (selectedBrand != null) {
                        DropdownMenu(
                            label = "Wybierz model",
                            options = models,
                            selectedOption = selectedModel,
                            onOptionSelected = { model -> selectedModel = model },
                            modifier = Modifier.fillMaxWidth(0.5f) // Set dropdown width to half of the screen
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Display selected car details as Card only when model is selected
                    selectedCar = cars.find { it.producer == selectedBrand && it.model == selectedModel }
                    if (selectedCar != null) {
                        CarDetailsCard(
                            car = selectedCar!!,
                            modifier = Modifier.fillMaxWidth(0.5f)
                        ) // Set Card width to half of the screen

                        Spacer(modifier = Modifier.height(16.dp))

                        // "Wypożycz Samochód" button
                        Button(
                            onClick = {
                                isValuationDialogShown = true
                            },
                            modifier = Modifier.fillMaxWidth(.25f) // Set button width to half of the screen
                        ) {
                            Text("Wyceń wypożyczenie")
                        }
                    } else {
                        Text("Brak wyników", style = MaterialTheme.typography.body1)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if(valuationResult != null) {
                        OfferView(
                            valuationResult!!,
                            onTimerEnd = { viewModel.resetValuationResult() },
                            onRent = { isCarRented = it },
                            onRentClick = {
                                if(viewModel.user != null) {
                                    viewModel.user?.let { user ->
                                        viewModel.requestRent(
                                            car = selectedCar!!,
                                            user = user,
                                            onRent = { isCarRented = it },
                                            startDate = "2024-12-10",
                                            endDate = "2024-12-12"
                                        )
                                    }
                                }
                                else {
                                    isLoginDialogShown = true
                                }
                            },
                        )
                    }

                    if (isCarRented) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(innerPadding),
                            contentAlignment = Alignment.Center // Centers the content within the Box
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(0.5f) // 50% of the screen width
                                    .padding(16.dp),
                                elevation = 8.dp
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Samochód wynajęty!",
                                        style = MaterialTheme.typography.h6,
                                        color = MaterialTheme.colors.primary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Dziękujemy za wynajęcie ${selectedCar!!.producer} ${selectedCar!!.model}.",
                                        style = MaterialTheme.typography.body1,
                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = {
                                            // Handle additional actions, e.g., navigate to home or details
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Powrót do Strony Głównej")
                                    }
                                }
                            }
                        }
                    }

                    // Footer at the bottom
                    Footer()
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
fun Footer() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Divider(thickness = 1.dp, color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "© 2024 Wypożyczalnia Samochodów by Developers in Crime",
            fontSize = 12.sp,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = "All rights reserved.",
            fontSize = 12.sp,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun CarDetailsCard(car: Car, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Marka: ${car.producer}", style = MaterialTheme.typography.h6)
            Text("Model: ${car.model}", style = MaterialTheme.typography.body1)
        }
    }
}

@Composable
fun DropdownMenu(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier) {
        OutlinedTextField(
            value = selectedOption ?: "",
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(), // Set the width of the dropdown to half of the screen
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.5f) // Set width of dropdown menu to half of the screen
        ) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    onOptionSelected(option)
                    expanded = false
                }) {
                    Text(option)
                }
            }
        }
    }
}
