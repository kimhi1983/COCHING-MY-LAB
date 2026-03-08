<template>
  <div>
    <AlertModal ref="alertModal"></AlertModal>
    <ConfirmModal ref="confirmModal"></ConfirmModal>
  </div>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import { getHomeRouteForLoggedInUser } from '@/auth/utils';
import moment from 'moment';

export default {
  name: 'coching-auth',
  mixins: [ernsUtils],
  components : {    
  },
  computed : { 
    publicPath(){
      return process.env.PUBLIC_PATH
    },   
  },
  props: {
    nextUrl: {
      type: String,
      default : ''
    },
    retRoute: {
      type: Object
    }    
  },
  data(){
    return {
      
    }
  },
  mounted(){
    const _vm = this;
    _vm.docReady();

    
  },
  beforeDestroy(){
    const _vm = this;
	},
  methods: {
    async docReady(){
      const _vm = this;

      const action = window.$giCochingApp.getServerSideAuchAction();
      
      console.debug(`action:${action}`);
      
      switch(action){
        case "userEmailCheckDone":          
          await _vm.$refs["alertModal"].open({
            title: _vm.$t('mypage.auth.label001') || '메일인증',
            content : _vm.$t('mypage.auth.label002') || '이메일 인증이 완료되었습니다.'
          });
          location.href = '/';
          //_vm.ermReplacePage({name:'coching-login'});
          return;

        case "join":                    
          
        default:
          //location.href = `${useJwt.jwtConfig.loginPage}`;
          break;
      }      
    }
  }  
}

</script>

<style lang="scss" scoped>  
</style>

<style lang="scss">
</style>