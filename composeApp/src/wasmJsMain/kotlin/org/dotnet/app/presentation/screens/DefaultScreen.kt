package org.dotnet.app.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.dotnet.app.presentation.components.FilterSection
import org.dotnet.app.presentation.components.FilteredCarsView
import org.dotnet.app.presentation.components.LoadingView
import org.dotnet.app.presentation.components.StandardCarsView
import org.dotnet.app.presentation.viewModels.CarRentalAppViewModel
import org.dotnet.app.presentation.viewModels.CarRentalUiState

@Composable
fun DefaultScreen(uiState: CarRentalUiState, viewModel: CarRentalAppViewModel, innerPadding: PaddingValues) {
    Row (
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        // Filtering Sidebar (20% width)
        Card(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.2f)  // Changed to use fillMaxWidth with a fraction
                .padding(8.dp),
            elevation = 4.dp
        ) {
            FilterSection(
                uiState = uiState,
                onBrandSelected = { viewModel.updateSelectedBrand(it) },
                onModelSelected = { viewModel.updateSelectedModel(it) },
                onYearSelected = { viewModel.updateSelectedYear(it) },
                onTypeSelected = { viewModel.updateSelectedType(it) },
                onLocationSelected = { viewModel.updateSelectedLocation(it) },
                onResetFilters = { viewModel.resetFilters() },
                onFilter = { viewModel.getFilteredCars(it) },
                getModels = { brand: String -> viewModel.getModelsByBrand(brand) }
            )
        }

        Column (
            modifier = Modifier
                .weight(1f)  // This will take up the remaining space
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /* LOADING SCREEN */
            if (uiState.isLoading) { // time
                LoadingView(uiState, innerPadding)
            }
            /* DISPLAYING FILTERED CARS */
            else if(uiState.areCarsFiltered) {
                FilteredCarsView(viewModel, uiState, innerPadding)
            }
            /* STANDARD DISPLAYING OF CARS */
            else if(!uiState.isLoading && !uiState.areCarsFiltered) {
                StandardCarsView(viewModel, uiState, innerPadding)
            }
        }
    }
}