package org.dotnet.app.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id: Int,
    val mail: String,
    val name: String,
    val surname: String,
    val profileCompleted: Boolean
)
