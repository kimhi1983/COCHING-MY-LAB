<template>
  <div id="coching-bo-lab-list">
    <!-- 검색조건 -->

    <b-card no-body>
      <b-card-body>
        <b-row>

          <b-col cols="12" md="4">
            <b-form-group label="제품형태" label-for="coching-bo-lab-list-search-type">
              <v-select 
                v-model="listData.sc.prodCateGroup"
                :options="CODES.grpList"
                label="name" 
                track-by="etc3" 
                :reduce="option => option.etc3"                
                placeholder="제품 카테고리를 선택하세요"
                class="d-inline-block select-padding-r10"
                :class="['w-50', 'select-size-sm', 'select-placeholder-gray', { 'has-value': listData.sc.prodCateGroup }]"
                @input="onChangeProdCateGroup"
              />

              <v-select 
                v-model="listData.sc.prodCateCode"
                :options="CODES[`CATE${listData.sc.prodCateGroup}`]"                
                label="name" 
                track-by="etc3" 
                :reduce="option => option.etc3"
                placeholder="상세 카테고리를 선택하세요"
                :class="['w-50', 'select-size-sm', 'select-placeholder-gray', { 'has-value': listData.sc.prodCateCode }]"
                class="d-inline-block w-50"
                @input="onChangeProdCateCode"
              />              
            </b-form-group>
          </b-col>


          <b-col cols="12" md="6">
            <b-form-group label="제목" label-for="coching-bo-lab-list-search-title">
              <b-input-group>
                <b-form-input
                  id="coching-bo-lab-list-search-title"
                  v-model="listData.sc.title"
                  @keyup.enter="loadList(1)"
                  placeholder="제목을 입력하세요."
                  size="sm"
                ></b-form-input>
                <b-input-group-append v-show="listData.sc.title">
                  <b-button variant="outline-secondary"
                    size="sm"
                    @click="listData.sc.title = ''"
                    v-b-tooltip.hover
                    title="검색어 초기화"
                    ><feather-icon icon="XIcon" size="14" />
                  </b-button>
                </b-input-group-append>
                <b-input-group-append>
                  <b-button @click="loadList(1)"
                    variant="outline-primary" size="sm" 
                    v-b-tooltip.hover
                    title="검색"
                    ><feather-icon icon="SearchIcon" size="14" />
                  </b-button>
                </b-input-group-append>
              </b-input-group>              
            </b-form-group>
          </b-col>


          <b-col cols="12" md="2" >
            <div class="erns-filter-div mt-1 text-right">
              <b-form-group label="">

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
        
        @cellClicked="onCellClicked"
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

import _ from 'lodash';

import vSelect from 'vue-select';
import { AgGridVue } from "ag-grid-vue";

import { PROD_CATES } from '@/constants/testCode';

import { AG_GRID_COMMON_COLUMN_DEFS } from '@/constants/agGrid';
import { DEF_SEARCH_OPT, DEF_PARAM, DEF_ROW } from "@/constants/rnd";
import {
    getLabMasterList,
    deleteLabMaster,
} from "@/api/coching-bo/rnd/rnd";
import ernsAgGrid from '@/components/ernsAgGrid/ernsAgGrid';
import ButtonCellRenderer from "@/components/ernsAgGrid/ButtonCellRenderer.vue";
import IconButtonCellRenderer from "@/components/ernsAgGrid/IconButtonCellRenderer.vue";

const GRID_ID_MAIN = 'main-grid';

export default {
  mixins: [ernsUtils, ernsAgGrid],
  components: {
    draggable, 
    AgGridVue,
    vSelect,
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
    _vm.loadCodes();
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
      { headerName: "제품형태"  , field: "type"       , width: 230  , cellClass: 'text-left'
        , valueFormatter: (params) => {
          const prodCateCode = params.data.prodCateCode;
          const prodCateGroupCode = params.data.prodCateGroup;
          if(!prodCateCode){
            return '-';
          }
          const prodCateCodeItem = _vm.CODES.list.find(cd=>cd.etc3 == prodCateCode);          
          const prodCateGroupItem = _vm.CODES.list.find(cd=>cd.etc3 == prodCateGroupCode);
          return `${prodCateGroupItem?.name} > ${prodCateCodeItem?.name}`;
        }
      },
      { headerName: "제목"      , field: "title"     , width: 400   , cellClass: 'text-left'
        , valueFormatter: (params) => params.value ? params.value : '-'
      },
      { headerName: "AI 처방 수" , field: "aiLabResCnt" , width: 100   , cellClass: 'text-center'
        , valueFormatter: (params) => params.value ? params.value : '-'
      },      
      { headerName: "수정자"    , field: "membName"     , width: 120  , cellClass: 'text-center'},
      { headerName: "수정일"    , field: "chngDttm"   , width: 150  , cellClass: 'text-center'},     
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

      CODES : {
        prodCateList: [...PROD_CATES],

        grpList: [],
        list: [],
        CATE : [],
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

    // 카테고리(그룹) 변경
    onChangeProdCateGroup(val){
      const _vm = this;
      // 하위 카테고리 초기화
      //_vm.listData.sc.prodCateCode = _vm.CODES[`CATE${_vm.listData.sc.prodCateGroup}`][0].etc3;   
      _vm.listData.sc.prodCateCode = '';

      setTimeout(() => {
        _vm.onSearch();
      }, 100);
    },

    onChangeProdCateCode(val){
      const _vm = this;
      setTimeout(() => {
        _vm.onSearch();
      }, 100);
    },

    //등록
    onClickRegster(){
      const _vm = this;

      _vm.$router.push({ name: 'coching-bo-rnd-ai-prsc-demo1-detail', query: { labMstSeq: 0}});
    },

    //Cell 클릭 이벤트
    onCellClicked(event){
      const _vm = this;

      const item = event.node.data;
      const clickField = event.colDef.field;

      const goDetailFileds = ["labMstSeq", "title", "type", "aiLabResCnt", "membId", "chngDttm"];
      if(goDetailFileds.includes(clickField)){
        _vm.$router.push({ name: 'coching-bo-rnd-ai-prsc-demo1-detail', query: { labMstSeq: item.labMstSeq}});
      }
    },

    // 삭제
    async onDeleteRow(gridParams){
      const _vm = this;
      const params = {
        labMstSeq: gridParams.data.labMstSeq || 0,
      };

      //Validation
      if(!params.labMstSeq){
        return;
      }

      const result = await _vm.$swal({
        title: '확인',
        text: `처방을 삭제 하시겠습니까?`,
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
          await deleteLabMaster(params);
          await _vm.loadList(1);
          _vm.alertSuccess("처방이 삭제 되었습니다.");
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
        const res = await getLabMasterList(params);
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

        titleL: searchOp.title,       
      };

      if(searchOp.prodCateGroup){
        params.prodCateGroup = searchOp.prodCateGroup;
      }
      if(searchOp.prodCateCode){
        params.prodCateCode = searchOp.prodCateCode;
      }

      return params;
    },
    
    async loadCodes(){
      const _vm = this;

      let filterList = _vm.CODES.prodCateList
        .filter(cd => cd.name != '전체')
        .filter(cd => cd.code != 2);

      filterList = _.sortBy(filterList, item => item.sortOrder);

      _vm.CODES.list = [...filterList.map(item=>{
        return {
          ...item,
        }
      })];

      _vm.CODES.grpList = _vm.CODES.list.filter(cd=>cd.etc1==1);
      _vm.CODES.grpList.forEach(code => {
        _vm.CODES[`CATE${code.etc3}`] = _vm.CODES.list.filter(cd=>{
          return cd.etc3.indexOf(code.etc3)==0 && cd.etc3 != code.etc3;
        });
      });      
    },

  },
};
</script>

<style lang="scss" scoped>
.erns-ag-grid-vue{
  min-height: calc(100vh - 420px);
}

.wd-50{
  width: 49.0% !important;
}

.select-padding-r10{
  padding-right: 10px;
}
</style>
