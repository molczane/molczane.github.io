package org.dotnet.app.utils

import org.dotnet.app.model.CredentialResponse
import kotlin.js.JsExport

@JsExport
fun createCredentialResponse(credential: String): JsReference<CredentialResponse> {
    return CredentialResponse(credential).toJsReference()
}

@JsExport
fun setCredentialResponse(credentialResponse: JsReference<CredentialResponse>, credential: String) {
    credentialResponse.get().credential = credential
}

@JsExport
fun getCredentialResponse(credentialResponse: JsReference<CredentialResponse>): String {
    return credentialResponse.get().credential
}