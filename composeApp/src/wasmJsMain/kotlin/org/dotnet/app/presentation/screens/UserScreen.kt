package org.dotnet.app.presentation.screens

import androidx.compose.foundation.clickable
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
import org.dotnet.app.domain.User
import org.dotnet.app.presentation.viewModels.CarRentalAppViewModel
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
                title = { Text("User Profile") },
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
            ProfileSection(user = user, onUpdateUser = { updatedUser -> viewModel.updateUser(updatedUser) })

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Rental History",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // RentalHistorySection(viewModel)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Current Rental",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // CurrentRentalSection(viewModel)
        }
    }
}

@Composable
fun ProfileSection(user: User, onUpdateUser: (User) -> Unit) {
    var firstname by remember { mutableStateOf(user.firstname ?: "") }
    var lastname by remember { mutableStateOf(user.lastname ?: "") }
    var email by remember { mutableStateOf(user.email ?: "") }
    var birthday by remember { mutableStateOf(user.birthday ?: "") }
    var driverLicenseReceiveDate by remember { mutableStateOf(user.driverLicenseReceiveDate ?: "") }

    Column(
        modifier = Modifier.fillMaxWidth(.5f)
    ) {
        TextField(
            value = firstname,
            onValueChange = { firstname = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = lastname,
            onValueChange = { lastname = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

//        // Birthday DatePicker
//        DatePickerField(
//            label = "Birthday",
//            selectedDate = birthday,
//            onDateSelected = { birthday = it }
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        // Driver License Receive DatePicker
//        DatePickerField(
//            label = "Driver License Receive Date",
//            selectedDate = driverLicenseReceiveDate,
//            onDateSelected = { driverLicenseReceiveDate = it }
//        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = birthday,
            onValueChange = { birthday = it },
            label = { Text("Birthday") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = driverLicenseReceiveDate,
            onValueChange = { driverLicenseReceiveDate = it },
            label = { Text("Driver License Receive Date") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val updatedUser = user.copy(
                    firstname = firstname,
                    lastname = lastname,
                    email = email,
                    birthday = birthday,
                    driverLicenseReceiveDate = driverLicenseReceiveDate
                )
                onUpdateUser(updatedUser)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Changes")
        }
    }
}

//@Composable
//fun DatePickerField(
//    label: String,
//    selectedDate: String,
//    onDateSelected: (String) -> Unit
//) {
//    var isDatePickerVisible by remember { mutableStateOf(false) }
//
//    Column {
//        TextField(
//            value = selectedDate,
//            onValueChange = {},
//            label = { Text(label) },
//            modifier = Modifier
//                .fillMaxWidth()
//                .clickable { isDatePickerVisible = true },
//            readOnly = true
//        )
//
//        if (isDatePickerVisible) {
//            DatePickerDialog(
//                onDismissRequest = { isDatePickerVisible = false },
//                onDateSelected = { date ->
//                    onDateSelected(date)
//                    isDatePickerVisible = false
//                }
//            )
//        }
//    }
//}
//
//@Composable
//fun DatePickerDialog(
//    onDismissRequest: () -> Unit,
//    onDateSelected: (String) -> Unit
//) {
//    var tempDate by remember { mutableStateOf("") }
//
//    Div(
//        attrs = {
//            addClass("date-picker-overlay")
//        }
//    ) {
//        Input(
//            type = InputType.date,
//            attrs = {
//                addClass("date-picker-input")
//                onInputFunction = {
//                    val target = it.target as? org.w3c.dom.HTMLInputElement
//                    tempDate = target?.value ?: ""
//                }
//            }
//        )
//        Button(
//            attrs = {
//                onClick { onDateSelected(tempDate) }
//            }
//        ) {
//            Text("Confirm")
//        }
//        Button(
//            attrs = {
//                onClick { onDismissRequest() }
//            }
//        ) {
//            Text("Cancel")
//        }
//    }
//}

