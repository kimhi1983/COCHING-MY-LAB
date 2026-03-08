<template>
  <div id="coching-bo-member-mgr-main">
    
    <!-- 검색조건 -->
    <b-card no-body>
      <b-card-body class="coching-bo-member-mgr-filter">
        <b-row>
          <b-col cols="12" md="3" class="mb-md-0">
            <b-form-group label="검색조건" label-for="coching-bo-member-mgr-search-option"> 
              <v-select
                id="coching-bo-member-mgr-search-option"
                v-model="selectedOption"
                :options="options" :reduce="op => op.value"
                :dir="$store.state.appConfig.isRTL ? 'rtl' : 'ltr'"
                :class="['w-100', 'select-size-sm', 'select-placeholder-gray', { 'has-value': selectedOption }]"              
                @option:selected="onSearch()"
                placeholder="검색조건을 선택하세요."
              >
                <span slot="no-options">검색된 항목이 없습니다.</span>
              </v-select>
            </b-form-group>
          </b-col>

          <b-col cols="12" md="6">
            <b-form-group label="키워드" label-for="coching-bo-member-mgr-search-keyword">  
            <b-input-group>
                <b-form-input 
                  id="coching-bo-member-mgr-search-keyword"
                  v-model="listData.sc.srchData"
                  @keyup.enter="onSearch"
                  placeholder="키워드를 검색하세요."
                  size="sm"
                ></b-form-input>
                <b-input-group-append v-show="listData.sc.srchData">
                  <b-button variant="outline-secondary"
                    size="sm"
                    @click="listData.sc.srchData = ''"
                    v-b-tooltip.hover
                    title="검색어 초기화"
                    ><feather-icon icon="XIcon" size="14" />
                  </b-button>
                </b-input-group-append>
                <b-input-group-append>
                  <b-button variant="outline-primary"
                    size="sm"
                    @click="onSearch"
                    v-b-tooltip.hover
                    title="검색"
                    ><feather-icon icon="SearchIcon" size="14" />
                  </b-button>
                </b-input-group-append>
              </b-input-group>
            </b-form-group>
          </b-col>           

          <b-col cols="12" md="3">
            <div class="erns-filter-div mt-1 text-right">
              <b-form-group label="">
                <b-button
                  :disabled="!listData.gridOptions.isEditData"
                  v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                  :variant="listData.isDisabledAllSave ? 'outline-secondary' : 'secondary'"
                  class="mr-1"
                  size="sm"
                  @click="onClickResetFilter"
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
                  @click="onClickMultpleSave"
                  v-b-tooltip.hover
                  title="저장"
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
    <!-- // 목록 -->

  </div>
</template>
<script>
import vSelect from 'vue-select';
import ernsUtils from '@/components/mixins/ernsUtils';
import Ripple from 'vue-ripple-directive';
import draggable from 'vuedraggable'
import "ag-grid-community/styles/ag-grid.css"; // Mandatory CSS required by the Data Grid
import "ag-grid-community/styles/ag-theme-quartz.css";

import { AgGridVue } from "ag-grid-vue";
import ernsAgGrid from '@/components/ernsAgGrid/ernsAgGrid';
import { AG_GRID_COMMON_COLUMN_DEFS } from '@/constants/agGrid';

import {getCodes} from '@/api/coching-bo/comm/code';
import { getList, setMemberState } from '@/api/coching-bo/member/member';

import { DEF_SEARCH_OPT } from '@/constants/member';

import ButtonCellRenderer from "@/components/ernsAgGrid/ButtonCellRenderer.vue";
import ToogleButtonCellRenderer from "@/components/ernsAgGrid/ToogleButtonCellRenderer.vue";
import IconButtonCellRenderer from "@/components/ernsAgGrid/IconButtonCellRenderer.vue";

const GRID_ID_MAIN = 'main-grid';

export default {
  name: 'coching-bo-partner-main',
  mixins: [ernsUtils, ernsAgGrid],
  components : {
    vSelect,
    draggable,
    AgGridVue,
    ButtonCellRenderer,
    ToogleButtonCellRenderer,
    IconButtonCellRenderer,
  },
  props: {},
  computed : {
    showRows(){
      return this.$store.state.erns.showRows;
    } 
  },
  directives: {
    Ripple,
  },
  filters : {
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
      { headerName: "타입"        , field: "membType", width: 100, cellClass: 'flex-center'
        ,valueFormatter: (params) => {
          return this.$options.filters.eufGetCodeName(params.value, this.codes.MEMBTYPE);
        }
      },
      { headerName: "아이디", field: "membId", width: 150, cellClass: 'flex-left'},
      { headerName: "이름", field: "membName", width: 150, cellClass: 'flex-center'},
      { headerName: "닉네임", field: "nickname", width: 250, cellClass: 'flex-center'},
      { headerName: "휴대폰번호", field: "phone", width: 150, cellClass: 'flex-center'
        , valueFormatter: (params) => params.value ? this.$options.filters.eufmtPhoneDash(params.value) : '-'
      },
      { headerName: "이메일", field: "email", width: 200, cellClass: 'flex-left'},
      
      { headerName: "사용", field: "useYn", width: 70
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
        headerName: "저장"      , field: "setButton"  , width: 70  , cellClass: 'flex-center',
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
      { headerName: "가입일", field: "rgtDttm", width: 200, cellClass: 'flex-center'},

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
          ToogleButtonCellRenderer,
        },

        dataSource: null,

        sc: { ...DEF_SEARCH_OPT.sc },
        pi: { ...DEF_SEARCH_OPT.pi },
        rowData: [],
        rawList: [],        
      },

      codes : {
        AUTH : [],
        MEMBTYPE : [],
      },

      selectedOption: 'allSrch',
      options: [
        { value: 'allSrch', label: '전체' },
        { value: 'membId', label: '아이디' },
        { value: 'membName', label: '이름' },
      ],
      
    };
  },
  created() {},
  async mounted() {
    const _vm = this;

    await _vm.loadCodes();
    await _vm.getInitParam();
    await _vm.loadList(_vm.listData.pi.curPage);
  },
  beforeDestroy() {
    const _vm = this;
  },
  methods: {
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
        } else if (selected.membId && selected.membId !== "") {
          _vm.selectedOption = "membId";
        } else if (selected.membName && selected.membName !== "") {
          _vm.selectedOption = "membName";
        } else {
          _vm.selectedOption = "allSrch";
        }
      }
    },

    //Cell 클릭 이벤트
    onCellClicked(event){
      const _vm = this;

      const item = event.node.data;
      const clickField = event.colDef.field;

      //상세이동
      const goDetailFileds = ["membId", "membName", "nickname", "phone", "email", "rgtDttm"];
      if(goDetailFileds.includes(clickField)){
        _vm.$router.push({ name: 'coching-bo-member-editForm', query: {membSeq: item.membSeq}});
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

    //검색
    onSearch() {
      const _vm = this;
      const searchOp = _vm.listData.sc;

      searchOp.allSrch = "";
      searchOp.membId = "";
      searchOp.membName = "";

      if(_vm.selectedOption === "allSrch") {
        searchOp.allSrch = searchOp.srchData;
      } else if(_vm.selectedOption === "membId") {
        searchOp.membId = searchOp.srchData;
      } else if(_vm.selectedOption === "membName") {
        searchOp.membName = searchOp.srchData;
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
          membSeq : rowNode.data.membSeq || 0,
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
        text: `${params.list.length}개 회원 설정을 저장 하시겠습니까?`,
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
          await setMemberState(params);
          await _vm.onSearch();
          _vm.alertSuccess("회원 설정 값이 변경되었습니다.");
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
        isRegMode: (gridParams.data.membSeq || 0) <= 0,
        membSeq: gridParams.data.membSeq || 0,
        useYn: (gridParams.data.useYn || '').trim(),     
      };

      //Validation
      if(!params.membSeq){
        return;
      }

      const result = await _vm.$swal({
        title: '확인',
        text: `회원 상태를 ${params.isRegMode ? '등록': '변경'} 하시겠습니까?`,
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
          await setMemberState({list:[params]});
          await _vm.onSearch();
          _vm.alertSuccess("회원 상태가 변경되었습니다.");
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
        const res = await getList(params); 

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
        menuType: "USER",
        useYn : "", //관리자니까 모두 가져오기

        allSrch : searchOp.allSrch,
        membIdL: searchOp.membId,
        membNameL : searchOp.membName,
      }
    },

    async loadCodes(){
      const _vm = this;

      const authList = await getCodes({grpCode:'QP001', etc5: 'KO', rowSize:-1});
      _vm.codes.AUTH = _vm.eumConvertToVueSelectOption(authList.resultData.list);

      const membTypeList = await getCodes({grpCode:'CH002', etc5: 'KO', rowSize:-1});
      _vm.codes.MEMBTYPE = _vm.eumConvertToVueSelectOption(membTypeList.resultData.list);
    },

    //PC 에뮬레이터
    async onClickPcEmulator(item){
      const _vm = this;

      try{
        _vm.loading(true);
        const emRes = await getBizMemberEmulatorPc({membSeq : item.membSeq});
        console.debug(emRes);

        const baseUrl = emRes.resultData.targetUrl;
        const wh = window.open(baseUrl, "emPc");

        const loginUrl = baseUrl + '/common/sns/login/emulator/callback.do';
        const emParams = {
          ...emRes.resultData
        };
        setTimeout(() => {
          //console.debug(emParams);
          _vm.post_to_url(loginUrl, emParams, "emPc", "post");

        }, 1000);
      }catch(err){
        _vm.alertError(err);
      }finally{
        _vm.loading(false);
      }
    },
    post_to_url(path, params, target, method) {
      method = method || "post";

      target = target || null;
      var form = document.createElement("form");
      form.setAttribute("method", method);
      form.setAttribute("action", path);

      if (target != null)
        form.setAttribute("target", target);

        for (var key in params) {
          var hiddenField = document.createElement("input");
          hiddenField.setAttribute("type", "hidden");
          hiddenField.setAttribute("name", key);
          hiddenField.setAttribute("value", params[key]);
          form.appendChild(hiddenField);
      }

      document.body.appendChild(form);
      form.submit();

      return false;
    },
    
  },


};
</script>

<style lang="scss" scoped>
.erns-ag-grid-vue{
  min-height: calc(100vh - 430px);
}
</style>