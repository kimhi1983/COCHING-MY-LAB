import useJwt from '@/auth/jwt/useJwt';
import jwtDefaultConfig from '@core/auth/jwt/jwtDefaultConfig';
import appConfig from '@/config';
import {setPushToken} from '@/api/coching/member/message';
import { getPtnInfo } from '@/api/coching/member/partnerInfo';
import { getPartnerBaseUrl } from '@/api/coching/comm/common';

const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default {
  data() {
    return {

    }
  },
  filters: {
    
  },
  computed: {
    deviceType(){
      return this.$store.state.coching.deviceType;
    },
    isAppDevice(){
      return this.deviceType == "APP";
    },

    deviceTypeNative(){
      return this.$store.state.coching.deviceTypeNative;
    },
    
    isAppAndroid(){
      return this.deviceTypeNative == "ANDROID";
    },
    isAppIos(){
      return this.deviceTypeNative == "IOS";
    },
    isMainPage(){
      return this.$route.name == "coching-main"
    },
    partnerInfo(){
      return this.$store.state.coching.partnerInfo;
    },
  },
  methods: {  
    ermOpenWindow(url){
      const _vm = this;      
      
      if(_vm.isAppDevice && window.erns_coching_app && window.erns_coching_app.openBrowser){        
        window.erns_coching_app.openBrowser(url);
        return;
      }
      
      window.open(url, '_blank');
    },

    //Push Router
    ermPushPage(routerData){
      const _vm = this;      
      // //console.debug("$this.$router");
      // //console.debug(_vm.$router);
      // if(_vm.isAppDevice && window.erns_coching_app && window.erns_coching_app.stackPage){
      //   window.erns_coching_app.stackPage(routerData.path);
      //   return;
    // }
      
      _vm.$router.push(routerData).catch(err => {
        // Ignore the vuex err regarding  navigating to the page they are already on.
        if (!(
          (err.name == 'NavigationDuplicated' &&
            err.message.includes('Avoided redundant navigation to current location'))
          || err.message.includes('Redirected when going from')
        )) {
          console.error(err);
        }
      });
    },

    //Replace Router
    ermReplacePage(routerData){
      const _vm = this;      
      // //console.debug("$this.$router");
      // //console.debug(_vm.$router);
      // if(_vm.isAppDevice && window.erns_coching_vue && window.erns_coching_vue.stackPage){
      //   window.erns_coching_vue.stackPage(routerData.path);
      //   return;
      // }
      
      _vm.$router.replace(routerData).catch(err => {
        // Ignore the vuex err regarding  navigating to the page they are already on.
        if (!(
          (err.name == 'NavigationDuplicated' &&
            err.message.includes('Avoided redundant navigation to current location'))
          || err.message.includes('Redirected when going from')
        )) {
          console.error(err);
        }
      });
    },    

    //App Interface 함수 유효성 검증
    hasAppFunction(functionName){
      const _vm = this;
      const appIF = _vm.getAppIF();
      if(_vm.isAppDevice 
        && appIF 
        && appIF[functionName]){
        return true;
      }
      return false;
    },

    //App Stack Page 처리[테스트 필요]
    ermStackPage(routerData, modalCloseCallback, message){
      const _vm = this;      
      //console.debug("$this.$router");
      //console.debug(_vm.$router);

      if(_vm.hasAppFunction('stackPage')){
        window.modalCloseCallback = modalCloseCallback;

        const params = routerData.query;
        const query = _vm.getQueryString(params);
        
        const url = `${routerData.path}${query}`;
        //console.debug(url);
        _vm.getAppIF().stackPage(url, message);
        return;
      }
      
      _vm.$router.push(routerData).catch(err => {
        // Ignore the vuex err regarding  navigating to the page they are already on.
        if (
          err.name !== 'NavigationDuplicated' &&
          !err.message.includes('Avoided redundant navigation to current location')
        ) {
          console.error(err);
        }
      });
    },

    hasHistory () { return window.history.length > 2 },

    ermHistoryBack(){
      const _vm = this; 

      if(_vm.hasAppFunction('historyBack')){      
        _vm.getAppIF().historyBack();
        return;
      }

      if(_vm.hasHistory()){
        _vm.$router.go(-1);
      }else{
        _vm.ermReplacePage({name:'coching-main'});
      }
    },

    //Push token 가져오기
    ermGetPushToken(){
      const _vm = this;

      if(_vm.hasAppFunction('getPushToken')){

        const pushToken =_vm.getAppIF().getPushToken();
        console.debug(`pushToken:${pushToken}`);
        return pushToken;
      }

      return "";
    },

    //Push token 전송
    async ermSendPushToken(){
      const _vm = this;

      if(!_vm.hasAppFunction('getPushToken')){
        return;
      }
      
      try{
        const pushTokenDataJson = localStorage.getItem(appConfig.storagePushTokenKey);
        let pushTokenInfo = _vm.$store.state.coching.pushTokenInfo;
        if(pushTokenDataJson){
          const sPushToken = JSON.parse(pushTokenDataJson);
          pushTokenInfo = {...sPushToken};        
        }

        const curPushToken = _vm.ermGetPushToken();
        pushTokenInfo.token = curPushToken;
        _vm.$store.dispatch('coching/setPushTokenInfo', pushTokenInfo);
        
        const userInfo = _vm.$store.state.coching.userInfo;
        pushTokenInfo = _vm.$store.state.coching.pushTokenInfo;
        console.debug('pushTokenInfo:');
        console.debug(pushTokenInfo);

        if(userInfo 
          && userInfo.userSeq && userInfo.userSeq > 0
          && pushTokenInfo
          && pushTokenInfo.token && pushTokenInfo.token != ""
          && pushTokenInfo.transToken != pushTokenInfo.token
        ){
          const sendToken = pushTokenInfo.token;
          console.debug("send Token : "+ sendToken);

          //send token
          const pushParam = {
            membSeq : userInfo.userSeq,
            appId : sendToken,
            deviceType : _vm.$store.state.coching.deviceTypeNative
          }
          const res = await setPushToken(pushParam);
          const {resultData, resultCode, resultFailMessage} = res;
          if (resultCode == "0000") {
            pushTokenInfo.transToken = sendToken;
            pushTokenInfo.transDate = (new Date()).getTime();

            _vm.$store.dispatch('coching/setPushTokenInfo', pushTokenInfo);
          }
        }else{
          console.debug("don't send Token : "+ pushTokenInfo.token);
        }
      }catch(err){
        console.debug(err);
      }
    },

    //네비게이션 Bar 설정
    ermSetNavigationBar(){
      const _vm = this;
      
      if(_vm.hasAppFunction('setNavigationBar')){
        const bnOpt = _vm.$route.meta.bottomNav;
        const showBottomNavFlag = bnOpt ? bnOpt.show : true;
        const bottomNaviId = to.meta.mainNavId || 1;
        const bnIdx = showBottomNavFlag ? bottomNaviId : "0";
        console.debug(bnOpt);
        console.debug(showBottomNavFlag);
        console.debug(bnIdx);

        vm.getAppIF().setNavigationBar(bnIdx);
      }
    },

    //COCHING 기본정보 로드
    async ermLoadUserBasicInfo(){
      const _vm = this;

      try{
        // 기업 정보
        const resPartnerInfo = await getPtnInfo({});
        if("0000" == resPartnerInfo.resultCode) {
          const {resultData} = resPartnerInfo;
          const reqPtn = resultData;

          _vm.$store.dispatch('coching/setPartnerInfo', reqPtn);
        }
        
      }catch(err){
        console.debug(err);
      }
    },

    //사용자 BASE URL 가져오기
    async ermLoadPartnerBaseUrl(){
      const _vm = this;

      try{
        // 기업 정보
        const resUrlInfo = await getPartnerBaseUrl();
        if("0000" == resUrlInfo.resultCode) {
          const {resultData} = resUrlInfo;
          const reqUrl = resultData;

          _vm.$store.dispatch('coching/setPartnerBaseUrlData', reqUrl);
        }
        
      }catch(err){
        console.debug(err);
      }
    },


    //App Version 가져오기
    ermGetAppVersionInfo(){
      const _vm = this;

      const defaultInfo = {
        applicationId : "com.nd.coching",
        versionName : "0.1",
        versionCode : 0
      }

      if(_vm.hasAppFunction('getVersionInfo')){

        const appVersionInfo =_vm.getAppIF().getVersionInfo();
        console.debug(`AppVersionInfo:${appVersionInfo}`);
        try{
          return JSON.parse(appVersionInfo);
        }catch(e){
          console.error(e);
        }
        return defaultInfo;
      }

      return defaultInfo;
    },

    //App 테마변경 이벤트 전송
    ermOnChangeTheme(){
      const _vm = this;

      if(_vm.hasAppFunction('onChangeTheme')){
        _vm.getAppIF().onChangeTheme();
      }
    },

    //클립보드 복사
    async edmSetClipboard(data, message){
      const _vm = this;

      try{
        await _vm.$copyText(data);

      }catch(e){
        console.log(e);
        return;
      }

      if(_vm.hasAppFunction('showToast')){
        var msg = message || '클립보드에 복사 되었습니다.';
        _vm.getAppIF().showToast(msg);
        return;
      }
    },

    //전화 다이얼
    async edmCallDial(tel){
      const _vm = this;

      if(_vm.hasAppFunction('callDial')){
        _vm.getAppIF().callDial(tel);
        return;
      }
    },

    //애플로그인
    edmAppleLogin(){
      const _vm = this;

      if(_vm.hasAppFunction('appleLogin')){
        _vm.getAppIF().appleLogin();
        return true;
      }

      return false;
    },

    //App용 javascript interface 객체 가져오기
    getAppIF(){
      return window.erns_coching_app;
    },

    //IOS javascript interface 등록
    edmRegisterIsoJavascriptInterface(){
      const _vm = this;
      
      window.erns_coching_app = window.erns_coching_app ||{};

      window.erns_coching_app.openBrowser = function(link){
        console.debug(link);
        window.webkit.messageHandlers.openBrowser.postMessage(link);
      };

      window.erns_coching_app.showToast = function(message){
        console.debug(message);
        window.webkit.messageHandlers.showToast.postMessage(message);
      };      
      
      window.erns_coching_app.getPushToken = function(){
        //const ret = window.webkit.messageHandlers.getPushToken.postMessage();
        const iosPushToken = _vm.$cookie.get("coching.pc.device.pushToken") || "";
        console.debug(`getPushToken:${iosPushToken}`);
        return iosPushToken;
      };

      window.erns_coching_app.appleLogin = function(){
          console.info("appleLogin");
          window.webkit.messageHandlers.appleLogin.postMessage("test");
      };

      window.erns_coching_app.getVersionInfo = function(){
        //window.webkit.messageHandlers.versionInfo.postMessage("");
        const iosVersionInfo = _vm.$cookie.get("coching.pc.device.version") || "{}";
        console.debug(`getVersionInfo:${iosVersionInfo}`);
        return iosVersionInfo;
      }

      window.erns_coching_app.setNavigationBar = function(id){
        console.info(`setNavigationBar:${id}`);
        window.webkit.messageHandlers.setNavigationBar.postMessage(id);
      };

      window.erns_coching_app.setDragRefresh = function(flag){
        console.info(`setDragRefresh:${flag}`);
        window.webkit.messageHandlers.setDragRefresh.postMessage(flag);
      };

      window.erns_coching_app.onChangeTheme = function(){
        console.info(`onChangeTheme:""`);
        window.webkit.messageHandlers.onChangeTheme.postMessage("");
      };

      //window.erns_coching_app.historyBack = function(message){
      //   window.webkit.messageHandlers.back.postMessage(true);
      // };      
    }
  }
}
