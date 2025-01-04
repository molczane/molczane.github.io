package org.dotnet.app.domain

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    var token: String,
    var user: UserDTO,
    val isNewUser: Boolean
)
