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
import org.dotnet.app.domain.user.User
import org.dotnet.app.presentation.viewModels.CarRentalAppViewModel
import org.dotnet.app.presentation.viewModels.CarRentalUiState
import org.dotnet.app.utils.ValidatedTextFieldItem
import org.dotnet.app.utils.validateDate
import org.jetbrains.compose.resources.painterResource

@Composable
fun UserScreen(
    viewModel: CarRentalAppViewModel,
    onClose: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user

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
                ProfileSection(user = user, onUpdateUser = { updatedUser -> viewModel.updateUser(updatedUser) }, uiState = uiState)
            }
            else {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colors.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Historia wypożyczeń",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // RentalHistorySection(viewModel)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Aktywne wypożyczenia",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // CurrentRentalSection(viewModel)
        }
    }
}

@Composable
fun ProfileSection(user: User, onUpdateUser: (User) -> Unit, uiState: CarRentalUiState) {
    var login by remember { mutableStateOf(user.login ?: "") }
    var firstname by remember { mutableStateOf(user.firstname ?: "") }
    var lastname by remember { mutableStateOf(user.lastname ?: "") }
    var email by remember { mutableStateOf(user.email ?: "") }
    var birthday by remember { mutableStateOf(user.birthday ?: "") }
    var birthdayError by remember { mutableStateOf<String?>(null) }
    var driverLicenseReceiveDate by remember { mutableStateOf(user.driverLicenseReceiveDate ?: "") }
    var driverLicenseError by remember { mutableStateOf<String?>(null) }

    val datePattern = Regex("\\d{4}-\\d{2}-\\d{2}") // Format: YYYY-MM-DD

    Column(
        modifier = Modifier.fillMaxWidth(.5f)
    ) {
        TextField(
            value = login,
            onValueChange = { login = it },
            label = { Text("Login") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = firstname,
            onValueChange = { firstname = it },
            label = { Text("Imię") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = lastname,
            onValueChange = { lastname = it },
            label = { Text("Nazwisko") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Mail") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        ValidatedTextFieldItem(
            value = birthday,
            onValueChange = { newValue ->
                birthday = newValue
                birthdayError = validateDate(newValue, datePattern)
            },
            label = "Birthday (YYYY-MM-DD)",
            error = birthdayError
        )

        Spacer(modifier = Modifier.height(16.dp))

        ValidatedTextFieldItem(
            value = driverLicenseReceiveDate,
            onValueChange = { newValue ->
                driverLicenseReceiveDate = newValue
                driverLicenseError = validateDate(newValue, datePattern)
            },
            label = "Driver License Receive Date (YYYY-MM-DD)",
            error = driverLicenseError
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val updatedUser = user.copy(
                    login = login,
                    firstname = firstname,
                    lastname = lastname,
                    email = email,
                    birthday = birthday,
                    driverLicenseReceiveDate = driverLicenseReceiveDate
                )
                onUpdateUser(updatedUser)
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
                Text("Save Changes")
            }
        }
    }
}