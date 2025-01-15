package org.dotnet.app.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.dotnet.app.presentation.viewModels.CarRentalUiState

@Composable
fun LoadingView(uiState: CarRentalUiState, innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = MaterialTheme.colors.primary
        )
        PaginationControls(
            currentPage = uiState.currentPageNumber,
            totalPages = uiState.totalPages,
            onPageSelected = {
                /* DO NOTHING */
            }
        )
        Footer()
    }
}