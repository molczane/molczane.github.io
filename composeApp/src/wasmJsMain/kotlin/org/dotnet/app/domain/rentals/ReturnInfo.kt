package org.dotnet.app.domain.rentals

import kotlinx.serialization.Serializable

@Serializable
data class ReturnInfo(
    val RentalId: String,
    val Condition: String,
    val EmployeeNotes: String,
    val ReturnDate: String
)
