package org.dotnet.app.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.viewModelFactory
import dotnetwebapp.composeapp.generated.resources.*
import dotnetwebapp.composeapp.generated.resources.Res
import kotlinx.browser.window
import org.dotnet.app.presentation.viewModels.CarRentalAppViewModel
import org.dotnet.app.domain.Car
import org.dotnet.app.utils.AppState
import org.jetbrains.compose.resources.*

@Composable
fun MainAppScreen(viewModel: CarRentalAppViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    var isValuationDialogShown by remember { mutableStateOf(false) }

    val selectedCar : Car? by remember { mutableStateOf(null) }

    val valuationResult by viewModel.valuationResult.collectAsState()

    var isCarRented by remember { mutableStateOf(false) }

    /* ============== THIS NEEDS TO BE REFACTORED AND MOVED OUT OF HERE =============== */
    val currentUrl by remember { mutableStateOf(window.location.href) }

    val isConfigLoaded by viewModel.isConfigLoaded.collectAsState()

    // observe changes in user login status - TODO() - I think this should be moved out of this composable
    LaunchedEffect(isConfigLoaded, currentUrl) {
        if (isConfigLoaded && currentUrl.contains("code=")) {
            println("Config loaded, sending authentication code to backend.")

            val code = window.location.search
                .substringAfter("code=")
                .substringBefore("&")
            println("code: ${code}")
            viewModel.sendAuthCodeToBackend(code)
            window.history.replaceState(null, "", window.location.pathname)
        }
    }
    /* ================================================================================ */

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text = "Wypożyczalnia Samochodów",
                    modifier = Modifier.clickable {
                        viewModel.changeAppState(AppState.Default)
                    }) },
                backgroundColor = MaterialTheme.colors.primaryVariant,
                actions = {
                    IconButton(
                        onClick = { /* DO NOTHING */ },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        val carIcon = Res.drawable.directions_car
                        Icon(
                            painter = painterResource(carIcon),
                            contentDescription = "Auto"
                        )
                    }

                    IconButton(
                        onClick = {  viewModel.changeAppState(AppState.User) },
                        modifier = Modifier.padding(8.dp),
                        enabled = uiState.isUserLoggedIn
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
                    if(uiState.isUserLoggedIn) {
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
            when(uiState.appState) {
                AppState.Default -> {
                    DefaultScreen(uiState, viewModel, innerPadding)
                }
                AppState.User -> {
                    UserScreen(viewModel) { viewModel.changeAppState(AppState.Default) }
                }
                AppState.Rental -> {}
            }
        }
    )

    // Login dialog window
    if(uiState.isLoginDialogShown) {
        LoginScreen(viewModel)
    }

    // Valuation Dialog Window TODO(move to different composable)
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
                // Optional: Add a custom confirmation button if needed
            },
            dismissButton = {
                Button(onClick = { isValuationDialogShown = false }) {
                    Text("Anuluj")
                }
            }
        )
    }
}

/* ============================ CODE FOR VALUATION AND REQUESTING VALUATION ================================= */

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