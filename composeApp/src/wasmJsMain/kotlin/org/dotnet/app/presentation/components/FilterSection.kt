package org.dotnet.app.presentation.components

import androidx.compose.runtime.Composable
import org.dotnet.app.presentation.viewModels.CarRentalUiState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.dotnet.app.domain.cars.CarFilters

@Composable
fun FilterSection(
    uiState: CarRentalUiState,
    onBrandSelected: (String?) -> Unit,
    onModelSelected: (String?) -> Unit,
    onYearSelected: (String?) -> Unit,
    onTypeSelected: (String?) -> Unit,
    onLocationSelected: (String?) -> Unit,
    onResetFilters: () -> Unit,
    onFilter: (CarFilters) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Filtry",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Brand Filter
        FilterItem(
            title = "Marka",
            options = uiState.distinctBrands, // Replace it with dynamic data
            selectedOption = uiState.selectedBrand,
            onOptionSelected = onBrandSelected
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Model Filter
        if (uiState.selectedBrand != null) {
            FilterItem(
                title = "Model",
                options = listOf("Model 1", "Model 2"), // Replace it with dynamic data
                selectedOption = uiState.selectedModel,
                onOptionSelected = onModelSelected
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Type Filter
        FilterItem(
            title = "Typ",
            options = uiState.distinctTypes, // Replace it with dynamic data
            selectedOption = uiState.selectedType,
            onOptionSelected = onTypeSelected
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Year Filter
        FilterItem(
            title = "Rok produkcji",
            optionsNumbers = uiState.distinctYears, // Replace it with dynamic data
            selectedOption = uiState.selectedYear,
            onOptionSelected = onYearSelected
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Location Filter
        FilterItem(
            title = "Lokalizacja",
            options = uiState.distinctLocations, // Replace it with dynamic data
            selectedOption = uiState.selectedLocation,
            onOptionSelected = onLocationSelected
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Reset Filters Button
        Button(
            onClick = onResetFilters,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Resetuj filtry")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reset Filters Button
        Button(
            onClick = { onFilter(
                CarFilters(
                    producer = uiState.selectedBrand,
                    model = uiState.selectedModel,
                    yearOfProduction = uiState.selectedYear,
                    type = uiState.selectedType,
                    location = uiState.selectedLocation
                )
            ) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Filtruj")
        }
    }
}