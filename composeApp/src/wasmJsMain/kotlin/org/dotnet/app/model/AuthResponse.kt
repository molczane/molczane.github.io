package org.dotnet.app.model

data class AuthResponse(
    var token: String,
    var user: UserDTO,
    val isNewUser: Boolean
)
