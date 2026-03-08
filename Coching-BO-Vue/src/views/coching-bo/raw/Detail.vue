<template>
  <div class="sys-mid-wrap">

    <b-card no-body>
      <validation-observer ref="partnerInfoForm">
        <!-- 입력 영역 -->
        <b-card-group class="mt-1">
          <b-card title="기본정보" class="col-md-12">
            <b-row>

              <b-col md="6" ref="rawName">
                <b-form-group label-for="rawName">
                  <template v-slot:label>
                    <span>원료명</span>
                  </template>
                  <validation-provider #default="{ errors }" name="'원료명'" rules="required">
                    <b-form-input id="rawName" v-model="localData.rawInfo.rawName"
                      :state="errors.length > 0 ? false : null" placeholder="원료사명을 입력하세요." autocomplete="off"
                      size="sm"
                      :readonly="true" />
                  </validation-provider>
                </b-form-group>
              </b-col>

              <b-col md="6" ref="prodCompany">
                <b-form-group label-for="prodCompany">
                  <template v-slot:label>
                    <span>제조사</span>
                  </template>
                  <validation-provider #default="{ errors }" name="'제조사'" rules="required">
                    <b-form-input id="prodCompany" v-model="localData.rawInfo.prodCompany"
                      :state="errors.length > 0 ? false : null" placeholder="제조사명을 입력하세요." autocomplete="off"
                      size="sm"
                      :readonly="true" />
                  </validation-provider>
                </b-form-group>
              </b-col>

              <b-col md="6" ref="prodCountry">
                <b-form-group label-for="prodCountry">
                  <template v-slot:label>
                    <span>제조국</span>
                  </template>
                  <validation-provider #default="{ errors }" name="'제조국'" rules="required">
                    <b-form-input id="prodCountry" v-model="localData.rawInfo.prodCountry"
                      :state="errors.length > 0 ? false : null" placeholder="제조국을 입력하세요." autocomplete="off"
                      size="sm"
                      :readonly="true" />
                  </validation-provider>
                </b-form-group>
              </b-col>

              <b-col md="6" ref="supplier">
                <b-form-group label-for="supplier">
                  <template v-slot:label>
                    <span>공급사</span>
                  </template>
                  <validation-provider #default="{ errors }" name="'공급사'" rules="required">
                    <b-form-input id="supplier" v-model="localData.rawInfo.supplier"
                      :state="errors.length > 0 ? false : null" placeholder="공급사명을 입력하세요." autocomplete="off"
                      size="sm"
                      :readonly="true" />
                  </validation-provider>
                </b-form-group>
              </b-col>

              <b-col md="6" ref="weight">
                <b-form-group label-for="weight">
                  <template v-slot:label>
                    <span>무게</span>
                  </template>
                  <validation-provider #default="{ errors }" name="'무게'" rules="required">
                    <b-form-input id="weight" v-model="localData.rawInfo.weight" :state="errors.length > 0 ? false : null"
                      size="sm"
                      placeholder="무게를 입력하세요." autocomplete="off" :readonly="true" />
                  </validation-provider>
                </b-form-group>
              </b-col>

              <b-col md="6" ref="managerStr">
                <b-form-group label-for="managerStr">
                  <template v-slot:label>
                    <span>담당자</span>
                    <span class="text-pink">*</span>
                  </template>
                  <validation-provider #default="{ errors }" name="'담당자'" rules="required">
                    <b-form-input id="managerStr" v-model="localData.rawInfo.managerStr"
                      size="sm"
                      :state="errors.length > 0 ? false : null" autocomplete="off" :readonly="true" />
                  </validation-provider>
                </b-form-group>
              </b-col>

            </b-row>
          </b-card>

        </b-card-group>
        <!-- // 입력 영역 -->

        <!-- <b-card-group>
          <b-card title="성분" class="col-md-12">
            <b-table class="position-relative" responsive striped hover :items="localData.rawInfo.elmlist"
              :fields="localData.rawInfo.elmFields" ref="elmListTable" no-local-sorting primary-key="index" show-empty
              empty-text="검색된 내용이 없습니다.">

              <template #cell(index)="data">
                {{ data.index + 1 }}
              </template>

            </b-table>

          </b-card>
        </b-card-group> -->

        <b-card-group>
          <b-card title="성분" class="col-md-12">
            <div class="erns-ag-grid-vue">
            <ag-grid-vue       
              class="ag-theme-quartz"
              :ref="listData.gridId"
              :gridOptions="listData.gridOptions"
              :columnDefs="listData.columnDefs"
              :rowData="listData.rowData"
              @gridReady="(params) => ernsAgGrid_registerGrid(listData.gridId, params, listData.gridOptions)"
              
              :frameworkComponents="listData.frameworkComponents"              
              pagination=false
              suppressRowTransform=true
              >
              </ag-grid-vue>          
            </div>
          </b-card>
        </b-card-group>

        <b-card-group>
          <b-card title="성분구분" class="col-md-12">
            <b-row>

              <b-col md="12" ref="type001Str">
                <b-form-group label-for="type001Str">
                  <template v-slot:label>
                    <span>효능</span>
                  </template>
                  <b-form-input id="type001Str" v-model="localData.rawInfo.type001Str" :readonly="true" />
                </b-form-group>
              </b-col>
              <b-col md="12" ref="type002Str">
                <b-form-group label-for="type002Str">
                  <template v-slot:label>
                    <span>기능</span>
                  </template>
                  <b-form-input id="type002Str" v-model="localData.rawInfo.type002Str" :readonly="true" />
                </b-form-group>
              </b-col>
              <b-col md="12" ref="type003Str">
                <b-form-group label-for="type003Str">
                  <template v-slot:label>
                    <span>제품</span>
                  </template>
                  <b-form-input id="type003Str" v-model="localData.rawInfo.type003Str" :readonly="true" />
                </b-form-group>
              </b-col>
              <b-col md="12" ref="type004Str">
                <b-form-group label-for="type004Str">
                  <template v-slot:label>
                    <span>성상</span>
                  </template>
                  <b-form-input id="type004Str" v-model="localData.rawInfo.type004Str" :readonly="true" />
                </b-form-group>
              </b-col>
              <b-col md="12" ref="type005Str">
                <b-form-group label-for="type005Str">
                  <template v-slot:label>
                    <span>원물</span>
                  </template>
                  <b-form-input id="type005Str" v-model="localData.rawInfo.type005Str" :readonly="true" />
                </b-form-group>
              </b-col>

            </b-row>
          </b-card>
        </b-card-group>

        <b-card-group>
          <b-card title="원료자료" class="col-md-12">
            <p>{{ localData.rawInfo.detail.rawDesc }}</p>
            <div v-for="(item, idx) of localData.rawInfo.detail.rawDetail.filelist" :key="idx" class="img">
              <img :src="eumFileImagePath(item.fileId, '0')" alt="" />
            </div>
            <div v-html="localData.rawInfo.detail.rawDetail"></div>
          </b-card>
        </b-card-group>

      </validation-observer>
    </b-card>


    <!-- rawInfo:{{localData.rawInfo}}<br/> -->

  </div>
</template>
<script>
import vSelect from 'vue-select';
import ernsUtils from '@/components/mixins/ernsUtils';
import rndMixin from "@/components/rnd/rndMixin";
import Ripple from 'vue-ripple-directive'
import "ag-grid-community/styles/ag-grid.css"; // Mandatory CSS required by the Data Grid
import "ag-grid-community/styles/ag-theme-quartz.css";

import _ from 'lodash';

import { getRaw } from '@/api/coching-bo/raw/raw';

import { extend } from 'vee-validate';
import { required, passwordCochingJoin, noWhiteSpace, min, max, telOnlyNumber, email } from '@validations';
import { readonly } from 'vue';
import { AgGridVue } from "ag-grid-vue";
import ernsAgGrid from '@/components/ernsAgGrid/ernsAgGrid';
import { AG_GRID_COMMON_COLUMN_DEFS } from '@/constants/agGrid';

import { DEF_SEARCH_OPT, DEF_ROW } from "@/constants/cochingIngredient";

import ButtonCellRenderer from "@/components/ernsAgGrid/ButtonCellRenderer.vue";
import EwgScoreCellRenderer from "@/components/ernsAgGrid/EwgScoreCellRenderer.vue";
import ImageCellRenderer from "@/components/ernsAgGrid/ImageCellRenderer.vue";
import IconButtonCellRenderer from "@/components/ernsAgGrid/IconButtonCellRenderer.vue";
import EsHighlightTextCellRenderer from "@/components/ernsAgGrid/EsHighlightTextCellRenderer.vue";
import EsHighlightArrayTextCellRenderer from "@/components/ernsAgGrid/EsHighlightArrayTextCellRenderer.vue"; 
  
const DEF_RAW_INF = {
  rawSeq: null,
  ptnSeq: null,
  rawName: '',
  prodCompany: '',
  prodCountry: '',
  supplier: '',
  managerlist: [],
  managerStr: '',
  elmlist: [],
  elmFields: [],
  typelist: [],
  type001Str: '',
  type002Str: '',
  type003Str: '',
  type004Str: '',
  type005Str: '',
  doclist: [],
  detail: {
      rawDetailSeq: null,
      rawSeq: null,
      membSeq: null,
      title: '',
      hashtag: '',
      filelist: [],
      fileId: '',
      rawDesc: '',
      rawDetail: '', // 에디터 내용
  },
};
  
const DEFAULT_ELM_TABLE_COLUMNS = [
  { key: 'index'      , label: '번호'           , sortable: false , class: 'text-center' },
  { key: 'elmName'    , label: '성분명'    , sortable: false , class: 'text-center'},
  { key: 'casNo'      , label: 'casNo'           , sortable: false , class: 'text-center' },
];

const GRID_ID_MAIN = 'ingredient-grid';
const DEF_INGREDIENT_LIST = [];
  
export default {
  name: 'coching-bo-partner-editForm',
  mixins: [ernsUtils, ernsAgGrid, rndMixin],
  components : {
    vSelect,
    AgGridVue,    
    ButtonCellRenderer,
    EwgScoreCellRenderer,
    ImageCellRenderer,
    IconButtonCellRenderer,
    EsHighlightTextCellRenderer,
    EsHighlightArrayTextCellRenderer,
  },
  directives: {
    Ripple
  },
  computed : {

    showRows(){
      var _vm = this;
      return _vm.$store.state.erns.showRows;
    },
  },
  watch: {
    // 라우트가 변경되면 메소드를 다시 호출됩니다.
    '$route': 'fetchData',

  },
  data(){
    const _vm = this;

      // Ag-Grid Column Definition
    const AG_GRID_COLUMN_DEFS = [
      { hide: true,
        headerName: "Row ID", field: "rowId"          , width: 70 
        , valueGetter: (params) => params.node.id // 내부 rowId 출력
      },
      { headerName: "번호"        , field: "index"        , width: 70  , cellClass: 'flex-center selectable-text'} ,
      { headerName: "EWG"         , field: "ewgScore"     , width: 100  , cellClass: 'flex-left selectable-text',
        cellRenderer: "EwgScoreCellRenderer", // 렌더러 지정
        cellRendererParams: (params) => ({
          ewgVal : `${params.node.data['ewg_score_min']}-${params.node.data['ewg_score']}`,
          ewgDataLabel: params.node.data.ewg_data_label,
        }),          
      },
      { headerName: "성분명"      , field: "rep_name"     , width: 150  , cellClass: 'flex-left selectable-text',
        cellRenderer: "EsHighlightTextCellRenderer", // 렌더러 지정
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
        cellStyle: { "white-space": "normal" } // 여러 줄로 표시
      },
      { hide: true,
        headerName: "동의어"      , field: "names_ko"     , width: 200  , cellClass: 'flex-left selectable-text',
        cellRenderer: "EsHighlightArrayTextCellRenderer", // 렌더러 지정
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
        cellStyle: { "white-space": "normal" } // 여러 줄로 표시
      },
      { headerName: "INCI(영문명)", field: "inci"         , width: 300  , cellClass: 'flex-left selectable-text',
        cellRenderer: "EsHighlightArrayTextCellRenderer", // 렌더러 지정
        cellRendererParams: {
          highlightPrefix: ["rep_name_en"],
        },
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
        cellStyle: { "white-space": "normal" } // 여러 줄로 표시
      },
      // { headerName: "영문명"      , field: "rep_name_en"  , width: 200  , cellClass: 'flex-left selectable-text',
      //   cellRenderer: "EsHighlightTextCellRenderer", // 렌더러 지정          
      //   wrapText: true, // 자동 줄바꿈 활성화
      //   autoHeight: true, // 행 높이를 자동 조정
      //   cellStyle: { "white-space": "normal" } // 여러 줄로 표시
      // },
      { hide: true,
        headerName: "영문동의어"  , field: "names_en"     , width: 200  , cellClass: 'flex-left selectable-text',
        cellRenderer: "EsHighlightArrayTextCellRenderer", // 렌더러 지정
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
        cellStyle: { "white-space": "normal" } // 여러 줄로 표시
      },
      { headerName: "CAS NO"      , field: "cas_no"       , width: 150  , cellClass: 'flex-left selectable-text',
        cellRenderer: "EsHighlightArrayTextCellRenderer", // 렌더러 지정
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
      },
      { headerName: "EC NO"       , field: "ec_no"        , width: 150  , cellClass: 'flex-left selectable-text',
        cellRenderer: "EsHighlightArrayTextCellRenderer", // 렌더러 지정
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
      },      
      { headerName: "시성식"      , field: "formula"      , width: 200  , cellClass: 'flex-left selectable-text',
        cellRenderer: "EsHighlightTextCellRenderer", // 렌더러 지정        
      },
      { headerName: "용도"        , field: "purposes"     , width: 250  , cellClass: 'flex-left selectable-text',
        cellRenderer: "EsHighlightTextCellRenderer", // 렌더러 지정        
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
        cellStyle: { "white-space": "normal" } // 여러 줄로 표시
      },
      
      
      
      { hide: true, 
        headerName: "exp_url_1"         , field: "exp_url_1"     , width: 200  , cellClass: 'flex-left selectable-text'},
      { hide: true, 
        headerName: "exp_url_2"         , field: "exp_url_2"     , width: 200  , cellClass: 'flex-left selectable-text'},
      { hide: true, 
        headerName: "ewg_score_min"     , field: "ewg_score_min"  , width: 70  , cellClass: 'flex-left selectable-text'},
      { hide: true, 
        headerName: "ewg_score"         , field: "ewg_score"      , width: 70  , cellClass: 'flex-left selectable-text'},
      { hide: true, 
        headerName: "ewg_data_label"    , field: "ewg_data_label" , width: 70  , cellClass: 'flex-left selectable-text'},
      { hide: true,
        headerName: "ewg_rawno_id"      , field: "ewg_rawno_id"   , width: 70  , cellClass: 'flex-left selectable-text'},
      { hide: true,
        headerName: "ewg_url"           , field: "ewg_url"        , width: 70  , cellClass: 'flex-left selectable-text'},
      
      { hide : true,
        headerName: "수정여부"  , field: "_isEditRow" , width: 100  , cellClass: 'flex-center'},      
    ];


    return {
      //전성분 목록
      listData: {
        gridId : GRID_ID_MAIN,
        nextRowIndex : 100,
        isDisabledAllSave : true,
        gridOptions : {
          isEditData : false,
          immutableData : true,
          defaultColDef : AG_GRID_COMMON_COLUMN_DEFS,
          getRowId: (params) => params.data.rowIndex, // 각 행의 ID를 'rowIndex' 로 지정
          overlayNoRowsTemplate: '<div class="text-center">등록된 성분 목록이 없습니다.</div>',          
        },
        columnDefs: AG_GRID_COLUMN_DEFS,
        frameworkComponents :{
          ButtonCellRenderer,
          EwgScoreCellRenderer,
          EsHighlightTextCellRenderer,
        },

        pi: { ...DEF_SEARCH_OPT.pi },
        rowData: [...DEF_INGREDIENT_LIST],
        rawList: [...DEF_INGREDIENT_LIST],
        debugRowData: [...DEF_INGREDIENT_LIST],
      },

      localData: {
        rawInfo: { ...DEF_RAW_INF },
      },
    }
  },
  async mounted() {
    const _vm = this;

    await _vm.loadCodes();
    await _vm.fetchData();

  },
  updated() {
    const _vm = this;
    // v-html 내용이 업데이트될 때마다 이미지 스타일 적용
    _vm.$nextTick(() => {
      _vm.applyImageStyles();
    });
  },
  beforeDestroy() {
    const _vm = this;
  },
  methods: {

    async fetchData() {
      const _vm = this;

      _vm.loading(true);

      try {
        //원료사 상세
        await _vm.loadRaw();

      } catch (err) {
        console.error(err)
      } finally {
        _vm.loading(false);
      }

    },

    async loadCodes() {
      const _vm = this;

    },

    async loadRaw() {
      const _vm = this;

      const rawListRes = await getRaw({ rawSeq: _vm.$route.query.rawSeq, rawDetailSeq: _vm.$route.query.rawDetailSeq });
      const { resultCode, resultFailMessage, resultData } = rawListRes;
      _vm.localData.rawInfo = { ...rawListRes.resultData };
      _vm.localData.rawInfo.managerStr = _vm.localData.rawInfo.managerlist
        .map(manager => manager.membName) // membName 필드만 추출
        .join(",");
      _vm.localData.rawInfo.elmFields = [...DEFAULT_ELM_TABLE_COLUMNS];

      _vm.localData.rawInfo.type001Str = _vm.localData.rawInfo.typelist
        .filter(type => type.grpCode === "001")
        .map(type => type.codeNmKo) // membName 필드만 추출
        .join(",");
      _vm.localData.rawInfo.type002Str = _vm.localData.rawInfo.typelist
        .filter(type => type.grpCode === "002")
        .map(type => type.codeNmKo) // membName 필드만 추출
        .join(",");
      _vm.localData.rawInfo.type003Str = _vm.localData.rawInfo.typelist
        .filter(type => type.grpCode === "003")
        .map(type => type.codeNmKo) // membName 필드만 추출
        .join(",");
      _vm.localData.rawInfo.type004Str = _vm.localData.rawInfo.typelist
        .filter(type => type.grpCode === "004")
        .map(type => type.codeNmKo) // membName 필드만 추출
        .join(",");
      _vm.localData.rawInfo.type005Str = _vm.localData.rawInfo.typelist
        .filter(type => type.grpCode === "005")
        .map(type => type.codeNmKo) // membName 필드만 추출
        .join(",");

      
      const ingredientList = (_vm.localData.rawInfo.elmlist || []).map(item=>{
        return {
          id : item.rawElmId,
          rep_name: item.elmName,
          rep_name_en: item.elmNameEn,
          repName : item.elmName,
          repNameEn : item.elmNameEn,
          inci : item.elmNameEn,
        }
      });

      await _vm.setIngredientList(ingredientList, true);

      // v-html로 삽입된 이미지에 스타일 적용
      _vm.$nextTick(() => {
        _vm.applyImageStyles();
      });

    },

    applyImageStyles() {
      const _vm = this;
      // raw-company 클래스를 가진 div 내부의 모든 이미지에 스타일 적용
      const rawCompanyDivs = _vm.$el.querySelectorAll('.raw-company');
      rawCompanyDivs.forEach(div => {
        const images = div.querySelectorAll('img');
        images.forEach(img => {
          img.style.maxWidth = '200px';
          img.style.height = 'auto';
          img.style.display = 'block';
        });
      });
    },

    async setIngredientList(pIngredientList, isLadEsSearch) {
      const _vm = this;
      let ingredientList = pIngredientList;

      //console.log('setIngredientList:', ingredientList, isLadEsSearch);

      if(isLadEsSearch){
        ingredientList = await _vm.findIngredientList(ingredientList);
      }      
      const ingredientListData = _vm.listData, pInfo = _vm.listData.pi;

      ingredientListData.rowData = [];
      ingredientListData.rawList = ingredientList.map((item, index) => {
          return {
            rowIndex : ingredientListData.nextRowIndex++, //rowIndex 설정
            index: _vm.$options.filters.eufRowNumberForPageInfo(index, pInfo),
            _isEditRow : false,
            ...item,
          };
        });
      ingredientListData.rowData = JSON.parse(JSON.stringify(ingredientListData.rawList));
      _vm.ernsAgGrid_resetOriginalData(ingredientListData.gridId, ingredientListData.rowData);

    },
  }
}
</script>
  
<style lang="scss" scoped>
.erns-ag-grid-vue{
  min-height: 400px; 
  max-height: 600px;
}

.fileInput{
  background-color: #edecfc;
  border: 0;
  border: 1px dotted #7367f0;
  height: inherit;
  line-height: 200px;
  text-align: center;
  font-size: 50px;
  color: #7367f0;
  padding: 0;
}

.fileInput:hover{
  background-color: #e2e0ff;
  cursor: pointer;
}

.imgs:hover {
  cursor: pointer;
}
</style>

<style lang="scss">
.raw-company img,
.raw-company .raw-comp-logo,
div.raw-company img,
div.raw-company .raw-comp-logo,
.raw-comp-logo {
  max-width: 200px !important;
  height: auto !important;
  display: block;
}

.raw-comp-name,
.raw-comp-tel,
.raw-comp-email {
  display: inline-block;   /* div 를 한줄에 정렬 */
}

/* 2개 이상일 때만 앞에 | 붙이기 */
.raw-comp-name + .raw-comp-tel::before,
.raw-comp-name + .raw-comp-email::before,
.raw-comp-tel + .raw-comp-email::before {
  content: "|";
  margin: 0 6px;
  color: #ccc;
}

.raw-comp-about{
  color: var(--color--gray-666);
}
</style>