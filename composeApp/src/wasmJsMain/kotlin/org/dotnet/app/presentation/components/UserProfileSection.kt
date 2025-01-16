package org.dotnet.app.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.dotnet.app.domain.user.User
import org.dotnet.app.presentation.viewModels.CarRentalUiState
import org.dotnet.app.utils.ValidatedTextFieldItem
import org.dotnet.app.utils.validateDate

@Composable
fun UserProfileSection(user: User, onUpdateUser: (User) -> Unit, uiState: CarRentalUiState) {
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
            } else {
                Text("Save Changes")
            }
        }
    }
}