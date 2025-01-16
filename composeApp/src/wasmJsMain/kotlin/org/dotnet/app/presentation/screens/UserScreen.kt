package org.dotnet.app.presentation.screens

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

    val showReturnScreen = remember { mutableStateOf(false) }

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
            if(!uiState.isLoading) {
                UserProfileSection(user = user, onUpdateUser = { updatedUser -> viewModel.updateUser(updatedUser) }, uiState = uiState)
            }
            else {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colors.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.getRentedCars() },
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primaryVariant),
                modifier = Modifier
                    .padding(bottom = 8.dp)
            ) {
                Text("Pobierz swoje wypozyczenia")
            }

            if(uiState.myRentals.isNotEmpty()) {
                uiState.myRentals.forEach { myRental ->
                    RentalCard(myRental, onClick = {
                        if(myRental.status == "planned" || myRental.status == "pendingReturn" || myRental.status == "inProgress") {
                            showReturnScreen.value = true
                        }
                    })
                }
            }
            else {
                Text(
                    text = "No rentals available",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.body1
                )
            }

            if(showReturnScreen.value) {
                   ReturnCarDialog(onReturnClick = { }, onDismiss = { showReturnScreen.value = false })
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
