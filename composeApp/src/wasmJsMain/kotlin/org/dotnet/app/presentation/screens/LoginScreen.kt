package org.dotnet.app.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dotnetwebapp.composeapp.generated.resources.Res
import dotnetwebapp.composeapp.generated.resources.google_icon
import org.dotnet.app.presentation.viewModels.CarRentalAppViewModel
import org.jetbrains.compose.resources.painterResource

@Composable
fun LoginScreen(viewModel: CarRentalAppViewModel) {
    // UI State
    val uiState by viewModel.uiState.collectAsState()

    AlertDialog(
        onDismissRequest = { viewModel.toggleLoginDialog(false) },
        title = { /* NO TITLE HERE */ },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth() // Ensure the Column fills the screen
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center, // Center elements vertically
                horizontalAlignment = Alignment.CenterHorizontally // Center elements horizontally
            ) {
                Text(text = "Login", style = MaterialTheme.typography.h4)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.initiateGoogleSignIn()
                    },
                    modifier = Modifier.fillMaxWidth(.5f),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colors.onPrimary
                        )
                    } else {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp), // Add padding for better alignment
                            verticalAlignment = Alignment.CenterVertically, // Align icon and text vertically
                            horizontalArrangement = Arrangement.Center
                        ) {
                            val googleIcon = Res.drawable.google_icon
                            Icon(
                                painter = painterResource(googleIcon),
                                contentDescription = "Google Icon",
                                tint = Color.Unspecified, // Use the native color of the icon
                                modifier = Modifier
                                    .size(40.dp) // Adjust size to match the text
                                    .padding(end = 8.dp) // Add space between icon and text
                            )
                            Text(
                                text = "Sign in with Google",
                                style = MaterialTheme.typography.button // Use a button-appropriate text style
                            )
                        }
                    }
                }

                if (uiState.loginResult != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.loginResult!!,
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { viewModel.toggleLoginDialog(false) }) {
                Text("Close")
            }
        }
    )
}