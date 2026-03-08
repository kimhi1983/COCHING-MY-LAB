import appConfig from '@/config';
import moment from 'moment';

import { BOARD_ID, BOARD_MODE } from '@/constants/board';

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
    fmtBoardDate(val) {
      if (!val) return '';

      const date = moment(val);
      if (!date.isSame(moment(), 'date')) {
        return date.format('YY.MM.DD');
      }

      return date.format('YY.MM.DD HH:mm');
    },

    /* 날짜형식 포매팅 */
    fmtBoardDateDtail(val) {
      if (!val) return '';

      const date = moment(val);
      return date.format('YY.MM.DD HH:mm');
    },
  },  
  methods: {
    getBoardId_rawSourcing() {
      const _vm = this;
      const locale = _vm.$i18n.locale;

      switch(locale){
        case 'en': return BOARD_ID.RAW_SOURCING_EN;
        default:
          return BOARD_ID.RAW_SOURCING_KO;
      }
    },

    getBoardId_notice() {
      const _vm = this;
      const locale = _vm.$i18n.locale;

      switch(locale){
        case 'en': return BOARD_ID.NOTICE_EN;
        default:
          return BOARD_ID.NOTICE_KO; 
      }
    },

    getBoardId_faq() {
      const _vm = this;
      const locale = _vm.$i18n.locale;

      switch(locale){
        case 'en': return BOARD_ID.FAQ_EN;
        default:
          return BOARD_ID.FAQ_KO;
      }
    },

    getBoardId_weeklyNews() {
      const _vm = this;
      const locale = _vm.$i18n.locale;

      switch(locale){
        case 'en': return BOARD_ID.WEEKLYNEWS_EN;
        default:
          return BOARD_ID.WEEKLYNEWS_KO;
      }
    },

    //모드에 따른 라우터 정보 가져오기
    getRouteName(currentRouteName, mode) {
      return currentRouteName.replace(/-board-(list|view|write|edit)$/, `-board-${mode}`);
    },

    //목록이동
    goList(page, isReplace){
      const _vm = this;

      const listRouteName = _vm.getRouteName(_vm.$route.name, BOARD_MODE.LIST);
      const routeParam = {
        //page: page || 1,
      };
      if(isReplace){
        _vm.ermReplacePage({
          name:listRouteName,
          params: routeParam,
        });
      }else{
        _vm.ermPushPage({
          name:listRouteName,
          params: routeParam,
          //query:routeParam,
        });
      }
    },

    //상세 페이지 이동
    goView(item){
      const _vm = this;

      const viewRouteName = _vm.getRouteName(_vm.$route.name, BOARD_MODE.VIEW);
      const boardSeq = item instanceof Object 
        ? item.boardSeq 
        : item;

      const routeParam = {
        boardSeq: boardSeq,
      };

      console.debug(viewRouteName);
      console.debug(routeParam);

      _vm.ermPushPage({
        name:viewRouteName, 
        //params:routeParam,
        query:routeParam,
      });
    },

    /* 글쓰기 */
    goWrite() {
      const _vm = this;

      const writeRouteName = _vm.getRouteName(_vm.$route.name, BOARD_MODE.WRITE);
      const routeParam = {        
        boardSeq: 0,
      };
      _vm.ermPushPage({
        name: writeRouteName, 
        //params:routeParam,
        query:routeParam,
      });
    },

    //수정 페이지 이동
    goEdit(item){
      const _vm = this;

      const viewRouteName = _vm.getRouteName(_vm.$route.name, BOARD_MODE.EDIT);
      const boardSeq = item instanceof Object 
        ? item.boardSeq 
        : item;

      const routeParam = {
        boardSeq: boardSeq,
      };

      // console.debug(viewRouteName);
      // console.debug(routeParam);

      _vm.ermPushPage({
        name:viewRouteName, 
        //params:routeParam,
        query:routeParam,
      });
    },
  }
}