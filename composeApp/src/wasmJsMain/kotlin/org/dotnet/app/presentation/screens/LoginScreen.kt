package org.dotnet.app.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.dotnet.app.presentation.viewModels.CarRentalAppViewModel

@Composable
fun LoginScreen(viewModel: CarRentalAppViewModel) {
    // UI State
    val uiState by viewModel.uiState.collectAsState()

    AlertDialog(
        onDismissRequest = { viewModel.toggleLoginDialog(false) },
        title = { /* NO TITLE HERE */ },
        text = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Login", style = MaterialTheme.typography.h4)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.initiateGoogleSignIn()
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
                        Text("Sign in with Google")
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