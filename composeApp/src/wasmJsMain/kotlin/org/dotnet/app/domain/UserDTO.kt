package org.dotnet.app.domain

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id: Int?,
    val email: String,
    val name: String,
    val profileCompleted: Boolean
)
