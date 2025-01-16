package org.dotnet.app.domain.rentals

import kotlinx.serialization.Serializable

@Serializable
data class ReturnRequest(
    val UserId: String,
    val RentalId: String,
    val RentalName: String
)
