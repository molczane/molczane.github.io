package org.dotnet.app.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.dotnet.app.domain.rentals.Rental

@Composable
fun RentalCard(
    rental: Rental,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .clickable { onClick() }
            .fillMaxWidth(.5f),
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Rental Header: Car Info and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "${rental.car.producer} ${rental.car.model}",
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.primary
                    )
                    Text(
                        text = "Type: ${rental.car.type}, Seats: ${rental.car.numberOfSeats}",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )
                }
                Text(
                    text = rental.status,
                    style = MaterialTheme.typography.body1,
                    color = when (rental.status.lowercase()) {
                        "completed" -> MaterialTheme.colors.primary
                        "active" -> MaterialTheme.colors.secondary
                        else -> MaterialTheme.colors.error
                    },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Rental Details
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = "Start: ${rental.startDate} (${rental.startLocation})",
                    style = MaterialTheme.typography.body2
                )
                Text(
                    text = "End: ${rental.endDate} (${rental.endLocation})",
                    style = MaterialTheme.typography.body2
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Price:",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
                )
                Text(
                    text = "$${rental.totalPrice}",
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.primary
                )
            }
        }
    }
}
