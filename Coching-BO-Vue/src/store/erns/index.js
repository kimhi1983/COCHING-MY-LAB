const SHOW_ROWS = [5, 15, 30, 50, 100, 500];

const SYSTEM_SERVICE_CURRENT_VERSION = process.env.SYSTEM_SERVICE_CURRENT_VERSION;

export default {
  namespaced: true,
  state: {
    systemServiceCurrentVersion : SYSTEM_SERVICE_CURRENT_VERSION, //서비스 버전

    showRows : SHOW_ROWS,
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
        label : "Spinning"
      }
    },
    routerHistoryMap : {},

    //라우터현황
    logRouteChange : {
      from : null,
      to : null
    },
  },
  getters: {
    showRows: state => state.showRows,
    loading : state => state.loading,
    routerHistoryMap : state => state.routerHistoryMap,
    logRouteChange : state => state.logRouteChange,
  },
  mutations: {
    CHANGE_OPTION: (state, { key, value }) => {
      // eslint-disable-next-line no-prototype-builtins
      if (state.hasOwnProperty(key)) {
        state[key] = value
      }
    },
    SET_LOADING : (state, loading) => {
      state.loading = loading;
    },
    SET_ROUTER_HISTORY_MAP : (state, {key, value}) => {
      state.routerHistoryMap[key] = value;
    },
    SET_LOG_ROUTE_CHANGE : (state, value) => {
      state.logRouteChange = {...state.logRouteChange, ...value};
    },
  },
  actions: {
    changeOption({ commit }, data) {
      commit('CHANGE_OPTION', data)
    },
    setLoading( {commit}, loading) {
      commit('SET_LOADING', loading)
    },
    setRouterHistoryMap( {commit}, hisData) {
      commit('SET_ROUTER_HISTORY_MAP', hisData)
    },
    setLogRouteChange( {commit}, logRouteChange) {
      commit('SET_LOG_ROUTE_CHANGE', logRouteChange);
    },
  },
}
