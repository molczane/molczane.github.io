package org.dotnet.app.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val Id: Int,
    val Email: String,
    val Name: String,
    val ProfileCompleted: Boolean
)
