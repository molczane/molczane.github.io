package org.dotnet.app.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.dotnet.app.domain.cars.Car

@Composable
fun CarDetailsCard(car: Car, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("${car.producer} ${car.model}", style = MaterialTheme.typography.h6)
            Text(car.location, style = MaterialTheme.typography.body1)
            Text(car.type, style = MaterialTheme.typography.body1)
            Text(car.yearOfProduction, style = MaterialTheme.typography.body1)
        }
    }
}