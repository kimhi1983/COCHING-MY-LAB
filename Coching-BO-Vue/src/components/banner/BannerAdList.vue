<template>
  <div class="cm-banner-wrap">

    <!-- bannerMstCd:{{ bannerMstCd }} -->

    <!-- 검색조건 -->

    <b-card no-body>
      <b-card-body>
        <b-row>

          <b-col cols="12" md="6">
            <b-form-group label="제목" label-for="search-banner-titleL">
              <b-input-group>
                <b-form-input
                  id="search-banner-titleL"
                  v-model="listData.sc.titleL"
                  @keyup.enter="onSearch"
                  placeholder="제목을 검색하세요."
                ></b-form-input>                
                <b-input-group-append>
                  <b-button variant="outline-primary" 
                    @click="onSearch">검색</b-button>
                </b-input-group-append>
              </b-input-group>
              <button v-if="listData.sc.titleL" class="search-input-clear-btn"
               @click="listData.sc.titleL = ''">✖</button>
            </b-form-group>
          </b-col>

          <b-col cols="12" md="6" >
            <div class="erns-filter-div mt-2 text-right">
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
            <b-form-group label="사용 여부" label-for="search-banner-useYn">
              <b-form-radio-group
                id="search-banner-useYn"
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
            <b-form-group label="노출 여부" label-for="search-banner-delYn">
              <b-form-radio-group
                id="search-banner-delYn"
                v-model="listData.sc.dispYn"
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

      <!-- <button @click="ernsAgGrid_showModifiedCells(listData.gridId)">main-grid 변경된 셀 확인</button> -->
      <!-- <button @click="ernsAgGrid_resetToOriginal(listData.gridId)">main-grid 원본 데이터로 복구</button> -->

      <!-- listData.rowData:{{ listData.rowData }} -->
      <!-- listData.gridOptions.isEditData:{{ listData.gridOptions.isEditData }} -->
    
    </b-card>
    
  </div>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import Ripple from 'vue-ripple-directive'
import draggable from 'vuedraggable'
import "ag-grid-community/styles/ag-grid.css"; // Mandatory CSS required by the Data Grid
import "ag-grid-community/styles/ag-theme-quartz.css";

import { AgGridVue } from "ag-grid-vue";
import ernsAgGrid from '@/components/ernsAgGrid/ernsAgGrid';
import { AG_GRID_COMMON_COLUMN_DEFS } from '@/constants/agGrid';

import {getCodes} from '@/api/coching-bo/comm/code';
import {getBannerMaster} from '@/api/coching-bo/system/bannerMaster';
import {
  getBannerAdList,
  delBannerAds,
  setBannerAdOrder,  
  setBannerAdState
} from '@/api/coching-bo/comm/banner';
import bannerMixin from '@/components/banner/bannerMixin';
import { DEF_SEARCH_OPT, BANNER_MODE } from '@/constants/banner';

import ButtonCellRenderer from "@/components/ernsAgGrid/ButtonCellRenderer.vue";
import ToogleButtonCellRenderer from "@/components/ernsAgGrid/ToogleButtonCellRenderer.vue";

const GRID_ID_MAIN = 'main-grid';

export default {
  name: 'coching-banner-ad-list',
  mixins: [ernsUtils, ernsAgGrid, bannerMixin],
  components : {
    draggable,
    AgGridVue,
    ButtonCellRenderer,
    ToogleButtonCellRenderer,
  },
  directives: {
    Ripple
  },
  computed : {
    showRows(){
      return this.$store.state.erns.showRows;
    }
  },
  watch: {},
  props:{
    bannerMstCd: {
      type: String,
      require: true
    }
  },
  async mounted(){
    const _vm = this;
    _vm.getInitParam();
    await _vm.loadBannerMaster();

    await _vm.loadList(_vm.listData.pi.curPage);
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
          pi : _vm.list.pi
        }
      };
      _vm.eumSetRouteHistoryParam(data);
    }catch(err){
      console.error(err);
    }
  
    return next();
  },
  data(){
    const _vm = this;

    // Ag-Grid Column Definition
    const AG_GRID_COLUMN_DEFS = [
      { headerName: "", field: "drag", rowDrag: true, width: 50 }, // 드래그 컬럼 추가
      { hide: true,
        headerName: "Row ID", field: "rowId"          , width: 70 
        , valueGetter: (params) => params.node.id // 내부 rowId 출력
      },
      { headerName: "번호"        , field: "index"    , width: 70    , cellClass: 'flex-center'},
      { headerName: "제목"      , field: "title", width: 300  , cellClass: 'flex-left'},
      { headerName: "내용"      , field: "content", width: 300   , cellClass: 'flex-left'},
      { headerName: "정렬"        , field: "sortOrd"   , width: 100  , cellClass: 'flex-center'},
      { headerName: "노출기간"    , field: "bannerDate", width: 300   , cellClass: 'flex-center'},
      { headerName: "노출"      , field: "dispYn"     , width: 120  //, cellClass: 'flex-center'
          ,editable: true,
          cellRenderer: "ToogleButtonCellRenderer", // 렌더러 지정
          cellRendererParams: {
            field : 'dispYn',
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
      { headerName: "사용"       , field: "useYn"      , width: 120  //, cellClass: 'flex-center'
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
        cellRenderer: "ButtonCellRenderer", // 렌더러 지정
        cellRendererParams: {
          label: "저장",
          variant : "outline-primary",
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
        headerName: "삭제"      , field: "delButton"  , width: 100  , cellClass: 'flex-center',
        cellRenderer: "ButtonCellRenderer", // 렌더러 지정
        cellRendererParams: {
          label: "삭제",
          variant : "primary",          
          action: (params) => {
            this.onDeleteRow(params);
          },
        },
      },      
      { headerName: "수정자"    , field: "membId"     , width: 150  , cellClass: 'flex-center'},
      { headerName: "최종 수정일", field: "chngDttm"   , width: 200  , cellClass: 'flex-center'},
      
      
      { hide : true,
        headerName: "수정여부"  , field: "_isEditRow" , width: 100  , cellClass: 'flex-center'},      
    ];

    return {      
      bannerMaster : {},

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

        sc: { ...DEF_SEARCH_OPT.sc, bannerMstCd : _vm.bannerMstCd },
        pi: { ...DEF_SEARCH_OPT.pi },
        rowData: [],
        rawList: [],        
      },
    }
  },  
  methods : {
    //$route.query, $route.params, HistoryParam 등으로 초기값 설정
    getInitParam() {
      const _vm = this;      
      const data = this.eumGetRouteHistoryParam();

      if(data && data.initParam){
        _vm.listData.sc = {...data.initParam.sc};
        _vm.listData.pi = {...data.initParam.pi};
      }
    },

    //Cell 클릭 이벤트
    onCellClicked(event){
      const _vm = this;

      // console.debug(event);
      const item = event.node.data;
      const clickField = event.colDef.field;

      //상세이동
      const goDetailFileds = ["title", "sortOrd", "bannerDate", "membId", "chngDttm", "content"];
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
      const checkFields = ["_isEditRow", "title"];
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
      if("" == (rowData.title || '').trim()) {
        return false; //제목 필수
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
    onSearch(){
      const _vm = this;
      _vm.loadList(1);   
    },

    //등록
    onClickRegster(){
      const _vm = this;
      _vm.goWrite();
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
          bannerSeq : rowNode.data.bannerSeq || 0,
          bannerMstCd : _vm.bannerMstCd,
          useYn : rowNode.data.useYn,
          dispYn : rowNode.data.dispYn,
        });
      });

      //TODO : Validation
      if(params.list.length <= 0){
        _vm.alertError("변경된 정보가 없습니다.");
        return;
      }

      
      const result = await _vm.$swal({
        title: '확인',
        text: `${params.list.length}개 배너 설정을 저장 하시겠습니까?`,
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
          await setBannerAdState(params);
          await _vm.onSearch();
          _vm.alertSuccess("배너 설정 값이 변경되었습니다.");
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
        isRegMode: (gridParams.data.bannerSeq || 0) <= 0,
        bannerMstCd : _vm.bannerMstCd,
        bannerSeq : gridParams.data.bannerSeq || 0,
        useYn: (gridParams.data.useYn || '').trim(),
        dispYn: (gridParams.data.dispYn || '').trim(),        
      };

      //Validation
      if(!params.bannerSeq){
        return;
      }

      const result = await _vm.$swal({
        title: '확인',
        text: `배너 상태를 ${params.isRegMode ? '등록': '변경'} 하시겠습니까?`,
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
          await setBannerAdState({list:[params]});
          await _vm.onSearch();
          _vm.alertSuccess("배너 상태가 변경되었습니다.");
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
        isRegMode: gridParams.data.bannerSeq <= 0,
        bannerMstCd : _vm.bannerMstCd,
        bannerSeq : gridParams.data.bannerSeq || 0, 
        delYn : 'Y'       
      };

      if(params.isRegMode){
        _vm.ernsAgGrid_deleteRow(_vm.listData.gridId, gridParams.node.id);
        return;
      }

      //Validation
      if(!params.bannerSeq){
        return;
      }

      const result = await _vm.$swal({
        title: '확인',
        text: `배너를 삭제 하시겠습니까?`,
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
          await delBannerAds({list:[params]});
          await _vm.onSearch();
          _vm.alertSuccess("배너가 삭제 되었습니다.");
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
          bannerMstCd : _vm.bannerMstCd,
          bannerSeq : row.data.bannerSeq,
          sortOrd : ++count
        });        
      }

      const result = await _vm.$swal({
        title: '확인',
        text: `배너 순서를 저장 하시겠습니까?`,
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
          await setBannerAdOrder(params);
          await _vm.onSearch();
          _vm.alertSuccess("배너 순서가 저장 되었습니다.");
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

    //목록로드
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

        const res = await getBannerAdList(params); 
        const { pageInfo, list, sc} = res.resultData;

        listData.pi = {
          ...listData.pi,
          curPage: pageInfo.currentPage,
          totalItem: pageInfo.totalItem,
          perPage: pageInfo.pageItemSize,
        };

        listData.rawList = list.map((item, index) => {
          let bannerDate = "-";
          if(item.fromDate && item.toDate){
            bannerDate = item.fromDate.concat(" ~ ", item.toDate);
          }
          if(item.fromDate && !item.toDate){
            bannerDate = item.fromDate;
          }
          if(!item.fromDate && item.toDate){
            bannerDate = item.toDate;
          }

          return {
            rowIndex : listData.nextRowIndex++, //rowIndex 설정
            index: _vm.$options.filters.eufRowNumberForPageInfo(index, pInfo),
            _isEditRow : false,
            ...item,
            bannerDate,
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

        //배너 아이디
        bannerMstCd : searchOp.bannerMstCd,
        useYn : searchOp.useYn,
        delYn : searchOp.delYn,
        dispYn : searchOp.dispYn,

        titleL : searchOp.titleL,
      }
    },

    //데이터 로드(마스터)
    async loadBannerMaster(){
      const _vm = this; 
      
      const params = {      
        bannerMstCd : _vm.bannerMstCd
      };

      const res = await getBannerMaster(params);
      _vm.bannerMaster = {
        ...res.resultData
      }
    },
  }
}
</script>

<style lang="scss" scoped>
  th[role=columnheader] div{
    white-space:nowrap;
  }

  .erns-ag-grid-vue{
    min-height: calc(100vh - 500px);  
  }
</style>