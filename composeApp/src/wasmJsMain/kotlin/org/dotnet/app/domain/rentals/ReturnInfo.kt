package org.dotnet.app.domain.rentals

import kotlinx.serialization.Serializable

@Serializable
data class ReturnInfo(
    val rentalId: String,
    val condition: String? = null,
    val employeeNotes: String? = null,
    val returnDate: String
)
