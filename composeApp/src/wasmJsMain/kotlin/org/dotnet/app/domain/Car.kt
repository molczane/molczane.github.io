package org.dotnet.app.domain

import kotlinx.serialization.Serializable

@Serializable
data class Car(
    val id: Int,
    // val rentalService: String,
    val producer: String,
    val model: String,
    val type: String,
    val yearOfProduction: String,
    val numberOfSeats: Int,
    val isAvailable: Int,
    val location: String
)

