package org.dotnet.app.domain.offer

import kotlinx.serialization.Serializable


@Serializable
data class Offer(
    val userId: Int,
    val carId: Int,
    val dailyRate: Float,
    val insuranceRate: Float,
    val validUntil: String,
    val isActive: Boolean
)
