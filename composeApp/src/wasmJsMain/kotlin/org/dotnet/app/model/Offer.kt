package org.dotnet.app.model

import kotlinx.datetime.internal.JSJoda.LocalDate
import kotlinx.serialization.Serializable


@Serializable
data class Offer(
    val id: Int,
    val carID: Int,
    val dayRate: Long,
    val insuranceRate: Long,
    val validUntil: String
)
