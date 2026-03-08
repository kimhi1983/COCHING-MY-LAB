<template>
  <div class="cm-notice-wrap">
  <!-- 검색조건 -->
    <b-card no-body>
      <b-card-body class="cm-notice-filter">
        <b-row>
          <b-col v-show="boardMst.cateCd"
            cols="12" md="3" class="mb-md-0">
            <b-form-group label="카테고리" label-for="board-notice-category"> 
              <v-select
                id="board-notice-category"
                v-model="listData.sc.cateCd"
                :options="codes.CATE" :reduce="op => op.value"
                  :dir="$store.state.appConfig.isRTL ? 'rtl' : 'ltr'"
                  :class="['w-100', 'select-size-sm', 'select-placeholder-gray', { 'has-value': listData.sc.cateCd }]"                
                  @input="onSearch"
                  placeholder="카테고리를 선택하세요."
                >
                <span slot="no-options">검색된 항목이 없습니다.</span>
              </v-select>
            </b-form-group>
          </b-col>

          <b-col cols="12" 
            :md='boardMst.cateCd ? "6" : "9"'
            class="mb-md-0">
            <b-form-group label="검색 키워드" label-for="board-notice-search-keyword">
              <b-input-group>
                <b-form-input 
                  id="board-notice-search-keyword"
                  v-model="listData.sc.titleL"
                  @keyup.enter="onSearch"
                  placeholder="검색어를 입력하세요."
                  size="sm"
                ></b-form-input>
                <b-input-group-append v-show="listData.sc.titleL">
                  <b-button variant="outline-secondary" 
                    @click="listData.sc.titleL = ''"
                    size="sm"
                    v-b-tooltip.hover
                    title="검색어 초기화"
                    ><feather-icon icon="XIcon" size="14" />
                  </b-button>
                </b-input-group-append>
                <b-input-group-append>
                  <b-button 
                    @click="onSearch"
                    variant="outline-primary" size="sm" 
                    v-b-tooltip.hover
                    title="검색"
                    ><feather-icon icon="SearchIcon" size="14" />
                  </b-button>                    
                </b-input-group-append>
              </b-input-group>
            </b-form-group>
          </b-col>

          <b-col cols="12" md="3" class="mb-md-0">
            <div class="erns-filter-div mt-1 text-right">
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
                size="sm"
                v-b-tooltip.hover
                title="등록"
                ><feather-icon icon="EditIcon" size="14" />
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

    <!-- codes.CATE : {{ codes.CATE }} -->

    <!-- 목록 -->
    <b-card no-body class="mb-0">
      <div class="erns-ag-grid-vue">
        <ag-grid-vue
          class="ag-theme-quartz"
          :ref="listData.gridId"
          :gridOptions="listData.gridOptions"
          :columnDefs="listData.columnDefs"
          :rowData="listData.rowData"
          @gridReady="(params) => ernsAgGrid_registerGrid(listData.gridId, params, listData.gridOptions)"

          @cellValueChanged="onCellValueChanged"
          :frameworkComponents="listData.frameworkComponents"
          
          @cellClicked="onCellClicked"
          
          suppressRowTransform=true
        >
        </ag-grid-vue>
      </div>
      
      <div class="mx-2 mb-1 mt-1">
        <b-row>
          <b-col cols="12" sm="6"
            class="d-flex align-items-center justify-content-center justify-content-sm-start"
          >
            <span class="text-muted">
              {{eumPaginationRangeForPageInfo(listData.pi)}}
            </span>
          </b-col>

          <b-col cols="12" sm="6"
            class="d-flex align-items-center justify-content-center justify-content-sm-end"
          >
          <b-pagination
              v-if="listData.pi.totalItem > 0"
              v-model="listData.pi.curPage"
              :total-rows="listData.pi.totalItem"
              :per-page="listData.pi.perPage"
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
    <!-- {{codes.CATE}} -->

    <!-- // 목록 -->
  </div>
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';

import {getCodes} from '@/api/coching-bo/comm/code';
import {getBoardMaster} from '@/api/coching-bo/system/boardMaster';
import {getBoardList, delBoardDetails} from '@/api/coching-bo/comm/board';

import Ripple from 'vue-ripple-directive'
import vSelect from 'vue-select';

import boardMixin from '@/components/board/boardMixin';
import { DEF_SEARCH_OPT, DEF_BOARD, BOARD_MODE } from '@/constants/board';

import "ag-grid-community/styles/ag-grid.css"; // Mandatory CSS required by the Data Grid
import "ag-grid-community/styles/ag-theme-quartz.css";

import { AgGridVue } from "ag-grid-vue";
import ernsAgGrid from '@/components/ernsAgGrid/ernsAgGrid';
import { AG_GRID_COMMON_COLUMN_DEFS } from '@/constants/agGrid';

import ButtonCellRenderer from "@/components/ernsAgGrid/ButtonCellRenderer.vue";
import ToogleButtonCellRenderer from "@/components/ernsAgGrid/ToogleButtonCellRenderer.vue";
import IconButtonCellRenderer from "@/components/ernsAgGrid/IconButtonCellRenderer.vue";

const GRID_ID_MAIN = 'main-grid';

export default {
  name: 'coching-boar-notice-list',
  mixins: [ernsUtils, boardMixin, ernsAgGrid],
  components : {
    vSelect,
    AgGridVue,
    ButtonCellRenderer,
    ToogleButtonCellRenderer,
    IconButtonCellRenderer,
  },
  directives: {
    Ripple
  },
  computed : {
    showRows(){
      return this.$store.state.erns.showRows;
    }
  },
  watch: {
    
  },
  props:{
    boardMstId: {
      type: String,
      require: true
    }
  },
  data(){
    const _vm = this;

    const AG_GRID_COLUMN_DEFS = [
      { hide: true,
        headerName: "Row ID", field: "rowId"          , width: 70 
        , valueGetter: (params) => params.node.id // 내부 rowId 출력
      },

      { headerName: "번호"        , field: "index"  , width: 70    , cellClass: 'flex-center'},
      { headerName: "카테고리"    , field: "cateCd" , width: 150  , cellClass: 'flex-center' 
        //, valueFormatter: (params) => params.value ? this.$options.filters.eufGetCodeName(this.codes.CATE) : '-'
        ,valueFormatter: (params) => {
          return this.$options.filters.eufGetCodeName(params.value, this.codes.CATE);
        }
      },
      { headerName: "제목"  , field: "title", width: 400, cellClass: 'flex-left' },
      { headerName: "닉네임"  , field: "regName", width: 150, cellClass: 'flex-center'},
      { headerName: "아이디"  , field: "regId", width: 150, cellClass: 'flex-center'},
      
      {
        headerName: "삭제"      , field: "delButton"  , width: 70  , cellClass: 'text-center',
        cellRenderer: "IconButtonCellRenderer", // 렌더러 지정
        cellRendererParams: {
          icon: "TrashIcon",
          tooltip: "삭제",
          iconSize: "20",
          variant : " ",
          btnClass: 'btn-outline-secondary btn-no-border',
          action: (params) => {
            this.onDeleteRow(params);
          },
        },
      },

      { headerName: "최종수정일", field: "chngDttm"  , width: 150, cellClass: 'flex-center' },

      { hide : true,
        headerName: "수정여부"  , field: "_isEditRow" , width: 100  , cellClass: 'text-center'},
    ];

    return {
      codes : {
        CATE : [],
      },

      boardMst : {},

      listData: {
        gridId : GRID_ID_MAIN,
        nextRowIndex : 1,
        isDisabledAllSave : true,
        gridOptions : {
          isEditData : false,
          immutableData : true,
          defaultColDef : AG_GRID_COMMON_COLUMN_DEFS,
          getRowId: (params) => params.data.rowIndex, // 각 행의 ID를 'rowIndex' 로 지정
        },
        columnDefs: AG_GRID_COLUMN_DEFS,
        frameworkComponents :{
          ButtonCellRenderer,
          ToogleButtonCellRenderer,
        },

        dataSource: null,

        sc: { ...DEF_SEARCH_OPT.sc, boardMstId : this.boardMstId },
        pi: { ...DEF_SEARCH_OPT.pi },
        rowData: [],
        rawList: [],        
      },
    }
  },
  async mounted(){
    const _vm = this;
    await _vm.loadCodes();

    _vm.getInitParam();
    _vm.loadList(_vm.listData.pi.curPage);
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
          sc : _vm.listData.sc,
          pi : _vm.listData.pi
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
  
  methods : {
    //$route.query, $route.params, HistoryParam 등으로 초기값 설정
    getInitParam() {
      const _vm = this;
      //const query = _vm.$route.query, params = _vm.$route.params;
      const data = _vm.eumGetRouteHistoryParam();
      //console.info("onAfterRouter");console.info(data);
      if(data && data.initParam){
        _vm.listData.sc = {...data.initParam.sc};
        _vm.listData.pi = {...data.initParam.pi};
      }
    },

   //Cell 클릭 이벤트
    onCellClicked(event){
      const _vm = this;

      const item = event.node.data;
      const clickField = event.colDef.field;

      //상세이동
      const goDetailFileds = ["cateCd", "title", "regName", "regId", "chngDttm"];
      if(goDetailFileds.includes(clickField)){
        _vm.goEdit(item);
        return;
      }
    },

    //agGrid셀변경시 처리
    onCellValueChanged(params) {
      const _vm = this;

      const listData = _vm.listData;

      //변경사항 저장
      _vm.ernsAgGrid_trackCellChanges(listData.gridId, params);

      //Cell 랜더러에게 변경 noti
      const checkFields = ["_isEditRow"];
      if (checkFields.includes(params.column.colId)) {   
        params.api.refreshCells({
          columns: ["setButton", "delButton"], // 새로고침
          rowNodes: [params.node],
          force : true
        });
      }

      listData.isDisabledAllSave = _vm.checkIsDisabledAllSave();
    },

    //검색
    onSearch(){
      const _vm = this;
      _vm.loadList(1);
    },

    // 삭제
    async onDeleteRow(gridParams){
      const _vm = this;
      const params = {
        isRegMode: gridParams.data.boardSeq <= 0,
        boardMstId : _vm.boardMstId,
        boardSeq : gridParams.data.boardSeq || 0,
        delYn : 'Y'
      };

      if(params.isRegMode){
        _vm.ernsAgGrid_deleteRow(_vm.listData.gridId, gridParams.node.id);
        return;
      }

      //Validation
      if(!params.boardSeq){
        return;
      }

      const result = await _vm.$swal({
        title: '확인',
        text: `게시글을 삭제 하시겠습니까?`,
        showCancelButton: true,
        confirmButtonText: `삭제`,
        customClass: {
          confirmButton: 'btn btn-danger ml-1',
          cancelButton: 'btn btn-outline-primary',
        },
        buttonsStyling: false,
      });

      _vm.loading(true);
      try {        

        if(result.value){
          await delBoardDetails({list:[params]});
          await _vm.onSearch();
          _vm.alertSuccess("게시글을 삭제 했습니다.");
        }
      } catch (err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
    },

    //상세이동
    onClickListRow(item, index, event){
      const _vm = this;

      _vm.goEdit(item);
    },

    //등록
    onClickNew(){
      const _vm = this;

      _vm.goWrite();
    },

    //삭제
    onClickDelete(){

    },

    //선텍
    onRowSelected(items) {
      const _vm = this;
      _vm.listData.selected = items;
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
      const sc = _vm.listData.sc;
      const pInfo = _vm.listData.pi;
      console.info(ctx);

      _vm.loadList(pInfo.curPage);
    },

    //목록 로드
    async loadList(pageNo){
      const _vm = this;      
      
      const listData = _vm.listData, pInfo = _vm.listData.pi, searchOp = _vm.listData.sc;

      pInfo.curPage = pageNo || 1;
      
      _vm.loading(true);

      try {
        listData.nextRowIndex = 1;
        listData.rowData = [];

        const params = _vm.getListSearchParam();

        //데이터 로드
        const res = await getBoardList(params); 

        const { pageInfo, list, sc} = res.resultData;

        listData.pi = {
          ...listData.pi,
          curPage: pageInfo.currentPage,
          totalItem: pageInfo.totalItem,
          perPage: pageInfo.pageItemSize,
        };

        listData.rawList = list.map((item, index) => {
          return {
            rowIndex : listData.nextRowIndex++, //rowIndex 설정
            index: _vm.$options.filters.eufRowNumberForPageInfo(index, pInfo),
            _isEditRow : false,
            ...item,
          };
        });
        listData.rowData = JSON.parse(JSON.stringify(listData.rawList));
        _vm.ernsAgGrid_resetOriginalData(listData.gridId, listData.rowData); 
        
      }catch(err){
        _vm.alertError(err);
      }finally{
        _vm.loading(false);
      }
    },

    getListSearchParam() {
      const _vm = this;
      const pInfo = _vm.listData.pi, searchOp = _vm.listData.sc;
      
      return {
        page : pInfo.curPage,
        rowSize : pInfo.perPage,

        delYn : "N",
        //게시판 아이디
        boardMstId : searchOp.boardMstId,
        titleL : searchOp.titleL,
        cateCd : searchOp.cateCd,
      }
    },

    async loadCodes(){
      const _vm = this;

      const retBoardMst = await getBoardMaster({boardMstId : _vm.listData.sc.boardMstId});
      _vm.boardMst = retBoardMst.resultData;

      const categoryGroupCd = _vm.boardMst ? _vm.boardMst.cateCd : null;
      if(categoryGroupCd){        
        const cateList = await getCodes({grpCode:categoryGroupCd, etc5: _vm.getLocale(), rowSize:-1});
        _vm.codes.CATE = _vm.eumConvertToVueSelectOption(cateList.resultData.list, 'etc1');
      }else{
        _vm.listData.columnDefs = _vm.listData.columnDefs.filter(item=>item.field != 'cateCd');
      }
    },

    getLocale(){
      const _vm = this;
      return _vm.$route.meta.locale;
    }
  }
}
</script>

<style lang="scss" scoped>
  .erns-ag-grid-vue{
    min-height: calc(100vh - 420px);
  }

  th[role=columnheader] div{
    white-space:nowrap;
  }
</style>