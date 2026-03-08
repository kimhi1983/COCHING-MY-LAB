<template>
  <!--여기만 main추가, 버튼 btn-wh-outline 변경-->
  <div class="wrap">
    <BasicHeader/>
    <BasicNav />
    <BasicContent />
    <!-- coching 로딩-->
    <div class="loader-wrap" v-show="isLoading">
      <div class="loader"></div>
    </div>    

    <!-- 쪽지 모달 -->
    <MessageSendModal ref="messageModal"></MessageSendModal>
  </div>
</template>

<script>
import { BasicHeader, BasicContent, BasicNav} from './components'
import { mapState } from 'vuex';
import ernsUtils from '@/components/mixins/ernsUtils';
import { isUserLoggedIn, getUserData} from '@/auth/utils';
import MessageSendModal from '@/components/modal/MessageSendModal.vue';

export default {
  name: 'MainLayout',
  components: {
    BasicHeader,
    BasicContent,
    BasicNav,

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
  data(){
    return {
      isLoggedIn: false,      
    }
  }, 
  mounted(){
    const _vm = this;    
    _vm.docReady();
    _vm.fetchData();
    
    _vm.loadLoginInfo();
  },
  methods: {
    handleClickOutside() {
      this.$store.dispatch('app/closeSideBar', { withoutAnimation: false })
    },

    loadLoginInfo(){
      const _vm = this;
      const userInfo = getUserData();
      _vm.$store.dispatch('coching/setUserInfo', userInfo);      
    },

    fetchData(){
      const _vm = this;

      _vm.isLoggedIn = (isUserLoggedIn() || null) ? true : false;
    },

    docReady(){
      const _vm = this;
    },
  }
}
</script>

<style lang="scss" scoped>
  
</style>
<style lang="scss">

</style>