import device from './device';

import datetimeFilter from './datetimeFilter';
import numberFilter from './numberFilter';
import pagination from './pagination';
import formatter from './formatter';
import loading from './loading';
import routerHistory from './routerHistory';
import ernsLoadImage from './ernsLoadImage';
import staticCode from './staticCode';
import cochingUtils from './cochingUtils';
import notiRouter from './notiRouter';

import {getHomeRouteForLoggedInUser } from '@/auth/utils';
import useJwt from '@/auth/jwt/useJwt';
import appConfig from '@/config';
import {getAllimMessageCount} from '@/api/coching/member/message';

const base = process.env.VUE_APP_API_BASE_URL;

export default {
  mixins: [
    device,
    routerHistory, 
    loading, 
    datetimeFilter,
    numberFilter, 
    pagination, 
    formatter, 
    ernsLoadImage,

    staticCode,
    cochingUtils,
    notiRouter,
  ],
  data() {
    return {
      
    }
  },
  computed: {
    eumLocale(){
      const locale = this.$store.state.coching.locale;
      return locale ? locale : 'ko';
    },
    eumLoginUserName(){
      const userInfo = this.$store.state.coching.userInfo
      return userInfo ? userInfo.userName : '-';
    },
    eumLoginUser(){
      return this.$store.state.coching.userInfo;
    },
    eumRefreshToken(){
      return useJwt.getRefreshToken();
    },
    eumRouterBasePath(){
      return process.env.VUE_APP_BASE_ROUTER_PATH;
    },
    eumPartnerBaseUrlData(){
      return this.$store.state.coching.partnerBaseUrlData;
    },
    eumConstAdress() {
      return process.env.VUE_APP_CONST_ADDRESS;
    },
    eumConstCustomerCenter() {
      return process.env.VUE_APP_CONST_CUSTOMER_CENTER;
    },
    eumConstBusinessNum() {
      return process.env.VUE_APP_CONST_BUSINESS_NUM;
    },
    chosen_browser_is_supported() {
      if ("Microsoft Internet Explorer" === window.navigator.appName) {
        return document.documentMode >= 8;
      }

      console.debug(`window.navigator.userAgent:${window.navigator.userAgent}`);

      if (/iP(od|hone)/i.test(window.navigator.userAgent)) {
        return false;
      }

      if (/Android.*Mobile/i.test(window.navigator.userAgent)) {
        return false;
      }
      
      if (/iP(od|hone)/i.test(window.navigator.userAgent) || /IEMobile/i.test(window.navigator.userAgent) || /Windows Phone/i.test(window.navigator.userAgent) || /BlackBerry/i.test(window.navigator.userAgent) || /BB10/i.test(window.navigator.userAgent) || /Android.*Mobile/i.test(window.navigator.userAgent)) {
        return false;
      }
      return true;
    },
    isSmallGnb() {
      const _vm = this;
      return _vm.$store.state.coching.isSmallGnb;
    }
  },  
  beforeRouteLeave : function(to, from, next){
    //페이지 이동전 검색 param 저장용
    try{
      const routerName = from.name;
      if(from.matched.length > 0){
        const coms = from.matched[0].components.default;
        const methods = coms.methods;
        if(methods && methods.hasOwnProperty('onBeforeRouter')){
          const fncOnBeforeRouter = methods['onBeforeRouter'];
          fncOnBeforeRouter(routerName);
        }
      }
    }catch(err){
      console.error(err);
    }
  
    return next();
  },
  filters :{
    //사용여부 필터
    useYnName(val){
      return val =="Y" ? "사용" : val =="N" ? "미사용" : "-";
    },

    //공통코드에서 코드에 해당하는 코드명 출력
    eufGetCodeName(val, codeList){
      let ret = "-";

      if(!codeList || codeList.length < 1 || !val ){return ret;}

      const cd = codeList.find((item)=>{
        return item.value == val;
      });

      if(cd){
        return cd.label;
      }

      return "-";
    },
  },
  methods: {
    isDarkMode(){
      return this.$store.state.coching.isDarkMode;
    },

    //사용자 정보 삭제
    async eumClearLoginInfo(){
      const _vm = this;
      // Remove Token
      localStorage.removeItem(useJwt.jwtConfig.storageTokenKeyName)
      localStorage.removeItem(useJwt.jwtConfig.storageRefreshTokenKeyName)

      // Remove userData from localStorage
      localStorage.removeItem(useJwt.jwtConfig.storageUserDataKeyName)

      _vm.$store.dispatch('coching/setUserInfo', null);
      _vm.$store.dispatch('coching/setJoinData', null);
      _vm.$store.dispatch('coching/setPartnerInfo', null); //기업정보
      _vm.$store.dispatch('coching/setPartnerBaseUrlData', null); //원료사 URL정보
    },

    
    //Logout
    async eumLogout(){
      const _vm = this;
      
      //Client 로그아웃 처리
      await useJwt.logout();
      _vm.$store.dispatch('coching/setUserInfo', null);  //회원정보
      _vm.$store.dispatch('coching/setJoinData', null); //회원가입정보      
      _vm.$store.dispatch('coching/setPartnerInfo', null); //기업정보
      _vm.$store.dispatch('coching/setPartnerBaseUrlData', null); //원료사 URL정보
      
      //console.debug(_vm.$router);
      const router = _vm.$router;
      router.go(0);
    },

    //Device Type 설정
    eumSetDeviceType(deviceType){
      const _vm = this;
      const dt = deviceType || "WEB";

      _vm.$store.dispatch('coching/changeOption', {
        key : "deviceType", value : dt.toUpperCase()
      }); 
    },

    //Device Type 설정
    eumSetDeviceTypeNative(deviceTypeNative){
      const _vm = this;
      const dt = deviceTypeNative || "android";

      _vm.$store.dispatch('coching/changeOption', {
        key : "deviceTypeNative", value : dt.toUpperCase()
      }); 
    },

    //공통코드 목록 => [{label:'text', value:'data'}] 로 변경
    eumConvertToVueSelectOption(rawCommonCodes){
      return rawCommonCodes.map(item=>{
        return {label:item.codeName, value:item.code};
      });
    },
    
    //Vue-Select 등에서 선택된 값의 value 값을 String 으로 리턴
    eumGetVSelectValue(selectModel, propName) {
      const pName = propName || 'value';
      if (!selectModel) { return null; }

      if (typeof selectModel === 'object') {
        if (selectModel.hasOwnProperty(pName)) {
          return selectModel[pName];
        }
      }
      return null;
    },

    //공통코드 List에서 코드를 찾아 Object리턴
    eumGetCodeObject(codeVal, codes){
      if(codeVal && codes && codes.length > 0){
        return codes.find(item=>{
          return item.value == codeVal;
        });
      }

      return null;
    },

    //난수 키를 생성
    eumGenerateKey(pPrefix, pMin, pMax){
      const prefix = (pPrefix || "").trim();
      const max = pMax ? pMax : 9999999999;
      const min = pMin ? pMin : 1;
      const rVal = Math.floor(Math.random() * (max - min))+ min;
    
      if(prefix == ""){
        return rVal;
      }
    
      return `${prefix}_${rVal}`;
    },

    //오류 alert
    async alertError(errorMsg, title, timer){
      const _vm = this;

      let eMsg = '알 수 없는 오류가 발생하였습니다.';
      if(errorMsg instanceof Error){
        const eRes = errorMsg.response;
        if(eRes && eRes.data && eRes.data.returnMessage){
          eMsg = eRes.data.returnMessage;
        }else{
          eMsg = errorMsg.message;
        } 
      }else{
        eMsg = errorMsg || eMsg;
      }
      
      return await _vm.$swal({
        icon: 'error',
        title : title,
        text: eMsg,         
        timer: timer,
        customClass: {
          confirmButton: 'btn btn-primary btn-lg',
        },
        buttonsStyling: false,
        confirmButtonText: "확인"
      });
    },

    async alertErrorHtml(errorMsg, title, timer){
      const _vm = this;
 
      let eMsg = '알 수 없는 오류가 발생하였습니다.';
      if(errorMsg instanceof Error){
        const eRes = errorMsg.response;
        if(eRes && eRes.data && eRes.data.returnMessage){
          eMsg = eRes.data.returnMessage;
        }else{
          eMsg = errorMsg.message;
        } 
      }else{
        eMsg = errorMsg || eMsg;
      }

      return await _vm.$swal({
        icon: 'error',
        title : title,
        html: eMsg,         
        timer: timer,
        customClass: {
          confirmButton: 'btn btn-primary btn-lg',
        },
        buttonsStyling: false,
        confirmButtonText: "확인"
      });
    },

    //성공 alert (자동 꺼짐)
    async alertSuccess(message, title, timer){
      const _vm = this;
      return await _vm.$swal({
        icon: 'success',
        title: title,
        text: message, 
        showConfirmButton: false,
        timer: timer || 1500,
        customClass: {
          confirmButton: 'btn btn-primary',
        },
        buttonsStyling: false,
      });
    },

    eumCamel2under(str){
      if(!str){
        return str;
      }

      return str.replace(/([A-Z])/g, function(arg){
        return "_"+arg.toLowerCase();
      }).toUpperCase();
    },

    eumUnder2camel(str){
      if(!str){
        return str;
      }
      
      return str.toLowerCase().replace(/(\_[a-z])/g, function(arg){
        return arg.toUpperCase().replace('_','');
      });
    },

    //파일 다운로드
    eumFileDownPath(fileId, noCache, isPdf){
      const isNoCache = noCache ? "1" : "0";
      const pdfFlag = isPdf ? "1" : "0";
      const encodedFileId = encodeURIComponent(fileId); // fileId를 URI 인코딩
      let link = `${base}/file/download.do?fId=${encodedFileId}&nc=${isNoCache}`;
      if(isPdf){
        link += "&pdf=1";
      }
      return link;
    },

    /**
     * 첨부파일 다운로드
     */
    eumFileDownloadResponse(res, fileName){
      const _vm = this;

      try{
        const lUrl = window.URL.createObjectURL(new Blob([res.data]));
        //console.debug(res);
        const link = document.createElement('a');
        const contentDisposition = res.headers['content-disposition']; // 파일 이름
        let tFileName = 'excelData.xls' || fileName;
        if (contentDisposition) {
          const [ fileNameMatch ] = contentDisposition.split(';').filter(str => str.includes('filename'));
          if (fileNameMatch){
            [ , tFileName ] = fileNameMatch.split('=');
            tFileName = tFileName.replace(/"/gi, '');
          }

          if(fileName){
            tFileName = fileName;
          }
        }else{
          //console.debug(fileName);
          tFileName = fileName || 'excelData.xls';
        }
        
        link.href = lUrl;
        link.setAttribute('download', `${tFileName}`);
        link.style.cssText = 'display:none';
        document.body.appendChild(link);
        link.click();
        link.remove();

      }catch(err){
        console.error(err);
        const { config, response } = err;
        //console.debug(config);
        //console.debug(response);
        if(response && response.status === 404){
          console.error('[DEV] 파일을 찾을 수 없습니다. (팝업 숨김)');
        }else{
          console.error('[DEV] 알 수 없는 오류 (팝업 숨김):', response?.status);
        }
        
      }
    },

    //첨부파일 이미지 로드
    eumFileImagePath(fileId, pThumb, replaceImage, noCache){
      if(!fileId && replaceImage){
        return replaceImage;
      }

      const nc = noCache || "1";      
      const thumb = pThumb || "1";
      const encodedFileId = encodeURIComponent(fileId); // fileId를 URI 인코딩
      let link = `${base}/file/image.do?thumb=${thumb}&fId=${encodedFileId}`;
      if(nc != "1"){
        link += "&nc="+nc;
      }
      return link;
    },

    //첨부파일 이미지 로드
    eumFileImagePathWithSize(fileId, pThumb, replaceImage, size){
      if(!fileId && replaceImage){
        return replaceImage;
      }
      
      const thumb = pThumb || "1";
      const encodedFileId = encodeURIComponent(fileId); // fileId를 URI 인코딩
      let link = `${base}/file/image.do?thumb=${thumb}&fId=${encodedFileId}`;
      if(thumb == "1" && size){
        link += "&size="+size;
      }
      return link;
    },

    //첨부파일 이미지 로드
    eumFileEImagePath(eImagePath, replaceImage){
      if(!eImagePath && replaceImage){
        return replaceImage;
      }
      
      const encodedPath = encodeURIComponent(eImagePath); // eImagePath를 URI 인코딩
      const link = `${base}/file/eImage.do?enp=${encodedPath}`;
      return link;
    },

    //서버 정적 리소스
    eumRefPath(refPath){      
      const link = `${base}${refPath}`;
      return link;
    },

    /** 알림 미확인 수 뱃지 표시 */
    async setNotiUnCheckBadge(){ 
      const _vm = this;
      const params = {
        readYn : "N"
      };
      let resCount = 0;

      const userInfo = _vm.$store.state.coching.userInfo;
      if(userInfo && userInfo.userSeq > 0){
        const res = await getAllimMessageCount(params);
        resCount = res.objReturnData;            

        if(!resCount) {
          resCount = 0;
        }
      }
      
      _vm.$store.dispatch('coching/setNoCheckNotiCount', resCount);            
    },

    /** 전화번호 존재유무/최소자릿수 확인 */
    eumTelValidation(tel) {
      const _vm = this;
      if(!tel) {
        return false;
      }
      const telLength = tel.replace(/-/g, '').length;
      
      if(telLength >= 9) {
        return true;
      }
      return false;
    },

    eumLocationUrl(refUrl) {
      const _vm = this;

      if(!refUrl) {
        return refUrl;
      }
      
      // 외부
      if(refUrl.indexOf("http:") >= 0 || refUrl.indexOf("https:") >= 0){
        //window.open(refUrl, '_blank');
        _vm.ermOpenWindow(refUrl);
        return;
      }

      // 내부
      const routerData = _vm.getUrlRouterData(refUrl);
      _vm.ermPushPage(routerData);
    },

    getUrlRouterData(search) {
      const hashes = search.slice(search.indexOf('?') + 1).split('&')
      const params = {}
      hashes.map(hash => {
          const [key, val] = hash.split('=')
          params[key] = decodeURIComponent(val)
      })
      const path = search.split('?')[0];
      
      const routerData = {
        path: path,
        query: params
      };

      return routerData;
    },

    getQueryString(paramMap){
      const esc = encodeURIComponent;
      const params = paramMap;
      let query = "";
      if(params){
        query = "?"+Object.keys(params).map(k => esc(k) + '=' + esc(params[k])).join('&');        
      }

      return query;
    },

    //팝업: 오늘 다시 보지 않기 여부
    getPopupTodayDoNotShow(popupMstCd){
      const _vm = this;
      return _vm.$cookie.get(`coching.popup.${popupMstCd}`) || "0";
    },

    //팝업: 오늘 다시 보지 않기 설정
    setPopupTodayDoNotShow(popupMstCd){
      const _vm = this;

      _vm.$cookie.set(`coching.popup.${popupMstCd}`, "1", 60 * 60 * 24);
    },


    //기본 API 오류처리
    async defaultApiErrorHandler(err){
      const _vm = this;
      let errorMsg = '알수 없는 오류가 발생했습니다.';
      if(!err.response){
        console.error('[DEV] API 오류 (팝업 숨김):', err);
        return true;
      }

      const { resultCode, resultFailMessage, resultMsg } = err.response.data;
      errorMsg = resultFailMessage || resultMsg || '통신이 원할하지 않습니다.\n잠시후 다시 시도해 주십시오.';

      if(resultCode != "0000"){
        console.error('[DEV] API 오류 (팝업 숨김):', errorMsg);
        return true;
      }

      return false;
    },

    //기본 API 오류처리
    async defaultApiErrorHandlerSyncAlert(err){
      const _vm = this;
      let errorMsg = '알수 없는 오류가 발생했습니다.';
      if(!err.response){
        console.error('[DEV] API 오류 (팝업 숨김):', err);
        return true;
      }

      const { resultCode, resultFailMessage, resultMsg } = err.response.data;
      errorMsg = resultFailMessage || resultMsg || '통신이 원할하지 않습니다.\n잠시후 다시 시도해 주십시오.';

      if(resultCode != "0000"){
        console.error('[DEV] API 오류 (팝업 숨김):', errorMsg);
        return true;
      }

      return false;
    },

    closeModal(){
      const _vm = this;

      _vm.$refs["fullModal"].close();
    },

    //snake_case → camelCase
    toCamelCase(str) {
      return str.replace(/_([a-z])/g, (_, letter) => letter.toUpperCase()) // 알파벳을 변환
            .replace(/_([0-9])/g, '$1'); // 숫자가 오면 _ 제거
    },

    //Object 의 키를 snake_case → camelCase
    convertKeysToCamelCase(obj) {
      const _vm = this;

      if (Array.isArray(obj)) {
        return obj.map(_vm.convertKeysToCamelCase);
      } else if (obj !== null && typeof obj === 'object') {
        return Object.keys(obj).reduce((acc, key) => {
          const camelKey = _vm.toCamelCase(key);
          acc[camelKey] = _vm.convertKeysToCamelCase(obj[key]);
          return acc;
        }, {});
      }
      return obj; // 원시값은 그대로 반환
    },

    //로그인 confirm
    async onClickConfirmLogin(){
      const _vm = this;

      const ret = await _vm.$refs["confirmModal"].open({
        title: _vm.$tt('mypage.main.label.001') || '로그인이 필요한 서비스 입니다\n로그인 하시겠습니까?',
        content : ''
      });

      if(ret){
        _vm.ermPushPage({
          name:'coching-login',
          query: { retRoute: _vm.$route.fullPath }
        });
        return true;
      }

      return false;
    },

    //홈이동
    onClickHome(){
      const _vm = this;
      _vm.ermReplacePage({name:'coching-main'});
    },
    //코칭TV
    onClickTv(){
      const _vm = this;      
      _vm.goSearchKeyword(``, '4002', null);
    },
    //해외 원료사
    onClickForeignPtn(){
      const _vm = this;
      _vm.ermPushPage({name:'coching-partner-foreign-list'});
    },
    //알림함
    onClickNoti(){
      const _vm = this;
      _vm.ermPushPage({name:'coching-mypage-notification'});
    },
    //원료요청
    onClickRequest(){
      const _vm = this;
      _vm.ermPushPage({name:'coching-mypage-requestRaw'});
    },
    //쪽지함
    onClickMessage(){
      const _vm = this;
      _vm.ermPushPage({name:'coching-mypage-message'});
    },
    //원료소싱
    onClickSourcing(){
      const _vm = this;
      _vm.ermPushPage({name:'coching-rawSourcing-board-list'});
    },
    //공지사항
    onClickNotice(){
      const _vm = this;
      _vm.ermPushPage({name:'coching-notice-board-list'});
    },
    //사용자 메뉴얼
    onClickUserManual(){
      const _vm = this;
      _vm.ermPushPage({name:'coching-user-manual-new'});
    },
    //1:1 문의하기
    onClickInquiry(){
      const _vm = this;
      _vm.ermPushPage({name:'coching-inqr-board-list'});
    },
    //Weekly News
    onClickWeeklyNews(){
      const _vm = this;
      _vm.ermPushPage({name:'coching-weeklyNews-board-list'});
    },
  }
}