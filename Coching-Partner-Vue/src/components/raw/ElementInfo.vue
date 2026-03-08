<template>
  <!--card-account-->
  <div class="section" id="elmInfo">
    <div class="title-wrap">
      <!--title-->
      <div class="title">성분</div>
    </div>
    <!--section-content-->
    <!--input-wrap-->
    <div v-show="rawInfo.rawSeq == 0 || (rawInfo.rawSeq != 0 && isAdmin && eumLoginUser.userSeq == managerSeq)" class="section-content">
      <div class="base-add">
        <div class="header">
          <div>성분 검색</div>
        </div>
      </div>
      <div class="input-set">
        <div class="input-ic-set">
          <input @keyup.enter="onSearch" 
            v-model="listData.sc.searchText" 
            type="text" placeholder="검색" />
          <button @click="onSearch" 
            type="button" class="input-ic ic-md ic-search-md"></button>
        </div>
      </div>
      <div class="erns-ag-grid-vue">
        <ag-grid-vue       
          class="ag-theme-quartz"
          :ref="listData.gridId"
          :gridOptions="listData.gridOptions"
          :columnDefs="listData.columnDefs"
          :rowData="listData.rowData"
          @gridReady="(params) => ernsAgGrid_registerGrid(listData.gridId, params, listData.gridOptions)"
          
          :frameworkComponents="listData.frameworkComponents"
          
          rowDragManaged="true"

          pagination=false
          suppressRowTransform=true
        >
        </ag-grid-vue>
      </div>
    </div>
    <div class="section-content">
      <div class="base-add">
        <div class="header">
          <div>선택 성분</div>
          <div v-show="rawInfo.rawSeq == 0 || (rawInfo.rawSeq != 0 && isAdmin && eumLoginUser.userSeq == managerSeq)">&downarrow;</div>
        </div>
      </div>
      <div class="erns-ag-grid-vue">
        <ag-grid-vue       
          class="ag-theme-quartz"
          :class="{ 'ag-disabled': rawInfo.rawSeq && (!isAdmin || (isAdmin && eumLoginUser.userSeq != managerSeq))}"
          :ref="localData.elementData.gridId"
          :gridOptions="localData.elementData.gridOptions"
          :columnDefs="localData.elementData.columnDefs"
          :rowData="localData.elementData.checkedList"
          @gridReady="(params) => ernsAgGrid_registerGrid(localData.elementData.gridId, params, localData.elementData.gridOptions)"
          @rowDataUpdated="onChangeValue"
          @rowDragEnd="onChangeValue"
          
          :frameworkComponents="localData.elementData.frameworkComponents"
          
          rowDragManaged="true"

          pagination=false
          suppressRowTransform=true
        >
        </ag-grid-vue>
      </div>
    </div>

    <AlertModal ref="alertModal"></AlertModal>
    <ConfirmModal ref="confirmModal"></ConfirmModal>
  </div>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import "ag-grid-community/styles/ag-grid.css"; // Mandatory CSS required by the Data Grid
import "ag-grid-community/styles/ag-theme-quartz.css";

import { AgGridVue } from "ag-grid-vue";
import ernsAgGrid from '@/components/ernsAgGrid/ernsAgGrid';
import { AG_GRID_COMMON_COLUMN_DEFS } from '@/constants/agGrid';

import { DEF_SEARCH_OPT, DEF_ROW } from "@/constants/cochingIngredient";

import { getElementList } from '@/api/coching/comm/raw';
import { getSearchIngredients, getStaticIngredientList } from '@/api/coching/search/search';

import ButtonCellRenderer from "@/components/ernsAgGrid/ButtonCellRenderer.vue";
import EwgScoreCellRenderer from "@/components/ernsAgGrid/EwgScoreCellRenderer.vue";

const GRID_ID_MAIN = 'main-grid';
const GRID_ID_SUB = 'sub-grid';

const DEF_RAW_INF = {
  rawSeq: null,
  ptnSeq: null,
  rawName: '',
  prodCompany: '',
  prodCountry: '',
  supplier: '',
  useYn: 'N',
  delYn: 'N',
};

export default {
    name: 'coching-rawInfo',
    mixins: [ernsUtils, ernsAgGrid],
    components: {
      AgGridVue,    
      ButtonCellRenderer,
      EwgScoreCellRenderer,
    },
    props: {
        rawInfo: {
          type : Object,
          defalt: {}
        },
        isAdmin: {
          type : Boolean,
          default: false
        },
        managerSeq: {
          type: Number,
          default: 0,
        },
    },
    computed: {},
    watch: {
      '$i18n.locale' : function(){
        const _vm = this;
      },
      rawInfo: {
        handler() {
          this.localData.rawInfoItem = { ...DEF_RAW_INF, ...this.rawInfo };
          this.init(); // 필요한 경우만 호출
        },
        immediate: true, // 컴포넌트가 마운트될 때도 트리거
        deep: true, // 깊은 감지 활성화
      },
      // 'localData.elementData.checkedList' : {
      //   handler(newVal, oldVal){
      //     this.onChangeValue();
      //   },
      //   deep: true
      // },      
    },
    data() {
      const _vm = this;
    
      // Ag-Grid Column Definition
      const AG_GRID_COLUMN_DEFS = [
        {
          headerName: "추가"      , field: "setButton"  , width: 100  , cellClass: 'text-center', pinned: "left", lockPinned: true, 
          cellRenderer: "ButtonCellRenderer", // 렌더러 지정  
          cellRendererParams: {
            label: "추가",
            btnClass: 'btn-primary btn-sm',
            action: (params) => {
              this.onAddValue(params);
            },
          },
        },
        { hide: true,
          headerName: "Row ID", field: "rowId"          , width: 70 
          , valueGetter: (params) => params.node.id // 내부 rowId 출력
        },
        { headerName: "번호"        , field: "index"        , width: 100  , cellClass: 'flex-center'} ,
        { headerName: "EWG"         , field: "ewgScore"     , width: 100  , cellClass: 'flex-left',
            cellRenderer: "EwgScoreCellRenderer", // 렌더러 지정
            cellRendererParams: (params) => ({
              ewgVal : `${params.node.data['ewg_score_min']}-${params.node.data['ewg_score']}`,
              ewgDataLabel: params.node.data.ewg_data_label,
            }),          
        },
        { headerName: "성분명"      , field: "rep_name"     , width: 250  , cellClass: 'flex-left',
          wrapText: true, // 자동 줄바꿈 활성화
          autoHeight: true, // 행 높이를 자동 조정
          cellStyle: { "white-space": "normal" } // 여러 줄로 표시
        },
        { headerName: "영문명"      , field: "rep_name_en"  , width: 250  , cellClass: 'flex-left',
          wrapText: true, // 자동 줄바꿈 활성화
          autoHeight: true, // 행 높이를 자동 조정
          cellStyle: { "white-space": "normal" } // 여러 줄로 표시
        },
        { headerName: "CAS NO"      , field: "cas_no"       , width: 200  , cellClass: 'flex-left'},
        { headerName: "EC NO"       , field: "ec_no"        , width: 200  , cellClass: 'flex-left'},
        { headerName: "INCI"        , field: "inci"         , width: 300  , cellClass: 'flex-left',
          wrapText: true, // 자동 줄바꿈 활성화
          autoHeight: true, // 행 높이를 자동 조정
          cellStyle: { "white-space": "normal" } // 여러 줄로 표시
        },
       
      ];

      const AG_GRID_COLUMN_DEFS2 = [
        { headerName: "", field: "drag", rowDrag: true, width: 50, hide:true }, // 드래그 컬럼 추가
        {
          headerName: "삭제", field: "setButton", width: 100, cellClass: 'text-center', hide: true,
          cellRenderer: "ButtonCellRenderer", // 렌더러 지정
          cellRendererParams: {
            label: "삭제",
            btnClass: 'btn-gray-outline btn-sm',
            action: (params) => {
              this.onDelValue(params);
            },
          },
        },
        { hide: true,
          headerName: "Row ID", field: "rowId"          , width: 70 
          , valueGetter: (params) => params.node.id // 내부 rowId 출력
        },
        { hide: true, headerName: "번호"        , field: "index"        , width: 100  , cellClass: 'flex-center'} ,
        { headerName: "EWG"         , field: "ewgScore"     , width: 100  , cellClass: 'flex-left',
            cellRenderer: "EwgScoreCellRenderer", // 렌더러 지정
            cellRendererParams: (params) => ({
              ewgVal : `${params.node.data['ewg_score_min']}-${params.node.data['ewg_score']}`,
              ewgDataLabel: params.node.data.ewg_data_label,
            }),          
        },
        { headerName: "성분명"      , field: "rep_name"     , width: 250  , cellClass: 'flex-left',
          wrapText: true, // 자동 줄바꿈 활성화
          autoHeight: true, // 행 높이를 자동 조정
          cellStyle: { "white-space": "normal" } // 여러 줄로 표시
        },
        { headerName: "영문명"      , field: "rep_name_en"  , width: 250  , cellClass: 'flex-left',
          wrapText: true, // 자동 줄바꿈 활성화
          autoHeight: true, // 행 높이를 자동 조정
          cellStyle: { "white-space": "normal" } // 여러 줄로 표시
        },
        { headerName: "CAS NO"      , field: "cas_no"       , width: 200  , cellClass: 'flex-left'},
        { headerName: "EC NO"       , field: "ec_no"        , width: 200  , cellClass: 'flex-left'},
        { headerName: "INCI"        , field: "inci"         , width: 300  , cellClass: 'flex-left',
          wrapText: true, // 자동 줄바꿈 활성화
          autoHeight: true, // 행 높이를 자동 조정
          cellStyle: { "white-space": "normal" } // 여러 줄로 표시
        }
      ];
      return {
        localInvalidState: false, //양식폼의 validate 상태
        status: '',
        CODES: {
          USE_001 : [],
        },
        localData:{
          rawInfoItem: {...DEF_RAW_INF},
          elementData: {
            gridId : GRID_ID_SUB,
            nextRowIndex : 1,
            isDisabledAllSave : true,
            gridOptions : {
              isEditData : false,
              immutableData : true,
              defaultColDef : AG_GRID_COMMON_COLUMN_DEFS,
              getRowId: (params) => params.data.id, // 각 행의 ID를 'rowIndex' 로 지정
              localeText: {
                noRowsToShow: "선택된 성분이 없습니다.", // 기본 문구 변경
              }
            },
            columnDefs: AG_GRID_COLUMN_DEFS2,
            frameworkComponents :{
              ButtonCellRenderer,
              EwgScoreCellRenderer,
            },
            checkedList:[],
            list:[],
          },
        },
        listData: {
          gridId : GRID_ID_MAIN,
          nextRowIndex : 1,
          isDisabledAllSave : true,
          gridOptions : {
            isEditData : false,
            immutableData : true,
            defaultColDef : AG_GRID_COMMON_COLUMN_DEFS,
            getRowId: (params) => params.data.id, // 각 행의 ID를 'rowIndex' 로 지정
          },
          columnDefs: AG_GRID_COLUMN_DEFS,
          frameworkComponents :{
            ButtonCellRenderer,
            EwgScoreCellRenderer,
          },

          sc: { ...DEF_SEARCH_OPT.sc },
          pi: { ...DEF_SEARCH_OPT.pi },
          rowData: [],
          rawList: [],
        },
        ingdList: [],
      }
    },
  async mounted(){
    const _vm = this;
    
    await _vm.loadIngdList();
    await _vm.loadList(1);
    _vm.fetchData();  
    _vm.docReady();  
  },
	beforeDestroy() {
    const _vm = this;
	},
  methods: {
    //fetchData
    async fetchData(){
      const _vm = this;

      _vm.loading(true);

      try{
        await _vm.init();
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },    

    async init(){
      const _vm = this;

      if(_vm.localData.rawInfoItem.rawSeq){
        _vm.loading(true);
        try{
            //성분 리스트
            const elmListRes = await getElementList({rawSeq: _vm.rawInfo.rawSeq});
            const elmList = elmListRes.resultData.list;
            if (elmList.length > 0) {
              _vm.localData.elementData.list = [...elmList].sort((a, b) => a.sortOrd - b.sortOrd);

              const filteredRowData = _vm.ingdList.filter(row => 
                elmList.some(elm => elm.rawElmId === row.id)
              );

              // checkedList를 `sortOrd` 값 기준으로 정렬
              _vm.localData.elementData.checkedList = [...filteredRowData].sort((a, b) => {
                const elmA = elmList.find(elm => elm.rawElmId === a.id);
                const elmB = elmList.find(elm => elm.rawElmId === b.id);
                return (elmA?.sortOrd || 0) - (elmB?.sortOrd || 0);
              });
            }
        } catch(err) {
          _vm.defaultApiErrorHandler(err); //기본에러처리
        } finally {
          _vm.loading(false);
        }
      }

      const elmentGridData = _vm.localData.elementData;
      const oldColumnDefs = elmentGridData.columnDefs;
      if (_vm.rawInfo.rawSeq == 0 || (_vm.isAdmin && _vm.eumLoginUser.userSeq == _vm.managerSeq)) {
        oldColumnDefs.find(item => item.field === 'drag').hide = false;
        oldColumnDefs.find(item => item.field === 'setButton').hide = false;
      }else{
        oldColumnDefs.find(item => item.field === 'drag').hide = true;
        oldColumnDefs.find(item => item.field === 'setButton').hide = true;
      }
      if(_vm.$refs[elmentGridData.gridId]){
        _vm.$refs[elmentGridData.gridId].api.setColumnDefs(oldColumnDefs);
      }

    },

    onAddValue(params){
      const _vm = this;
      const newItem = params.data;
      
      // `find()`를 사용하여 중복된 ID가 있는지 확인
      if (_vm.localData.elementData.checkedList.find(item => item.id === newItem.id)) {
        _vm.$refs["alertModal"].open({
          title: _vm.$t('') || '성분 추가',
          titleHtml : _vm.$t('') || '이미 추가된 성분입니다.',
        });
        return;
      }

      const addItem = _vm.ingdList.find(item => item.id === newItem.id);
      _vm.localData.elementData.checkedList 
        = [..._vm.localData.elementData.checkedList, addItem];
    },

    onDelValue(params){
      const _vm = this;

      _vm.localData.elementData.checkedList = _vm.localData.elementData.checkedList.filter(item => item.id !== params.data.id);
    },

    async onChangeValue(){
      const _vm = this;

      //성분 배열 정리
      const checkList = _vm.localData.elementData;
      const gridRef = _vm.$refs[checkList.gridId];

      const elmInfo = {
        elmList : []
      };

      const allRows = gridRef.api.getModel().rowsToDisplay; // 전체 row 배열 가져오기
      let count = 0;
      for (const row of allRows) {
        elmInfo.elmList.push({
          rawElmId : row.data.id,
          sortOrd : ++count
        });        
      }

      _vm.$emit("update:data", elmInfo);
      const preData = checkList.checkedList.map((item) => {
        return {
          rawSeq: _vm.rawInfo.rawSeq,
          rawElmSeq: null,
          id: item.id,
          repNameKo: item.rep_name,
          repNameEn: item.rep_name_en,
          kcaiId: item.kcai_id,
          ewgId: item.ewg_id,
          hwIngdId: item.hw_ingd_id,
          inci: item.inci,
          namesKo: item.names_ko,
          namesEn: item.names_en,
          casNo: item.cas_no,
          ecNo: item.ec_no,
          formula: item.formula,
          expUrl1: item.exp_url1,
          expUrl2: item.exp_url2,
          ewgScoreMin: item.ewg_score_min,
          ewgScore: item.ewg_score,
          ewgDataLabel: item.ewg_data_label,
          ewgUrl: item.ewg_url,
          sortOrd: item.sort_ord,
          ewgChngDttm: item.ewg_chng_dttm,
        };
      });
      _vm.$emit("update:preData", preData);
    },

    //양식의 상태값을 확인
    updateInvalidState(invalid) {
      const _vm = this;
      const isChanged = _vm.localInvalidState != invalid;
      _vm.localInvalidState = invalid;
      if(isChanged){
        //상태값이 변경되면 부모에게 알림
        _vm.$emit("update:valid", !_vm.localInvalidState);
      }
      return false; // v-if에서 항상 false를 반환하여 실제 DOM에 렌더링되지 않게 함
    },

     //검색
     onSearch() {
      const _vm = this;
      _vm.loadList(1);
    },

    //고정 JSON 데이터 로드, 성분목록
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
        const res = await getSearchIngredients(params);
        const { total, list, maxScore, version } = res.resultData;

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
            //rowIndex : listData.nextRowIndex++, //rowIndex 설정
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

      const params = {
        page: pInfo.curPage,
        rowSize: 100,

        text: searchOp.searchText,
      };

      return params;
    },  
    
    async loadIngdList(){
      const _vm = this;

      _vm.loading(true);
      try {
        const res = await getStaticIngredientList();
        //console.debug(res.data);
        const list  = res.data;
        const total = list.length;
        _vm.ingdList = [...list];

      } catch (err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }

    },

    docReady(){
      const _vm = this;
    },
  }  
}
</script>

<style lang="scss" scoped>
.erns-ag-grid-vue{
  min-height: calc(70vh - 360px);  
}

#elmInfo {
  
  .section-content > div:nth-child(n + 2) {
    padding-top: 0;
  }

  .section-content{
    .base-add .header{
      border-bottom: none;
    }
  }
}
</style>

<style lang="scss">

</style>