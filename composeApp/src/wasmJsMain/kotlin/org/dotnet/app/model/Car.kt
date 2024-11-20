package org.dotnet.app.model

import kotlinx.serialization.Serializable

@Serializable
data class Car(
    val brand: String,
    val model: String
)

