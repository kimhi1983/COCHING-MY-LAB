const SHOW_ROWS = [5, 15, 30, 50, 100];

import appConfig from '@/config'

export default {
  namespaced: true,
  state: {
    deviceType : "WEB", //접속 device    
    deviceTypeNative : "android", //접속 android or ios
    locale : 'ko',  //언어
    isSmallGnb : false, //GNB가 작아졌는지 여부

    userInfo : null,    //사용자 프로필     
    joinData : null,    //회원가입 데이터

    showRows : SHOW_ROWS,    

    isDarkMode : false,

    loading : {
      isLoading : false,
      mode : 'spinner', //spinner, progress
      progress : {
        animated : true,
        showProgress : true,
        showValue : true,
        striped : true,
        height : "calc(4vh)",
        max : 100,
        precision : 0,
        value : 0,
        variant : "danger" //"success"
      },
      spinner : {
        label : "Spinning",
        loader : null,
      }
    },
    routerHistoryMap : {},

    noCheckNotiCount : 0,

    pushTokenInfo : {
      token : '',
      transDate : 0,
      transToken : '',
    },

    //라우터현황
    logRouteChange : {
      from : null,
      to : null
    },

    saveRecentSearchKeywords : true, //최근 검색어 저장 여부
    recentSearchKeywords : [], //최근 검색어


    partnerInfo : null, //소속된 기업
    partnerBaseUrlData : null,

    compareProdList : [], //제품 비교
  },
  getters: {
    deviceType : state => state.deviceType,
    locale : state => state.locale,
    isSmallGnb : state => state.isSmallGnb,

    userInfo : state => state.userInfo,    
    joinData : state => state.joinData,
    
    showRows: state => state.showRows,
    isDarkMode: state => state.isDarkMode,
    loading : state => state.loading,
    routerHistoryMap : state => state.routerHistoryMap,

    noCheckNotiCount : state => state.noCheckNotiCount,
    pushTokenInfo : state => state.pushTokenInfo,
    logRouteChange : state => state.logRouteChange,

    partnerInfo : state => state.partnerInfo,
    partnerBaseUrlData: state => state.partnerBaseUrlData,

    saveRecentSearchKeywords : state => state.saveRecentSearchKeywords,
    recentSearchKeywords : state => state.recentSearchKeywords,

    compareProdList : state => state.compareProdList || [],
  },
  mutations: {
    SET_LOCALE : (state, locale) => {
      state.locale = locale;
    }, 
    SET_IS_SMALL_GNB : (state, isSmallGnb) => {
      state.isSmallGnb = isSmallGnb;
    },
    SET_USERINFO : (state, userInfo) => {
      state.userInfo = userInfo;
    },    
    SET_JOIN_DATA : (state, joinData) => {
      state.joinData = joinData;
    },
    CHANGE_OPTION: (state, { key, value }) => {
      // eslint-disable-next-line no-prototype-builtins
      if (state.hasOwnProperty(key)) {
        state[key] = value
      }
    },
    SET_LOADING : (state, loading) => {
      state.loading = loading;
    },
    SET_DARK_MODE : (state, value) => {
      state.isDarkMode = value;
    },
    SET_ROUTER_HISTORY_MAP : (state, {key, value}) => {
      state.routerHistoryMap[key] = value;
    },
    SET_NO_CHECK_NOTI_COUNT : (state, value) => {
      state.noCheckNotiCount = value;
    },
    SET_PUSH_TOKEN_INFO : (state, value) => {
      state.pushTokenInfo = value;
    },
    SET_LOG_ROUTE_CHANGE : (state, value) => {
      state.logRouteChange = {...state.logRouteChange, ...value};
    },
    SET_PARTNER_INFO : (state, value) => {
      state.partnerInfo = value;
    },
    SET_PARTNER_BASE_URL_DATA : (state, value) => {
      state.partnerBaseUrlData = value;
    },
    SET_SAVE_RECENT_SEARCH_KEYWORDS : (state, value) => {
      state.saveRecentSearchKeywords = value;
    },
    SET_RECENT_SEARCH_KEYOWRDS: (state, value) => {
      state.recentSearchKeywords = [...value];
    },
    SET_COMPARE_PROD_LIST: (state, value) => {
      state.compareProdList = value || [];
    }
  },
  actions: {
    setLocale( {commit}, locale) {
      //save local storage
      localStorage.setItem(appConfig.storageLocale, locale);

      commit('SET_LOCALE', locale)
    },

    setIsSmallGnb( {commit}, isSmallGnb) {
      commit('SET_IS_SMALL_GNB', isSmallGnb)
    },

    setUserInfo( {commit}, userInfo) {
      commit('SET_USERINFO', userInfo)
    },
    setJoinData( {commit}, joinData) {
      commit('SET_JOIN_DATA', joinData)
    },
    changeOption({ commit }, data) {
      commit('CHANGE_OPTION', data)
    },
    setLoading( {commit}, loading) {
      commit('SET_LOADING', loading)
    },
    setDarkMode( {commit}, value) {
      //save local storage
      localStorage.setItem(appConfig.storageDarkMode, JSON.stringify(value));

      commit('SET_DARK_MODE', value)
    },    
    setRouterHistoryMap( {commit}, hisData) {
      commit('SET_ROUTER_HISTORY_MAP', hisData)
    },
    setNoCheckNotiCount( {commit}, value) {
      commit('SET_NO_CHECK_NOTI_COUNT', value)
    },
    setPushTokenInfo( {commit}, pushTokenInfo) {
      //save local storage
      localStorage.setItem(appConfig.storagePushTokenKey, JSON.stringify(pushTokenInfo));
      
      commit('SET_PUSH_TOKEN_INFO', pushTokenInfo);
    },
    setLogRouteChange( {commit}, logRouteChange) {
      commit('SET_LOG_ROUTE_CHANGE', logRouteChange);
    },

    setPartnerInfo( {commit}, partnerInfo) {     
      localStorage.setItem(appConfig.storagePartnerInfoKey, JSON.stringify(partnerInfo)); 
      commit('SET_PARTNER_INFO', partnerInfo);
    },
    setPartnerBaseUrlData( {commit}, partnerBaseUrlData) {     
      localStorage.setItem(appConfig.storagePartnerBaseUrl, JSON.stringify(partnerBaseUrlData)); 
      commit('SET_PARTNER_BASE_URL_DATA', partnerBaseUrlData);
    },
    setSaveRecentSearchKeywords( {commit}, value) {
      //save local storage
      localStorage.setItem(appConfig.storageSaveRecentSearchKeywords, JSON.stringify(value));

      commit('SET_SAVE_RECENT_SEARCH_KEYWORDS', value)
    },
    setRecentSearchKeywords( {commit}, value) {
      //save local storage
      localStorage.setItem(appConfig.storageRecentSearchKeywords, JSON.stringify(value));

      commit('SET_RECENT_SEARCH_KEYOWRDS', value)
    },
    setCompareProdList( {commit}, value) {
      //save local storage
      localStorage.setItem(appConfig.storageCompareProdList, JSON.stringify(value));

      commit('SET_COMPARE_PROD_LIST', value)
    },
  },
}
