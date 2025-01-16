package org.dotnet.app.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dotnetwebapp.composeapp.generated.resources.Res
import dotnetwebapp.composeapp.generated.resources.arrow_back
import org.dotnet.app.domain.cars.Car
import org.dotnet.app.domain.offer.OfferRequest
import org.dotnet.app.presentation.components.NotificationSnackbar
import org.dotnet.app.presentation.viewModels.CarRentalAppViewModel
import org.dotnet.app.presentation.viewModels.CarRentalUiState
import org.dotnet.app.utils.ValidatedTextFieldItem
import org.dotnet.app.utils.validateDate
import org.jetbrains.compose.resources.painterResource

@Composable
fun CarDetailsScreen(
    viewModel: CarRentalAppViewModel,
    onClose: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val car = uiState.selectedCar

    var showValuationDialog by remember { mutableStateOf(false) }

    if (car == null) {
        Text("No car selected")
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Szczegóły Auta") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            painter = painterResource(Res.drawable.arrow_back),
                            contentDescription = "Back"
                        )
                    }
                },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter // Centers content vertically and horizontally,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .verticalScroll(rememberScrollState()), // Ensure 50% width
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CarDetailsContent(car = car)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (uiState.isUserLoggedIn) {
                            showValuationDialog = !showValuationDialog
                        }
                        else {
                            viewModel.toggleYouHaveToBeLoggedIn(true)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(.5f),
                ) {
                    Text("Proceed to valuation")
                }
                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(
                    visible = uiState.showYouHaveToBeLoggedIn
                ) {
                    NotificationSnackbar("Aby poprosić o wycenę musisz być zalogowany!", { viewModel.toggleYouHaveToBeLoggedIn(false) })
                }


                AnimatedVisibility(
                    visible = uiState.showRentalNotification
                ) {
                    NotificationSnackbar("Wysłano link z ofertą na maila!", { viewModel.toggleRentalNotification(false) })
                }

                // Animated Valuation Dialog
                AnimatedVisibility(
                    visible = showValuationDialog
                ) {
                    ValuationDialog(
                        car = car,
                        onClose = { showValuationDialog = false },
                        uiState = uiState,
                        onRequestValuation = { viewModel.getOffer(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun CarDetailsContent(car: Car, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        elevation = 8.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${car.producer} ${car.model}",
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.primary
            )
            Text(
                text = "Year of Production: ${car.yearOfProduction}",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(vertical = 2.dp)
            )
            Text(
                text = "Number of Seats: ${car.numberOfSeats}",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(vertical = 2.dp)
            )
            Text(
                text = "Availability: ${if (car.isAvailable == 1) "Available" else "Not Available"}",
                style = MaterialTheme.typography.body1,
                color = if (car.isAvailable == 1) MaterialTheme.colors.primary else MaterialTheme.colors.error,
                modifier = Modifier.padding(vertical = 2.dp)
            )
            Text(
                text = "Location: ${car.location}",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
    }
}

@Composable
fun ValuationDialog(car: Car, onClose: () -> Unit, uiState: CarRentalUiState, onRequestValuation: ( OfferRequest ) -> Unit) {
    var plannedStartDate by remember { mutableStateOf("") }
    var plannedStartDateError by remember { mutableStateOf<String?>(null) }
    var plannedEndDate by remember { mutableStateOf("") }
    var plannedEndDateError by remember { mutableStateOf<String?>(null) }

    val datePattern = Regex("\\d{4}-\\d{2}-\\d{2}") // Format: YYYY-MM-DD

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = 8.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ValidatedTextFieldItem(
                value = plannedStartDate,
                onValueChange = { newValue ->
                    plannedStartDate = newValue
                    plannedStartDateError = validateDate(newValue, datePattern)
                },
                label = "Planned start date (YYYY-MM-DD)",
                error = plannedStartDateError
            )

            Spacer(modifier = Modifier.height(16.dp))

            ValidatedTextFieldItem(
                value = plannedEndDate,
                onValueChange = { newValue ->
                    plannedEndDate = newValue
                    plannedEndDateError = validateDate(newValue, datePattern)
                },
                label = "Planned end date (YYYY-MM-DD)",
                error = plannedEndDateError
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    println("Parsing offer request")
                    println("User: ${uiState.user}")
                    val offerRequest = uiState.user?.id?.let {
                        OfferRequest(
                            CarId = car.id,
                            CustomerId = uiState.user.id,
                            PlannedStartDate = plannedStartDate,
                            PlannedEndDate = plannedEndDate
                        )
                    }
                    println("Offer request: $offerRequest")
                    println("Sending offer request")
                    if (offerRequest != null) {
                        onRequestValuation(offerRequest)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {

                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colors.onPrimary
                    )
                }
                else {
                    Text("Zapytaj o ofertę")
                }
            }
        }
    }
}