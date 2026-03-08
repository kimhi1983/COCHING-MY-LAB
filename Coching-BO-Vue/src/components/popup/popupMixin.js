import appConfig from '@/config';
import moment from 'moment';

import { POPUP_ID, POPUP_MODE } from '@/constants/popup';

const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default {
  data() {
    return {

    }
  },
  computed: {

  },
  filters: {
    /* 날짜형식 포매팅 */
    fmtPopupDate(val) {
      if (!val) return '';

      const date = moment(val);
      if (date.isSame(moment(), 'date')) {
        return date.format('HH:mm');
      }

      return date.format('YY.MM.DD HH:mm');
    },

    /* 날짜형식 포매팅 */
    fmtPopupDateDtail(val) {
      if (!val) return '';

      const date = moment(val);
      return date.format('YY.MM.DD HH:mm');
    },
  },  
  methods: {  
    //모드에 따른 라우터 정보 가져오기
    getRouteName(currentRouteName, mode) {
      return currentRouteName.replace(/-popup-(list|view|write|edit)$/, `-popup-${mode}`);
    },

    //목록이동
    goList(page, isReplace){
      const _vm = this;

      const listRouteName = _vm.getRouteName(_vm.$route.name, POPUP_MODE.LIST);
      const routeParam = {
        //page: page || 1,
      };
      if(isReplace){
        _vm.$router.replace({
          name:listRouteName,
          params: routeParam,
        });
      }else{
        _vm.$router.push({
          name:listRouteName,
          params: routeParam,
          //query:routeParam,
        });
      }
    },

    //상세 페이지 이동
    goView(item){
      const _vm = this;

      const viewRouteName = _vm.getRouteName(_vm.$route.name, POPUP_MODE.VIEW);
      const popupSeq = item instanceof Object 
        ? item.popupSeq 
        : item;

      const routeParam = {
        popupSeq: popupSeq,
      };

      // console.debug(viewRouteName);
      // console.debug(routeParam);

      _vm.$router.push({
        name:viewRouteName, 
        //params:routeParam,
        query:routeParam,
      });
    },

    /* 등록록 */
    goWrite() {
      const _vm = this;

      const writeRouteName = _vm.getRouteName(_vm.$route.name, POPUP_MODE.WRITE);
      const routeParam = {        
        popupSeq: 0,
      };
      _vm.$router.push({
        name: writeRouteName, 
        //params:routeParam,
        query:routeParam,
      });
    },

    //수정 페이지 이동
    goEdit(item, isReplace){
      const _vm = this;

      const viewRouteName = _vm.getRouteName(_vm.$route.name, POPUP_MODE.EDIT);
      const popupSeq = item instanceof Object 
        ? item.popupSeq 
        : item;

      const routeParam = {
        popupSeq: popupSeq,
      };

      // console.debug(viewRouteName);
      // console.debug(routeParam);
      if(isReplace){
        _vm.$router.replace({
          name:viewRouteName, 
          //params:routeParam,
          query:routeParam,
        });
        return;  
      }

      _vm.$router.push({
        name:viewRouteName, 
        //params:routeParam,
        query:routeParam,
      });
    },
  }
}