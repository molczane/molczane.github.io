package org.dotnet.app.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.dotnet.app.viewModel.CarRentalAppViewModel
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, viewModel: CarRentalAppViewModel) {
    var isLoading by remember { mutableStateOf(false) }
    var loginResult by remember { mutableStateOf<String?>(null) }

    // Create a stable reference to the callback handler
    val handleCallback = remember {
        { code: String ->
            viewModel.sendAuthCodeToBackend(code)
        }
    }

    // Create a disposable effect that triggers once when coming back from Google
    DisposableEffect(Unit) {
        val currentUrl = window.location.href
        if (currentUrl.contains("code=")) {
            isLoading = true
            val code = window.location.search
                .substringAfter("code=")
                .substringBefore("&")

            // Launch in a coroutine to handle the auth code
            MainScope().launch {
                try {
                    viewModel.sendAuthCodeToBackend(code)
                    // Clean up URL parameters
                    window.history.replaceState(null, "", window.location.pathname)
                    onLoginSuccess()
                } finally {
                    isLoading = false
                }
            }
        }

        // Cleanup function
        onDispose { }
    }

    // Handle initial load and subsequent navigation
    LaunchedEffect(Unit) {
        val searchParams = window.location.search
        if (searchParams.contains("code=")) {
            isLoading = true
            try {
                val authorizationCode = searchParams
                    .substringAfter("code=")
                    .substringBefore("&")
                handleCallback(authorizationCode)

                // Clean up URL after handling the code
                window.history.replaceState(null, "", window.location.pathname)

                onLoginSuccess()
            } catch (e: Exception) {
                loginResult = "Login failed: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

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
                redirectUri = "https://molczane.github.io/"
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

//@Composable
//fun LoginScreen(onLoginSuccess: () -> Unit, viewModel: CarRentalAppViewModel) {
//    var isLoading by remember { mutableStateOf(false) }
//    var loginResult by remember { mutableStateOf<String?>(null) }
//    val coroutineScope = rememberCoroutineScope()
//    var isCallbackHandled by remember { mutableStateOf(false) } // To prevent repeated calls
//
//    // Google OAuth configuration
//    val googleClientId = "248107412465-i64fdf66a6f4nrj7232ghdmvbsg91pp3.apps.googleusercontent.com"
//    val redirectUri = "https://molczane.github.io/"
//    //val redirectUri = "http://localhost:8080"
//
//    Column(
//        modifier = Modifier
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(text = "Login", style = MaterialTheme.typography.h4)
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Google Sign-In Button
//        Button(
//            onClick = {
//                initiateGoogleSignIn(googleClientId, redirectUri)
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            if (isLoading) {
//                CircularProgressIndicator(modifier = Modifier.size(24.dp))
//            } else {
//                Text("Sign in with Google")
//            }
//        }
//
//        // Optional: Existing username/password login
//        Spacer(modifier = Modifier.height(16.dp))
//        Text("Or continue with:", style = MaterialTheme.typography.subtitle1)
//
//        Spacer(modifier = Modifier.height(16.dp))
//        Button(
//            onClick = {
//                // Toggle to show traditional login fields
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Use Username/Password")
//        }
//
//        loginResult?.let {
//            Text(it, style = MaterialTheme.typography.body1, color = MaterialTheme.colors.primary)
//        }
//    }
//
//    // Handle OAuth callback
//    if(window.location.search.contains("code=")) {
//        coroutineScope.launch {
//            handleOAuthCallback(viewModel, onLoginSuccess)
//        }
//    }
//
//    // Handle OAuth callback
//    LaunchedEffect(window.location.search) {
//        val searchParams = window.location.search
//        if (!isCallbackHandled && searchParams.contains("code=")) {
//            isCallbackHandled = true // Mark callback as handled to prevent re-execution
//            coroutineScope.launch {
//                handleOAuthCallback(viewModel, onLoginSuccess)
//            }
//        }
//    }
////    LaunchedEffect(window.location.search) {
////        println("Handling OAuth callback")
////        handleOAuthCallback(viewModel, onLoginSuccess)
////    }
//}
//
//// JavaScript interop function to initiate Google Sign-In
//private fun initiateGoogleSignIn(clientId: String, redirectUri: String) {
//    val scope = "openid%20email%20profile"
//    val googleOAuthUrl = "https://accounts.google.com/o/oauth2/v2/auth" +
//            "?client_id=$clientId" +
//            "&redirect_uri=$redirectUri" +
//            "&response_type=code" +
//            "&scope=$scope"
//
//    window.location.href = googleOAuthUrl
//}
//
//external fun decodeURIComponent(encodedURI: String): String
//
//// Handle OAuth callback and token exchange
//private fun handleOAuthCallback(
//    viewModel: CarRentalAppViewModel,
//    onLoginSuccess: () -> Unit
//) {
//    val urlParams = window.location.search
//    if (urlParams.contains("code=")) {
//        val authorizationCode = urlParams.substringAfter("code=").substringBefore("&")
//        println(authorizationCode)
//        // Exchange authorization code for access token
//        // This would typically be done server-side for security
//
//        viewModel.sendAuthCodeToBackend(authorizationCode)
//
////        viewModel.exchangeGoogleAuthCode(
////            authorizationCode,
////            onSuccess = {
////                onLoginSuccess()
////            },
////            onError = { errorMessage ->
////                // Handle login error
////            }
////        )
//    }
//}