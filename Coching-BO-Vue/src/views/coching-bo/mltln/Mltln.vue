<template>
  <div>
    <!-- 검색조건 -->
    <b-card no-body>
      <b-card-body class="sys-i18n-filter">
        <b-row>
          <b-col cols="12" md="2">
            <b-form-group label="검색조건" label-for="sys-i18n-search-option">
              <v-select
                v-model="selectedOption"
                :options="options" :reduce="op => op.value"
                :dir="$store.state.appConfig.isRTL ? 'rtl' : 'ltr'"
                :class="['w-100', 'select-size-sm', 'select-placeholder-gray', { 'has-value': selectedOption }]"
                placeholder="검색조건을 선택하세요"
                @option:selected="loadList(1)"                
              >
                <span slot="no-options">검색된 항목이 없습니다.</span>
              </v-select>
            </b-form-group>
          </b-col>

          <b-col cols="12" md="6" class="">
            <b-form-group label="키워드" label-for="sys-i18n-search-keyword">
              <b-input-group>
                <b-form-input 
                  id="sys-i18n-search-keyword"
                  v-model="listData.sc.srchData"
                  @keyup.enter="onSearch"
                  placeholder="키워드를 입력하세요"
                  size="sm"
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

          <b-col cols="12" md="4" class="">
            <div class="erns-filter-div mt-2 text-right">
               <b-button
                v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                variant="outline-primary"
                class="mr-1"
                @click="showImportModal"
                size="sm"
                v-b-tooltip.hover
                title="Import i18n"
                ><feather-icon icon="UploadIcon" size="14" />
              </b-button> 

              <b-button
                v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                variant="outline-primary"
                class="mr-1"
                @click="onClickExport"
                size="sm"
                v-b-tooltip.hover
                title="Export i18n"
                ><feather-icon icon="DownloadIcon" size="14" />
              </b-button>

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
      <!--
      <b-table
        class="position-relative" responsive striped hover
        :items="list.rawList"
        :fields="list.fields"
        ref="mltlnListTable"
        @row-clicked="onClickListRow"
        tbody-tr-class="table-click-row"
        primary-key="code"
        show-empty
        empty-text="검색된 다국어가 없습니다."
      >
   
        <template #cell(index)="data">
          {{ data.index | eufRowNumberForPageInfo(list.pi) }}
        </template>

        <template #cell(code)="data">
          {{ data.item.code }}
        </template>
      </b-table>

      <div class="mx-2 mb-2">
        <b-row>
          <b-col
            cols="12"
            sm="6"
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
              {{ eumPaginationRangeForPageInfo(list.pi) }}
            </span>
          </b-col>

          <b-col
            cols="12"
            sm="6"
            class="d-flex align-items-center justify-content-center justify-content-sm-end"
          >
            <b-pagination
              v-if="list.pi.totalItem > 0"
              v-model="list.pi.curPage"
              :total-rows="list.pi.totalItem"
              :per-page="list.pi.perPage"
              first-number
              last-number
              limit="10"
              class="mb-0 mt-1 mt-sm-0"
              prev-class="prev-item"
              next-class="next-item"
              @change="loadList"
            >
              <template #prev-text>
                <feather-icon icon="ChevronLeftIcon" size="18" />
              </template>
              <template #next-text>
                <feather-icon icon="ChevronRightIcon" size="18" />
              </template>
            </b-pagination>
          </b-col>
        </b-row>
      </div>
      -->
    </b-card>
    <!-- // 목록 -->

    <!-- import excel 팝업 -->
    <b-modal v-model="importModal.show"
      centered no-close-on-backdrop
      cancel-title="닫기"
      size="md" title="Import i18n"
      >

      <b-form-file
        @change="changeFile"
        v-model="importModal.file"
        placeholder="파일을 선택해 주십시오..."
        drop-placeholder="파일을 선택해 주십시오..."
        accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
      ></b-form-file>       

      <template #modal-footer="{ cancel }">
        <b-button
          @click="fileUpload"
          variant="primary">Import</b-button>
        <b-button
          @click="importModal.show = false;"
          variant="outline-primary">닫기</b-button>
      </template>
    </b-modal>
    <!--/ import excel 팝업 -->

  </div>
</template>

<script>
import {getMltlnList, exportI18nJson, uploadFile} from '@/api/coching-bo/comm/mltlnMaster'; 

import Ripple from 'vue-ripple-directive'
import ernsUtils from "@/components/mixins/ernsUtils";

import vSelect from 'vue-select';

import "ag-grid-community/styles/ag-grid.css"; // Mandatory CSS required by the Data Grid
import "ag-grid-community/styles/ag-theme-quartz.css";

import { AgGridVue } from "ag-grid-vue";
import ernsAgGrid from '@/components/ernsAgGrid/ernsAgGrid';
import { AG_GRID_COMMON_COLUMN_DEFS } from '@/constants/agGrid';

import ButtonCellRenderer from "@/components/ernsAgGrid/ButtonCellRenderer.vue";
import ToogleButtonCellRenderer from "@/components/ernsAgGrid/ToogleButtonCellRenderer.vue";

const GRID_ID_MAIN = 'main-grid';

const DEF_SEARCH_OPT = {
	sc: {
    useYn: "Y",
		delYn: "N",
    srchData: "",
  },
  pi:{
    curPage : 1,
    totalItem : 0,
    perPage : 15,
		//perPage : 2,
  },
};

export default {
  name: "coching-bo-mltln-main",
  mixins: [ernsUtils, ernsAgGrid],
  components: {
    vSelect,
    AgGridVue,
    ButtonCellRenderer,
    ToogleButtonCellRenderer,
  },
  directives: {Ripple},
  props: {},
  computed: {
    showRows(){
      return this.$store.state.erns.showRows;
    }
  },
  filters: {
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
  data() {
    const _vm = this;

    const AG_GRID_COLUMN_DEFS = [
      { hide: true,
        headerName: "Row ID", field: "rowId"          , width: 70 
        , valueGetter: (params) => params.node.id // 내부 rowId 출력
      },

      { headerName: "번호"        , field: "index", width: 70    , cellClass: 'flex-center'},
      { headerName: "코드", field: "code", width: 350, cellClass: 'flex-left' },
      { headerName: "한국어", field: "codeNmKo", width: 350, cellClass: 'flex-left' },
      { headerName: "영어", field: "codeNmEn", width: 350, cellClass: 'flex-left' },

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

      selectedOption: 'allSrch',
      options: [
        { value: 'allSrch', label: '전체' },
        { value: 'code', label: '코드' },
        { value: 'codeNmKo', label: '한국어' },
        { value: 'codeNmEn', label: '영어' },
      ],

      importModal: {
        show: false,
        file : null,
      },
    };
  },
  created() {},
  async mounted() {
    const _vm = this;

    await _vm.getInitParam();
    await _vm.loadList(_vm.listData.pi.curPage);
  },
  beforeDestroy() {
    const _vm = this;
  },
  methods: {
    //Cell 클릭 이벤트
    onCellClicked(event){
      const _vm = this;

      const item = event.node.data;
      const clickField = event.colDef.field;

      //상세이동
      const goDetailFileds = ["code", "codeNmKo", "codeNmEn"];
      if(goDetailFileds.includes(clickField)){
        _vm.$router.push({ name: 'coching-bo-mltln-mltlnEdit', query: {code: item.code}});
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

    //$route.query, $route.params, HistoryParam 등으로 초기값 설정
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
        } else if (selected.code && selected.code !== "") {
          _vm.selectedOption = "code";
        } else if (selected.codeNmKo && selected.codeNmKo !== "") {
          _vm.selectedOption = "codeNmKo";
        } else if (selected.codeNmEn && selected.codeNmEn !== "") {
          _vm.selectedOption = "codeNmEn";
        } else {
          _vm.selectedOption = "allSrch";
        }

      }
    },

    //검색
    onSearch() {
      const _vm = this;
      const searchOp = _vm.listData.sc;
      searchOp.allSrch = "";
      searchOp.code = "";
      searchOp.codeNmKo = "";
      searchOp.codeNmEn = "";

      if(_vm.selectedOption === "allSrch") {
        searchOp.allSrch = searchOp.srchData;
      } else if(_vm.selectedOption === "code") {
        searchOp.code = searchOp.srchData;
      } else if(_vm.selectedOption === "codeNmKo") {
        searchOp.codeNmKo = searchOp.srchData;
      } else if(_vm.selectedOption === "codeNmEn") {
        searchOp.codeNmEn = searchOp.srchData;
      }

      _vm.loadList(1);
    },

    //검색 초기화
    onClickResetFilter() {
      const _vm = this;

      _vm.loadList(1);
    },

    onClickListRow(item) {
      const _vm = this;
      _vm.$router.push({ name: 'coching-bo-mltln-mltlnEdit', query: {code: item.code}});
    },

    //등록
    onClickNew(){
      const _vm = this;
      _vm.$router.push({ name: 'coching-bo-mltln-mltlnAdd', query: {code: 0}});
    },

    //i18n Json Export
    async onClickExport(){
      const _vm = this;
      
      const result = await _vm.$swal({
        title: '확인',
        text: "다국어 json 파일을 export 하시겠습니까?",
        showCancelButton: true,
        confirmButtonText: 'Export',
        customClass: {
          confirmButton: 'btn btn-danger ml-1',
          cancelButton: 'btn btn-outline-primary',
        },
        buttonsStyling: false,
      });

      if(!result.value) {
        return;
      }

      _vm.loading(true);
      try {
        const params = {};
        await exportI18nJson(params);
        _vm.alertSuccess(`다국어 Json 파일이 export 되었습니다.`);
      } catch (err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
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
        const res = await getMltlnList(params); 

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
            //추가로 필요한 데이터 포맷팅 여기에서
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

        allSrch : searchOp.allSrch,
        codeRL : searchOp.code,
        codeNmKoL : searchOp.codeNmKo,
        codeNmEnL : searchOp.codeNmEn,
      };
    },

    showImportModal() {
      const _vm = this;
      _vm.importModal.file = null;

      _vm.importModal.show = true;
    },

    changeFile(event) {
      const _vm = this;
      if(event.target.files.length > 0) {
        _vm.importModal.file = event.target.files[0];
      }
    },

    async fileUpload() {
      const _vm = this;

      if(_vm.importModal.file) {
        const formData = new FormData();
        formData.append('mltln_file_01', _vm.importModal.file);

        const result = await _vm.$swal({
          title: '확인',
          text: "EXCEL 파일을 import 하시겠습니까?",
          showCancelButton: true,
          confirmButtonText: 'Import',
          customClass: {
            confirmButton: 'btn btn-danger ml-1',
            cancelButton: 'btn btn-outline-primary',
          },
          buttonsStyling: false,
        });

        if(!result.value) {
          return;
        }

        _vm.loading(true);
        try {
          const res = await uploadFile(formData);

          if(res.resultCode === "0000") {

            _vm.alertSuccess(`파일을 import 후 export 완료하였습니다.`);
            _vm.importModal.show = false;

            window.location.reload();
          }
        } catch (err) {
          _vm.alertError(err);
        } finally {
          _vm.loading(false);
        }
      }
    },


    async onClickSave() {},
  },
};
</script>

<style lang="scss" scoped>
  .erns-ag-grid-vue{
    min-height: calc(100vh - 420px);
  }

  button[type="button"]:disabled {
    background: rgb(216, 206, 206);
  }
  /*
  button[type="button"]:not(disabed) {
    background: transparent;
    color: #1e0bf0;
  }
  */
</style>
