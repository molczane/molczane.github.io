package org.dotnet.app.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun NotificationSnackbar(message: String, onDismiss: () -> Unit) {
    Snackbar(
        action = {
            TextButton(onClick = onDismiss) {
                Text("OK", color = Color.White)
            }
        },
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = message)
    }
}