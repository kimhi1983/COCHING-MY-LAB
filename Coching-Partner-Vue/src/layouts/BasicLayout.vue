<template>
  <fragment>
    <BasicNav />  
    <div class="wrap" :class="{...wrapClass, 'active': !showNav}">  
      <!-- navInfo:{{navInfo}} -->
      <BasicContent />    
      <BottomFooter />
    </div>

    <!-- coching 로딩-->
    <div class="loader-wrap" v-show="isLoading">
      <div class="loader"></div>
    </div>


    <!-- Back to top -->
    <div id="back-to-top"></div>
  </fragment>
</template>

<script>
import { BasicNav, BasicContent, BottomFooter} from './components'
import { mapState } from 'vuex';
import appConfig from '@/config';
import ernsUtils from '@/components/mixins/ernsUtils';
import {getUserData} from '@/auth/utils';

const DEFAULT_NAVINFO = appConfig.DEFAULT_NAVINFO;
  
export default {
  name: 'BasicLayout',
  components: {
    BasicNav,
    BasicContent,
    BottomFooter,
  },
  mixins: [ernsUtils],  
  computed: {
    ...mapState({
      sidebar: state => state.app.sidebar,
      device: state => state.app.device,
      showSettings: state => state.settings.showSettings,
      needTagsView: state => state.settings.tagsView,
      fixedHeader: state => state.settings.fixedHeader
    }),
    isLoading(){
      return this.$store.state.coching.loading.isLoading;
    },
    classObj() {
      return {
        mobile: this.device === 'mobile'
      }
    },
    wrapClass(){
      const _vm = this;
      const wrapClass = _vm.$route.meta.wrapClass;
      const ret = {};
      if(!wrapClass){
        return ret;
      }

      for(const cn of wrapClass){
        ret[cn] = true;
      }

      return ret;
    },  
    showNav() {
      return this.$store.state.coching.isNavShow;
    }  
  },
  watch: {
    '$route': 'fetchData' //라우트가 변경되면 메소드를 다시 호출됩니다.
  },
  data(){
    return {
      navInfo:{...DEFAULT_NAVINFO},
      fMeta:{
        header    : {...DEFAULT_NAVINFO.meta.header},
        bottomNav : {...DEFAULT_NAVINFO.meta.bottomNav}
      }
    }
  }, 
  mounted(){
    const _vm = this;    
    _vm.docReady();
    _vm.fetchData(); 
  },
  methods: {
    handleClickOutside() {
      this.$store.dispatch('app/closeSideBar', { withoutAnimation: false })
    },

    fetchData(){
      const _vm = this;

      const curRoute = _vm.$router.currentRoute;
      _vm.navInfo = {
        ...DEFAULT_NAVINFO,
        curPath : curRoute.path,
        meta : {...curRoute.meta}
      };

      _vm.fMeta.header = {...DEFAULT_NAVINFO.meta.header, ...curRoute.meta.header};
      _vm.fMeta.bottomNav = {...DEFAULT_NAVINFO.meta.bottomNav, ...curRoute.meta.bottomNav};
    },

    docReady(){
      const _vm = this;

      _vm.uiScript();
    },

    uiScript(){
      /*back-to-top */
      var btn = $('#back-to-top');

      $(window).scroll(function () {
        if ($(window).scrollTop() > 300) {
          btn.addClass('show');
        } else {
          btn.removeClass('show');
        }
      });

      btn.on('click', function (e) {
        e.preventDefault();
        $('html, body').animate({
          scrollTop: 0
        }, '400');
      });
    }
  }
}
</script>

<style lang="scss" scoped>
  
</style>
<style lang="scss">

</style>