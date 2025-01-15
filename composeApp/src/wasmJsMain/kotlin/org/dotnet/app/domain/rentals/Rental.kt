package org.dotnet.app.domain.rentals

import kotlinx.serialization.Serializable
import org.dotnet.app.domain.cars.Car

@Serializable
data class Rental(
    val id: Int,
    val car: Car,
    val userId: Int,
    val startDate: String,
    val endDate: String,
    val totalPrice: Double,
    val status: String,
    val startLocation: String,
    val endLocation: String
)
