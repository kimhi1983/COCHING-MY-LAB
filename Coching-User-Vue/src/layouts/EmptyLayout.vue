<template>
  <fragment>
    <div class="wrap">
      <BasicContent />    
    </div>
    <!-- coching 로딩-->
    <div class="loader-wrap" v-show="isLoading">
      <div class="loader"></div>
    </div>
  </fragment>
</template>

<script>
import { BasicContent} from './components'
import { mapState } from 'vuex';
import ernsUtils from '@/components/mixins/ernsUtils';
import {getUserData} from '@/auth/utils';

export default {
  name: 'EmptyLayout',
  components: {
    BasicContent,
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