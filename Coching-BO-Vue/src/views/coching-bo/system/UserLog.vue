<template>
  <div class="sys-user-logs-wrap">
  <!-- 검색조건 -->
    <b-card no-body>
      <b-card-body>
        <b-row>

          <b-col cols="12" md="4" 
            class="mb-md-0 mb-0"
          >
            <b-form-group label="등록일" label-for="sys-user-logs-fromDate">
              <b-input-group>
                <b-form-datepicker 
                  id="sys-user-logs-fromDate"
                  v-model="listData.sc.fromDate"
                  placeholder="YYYY-MM-DD" locale="ko" 
                  :date-format-options="{ year: 'numeric', month: 'numeric', day: 'numeric' }"
                  @input="loadList(1)"
                  size="sm"
                  v-b-tooltip.hover
                  title="등록일 From"
                />
                <b-form-datepicker 
                  v-model="listData.sc.toDate"
                  :date-format-options="{ year: 'numeric', month: 'numeric', day: 'numeric' }"
                  placeholder="YYYY-MM-DD" locale="ko"
                  @input="loadList(1)"
                  size="sm"
                  v-b-tooltip.hover
                  title="등록일 To"
                />
              </b-input-group>
            </b-form-group>
          </b-col>

          <b-col cols="12" md="2"
            class="mb-md-0 mb-0"
          >
            <b-form-group label="사이트" label-for="sys-user-logs-siteType">
              <v-select
                id="sys-user-logs-siteType"
                v-model="listData.sc.siteType"
                :options="code.SITE_TYPE" :reduce="op => op.value"
                :dir="$store.state.appConfig.isRTL ? 'rtl' : 'ltr'"
                :class="['w-100', 'select-size-sm', 'select-placeholder-gray', { 'has-value': listData.sc.siteType }]"              
                @input="loadList(1)"
                placeholder="사이트를 선택하세요"
              >
                <span slot="no-options">검색된 항목이 없습니다.</span>
              </v-select>
            </b-form-group>
          </b-col>

          <b-col cols="12" md="6"
            class="mb-md-0 mb-0"
          >
            <b-form-group label="로그 타입" label-for="sys-user-logs-userLogType">
              <v-select
                id="sys-user-logs-userLogType"
                v-model="listData.sc.logType"
                :options="code.USER_LOG_TYPE" :reduce="op => op.value"
                :dir="$store.state.appConfig.isRTL ? 'rtl' : 'ltr'"
                :class="['w-100', 'select-size-sm', 'select-placeholder-gray', { 'has-value': listData.sc.logType }]"              
                @input="loadList(1)"
                placeholder="로그 타입을 선택하세요"
              >
                <span slot="no-options">검색된 항목이 없습니다.</span>
              </v-select>
            </b-form-group>
          </b-col>

          <!-- <b-col cols="12" md="3" class="mb-md-0 mb-0">
            <label>로그생성일</label>
            <b-input-group>
              <b-form-input v-model="listData.sc.logDateRL"
                @keyup.enter="loadList(1)"
                placeholder="날짜를 입력하세요."
              ></b-form-input>
              <b-input-group-append>
                <b-button variant="outline-primary"
                  @click="loadList(1)"
                >검색</b-button>      
              </b-input-group-append>
            </b-input-group>
          </b-col> -->

          <b-col cols="12" md="4" 
            class="mb-md-0 mb-0"
          >
            <b-form-group label="사용자 ID/성명" label-for="sys-user-logs-userSearch">
              <b-input-group>
                <b-form-input 
                  id="sys-user-logs-userSearch"
                  v-model="listData.sc.userSearch"
                  @keyup.enter="loadList(1)"
                  placeholder="사용자 ID / 성명를 입력하세요."
                  size="sm"
                ></b-form-input>
                <b-input-group-append>
                  <b-button 
                    variant="outline-primary"
                    @click="loadList(1)"
                    size="sm"
                    v-b-tooltip.hover
                    title="검색"
                    ><feather-icon icon="SearchIcon" size="14" />
                  </b-button>      
                </b-input-group-append>
              </b-input-group>
            </b-form-group>
          </b-col>

          <b-col cols="12" md="4" 
            class="mb-md-0 mb-0"
          >
            <b-form-group label="로그 코드" label-for="sys-user-logs-logTypeRL">
              <b-input-group>
                <b-form-input 
                  id="sys-user-logs-logTypeRL"
                  v-model="listData.sc.logTypeRL"
                  @keyup.enter="loadList(1)"
                  placeholder="ex) 990_0"
                  size="sm"
                ></b-form-input>
                <b-input-group-append>
                  <b-button variant="outline-primary"
                    size="sm"
                    @click="loadList(1)"
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
    <b-card no-body class="mb-0">
      <div class="erns-ag-grid-vue">
        <ag-grid-vue       
          class="ag-theme-quartz"
          :ref="listData.gridId"
          :gridOptions="listData.gridOptions"
          :columnDefs="listData.columnDefs"
          :rowData="listData.rowData"
          @gridReady="(params) => ernsAgGrid_registerGrid(listData.gridId, params, listData.gridOptions)"
          @rowClicked="onClickListRow"
          
          :frameworkComponents="listData.frameworkComponents"
          
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

    <b-modal 
      v-model="userLogDetail.show"
      title="로그 상세"
      size="lg"
      centered no-close-on-backdrop>
      <UserLogJson
        :json1Label="'params : '"
        :json2Label="'response :'"
        :data="userLogDetail.data"
      ></UserLogJson>
    </b-modal>
  </div>
</template>
<script>
import vSelect from 'vue-select';
import ernsUtils from '@/components/mixins/ernsUtils';
import Ripple from "vue-ripple-directive";
import "ag-grid-community/styles/ag-grid.css"; // Mandatory CSS required by the Data Grid
import "ag-grid-community/styles/ag-theme-quartz.css";

import { AgGridVue } from "ag-grid-vue";
import ernsAgGrid from '@/components/ernsAgGrid/ernsAgGrid';
import { AG_GRID_COMMON_COLUMN_DEFS } from '@/constants/agGrid';

import { DEF_SEARCH_OPT, DEF_ROW } from "@/constants/userLogs";

import { getEnumCodes } from '@/api/coching-bo/comm/code';
import { getUserLogList } from '@/api/coching-bo/system/userLog';

import UserLogJson from './UserLogJson.vue';

const GRID_ID_MAIN = 'main-grid';

export default {
  name: 'coching-BackOffice-System-UserLog',
  mixins: [ernsUtils, ernsAgGrid],
  components : {
    vSelect,
    AgGridVue,
    UserLogJson
  },
  directives: {
    Ripple,
  },
  props: {},
  computed : {
    showRows(){
      return this.$store.state.erns.showRows;
    } 
  },
  filters: {},
  async mounted() {
    const _vm = this;

    _vm.getInitParam();    
    await _vm.fetchData();
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
  data(){
    const _vm = this;
    
    // Ag-Grid Column Definition
    const AG_GRID_COLUMN_DEFS = [
      { hide: true,
        headerName: "Row ID", field: "rowId"              , width: 70 
        , valueGetter: (params) => params.node.id // 내부 rowId 출력
      },
      { headerName: "번호"        , field: "index"        , width: 100  , cellClass: 'flex-center',
      },      
      { headerName: "로그시간"    , field: "rgtDttm"      , width: 120  , cellClass: 'flex-center',
      },
      { headerName: "사이트"      , field: "siteTypeName" , width: 120  , cellClass: 'flex-center',
      },
      { headerName: "로그 코드"   , field: "logType"      , width: 120  , cellClass: 'flex-center',
      },
      { headerName: "로그명"      , field: "logTypeName"  , width: 200  , cellClass: 'flex-left'
      },
      { headerName: "상태"        , field: "statusName"   , width: 80  , cellClass: 'flex-center'
      },
      { headerName: "사용자ID"    , field: "membId"       , width: 150  , cellClass: 'flex-center',
      },
      { headerName: "사용자명"    , field: "membName"     , width: 150  , cellClass: 'flex-center'
      },
      { headerName: "access IP"   , field: "accessIp"     , width: 120  , cellClass: 'flex-left'
      },
      { headerName: "라우터명"    , field: "routerNm"     , width: 200  , cellClass: 'flex-left'
      },
      { headerName: "URI"         , field: "apiUri"       , width: 300  , cellClass: 'flex-left'
      },
      { hide : true,
        headerName: "수정여부"  , field: "_isEditRow" , width: 100  , cellClass: 'flex-center'},
    ];

    return {
      code : {
        SITE_TYPE : [],
        USER_LOG_TYPE : [],
        PROC_STATUS : [
          {label: '요청', value: '0'},
          {label: '성공', value: '1'},
          {label: '오류', value: 'E'},
        ]
      },

      listData :{
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
        frameworkComponents :{},


        sc:{...DEF_SEARCH_OPT.sc},
        pi:{...DEF_SEARCH_OPT.pi},
        rowData: [],
        rawList: [],
      },

      userLogDetail: {
        show: false,
        data: {
         
        }
      }
    }
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

    onSearch(){
      const _vm = this;      
    },
    async fetchData() {
      const _vm = this;
      await _vm.loadCodes();
      await _vm.loadList(_vm.listData.pi.curPage);
    },

    async loadCodes(){
      const _vm = this;

      //사이트타입
      const siteType = await getEnumCodes({grpCode: 'SITE_TYPE', rowSize: -1});
      _vm.code.SITE_TYPE = siteType.resultData.list
        .map(item=>{
          return {label:item.nameKo, value:item.code};
        });

      
      //로그타입 로그
      const rawTypeList = await getEnumCodes({grpCode: 'USER_LOG_TYPE', rowSize: -1});
      _vm.code.USER_LOG_TYPE = rawTypeList.resultData.list
        .filter(item=>item.code != '000_000000')
        .map(item=>{
          return {label:item.nameKo, value:item.code};
        });

      const sortedList = _vm.code.USER_LOG_TYPE.sort(function(a, b){
        return a.value < b.value ? -1 : a.value > b.value ? 1 : 0;
      });

      _vm.code.USER_LOG_TYPE = sortedList;
    },

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
        const res = await getUserLogList(params);
        const {pageInfo, list, sc} = res.resultData;
  
        const logType = _vm.code.USER_LOG_TYPE;

        listData.pi = {
          ...listData.pi, 
          curPage: pageInfo.currentPage,
          totalItem: pageInfo.totalItem,
          perPage: pageInfo.pageItemSize,         
        };

        
        listData.rawList = list.map((item, index) => {
          const logTypeCode = _vm.code.USER_LOG_TYPE.find(code=>code.value == item.logType);
          const siteTypeCode = _vm.code.SITE_TYPE.find(code=>code.value == item.siteType);
          const statusName = _vm.code.PROC_STATUS.find(code=>code.value == item.procStatus);

          return {
            rowIndex : listData.nextRowIndex++, //rowIndex 설정
            index: _vm.$options.filters.eufRowNumberForPageInfo(index, pInfo),
            _isEditRow : false,
            ...item,
            logTypeName : logTypeCode ? logTypeCode.label : '-',
            siteTypeName : siteTypeCode ? siteTypeCode.label : '-',
            statusName : statusName ? statusName.label : '-',
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

    async onClickListRow(event) {
      const _vm = this;

      const item = event.node.data;
      //console.log("클릭된 행 데이터:", item);
      
      if(item.apiParams || item.apiRes) {
        _vm.userLogDetail.show = true;
        _vm.userLogDetail.data = {
          ...item,
          logJson : item.apiParams,
          logJson2 : item.apiRes,          
        };
        return;
      }
    },

    getListSearchParam() {
      const _vm = this;

      const pInfo = _vm.listData.pi, searchOp = _vm.listData.sc;

      const params = {
        page : pInfo.curPage,
        rowSize : pInfo.perPage,

        fromDate : searchOp.fromDate,

        toDate : searchOp.toDate,

        siteType : searchOp.siteType,

        logType: searchOp.logType,

        logTypeRL: searchOp.logTypeRL,
        
        userSearch: searchOp.userSearch,


      }
      
      return params;
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