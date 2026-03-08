<template>
  <div>
    <!--total-wrap-->
    <div class="total-wrap">
      <div class="total-num">총<span>{{boardData.pi.totalItem}}</span></div>
      <!--right-->
      <div class="right">
        <!--search-->
        <div v-if="true" class="input-set m-none">
          <div class="input-ic-set">
            <input type="text" placeholder="검색" v-model="boardData.sc.titleL" @keyup.enter="onSearch"/>
            <button type="button" class="input-ic ic-md ic-search-md" @click="onSearch"></button>
          </div>
        </div>
        <!--button-->
        <button 
          type="button" class="btn btn-sm btn-primary" @click="onClickWrite">1:1 문의하기</button>
      </div>
    </div>

    <!--board-list-->
    <div class="board-list">
      <a href="javascript:;"
       v-for="(item, idx) of boardData.list"
       :key="item.boardSeq"
       @click="onClickItem(item)">
        <div class="content-wrap">
          <div class="title-wrap">
            <div class="title">{{item.title}}</div>
          </div>
          <div class="date">
            <span>{{item.regName}}</span>
            <span>{{item.rgtDttm | fmtBoardDate}}</span>
          </div>
        </div>
        <!--state-badge-->
        <div v-if="item.isReply == 'Y'" class="state-badge blue">답변완료</div>
        <div v-else class="state-badge gray">답변대기</div>
      </a>
    </div>
    
    <div v-show="boardData.list.length <= 0"
      class="result-none empty-content">
      등록된 게시물이 없습니다.
    </div>

    <!--pagenation-->
    <Pagenation
      v-model="boardData.pi.curPage"
      :totalRows="boardData.pi.totalItem"
      :perPage="boardData.pi.perPage"
      @input="onChangePage"
    ></Pagenation>
    
    <!--오류-->
    <AlertModal ref="alertModal"></AlertModal>
    <ConfirmModal ref="confirmModal"></ConfirmModal>
  </div>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import boardMixin from './boardMixin';
import moment from 'moment';

import Pagenation from '@/components/Pagenation.vue';

import { DEF_BOARD } from '@/constants/board';
import { getCodes } from '@/api/coching/comm/code';
import { getInprBoardList } from '@/api/coching/comm/board';

export default {
  name: 'coching-inqr-board-list',
  mixins: [ernsUtils, boardMixin],
  components: {
    Pagenation,
  },
  computed: {},
  watch: {
    async boardMstId(newVal, oldVal) {
      const _vm = this;
      _vm.boardData.sc = { ...DEF_BOARD.sc }; //검색조건 초기화
      await _vm.loadCodes();
      await _vm.fetchData();
    },
  },
  props:{
    boardMstId: {
      type: String,
      default: 'BD_INQR_001'
    }
  },
  async mounted(){
    const _vm = this;
    _vm.getInitParam();
    await _vm.loadCodes();
    await _vm.fetchData();  
  },
  data(){
    return {
      CODES: {
				SC_001 : [],
			},

			boardData : {
        list: [],
        pi: {...DEF_BOARD.pi},
        sc: {...DEF_BOARD.sc},
      },
    }
  },
  methods : {
    //$route.query, $route.params, HistoryParam 등으로 초기값 설정
    getInitParam() {
      const _vm = this;

      //상세화면에서 온 경우만 복구
      const fromName = _vm.$store.state.coching.logRouteChange?.from?.name;
      const expectedViewName = _vm.$route.name.replace(/-list$/, '-view');
      if (fromName !== expectedViewName) return;

      //const query = _vm.$route.query, params = _vm.$route.params;
      const data = _vm.eumGetRouteHistoryParam();
      //console.info("onAfterRouter");console.info(data);
      if(data && data.initParam){
        _vm.boardData.sc = {...data.initParam.sc}; 
        _vm.boardData.pi = {...data.initParam.pi};
      }
    },

    setInitParam() {
      const _vm = this;
      try{
        const data = {
          initParam : {
            sc : _vm.boardData.sc,
            pi : _vm.boardData.pi
          }
        };
        //console.info("beforeRouteLeave");
        //console.info(data);
        _vm.eumSetRouteHistoryParam(data);
      }catch(err){
        console.error(err);
      }
    },

    // 검색 
		onSearch() {
      const _vm = this;
      _vm.loadList(1);
    },

    //게시글 선택 
    async onClickItem(item) {
      const _vm = this;
      await _vm.setInitParam();
      _vm.goView(item);
    },

    //게시글 등록
    async onClickWrite(){
      const _vm = this;

      var auth = _vm.eumLoginUser && _vm.$can('write', _vm.boardMstId);
      
      if(!auth){
        await _vm.onClickConfirmLogin(); //로그인 확인
        return;
      }

      _vm.goWrite();
    },

    // 페이지 변경 
		onChangePage(pageNo) {
      const _vm = this;
      _vm.loadList(pageNo);
    },    

    async fetchData(){      
      const _vm = this;
      _vm.loading(true);

      try{
        //TestCode
        //_vm.$ability.update(defineAbilitiesForUserAuth([]));

        await _vm.loadList(_vm.boardData.pi.curPage || 1);

      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },

    /* 게시글 목록 가져오기 */
    async loadList(pageNo) {
			const _vm = this;

      const dataMap = _vm.boardData, pInfo = _vm.boardData.pi, searchOp = _vm.boardData.sc;
      pInfo.curPage = pageNo || 1;

      if(!_vm.eumLoginUser) {
        dataMap.list = [];
        return;
      }

			_vm.loading(true);
      try {
        const params = _vm.getListSearchParam();
        const res = await getInprBoardList(params);
        const resData = res.resultData;
        
      const pageInfo = resData.pageInfo;
        dataMap.pi = {
          ...dataMap.pi,
          curPage : pageInfo.currentPage,
          totalItem : pageInfo.totalItem,
          perPage : pageInfo.pageItemSize
        }

        //데이터 바인딩
        dataMap.list = resData.list;
        //dataMap.backupList = JSON.parse(JSON.stringify(resData.list));
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
		},

    /* 검색 조건 가져오기 */
		getListSearchParam() {
      const _vm = this;
      const pInfo = _vm.boardData.pi, searchOp = _vm.boardData.sc;

			const retParam = {
        boardMstId : _vm.boardMstId,
        delYn : searchOp.delYn,
        titleL : searchOp.titleL,

        page : pInfo.curPage,
        rowSize : pInfo.perPage                
        // rowSize : 2                
      };
			
			return retParam;
    },

    //코드 로드
	  async loadCodes(){
		  const _vm = this;
		  _vm.CODES.SC_001 = [
        {value:"", label : _vm.$t('') || '전체'},
        {value:"001", label : _vm.$t('') || '원료명'},
        {value:"002", label : _vm.$t('') || '담당자'}
      ];
    },

		docReady() {
			const _vm = this;
		},
  }
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">
</style>
