package org.dotnet.app.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import org.dotnet.app.model.Car


@Composable
fun ValuationScreen(
    onRequestValuation: (String, String, Car) -> Unit, // Callback do wysyłania żądania
    valuationResult: String?, // Wynik wyceny
    car: Car
) {
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Wyceń wypożyczenie",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Data początkowa
        OutlinedTextField(
            value = startDate,
            onValueChange = { startDate = it },
            label = { Text("Data początkowa (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Data końcowa
        OutlinedTextField(
            value = endDate,
            onValueChange = { endDate = it },
            label = { Text("Data końcowa (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Przyciski
        Button(
            onClick = {
                isLoading = true
                onRequestValuation(startDate, endDate, car)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = startDate.isNotBlank() && endDate.isNotBlank() && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Wyślij zapytanie o wycenę")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Wyświetlenie wyniku wyceny
        valuationResult?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.primary
            )
        }
    }
}
