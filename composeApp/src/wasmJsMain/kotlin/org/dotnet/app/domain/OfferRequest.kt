package org.dotnet.app.domain

import kotlinx.serialization.Serializable

@Serializable
data class OfferRequest(
    val startDate: String,
    val endDate: String,
    val car: Car
)
