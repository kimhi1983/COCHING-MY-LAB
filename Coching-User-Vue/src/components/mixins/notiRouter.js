import { BOARD_ID, BOARD_MODE } from '@/constants/board';
export default {
  data() {
    return {
      
    }
  },
  computed : {    
  },
  methods: {
    eumGoNotiRouter(item) {
      const _vm = this;
        
      if(item.refCode.includes('REQ') ){
        _vm.ermPushPage({name:'coching-mypage-requestRaw-detail', query:{rawRequestSeq: item.refSeq}});
      }else if(item.refCode.includes('MSG') ){
        _vm.ermPushPage({name:'coching-mypage-message', query:{messageSeq: item.refSeq}});
      }else if(item.refCode.includes('INQR') ){
        _vm.ermPushPage({name:'coching-inqr-board-view', query:{boardSeq: item.refSeq}});
      }else if(item.refCode.includes('SRC') ){
        _vm.goView(item);
      }
    },
    //상세 페이지 이동
    goView(item){
      const _vm = this;

      const boardSeq = item instanceof Object 
        ? item.refSeq
        : 0;

      const viewRouteName = _vm.getRouteName('coching-rawSourcing-board-view', boardSeq != 0 ? BOARD_MODE.VIEW : BOARD_MODE.LIST);  

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
     //모드에 따른 라우터 정보 가져오기
     getRouteName(currentRouteName, mode) {
      return currentRouteName.replace(/-board-(list|view|write|edit)$/, `-board-${mode}`);
    }
  }
}
