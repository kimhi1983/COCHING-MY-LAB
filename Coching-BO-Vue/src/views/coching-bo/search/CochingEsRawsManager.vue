<template>
  <div>
    <!-- 검색조건 -->

    <b-card no-body>
      <b-card-body>
        <b-row>

          <b-col cols="12" md="6">
            <b-form-group label="원료명" label-for="search-coching-raws-keyword-searchText">
              <b-input-group>
                <b-form-input
                  id="search-coching-raws-keyword-searchText"
                  v-model="listData.sc.searchText"
                  @keyup.enter="onSearch"
                  placeholder="원료를 검색하세요."
                  size="sm"
                ></b-form-input>                
                <b-input-group-append v-show="listData.sc.searchText">
                  <b-button variant="outline-secondary" 
                    @click="listData.sc.searchText = ''"
                    size="sm"
                    v-b-tooltip.hover
                    title="검색어 초기화"
                    ><feather-icon icon="XIcon" size="14" />
                  </b-button>
                </b-input-group-append>              
                <b-input-group-append>
                  <b-button variant="outline-primary" 
                    @click="onSearch"
                    size="sm"
                    v-b-tooltip.hover
                    title="검색"
                    ><feather-icon icon="SearchIcon" size="14" />
                  </b-button>
                </b-input-group-append>              
              </b-input-group>
            </b-form-group>
          </b-col>

          <!-- <b-col cols="12" md="6">
            <label>인덱스 목록</label>
              <v-select
                v-model="esIndex.selectedOption"
                :options="esIndex.options" :reduce="op => op.value"
                :dir="$store.state.appConfig.isRTL ? 'rtl' : 'ltr'"
                class="w-100"              
                @option:selected="loadList(1)"
              >
                <span slot="no-options">등록된 인덱스 목록</span>
              </v-select>
          </b-col> -->

          <b-col cols="12" md="6" >
            <div class="erns-filter-div mt-2 text-right">
              <b-form-group label="">

                <b-button
                  v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                  variant="primary"
                  class="mr-1"
                  size="sm"
                  v-b-tooltip.hover
                  title="T_ES_RAW 초기화"
                  @click="onClickResetTable"
                  ><feather-icon icon="RefreshCcwIcon" size="14" /> T_ES_RAW 초기화
                </b-button>

                <b-button
                  v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                  variant="primary"
                  class=""  
                  size="sm"
                  v-b-tooltip.hover
                  title="ES 전송"
                  @click="onClickTransferToEs"
                  ><feather-icon icon="UploadIcon" size="14" /> ES 전송
                </b-button>
              </b-form-group>
            </div>
          </b-col>

         

        </b-row>

      </b-card-body>
    </b-card>
    <!-- // 검색조건 -->

    <!-- listData.sc:{{ listData.sc }} -->

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
          
          rowDragManaged="true"

          pagination=false
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
  </div>
</template>

<script>
import vSelect from 'vue-select';
import ernsUtils from "@/components/mixins/ernsUtils";
import Ripple from "vue-ripple-directive";
import draggable from 'vuedraggable';
import "ag-grid-community/styles/ag-grid.css"; // Mandatory CSS required by the Data Grid
import "ag-grid-community/styles/ag-theme-quartz.css";

import { AgGridVue } from "ag-grid-vue";
import ernsAgGrid from '@/components/ernsAgGrid/ernsAgGrid';
import { AG_GRID_COMMON_COLUMN_DEFS } from '@/constants/agGrid';

import { DEF_SEARCH_OPT, DEF_ROW } from "@/constants/cochingIngredient";
import {
  getEsCochingRawManageList,

  getEsCochingRawIndices,

  resetEsCochingRawTable,
  getEsCochingRawCreateEsIndex,  
} from "@/api/coching-bo/search/esCochingRawManager";

import ButtonCellRenderer from "@/components/ernsAgGrid/ButtonCellRenderer.vue";
import EwgScoreCellRenderer from "@/components/ernsAgGrid/EwgScoreCellRenderer.vue";

import cochingUtils from "@/components/mixins/cochingUtils";
import moment from 'moment';

const GRID_ID_MAIN = 'main-grid';
const DEF_PER_PAGE = 20;

export default {
  mixins: [ernsUtils, ernsAgGrid, cochingUtils],
  components: {
    vSelect,
    draggable, 
    AgGridVue,    
    ButtonCellRenderer,
    EwgScoreCellRenderer,
  },
  directives: {
    Ripple,
  },
  props: {},
  computed: {
    showRows(){
      return this.$store.state.erns.showRows;
    }
  },
  filters: {},
  async mounted() {
    const _vm = this;

    _vm.getInitParam();
    //_vm.loadList(_vm.listData.pi.curPage);
    await _vm.loadIndexList();
    await _vm.onSearch();
  },
  beforeDestroy() {
    const _vm = this;
  },
  //페이지 이동전 처리
  beforeRouteLeave : function(to, from, next){
    const _vm = this;
    try{
      const data = {
        initParam : {
          pi : _vm.listData.pi
        }
      };
      _vm.eumSetRouteHistoryParam(data);
    }catch(err){
      console.error(err);
    }

    return next();
  },  
  data() {
    const _vm = this;
    
    // Ag-Grid Column Definition
    const AG_GRID_COLUMN_DEFS = [
      { hide: true,
        headerName: "Row ID", field: "rowId"          , width: 70 
        , valueGetter: (params) => params.node.id // 내부 rowId 출력
      },
      { headerName: "번호"        , field: "index"        , width: 70  , cellClass: 'flex-center',
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
        cellStyle: { "white-space": "normal" } // 여러 줄로 표시
      },
      { headerName: "아이디"        , field: "rawSeq"        , width: 70  , cellClass: 'flex-right',
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
        cellStyle: { "white-space": "normal" } // 여러 줄로 표시
      },
      { headerName: "원료명"      , field: "rawName"      , width: 200  , cellClass: 'flex-left',
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
        cellStyle: { "white-space": "normal" } // 여러 줄로 표시
      },
      { headerName: "중량"        , field: "weight"       , width: 100  , cellClass: 'flex-right'
        , valueFormatter: (params) => params.value ? `${params.value} Kg` : '-'
      },

      { headerName: "성분수"     , field: "rawElemInfoCount" , width: 120  , cellClass: 'flex-right',
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
        cellStyle: { "white-space": "normal" } // 여러 줄로 표시
      },
      { headerName: "성분명"      , field: "rawElemNames"    , width: 300  , cellClass: 'flex-left'
        , valueFormatter: (params) => {
          if(!params.node.data.rawElemInfo) return "-";
          return this.displayArrayVal(params.node.data.rawElemInfo.map(item=>item.repNameKo));
        },
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
        cellStyle: { "white-space": "normal" } // 여러 줄로 표시
      },
      { headerName: "원료 조회수"  , field: "viewSumCnt"     , width: 120  , cellClass: 'flex-right'},

      { headerName: "공급사"      , field: "supplier"     , width: 150  , cellClass: 'flex-left'},      
      { headerName: "제조사"      , field: "prodCompany"  , width: 200  , cellClass: 'flex-left'},
      { headerName: "제조국"      , field: "prodCountryName"  , width: 200  , cellClass: 'flex-left'},

      { headerName: "파트너사명"  , field: "ptnName"      , width: 200  , cellClass: 'flex-left'},
      { headerName: "사업자번호"  , field: "ptnLic"       , width: 150  , cellClass: 'flex-center'
        , valueFormatter: (params) => params.value ? this.$options.filters.eufmtBizNumDash(params.value) : '-'
      },
      { headerName: "국적"        , field: "nation"       , width: 100  , cellClass: 'flex-left'},
      
      { hide : true,
        headerName: "수정여부"  , field: "_isEditRow" , width: 100  , cellClass: 'flex-center'},
    ];

    return {
      esIndex : {
        selectedOption: '',
        options: [],
        // selectedOption: 'coching_raw20250115',
        // options: [
        //   { value: 'coching_raw20250115', label: 'coching_raw20250115' },          
        // ],
      },

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
          EwgScoreCellRenderer,
        },

        sc: { ...DEF_SEARCH_OPT.sc },
        pi: { ...DEF_SEARCH_OPT.pi, perPage : DEF_PER_PAGE },
        rowData: [],
        rawList: [],
      },
    };
  },
  methods: {
    //$route.query, $route.params, HistoryParam 등으로 초기값 설정
    getInitParam() {
      const _vm = this;
      const data = this.eumGetRouteHistoryParam();

      if(data && data.initParam){
        _vm.listData.pi = {...data.initParam.pi};
      }      
    },

    //agGrid셀변경시 처리
    onCellValueChanged(params) {
      const _vm = this;

      const listData = _vm.listData;

      //변경사항 저장
      _vm.ernsAgGrid_trackCellChanges(listData.gridId, params);

      //Cell 랜더러에게 변경 noti
      const checkFields = ["_isEditRow", "name"];
      if (checkFields.includes(params.column.colId)) {   
        params.api.refreshCells({
          columns: ["setButton", "delButton"], // 새로고침
          rowNodes: [params.node],
          force : true
        });
      }

      listData.isDisabledAllSave = _vm.checkIsDisabledAllSave();
    },

    //각 Row 필수값 체크
    validateRowValue(rowData){
      if("" == (rowData.name || '').trim()) {
        return false; //키워드 필수
      }
      
      return true;
    },

    //전체 저장 활성화 여부
    checkIsDisabledAllSave(){
      const _vm = this;

      const listData = _vm.listData;
      if(!listData.gridOptions.isEditData){
        return true;
      }

      const gridRef = _vm.$refs[listData.gridId];
      if(!gridRef?.api){
        return true;
      }

      const allRows = gridRef.api.getModel().rowsToDisplay; // 전체 row 배열 가져오기
      for (const row of allRows) {
        if(!_vm.validateRowValue(row.data)){
          return true;
        }
      }

      return false;
    },

    //검색
    onSearch() {
      const _vm = this;
      _vm.loadList(1);
    },

    //T_ES_RAW 초기화
    async onClickResetTable() {
      const _vm = this;
      
      const result = await _vm.$swal({
        title: '확인',
        text: `T_ES_RAW 초기화 하시겠습니까?`,
        showCancelButton: true,
        confirmButtonText: `초기화`,
        customClass: {
          confirmButton: 'btn btn-danger ml-1',
          cancelButton: 'btn btn-outline-primary',
        },
        buttonsStyling: false,
      });

      _vm.loading(true);
      try {        
        const params = {};

        if(result.value){
          await resetEsCochingRawTable(params);
          await _vm.onSearch();
          _vm.alertSuccess("T_ES_RAW 테이블이 초기화 되었습니다.");
        }
      } catch (err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
    },

    //ES 전송
    async onClickTransferToEs() {
      const _vm = this;

      const result = await _vm.$swal({
        title: '확인',
        text: `검색엔진에 원료데이터를 반영하시겠습니까?`,
        showCancelButton: true,
        confirmButtonText: `반영`,
        customClass: {
          confirmButton: 'btn btn-danger ml-1',
          cancelButton: 'btn btn-outline-primary',
        },
        buttonsStyling: false,
      });

      _vm.loading(true);
      try {
        const params = {
          "indexName" : `coching_raw${moment().format('YYYYMMDDHHmmss')}`
        };        
        
        if(result.value){
          await getEsCochingRawCreateEsIndex(params);
          await _vm.loadIndexList();
          _vm.alertSuccess("원료 데이터가 검색엔진에 전송 되었습니다.");
        }
      } catch (err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
    },

    //검색 초기화
    async onClickResetFilter() {
      const _vm = this;
      _vm.listData.sc = { ...DEF_SEARCH_OPT.sc };
      _vm.listData.pi = { ...DEF_SEARCH_OPT.pi, perPage : DEF_PER_PAGE };
      await _vm.onSearch();
    },

    //목록 로드
    async loadList(pageNo) {
      const _vm = this;

      const listData = _vm.listData, pInfo = _vm.listData.pi, searchOp = _vm.listData.sc;

      pInfo.curPage = pageNo || 1;

      _vm.loading(true);
      try {
        listData.nextRowIndex = 1;
        listData.rowData = [];
        
        const params = _vm.getListSearchParam();
        //데이터 로드
        const res = await getEsCochingRawManageList(params);
        const { pageInfo, list, sc } = res.resultData;

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

            // rawTypeInfo : item.rawTypeInfo ? JSON.parse(item.rawTypeInfo) : [],
            // rawElemInfo : item.rawElemInfo ? JSON.parse(item.rawElemInfo) : [],
            // rawDetailInfo : item.rawDetailInfo ? JSON.parse(item.rawDetailInfo) : [] ,
            // docInfo : item.docInfo ? JSON.parse(item.docInfo) : [],
          };
        });
        listData.rowData = JSON.parse(JSON.stringify(listData.rawList));
        _vm.ernsAgGrid_resetOriginalData(listData.gridId, listData.rowData);   
        
      } catch (err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
    },

    //SearchParam 입력
    getListSearchParam() {
      const _vm = this;

      const pInfo = _vm.listData.pi, searchOp = _vm.listData.sc;

      const params = {
        page: pInfo.curPage,
        rowSize: pInfo.perPage,

        rawNameL: searchOp.searchText,
      };

      return params;
    },

    //인덱스 목록 로드
    async loadIndexList() {
      const _vm = this;

      const esIndex = _vm.esIndex;

      _vm.loading(true);
      try {
        const params = {
          commands : "/coching_raw*?format=json"
        };

        //데이터 로드
        const res = await getEsCochingRawIndices(params);
        
        esIndex.options = res.resultData.map(item=>{
          return {
            ...item,
            value : item.index,
            label : item.index,
          }
        });
        if(!esIndex.selectedOption){
          esIndex.selectedOption = esIndex.options[0].value;
        }

      } catch (err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
    }
  },
};
</script>

<style lang="scss" scoped>
.erns-ag-grid-vue{
  min-height: calc(100vh - 420px);  
}
</style>
