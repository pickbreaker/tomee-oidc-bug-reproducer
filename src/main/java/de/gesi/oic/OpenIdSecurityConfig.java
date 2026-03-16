package de.gesi.oic;

import jakarta.security.enterprise.authentication.mechanism.http.OpenIdAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.openid.LogoutDefinition;

@OpenIdAuthenticationMechanismDefinition(providerURI = "<providerUri>", clientId = "<clientId>", redirectURI = "${baseURL}/api/secured", redirectToOriginalResource = true, scope = {"openid", "email", "profile", "roles"}, logout = @LogoutDefinition(notifyProvider = true, redirectURI = "${baseURL}/"))
public class OpenIdSecurityConfig {
}