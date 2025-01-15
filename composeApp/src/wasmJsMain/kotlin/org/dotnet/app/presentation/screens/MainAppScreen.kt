package org.dotnet.app.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dotnetwebapp.composeapp.generated.resources.*
import dotnetwebapp.composeapp.generated.resources.Res
import kotlinx.browser.window
import org.dotnet.app.presentation.viewModels.CarRentalAppViewModel
import org.dotnet.app.utils.AppState
import org.jetbrains.compose.resources.*

@Composable
fun MainAppScreen(viewModel: CarRentalAppViewModel) {
    val uiState by viewModel.uiState.collectAsState()

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
                        onClick = { viewModel.sendTokenToBackend() },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        val carIcon = Res.drawable.directions_car
                        Icon(
                            painter = painterResource(carIcon),
                            contentDescription = "Auto"
                        )
                    }

                    IconButton(
                        onClick = {
                            uiState.user?.id?.let { viewModel.getUserDetails(it) }
                            viewModel.changeAppState(AppState.User)
                                  },
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
                AppState.Rental -> {
                    /* NOTHING FOR NOW */
                }
                AppState.CarDetails -> {
                    CarDetailsScreen(viewModel) { viewModel.changeAppState(AppState.Default) }
                }
            }
        }
    )

    // Login dialog window
    if(uiState.isLoginDialogShown) {
        LoginScreen(viewModel)
    }
}