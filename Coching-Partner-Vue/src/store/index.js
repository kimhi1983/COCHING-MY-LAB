import Vue from 'vue'
import Vuex from 'vuex'

// Modules
import app from './app'
import coching from './coching'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    app,
    coching,
  },
  strict: process.env.DEV,
})
