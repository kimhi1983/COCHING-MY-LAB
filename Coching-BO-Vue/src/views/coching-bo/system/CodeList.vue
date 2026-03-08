<template>
  <div>
    <!-- 검색조건 -->
    <b-card no-body>
      <b-card-body class="sys-code-list-filter">
        <b-row>
          <b-col cols="12" md="3" class="mb-md-0">
            <b-form-group label="언어" label-for="sys-code-list-etc5">
              <v-select
                id="sys-code-list-etc5"
                v-model="listData.sc.etc5"
                :options="codes.ETC5_CODE" :reduce="op => op.value"
                :dir="$store.state.appConfig.isRTL ? 'rtl' : 'ltr'"
                :class="['w-100', 'select-size-sm', 'select-placeholder-gray', { 'has-value': listData.sc.etc5 }]"              
                @option:selected="loadList(1)"
              >
                <span slot="no-options">검색된 항목이 없습니다.</span>
              </v-select>
            </b-form-group>
          </b-col> 

          <b-col cols="12" md="3">
            <b-form-group
              label="검색" label-for="sys-code-list-search-text">
              <b-input-group>
                <b-form-input 
                  id="sys-code-list-search-text"
                  v-model="listData.sc.searchText"
                  @keyup.enter="loadList(1)"
                  placeholder="코드, 코드명을 검색하세요."
                  size="sm"
                ></b-form-input>
                <b-input-group-append>
                  <b-button 
                    @click="loadList(1)"
                    variant="outline-primary" size="sm" 
                    v-b-tooltip.hover
                    title="검색"                    
                    ><feather-icon icon="SearchIcon" size="14" />                  
                  </b-button>      
                </b-input-group-append>
              </b-input-group>
            </b-form-group>
          </b-col>

          <b-col cols="12" md="6">
            <div class="erns-filter-div mt-1 text-right">
              <b-form-group label="">
                <b-button
                  :disabled="!listData.gridOptions.isEditData"
                  v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                  :variant="listData.isDisabledAllSave ? 'outline-secondary' : 'secondary'"
                  class="mr-1"
                  size="sm"
                  v-b-tooltip.hover
                  title="변경사항 초기화"
                  @click="onClickResetFilter"
                  ><feather-icon icon="RefreshCcwIcon" size="14" />
                </b-button>

                <b-button
                  :disabled="listData.isDisabledAllSave"
                  v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                  :variant="listData.isDisabledAllSave ? 'outline-primary' : 'primary'"
                  class="mr-1"
                  @click="onClickMultpleSave"
                  size="sm"
                  v-b-tooltip.hover
                  title="저장"
                  ><feather-icon icon="SaveIcon" size="14" />
                </b-button>

                <b-button
                  v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                  variant="outline-primary"
                  class=""
                  @click="onClickSaveOrder()"
                  size="sm"
                  v-b-tooltip.hover
                  title="순서 저장"
                  ><feather-icon icon="BarChartIcon" size="14" />
                </b-button>

              </b-form-group>
            </div>
          </b-col>
          
        </b-row>
      </b-card-body>
    </b-card>  
    <!-- // 검색조건 -->

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

    <!-- // 목록 -->

    <!-- 버튼 영역 -->
    <div class="text-center mt-4">
      <b-button
        v-ripple.400="'rgba(113, 102, 240, 0.15)'"
        variant="outline-primary"
        @click="onClickList"
      > 목록
      </b-button>
    </div>
  
  </div>
</template>
<script>
import {
  getCodes, 
  setCodeState,
  setCodeOrder
} from '@/api/coching-bo/comm/code';

import ernsUtils from '@/components/mixins/ernsUtils';
import draggable from 'vuedraggable'

import "ag-grid-community/styles/ag-grid.css"; // Mandatory CSS required by the Data Grid
import "ag-grid-community/styles/ag-theme-quartz.css";

import { AgGridVue } from "ag-grid-vue";
import ernsAgGrid from '@/components/ernsAgGrid/ernsAgGrid';
import { AG_GRID_COMMON_COLUMN_DEFS } from '@/constants/agGrid';

import { DEF_SEARCH_OPT } from '@/constants/code';

import ButtonCellRenderer from "@/components/ernsAgGrid/ButtonCellRenderer.vue";
import ToogleButtonCellRenderer from "@/components/ernsAgGrid/ToogleButtonCellRenderer.vue";
import IconButtonCellRenderer from "@/components/ernsAgGrid/IconButtonCellRenderer.vue";

const GRID_ID_MAIN = 'main-grid';

import Ripple from 'vue-ripple-directive';
import vSelect from 'vue-select';

const ECT5_CODES = [
  {label:'전체', value:''},
  {label:'ko', value:'ko'},
  {label:'en', value:'en'}
];

export default {
  name: 'coching-BackOffice-System-Code-list',
  mixins: [ernsUtils, ernsAgGrid],
  components : {
    draggable,
    vSelect,
    AgGridVue,
    ButtonCellRenderer,
    ToogleButtonCellRenderer,
    IconButtonCellRenderer,
  },
  directives: {
    Ripple
  },
  props: {
  },
  filters :{
  },
  computed: {
    showRows(){
      return this.$store.state.erns.showRows;
    }  
  },
  data(){
    const _vm = this;

    const AG_GRID_COLUMN_DEFS = [
      { headerName: "", field: "drag", rowDrag: true, width: 50 }, // 드래그 컬럼 추가
      { hide: true,
        headerName: "Row ID", field: "rowId"          , width: 70 
        , valueGetter: (params) => params.node.id // 내부 rowId 출력
      },

      { headerName: "번호", field: "index", width: 70    , cellClass: 'flex-center'},
      { headerName: "코드", field: "code", width: 100    , cellClass: 'flex-center'},
      { headerName: "그룹코드", field: "grpCode", width: 100, cellClass: 'flex-center' },
      { headerName: "코드명", field: "codeName", width: 250, cellClass: 'flex-left' },
      { headerName: "정렬순서", field: "sortOrd", width: 100, cellClass: 'flex-center' },
      { headerName: "설명" , field: "codeDesc"   , width: 150
        , editable: true
        , cellRenderer: (params) => { //Placeholder 느낌
          return params.value ? params.value : "<span style='color: gray;'>설명을 입력해 주세요.</span>";
        }
        , cellClass: (params) => {
          const retClass = ['flex-left'];
          const editData = _vm.ernsAgGrid_getModifiedCells(_vm.listData.gridId, params.node.id, params.colDef.field);
          if(editData){
            retClass.push('erns-agGrid-edit-cell');
          }

          return retClass;
        }
      },
      { headerName: "etc1" , field: "etc1"   , width: 100
        , editable: true
        , cellRenderer: (params) => { //Placeholder 느낌
          return params.value ? params.value : "<span style='color: gray;'>etc1을 입력해 주세요.</span>";
        }
        , cellClass: (params) => {
          const retClass = ['flex-left'];
          const editData = _vm.ernsAgGrid_getModifiedCells(_vm.listData.gridId, params.node.id, params.colDef.field);
          if(editData){
        retClass.push('erns-agGrid-edit-cell');
          }

          return retClass;
        }
      },
      { headerName: "etc2" , field: "etc2"   , width: 100
        , editable: true
        , cellClass: (params) => {
          const retClass = ['flex-left'];
          const editData = _vm.ernsAgGrid_getModifiedCells(_vm.listData.gridId, params.node.id, params.colDef.field);
          if(editData){
            retClass.push('erns-agGrid-edit-cell');
          }

          return retClass;
        }
      },
      { headerName: "etc3" , field: "etc3"   , width: 100
        , editable: true
        , cellClass: (params) => {
          const retClass = ['flex-left'];
          const editData = _vm.ernsAgGrid_getModifiedCells(_vm.listData.gridId, params.node.id, params.colDef.field);
          if(editData){
            retClass.push('erns-agGrid-edit-cell');
          }

          return retClass;
        }
      },
      { headerName: "etc4" , field: "etc4"   , width: 100
        , editable: true
        , cellClass: (params) => {
          const retClass = ['flex-left'];
          const editData = _vm.ernsAgGrid_getModifiedCells(_vm.listData.gridId, params.node.id, params.colDef.field);
          if(editData){
            retClass.push('erns-agGrid-edit-cell');
          }

          return retClass;
        }
      },
      { headerName: "etc5" , field: "etc5"   , width: 100
        , editable: true
        , cellClass: (params) => {
          const retClass = ['flex-left'];
          const editData = _vm.ernsAgGrid_getModifiedCells(_vm.listData.gridId, params.node.id, params.colDef.field);
          if(editData){
            retClass.push('erns-agGrid-edit-cell');
          }

          return retClass;
        }
      },
      { headerName: "기타설명" , field: "etcCmmt"   , width: 150
        , editable: true
        , cellClass: (params) => {
          const retClass = ['flex-left'];
          const editData = _vm.ernsAgGrid_getModifiedCells(_vm.listData.gridId, params.node.id, params.colDef.field);
          if(editData){
            retClass.push('erns-agGrid-edit-cell');
          }

          return retClass;
        }
      },
      { headerName: "비고" , field: "remarks"   , width: 150
        , editable: true
        , cellClass: (params) => {
          const retClass = ['flex-left'];
          const editData = _vm.ernsAgGrid_getModifiedCells(_vm.listData.gridId, params.node.id, params.colDef.field);
          if(editData){
            retClass.push('erns-agGrid-edit-cell');
          }
          return retClass;
        }
      },
      
      { headerName: "사용", field: "useYn", width: 100
          ,editable: true,
          cellRenderer: "ToogleButtonCellRenderer", // 렌더러 지정
          cellRendererParams: {
            field : 'useYn',
            size: "md",
            variant : "primary",
            checkValue : "Y", uncheckValue: "N",
            checkEditRow : false,            
          },
          cellClass: (params) => {
          const retClass = ['d-flex', 'align-items-center', 'justify-content-center'];
          const editData = _vm.ernsAgGrid_getModifiedCells(_vm.listData.gridId, params.node.id, params.colDef.field);
          if(editData){
            retClass.push('erns-agGrid-edit-cell');
          }

          return retClass;
        }
      },
      {
        headerName: "저장"      , field: "setButton"  , width: 100  , cellClass: 'flex-center',
        cellRenderer: "IconButtonCellRenderer", // 렌더러 지정
        cellRendererParams: {
          icon: "SaveIcon",
          tooltip: "저장",
          iconSize: "20",
          variant : " ",
          btnClass: 'btn-outline-primary btn-no-border',
          isDisabled: (params) => {
            if(!this.validateRowValue(params.data)){
              return true;
            }
            return !params.data["_isEditRow"];
          },
          action: (params) => {
            this.onApplyValue(params);
          },
        },
      },

      { headerName: "최종수정일"        , field: "chngDttm", width: 150    , cellClass: 'flex-center'},

      { hide : true,
        headerName: "수정여부"  , field: "_isEditRow" , width: 100  , cellClass: 'text-center'},
    ];


    return {
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

        sc: { ...DEF_SEARCH_OPT.sc },
        pi: { ...DEF_SEARCH_OPT.pi },
        rowData: [],
        rawList: [],        
      },

      items: [],
      codes : {
        ETC5_CODE : [...ECT5_CODES],
      },
      
    }
  },
  watch: {
    // 라우트가 변경되면 메소드를 다시 호출됩니다.
    '$route': 'fetchData'
  },
  mounted(){
    const _vm = this;
    _vm.fetchData(1);
    
  },
  beforeDestroy(){
    const _vm = this;
	},
  methods: {

    //agGrid셀변경시 처리
    onCellValueChanged(params) {
      const _vm = this;

      const listData = _vm.listData;

      //변경사항 저장
      _vm.ernsAgGrid_trackCellChanges(listData.gridId, params);

      //Cell 랜더러에게 변경 noti
      const checkFields = ["_isEditRow", "etc1", "etc2", "etc3", "etc4", "etc5", "etcCmmt", "remarks"];
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
      /* 예시
      if("" == (rowData.popupName || '').trim()) {
        return false; //팝업명 필수
      }
      */
      
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

    //다중 - 등록 / 수정
    async onClickMultpleSave(){
      const _vm = this;
      const params = {
        list : []
      };

      const listData = _vm.listData;
      _vm.ernsAgGrid_stopEditing(listData.gridId);
      const editCells = _vm.ernsAgGrid_getModifiedCells(listData.gridId);
      Object.entries(editCells).forEach(([rowId, editCellInfo]) => {
        const rowNode = _vm.ernsAgGrid_getRowNode(listData.gridId, rowId);
        params.list.push({
          code : rowNode.data.code || 0,
          grpCode: rowNode.data.grpCode || 0,
          useYn : rowNode.data.useYn,

          codeDesc : rowNode.data.codeDesc,
          etc1 : rowNode.data.etc1,
          etc2 : rowNode.data.etc2,
          etc3 : rowNode.data.etc3,
          etc4 : rowNode.data.etc4,
          etc5 : rowNode.data.etc5,
          etcCmmt : rowNode.data.etcCmmt,
          remarks : rowNode.data.remarks,
        });
      });

      //TODO : Validation
      if(params.list.length <= 0){
        _vm.alertError("변경된 정보가 없습니다.");
        return;
      }

      const result = await _vm.$swal({
        title: '확인',
        text: `${params.list.length}개 코드 설정을 저장 하시겠습니까?`,
        showCancelButton: true,
        confirmButtonText: `저장`,
        customClass: {
          confirmButton: 'btn btn-danger ml-1',
          cancelButton: 'btn btn-outline-primary',
        },
        buttonsStyling: false,
      });

      _vm.loading(true);
      try {        

        if(result.value){
          await setCodeState(params);
          await _vm.onSearch();
          _vm.alertSuccess("코드 설정 값이 변경되었습니다.");
        }
      } catch (err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
    },

    //상태 변경
    async onApplyValue(gridParams){
      const _vm = this;

      const params = {
        isRegMode: (gridParams.data.code || 0) <= 0,
        code: gridParams.data.code || 0,
        grpCode: gridParams.data.grpCode || 0,
        useYn: (gridParams.data.useYn || '').trim(),    
        
        codeDesc : gridParams.data.codeDesc,
        etc1 : gridParams.data.etc1,
        etc2 : gridParams.data.etc2,
        etc3 : gridParams.data.etc3,
        etc4 : gridParams.data.etc4,
        etc5 : gridParams.data.etc5,
        etcCmmt : gridParams.data.etcCmmt,
        remarks : gridParams.data.remarks,
      };

      //Validation
      if(!params.code || !params.grpCode){
        return;
      }

      const result = await _vm.$swal({
        title: '확인',
        text: `코드 상태를 ${params.isRegMode ? '등록': '변경'} 하시겠습니까?`,
        showCancelButton: true,
        confirmButtonText: `${params.isRegMode ? '등록': '변경'}`,
        customClass: {
          confirmButton: 'btn btn-danger ml-1',
          cancelButton: 'btn btn-outline-primary',
        },
        buttonsStyling: false,
      });

      _vm.loading(true);
      try {        

        if(result.value){
          await setCodeState({list:[params]});
          await _vm.onSearch();
          _vm.alertSuccess("코드 상태가 변경되었습니다.");
        }
      } catch (err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
    },

    //순서저장
    async onClickSaveOrder(){
      const _vm = this;
      const listData = _vm.listData;
      const gridRef = _vm.$refs[listData.gridId];

      const params = {
        list : []
      };

      const allRows = gridRef.api.getModel().rowsToDisplay; // 전체 row 배열 가져오기
      let count = 0;
      for (const row of allRows) {
        params.list.push({
          code : row.data.code,
          grpCode : row.data.grpCode,
          sortOrd : ++count
        });        
      }

      const result = await _vm.$swal({
        title: '확인',
        text: `코드 순서를 저장 하시겠습니까?`,
        showCancelButton: true,
        confirmButtonText: `저장`,
        customClass: {
          confirmButton: 'btn btn-danger ml-1',
          cancelButton: 'btn btn-outline-primary',
        },
        buttonsStyling: false,
      });

      _vm.loading(true);
      try {        

        if(result.value){
          await setCodeOrder(params);
          await _vm.onSearch();
          _vm.alertSuccess("코드 순서가 저장 되었습니다.");
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

      const listData = _vm.listData, pInfo = _vm.listData.pi, searchOp = _vm.listData.sc;      
      await _vm.loadList(pInfo.curPage);
    },

    getInitParam() {
      const _vm = this;      
      const query = _vm.$route.query, params = _vm.$route.params;      
      if(query){
        _vm.listData.sc = {
          ..._vm.listData.sc,
          grpCode: query.grpCode
        }
      }
    },
    async fetchData() {
      const _vm = this;
      await _vm.getInitParam();
      await _vm.loadList(1);
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
        const res = await getCodes(params); 

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

        // 전체 저장 버튼 상태 초기화
        listData.isDisabledAllSave = _vm.checkIsDisabledAllSave();

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

        grpCode: searchOp.grpCode,
        etc5 : searchOp.etc5,
        searchText: searchOp.searchText
      }
    },

    onClickList() {
      const _vm = this;
      _vm.$router.back();
    }
  }  
}
</script>

<style lang="scss" scoped>
.erns-ag-grid-vue{
  min-height: calc(100vh - 500px);
}
</style>
<style lang="scss">
.sys-vehicle-filter1{
  .v-select{
    .vs__dropdown-toggle{
      height: 2.142rem;
    }
  }
}
</style>
