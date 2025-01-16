package org.dotnet.app.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dotnetwebapp.composeapp.generated.resources.Res
import dotnetwebapp.composeapp.generated.resources.arrow_back
import org.dotnet.app.domain.rentals.Rental
import org.dotnet.app.domain.rentals.ReturnRequest
import org.dotnet.app.presentation.viewModels.CarRentalAppViewModel
import org.jetbrains.compose.resources.painterResource
import org.dotnet.app.presentation.components.RentalCard
import org.dotnet.app.presentation.components.UserProfileSection

@Composable
fun UserScreen(
    viewModel: CarRentalAppViewModel,
    onClose: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user

    var selectedRental by remember { mutableStateOf<Rental?>(null) }

    var showEndedRentals by remember { mutableStateOf(false) }

    if (user == null) {
        Text("User data not available.")
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil użytkownika") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(!uiState.isUserLoading) {
                UserProfileSection(user = user, onUpdateUser = { updatedUser -> viewModel.updateUser(updatedUser) }, uiState = uiState)
            }
            else {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.getRentedCars() },
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primaryVariant),
                modifier = Modifier
                    .padding(bottom = 8.dp)
            ) {
                Text("Odśwież swoje wypozyczenia")
            }
            if (uiState.myRentals.isNotEmpty()) {
                // Define the order of statuses
                val statusOrder = listOf("planned", "inProgress", "pendingReturn", "ended")

                // Sort rentals based on the defined order of statuses
                val sortedRentals = uiState.myRentals.sortedBy { rental ->
                    statusOrder.indexOf(rental.status)
                }

                // Filter rentals into active and ended
                val activeRentals = sortedRentals.filter { it.status != "ended" }
                val endedRentals = sortedRentals.filter { it.status == "ended" }

                // Display active rentals
                activeRentals.forEach { rental ->
                    RentalCard(rental, onClick = {
                        if (rental.status in listOf("planned", "pendingReturn", "inProgress")) {
                            selectedRental = rental
                            // Trigger return screen logic
                            viewModel.toggleShowReturnScreen(true)
                        }
                    })
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Button to toggle ended rentals visibility
                Button(
                    onClick = { showEndedRentals = !showEndedRentals },
                    modifier = Modifier.fillMaxWidth(.5f).padding(16.dp)
                ) {
                    Text(if (showEndedRentals) "Hide Rental History" else "Show Rental History")
                }

                // Display ended rentals if toggled
                if (showEndedRentals) {
                    endedRentals.forEach { rental ->
                        RentalCard(rental, onClick = {
                            // Additional action for ended rentals, if needed
                        })
                    }
                }
            }
            else {
                Text(
                    text = "No rentals available",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.body1
                )
            }

            if(/*showReturnScreen.value*/ uiState.showReturnScreen ) {
                if(selectedRental != null) {
                    val returnRequest = ReturnRequest(
                        UserId = uiState.user!!.id!!.toString(),
                        RentalId = selectedRental!!.id.toString(),
                        RentalName = selectedRental!!.car.rentalService
                    )
                    ReturnCarDialog(onReturnClick = { viewModel.returnCar(returnRequest) }, onDismiss = { viewModel.toggleShowReturnScreen(false) })
                }
                else {
                    Text("Nie wybrano auta do zwrotu")
                }
            }

            if(/*showReturnScreen.value*/ uiState.showReturnRequestDialog ) {
                if(selectedRental != null) {
                    ReturnRequestedDialog(
                        onReturnClick = { viewModel.toggleShowReturnRequestedScreen(false) },
                        onDismiss = { viewModel.toggleShowReturnRequestedScreen(false) }
                    )
                }
                else {
                    Text("Nie wybrano auta do zwrotu")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ReturnCarDialog(onReturnClick: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally, // Center contents horizontally
                verticalArrangement = Arrangement.Center // Center contents vertically
            ) {
                Text(
                    text = "Czy na pewno chcesz zgłosić zwrot auta do wypożyczalni?",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(bottom = 16.dp) // Add spacing below the text
                )
                Button(
                    onClick = onReturnClick,
                    modifier = Modifier.align(Alignment.CenterHorizontally) // Center the button
                ) {
                    Text("Zgłoś zwrot auta do wypożyczalni")
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        buttons = {}
    )
}

@Composable
fun ReturnRequestedDialog(onReturnClick: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally, // Center contents horizontally
                verticalArrangement = Arrangement.Center // Center contents vertically
            ) {
                Text(
                    text = "Zwrot auta przyjęty do wypożyczalni!",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(bottom = 16.dp) // Add spacing below the text
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        buttons = {}
    )
}
