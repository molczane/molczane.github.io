package org.dotnet.app.model

import kotlinx.serialization.Serializable

@Serializable
data class OfferRequest(
    val startDate: String,
    val endDate: String,
    val car: Car
)
