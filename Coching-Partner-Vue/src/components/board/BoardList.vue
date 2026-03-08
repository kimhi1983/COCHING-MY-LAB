<template>  
  <!--content-->
  <div class="content-inner">

    <!--total-wrap-->
    <div class="total-wrap">
      <div class="total-num">총<span>{{boardData.pi.totalItem}}</span></div>
      <div class="right">
        <!--select-->
        <!-- <CochingSelect v-model="faqData.sc.searchField"
        name="sources"
        :placeholder="$t('') || '전체'"
        :options="CODES.SC_001"
        :label="'label'"
        :trackBy="'etc1'"
        >
        </CochingSelect> -->
        <!--search-->
        <div v-if="true" class="input-set m-none">
          <div class="input-ic-set">
            <input type="text" placeholder="검색" v-model="boardData.sc.titleL" @keyup.enter="onSearch"/>
            <button type="button" class="input-ic ic-md ic-search-md" @click="onSearch"></button>
          </div>
        </div>
        <!--button-->
        <button v-if="eumLoginUser && $can('write', boardMstId)" 
          type="button" class="btn btn-sm btn-primary" @click="onClickWrite">글쓰기</button>
      </div>
    </div>

    <!--table-->
    <table>
      <colgroup>
        <col width="100" />
        <col />
        <col width="150" />
        <col width="150" />
      </colgroup>
      <thead>
        <tr>
          <th>번호</th>
          <th class="text-left">제목</th>
          <th>작성자</th>
          <th>작성일</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, idx) of boardData.list" 
          :key="item.boardSeq"
          class="link"
          @click="onClickItem(item)">
          <td class="num">
            {{ idx | eufRowNumberDescForPageInfo(boardData.pi) }}
          </td>
          <td class="text-left">
            <div class="title">{{item.title}}</div>                  
            <span v-if="item.cmtCnt"
              class="text-primary">({{item.cmtCnt}})</span>
          </td>
          <td>{{item.regName}}</td>
          <td>{{item.rgtDttm | fmtBoardDate}}</td>
        </tr>
      </tbody>
    </table>

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
import CochingSelect from '@/components/CochingSelect.vue';

import { DEF_BOARD } from '@/constants/board';
import { getCodes } from '@/api/coching/comm/code';
import { getBoardList } from '@/api/coching/comm/board';

export default {
  name: 'coching-board-list',
  mixins: [ernsUtils, boardMixin],
  components: {
    Pagenation,
    CochingSelect,
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
      default: 'BD_NT_001'
    }
  },
  async mounted(){
    const _vm = this;

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
    // 검색 
		onSearch() {
      const _vm = this;
      _vm.loadList(1);
    },

    //게시글 선택 
    onClickItem(item) {
      const _vm = this;
      _vm.goView(item);
    },

    //게시글 등록
    onClickWrite(){
      const _vm = this;
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

        await _vm.loadList(1);

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

			_vm.loading(true);
      try {
        const params = _vm.getListSearchParam();
        const res = await getBoardList(params);
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
