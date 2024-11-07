package org.dotnet.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.dotnet.app.dataSource.cars

@Composable
fun CarRentalApp() {
    MaterialTheme {
        var selectedBrand by remember { mutableStateOf<String?>(null) }
        var selectedModel by remember { mutableStateOf<String?>(null) }
        val brands = cars.map { it.brand }.distinct().sorted()
        val models = cars.filter { it.brand == selectedBrand }.map { it.model }.distinct().sorted()

        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Wypożyczalnia Samochodów", style = MaterialTheme.typography.h5)

            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown for car brand selection
            DropdownMenu(
                label = "Wybierz markę",
                options = brands,
                selectedOption = selectedBrand,
                onOptionSelected = { brand ->
                    selectedBrand = brand
                    selectedModel = null  // Reset model selection when brand changes
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Dropdown for car model selection
            if (selectedBrand != null) {
                DropdownMenu(
                    label = "Wybierz model",
                    options = models,
                    selectedOption = selectedModel,
                    onOptionSelected = { model -> selectedModel = model }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display filtered results
            val filteredCars = cars.filter {
                (selectedBrand == null || it.brand == selectedBrand) &&
                        (selectedModel == null || it.model == selectedModel)
            }

            AnimatedVisibility(visible = filteredCars.isNotEmpty()) {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    filteredCars.forEach { car ->
                        Text("Marka: ${car.brand}, Model: ${car.model}")
                    }
                }
            }

            if (filteredCars.isEmpty()) {
                Text("Brak wyników", style = MaterialTheme.typography.body1)
            }
        }
    }
}

@Composable
fun DropdownMenu(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedOption ?: "",
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    onOptionSelected(option)
                    expanded = false
                }) {
                    Text(option)
                }
            }
        }
    }
}
