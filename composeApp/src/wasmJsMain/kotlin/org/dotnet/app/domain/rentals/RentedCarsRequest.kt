package org.dotnet.app.domain.rentals

import kotlinx.serialization.Serializable

@Serializable
data class RentedCarsRequest(
    val UserId: String
)
