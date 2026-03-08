<template>
  <div>
    <!-- 검색조건 -->

    <b-card no-body>
      <b-card-body>
        <b-row>

          <b-col cols="6" md="3">
            <b-form-group label="검색 조건" label-for="search-coching-ingredient-keyword-exactMatchOnly">
              <b-form-radio-group
                id="search-coching-ingredient-keyword-exactMatchOnly"
                v-model="listData.sc.exactMatchOnly"
                buttons
                size="sm"
                button-variant="outline-primary"
                @change="onSearch"
                :options="[
                  {text:'전체일치', value: true},{text:'부분일치', value: false}
                ]"
              >
              </b-form-radio-group>
            </b-form-group>
          </b-col>

          <b-col cols="6" md="9" class="search-input-container">
            <b-form-group label="검색" label-for="search-coching-ingredient-keyword-searchText">
              <b-input-group>
                <b-form-input
                  id="search-coching-ingredient-keyword-searchText"
                  v-model="listData.sc.searchText"
                  @keyup.enter="onSearch"
                  placeholder="성분을 검색하세요."
                  size="sm"
                ></b-form-input>  
                <b-input-group-append v-show="listData.sc.searchText">
                  <b-button variant="outline-secondary" 
                    @click="onClearSearch"
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

        </b-row>

      </b-card-body>
    </b-card>
    <!-- // 검색조건 -->

    <!-- listData.sc:{{ listData.sc }} -->

    <!-- 목록 -->
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

      <!-- <button @click="ernsAgGrid_showModifiedCells(listData.gridId)">main-grid 변경된 셀 확인</button> -->
      <!-- <button @click="ernsAgGrid_resetToOriginal(listData.gridId)">main-grid 원본 데이터로 복구</button> -->

      <!-- listData.rowData:{{ listData.rowData }} -->
      <!-- listData.gridOptions.isEditData:{{ listData.gridOptions.isEditData }} -->

  </div>
</template>

<script>
import ernsUtils from "@/components/mixins/ernsUtils";
import cochingUtils from "@/components/mixins/cochingUtils";
import Ripple from "vue-ripple-directive";
import draggable from 'vuedraggable';
import "ag-grid-community/styles/ag-grid.css"; // Mandatory CSS required by the Data Grid
import "ag-grid-community/styles/ag-theme-quartz.css";

import { AgGridVue } from "ag-grid-vue";
import ernsAgGrid from '@/components/ernsAgGrid/ernsAgGrid';
import { AG_GRID_COMMON_COLUMN_DEFS } from '@/constants/agGrid';

import { DEF_SEARCH_OPT, DEF_ROW } from "@/constants/cochingIngredient";
import {
  getEsIngredientList,
} from "@/api/coching-bo/search/esSearch";

import ButtonCellRenderer from "@/components/ernsAgGrid/ButtonCellRenderer.vue";
import EwgScoreCellRenderer from "@/components/ernsAgGrid/EwgScoreCellRenderer.vue";
import EsHighlightTextCellRenderer from "@/components/ernsAgGrid/EsHighlightTextCellRenderer.vue";
import EsHighlightArrayTextCellRenderer from "@/components/ernsAgGrid/EsHighlightArrayTextCellRenderer.vue";  

const GRID_ID_MAIN = 'main-grid';

export default {
  mixins: [ernsUtils, cochingUtils, ernsAgGrid],
  components: {
    draggable, 
    AgGridVue,    
    ButtonCellRenderer,
    EwgScoreCellRenderer,
    EsHighlightTextCellRenderer,
    EsHighlightArrayTextCellRenderer,
  },
  directives: {
    Ripple,
  },
  props: {},
  computed: {    
  },
  filters: {},
  mounted() {
    const _vm = this;

    //_vm.getInitParam();
    _vm.loadList(1);
  },
  beforeDestroy() {
    const _vm = this;
  },
  // //페이지 이동전 처리
  // beforeRouteLeave : function(to, from, next){
  //   const _vm = this;
  //   try{
  //     const data = {
  //       initParam : {
  //         pi : _vm.listData.pi
  //       }
  //     };
  //     _vm.eumSetRouteHistoryParam(data);
  //   }catch(err){
  //     console.error(err);
  //   }

  //   return next();
  // },  
  data() {
    const _vm = this;
    
    // Ag-Grid Column Definition
    const AG_GRID_COLUMN_DEFS = [
      { hide: true,
        headerName: "Row ID", field: "rowId"          , width: 70 
        , valueGetter: (params) => params.node.id // 내부 rowId 출력
      },
      { headerName: "번호"        , field: "index"        , width: 100  , cellClass: 'flex-center selectable-text'} ,
      { headerName: "EWG"         , field: "ewgScore"     , width: 100  , cellClass: 'flex-left selectable-text',
        cellRenderer: "EwgScoreCellRenderer", // 렌더러 지정
        cellRendererParams: (params) => ({
          ewgVal : `${params.node.data['ewg_score_min']}-${params.node.data['ewg_score']}`,
          ewgDataLabel: params.node.data.ewg_data_label,
        }),          
      },
      { headerName: "성분명"      , field: "rep_name"     , width: 200  , cellClass: 'flex-left selectable-text',
        cellRenderer: "EsHighlightTextCellRenderer", // 렌더러 지정
        cellRendererParams: {
          onClick: this.onClickGridCell  // 클릭 핸들러 전달
        },
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
        cellStyle: { "white-space": "normal" } // 여러 줄로 표시
      },
      { headerName: "동의어"      , field: "names_ko"     , width: 200  , cellClass: 'flex-left selectable-text',
        cellRenderer: "EsHighlightArrayTextCellRenderer", // 렌더러 지정
        cellRendererParams: {
          onClick: this.onClickGridCell  // 클릭 핸들러 전달
        },
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
        cellStyle: { "white-space": "normal" } // 여러 줄로 표시
      },
      { headerName: "INCI(영문명)", field: "inci"         , width: 300  , cellClass: 'flex-left selectable-text',
        cellRenderer: "EsHighlightArrayTextCellRenderer", // 렌더러 지정
        cellRendererParams: {
          highlightPrefix: ["rep_name_en"],
          onClick: this.onClickGridCell  // 클릭 핸들러 전달
        },
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
        cellStyle: { "white-space": "normal" } // 여러 줄로 표시
      },
      // { headerName: "영문명"      , field: "rep_name_en"  , width: 200  , cellClass: 'flex-left selectable-text',
      //   cellRenderer: "EsHighlightTextCellRenderer", // 렌더러 지정          
      //   wrapText: true, // 자동 줄바꿈 활성화
      //   autoHeight: true, // 행 높이를 자동 조정
      //   cellStyle: { "white-space": "normal" } // 여러 줄로 표시
      // },
      { headerName: "영문동의어"  , field: "names_en"     , width: 200  , cellClass: 'flex-left selectable-text',
        cellRenderer: "EsHighlightArrayTextCellRenderer", // 렌더러 지정
        cellRendererParams: {
          onClick: this.onClickGridCell  // 클릭 핸들러 전달
        },
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
        cellStyle: { "white-space": "normal" } // 여러 줄로 표시
      },
      { headerName: "CAS NO"      , field: "cas_no"       , width: 150  , cellClass: 'flex-left selectable-text',
        cellRenderer: "EsHighlightArrayTextCellRenderer", // 렌더러 지정
        cellRendererParams: {
          onClick: this.onClickGridCell  // 클릭 핸들러 전달
        },
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
      },
      { headerName: "EC NO"       , field: "ec_no"        , width: 150  , cellClass: 'flex-left selectable-text',
        cellRenderer: "EsHighlightArrayTextCellRenderer", // 렌더러 지정
        cellRendererParams: {
          onClick: this.onClickGridCell  // 클릭 핸들러 전달
        },
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
      },      
      { headerName: "시성식"      , field: "formula"      , width: 200  , cellClass: 'flex-left selectable-text',
        cellRenderer: "EsHighlightTextCellRenderer", // 렌더러 지정        
      },
      { headerName: "용도"        , field: "purposes"     , width: 250  , cellClass: 'flex-left selectable-text',
        cellRenderer: "EsHighlightTextCellRenderer", // 렌더러 지정        
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
        cellStyle: { "white-space": "normal" } // 여러 줄로 표시
      },
      
      
      
      { hide: true, 
        headerName: "exp_url_1"         , field: "exp_url_1"     , width: 200  , cellClass: 'flex-left selectable-text'},
      { hide: true, 
        headerName: "exp_url_2"         , field: "exp_url_2"     , width: 200  , cellClass: 'flex-left selectable-text'},
      { hide: true, 
        headerName: "ewg_score_min"     , field: "ewg_score_min"  , width: 70  , cellClass: 'flex-left selectable-text'},
      { hide: true, 
        headerName: "ewg_score"         , field: "ewg_score"      , width: 70  , cellClass: 'flex-left selectable-text'},
      { hide: true, 
        headerName: "ewg_data_label"    , field: "ewg_data_label" , width: 70  , cellClass: 'flex-left selectable-text'},
      { hide: true,
        headerName: "ewg_rawno_id"      , field: "ewg_rawno_id"   , width: 70  , cellClass: 'flex-left selectable-text'},
      { hide: true,
        headerName: "ewg_url"           , field: "ewg_url"        , width: 70  , cellClass: 'flex-left selectable-text'},
      
      { hide : true,
        headerName: "수정여부"  , field: "_isEditRow" , width: 100  , cellClass: 'flex-center'},      
    ];

    return {
      isDisabled: true,

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
          EsHighlightTextCellRenderer,
        },

        sc: { ...DEF_SEARCH_OPT.sc, exactMatchOnly: true },
        pi: { ...DEF_SEARCH_OPT.pi },
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

    //검색어 초기화
    onClearSearch() {
      const _vm = this;
      
      // Vue의 반응성을 보장하기 위해 Vue.set 사용
      _vm.$set(_vm.listData.sc, 'searchText', '');
      
      // 강제로 업데이트 트리거
      _vm.$forceUpdate();
      
      _vm.$nextTick(() => {
        // input에 포커스 주기
        const searchInput = document.getElementById('search-coching-ingredient-keyword-searchText');
        if (searchInput) {
          searchInput.focus();
        }
      });
    },

    //검색 초기화
    async onClickResetFilter() {
      const _vm = this;
      _vm.listData.sc = { ...DEF_SEARCH_OPT.sc };
      _vm.listData.pi = { ...DEF_SEARCH_OPT.pi };
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
        const res = await getEsIngredientList(params);
        const { total, list, maxScore, version} = res.resultData;

        console.debug(maxScore);

        listData.pi = {
          ...listData.pi,
          curPage: 1,
          totalItem: total,
          perPage: total,
        };

        const dataList = _vm.convertSearchResultByVersion(version, list);
        listData.rawList = dataList.map((item, index) => {
          return {
            rowIndex : listData.nextRowIndex++, //rowIndex 설정
            index: _vm.$options.filters.eufRowNumberForPageInfo(index, pInfo),
            _isEditRow : false,
            ...item,
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
        rowSize: 100,

        text: searchOp.searchText,
        exactMatchOnly: searchOp.exactMatchOnly,
      };

      return params;
    },

    onClickGridCell(cellData) {
      const _vm = this;

      //console.log('셀 클릭됨:', cellData);
      // cellData.event - 클릭 이벤트
      // cellData.data - 행 데이터
      // cellData.value - 셀 값 (배열 아이템인 경우 개별 아이템 값)
      // cellData.originalValue - 원본 배열 값 (배열 아이템인 경우만)
      // cellData.index - 배열 인덱스 (배열 아이템인 경우만)
      // cellData.isArrayItem - 배열 아이템 여부
      // cellData.field - 필드명
      // cellData.params - 전체 params
      const { data, value, originalValue, index, isArrayItem, field, params } = cellData;

      let scOptionExactMatchOnly = true;
      let scKeyword = (value || '').trim().replace(/,/g, "\\,");
      
      switch(field){
        case "rep_name":          // 성분명
        case "names_ko":          // 동의어
        case "inci":              // INCI(영문명)
        case "names_en":          // 영문동의어
        case "cas_no":            // CAS NO
        case "ec_no":             // EC NO
        case "formula":           // 시성식
        case "purposes":          // 용도
          break;
        default:
          break;
      }

      _vm.listData.sc = {
        ..._vm.listData.sc,
        exactMatchOnly: scOptionExactMatchOnly,
        searchText: scKeyword
      };
      _vm.onSearch();
    }    
  },
};
</script>

<style lang="scss" scoped>
.erns-ag-grid-vue{
  min-height: calc(100vh - 360px);  
}
</style>
