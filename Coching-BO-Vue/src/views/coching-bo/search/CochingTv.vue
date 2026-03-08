<template>
  <div>
    <!-- 검색조건 -->

    <b-card no-body>
      <b-card-body>
        <b-row>

          <b-col cols="12" md="4">
            <b-form-group label="인덱스" label-for="search-coching-tv-keyword-esIndex">
              <v-select
                id="search-coching-tv-keyword-esIndex"
                v-model="esIndex.selectedOption"
                :options="esIndex.options" :reduce="op => op.value"
                :dir="$store.state.appConfig.isRTL ? 'rtl' : 'ltr'"
                :class="['w-100', 'select-size-sm', 'select-placeholder-gray', { 'has-value': esIndex.selectedOption }]"              
                @option:selected="loadList(1)"
              >
                <span slot="no-options">등록된 인덱스 목록</span>
              </v-select>
            </b-form-group>
          </b-col>

          <b-col cols="12" md="8">
            <b-form-group label="검색" label-for="search-coching-tv-keyword-searchText">
              <b-input-group>
                <b-form-input
                  id="search-coching-tv-keyword-searchText"
                  v-model="listData.sc.searchText"
                  @keyup.enter="onSearch"
                  placeholder="코칭 TV를 검색하세요."
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

          <!-- <b-col cols="12" md="6" >
            <div class="erns-filter-div mt-2 flex-right">
              <b-form-group label="">

                <b-button
                  :disabled="!listData.gridOptions.isEditData"
                  v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                  variant="outline-secondary"
                  class="mr-1 mb-1"
                  @click="onClickResetFilter"
                  >원복
                </b-button>

                <b-button
                  :disabled="listData.isDisabledAllSave"
                  v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                  variant="outline-primary"
                  class="mr-1 mb-1"
                  @click="onClickMultpleSave"
                  >저장
                </b-button>

                <b-button
                  v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                  variant="outline-primary"
                  class="mr-1 mb-1"
                  @click="onClickSaveOrder()"
                  >순서 저장
                </b-button>

                <b-button
                  v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                  variant="outline-primary"
                  class="mr-1 mb-1"
                  @click="onClickRegster()"
                  >등록
                </b-button>
              </b-form-group>
            </div>
          </b-col>

          <b-col cols="6" md="3">
            <b-form-group label="사용 여부" label-for="search-coching-tv-keyword-useYn">
              <b-form-radio-group
                id="search-coching-tv-keyword-useYn"
                v-model="listData.sc.useYn"
                buttons
                button-variant="outline-primary"
                @change="onSearch"
                :options="[
                  {text:'All', value: ''},{text:'Y', value: 'Y'},{text:'N', value: 'N'}
                ]"
              >
              </b-form-radio-group>
            </b-form-group>
          </b-col>

          <b-col cols="6" md="3">
            <b-form-group label="숨김 여부" label-for="search-coching-tv-keyword-delYn">
              <b-form-radio-group
                id="search-coching-tv-keyword-delYn"
                v-model="listData.sc.delYn"
                buttons
                button-variant="outline-primary"
                @change="onSearch"
                :options="[
                  {text:'All', value: ''},{text:'Y', value: 'Y'},{text:'N', value: 'N'}
                ]"
              >
              </b-form-radio-group>
            </b-form-group>
          </b-col> -->

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
        :rowHeight="200"
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
  getSearchTvList,
} from "@/api/coching-bo/search/esSearch";

import {
  getEsCochingTvIndices,
} from "@/api/coching-bo/search/esCochingTvManager";

import ButtonCellRenderer from "@/components/ernsAgGrid/ButtonCellRenderer.vue";
import YoutubeCellRenderer from "@/components/ernsAgGrid/YoutubeCellRenderer.vue";

import cochingUtils from "@/components/mixins/cochingUtils";

const GRID_ID_MAIN = 'main-grid';

export default {
  mixins: [ernsUtils, ernsAgGrid, cochingUtils],
  components: {
    vSelect,
    draggable, 
    AgGridVue,    
    ButtonCellRenderer,
    YoutubeCellRenderer,
  },
  directives: {
    Ripple,
  },
  props: {},
  computed: {},
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
      { headerName: "아이디"        , field: "tvSeq"        , width: 70  , cellClass: 'flex-center',
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
        cellStyle: { "white-space": "normal" } // 여러 줄로 표시
      },
      { headerName: "YouTube"   , field: "ytUrl"   , width: 400 //, cellClass: 'text-center'
          ,
          cellRenderer: "YoutubeCellRenderer", // 렌더러 지정
          cellRendererParams: {
            width: 300,
            height: 200
          },
          cellClass: (params) => {
            const retClass = [
              'd-flex'
              , 'align-items-center'
              , 'justify-content-center'
            ];          
            return retClass;
          }
      },
      { headerName: "제목"          , field: "title"      , width: 200  , cellClass: 'flex-left',
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
        cellStyle: { "white-space": "normal" } // 여러 줄로 표시
      },
      { headerName: "조회수"      , field: "views"        , width: 100  , cellClass: 'flex-right',
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
        cellStyle: { "white-space": "normal" } // 여러 줄로 표시
      },
      { headerName: "해시테그"    , field: "hashtag"     , width: 150  , cellClass: 'flex-left'},      
      { headerName: "등록일"      , field: "rgtDttm"     , width: 150  , cellClass: 'flex-center'
        , valueFormatter: (params) => params.value ? this.$options.filters.eFmtDateTime(params.value) : '-'
      },
      { headerName: "내용"        , field: "content"     , width: 350  , cellClass: 'flex-left',
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
        cellStyle: { "white-space": "normal" } // 여러 줄로 표시
      },
    ];

    return {
      esIndex : {
        selectedOption: '',
        options: [],
        // selectedOption: 'dev_coching_tv_20250610175259',
        // options: [
        //   { value: 'dev_coching_tv_20250610175259', label: 'dev_coching_tv_20250610175259' },          
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
          YoutubeCellRenderer,
        },

        sc: { ...DEF_SEARCH_OPT.sc },
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
    async onSearch() {
      const _vm = this;
      await _vm.loadList(1);
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
        const res = await getSearchTvList(params);
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
      const esIndex = _vm.esIndex;

      const params = {
        page: pInfo.curPage,
        rowSize: pInfo.perPage,

        index : esIndex.selectedOption,

        text: searchOp.searchText,
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
          commands : "/coching_tv*?format=json"
        };

        //데이터 로드
        const res = await getEsCochingTvIndices(params);
        
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
  min-height: calc(100vh - 360px);  
}
</style>
