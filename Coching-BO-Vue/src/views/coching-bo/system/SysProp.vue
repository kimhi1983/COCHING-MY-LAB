<template>
  <div>
    <!-- 검색조건 -->

    <b-card no-body>
      <b-card-body>
        <b-row>

          <b-col cols="12" md="4">
            <b-form-group label="시스템 키" label-for="sys-listData-sc-sysKey">
              <b-input-group>
                <b-form-input
                  id="sys-listData-sc-sysKey"
                  v-model="listData.sc.sysKey"
                  @keyup.enter="loadList(1)"
                  placeholder="시스템 키를 입력하세요."
                  size="sm"
                ></b-form-input>
                <b-input-group-append>
                  <b-button @click="loadList(1)"
                    variant="outline-primary" size="sm" 
                    v-b-tooltip.hover
                    title="검색"
                    ><feather-icon icon="SearchIcon" size="14" />
                  </b-button>
                </b-input-group-append>
              </b-input-group>
              <button v-if="listData.sc.sysKey" class="search-input-clear-btn " @click="listData.sc.sysKey = ''">✖</button>
            </b-form-group>
          </b-col>

          <b-col cols="12" md="4">
            <b-form-group label="시스템 값" label-for="sys-listData-sc-sysValue">
              <b-input-group>
                <b-form-input
                  id="sys-listData-sc-sysValue"
                  v-model="listData.sc.sysValue"
                  @keyup.enter="loadList(1)"
                  placeholder="시스템 값을 입력하세요."
                  size="sm"
                ></b-form-input>
                <b-input-group-append>
                  <b-button @click="loadList(1)"
                    variant="outline-primary" size="sm" 
                    v-b-tooltip.hover
                    title="검색"
                    ><feather-icon icon="SearchIcon" size="14" />
                  </b-button>
                </b-input-group-append>
              </b-input-group>
              <button v-if="listData.sc.sysValue" class="search-input-clear-btn " @click="listData.sc.sysValue = ''">✖</button>
            </b-form-group>
          </b-col>


          <b-col cols="12" md="4" >
            <div class="erns-filter-div mt-1 text-right">
              <b-form-group label="">
                <b-button
                  :disabled="!listData.gridOptions.isEditData"
                  v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                  :variant="listData.isDisabledAllSave ? 'outline-secondary' : 'secondary'"
                  class="mr-1"
                  @click="onClickResetFilter"
                  size="sm"
                  v-b-tooltip.hover
                  title="변경사항 초기화"
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
                  variant="primary"
                  @click="onClickRegster()"
                  size="sm"
                  v-b-tooltip.hover
                  title="등록"
                  ><feather-icon icon="EditIcon" size="14" />
                </b-button>
              </b-form-group>
            </div>
          </b-col>
        </b-row>

      </b-card-body>
    </b-card>
    <!-- // 검색조건 -->

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
import Ripple from "vue-ripple-directive";
import draggable from 'vuedraggable';
import "ag-grid-community/styles/ag-grid.css"; // Mandatory CSS required by the Data Grid
import "ag-grid-community/styles/ag-theme-quartz.css";

import { AgGridVue } from "ag-grid-vue";

import { AG_GRID_COMMON_COLUMN_DEFS } from '@/constants/agGrid';
import { DEF_SEARCH_OPT, DEF_PARAM, DEF_ROW } from "@/constants/sysProp";
import {
  getSysPropList,
  getSysProp,
  setSysProps,
  delSysProps,
} from "@/api/coching-bo/system/sysProp";
import ernsAgGrid from '@/components/ernsAgGrid/ernsAgGrid';
import ButtonCellRenderer from "@/components/ernsAgGrid/ButtonCellRenderer.vue";
import IconButtonCellRenderer from "@/components/ernsAgGrid/IconButtonCellRenderer.vue";

const GRID_ID_MAIN = 'main-grid';

export default {
  mixins: [ernsUtils, ernsAgGrid],
  components: {
    draggable, 
    AgGridVue,    
    ButtonCellRenderer,
    IconButtonCellRenderer,
  },
  directives: {
    Ripple,
  },
  props: {},
  computed: {},
  filters: {},
  mounted() {
    const _vm = this;

    _vm.getInitParam();
    _vm.loadList(_vm.listData.pi.curPage);
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
      { headerName: "번호"      , field: "index"      , width: 70   , cellClass: 'text-center'},
      { headerName: "시스템 키" , field: "sysKey"     , width: 300  //, cellClass: 'text-left'
        , editable: (params) => params.data.rgtr <= 0 
        , cellRenderer: (params) => { //Placeholder 느낌
          return params.value ? params.value : "<span style='color: gray;'>시스템 키를 입력해 주세요.</span>";
        }
        , cellClass: (params) => {
          const retClass = ['text-left', 'selectable-text'];
          const editData = _vm.ernsAgGrid_getModifiedCells(_vm.listData.gridId, params.node.id, params.colDef.field);
          if(editData){
            retClass.push('erns-agGrid-edit-cell');
          }

          return retClass;
        }        
      },
      { headerName: "시스템 값" , field: "sysValue"   , width: 300  //, cellClass: 'text-left'
        , editable: true
        , cellRenderer: (params) => { //Placeholder 느낌
          return params.value ? params.value : "<span style='color: gray;'>시스템 값을 입력해 주세요.</span>";
        }
        , cellClass: (params) => {
          const retClass = ['text-left'];
          const editData = _vm.ernsAgGrid_getModifiedCells(_vm.listData.gridId, params.node.id, params.colDef.field);
          if(editData){
            retClass.push('erns-agGrid-edit-cell');
          }

          return retClass;
        }        
      },
      { headerName: "수정자"    , field: "membId"     , width: 150  , cellClass: 'text-center'},
      { headerName: "수정일"    , field: "chngDttm"   , width: 150  , cellClass: 'text-center'},
      {
        headerName: "저장"      , field: "setButton"  , width: 70  , cellClass: 'text-center',
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
      {
        headerName: "삭제"      , field: "delButton"  , width: 70  , cellClass: 'text-center',
        cellRenderer: "IconButtonCellRenderer", // 렌더러 지정
        cellRendererParams: {
          icon: "TrashIcon",
          tooltip: "삭제",
          iconSize: "20",
          variant : "btn-secondary btn-no-border ",
          action: (params) => {
            this.onDeleteRow(params);
          },
        },
      },
      { hide: true,
        headerName: "수정여부"  , field: "_isEditRow" , width: 150  , cellClass: 'text-center'},
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
        },

        sc: { ...DEF_SEARCH_OPT.sc },
        pi: { ...DEF_SEARCH_OPT.pi },
        rowData: [],
        rawList: [],
      },

      selected: false,
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
      const checkFields = ["_isEditRow", "sysKey", "sysValue"];
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
      if("" == (rowData.sysKey || '').trim()) {
        return false; //시스템 키 필수
      }
      if("" == (rowData.sysValue || '').trim()) {
        return false; //시스템 값 필수
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

    //등록
    onClickRegster(){
      const _vm = this;

      const listData = _vm.listData;
      _vm.$refs[listData.gridId].api.applyTransaction({ add: [{
        ...DEF_ROW
        , '_isEditRow': true
        , rowIndex : listData.nextRowIndex++
      }] });      
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
        console.log(`${rowId}: ${editCellInfo}`);

        const rowNode = _vm.ernsAgGrid_getRowNode(listData.gridId, rowId);
        params.list.push({
          sysKey : rowNode.data.sysKey, 
          sysValue : rowNode.data.sysValue
        });
      });

      console.debug(params);


      //TODO : Validation
      if(params.list.length <= 0){
        _vm.alertError("변경된 정보가 없습니다.");
        return;
      }
      
      const result = await _vm.$swal({
        title: '확인',
        text: `${params.list.length}개 시스템 값을 저장 하시겠습니까?`,
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
          await setSysProps(params);
          await _vm.loadList(1);
          _vm.alertSuccess("시스템 값이 변경되었습니다.");
        }
      } catch (err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
    },

    //등록 / 수정
    async onApplyValue(gridParams){
      const _vm = this;
      const params = {
        isRegMode: gridParams.data.rgtr <= 0,
        sysKey: (gridParams.data.sysKey || '').trim(),
        sysValue: (gridParams.data.sysValue || '').trim(),        
      };

      //Validation
      if(!params.sysKey || !params.sysValue){
        return;
      }

      const result = await _vm.$swal({
        title: '확인',
        text: `시스템 값을 ${params.isRegMode ? '등록': '변경'} 하시겠습니까?`,
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
          await setSysProps({list:[params]});
          await _vm.loadList(1);
          _vm.alertSuccess("시스템 값이 변경되었습니다.");
        }
      } catch (err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
    },

    // 삭제
    async onDeleteRow(gridParams){
      const _vm = this;
      const params = {
        isRegMode: gridParams.data.rgtr <= 0,
        sysKey: (gridParams.data.sysKey || '').trim(),
        sysValue: (gridParams.data.sysValue || '').trim(),        
      };

      if(params.isRegMode){
        _vm.ernsAgGrid_deleteRow(_vm.listData.gridId, gridParams.node.id);
        return;
      }

      //Validation
      if(!params.sysKey || !params.sysValue){
        return;
      }

      const result = await _vm.$swal({
        title: '확인',
        text: `시스템 값을 삭제 하시겠습니까?`,
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
          await delSysProps({list:[params]});
          await _vm.loadList(1);
          _vm.alertSuccess("시스템 값이 삭제 되었습니다.");
        }
      } catch (err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
    },

    //검색
    onSearch() {
      const _vm = this;
      _vm.loadList(1);
    },

    //검색 초기화
    async onClickResetFilter() {
      const _vm = this;
      _vm.listData.sc = { ...DEF_SEARCH_OPT.sc };
      _vm.listData.pi = { ...DEF_SEARCH_OPT.pi };
      await _vm.loadList(1);
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
        const res = await getSysPropList(params);
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

        sysKey: searchOp.sysKey,
        sysValue: searchOp.sysValue
      };

      return params;
    },    
  },
};
</script>

<style lang="scss" scoped>
.erns-ag-grid-vue{
  min-height: calc(100vh - 420px);
}
</style>
