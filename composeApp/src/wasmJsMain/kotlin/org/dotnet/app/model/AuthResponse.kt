package org.dotnet.app.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    var token: String,
    var user: UserDTO,
    val isNewUser: Boolean
)
