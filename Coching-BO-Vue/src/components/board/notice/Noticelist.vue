<template>
  <div class="cm-notice-wrap">
  <!-- 검색조건 -->
    <b-card no-body>
      <b-card-body class="cm-notice-filter">
        <b-row>
          <b-col cols="12" md="9" class="mb-md-0 mb-2">
            <label>검색 키워드</label>
            <b-input-group>
              <b-form-input v-model="list.sc.titleL"
                @keyup.enter="onSearch"
                placeholder="검색어를 입력하세요."
              ></b-form-input>
              <b-input-group-append>
                <b-button variant="outline-primary"
                  @click="onSearch"
                >검색</b-button>
              </b-input-group-append>
            </b-input-group>
          </b-col>

          <b-col cols="12" md="3" class="mb-md-0 mb-2">
            <div class="erns-filter-div mt-2 text-right">
              <!-- <b-button
                v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                variant="outline-danger"
                class="mr-1"
                @click="onClickDelete"
              > 삭제
              </b-button> -->
              <b-button
                v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                variant="primary"
                @click="onClickNew"
              > 등록
              </b-button>
            </div>
          </b-col>

        </b-row>
      </b-card-body>
    </b-card>
    <!-- // 검색조건 -->

    <!-- <p class="erns-table-buttons">
      <b-button size="sm" @click="selectAllRows">전체 선택</b-button>
      <b-button size="sm" @click="clearSelected">선택 해제</b-button>
    </p> -->

    <!-- 목록 -->
    <b-card no-body class="mb-0">

      <!-- @row-selected="onRowSelected" selectable select-mode="multi" selected-variant="info" -->
      <b-table
        ref="boardListTable"
        responsive striped hover
        :items="list.rawList" :fields="list.fields"
        @row-clicked="onClickListRow"
        @sort-changed="onSortingChanged" no-local-sorting
        :sort-by.sync="list.sc.sortBy"
        :sort-desc.sync="list.sc.sortDesc"
        primary-key="boardId"
        show-empty empty-text="게시글이 없습니다."
      >
        <template #table-colgroup="scope">
          <col
            v-for="field in scope.fields"
            :key="field.key"
            :style="{ width: field.width }"
          >
        </template>

        <template #cell(index)="data">
          {{ data.index | eufRowNumberForPageInfo(list.pi) }}
        </template>

        <template #cell(selected)="{ rowSelected }">
          <template v-if="rowSelected">
            <span aria-hidden="true">&check;</span>
            <span class="sr-only">Selected</span>
          </template>
          <template v-else>
            <span aria-hidden="true">&nbsp;</span>
            <span class="sr-only">Not selected</span>
          </template>
        </template>

        <template #cell(category)="data">
          {{ data.item.category | eufGetCodeName(codes.CATE) }}
        </template>

        <template #cell(views)="data">
          {{ data.item.views | eFmtNumT }}
        </template>

        <template #cell(startDate)="data">
          {{ data.item.startDate | eFmtDate('-') }}
        </template>

        <template #cell(endDate)="data">
          {{ data.item.endDate | eFmtDate('-') }}
        </template>

        <template #cell(regDate)="data">
          {{ data.item.regDate | eFmtDateTime('-') }}
        </template>

        <template #cell(udtDate)="data">
          {{ (data.item.udtDate || data.item.regDate) | eFmtDate('-') }}
        </template>

      </b-table>

      <div class="mx-2 mb-2">
        <b-row>
          <b-col cols="12" sm="6"
            class="d-flex align-items-center justify-content-center justify-content-sm-start"
          >
            <v-select
              v-model="list.pi.perPage"
              :dir="$store.state.appConfig.isRTL ? 'rtl' : 'ltr'"
              :options="showRows"
              :clearable="false"
              class="per-page-selector d-inline-block mx-50"
              @option:selected="loadList(1)"
            />

            <span class="text-muted">
              {{eumPaginationRangeForPageInfo(list.pi)}}
            </span>
          </b-col>

          <b-col cols="12" sm="6"
            class="d-flex align-items-center justify-content-center justify-content-sm-end"
          >
            <b-pagination
              v-if="list.pi.totalItem > 0"
              v-model="list.pi.curPage"
              :total-rows="list.pi.totalItem"
              :per-page="list.pi.perPage"
              first-number
              last-number
              limit = "10"
              class="mb-0 mt-1 mt-sm-0"
              prev-class="prev-item"
              next-class="next-item"
              @change="loadList"
            >
              <template #prev-text>
                <feather-icon icon="ChevronLeftIcon" size="18"/>
              </template>
              <template #next-text>
                <feather-icon icon="ChevronRightIcon" size="18"/>
              </template>
            </b-pagination>
          </b-col>

        </b-row>
      </div>
    </b-card>

    <!-- {{list}} -->
    <!-- {{list.selected}} -->

    <!-- // 목록 -->
  </div>
</template>
<script>
import {getCodes} from '@/api/coching-bo/comm/code';
import {getBoardMaster} from '@/api/coching-bo/system/boardMaster';
import {getBoardList} from '@/api/coching-bo/comm/board';

import Ripple from 'vue-ripple-directive'
import ernsUtils from '@/components/mixins/ernsUtils';

import vSelect from 'vue-select';

const DEF_SEARCH_OPT = {
  pi:{
    curPage : 1,
    totalItem : 0,
    perPage : 15
  },
  sc: {
    delYn: 'N',
    titleL: '',
    boardMstId : 'BOARD_NT_0001',
  }
};

export default {
  name: 'coching-BackOffice-Notice',
  mixins: [ernsUtils],
  components : {
    vSelect
  },
  directives: {
    Ripple
  },
  props: {

  },
  computed : {
    showRows(){
      return this.$store.state.erns.showRows;
    }
  },
  data(){
    return {
      codes : {
        CATE : [],
      },

      boardMst : {},

      list :{
        sc : {...DEF_SEARCH_OPT.sc},

        pi : {...DEF_SEARCH_OPT.pi},

        fields : [
          { width: "90px" , key: 'index'        , label: '번호'         , stickyColumn: false, sortable: false , class: 'text-center' },
          { width: "*"    , key: 'title'        , label: '제목'         , sortable: false , thClass: 'text-center' },
          { width: "150px", key: 'writer'       , label: '게시자'       , sortable: false , class: 'text-center' },
          { width: "150px", key: 'chngDttm'     , label: '최종수정일'   , sortable: false , class: 'text-center' },
        ],

        rawList:[],
        dispList:[],
        selected:[]
      },
    }
  },
  mounted(){
    const _vm = this;
    _vm.loadCodes();

    _vm.getInitParam();
    _vm.loadList(_vm.list.pi.curPage);
  },
  beforeDestroy(){
    const _vm = this;
  },
  //페이지 이동전 처리
  beforeRouteLeave : function(to, from, next){
    const _vm = this;
    try{
      const data = {
        initParam : {
          sc : _vm.list.sc,
          pi : _vm.list.pi
        }
      };
      //console.info("beforeRouteLeave");
      //console.info(data);
      _vm.eumSetRouteHistoryParam(data);
    }catch(err){
      console.error(err);
    }

    return next();
  },
  methods: {
    //$route.query, $route.params, HistoryParam 등으로 초기값 설정
    getInitParam() {
      const _vm = this;
      //const query = _vm.$route.query, params = _vm.$route.params;
      const data = this.eumGetRouteHistoryParam();
      //console.info("onAfterRouter");console.info(data);
      if(data && data.initParam){
        _vm.list.sc = {...data.initParam.sc};
        _vm.list.pi = {...data.initParam.pi};
      }
    },

    //검색
    onSearch(){
      const _vm = this;
      _vm.loadList(1);
    },

    //상세이동
    onClickListRow(item, index, event){
      const _vm = this;
      _vm.$router.push({ name: 'coching-bo-fn-notice-edit', query: {boardMstId: item.boardMstId, boardSeq: item.boardSeq}});
    },

    //등록
    onClickNew(){
      const _vm = this;
      _vm.$router.push({ name: 'coching-bo-fn-notice-add', query: {boardMstId: _vm.list.sc.boardMstId}});
    },

    //삭제
    onClickDelete(){

    },

    //선텍
    onRowSelected(items) {
      const _vm = this;
      _vm.list.selected = items;
    },

    //전체선택
    selectAllRows() {
      this.$refs.boardListTable.selectAllRows()
    },

    //선택해제
    clearSelected() {
      this.$refs.boardListTable.clearSelected()
    },

    //정렬변경 이벤트
    onSortingChanged(ctx){
      const _vm = this;
      const sc = _vm.list.sc;
      const pInfo = _vm.list.pi;
      console.info(ctx);

      _vm.loadList(pInfo.curPage);
    },

    //목록로드
    async loadList(pageNo){
      const _vm = this;

      const dataList = _vm.list, pInfo = _vm.list.pi, searchOp = _vm.list.sc;
      pInfo.curPage = pageNo || 1;

      try{
        const params = _vm.getListSearchParam();

        _vm.loading(true);
        const res = await getBoardList(params);
        const pageInfo = res.resultData.pageInfo;
        dataList.rawList = res.resultData.list;
        dataList.pi = {
          ...dataList.pi,
          curPage : pageInfo.currentPage,
          totalItem : pageInfo.totalItem,
          perPage : pageInfo.pageItemSize
        };
      }catch(err){
        _vm.alertError(err);
      }finally{
        _vm.loading(false);
      }

    },

    async loadCodes(){
      const _vm = this;

      const retBoardMst = await getBoardMaster({boardMstId : _vm.list.sc.boardMstId});
      _vm.boardMst = retBoardMst.resultData;

      const categoryGroupCd = _vm.boardMst ? _vm.boardMst.cateCd : null;
      if(categoryGroupCd){
        const cateList = await getCodes({codeMst:categoryGroupCd, rowSize:-1});
        _vm.codes.CATE = _vm.eumConvertToVueSelectOption(cateList.resultData.list);
      }
    },

    getListSearchParam() {
      const _vm = this;
      const pInfo = _vm.list.pi, searchOp = _vm.list.sc;
      
      return {
        page : pInfo.curPage,
        rowSize : pInfo.perPage,
        delYn : "N",
        //게시판 아이디
        boardMstId : searchOp.boardMstId,
        titleL : searchOp.titleL,
      }
    }
  }
}
</script>

<style lang="scss" scoped>
  th[role=columnheader] div{
    white-space:nowrap;
  }
</style>
