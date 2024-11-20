package org.dotnet.app.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.browser.window

@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Please log in to access the Rent a Car service.")
        Spacer(modifier = Modifier.height(16.dp))

        // Google Login Button
        Button(
            onClick = {
                // Simulate a successful login by generating a token
                val token = "simulated_google_access_token"
                onLoginSuccess(token)
            }
        ) {
            Text("Log in with Google")
        }

        // Alternatively, handle token extraction after a real OAuth flow
        LaunchedEffect(Unit) {
            val token = getAccessTokenFromURL()
            if (token != null) {
                onLoginSuccess(token)
            }
        }
    }
}

// Dummy function to simulate opening Google login (should be replaced with actual OAuth flow)
fun openGoogleLogin() {
    val googleOAuthUrl = "https://accounts.google.com/o/oauth2/v2/auth?" +
            "client_id=YOUR_GOOGLE_CLIENT_ID&" +
            "redirect_uri=${window.location.origin}/callback&" +
            "response_type=token&scope=email"
    window.open(googleOAuthUrl, "_self")
}

// Simulated function to extract access token from URL fragment after redirect
fun getAccessTokenFromURL(): String? {
    val hash = window.location.hash
    if (hash.contains("access_token")) {
        return hash.substringAfter("access_token=").substringBefore("&")
    }
    return null
}