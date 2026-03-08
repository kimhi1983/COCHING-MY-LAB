const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default {
  // pages
  loginPage : `${VUE_APP_BASE_ROUTER_PATH}/login`,

  // Endpoints
  loginEndpoint: '/api/login/login.api',
  refreshEndpoint: '/api/login/refreshToken.api',

  registerEndpoint: '/jwt/register',  
  logoutEndpoint: '/api/login/logout.api',

  // This will be prefixed in authorization header with token
  // e.g. Authorization: Bearer <token>
  //tokenType: 'Bearer',
  //tokenType: '',

  // Value of this property will be used as key to store JWT token in storage
  storageTokenKeyName: 'erns.coching.backoffice.accessToken',
  storageRefreshTokenKeyName: 'erns.coching.backoffice.refreshToken',

  // UserData
  storageUserDataKeyName: 'erns.coching.backoffice.userData',
}
