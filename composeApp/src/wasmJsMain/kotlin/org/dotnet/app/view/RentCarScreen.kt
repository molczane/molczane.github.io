package org.dotnet.app.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.dotnet.app.CarRentalAppViewModel
//import org.dotnet.app.dataSource.cars
import org.dotnet.app.model.Car
import org.dotnet.app.model.Offer

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

    LaunchedEffect(Unit) {
        viewModel.updateCars()
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
                    Button(
                        onClick = { isLoginDialogShown = true },
                        elevation = ButtonDefaults.elevation(defaultElevation = 15.dp),
                        modifier = Modifier.padding(12.dp)
                    ) {
                        if(!isSignedIn.value) {
                            Text("Zaloguj się")
                        }
                        else {
                            Text("Wyloguj się")
                        }
                    }

                    Button(
                        onClick = { isValuationDialogShown = true },
                        elevation = ButtonDefaults.elevation(defaultElevation = 15.dp),
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text("Show Valuation Dialog")
                    }
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
                            {}
                        )
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
