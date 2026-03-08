import "core-js/stable";

import Vue from 'vue'

import router from './router'
import store from './store'
import App from './App.vue'
import VueCookie from 'vue-cookie'
import {i18n} from '@/utils/i18n';

//Vue Cookie
Vue.use(VueCookie)

// 3rd party plugins
import '@axios'
import '@/libs/acl'
import '@/libs/sweet-alerts'

// VeeValidate locale 설정
import {ValidationProvider, ValidationObserver, localize} from 'vee-validate';
Vue.component('ValidationProvider', ValidationProvider);
Vue.component('ValidationObserver', ValidationObserver);
localize(i18n.locale);


//VueClipboard
import VueClipboard from 'vue-clipboard2' 
Vue.use(VueClipboard);

//Vue ScrollTo
import VueScrollTo from 'vue-scrollto'
Vue.use(VueScrollTo);

// Fragment
import Fragment from 'vue-fragment'
Vue.use(Fragment.Plugin);

//Vue signature
import VueSignature from "vue-signature-pad";
Vue.use(VueSignature);

// import VueQuillEditor from 'vue-quill-editor';
// require('@/assets/css/quill.core.css')
// require('@/assets/css/quill.snow.css')
// require('@/assets/css/quill.bubble.css')

// Vue.use(VueQuillEditor);
// console.log(VueQuillEditor);

// Loading => 자체로딩 화면으로 변경

// Modal
import '@/components/dialog/dialogPlugin';

// import core styles
require('@/assets/css/reset.css')
require('@/assets/css/style.css')
require('@/assets/css/chosen.css')
// require('@/assets/css/swiper-bundle.min.css')

// import assets styles
require('@/assets/scss/style.scss')

Vue.config.productionTip = false

//다국어 함수 $t() 래핑 함수 정의
Vue.prototype.$tt = function(mlt_key) {
  const mk = (mlt_key || "").trim();
  if(!mk){
    return "";
  }

  const tv = (this.$t(mk) || "").trim();
  if(tv.toUpperCase() == mk.toUpperCase()){
    return "";
  }

  return tv;
};

Vue.config.ignoredElements = [/^cropper-/];

import loading from '@/components/mixins/loading';
import device from '@/components/mixins/device';
const vm = new Vue({
  mixins: [loading, device],
  router,
  store,
  render: h => h(App),
  i18n
}).$mount('#app')

window.erns_coching_vue = vm;
