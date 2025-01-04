package org.dotnet.app.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.dotnet.app.viewModel.CarRentalAppViewModel
import kotlinx.browser.window

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, viewModel: CarRentalAppViewModel) {
    val isLoading by remember { mutableStateOf(false) }
    val loginResult by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login", style = MaterialTheme.typography.h4)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { initiateGoogleSignIn(
                clientId = "248107412465-i64fdf66a6f4nrj7232ghdmvbsg91pp3.apps.googleusercontent.com",
                //redirectUri = "https://molczane.github.io/"
                redirectUri = "http://localhost:8081/"
            )},
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colors.onPrimary
                )
            } else {
                Text("Sign in with Google")
            }
        }

        if (loginResult != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = loginResult!!,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption
            )
        }
    }
}

private fun initiateGoogleSignIn(clientId: String, redirectUri: String) {
    val scope = "openid%20email%20profile"
    val googleOAuthUrl = buildString {
        append("https://accounts.google.com/o/oauth2/v2/auth")
        append("?client_id=$clientId")
        append("&redirect_uri=$redirectUri")
        append("&response_type=code")
        append("&scope=$scope")
        // Add state parameter for security
        append("&state=${generateRandomState()}")
    }
    window.location.href = googleOAuthUrl
}

private fun generateRandomState(): String {
    return (1..32)
        .map { ('a'..'z') + ('A'..'Z') + ('0'..'9') }
        .map { it.random() }
        .joinToString("")
}