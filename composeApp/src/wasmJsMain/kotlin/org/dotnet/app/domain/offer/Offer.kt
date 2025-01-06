package org.dotnet.app.domain.offer

import kotlinx.serialization.Serializable


@Serializable
data class Offer(
    val id: Int,
    val carID: Int,
    val dayRate: Long,
    val insuranceRate: Long,
    val validUntil: String
)
