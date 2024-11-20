package org.dotnet.app

import androidx.compose.runtime.*
import kotlinx.browser.window
import org.dotnet.app.view.LoginScreen
import org.dotnet.app.view.RentCarScreen

@Composable
fun CarRentalApp() {
    var isLoggedIn by remember { mutableStateOf(false) }

    // Check if the user has an access token in localStorage (for example, Google OAuth token)
    LaunchedEffect(Unit) {
        val token = window.localStorage.getItem("access_token")
        if (token != null) {
            isLoggedIn = true
        }
    }

    if (isLoggedIn) {
        RentCarScreen()
    } else {
        LoginScreen(onLoginSuccess = { token ->
            // Store token and update login state
            window.localStorage.setItem("access_token", token)
            isLoggedIn = true
        })
    }
}