package org.dotnet.app.domain.authentication

import org.dotnet.app.domain.user.User

data class AuthRegisterResponse(
    val Token: String?,
    val User: User
)