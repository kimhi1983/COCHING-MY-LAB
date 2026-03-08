<template>
  <fragment>
    <!--여기만 main추가, 버튼 btn-wh-outline 변경-->
    <BasicNav/>
    <div class="wrap" :class="{'active': !showNav}">
      <BasicContent />
      <BottomFooter />
      <!-- coching 로딩-->
      <div class="loader-wrap" v-show="isLoading">
        <div class="loader"></div>
      </div>    
    </div>
  </fragment>
</template>

<script>
import { BasicNav, BasicContent, BottomFooter} from './components'
import { mapState } from 'vuex';
import ernsUtils from '@/components/mixins/ernsUtils';
import {getUserData} from '@/auth/utils';

export default {
  name: 'MainLayout',
  components: {
    BasicNav,
    BasicContent,
    BottomFooter ,  
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
    showNav() {
      return this.$store.state.coching.isNavShow;
    }   
  },
  mounted(){
    const _vm = this;    
    _vm.docReady();
    
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