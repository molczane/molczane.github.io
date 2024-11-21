package org.dotnet.app.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.dotnet.app.model.Offer

@Composable
fun OfferView(
    offer: Offer,
    onTimerEnd: () -> Unit,
    onRent: (Boolean) -> Unit = {},
    onRentClick: (onRent: (Boolean) -> Unit ) -> Unit = {},
) {
    var timeLeft by remember { mutableStateOf(10 * 60) } // 10 minutes in seconds
    val minutes = timeLeft / 60
    val seconds = timeLeft % 60

    // Timer effect
    LaunchedEffect(timeLeft) {
        if (timeLeft > 0) {
            delay(1000L)
            timeLeft -= 1
        } else {
            onTimerEnd() // Trigger callback when timer ends
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(.5f)
            .padding(16.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Day Rate: $${offer.dayRate}",
                style = MaterialTheme.typography.body1
            )
            Text(
                text = "Insurance Rate: $${offer.insuranceRate}",
                style = MaterialTheme.typography.body1
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Timer
            Text(
                text = "Time left: ${formatTime(minutes, seconds)}",
                style = MaterialTheme.typography.h5,
                color = if (timeLeft > 0) MaterialTheme.colors.primary else MaterialTheme.colors.error
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onRentClick(onRent) },
                elevation = ButtonDefaults.elevation(defaultElevation = 15.dp),
                modifier = Modifier.padding(12.dp)
            ) {
                Text("Rent this car")
            }
        }
    }
}

// Custom function to format the time as MM:SS
fun formatTime(minutes: Int, seconds: Int): String {
    val minutesStr = if (minutes < 10) "0$minutes" else "$minutes"
    val secondsStr = if (seconds < 10) "0$seconds" else "$seconds"
    return "$minutesStr:$secondsStr"
}
