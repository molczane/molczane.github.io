package org.dotnet.app.domain.offer

import kotlinx.serialization.Serializable
import org.dotnet.app.domain.cars.Car

@Serializable
data class OfferRequest(
    val CarId: Int,
    val RentalName: String = "JEJ Car Rental",
    val CustomerId: Int,
    val PlannedStartDate: String,
    val PlannedEndDate: String
)