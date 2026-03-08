<template>
  <div
    id="app"
    class="h-100"
    :class="[skinClasses]"
  >
    <b-overlay 
      id="coching-bo-main-loading"
      :show="isLoading"
      v-bind="mainOverlay"
      class="h-100"      
    >  
      <component :is="layout">
        <router-view />
      </component>
    
      <scroll-to-top v-if="enableScrollToTop" />    
    </b-overlay>
  </div>  
</template>

<script>
import ScrollToTop from '@core/components/scroll-to-top/ScrollToTop.vue'

// This will be populated in `beforeCreate` hook
import { $themeColors, $themeBreakpoints, $themeConfig } from '@themeConfig'
import { provideToast } from 'vue-toastification/composition'
import { watch } from '@vue/composition-api'
import useAppConfig from '@core/app-config/useAppConfig'

import { useWindowSize, useCssVar } from '@vueuse/core'

import store from '@/store'

import loading from '@/components/mixins/loading'

const LayoutVertical = () => import('@/layouts/vertical/LayoutVertical.vue')
const LayoutHorizontal = () => import('@/layouts/horizontal/LayoutHorizontal.vue')
const LayoutFull = () => import('@/layouts/full/LayoutFull.vue')

export default {  
  mixins: [loading],
  components: {

    // Layouts
    LayoutHorizontal,
    LayoutVertical,
    LayoutFull,

    ScrollToTop,
  },
  // ! We can move this computed: layout & contentLayoutType once we get to use Vue 3
  // Currently, router.currentRoute is not reactive and doesn't trigger any change
  computed: {
    layout() {
      if (this.$route && this.$route.meta.layout === 'full') return 'layout-full'
      return `layout-${this.contentLayoutType}`
    },
    contentLayoutType() {
      return this.$store.state.appConfig.layout.type
    },
    isLoading(){
      const _vm = this;
      const ret = _vm.$store.state.erns.loading.isLoading;
      if(ret){
        const scrollHeight = document.body.scrollHeight;
        const clientHeight = document.body.clientHeight;
        const maxVal = Math.max(scrollHeight, clientHeight);
        _vm.mainOverlay.style.height = `${maxVal}px !important`;
      }else{
        _vm.mainOverlay.style.height = '100% !important';
      }

      return ret;
    }
  },  
  beforeCreate() {
    // Set colors in theme
    const colors = ['primary', 'secondary', 'success', 'info', 'warning', 'danger', 'light', 'dark']

    // eslint-disable-next-line no-plusplus
    for (let i = 0, len = colors.length; i < len; i++) {
      $themeColors[colors[i]] = useCssVar(`--${colors[i]}`, document.documentElement).value.trim()
    }

    // Set Theme Breakpoints
    const breakpoints = ['xs', 'sm', 'md', 'lg', 'xl']

    // eslint-disable-next-line no-plusplus
    for (let i = 0, len = breakpoints.length; i < len; i++) {
      $themeBreakpoints[breakpoints[i]] = Number(useCssVar(`--breakpoint-${breakpoints[i]}`, document.documentElement).value.slice(0, -2))
    }

    // Set RTL
    const { isRTL } = $themeConfig.layout
    document.documentElement.setAttribute('dir', isRTL ? 'rtl' : 'ltr')
  },
  setup() {
    const { skin, skinClasses } = useAppConfig()
    const { enableScrollToTop } = $themeConfig.layout

    // If skin is dark when initialized => Add class to body
    if (skin.value === 'dark') document.body.classList.add('dark-layout')

    // Provide toast for Composition API usage
    // This for those apps/components which uses composition API
    // Demos will still use Options API for ease
    provideToast({
      hideProgressBar: true,
      closeOnClick: false,
      closeButton: false,
      icon: false,
      timeout: 3000,
      transition: 'Vue-Toastification__fade',
    })

    // Set Window Width in store
    store.commit('app/UPDATE_WINDOW_WIDTH', window.innerWidth);
    const { width: windowWidth } = useWindowSize()
    watch(windowWidth, val => {
      store.commit('app/UPDATE_WINDOW_WIDTH', val)
    })

    return {
      skinClasses,
      enableScrollToTop,
    }
  },
  mounted(){
    const _vm = this;    
    
    //loading
    _vm.loading(false);
  },  
  data() {
    return {
      mainOverlay:{        
        variant: 'dark',
        opacity: 0.30,
        blur: '3px',
        zIndex : 2000,
        style : {
          height:'100% !important'
        }
      }
    };
  },  
}
</script>
<style lang="scss">
@import '@core/scss/vue/libs/vue-flatpicker.scss';
#coching-bo-main-loading {
  .b-overlay.position-absolute{
    //height:calc(100vh)
  }
  
}
</style>