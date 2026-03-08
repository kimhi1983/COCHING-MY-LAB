import jwtDefaultConfig from './jwtDefaultConfig'
import Vue from 'vue'
import appConfig from '@/config'
import store from '@/store';

class ApiError extends Error {
  constructor(message = "", ...args) {
    super(message, ...args);
    this.response = args[0];
  }
}

export default class JwtService {
  // Will be used by this service for making API calls
  axiosIns = null

  // jwtConfig <= Will be used by this service
  jwtConfig = { ...jwtDefaultConfig }

  // For Refreshing Token
  isAlreadyFetchingAccessToken = false

  // For Refreshing Token
  subscribers = []

  constructor(axiosIns, jwtOverrideConfig) {
    this.axiosIns = axiosIns
    this.jwtConfig = { ...this.jwtConfig, ...jwtOverrideConfig }

    // Request Interceptor
    this.axiosIns.interceptors.request.use(
      config => {
        // Get token from localStorage
        const accessToken = this.getToken()

        // If token is present add it to request's Authorization Header
        if (accessToken) {
          // eslint-disable-next-line no-param-reassign
          //config.headers.Authorization = `${this.jwtConfig.tokenType} ${accessToken}`
          config.headers["X-AUTH-TOKEN"] = `${accessToken}`
        }
        const localeInfo = localStorage.getItem(appConfig.storageLocale);
        let retLocale = localeInfo ? localeInfo : 'ko';
        config.headers["COCHING-LOCALE"] = retLocale
        return config
      },
      error => Promise.reject(error),
    )

    // Add request/response interceptor
    this.axiosIns.interceptors.response.use(
      response => {        
        return this.onResponseHandler(response);
      },
      async error => {
        const _vm = this;
        // const { config, response: { status } } = error
        const { config, response } = error
        const originalRequest = config
        //console.error(error);

        if (response && response.status === 401) {
          const {data:res} = response;
          //console.error(error);
          //console.info(response);
          
          if(!(res.resultCode === "9401" || res.resultCode === "9403")){
            //console.info(response);
            await _vm.forceLogoutInvalidAccess();
            return Promise.reject(error);
          }

          if (!_vm.isAlreadyFetchingAccessToken) {
            _vm.isAlreadyFetchingAccessToken = true;
            _vm.refreshToken().then(r => {
              _vm.isAlreadyFetchingAccessToken = false;

              // Update accessToken in localStorage
              const {resultCode, resultData} = r;
              if(resultCode == "0000"){
                _vm.setToken(resultData.accessToken);
                _vm.setRefreshToken(resultData.refreshToken);
                _vm.onAccessTokenFetched(resultData.accessToken);

              }else{
                console.error("Cannot refresh token");
                throw 'Cannot refresh token';
              }
            }).catch(err=>{
              _vm.forceLogout();
            })
          }
          const retryOriginalRequest = new Promise(resolve => {
            _vm.addSubscriber(accessToken => {
              // Make sure to assign accessToken according to your response.
              // Check: https://pixinvent.ticksy.com/ticket/2413870
              // Change Authorization header
              //originalRequest.headers.Authorization = `${_vm.jwtConfig.tokenType} ${accessToken}`
              originalRequest.headers["X-AUTH-TOKEN"] = `${accessToken}`
              resolve(_vm.axiosIns(originalRequest))
            })
          })
          return retryOriginalRequest
        }

        if(config.url == _vm.jwtConfig.refreshEndpoint){
          await _vm.forceLogout();
        }

        return Promise.reject(error)
      },
    )
  }

  async onAccessTokenFetched(accessToken) {
    this.subscribers = this.subscribers.filter(callback => callback(accessToken));

    //Push token 처리
    const _vueMainVm = window.erns_coching_vue;
    if(_vueMainVm && _vueMainVm.ermSendPushToken){
      await _vueMainVm.ermSendPushToken();
    }

    //사용자 기본정보 로드
    if(_vueMainVm && _vueMainVm.ermLoadUserBasicInfo){
      await _vueMainVm.ermLoadUserBasicInfo();
    }
  }

  addSubscriber(callback) {
    this.subscribers.push(callback)
  }

  getToken() {
    return localStorage.getItem(this.jwtConfig.storageTokenKeyName)
  }

  getRefreshToken() {
    return localStorage.getItem(this.jwtConfig.storageRefreshTokenKeyName)
  }

  setToken(value) {
    localStorage.setItem(this.jwtConfig.storageTokenKeyName, value)
  }

  setRefreshToken(value) {
    localStorage.setItem(this.jwtConfig.storageRefreshTokenKeyName, value)
  }

  login(...args) {
    return this.axiosIns.post(this.jwtConfig.loginEndpoint, ...args)
  }

  register(...args) {
    return this.axiosIns.post(this.jwtConfig.registerEndpoint, ...args)
  }

  async logout(){
    const accessToken = this.getToken();
    const refreshToken = this.getRefreshToken();
    const logRouteChange = store.state.coching.logRouteChange || {from : null,
      to : null
    };
    const currentLogRoute = logRouteChange.to || {};

    let resLogout = null;
    try{
      const headers = {};
      if (accessToken) {
        headers["X-AUTH-TOKEN"] = `${accessToken}`;
      }

      //서버에 토큰 BlackList 로 처리요청
      resLogout = await this.axiosIns.post(this.jwtConfig.logoutEndpoint, {
        accessToken : accessToken
        , refreshToken : refreshToken
        , routerName : currentLogRoute.name || ''
      });
    }catch(err){
      console.error(err);
    }
    
    // Remove Token
    localStorage.removeItem(this.jwtConfig.storageTokenKeyName)
    localStorage.removeItem(this.jwtConfig.storageRefreshTokenKeyName)

    // Remove userData from localStorage
    localStorage.removeItem(this.jwtConfig.storageUserDataKeyName)

    // Remove BizInfo List
    localStorage.removeItem(appConfig.storageBizLicInfoListKey)

    return resLogout;
  }

  refreshToken() {
    return this.axiosIns.post(this.jwtConfig.refreshEndpoint, {
      accessToken : this.getToken(),
      refreshToken: this.getRefreshToken(),
    })
  }

  async forceLogout(){
    const _vm = this;

    const _vueMainVm = window.erns_coching_vue;
    if(_vueMainVm && _vueMainVm.loading){
      _vueMainVm.loading(false);
    }

    _vueMainVm.$store.dispatch('coching/setUserInfo', null);  //회원정보
    _vueMainVm.$store.dispatch('coching/setJoinData', null); //회원가입정보

    //로그 아웃 처리
    await _vm.logout();
    window.location.href = _vm.jwtConfig.loginPage;
  }

  async forceLogoutInvalidAccess(){
    const _vm = this;

    const _vueMainVm = window.erns_coching_vue;
    if(_vueMainVm && _vueMainVm.loading){
      _vueMainVm.loading(false);
    }

    //로그 아웃 처리
    await _vm.logout();
    _vueMainVm.$store.dispatch('coching/setUserInfo', null);  //회원정보
    _vueMainVm.$store.dispatch('coching/setJoinData', null); //회원가입정보

    window.location.href = _vm.jwtConfig.loginPage;
  }

  async onResponseHandler(response){
    const _vm = this;
    const {config, data:res} = response;
    const {resultData, resultCode, resultFailMessage} = res;

    if(response.data instanceof Blob){
      return response;
    }

    if(resultCode == null || resultCode == undefined){
      console.debug(response);
      return Promise.reject(new Error('통신오류가 발생하였습니다.'));
    }

    // 만약 resultCode 가 "0000" 가 아니면 오류    
    if (resultCode != "0000") {
      console.debug(response);

      // 10401: Token expired;
      // 10402: Illegal token;
      // 10403: Token is blockd;
      if (config.url == _vm.jwtConfig.refreshEndpoint || resultCode === "9401" || resultCode === "9402" || resultCode === "9403") {
        // to re-login
        await _vm.forceLogout();
      }

      if (resultCode === "9402" || resultCode === "9403") {
        // to re-login
        await _vm.forceLogout();
      }

      return Promise.reject(new ApiError(resultFailMessage || '알수 없는 오류가 발생하였습니다.'
        , response
      ));
      
    } else {
      return res
    }
  }
}
