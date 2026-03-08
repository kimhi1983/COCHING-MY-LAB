
export default {
  namespaced: true,
  state: {
    windowWidth: 0,
    shallShowOverlay: false,
  },
  getters: {
    
  },
  mutations: {
    UPDATE_WINDOW_WIDTH(state, val) {
      state.windowWidth = val
    },
    TOGGLE_OVERLAY(state, val) {
      state.shallShowOverlay = val !== undefined ? val : !state.shallShowOverlay
    },
  },
  actions: {},
}
