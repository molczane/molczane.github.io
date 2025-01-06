package org.dotnet.app.domain.offer

import kotlinx.serialization.Serializable
import org.dotnet.app.domain.cars.Car

@Serializable
data class OfferRequest(
    val startDate: String,
    val endDate: String,
    val car: Car
)
