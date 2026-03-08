<template>
  <div class="misc-wrapper">
    <b-link to="/" class="brand-logo">
      <h2 class="brand-text text-primary ml-1">
        {{appName}}
      </h2>
    </b-link>

    <div class="misc-inner p-2 p-sm-3">
      <div class="w-100 text-center">
        <h2 class="mb-1">
          준비중 입니다.
        </h2>
        <p class="mb-2">
          준비중 입니다. 관리자에게 문의하세요.          
        </p>
        <div>
          <b-button
            variant="primary"
            class="mb-1 btn-sm-block"
            :to="loginRoute()"
          >홈으로 이동</b-button>
        </div>
        <b-img
          fluid
          :src="imgUrl"
          alt="Not authorized page"
        />
      </div>
    </div>
  </div>
</template>

<script>
/* eslint-disable global-require */
import { BLink, BImg, BButton } from 'bootstrap-vue'
import VuexyLogo from '@core/layouts/components/Logo.vue'
import store from '@/store/index'
import { 
  getUserData,
  getHomeRouteForLoggedInUser } from '@/auth/utils';
import { $themeConfig } from '@themeConfig'

export default {
  components: {
    BLink, BImg, BButton, VuexyLogo,
  },
  data() {
    return {
      appName : $themeConfig.app.appName,
      downImg: require('@/assets/images/pages/not-authorized.svg'),
    }
  },
  computed: {
    imgUrl() {
      if (store.state.appConfig.layout.skin === 'dark') {
        // eslint-disable-next-line vue/no-side-effects-in-computed-properties
        this.downImg = require('@/assets/images/pages/not-authorized-dark.svg')
        return this.downImg
      }
      return this.downImg
    },
  },
  methods: {
    loginRoute() {
      const user = getUserData();
      const r = getHomeRouteForLoggedInUser(user ? user.role : null);
      return r;
    },
  },
}
</script>

<style lang="scss">
@import '@core/scss/vue/pages/page-misc.scss';
</style>
