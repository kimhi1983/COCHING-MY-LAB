<template>
  <div>
    <!-- {{vPopup}} -->
    
    <!-- 검색조건 -->
    <b-card no-body>
      <b-card-body class="cm-main-filter">
        <b-row>
          <b-col cols="12" md="3"
            class="mb-md-0 mb-2"
          >
            <b-form-group label="검색조건" label-for="sys-popup-master-search-option">
              <v-select
                id="sys-popup-master-search-option"
                v-model="selectedOption"
                :options="options" :reduce="op => op.value"
                :dir="$store.state.appConfig.isRTL ? 'rtl' : 'ltr'"
                :class="['w-100', 'select-size-sm', 'select-placeholder-gray', { 'has-value': selectedOption }]"              
                @option:selected="onSearch()"
                placeholder="검색조건을 선택하세요"
              >
                <span slot="no-options">검색된 항목이 없습니다.</span>
              </v-select>
            </b-form-group>
          </b-col>

          <b-col cols="12" md="6">
            <b-form-group label="키워드" label-for="sys-popup-master-search-keyword">
              <b-input-group>
                <b-form-input v-model="listData.sc.srchData"
                  @keyup.enter="onSearch"
                  size="sm"
                  placeholder="키워드를 입력하세요."
                ></b-form-input>
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

          <b-col cols="12" md="3">
            <div class="erns-filter-div mt-2 text-right">
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
                  class=""
                  size="sm"
                  v-b-tooltip.hover
                  title="저장"
                  @click="onClickMultpleSave"
                  ><feather-icon icon="SaveIcon" size="14" />
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

      <!--검색 필터
      <div class="m-2">
      </div>
      -->
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
import {getCodes} from '@/api/coching-bo/comm/code';
import {getPopupMasterList, setPopupMasterState} from '@/api/coching-bo/system/popupMaster';
import vSelect from 'vue-select';

import ernsUtils from '@/components/mixins/ernsUtils';
import Ripple from "vue-ripple-directive";

import "ag-grid-community/styles/ag-grid.css"; // Mandatory CSS required by the Data Grid
import "ag-grid-community/styles/ag-theme-quartz.css";

import { AgGridVue } from "ag-grid-vue";
import ernsAgGrid from '@/components/ernsAgGrid/ernsAgGrid';
import { AG_GRID_COMMON_COLUMN_DEFS } from '@/constants/agGrid';

import { DEF_SEARCH_OPT } from '@/constants/popup';

import ButtonCellRenderer from "@/components/ernsAgGrid/ButtonCellRenderer.vue";
import ToogleButtonCellRenderer from "@/components/ernsAgGrid/ToogleButtonCellRenderer.vue";
import IconButtonCellRenderer from "@/components/ernsAgGrid/IconButtonCellRenderer.vue";

const GRID_ID_MAIN = 'main-grid';

export default {
  name: 'erns-coching-BackOffice-System-Main-Popup',
  mixins: [ernsUtils, ernsAgGrid],
  components : {
    vSelect,
    AgGridVue,
    ButtonCellRenderer,
    ToogleButtonCellRenderer,
    IconButtonCellRenderer,
  },
  directives: {
    Ripple,
  },
  props: {

  },
  //페이지 이동전 처리
  beforeRouteLeave: function (to, from, next) {
    const _vm = this;
    try {
      const data = {
        initParam: {
          sc: _vm.listData.sc,
          pi: _vm.listData.pi,
        },
      };
      _vm.eumSetRouteHistoryParam(data);
    } catch (err) {
      console.error(err);
    }
    return next();
  },
  data(){
    const _vm = this;

    const AG_GRID_COLUMN_DEFS = [
      { hide: true,
        headerName: "Row ID", field: "rowId"          , width: 70 
        , valueGetter: (params) => params.node.id // 내부 rowId 출력
      },

      { headerName: "번호"        , field: "index", width: 70    , cellClass: 'flex-center'},
      { headerName: "분류"        , field: "popupType", width: 100    , cellClass: 'flex-center'},
      { headerName: "코드"        , field: "popupMstCd", width: 100    , cellClass: 'flex-center'},

      //{ headerName: "설명"        , field: "popupMstDesc", width: 350    , cellClass: 'flex-center'},
      { headerName: "설명" , field: "popupMstDesc"   , width: 400
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

      { headerName: "사용", field: "useYn", width: 120
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
        headerName: "저장"      , field: "setButton"  , width: 120  , cellClass: 'flex-center',
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
      
      code : {
        POPUP_TYPE : []
      },

      selectedOption: 'allSrch',
      options: [
        { value: 'allSrch', label: '전체' },
        { value: 'popupType', label: '분류' },
        { value: 'popupMstCd', label: '코드' },
        { value: 'popupMstDesc', label: '설명' },
      ],
    }
  },
  async mounted() {
    const _vm = this;

    await _vm.getInitParam();
    await _vm.loadList(_vm.listData.pi.curPage);
  },
  beforeDestroy(){
    const _vm = this;
	},
  methods: {

    getInitParam() {
      const _vm = this;      
      //const query = _vm.$route.query, params = _vm.$route.params;      
      const data = this.eumGetRouteHistoryParam();
      //console.info("onAfterRouter");console.info(data);
      if(data && data.initParam){
        _vm.listData.sc = {...data.initParam.sc};
        _vm.listData.pi = {...data.initParam.pi};

        const selected = _vm.listData.sc;
        if (selected.allSrch && selected.allSrch !== "") {
          _vm.selectedOption = "allSrch";
        } else if (selected.popupType && selected.popupType !== "") {
          _vm.selectedOption = "popupType";
        } else if (selected.popupMstCd && selected.popupMstCd !== "") {
          _vm.selectedOption = "popupMstCd";
        } else if (selected.popupMstDesc && selected.popupMstDesc !== "") {
          _vm.selectedOption = "popupMstDesc";
        } else {
          _vm.selectedOption = "allSrch";
        }
      }
    },

    //agGrid셀변경시 처리
    onCellValueChanged(params) {
      const _vm = this;

      const listData = _vm.listData;

      //변경사항 저장
      _vm.ernsAgGrid_trackCellChanges(listData.gridId, params);

      //Cell 랜더러에게 변경 noti
      const checkFields = ["_isEditRow", "popupMstDesc"];
      if (checkFields.includes(params.column.colId)) {   
        params.api.refreshCells({
          columns: ["setButton", "delButton"], // 새로고침
          rowNodes: [params.node],
          force : true
        });
      }

      listData.isDisabledAllSave = _vm.checkIsDisabledAllSave();
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

    //각 Row 필수값 체크
    validateRowValue(rowData){
      if("" == (rowData.popupMstDesc || '').trim()) {
        return false; //설명 필수
      }

      return true;
    },

    onSearch(){
      const _vm = this;
      const searchOp = _vm.listData.sc;

      searchOp.allSrch = "";
      searchOp.popupType = "";
      searchOp.popupMstCd = "";
      searchOp.popupMstDesc = "";

      if(_vm.selectedOption === "allSrch") {
        searchOp.allSrch = searchOp.srchData;
      } else if(_vm.selectedOption === "popupType") {
        searchOp.popupType = searchOp.srchData;
      } else if(_vm.selectedOption === "popupMstCd") {
        searchOp.popupMstCd = searchOp.srchData;
      } else if(_vm.selectedOption === "popupMstDesc") {
        searchOp.popupMstDesc = searchOp.srchData;
      }

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
          popupMstCd : rowNode.data.popupMstCd || 0,
          popupMstDesc : rowNode.data.popupMstDesc,
          useYn : rowNode.data.useYn,
        });
      });

      //TODO : Validation
      if(params.list.length <= 0){
        _vm.alertError("변경된 정보가 없습니다.");
        return;
      }
      
      const result = await _vm.$swal({
        title: '확인',
        text: `${params.list.length}개 팝업 마스터 설정을 저장 하시겠습니까?`,
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
          await setPopupMasterState(params);
          await _vm.onSearch();
          _vm.alertSuccess("팝업 마스터 설정 값이 변경되었습니다.");
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
        isRegMode: (gridParams.data.popupMstCd || 0) <= 0,
        popupMstCd: gridParams.data.popupMstCd || 0,
        popupMstDesc: gridParams.data.popupMstDesc,
        useYn: (gridParams.data.useYn || '').trim(),     
      };

      //Validation
      if(!params.popupMstCd){
        return;
      }

      const result = await _vm.$swal({
        title: '확인',
        text: `팝업 마스터 상태를 ${params.isRegMode ? '등록': '변경'} 하시겠습니까?`,
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
          await setPopupMasterState({list:[params]});
          await _vm.onSearch();
          _vm.alertSuccess("팝업 마스터 상태가 변경되었습니다.");
        }
      } catch (err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
    },

    async loadCodes(){
      const _vm = this;

      const rawTypeList = await getCodes({codeMst: 'POPUP_TYPE', rowSize: -1});

      _vm.code.POPUP_TYPE = rawTypeList.resultData.list.map(item=>{
        return {label:item.codeName, value:item.code};
      });
    },

    //검색 초기화
    async onClickResetFilter() {
      const _vm = this;

      const listData = _vm.listData, pInfo = _vm.listData.pi, searchOp = _vm.listData.sc;      
      await _vm.loadList(pInfo.curPage);
    },

    loadList(pageNo) {
      const _vm = this;
      _vm.apiList(_vm.listData.pi.curPage = pageNo || 1);
    },

    async apiList(pageNo){
      const _vm = this;      
      
      const listData = _vm.listData, pInfo = _vm.listData.pi, searchOp = _vm.listData.sc;

      pInfo.curPage = pageNo || 1;
      
      _vm.loading(true);

      try {
        listData.nextRowIndex = 1;
        listData.rowData = [];

        const params = _vm.getListSearchParam();

        //데이터 로드
        const res = await getPopupMasterList(params); 

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

        allSrch : searchOp.allSrch,
        popupTypeL : searchOp.popupType,
        popupMstCdL : searchOp.popupMstCd,
        popupMstDescL : searchOp.popupMstDesc,
      }
    },

    /*
    async apiList(pageNo) {
      const _vm = this;
      const vPopup = _vm.vPopup, pInfo = _vm.vPopup.pi, searchOp = _vm.vPopup.sc;

      const params = {
        page : pInfo.curPage,
        rowSize : pInfo.perPage
      }

      const res = await getPopupMasterList(params);
      const pageInfo = res.resultData.pageInfo;
      vPopup.rawList = res.resultData.list
      vPopup.pi = {
        ...vPopup.pi,
        curPage : pageInfo.currentPage,
        totalItem : pageInfo.totalItem,
        perPage : pageInfo.pageItemSize
      }

      console.log(vPopup)
    }
    */

  }
}
</script>

<style lang="scss" scoped>
.erns-ag-grid-vue{
  min-height: calc(100vh - 430px);
}
</style>