package org.dotnet.app.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id: Int?,
    val email: String,
    val name: String,
    val profileCompleted: Boolean
)
