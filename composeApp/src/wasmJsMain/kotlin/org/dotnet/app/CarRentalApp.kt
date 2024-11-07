package org.dotnet.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun CarRentalApp() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        var searchBrand by remember { mutableStateOf(TextFieldValue("")) }
        var searchModel by remember { mutableStateOf(TextFieldValue("")) }
        val cars = listOf(
            Car("Toyota", "Corolla"),
            Car("Honda", "Civic"),
            Car("Ford", "Mustang"),
            Car("BMW", "3 Series"),
            Car("Volskwagen", "Passat")
        )
        val filteredCars = cars.filter {
            it.brand.contains(searchBrand.text, ignoreCase = true) &&
                    it.model.contains(searchModel.text, ignoreCase = true)
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Wypożyczalnia Samochodów", style = MaterialTheme.typography.h5)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchBrand,
                onValueChange = { searchBrand = it },
                label = { Text("Wyszukaj po marce") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = searchModel,
                onValueChange = { searchModel = it },
                label = { Text("Wyszukaj po modelu") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { showContent = !showContent }) {
                Text("Pokaż Wyniki")
            }

            AnimatedVisibility(showContent) {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (filteredCars.isNotEmpty()) {
                        filteredCars.forEach { car ->
                            Text("Marka: ${car.brand}, Model: ${car.model}")
                        }
                    } else {
                        Text("Brak wyników")
                    }
                }
            }
        }
    }
}

data class Car(val brand: String, val model: String)