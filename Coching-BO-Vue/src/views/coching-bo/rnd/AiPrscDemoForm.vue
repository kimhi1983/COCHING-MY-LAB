<template>
  <div id="ai-prsc-demo-form">
    
    <b-card no-body>
      <validation-observer ref="prscInfoForm">
        <!-- 입력 영역 -->
        <b-card-group class="mt-1">
          <b-card title="AI 처방 입력" class="col-md-12">
            <b-row>

              <b-col md="12" ref="prscName" class="prscName_under">
                <b-form-group label-for="prscName">
                  <template v-slot:label>
                      <span>처방명</span>
                      <span class="text-pink">*</span>
                  </template>
                  <validation-provider #default="{ errors }"
                    name="'처방명'"
                    rules="required"
                  >
                    <b-form-input
                      id="prscName"
                      v-model="localData.prscInfo.prscName"
                      :state="errors.length > 0 ? false:null"                     
                      autoptnlete="off"
                      aria-readonly="true"
                      placeholder="처방명을 입력하세요"
                      :readonly="!isNew"
                    />                    
                  </validation-provider>               
                </b-form-group>
              </b-col>

              <b-col md="4">
                <b-form-group label-for="code">
                  <template v-slot:label>
                      <span>제품형태</span>
                      <span class="text-pink">*</span>
                  </template> 
                  
                  <validation-provider #default="{ errors }"
                    name="'제품형태'"
                    rules="required"
                  >
                    <v-select 
                      v-model="localData.prscInfo.prodCateGroup"
                      :options="CODES.grpList"
                      label="name" 
                      track-by="etc3" 
                      :reduce="option => option.etc3"
                      :disabled="!isNew"
                      placeholder="제품 카테고리를 선택하세요"
                      class="d-inline-block w-100 mb-1"
                      @input="onChangeProdCateGroup"
                    />

                    <v-select 
                      v-model="localData.prscInfo.prodCateCode"
                      :options="CODES[`CATE${localData.prscInfo.prodCateGroup}`]"
                      label="name" 
                      track-by="etc3" 
                      :reduce="option => option.etc3"
                      :disabled="!isNew"
                      placeholder="상세 카테고리를 선택하세요"
                      class="d-inline-block w-100"
                      @input="onChangeProdCateCode"
                    />
                    <small class="text-danger">{{ errors[0] }}</small>
                  </validation-provider> 
                </b-form-group>
              </b-col>

              <b-col cols="8">
                <b-form-group
                  label="처방 요구사항"
                  label-for="reqContent"
                >
                  <validation-provider
                    #default="{ errors }"
                    name="'처방 요구사항'"                    
                  >
                    <b-form-textarea
                      id="reqContent"
                      rows="3"
                      v-model="localData.prscInfo.reqContent"
                      :readonly="!isNew"
                      placeholder="처방 요구사항을 입력하세요.
ex) 미백효과가 있는 제품으로 만들어주세요."
                    />
                    <small class="text-danger">{{ errors[0] }}</small>
                  </validation-provider>
                </b-form-group> 
              </b-col> 
              
              <b-col cols="6">
                <b-form-group label-for="ingredients"
                >
                  <template v-slot:label>
                    <span>전성분</span>
                    <span class="text-pink">*</span>
                  </template>
                  <div class="d-flex justify-content-between align-items-center mb-1">
                    <div class="text-left float-left">
                      <small class="text-secondary">Total:{{ listData.rowData.length }}</small>
                    </div>
                    <div class="text-right float-right">
                      <b-button
                        @click.prevent="onClickDeleteIngredient"
                        size="sm"
                        class="btn-icon mr-1"
                        variant="outline-primary"
                        v-b-tooltip.hover
                        title="전체 삭제"
                      > 
                        <feather-icon icon="TrashIcon" size="12" />
                      </b-button> 
                      <b-button
                        @click.prevent="onClickAddIngredient"
                        size="sm"
                        class="btn-icon"
                        variant="outline-success"
                        v-b-tooltip.hover
                        title="성분 추가"
                      > 
                        <feather-icon icon="PlusIcon" size="12" />
                      </b-button>                    
                    </div>
                  </div>
                  
                  <validation-provider
                    #default="{ errors }"
                    name="'전성분'"
                    class="erns-ag-grid-vue ingredient-grid"                    
                  >
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
                      
                      pagination=false
                      suppressRowTransform=true
                    >
                    </ag-grid-vue>
                    <small class="text-danger">{{ errors[0] }}</small>
                  </validation-provider>
                </b-form-group> 
              </b-col>

              <b-col cols="6">
                <b-form-group label-for="search-prod-nameL"
                >
                  <template v-slot:label>
                    <span>상품목록</span>                      
                  </template>
                  <b-input-group class="mb-1">
                    <b-form-input
                      id="search-prod-nameL"
                      size="sm"
                      v-model="listData2.sc.nameL"
                      @keyup.enter="onSearchProd"
                      placeholder="상품명을 검색하세요."
                    ></b-form-input>                
                    <b-input-group-append>
                      <b-button 
                        variant="outline-primary" 
                        size="sm"
                        @click="onSearchProd"
                        v-b-tooltip.hover
                        title="상품 검색"
                        ><feather-icon icon="SearchIcon" size="15" />
                      </b-button>
                    </b-input-group-append>
                  </b-input-group>
                  <div
                    class="erns-ag-grid-vue prod-grid"                    
                  >
                    <ag-grid-vue       
                      class="ag-theme-quartz"
                      :ref="listData2.gridId"
                      :gridOptions="listData2.gridOptions"
                      :columnDefs="listData2.columnDefs"
                      :rowData="listData2.rowData"
                      @gridReady="(params) => ernsAgGrid_registerGrid(listData2.gridId, params, listData2.gridOptions)"
                      
                      :frameworkComponents="listData2.frameworkComponents"
                      
                      rowDragManaged=false

                      pagination=false
                      suppressRowTransform=true
                    >
                    </ag-grid-vue>
                  </div>
                </b-form-group> 
              </b-col>

              <b-col cols="6">
                <b-form-group label-for="ai-option"
                >
                  <template v-slot:label>
                    <span>AI 모델</span>
                    <span class="text-pink">*</span>                      
                  </template>
                  <v-select 
                      v-model="localData.prscInfo.aiModelCode"
                      :options="formattedAiModelList"
                      label="label" 
                      track-by="code" 
                      :reduce="option => option.code"                      
                      placeholder="모델을 선택하세요"
                      class="d-inline-block w-100 mb-1"
                    />                
                </b-form-group> 
              </b-col>

              <b-col cols="6">
                <b-form-group label-for="ai-temperature">
                  <template v-slot:label>
                    <span class="text-pink">*</span>
                    <span>창의성 (일부 모델은 지원안함) </span>
                    <span class="text-pink">(오류발생시 1로 설정 권장)</span>
                  </template>
                  <div class="d-flex align-items-center mt-1">
                    <small class="text-secondary mr-2 text-nowrap">{{ getModelInfo(localData.prscInfo.aiModelCode).tempMin || 0 }}</small>
                    <b-form-input
                      id="ai-temperature"
                      v-model.number="localData.prscInfo.temperature"
                      type="range"
                      :min="getModelInfo(localData.prscInfo.aiModelCode).tempMin || 0"
                      :max="getModelInfo(localData.prscInfo.aiModelCode).tempMax || 1"
                      step="0.1"
                      class="mr-2 flex-grow-1"
                    />
                    <small class="text-secondary mr-2 text-nowrap">{{ getModelInfo(localData.prscInfo.aiModelCode).tempMax || 1 }}</small>
                    <span class="text-nowrap font-weight-bold">{{ localData.prscInfo.temperature }}</span>
                  </div>
                </b-form-group>
              </b-col>

              <b-col cols="12">
                <b-form-group label-for="ai-script">
                  <template v-slot:label>
                    <span>스크립트</span>
                  </template>
                  <v-select 
                      v-model="localData.prscInfo.promptType"
                      :options="scriptList"
                      label="name" 
                      track-by="code" 
                      :reduce="option => option.code"                      
                      placeholder="스크립트를 선택하세요"
                      class="d-inline-block w-100"
                    />                
                </b-form-group> 
                <small class="text-secondary cursor-pointer" 
                  @click="showScriptDetail = !showScriptDetail">
                  스크립트 자세히 보기
                  <feather-icon 
                    :icon="showScriptDetail ? 'ChevronUpIcon' : 'ChevronDownIcon'" 
                    size="12"                                      
                  />
                </small>
                <div class="script-detail-content" v-if="showScriptDetail">
                  <pre>{{ getScriptDescription(localData.prscInfo.promptType) }}</pre>
                </div>
              </b-col>
              
              <!-- <b-col cols="12">
                <b-form-group label-for="ingredients"
                >
                  <template v-slot:label>
                      <span>전성분 raw</span>
                  </template>
                  listData.debugRowData:{{ listData.debugRowData }}<br/>
                </b-form-group> 
              </b-col> -->
            </b-row>


          </b-card>
        </b-card-group>
        <!-- // 입력 영역 -->

        <!-- 버튼 영역 -->
        <b-card-body>
          <div class="text-center">

            <b-button
              v-ripple.400="'rgba(113, 102, 240, 0.15)'"
              variant="outline-primary"
              class="mr-1"
              @click.prevent="onClickSave"
            > 저장
            </b-button>
            
            <b-button
              v-ripple.400="'rgba(113, 102, 240, 0.15)'"
              variant="primary"
              class="mr-1"
              @click.prevent="onClickDoAiPrsc"
            > AI 처방 생성
            </b-button>

            <b-button
              v-ripple.400="'rgba(113, 102, 240, 0.15)'"
              variant="outline-primary"
              @click="onClickCancel"
            > 취소
            </b-button>
          </div>
        </b-card-body>
        <!-- // 버튼 영역 -->

      </validation-observer>
    </b-card>
    
    <!-- localData.prscInfo:{{ localData.prscInfo }}<br/> -->
    <!-- prodCateList:{{ CODES.prodCateList }} -->
    <!-- localData.prscInfo.prodCateGroup:{{ localData.prscInfo.prodCateGroup }}<br/> -->
    <!-- grpList:{{ CODES.grpList }}<br/> -->
    <!-- CATE{{ localData.prscInfo.prodCateGroup }}:{{ CODES[`CATE${localData.prscInfo.prodCateGroup}`] }} -->
    <!-- ptnInfo:{{localData.ptnInfo}}<br/> -->
    <!-- membInfo:{{localData.memberInfo}}<br/>  -->
    
    <!-- AI 가이드 처방 목록 -->
    <b-card title="AI 가이드 처방" 
      class="ai-prsc-list-section mt-3" 
      v-if="localData.aiPrscList && localData.aiPrscList.length > 0">      
      <div class="ai-prsc-card-container">
        <AiPrscCard
          v-for="(aiPrsc, index) in localData.aiPrscList" 
          :key="aiPrsc.aiLabMstSeq"
          :ai-prsc="aiPrsc"
          :prsc-number="getPrscNumber(index)"
          @view-detail="onClickViewDetail"
        />
      </div>
    </b-card>

    <!-- AI 처방 결과 JSON 뷰어 -->
    <b-card v-if="showJsonResult && localData.aiResult" title="AI 처방 결과" class="mt-3">
      <b-card-text>
        <!-- <div class="d-flex justify-content-between align-items-center mb-2">
          <h6 class="mb-0">JSON 응답 결과</h6>
          <div>
            <b-button 
              variant="outline-secondary" 
              size="sm" 
              @click="copyJsonToClipboard"
              class="mr-2"
            >
              <feather-icon icon="CopyIcon" size="14" class="mr-1" />
              복사
            </b-button>
            <b-button 
              variant="outline-secondary" 
              size="sm" 
              @click="showJsonResult = false"
            >
              <feather-icon icon="XIcon" size="14" class="mr-1" />
              닫기
            </b-button>
          </div>
        </div> -->
        
        <!-- JSON 뷰어 앵커 -->
        <div ref="jsonViewerAnchor"></div>

        <!-- JSON 뷰어 옵션 -->
        <div class="d-flex align-items-center mb-2">
          <b-form-radio-group 
            v-model="jsonViewerOptions.viewMode" 
            :options="[
              { text: '테이블 뷰', value: 'table' },
              { text: 'JSON 뷰', value: 'json' }
            ]"
            class="mr-3"
            buttons
            button-variant="outline-primary"
            size="sm"
          />
          <!-- <b-form-checkbox v-model="jsonViewerOptions.collapsed" class="mr-3" v-if="jsonViewerOptions.viewMode === 'json'">
            접기/펼치기
          </b-form-checkbox>
          <b-form-checkbox v-model="jsonViewerOptions.withQuotes" class="mr-3" v-if="jsonViewerOptions.viewMode === 'json'">
            따옴표 표시
          </b-form-checkbox> -->
        </div>
        
        <!-- JSON 뷰어 -->
        <JsonViewer v-if="jsonViewerOptions.viewMode === 'json'"
          :jsonString="JSON.stringify(localData.aiResult)"
          :collapsed="jsonViewerOptions.collapsed"
          :withQuotes="jsonViewerOptions.withQuotes"
        />

        <!-- 테이블 뷰어 -->
        <AiPrscResultTable v-if="jsonViewerOptions.viewMode === 'table'" 
          :aiResult="localData.aiResult" />
      </b-card-text>
    </b-card>

    <SearchIngedientModal 
      :addPlusIcon=true
      v-model="searchIngedientModal.show"
      @onAddIngredient="onAddIngredient"
      :size="'xl'"
    />

  </div>
</template>

<script>
import ernsUtils from "@/components/mixins/ernsUtils";
import cochingUtils from "@/components/mixins/cochingUtils";
import rndMixin from "@/components/rnd/rndMixin";
import Ripple from "vue-ripple-directive";
import draggable from 'vuedraggable';
import "ag-grid-community/styles/ag-grid.css"; // Mandatory CSS required by the Data Grid
import "ag-grid-community/styles/ag-theme-quartz.css";

import _ from 'lodash';

import vSelect from 'vue-select';
import { integer } from 'vee-validate/dist/rules';

import { PROD_CATES } from '@/constants/testCode';

import { AgGridVue } from "ag-grid-vue";
import ernsAgGrid from '@/components/ernsAgGrid/ernsAgGrid';
import { AG_GRID_COMMON_COLUMN_DEFS } from '@/constants/agGrid';

import { DEF_SEARCH_OPT, DEF_ROW } from "@/constants/cochingIngredient";

import { 
  getEsIngredientList,
  getSearchProdList,
} from "@/api/coching-bo/search/esSearch";
import {
  getAiPrscResult,
  getLabMasterDetail,
  addLabMaster,
  setLabMaster,
  getLabAiResList,
} from "@/api/coching-bo/rnd/rnd";

import { AI_MODEL_LIST, SCRIPT_LIST } from "@/constants/rnd";
import ButtonCellRenderer from "@/components/ernsAgGrid/ButtonCellRenderer.vue";
import EwgScoreCellRenderer from "@/components/ernsAgGrid/EwgScoreCellRenderer.vue";
import ImageCellRenderer from "@/components/ernsAgGrid/ImageCellRenderer.vue";
import IconButtonCellRenderer from "@/components/ernsAgGrid/IconButtonCellRenderer.vue";
import EsHighlightTextCellRenderer from "@/components/ernsAgGrid/EsHighlightTextCellRenderer.vue";
import EsHighlightArrayTextCellRenderer from "@/components/ernsAgGrid/EsHighlightArrayTextCellRenderer.vue";  
import JsonViewer from "@/components/common/JsonViewer.vue";
import AiPrscResultTable from "@/views/coching-bo/rnd/AiPrscResultTable.vue";
import SearchIngedientModal from "@/components/modal/SearchIngedientModal.vue";
import AiPrscCard from "@/views/coching-bo/rnd/AiPrscCard.vue";

const GRID_ID_MAIN = 'main-grid';
const GRID_ID_PROD = 'prod-grid';
const DEF_INGREDIENT_LIST = [];
const DEF_PRSC_INF = {
  labMstSeq: 0,
  prscName: 'AI 테스트 처방',
  prodCateGroup : '001:001',  
  prodCateCode : '001:001:003',
  reqContent : '집중 미백 및 피부톤 개선',
  ingredientList : JSON.stringify([...DEF_INGREDIENT_LIST]),
  aiModelCode : 'gpt-4.1-mini',
  temperature : 1.0,
  promptType : 'default',
};

export default {
  mixins: [ernsUtils, cochingUtils, ernsAgGrid, rndMixin],
  components: {
    vSelect,
    draggable, 
    AgGridVue,    
    ButtonCellRenderer,
    EwgScoreCellRenderer,
    ImageCellRenderer,
    IconButtonCellRenderer,
    EsHighlightTextCellRenderer,
    EsHighlightArrayTextCellRenderer,
    JsonViewer,
    AiPrscResultTable,
    SearchIngedientModal,
    AiPrscCard,
  },
  directives: {
    Ripple,
  },
  props: {
    labMstSeq: {
      type: [Number, String],
      default: 0,
    },
    aiLabMstSeq: {
      type: [Number, String],
      default: 0,
    },
  },
  computed: {    
    isRegMode(){
      const plabMstSeq = parseInt('' + (this.labMstSeq || 0), 10);
      return plabMstSeq == 0;
    },

    // 포맷팅된 AI 모델 목록
    formattedAiModelList() {
      return this.CODES.aiModelList.map(model => ({
        ...model,
        label: this.formatAiModelLabel(model)
      }));
    },    
  },
  watch: {
    // 라우트가 변경되면 메소드를 다시 호출됩니다.
    '$route': 'fetchData',
  },
  filters: {},
  async mounted() {
    const _vm = this;

    //_vm.getInitParam();
    await _vm.loadCodes();
    await _vm.fetchData();  
    
    _vm.onSearchProd();
  },
  beforeDestroy() {
    const _vm = this;
  },
  // //페이지 이동전 처리
  // beforeRouteLeave : function(to, from, next){
  //   const _vm = this;
  //   try{
  //     const data = {
  //       initParam : {
  //         pi : _vm.listData.pi
  //       }
  //     };
  //     _vm.eumSetRouteHistoryParam(data);
  //   }catch(err){
  //     console.error(err);
  //   }

  //   return next();
  // },  
  data() {
    const _vm = this;
    
    // Ag-Grid Column Definition
    const AG_GRID_COLUMN_DEFS = [
      { headerName: "순서", field: "drag", rowDrag: true, width: 60 }, // 드래그 컬럼 추가
      { hide: true,
        headerName: "Row ID", field: "rowId"          , width: 70 
        , valueGetter: (params) => params.node.id // 내부 rowId 출력
      },
      { hide: true,
        headerName: "번호"        , field: "index"        , width: 50  , cellClass: 'flex-center selectable-text'} ,
      { headerName: "삭제"         , field: "addPlusIcon"  , width: 60  , cellClass: 'flex-center',
        cellRenderer: "IconButtonCellRenderer", // 렌더러 지정
        cellRendererParams: {
          tooltip: "성분 삭제",
          icon: "MinusCircleIcon",
          iconSize: "24",
          variant : " ",
          btnClass: "btn-red-outline btn-no-border", 
          action: (params) => {
            this.onDeleteIngredient(params);
          },
        },
      },
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

    // Ag-Grid Column Definition
    const AG_GRID_COLUMN_DEFS_PROD = [
      { hide: true,
        headerName: "Row ID", field: "rowId"          , width: 70 
        , valueGetter: (params) => params.node.id // 내부 rowId 출력
      },
      { headerName: "No"        , field: "index"        , width: 60  , cellClass: 'flex-center selectable-text'},
      {
        headerName: "추가"         , field: "applyProdIngredient"  , width: 60  , cellClass: 'flex-center',
        cellRenderer: "IconButtonCellRenderer", // 렌더러 지정
        cellRendererParams: {
          icon: "TriangleIcon",
          tooltip: "전성분 적용",
          iconSize: "20",
          variant : " ",
          btnClass: 'rotate-left btn-green-outline btn-no-border',
          action: (params) => {
            this.onApplyProdIngredient(params);
          },
        },
      },
      { headerName: "대표이미지"   , field: "product.image_url"   , width: 164  //, cellClass: 'text-center'
          ,
          cellRenderer: "ImageCellRenderer", // 렌더러 지정
          cellRendererParams: {
            width: 160,
            height: 160
          },
          cellClass: (params) => {
            const retClass = ['d-flex'
              , 'align-items-center'
              , 'justify-content-center'
            ];          
            return retClass;
          }
      },
      { headerName: "상품정보"    , field: "goodsInfo" , width: 180  , cellClass: 'flex-left selectable-text',
        valueGetter: (params) => {
          const productId = params.node.data.product.id;
          const brandFullName = params.node.data.product.brand.full_name;
          const productName = params.node.data.product.name;
          const buyInfo = params.node.data.product.buy_info;
          const goods = params.node.data.goods;
          const selling = goods?.sale_status == "SELNG" ? "[판매중] " : "";
          const goodsName = goods?.name || '';

          const retStr = `[ ${brandFullName} ]\n${productName}\n${buyInfo}\n${selling}${goodsName}\nID: ${productId}`;

          return retStr;
        },
        cellRenderer: "EsHighlightTextCellRenderer", // 렌더러 지정
        cellRendererParams: {
          highlightPrefix: ["product.brand.name", "product.brand.full_name", "product.name", "product.buy_info", "goods.name"],          
        },        
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
        cellStyle: { "white-space": "pre-wrap" } // 여러 줄로 표시
      },  
      { hide: true,
        headerName: "아이디"      , field: "product.id"     , width: 100  , cellClass: 'flex-center selectable-text'},
      { hide: true,
        headerName: "상품명"      , field: "product.name" , width: 200  , cellClass: 'flex-left selectable-text',
        cellRenderer: "EsHighlightTextCellRenderer", // 렌더러 지정
        cellRendererParams: {

        },
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
        cellStyle: { "white-space": "normal" } // 여러 줄로 표시
      },
      { hide: true,
        headerName: "브렌드"      , field: "product.brand.full_name"  , width: 150  , cellClass: 'flex-center selectable-text',
        cellRenderer: "EsHighlightTextCellRenderer", // 렌더러 지정
        cellRendererParams: {
          highlightPrefix: ["product.brand.name", "product.brand.full_name"],          
        },
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
        cellStyle: { "white-space": "normal" } // 여러 줄로 표시
      },
      { hide: true,
        headerName: "상품정보"    , field: "product.buy_info" , width: 150  , cellClass: 'flex-center selectable-text',
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
        cellStyle: { "white-space": "normal" } // 여러 줄로 표시
      },      
      { hide: true,
        headerName: "카테고리"    , field: "cas_no"       , width: 150  , cellClass: 'flex-center selectable-text'
        , valueFormatter: (params) => {
          if(!params.node.data.product?.categories) return "-";
          return this.displayArrayVal(params.node.data.product?.categories.map(item=>item.name));
        }
      },
      { headerName: "전성분"       , field: "ingdInfo"        , width: 500  , cellClass: 'flex-left selectable-text'
        , cellRenderer: "EsHighlightArrayTextCellRenderer" // 배열용 렌더러 지정
        , cellRendererParams: {
          highlightPrefix: "ingredients.korean",          
        }
        , valueGetter: (params) => {
          if(!params.node.data.ingredients) return [];
          return params.node.data.ingredients.map(item=>item.korean);
        },
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: false, // 행 높이를 자동 조정
        cellStyle: { "white-space": "normal" } // 여러 줄로 표시
      },

      { hide: true,
        headerName: "상품상세"    , field: "gootsInfo"       , width: 150  , cellClass: 'flex-left selectable-text'
        , valueFormatter: (params) => {
          const goods = params.node.data.goods;
          if(!goods) return "-";

          const selling = goods?.sale_status == "SELNG" ? "[판매중] " : "";
          const goodsName = goods?.name || '';
          return `${selling}${goodsName}`;
        },
        wrapText: true, // 자동 줄바꿈 활성화
        autoHeight: true, // 행 높이를 자동 조정
        cellStyle: { "white-space": "normal" } // 여러 줄로 표시
      },     
      { hide : true,
        headerName: "수정여부"  , field: "_isEditRow" , width: 100  , cellClass: 'text-center'},     
    ];

    return {
      isNew: true,

      CODES : {
        prodCateList: [...PROD_CATES],
        aiModelList: [...AI_MODEL_LIST],

        grpList: [],
        list: [],
        CATE : [],
      },

      showScriptDetail : false,
      scriptList: [SCRIPT_LIST[0]],      

      localData: {
        prscInfo: {...DEF_PRSC_INF},
        aiResult: null, // AI 처방 결과 저장

        aiPrscList: [], // AI 처방 목록
      },

      // JSON 뷰어 관련
      showJsonResult: false,
      jsonViewerOptions: {
        collapsed: false,
        withQuotes: false,
        withComments: false,
        viewMode: 'table' // 'json' 또는 'table' - 테이블 뷰가 기본
      },

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
          overlayNoRowsTemplate: '<div class="text-center">전성분 데이터가 없습니다.<br>추가 버튼을 클릭하여 성분을 추가해주세요.</div>',          
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

      //상품목록
      listData2: {
        gridId : GRID_ID_PROD,
        nextRowIndex : 1,
        isDisabledAllSave : true,
        gridOptions : {
          rowHeight: 170,
          isEditData : false,
          immutableData : true,
          defaultColDef : AG_GRID_COMMON_COLUMN_DEFS,
          getRowId: (params) => params.data.rowIndex, // 각 행의 ID를 'rowIndex' 로 지정
          localeText: {
            noRowsToShow: '상품 데이터가 없습니다.'
          },
        },
        columnDefs: AG_GRID_COLUMN_DEFS_PROD,
        frameworkComponents :{
          ButtonCellRenderer,
          EwgScoreCellRenderer,
          EsHighlightTextCellRenderer,
          IconButtonCellRenderer,
        },

        sc: { ...DEF_SEARCH_OPT.sc, exactMatchOnly: true },
        pi: { ...DEF_SEARCH_OPT.pi },
        rowData: [],
        rawList: [],
      },

      searchIngedientModal: {
        show : false,
      },
    };
  },
  methods: {
    // 카테고리(그룹) 변경
    onChangeProdCateGroup(val){
      const _vm = this;
      // 하위 카테고리 초기화
      _vm.localData.prscInfo.prodCateCode = _vm.CODES[`CATE${_vm.localData.prscInfo.prodCateGroup}`][0].etc3;
      // 상세 변경 시 상품 재검색
      _vm.onSearchProd();
    },

    // 카테고리(상세) 변경
    onChangeProdCateCode(val){
      const _vm = this;
      // 상세 변경 시 상품 재검색
      _vm.onSearchProd();
    },
    //$route.query, $route.params, HistoryParam 등으로 초기값 설정
    getInitParam() {
      const _vm = this;

      const query = _vm.$route.query, params = _vm.$route.params;

      _vm.params = {
        ...query
      };    
    },

    //취소
    onClickCancel(){
      const _vm = this;
      _vm.$router.back();
    },

    //성분 추가
    onClickAddIngredient(){
      const _vm = this;
      _vm.searchIngedientModal.show = true;
    },

    //저장
    async onClickSave(){
      const _vm = this;

      const params = {
        labMstSeq : _vm.labMstSeq,
        title : _vm.localData.prscInfo.prscName,        
        content : _vm.localData.prscInfo.reqContent,        
        prodCateGroup : _vm.localData.prscInfo.prodCateGroup,
        prodCateCode : _vm.localData.prscInfo.prodCateCode,
      };
      {
        const ingredientList = [];
        const ingredientListData = _vm.listData;
        const gridRef = _vm.$refs[ingredientListData.gridId];
        const allRows = gridRef.api.getModel().rowsToDisplay; // 전체 row 배열 가져오기
        for (const row of allRows) {
          ingredientList.push({
            id : row.data.id || 0,
            repName : row.data.repName || '',
            repNameEn : row.data.repNameEn || '',
          });        
        }
        params.ingredientList = ingredientList;
      }

      

      const result = await _vm.$swal({
        title: '확인',
        text: `처방정보를 ${_vm.isRegMode ? '등록': '수정'} 하시겠습니까?`,
        showCancelButton: true,
        confirmButtonText: `${_vm.isRegMode ? '등록': '수정'}`,
        customClass: {
          confirmButton: 'btn btn-danger ml-1',
          cancelButton: 'btn btn-outline-primary',
        },
        buttonsStyling: false,
      });

      if(!result.value){
        return;
      }

      _vm.loading(true);
      try {
        const res = await (_vm.isRegMode ? addLabMaster(params) : setLabMaster(params));
        
        _vm.loading(false);

        await _vm.alertSuccess(`처방정보가 ${_vm.isRegMode ? '등록': '수정'} 되었습니다.`);

        const resultData = res.resultData;
        const labMstSeq = resultData.labMstSeq;
        _vm.$router.replace({ name: 'coching-bo-rnd-ai-prsc-demo1-detail', query: { labMstSeq: labMstSeq}});
        
      } catch (err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
    },

    //AI 처방 생성
    async onClickDoAiPrsc(){
      const _vm = this;
      _vm.loading(true);
      try {
        const prodCateCodeItem = _vm.CODES.list.find(item => item.etc3 == _vm.localData.prscInfo.prodCateCode);
        //console.log('prodCateCodeItem:', prodCateCodeItem);

        const ingredientList = [];
        {
          const ingredientListData = _vm.listData;
          const gridRef = _vm.$refs[ingredientListData.gridId];
          const allRows = gridRef.api.getModel().rowsToDisplay; // 전체 row 배열 가져오기
          for (const row of allRows) {
            ingredientList.push({
              id : row.data.id || 0,
              repName : row.data.repName || '',
              repNameEn : row.data.repNameEn || '',
            });        
          }
        }

        if(ingredientList.length == 0){
          _vm.alertError('성분을 추가해주세요.');
          _vm.loading(false);
          return;
        }

        //console.log('ingredientList:', ingredientList);

        const params = {
          labMstSeq : _vm.labMstSeq,
          title : _vm.localData.prscInfo.prscName,
          ingredients : ingredientList,
          direction : _vm.localData.prscInfo.reqContent,
          formulation : prodCateCodeItem.etcComment,
          prodCateGroup : _vm.localData.prscInfo.prodCateGroup,
          prodCateCode : _vm.localData.prscInfo.prodCateCode,

          aiOptModel : _vm.localData.prscInfo.aiModelCode || 'gpt-5-nano',
          aiOptTemperature : _vm.localData.prscInfo.temperature || 1.0,
          aiOptScript : _vm.localData.prscInfo.promptType || 'default',
        }
        const res = await getAiPrscResult(params);
        
        // JSON을 예쁘게 포맷팅하여 콘솔에 출력
        console.log('AI 처방 결과:', JSON.stringify(res, null, 2));

        if(_vm.isRegMode){

          // 등록 모드인 경우 페이리 리로딩 필요
          const labMstSeq = res.labMstSeq;
          const aiLabMstSeq = res.aiLabMstSeq;
          _vm.$router.replace({ name: 'coching-bo-rnd-ai-prsc-demo1-detail', query: { labMstSeq: labMstSeq, aiLabMstSeq: aiLabMstSeq}});

          return;
        }

        // AI 가이드 처방 목록 갱신
        await _vm.loadAiPrscList();
        
        // 결과를 화면에 표시하기 위해 데이터 저장
        _vm.localData.aiResult = res;
        
        // 결과 표시 및 JSON 뷰로 전환
        _vm.showJsonResult = true;
        
        // 100ms 후 JSON 뷰어 위치로 스크롤 이동
        _vm.$nextTick(() => {
          setTimeout(() => {
            const anchor = _vm.$refs.jsonViewerAnchor;
            if (anchor && anchor.scrollIntoView) {
              anchor.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }
          }, 100);
        });
        
      } catch (err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
    },

    // JSON을 클립보드에 복사
    async copyJsonToClipboard() {
      try {
        const jsonString = JSON.stringify(this.localData.aiResult, null, 2);
        await navigator.clipboard.writeText(jsonString);
        this.$toast.success('JSON이 클립보드에 복사되었습니다.');
      } catch (err) {
        console.error('클립보드 복사 실패:', err);
        this.$toast.error('클립보드 복사에 실패했습니다.');
      }
    },

    //agGrid셀변경시 처리
    onCellValueChanged(params) {
      const _vm = this;

      const listData = _vm.listData;

      //변경사항 저장
      _vm.ernsAgGrid_trackCellChanges(listData.gridId, params);

      //Cell 랜더러에게 변경 noti
      const checkFields = ["_isEditRow", "name"];
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
      if("" == (rowData.name || '').trim()) {
        return false; //키워드 필수
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
    onSearch() {
      const _vm = this;      
    },

    //검색어 초기화
    onClearSearch() {
      const _vm = this;
      
      // Vue의 반응성을 보장하기 위해 Vue.set 사용
      _vm.$set(_vm.listData.sc, 'searchText', '');
      
      // 강제로 업데이트 트리거
      _vm.$forceUpdate();
      
      _vm.$nextTick(() => {
        // input에 포커스 주기
        const searchInput = document.getElementById('search-coching-ingredient-keyword-searchText');
        if (searchInput) {
          searchInput.focus();
        }
      });
    },

    //성분 추가
    onAddIngredient(ingredientData) {
      const _vm = this;
      try{
        const listData = _vm.listData;
        const gridRef = _vm.$refs[listData.gridId];
        if(!gridRef?.api) return;

        const addId = ingredientData.id;
        if(addId && addId > 0){
          const existingRow = listData.rowData.find(item => item.id === addId);
          if(existingRow){
            _vm.$toast && _vm.$toast.error('이미 추가된 성분입니다.');
            return;
          }
        }

        // 삽입 위치: 선택(또는 포커스)된 행 바로 아래
        const focused = gridRef.api.getFocusedCell();
        const selectedNodes = gridRef.api.getSelectedNodes?.() || [];
        const rowsToDisplay = gridRef.api.getModel().rowsToDisplay || [];

        console.log('focused:', focused);
        console.log('selectedNodes:', selectedNodes);
        console.log('rowsToDisplay:', rowsToDisplay);
        
        // displayIndex 결정: 포커스 > 선택 > 맨 끝
        let displayIndex;
        if (focused && typeof focused.rowIndex === 'number') {
          displayIndex = focused.rowIndex + 1;
        } else if (selectedNodes.length > 0 && selectedNodes[0]?.rowIndex != null) {
          displayIndex = selectedNodes[0].rowIndex + 1;
        } else {
          // 아무것도 선택되지 않았을 때: 맨 끝에 추가
          displayIndex = rowsToDisplay.length;
        }

        // 행 메타 구성
        const currentLen = rowsToDisplay.length;
        const newRow = {
          ...ingredientData,
          _highlight : null,
          repName: ingredientData.rep_name,
          repNameEn: ingredientData.rep_name_en,
          rowIndex : listData.nextRowIndex++,
          index: _vm.$options.filters.eufRowNumberForPageInfo(currentLen, listData.pi),
          _isEditRow : false,          
          //순서 중요. 특히 rowIndex 가 덮히지 않도록 처리해야함
        };

        // 내부 상태 동기화 (표시 인덱스에 삽입)
        // 안전한 인덱스 범위로 제한 (0 ~ rawList.length)
        const safeDisplayIndex = Math.min(Math.max(displayIndex, 0), listData.rawList.length);
        listData.rawList = [
          ...listData.rawList.slice(0, safeDisplayIndex),
          newRow,
          ...listData.rawList.slice(safeDisplayIndex)
        ];
        listData.rowData = JSON.parse(JSON.stringify(listData.rawList));
        _vm.ernsAgGrid_resetOriginalData(listData.gridId, listData.rowData);

        _vm.syncIngredientList();

        _vm.$toast && _vm.$toast.success(`${ingredientData.rep_name} 성분이 추가되었습니다.`);
      }catch(err){
        console.error(err);
        _vm.$toast && _vm.$toast.error('행 추가 중 오류가 발생했습니다.');
      }
    },

    //성분 삭제
    onDeleteIngredient(gridParams) {
      const _vm = this;
      try{
        const listData = _vm.listData;
        const gridRef = _vm.$refs[listData.gridId];
        if(!gridRef?.api) return;

        // 그리드에서 행 제거
        gridRef.api.applyTransaction({ remove: [gridParams.data] });
        // 행 높이/렌더 즉시 반영
        gridRef.api.refreshCells({ force: true });
        _vm.$nextTick(() => {
          gridRef.api.resetRowHeights();
        });

        // 내부 rowData/rawList도 동기화 (immutableData 사용 시 권장)
        listData.rowData = listData.rowData.filter(item => item !== gridParams.data);
        listData.rawList = listData.rawList.filter(item => item !== gridParams.data);

        _vm.syncIngredientList();
      }catch(err){
        console.error(err);
        _vm.$toast && _vm.$toast.error('행 삭제 중 오류가 발생했습니다.');
      }
    },

    //성분 전체 삭제
    onClickDeleteIngredient() {
      const _vm = this;
      console.log('onClickDeleteIngredient:');
      const listData = _vm.listData;
      const gridRef = _vm.$refs[listData.gridId];
      if(!gridRef?.api) return;

      // 표시 중 모든 행 데이터를 제거
      const rows = gridRef.api.getModel().rowsToDisplay || [];
      const removeDataList = rows.map(r => r.data);
      if(removeDataList.length){
        gridRef.api.applyTransaction({ remove: removeDataList });
      }

      // 내부 상태 초기화 및 JSON 동기화
      listData.rowData = [];
      listData.rawList = [];
      listData.nextRowIndex = 1;
      // 행 높이/렌더 즉시 반영
      gridRef.api.refreshCells({ force: true });
      _vm.$nextTick(() => {
        gridRef.api.resetRowHeights();
      });

      _vm.syncIngredientList();
    },

    syncIngredientList() {
      const _vm = this;
      const listData = _vm.listData;
      const gridRef = _vm.$refs[listData.gridId];
      if(!gridRef?.api) return;

      const allRows = gridRef.api.getModel().rowsToDisplay;
      const ingredientList = allRows.map(row => ({
        id: row.data.id,
        repName: row.data.repName || row.data.rep_name,
        repNameEn: row.data.repNameEn || row.data.rep_name_en,
      }));
      _vm.localData.prscInfo.ingredientList = JSON.stringify(ingredientList);
    },
    
    //상품 검색
    onSearchProd() {
      const _vm = this;
      _vm.loadProdList(1);
    },
    
    //상품 전성분 적용
    async onApplyProdIngredient(gridParams){
      const _vm = this;
      console.log('onApplyProdIngredient:', gridParams);

      const ingredientList = gridParams.data.ingredients;
      if(!ingredientList) return;     

      const moveIngredientList = ingredientList.map(item=>{
        return {
          id: 0,
          h_id: item.id,
          ewg_score_min : item.ewg || 'null',
          ewg_score_max : item.ewg || 'null',
          ewg_score : item.ewg || 'null',
          ewg_data_label : '',
          rep_name: item.korean,
          rep_name_en: item.english,
          repName: item.korean,
          repNameEn: item.english,
          inci: item.english,
        };
      });

      await _vm.setIngredientList(moveIngredientList, true);
    },

    async setIngredientList(pIngredientList, isLadEsSearch) {
      const _vm = this;
      let ingredientList = pIngredientList;

      //console.log('setIngredientList:', ingredientList, isLadEsSearch);

      if(isLadEsSearch){
        ingredientList = await _vm.findIngredientList(ingredientList);
      }
      _vm.localData.prscInfo.ingredientList = JSON.stringify(ingredientList);
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

    //상품목록 로드
    async loadProdList(pageNo) {
      const _vm = this;

      const listData = _vm.listData2, pInfo = _vm.listData2.pi, searchOp = _vm.listData2.sc;

      pInfo.curPage = pageNo || 1;

      _vm.loading(true);
      try {
        listData.nextRowIndex = 1;
        listData.rowData = [];
        
        const params = _vm.getProdListSearchParam();
        //데이터 로드
        const res = await getSearchProdList(params);
        const { total, list, maxScore, version} = res.resultData;

        console.debug(maxScore);

        listData.pi = {
          ...listData.pi,
          curPage: 1,
          totalItem: total,
          perPage: total,
        };

        const dataList = _vm.convertSearchResultByVersion(version, list);
        listData.rawList = dataList.map((item, index) => {
          const retData = {
            rowIndex : listData.nextRowIndex++, //rowIndex 설정
            index: _vm.$options.filters.eufRowNumberForPageInfo(index, pInfo),
            _isEditRow : false,
            ...item
          };

          retData.product.image_url = _vm.getProxyFile(retData.product.image_url);

          return retData;
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
    getProdListSearchParam() {
      const _vm = this;

      const pInfo = _vm.listData2.pi, searchOp = _vm.listData2.sc;

      const params = {
        page: pInfo.curPage,
        rowSize: DEF_SEARCH_OPT.pi.perPage,

        text: searchOp.nameL,
        exactMatchOnly: searchOp.exactMatchOnly,
      };

      //카테고리 추가
      if(_vm.localData.prscInfo.prodCateCode){
        params["categories"] = [_vm.localData.prscInfo.prodCateCode];
      }else if(_vm.localData.prscInfo.prodCateGroup){
        const cateList = _vm.CODES[`CATE${_vm.localData.prscInfo.prodCateGroup}`].map(item=>item.etc3);
        params["categories"] = [...cateList];
      }

      return params;
    },    

    async loadAiPrscList(){
      const _vm = this;
      _vm.loading(true);
      try {
        const res = await getLabAiResList({labMstSeq: _vm.labMstSeq});

        const {resultData} = res;
        _vm.localData.aiPrscList = resultData.list;
      } catch (err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
    },

    // 처방 번호 (①, ②, ③ 등)
    getPrscNumber(index) {
      const numbers = ['①', '②', '③', '④', '⑤', '⑥', '⑦', '⑧', '⑨', '⑩'];
      return numbers[index] || `(${index + 1})`;
    },

    // AI 모델 label 포맷팅
    formatAiModelLabel(aiModel) {
      const _vm = this;
      
      //console.log('formatAiModelLabel:', aiModel);
      if (!aiModel) return '';
      // 예시: "모델명 (코드)" 형식으로 포맷팅
      // 필요에 따라 포맷팅 로직을 변경하세요
      if (aiModel.code && aiModel.name) {
        return `${aiModel.name} (input: $${aiModel.inputCost}, output: $${aiModel.outputCost})`;
      }
      return aiModel.name || aiModel.code || '';
    },

    // 자세히보기 클릭
    onClickViewDetail(aiPrsc) {
      const _vm = this;
      // TODO: 상세보기 모달 또는 페이지로 이동
      console.log('자세히보기:', aiPrsc);
      if(!aiPrsc) return;

      // aiLabRes를 화면에 표시하기 위해 데이터 저장
      _vm.localData.aiResult = JSON.parse(aiPrsc.aiLabRes);
        
      // 결과 표시 및 JSON 뷰로 전환
      _vm.showJsonResult = true;

      // 100ms 후 JSON 뷰어 위치로 스크롤 이동
      _vm.$nextTick(() => {
          setTimeout(() => {
            const anchor = _vm.$refs.jsonViewerAnchor;
            if (anchor && anchor.scrollIntoView) {
              anchor.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }
          }, 100);
        });
    },

    getScriptDescription(promptType) {
      const _vm = this;

      const script = _vm.scriptList.find(item => item.code == promptType);
      if(script){
        return script.description;
      }

      return '스크립트 설명이 없습니다.';
    },
    // AI 모델 정보 조회
    getModelInfo(modelCode) {
      const _vm = this;
      const model = _vm.CODES.aiModelList.find(item => item.code == modelCode);
      return model;
    },
    
    async loadCodes(){
      const _vm = this;

      _vm.CODES.aiModelList = [...AI_MODEL_LIST];

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

    async fetchData(){
      const _vm = this;
      
      _vm.loading(true);   

      try{
        if(_vm.isRegMode){
          return;
        }

        const res = await getLabMasterDetail({labMstSeq: _vm.labMstSeq});
        const {resultData } = res;

        const ingredientList = (resultData.ingredientlist || []).map(item=>{
          return {
            id : item.rawElmId,
            rep_name: item.rawElmKr,
            rep_name_en: item.rawElmEn,
            repName : item.rawElmKr,
            repNameEn : item.rawElmEn,
            inci : item.rawElmEn,
          }
        });

        //console.log('resultData:', resultData);

        _vm.localData.prscInfo = {
          ...DEF_PRSC_INF,
          prscName : resultData.title,
          prodCateGroup : resultData.prodCateGroup,
          prodCateCode : resultData.prodCateCode,
          reqContent : resultData.content,          
        };

        await _vm.setIngredientList(ingredientList, true);

        await _vm.loadAiPrscList();

        if(_vm.aiLabMstSeq){
          const aiLabRes = _vm.localData.aiPrscList.find(item => item.aiLabMstSeq == _vm.aiLabMstSeq);
          _vm.onClickViewDetail(aiLabRes);
        }
        
      } catch(err) {
        console.error(err)
      } finally {
        _vm.loading(false);
      }

    },
  },
};
</script>

<style lang="scss" scoped>
.erns-ag-grid-vue{
  min-height: 400px;  

  &.ingredient-grid{
    min-height: 400px;  
  }

  &.prod-grid{
    min-height: 400px;  
  }
}

.wd-50{
  width: 49.0% !important;
}

// AI 가이드 처방 목록 스타일
.ai-prsc-list-section {
  h5 {
    font-weight: 600;
    color: #333;
  }
}

.ai-prsc-card-container {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 20px;
}
</style>
