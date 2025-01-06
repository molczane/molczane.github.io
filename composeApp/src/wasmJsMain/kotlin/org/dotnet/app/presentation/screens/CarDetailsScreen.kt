package org.dotnet.app.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dotnetwebapp.composeapp.generated.resources.Res
import dotnetwebapp.composeapp.generated.resources.arrow_back
import org.dotnet.app.domain.cars.Car
import org.dotnet.app.presentation.viewModels.CarRentalAppViewModel
import org.jetbrains.compose.resources.painterResource

@Composable
fun CarDetailsScreen(
    viewModel: CarRentalAppViewModel,
    onClose: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val car = uiState.selectedCar

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
                modifier = Modifier.fillMaxWidth(0.5f), // Ensure 50% width
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CarDetailsContent(car = car)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /* DO NOTHING FOR NOW */ },
                    modifier = Modifier.fillMaxWidth(.5f),
                ) {
                    Text("Proceed to valuation")
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