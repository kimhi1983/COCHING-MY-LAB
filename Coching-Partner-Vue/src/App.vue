<template>
  <router-view id="app"
    :class="{
      'main': isMainPage,  
      ...wrapClass,
      ...wrapAppClass, 
  }"/>
</template>

<script>
import appConfig from '@/config';
import ernsUtils from '@/components/mixins/ernsUtils';
import {getUserData} from '@/auth/utils';
import {loadLocaleMessages, defaultLocale} from '@/utils/i18n';

export default {  
  mixins: [ernsUtils],
  components: {
    //ScrollToTop,
  },
  computed: {
    isLoading(){
      return this.$store.state.coching.loading.isLoading;
    },
    //TODO : 퍼블적용 후 확인 필요
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
    wrapAppClass(){
      const _vm = this;
      if(!_vm.isAppDevice){
        return {};
      }

      const wrapAppClass = _vm.$route.meta.wrapAppClass;
      const ret = {};

      if(!wrapAppClass){
        return ret;
      }

      for(const cn of wrapAppClass){
        ret[cn] = true;
      }

      return ret;
    }
  },
  beforeCreate() {
    //load local storage
    const isDarkMode = appConfig.getDarkModeInLocalStorage();
    console.debug(`isDarkMode:${isDarkMode}`);
    this.$store.dispatch('coching/setDarkMode', isDarkMode);
		appConfig.setDarkModeInHtml(isDarkMode);
  },
  setup() {
   
  }, 
  async mounted(){
    const _vm = this;  
    
    //loading
    _vm.loading(false);

    //console.debug(_vm.$route);

    { //load storage data
      const userInfo = getUserData();
      _vm.$store.dispatch('coching/setUserInfo', userInfo);
    }

    {
      //Store에 기업 정보 로딩
      const partnerInfoJson = localStorage.getItem(appConfig.storagePartnerInfoKey);
      const sBlil = partnerInfoJson ? JSON.parse(partnerInfoJson) : [];

      _vm.$store.dispatch('coching/setPartnerInfo', sBlil);
    }

    {
      //Store에 baseUrl 로딩
      const baseUrlJson = localStorage.getItem(appConfig.storageUserBaseUrl);
      const sBlil = baseUrlJson ? JSON.parse(baseUrlJson) : [];

      _vm.$store.dispatch('coching/setUserBaseUrlData', sBlil);
    }
    
    // Set device Type 
    {
      const deviceType = _vm.$cookie.get("erns.coching.pc.device") || "WEB";
      console.debug(`deviceType :${deviceType}`);
      _vm.eumSetDeviceType(deviceType || "WEB");    

      const deviceTypeNative = _vm.$cookie.get("erns.coching.pc.device.type") || "pc";
      console.debug(`deviceTypeNative :${deviceTypeNative}`);
      _vm.eumSetDeviceTypeNative(deviceTypeNative || "PC");

      if(_vm.isAppIos){      
        console.info(`I am iOS :${deviceTypeNative}`);  
        _vm.edmRegisterIsoJavascriptInterface();
      }
    }

    {
      //Locale 로드        
      const sLocale = defaultLocale;
       _vm.$store.dispatch('coching/setLocale', sLocale);

      if (!_vm.$i18n.messages[sLocale]) {
        _vm.$i18n.setLocaleMessage(sLocale, await loadLocaleMessages(sLocale));
      }
    }
    
    //Native Modal Activity 종료시 호출
    window.modalActivityClose = function(message){
      if(window.modalCloseCallback && typeof(window.modalCloseCallback) == 'function' ) {
        window.modalCloseCallback(message);
      }
    };

  },
  data() {
    return {
      mainOverlay:{        
        variant: 'dark',
        opacity: 0.10,
        blur: '3px',
        zIndex : 2000,
        rounded : 'lg'
      }
    };
  },  
}
</script>
<style lang="scss">
</style>