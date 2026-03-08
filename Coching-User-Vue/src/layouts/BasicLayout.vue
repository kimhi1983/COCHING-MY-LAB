<template>
  <fragment>
    <BasicHeader v-if="fMeta.header.show"/>
    <div class="wrap">    
      <!-- navInfo:{{navInfo}} -->
      <BasicNav />
      <BasicContent />    
      <!-- <BottomFooter /> -->
    </div>

    <!-- coching 로딩-->
    <div class="loader-wrap" v-show="isLoading">
      <div class="loader"></div>
    </div>

    <!-- 쪽지 모달 -->
    <MessageSendModal ref="messageModal"></MessageSendModal>

    <!-- Back to top -->
    <div id="back-to-top"></div>
  </fragment>
</template>

<script>
import { BasicHeader,  BasicContent, BasicNav } from './components'
import { mapState } from 'vuex';
import appConfig from '@/config';
import ernsUtils from '@/components/mixins/ernsUtils';
import MessageSendModal from '@/components/modal/MessageSendModal.vue';
import { isUserLoggedIn, getUserData} from '@/auth/utils';

const DEFAULT_NAVINFO = appConfig.DEFAULT_NAVINFO;
  
export default {
  name: 'BasicLayout',
  components: {
    BasicHeader,
    BasicNav,
    BasicContent,
    MessageSendModal,
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
    }    
  },
  watch: {
    '$route': 'fetchData' //라우트가 변경되면 메소드를 다시 호출됩니다.
  },
  data(){
    return {
      isLoggedIn: false,
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

      _vm.isLoggedIn = (isUserLoggedIn() || null) ? true : false;
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