<template>
  <div id="coching-bo-cochingtv-main">
    <!-- 검색조건 -->

    <b-card no-body>
      <b-card-body>
        <b-row>

          <b-col cols="12" md="3">
            <b-form-group label="사용 여부" label-for="search-cochingtv-useYn">
              <b-form-radio-group
                id="search-cochingtv-useYn"
                v-model="listData.sc.useYn"
                buttons
                button-variant="outline-primary"
                @change="onSearch"
                size="sm"
                :options="[
                  {text:'전체', value: ''},{text:'사용', value: 'Y'},{text:'미사용', value: 'N'}
                ]"
              >
              </b-form-radio-group>
            </b-form-group>
          </b-col>

          <b-col cols="12" md="6">
            <b-form-group label="제목" label-for="search-cochingtv-titleL">
              <b-input-group>
                <b-form-input
                  id="search-cochingtv-titleL"
                  v-model="listData.sc.titleL"
                  @keyup.enter="onSearch"
                  placeholder="제목을 검색하세요."
                  size="sm"
                ></b-form-input>                
                <b-input-group-append v-show="listData.sc.titleL">
                  <b-button variant="outline-secondary" 
                    @click="listData.sc.titleL = ''"
                    size="sm"
                    v-b-tooltip.hover
                    title="검색어 초기화"
                    ><feather-icon icon="XIcon" size="14" />
                  </b-button>
                </b-input-group-append>              
                <b-input-group-append>
                  <b-button variant="outline-primary" 
                    @click="onSearch"
                    size="sm"
                    v-b-tooltip.hover
                    title="검색"
                    ><feather-icon icon="SearchIcon" size="14" />
                  </b-button>
                </b-input-group-append>
              </b-input-group>
            </b-form-group>
          </b-col>

          <b-col cols="12" md="3" >
            <div class="erns-filter-div mt-1 text-right">
              <b-form-group label="">

                <b-button
                  :disabled="listData.isDisabledAllSave"
                  v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                  variant="outline-primary"
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
                  class="mr-1"
                  @click="onClickSaveOrder()"
                  size="sm"
                  v-b-tooltip.hover
                  title="순서 저장"
                  ><feather-icon icon="BarChartIcon" size="14" />
                </b-button>

                <b-button
                  v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                  variant="primary"
                  class=" "
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

    <!-- listData.sc:{{ listData.sc }} -->

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
        rowDragManaged="true"
        :rowHeight="200"

        suppressRowTransform=true
        
      >
      </ag-grid-vue>
    </div>

    <!-- 등록 -->
    <form-modal
      v-model="regsterModal.show"
      @submit="onClickFormModal"
    />

  </div>
</template>

<script>
import { getCodes } from '@/api/coching-bo/comm/code';
import ernsUtils from "@/components/mixins/ernsUtils";
import Ripple from "vue-ripple-directive";
import draggable from 'vuedraggable';
import "ag-grid-community/styles/ag-grid.css"; // Mandatory CSS required by the Data Grid
import "ag-grid-community/styles/ag-theme-quartz.css";

import { AgGridVue } from "ag-grid-vue";
import ernsAgGrid from '@/components/ernsAgGrid/ernsAgGrid';
import { AG_GRID_COMMON_COLUMN_DEFS } from '@/constants/agGrid';

import { DEF_SEARCH_OPT } from "@/constants/cochingtv";
import {
  getCochigtvList,
  addCochigtv,
  setCochingtvState,
  delCochingtv,
  setCochingtvOrder,
  getYoutubeApiInfo
} from "@/api/coching-bo/cochingtv/cochingtv";

import ButtonCellRenderer from "@/components/ernsAgGrid/ButtonCellRenderer.vue";
import EwgScoreCellRenderer from "@/components/ernsAgGrid/EwgScoreCellRenderer.vue";
import ImageCellRenderer from "@/components/ernsAgGrid/ImageCellRenderer.vue";
import ToogleButtonCellRenderer from "@/components/ernsAgGrid/ToogleButtonCellRenderer.vue";
import YoutubeCellRenderer from "@/components/ernsAgGrid/YoutubeCellRenderer.vue";
import TextareaEditCellRenderer from "@/components/ernsAgGrid/TextareaEditCellRenderer.vue";
import SelectCellRenderer from "@/components/ernsAgGrid/SelectCellRenderer.vue";
import IconButtonCellRenderer from "@/components/ernsAgGrid/IconButtonCellRenderer.vue";

import FormModal from './FormModal.vue';

import cochingUtils from "@/components/mixins/cochingUtils";

const GRID_ID_MAIN = 'main-grid';

export default {
  mixins: [ernsUtils, ernsAgGrid, cochingUtils],
  components: {
    draggable, 
    AgGridVue,    
    ButtonCellRenderer,
    ToogleButtonCellRenderer,
    EwgScoreCellRenderer,
    ImageCellRenderer,
    YoutubeCellRenderer,
    FormModal,
    TextareaEditCellRenderer,
    SelectCellRenderer,
    IconButtonCellRenderer
  },
  directives: {
    Ripple,
  },
  props: {},
  computed: {},
  filters: {},
  async mounted() {
    const _vm = this;

    await _vm.loadCodes();

    //_vm.getInitParam();
    await _vm.loadList(1);
    await _vm.setYoutubeApiInfoList();
  },
  beforeDestroy() {
    const _vm = this;
  },  
  data() {
    const _vm = this;
    
    // Ag-Grid Column Definition
    const AG_GRID_COLUMN_DEFS = [
      { headerName: "", field: "drag", rowDrag: true, width: 50 
        , cellClass: (params) => {
            const retClass = ['d-flex', 'align-items-center', 'justify-content-center'];          
            return retClass;
          }
      }, // 드래그 컬럼 추가
      { hide: true,
        headerName: "Row ID", field: "rowId"          , width: 50 
        , valueGetter: (params) => params.node.id // 내부 rowId 출력
      },
      { headerName: "번호"        , field: "index"        , width: 70  , cellClass: 'flex-center'},
      {
        headerName: "구분"        , field: "category"     , width: 100  , cellClass: 'flex-center',
        editable: true,
        cellEditor: "SelectCellRenderer",
        cellEditorParams: (params) => {
          const codeList = _vm.codes.CATE;
          return {
            options: codeList.map(item => ({
              text: item.label,
              value: item.value
            }))
          };
        },
        cellRenderer: (params) => {
          const value = params.value;
          const code = _vm.codes.CATE.find(item => item.value === value);
          return code ? code.label : value;
        },
        cellClass: (params) => {
          const retClass = [
            'd-flex'
            , 'align-items-center'
            , 'justify-content-center'
          ];    
          const editData = _vm.ernsAgGrid_getModifiedCells(_vm.listData.gridId, params.node.id, params.colDef.field);
          if(editData){
            retClass.push('erns-agGrid-edit-cell');
          }      
          return retClass;
        },
      },
      { headerName: "YouTube"   , field: "ytUrl"   , width: 320 //, cellClass: 'text-center'
          , editable: true
          ,
          cellRenderer: "YoutubeCellRenderer", // 렌더러 지정
          cellRendererParams: {
            width: 300,
            height: 200
          },
          cellClass: (params) => {
            const retClass = [
              'd-flex'
              , 'align-items-center'
              , 'justify-content-center'
            ];          
            return retClass;
          },
          onCellValueChanged: (params) => {
            const _vm = this;
            if(params.data?.ytUrl){
              // 유튜브 URL 변경시 정보 가져오기
              _vm.fetchYoutubeInfo(params.data);
            }
          },
      },
      { headerName: "제목"        , field: "title"        , width: 250
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

      { headerName: "내용"      , field: "content"        , width: 300
        , editable: true
        , cellEditor: "TextareaEditCellRenderer"
        , cellRenderer: (params) => {
          return params.value ? params.value.replace(/\n/g, '<br>') : '';
        }
        , cellClass: (params) => {
          const retClass = ['flex-left'];
          const editData = _vm.ernsAgGrid_getModifiedCells(_vm.listData.gridId, params.node.id, params.colDef.field);
          if(editData){
            retClass.push('erns-agGrid-edit-cell');
          }

          return retClass;
        }
        , wrapText: true
      },
      { headerName: "해시태그"      , field: "hashtag"        , width: 150
        , editable: true
        , cellClass: (params) => {
          const retClass = ['flex-left'];
          const editData = _vm.ernsAgGrid_getModifiedCells(_vm.listData.gridId, params.node.id, params.colDef.field);
          if(editData){
            retClass.push('erns-agGrid-edit-cell');
          }

          return retClass;
        }},
      { headerName: "조회수"      , field: "views"        , width: 100  , cellClass: 'flex-center'
        , valueFormatter: (params) => params.value ? params.value : '-'
      },
      { headerName: "영상 등록일"      , field: "ytDttm"       , width: 150  , cellClass: 'flex-center'
        , autoHeight: true
        , cellStyle: { whiteSpace: 'pre-line', textAlign: 'center' }
        , valueFormatter: (params) => params.value ? this.$options.filters.eFmtDateTime(params.value) : '-'
      },
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
      {
        headerName: "삭제"      , field: "delButton"  , width: 70  , cellClass: 'flex-center',
        cellRenderer: "IconButtonCellRenderer", // 렌더러 지정
        cellRendererParams: {
          icon: "TrashIcon",
          tooltip: "삭제",
          iconSize: "20",
          variant : " ",
          btnClass: 'btn-outline-secondary btn-no-border',
          action: (params) => {
            this.onDeleteRow(params);
          },
        },
        cellClass: (params) => {
          const retClass = ['d-flex', 'align-items-center', 'justify-content-center'];
          return retClass;
        }
      }, 
      

      { headerName: "내용"      , field: "content"        , width: 500
        , editable: true
        , cellEditor: "TextareaEditCellRenderer"
        , cellRenderer: (params) => {
          return params.value ? params.value.replace(/\n/g, '<br>') : '';
        }
        , cellClass: (params) => {
          const retClass = ['flex-left'];
          const editData = _vm.ernsAgGrid_getModifiedCells(_vm.listData.gridId, params.node.id, params.colDef.field);
          if(editData){
            retClass.push('erns-agGrid-edit-cell');
          }

          return retClass;
        }
        , wrapText: true
      },

      { hide : true,
        headerName: "수정여부"  , field: "_isEditRow" , width: 100  , cellClass: 'flex-center'},     
      
    ];

    return {
      codes : {
        CATE : [],
      },

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

          stopEditingShortcuts: (params) => {
            console.log(params.column.getColId()); 
            if (params.column.getColId() === 'content') {
              console.log('content');
              return ['tab', 'shift+tab', 'f2', 'escape'];
            }
            return ['enter', 'tab', 'shift+tab', 'f2', 'escape'];
          },
        },
        columnDefs: AG_GRID_COLUMN_DEFS,
        frameworkComponents :{
          ButtonCellRenderer,
          ToogleButtonCellRenderer,
          EwgScoreCellRenderer,
          YoutubeCellRenderer,
          TextareaEditCellRenderer,
          SelectCellRenderer,
        },

        sc: { ...DEF_SEARCH_OPT.sc },
        pi: { ...DEF_SEARCH_OPT.pi },
        rowData: [],
        rawList: [],
      },

      regsterModal :{
        show : false,

        data : [
        ]
      },
    };
  },
  methods: {
    async loadCodes(){
      const _vm = this;
      const cateList = await getCodes({grpCode:'CH016', etc5: 'ko', rowSize:-1});
      _vm.codes.CATE = _vm.eumConvertToVueSelectOption(cateList.resultData.list, 'etc1');
    },

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
      const checkFields = ["_isEditRow", "ytUrl", "title", "hashtag", "useYn", "category"];
      if (checkFields.includes(params.column.colId)) {   
        params.api.refreshCells({
          columns: ["setButton"], // 새로고침
          rowNodes: [params.node],
          force : true
        });
      }

      listData.isDisabledAllSave = _vm.checkIsDisabledAllSave();
    },

    //각 Row 필수값 체크
    validateRowValue(rowData){
      /*
      if("" == (rowData.name || '').trim()) {
        return false; //키워드 필수
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
          tvSeq : rowNode.data.tvSeq || 0,
          ytUrl : rowNode.data.ytUrl,
          title : rowNode.data.title,
          hashtag : rowNode.data.hashtag,
          views : rowNode.data.views,
          ytDttm : rowNode.data.ytDttm,
          useYn : rowNode.data.useYn,
          content : rowNode.data.content,
          category : rowNode.data.category,
        });
      });

      if(params.list.length <= 0){
        _vm.alertError("변경된 정보가 없습니다.");
        return;
      }

      const result = await _vm.$swal({
        title: '확인',
        text: `${params.list.length}개 코칭TV 설정을 저장 하시겠습니까?`,
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
          await setCochingtvState(params);
          await _vm.onSearch();
          _vm.alertSuccess("코칭TV 설정 값이 변경되었습니다.");
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
        tvSeq: gridParams.data.tvSeq || 0,
        ytUrl: gridParams.data.ytUrl,
        title: gridParams.data.title,
        hashtag: gridParams.data.hashtag,
        views: gridParams.data.views,
        ytDttm: gridParams.data.ytDttm,
        useYn: (gridParams.data.useYn || '').trim(),    
        content: gridParams.data.content,
        category: gridParams.data.category,
      };

      //Validation
      if(!params.tvSeq){
        return;
      }

      const result = await _vm.$swal({
        title: '확인',
        text: `코칭TV 상태를 변경 하시겠습니까?`,
        showCancelButton: true,
        confirmButtonText: '변경',
        customClass: {
          confirmButton: 'btn btn-danger ml-1',
          cancelButton: 'btn btn-outline-primary',
        },
        buttonsStyling: false,
      });

      _vm.loading(true);
      try {        

        if(result.value){
          await setCochingtvState({list:[params]});
          _vm.alertSuccess("코칭TV 상태가 변경되었습니다.");
          await _vm.onSearch();
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
        tvSeq : gridParams.data.tvSeq || 0, 
        delYn : 'Y'       
      };

      //Validation
      if(!params.tvSeq){
        return;
      }

      const result = await _vm.$swal({
        title: '확인',
        text: `코칭TV를 삭제 하시겠습니까?`,
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
          await delCochingtv({list:[params]});
          _vm.alertSuccess("코칭TV가 삭제 되었습니다.");
          await _vm.onSearch();
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
          tvSeq : row.data.tvSeq,
          sortOrd : ++count
        });        
      }

      const result = await _vm.$swal({
        title: '확인',
        text: `코칭TV 순서를 저장 하시겠습니까?`,
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
          await setCochingtvOrder(params);
          _vm.alertSuccess("코칭TV 순서가 저장 되었습니다.");
          await _vm.onSearch();
        }
      } catch (err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
    },

    

    //검색
    async onSearch() {
      const _vm = this;
      await _vm.loadList(1);
      await _vm.setYoutubeApiInfoList();
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
        const res = await getCochigtvList(params);
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
            views : '',
            ytDttm : '',
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
        rowSize: 60,

        titleL: searchOp.titleL,
        useYn: searchOp.useYn,
      };

      return params;
    },  

    async setYoutubeApiInfoList() {
      const _vm = this;
      const listData = _vm.listData;
      const allRows = [...listData.rowData];

      try {
        const youtubeInfos = await Promise.all(allRows.map(v => _vm.setYoutubeApiInfo(v)));
        
        allRows.forEach((item, index) => {
          const youtubeInfo = youtubeInfos[index];
          if (youtubeInfo) {
            item.views = youtubeInfo.viewCount || '';
            item.ytDttm = youtubeInfo.publishedAt || '';
          }
        });
      } catch (err) {
        console.warn('Youtube API 가져오기 오류:', err);
      }
      // ag-Grid에 변경사항 알림
      _vm.listData.rowData = allRows;
      
      // ag-Grid API를 사용하여 데이터 변경 알림
      const gridRef = _vm.$refs[listData.gridId];
      if (gridRef && gridRef.api) {
        gridRef.api.applyTransaction({ update: allRows });
      }
    },

    async setYoutubeApiInfo(item){
      try {
        if (!item.ytUrl) {
          console.warn('URL이 없습니다:', item);
          return;
        }

        const params = {
          ytUrl : item.ytUrl,
        };
        const youtubeInfoRes = await getYoutubeApiInfo(params);
        const youtubeInfo = youtubeInfoRes.resultData;

        return youtubeInfo;

      } catch (err) {
        console.warn('YouTube API 호출 오류:', err);
        return null;
      }
    },

    async fetchYoutubeInfo(param) {
        const _vm = this;
        if (!param || !param.ytUrl) return;
        _vm.loading(true);
        try {
          const youtubeInfo = await _vm.setYoutubeApiInfo(param);

          if(youtubeInfo) {
            param.title = youtubeInfo.title || '';
            param.views = youtubeInfo.viewCount || '';
            param.ytDttm = youtubeInfo.publishedAt || '';
          } else {
            param.title = '';
            param.views = '';
            param.ytDttm = '';
            _vm.alertError('유튜브 정보를 가져오는데 실패했습니다.');
          }

          const gridRef = this.$refs[this.listData.gridId];
          if (gridRef && gridRef.api) {
            gridRef.api.applyTransaction({ update: [param] });
          }

        } catch (err) {
          _vm.alertError(err);
        } finally {
          _vm.loading(false);
        }
    },

    /* 모달 함수 */
    // 등록 버튼 클릭 시 모달 열기
    onClickRegster() {
      const _vm = this;
      _vm.regsterModal.show = true;
    },

    // 등록/수정 모달 확인 버튼 클릭 처리
    async onClickFormModal(formData) {
      const _vm = this;

      var isRegMode = (formData.tvSeq || 0) <= 0;

      try {
        _vm.loading(true);
        
        // TODO: API 호출하여 데이터 저장
        let res = null;
        if(isRegMode){
          res = await addCochigtv(formData);
          await _vm.alertSuccess(`코칭TV가 등록 되었습니다.`);
        }else{
          //res = await setCochigtv(formData);
          //await _vm.alertSuccess(`코칭TV가 수정 되었습니다.`);
        }

        // 성공 시 모달 닫기 및 목록 새로고침
        _vm.regsterModal.show = false;
        await _vm.loadList(_vm.listData.pi.curPage);
        await _vm.setYoutubeApiInfoList();
      } catch (err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
    },


  },
};
</script>

<style lang="scss" scoped>
.erns-ag-grid-vue{
  min-height: calc(100vh - 360px);  
}

</style>
