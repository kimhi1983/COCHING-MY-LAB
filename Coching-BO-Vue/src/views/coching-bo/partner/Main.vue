<template>
  <div id="coching-bo-comp-mgr-main">
    
    <!-- 검색조건 -->
    <b-card no-body>
      <b-card-body class="coching-bo-partner-mgr-filter">
        <b-row>
          <b-col cols="12" md="3">
            <b-form-group label="원료사 구분" label-for="search-partner-nation">
              <b-form-radio-group
                id="search-partner-nation"
                v-model="listData.sc.nation"
                buttons
                button-variant="outline-primary"
                size="sm"
                @change="onSearch"
                :options="[
                  {text:'전체체', value: ''},{text:'국내', value: '001'},{text:'해외', value: '002'}
                ]"
              >
              </b-form-radio-group>
            </b-form-group>
          </b-col>
          <b-col cols="12" md="3">
            <b-form-group label="대리점 모집" label-for="search-partner-nation">
              <b-form-radio-group
                id="search-partner-nation"
                v-model="listData.sc.recruitYn"
                buttons
                button-variant="outline-primary"
                size="sm"
                @change="onSearch"
                :options="[
                  {text:'전체', value: ''},{text:'모집중', value: 'N'},{text:'모집완료', value: 'Y'}
                ]"
              >
              </b-form-radio-group>
            </b-form-group>
          </b-col>

          <b-col cols="0" md="6" class="mb-md-0">
          </b-col>

          <b-col cols="12" md="3" class="mb-md-0">
            <b-form-group label="검색조건" label-for="coching-bo-partner-mgr-search-option"> 
              <v-select
                id="coching-bo-partner-mgr-search-option"
                v-model="listData.sc.searchOption"
                :options="options" :reduce="op => op.value"
                :dir="$store.state.appConfig.isRTL ? 'rtl' : 'ltr'"
                :class="['w-100', 'select-size-sm', 'select-placeholder-gray', { 'has-value': listData.sc.searchOption }]"              
                @option:selected="onSearch"
                placeholder="검색조건을 선택하세요."
              >
                <span slot="no-options">검색된 항목이 없습니다.</span>
              </v-select>
            </b-form-group>
          </b-col>

          <b-col cols="12" md="6" class="mb-md-0">
            <b-form-group label="키워드" label-for="coching-bo-partner-mgr-search-keyword">  
            <b-input-group>
              <b-form-input 
                id="coching-bo-partner-mgr-search-keyword"
                v-model="listData.sc.searchKeyword"
                  @keyup.enter="onSearch" 
                  placeholder="키워드를 검색하세요."
                  size="sm"
                ></b-form-input>
                <b-input-group-append v-show="listData.sc.searchKeyword"> 
                  <b-button variant="outline-secondary"
                    size="sm"
                    @click="listData.sc.searchKeyword = ''"
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

          <b-col cols="12" md="3" class="mb-md-0 mb-2">
            <div class="erns-filter-div mt-2 text-right">

              <!-- <b-button
                v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                variant="outline-secondary"
                class="mr-1"
                @click="onClickResetFilter"
              > 검색초기화
              </b-button> -->
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

    <!-- 가입 계정 팝업 -->
    <b-modal v-model="membModal.show"
      centered no-close-on-backdrop
      cancel-title="닫기"
      size="lg" title="가입 계정"
      >
      <b-card no-body class="mb-0">
        <b-table
          class="position-relative" 
          responsive 
          striped 
          hover
          :items="membList.rawList"
          :fields="membList.fields"
          ref="membListTable"
          no-local-sorting
          :sort-by.sync="membList.sc.sortBy"
          :sort-desc.sync="membList.sc.sortDesc"
          primary-key="membSeq"
          show-empty
          empty-text="검색된 내용이 없습니다."
        >

          <template #cell(index)="data">
            {{ data.index | eufRowNumberForPageInfo(membList.pi) }}
          </template>
          <template #cell(membId)="data">
            <a class="onClick" @click="onClickPcEmulator(data.item)">{{ data.item.membId }}</a>
          </template>
          <template #cell(useYn)="data">
            {{ data.item.useYn == 'Y' ? '사용' : '미사용' }}
          </template>
          <template #cell(rgtDttm)="data">
            {{ data.item.rgtDttm  | eFmtDateTime('-')}}
          </template>
        </b-table>

        <div class="mx-2 mb-2">
          <b-row>
            <b-col cols="12" sm="6"
              class="d-flex align-items-center justify-content-center justify-content-sm-start"
            >
              <v-select
                v-model="membList.pi.perPage"
                :dir="$store.state.appConfig.isRTL ? 'rtl' : 'ltr'"
                :options="showRows"
                :clearable="false"
                class="per-page-selector d-inline-block mx-50"
                @option:selected="loadMembList(1)"
              />

              <span class="text-muted">
                {{eumPaginationRangeForPageInfo(membList.pi)}}
              </span>
            </b-col>

            <b-col cols="12" sm="6"
              class="d-flex align-items-center justify-content-center justify-content-sm-end"
            >
              <b-pagination
                v-if="membList.pi.totalItem > 0"
                v-model="membList.pi.curPage"
                :total-rows="membList.pi.totalItem"
                :per-page="membList.pi.perPage"
                first-number
                last-number
                limit = "10"
                class="mb-0 mt-1 mt-sm-0"
                prev-class="prev-item"
                next-class="next-item"
                @change="loadMembList"
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

      <template #modal-footer="{ cancel }">
        <b-button
          @click="membModal.show = false;"
          variant="outline-primary">닫기</b-button>
      </template>
    </b-modal>
    <!--/ 가입 계정 팝업 -->

    <!-- fields:{{ list.fields }} -->
  </div>
</template>
<script>
import vSelect from 'vue-select';
import ernsUtils from '@/components/mixins/ernsUtils';
import Ripple from 'vue-ripple-directive';
import "ag-grid-community/styles/ag-grid.css"; // Mandatory CSS required by the Data Grid
import "ag-grid-community/styles/ag-theme-quartz.css";

import { AgGridVue } from "ag-grid-vue";
import ernsAgGrid from '@/components/ernsAgGrid/ernsAgGrid';
import { AG_GRID_COMMON_COLUMN_DEFS } from '@/constants/agGrid';

import ButtonCellRenderer from "@/components/ernsAgGrid/ButtonCellRenderer.vue";
import ToogleButtonCellRenderer from "@/components/ernsAgGrid/ToogleButtonCellRenderer.vue";

import {getCodes} from '@/api/coching-bo/comm/code';
import { getPartnerList, getMembList } from '@/api/coching-bo/partner/partner';

const GRID_ID_MAIN = 'main-grid';

const DEF_SEARCH_OPT = {
	sc: {
    useYn: "", //관리자니까 모두 가져오기
		delYn: "N",
    nation: "",
    recruitYn: "",
    searchOption: "All",
    searchKeyword: "",
  },
  pi:{
    curPage : 1,
    totalItem : 0,
    perPage : 50,
		//perPage : 2,
  },
};

const DEFAULT_MEMB_TABLE_COLUMNS = [
  { key: 'index'      , label: '번호'       , sortable: false , class: 'text-center' },
  { key: 'membId'      , label: '아이디'    , sortable: false , class: 'text-center', thClass: "text-center" },
  { key: 'membName'   , label: '이름'       , sortable: false , class: 'text-center' },
  { key: 'useYn'       , label: '사용여부'  , sortable: false , class: 'text-center' },  
  { key: 'rgtDttm'       , label: '가입일'  , sortable: false , class: 'text-center' },  
];


export default {
  name: 'coching-bo-partner-main',
  mixins: [ernsUtils, ernsAgGrid],
  components : {
    vSelect,
    AgGridVue,
    ButtonCellRenderer,
    ToogleButtonCellRenderer,
  },
  props: {},
  computed : {
    showRows(){
      return this.$store.state.erns.showRows;
    } 
  },
  watch: {
    // 라우트가 변경되면 메소드를 다시 호출됩니다.
    '$route': 'fetchData',
    
    // 대리점 모집 변경 시 원료사 구분 변경
    'listData.sc.recruitYn'(newValue, oldValue) {
      const _vm = this;

      if(newValue != ''){
        _vm.listData.sc.nation = '002';
      }
    },

    // 원료사 구분 변경 시 대리점 모집 변경
    'listData.sc.nation'(newValue, oldValue) {
      const _vm = this;

      if(newValue == '001'){
        _vm.listData.sc.recruitYn = '';
      }
    },
  },
  directives: {
    Ripple,
  },
  filters : {
  },
  data() {
    const _vm = this;

    const AG_GRID_COLUMN_DEFS = [
      { hide: true,
        headerName: "Row ID", field: "rowId"          , width: 70 
        , valueGetter: (params) => params.node.id // 내부 rowId 출력
      },

      { headerName: "번호"        , field: "index", width: 70    , cellClass: 'flex-center'},
      { headerName: "구분", field: "nation", width: 120, cellClass: 'flex-center' 
        , valueFormatter: (params) => {
          return this.$options.filters.eufGetCodeName(params.value, this.codes.NATION);
        }
      },
      { headerName: "원료사명", field: "ptnName", width: 400, cellClass: 'flex-left' },
      { headerName: "사업자번호", field: "ptnLic", width: 100, cellClass: 'flex-center' 
        , valueFormatter: (params) => {
          var val = params.value;
          if(params.node.data.nation == '002'){
            val = "-";
          }
          return val;
        }
      },
      { headerName: "영업사원", field: "cntMember", width: 100, cellClass: 'flex-center' },
      { headerName: "대리점 모집", field: "recruitYn", width: 100, cellClass: 'flex-center' 
        , valueFormatter: (params) => {
          var val = params.value;
          if(params.node.data.nation == '002'){
            if(val == "N"){
              val = "모집중";
            }else{
              val = "모집완료";
            }
          } else {
            val = "-";
          }
          return val;
        }
      },
      { headerName: "가입일", field: "rgtDate", width: 300, cellClass: 'flex-center' },
      { hide : true,
        headerName: "수정여부"  , field: "_isEditRow" , width: 100  , cellClass: 'text-center'},
    ];


    return {
      codes : {
        AUTH : [],
        NATION : [],
        BANK : [],
        NATIONTYPE : [],
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
          ToogleButtonCellRenderer,
        },

        dataSource: null,

        sc: { ...DEF_SEARCH_OPT.sc },
        pi: { ...DEF_SEARCH_OPT.pi },
        rowData: [],
        rawList: [],        
      },
      
      options: [
        { value: 'All', label: '전체' },
        { value: 'ptnName', label: '원료사명' },
        { value: 'ptnLic', label: '사업자번호' },
      ],

      membModal: {
        show: false,
        ptnSeq: 0,
      },

      membList: {
         sc: {
          ptnSeq: "",
          sortBy: "membName",
          sortDesc: true,
        },
        pi: {
          // 페이지네이션
          curPage: 1,
          totalItem: 0,
          perPage: 5,
        },

        fields: [...DEFAULT_MEMB_TABLE_COLUMNS],

        rawList: [],
      },
      
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
      }
    },

    //Cell 클릭 이벤트
    onCellClicked(event){
      const _vm = this;

      const item = event.node.data;
      const clickField = event.colDef.field;

      //상세이동
      const goDetailFileds = ["ptnName", "ptnLic", "cntMember", "rgtDate", "nation", "recruitYn"];
      if(goDetailFileds.includes(clickField)){
        _vm.$router.push({ name: 'coching-bo-partner-editForm', query: {ptnSeq: item.ptnSeq}});
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
      _vm.loadList(1);
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
        const res = await getPartnerList(params); 

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
        ...searchOp,
        page : pInfo.curPage,
        rowSize : pInfo.perPage,
      }
    },

    async loadCodes(){
      const _vm = this;

      const nationList = await getCodes({grpCode:'CH007', etc5: 'KO', rowSize:-1});
      _vm.codes.NATION = _vm.eumConvertToVueSelectOption(nationList.resultData.list);
    },

    async onClickMemb(ptnSeq) {
      const _vm = this;

      _vm.membModal.ptnSeq = ptnSeq;
      _vm.membModal.show = true;

      _vm.loadMembList(1);
    },

    //가입계정 목록 로드
    async loadMembList(pageNo) {
      const _vm = this;
      const dataList = _vm.membList,
            pInfo = _vm.membList.pi,
            searchOp = _vm.membList.sc;
      pInfo.curPage = pageNo || 1;

      const params = _vm.getListSearchParam(_vm.membList);
      params.ptnSeq = _vm.membModal.ptnSeq;

      try {
        _vm.loading(true);

        const res = await getMembList(params);

        const pageInfo = res.resultData.pageInfo;
        dataList.rawList = res.resultData.list;
        dataList.pi = {
          ...dataList.pi,
          curPage: pageInfo.currentPage,
          totalItem: pageInfo.totalItem,
          perPage: pageInfo.pageItemSize,
        };
      } catch (err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
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
  min-height: calc(100vh - 420px);
}
</style>