export const authConfig = {
  clientId: 'oauth2-pkce-client',
  authorizationEndpoint: 'http://localhost:8181/realms/fitness-app/protocol/openid-connect/auth',
  tokenEndpoint: 'http://localhost:8181/realms/fitness-app/protocol/openid-connect/token',
  redirectUri: 'http://localhost:5173',
  scope: 'openid profile email offline_access',
  onRefreshTokenExpire: (event) => event.logIn(),
}

//frontend uses the library react-oauth2-code-pkce and it needs to know the above mentioned config