import Vue from 'vue'
//import { ToastPlugin, ModalPlugin } from 'bootstrap-vue'
import {BootstrapVue, IconsPlugin} from 'bootstrap-vue'
import VueCompositionAPI from '@vue/composition-api'

import i18n from '@/libs/i18n'
import router from './router'
import store from './store'
import App from './App.vue'

// Global Components
import './global-components'

// 3rd party plugins
import '@axios'
import '@/libs/acl'
import '@/libs/portal-vue'
import '@/libs/clipboard'
import '@/libs/toastification'
import '@/libs/sweet-alerts'
import '@/libs/vue-select'

// BSV Plugin Registration
// Vue.use(ToastPlugin)
// Vue.use(ModalPlugin)
Vue.use(BootstrapVue)
Vue.use(IconsPlugin)

// VeeValidate locale 설정
import {ValidationProvider, ValidationObserver, localize} from 'vee-validate';
Vue.component('ValidationProvider', ValidationProvider);
Vue.component('ValidationObserver', ValidationObserver);
localize('ko');

// Composition API
Vue.use(VueCompositionAPI)

// Feather font icon - For form-wizard
// * Shall remove it if not using font-icons of feather-icons - For form-wizard
require('@core/assets/fonts/feather/iconfont.css') // For form-wizard

// import core styles
require('@core/scss/core.scss')

// import assets styles
require('@/assets/scss/style.scss')


Vue.config.productionTip = false

import loading from '@/components/mixins/loading';
const vm = new Vue({
  mixins: [loading],
  router,
  store,
  i18n,
  render: h => h(App),
}).$mount('#app')

window.erns_coching_bo_vue = vm;
