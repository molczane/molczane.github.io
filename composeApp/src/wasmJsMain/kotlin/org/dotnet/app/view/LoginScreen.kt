package org.dotnet.app.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.dotnet.app.viewModel.CarRentalAppViewModel
import kotlinx.browser.window
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, viewModel: CarRentalAppViewModel) {
    var isLoading by remember { mutableStateOf(false) }
    var loginResult by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Google OAuth configuration
    val googleClientId = "248107412465-i64fdf66a6f4nrj7232ghdmvbsg91pp3.apps.googleusercontent.com"
    val redirectUri = "https://molczane.github.io/"
    //val redirectUri = "http://localhost:8080"

    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login", style = MaterialTheme.typography.h4)

        Spacer(modifier = Modifier.height(16.dp))

        // Google Sign-In Button
        Button(
            onClick = {
                initiateGoogleSignIn(googleClientId, redirectUri)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Sign in with Google")
            }
        }

        // Optional: Existing username/password login
        Spacer(modifier = Modifier.height(16.dp))
        Text("Or continue with:", style = MaterialTheme.typography.subtitle1)

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                // Toggle to show traditional login fields
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Use Username/Password")
        }

        loginResult?.let {
            Text(it, style = MaterialTheme.typography.body1, color = MaterialTheme.colors.primary)
        }
    }

    // Handle OAuth callback
    LaunchedEffect(Unit) {
        handleOAuthCallback(viewModel, onLoginSuccess)
    }
}

// JavaScript interop function to initiate Google Sign-In
private fun initiateGoogleSignIn(clientId: String, redirectUri: String) {
    val scope = "openid%20email%20profile"
    val googleOAuthUrl = "https://accounts.google.com/o/oauth2/v2/auth" +
            "?client_id=$clientId" +
            "&redirect_uri=$redirectUri" +
            "&response_type=code" +
            "&scope=$scope"

    window.location.href = googleOAuthUrl
}

// Handle OAuth callback and token exchange
private fun handleOAuthCallback(
    viewModel: CarRentalAppViewModel,
    onLoginSuccess: () -> Unit
) {
    val urlParams = window.location.search
    if (urlParams.contains("code=")) {
        val authorizationCode = urlParams.substringAfter("code=").substringBefore("&")
        println(authorizationCode)
        // Exchange authorization code for access token
        // This would typically be done server-side for security
        viewModel.exchangeGoogleAuthCode(
            authorizationCode,
            onSuccess = {
                onLoginSuccess()
            },
            onError = { errorMessage ->
                // Handle login error
            }
        )
    }
}