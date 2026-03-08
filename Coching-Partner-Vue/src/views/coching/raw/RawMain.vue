<template>
  <!--section-->
  <section>
    <div class="container">
      <div class="content-wrap">
        <!--title-->
        <div class="h1">{{ $t('') || '원료 등록 및 관리'}}</div>
        <!--content-->
        <div class="content-inner">
          <!--total-wrap-->
          <div class="total-wrap">
            <div class="total-num">{{ $t('') || '총' }}<span>{{ rawData.pi.totalItem }}</span></div>
            <div class="right">
              <!--checkbox-->
              <div class="chehck-filter-wrap">
                <div class="chehck-radio-flex">
                  <div>
                    <div class="checkbox">
                      <input v-model="rawData.sc.registY" @change="updateregistValue" id="checkbox-01" type="checkbox" value="true"/>
                      <label for="checkbox-01" class="checkbox-label">등록</label>
                    </div>
                  </div>
                  <div>
                    <div class="checkbox">
                      <input v-model="rawData.sc.registN" @change="updateregistValue" id="checkbox-02" type="checkbox" value="false"/>
                      <label for="checkbox-02" class="checkbox-label">미등록</label>
                    </div>
                  </div>
                </div>
                <div class="chehck-radio-flex">
                  <div>
                    <div class="checkbox">
                      <input v-model="rawData.sc.useY" @change="updateUseValue" id="checkbox-03" type="checkbox" value="true"/>
                      <label for="checkbox-03" class="checkbox-label">노출</label>
                    </div>
                  </div>
                  <div>
                    <div class="checkbox">
                      <input v-model="rawData.sc.useN" @change="updateUseValue" id="checkbox-04" type="checkbox" value="false"/>
                      <label for="checkbox-04" class="checkbox-label">미노출</label>
                    </div>
                  </div>
                </div>
              </div>

              <!--select-->
              <CochingSelect v-model="rawData.sc.searchField"
                name="sources"
                :placeholder="$t('') || '전체'"
                :options="CODES.SC_001"
                :label="'label'"
                :trackBy="'value'"
                >
            </CochingSelect>

              <!--search-->
              <div class="input-set">
                <div class="input-ic-set">
                  <input v-model="rawData.sc.searchText" 
                    @keyup.enter="onClickSearch"
                    type="text" placeholder="검색" />
                  <button @click="onClickSearch" 
                    type="button" class="input-ic ic-md ic-search-md"></button>
                </div>
              </div>

              <!--button-->
              <a v-if="eumLoginUser.userType == '002'" @click="onClickSetRaw()"
                href="javascript:;" class="btn btn-sm btn-primary ic ic-plus">{{ $t('') || '원료 등록' }}</a>
              <a v-if="eumLoginUser.userType == '002'" @click="onClickUpload()"
                href="javascript:;" class="btn btn-sm btn-green ic ic-upload modal-open upload-modal">{{ $t('') || '엑셀 업로드' }}</a>
              <!-- <a href="javascript:;" class="btn btn-sm btn-green ic ic-download" download>{{ $t('') || '엑셀 다운로드' }}</a> -->
            </div>
          </div>
          <div class="erns-ag-grid-vue">
            <ag-grid-vue       
              class="ag-theme-quartz"
              :ref="rawData.gridId"
              :gridOptions="rawData.gridOptions"
              :columnDefs="rawData.columnDefs"
              :rowData="rawData.list"
              @gridReady="(params) => ernsAgGrid_registerGrid(rawData.gridId, params, rawData.gridOptions)"
              
              :frameworkComponents="rawData.frameworkComponents"

              @cellClicked="onClickDetail"

              pagination=false
              suppressRowTransform=true
            >
            </ag-grid-vue>
          </div>
          <!-- <div v-if="rawData.list.length == 0" class="result-none">검색결과가 없습니다.</div> -->

          <!--pagenation-->
          <Pagenation
            v-model="rawData.pi.curPage"
            :totalRows="rawData.pi.totalItem"
            :perPage="rawData.pi.perPage"
            @input="onChangePage"></Pagenation>
        </div>
      </div>
      <!--엑셀업로드-->
      <div ref="uploadModal" class="modal for-upload">
        <div class="layer">
          <div class="layer-content">
            <div class="modal-inner modal-md">
              <div class="modal-content">
                <div class="modal-header">
                  <div @click="onClickUploadClose()" class="modal-close"></div>
                  <div class="title">엑셀 업로드</div>
                  <div class="upload-notice">* 업로드하는 데이터가 많을 경우 몇분의 시간이 소요될 수 있습니다.</div>
                </div>
                <div class="modal-body">
                  <!--upload-->
                  <div @click="handleInputFile" 
                    @dragover.prevent="handleDragOver"
                    @dragenter.prevent="handleDragEnter"
                    @dragleave="handleDragLeave"
                    @drop.prevent="handleDrop" class="upload-wrap">
                    <div class="upload-area" id="uploadArea">
                      <input @change="handleFileChange" type="file" ref="fileInput" id="fileInput" style="display: none" />
                      <div id="fileList">
                        <div v-for="(item, index) of excel.filelist" :key="index"
                            class="file-item">
                            <div @click.stop="onClickFileDownload(item)" class="file-wrap">
                              <div class="file-name">
                                <span>{{ item.fileName.split('.')[0] }}</span>
                                <span class="file-extension">{{ '.' + item.fileName.split('.').pop() }}</span>
                              </div>
                              <div class="file-size">({{ eufmtFileSize(item.fileSize) }})</div>
                            </div>
                            <button @click.stop="deleteFile(index)" type="button" class="delete-button"></button>
                          </div>
                        </div>
                      <p v-show="excel.filelist.length === 0">{{ $t('') || '파일을 마우스로 끌어 오거나 클릭하세요' }}</p>
                    </div>
                  </div>
                </div>
                <div class="modal-footer">
                  <button @click="onClickUploadClose()"
                    type="button" class="btn btn-md btn-gray bottom-modal-close">취소</button>
                  <button @click="onClickExcelDownload" 
                    type="button" class="btn btn-md btn-green">양식 다운로드</button>
                  <button @click="onClickExcelUpload" 
                    type="button" class="btn btn-md btn-primary">파일 업로드</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div ref="rawDocModal" class="modal">
       <ModalLayout :classOption="classOptions.doc">
        <!-- Header Content -->
          <template v-slot:header>
            <div @click="onClickRawDocClose" class="modal-close"></div>
            <div class="title">구비서류</div>
          </template>

          <!-- Body Content -->
          <template v-slot:body>
            <div class="document-content scroller">
              <div v-for="(code, idx) of CODES.CM_DOC" :key="idx" 
                :class="{'empty' : !modalData.rawDocList.some(doc => doc.docCode === code.code)}"
                class="item">{{ code.codeName }}</div>
            </div>
          </template>
       </ModalLayout>
      </div>

        <!--오류-->
      <AlertModal ref="alertModal"></AlertModal>
      <ConfirmModal ref="confirmModal"></ConfirmModal>

    </div>
  </section>
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';
import "ag-grid-community/styles/ag-grid.css"; // Mandatory CSS required by the Data Grid
import "ag-grid-community/styles/ag-theme-quartz.css";

import { AgGridVue } from "ag-grid-vue";
import ernsAgGrid from '@/components/ernsAgGrid/ernsAgGrid';
import { AG_GRID_COMMON_COLUMN_DEFS } from '@/constants/agGrid';
import ButtonCellRenderer from "@/components/ernsAgGrid/ButtonCellRenderer.vue";
import ToggleCellRenderer from '@/components/ernsAgGrid/ToggleCellRenderer.vue';

import { getCodes } from '@/api/coching/comm/code';
import { getRawList, updateRawUseYn, updateRawDelYn, updateRawDetailUseYn, getDocList, uploadRawExcel } from '@/api/coching/comm/raw';

import Pagenation from '@/components/Pagenation.vue';
import CochingSelect from '@/components/CochingSelect.vue';
import ModalLayout from '@/components/dialog/ModalLayout.vue';
import { _ } from 'core-js';

const base = process.env.VUE_APP_API_BASE_URL;

const DEF_RAW = {
	sc : {
		searchField : '',
    searchText : '',
    ptnSeq: null,
    useYn: ''
	},
	pi:{
		curPage : 1,
    totalItem : 0,
    perPage : 10
		//perPage : 1 //Test
	},
	summary : []
};

const DEF_RAW_INF = {
  rawSeq: '',  
  ptnSeq: '',
  rawDetailSeq: '',
  rawName: '',
  rawMemb: '',
  rgtDttm: '',
};

const DEF_FILE ={
  fileId: null,
  fileName: '',
  file: null,
};

const GRID_ID_MAIN = 'main-grid';

export default {
	name: 'coching-manage-main',
	mixins: [ernsUtils, ernsAgGrid],
	components: {
    Pagenation,
    CochingSelect,
    ModalLayout,
    AgGridVue,    
    ButtonCellRenderer,
    ToggleCellRenderer,
},
	computed: {    
  },
	watch: {
	},
	props: {
    
  },
	data() {
    const _vm = this;
    const AG_GRID_COLUMN_DEFS = [
        { hide: true,
          headerName: "Row ID", field: "rowId"          , width: 70 
          , valueGetter: (params) => params.node.id // 내부 rowId 출력
        },
        { headerName: "번호"        , field: "index"        , width: 100  , cellClass: 'flex-center'} ,
        { headerName: "원료명"      , field: "rawName"     , width: 500  , cellClass: 'flex-left',
          wrapText: true, // 자동 줄바꿈 활성화
          autoHeight: true, // 행 높이를 자동 조정
          cellStyle: { "white-space": "normal" } // 여러 줄로 표시
        },
        {
          headerName: "구비서류"      , field: "setButton"  , width: 100  , cellClass: 'text-center', 
          cellRenderer: "ButtonCellRenderer", // 렌더러 지정  
          cellRendererParams: {
            label: "",
            btnClass: 'btn-xsm ic-lg ic-list-check modal-open document-modal',
            action: (params) => {
              this.onClickDocModal(params.data);
            },
          },
        },
        { headerName: "담당자"      , field: "membName"       , width: 200  , cellClass: 'text-center'},
        { headerName: "등록여부"       , field: "rawDetailSeq"        , width: 100  , cellClass: 'text-center'
          , valueGetter: (params) => params.data.rawDetailSeq ? '등록' : '미등록'
        },
        { headerName: "등록일"       , field: "rgtDttm"        , width: 200  , cellClass: 'flex-center'},
        {
          headerName: "원료노출"      , field: "setButton"  , width: 100  , cellClass: 'text-center', 
          cellRenderer: "ToggleCellRenderer", // 렌더러 지정  
          cellRendererParams: (params) => ({
            checked: params.data.useYn == "Y",
            isChange: true,
            isDisabled: () => {
              if(this.eumLoginUser.userType != '002'){
                return true;
              }
              return false;
            },
            action: ($event) => {
              this.updateUseYn(params.data);
            },
          }),
        },
        {
          headerName: "자료노출"      , field: "setButton"  , width: 100  , cellClass: 'text-center', 
          cellRenderer: "ToggleCellRenderer", // 렌더러 지정  
          cellRendererParams: (params) => ({
            checked: params.data.detailUseYn  == "Y",
            isChange: params.data.rawDetailSeq > 0,
            isDisabled: () => {
              return false;
            },
            action: async () => {
              if (!params.data.rawDetailSeq) {
                // rawDetailSeq가 없으면 팝업 표시 후 checked 상태 유지
                await _vm.$refs["alertModal"].open({
                  title: _vm.$t('') || '자료 노출',
                  titleHtml: _vm.$t('') || '원료자료 등록 후 설정해주세요.',
                });

                return;
              }

              //rawDetailSeq가 있는 경우에만 updateDetailUseYn 실행
              _vm.updateDetailUseYn(params.data);
            },
          }),
        },
        {
          headerName: "삭제"      , field: "setButton"  , width: 100  , cellClass: 'text-center',
          cellRenderer: "ButtonCellRenderer", // 렌더러 지정  
          cellRendererParams: {
            label: "",
            btnClass: 'btn-xsm ic-lg ic-trash',
            action: (params) => {
              this.onClickDelete(params.data);
            },
          },
        },
       
      ];
		return {
      CODES: {
            SC_001 : [],
            CM_DOC: [],
          },
      classOptions: {
        doc: 'modal-xlg',
        detail: 'modal-xlg'
      },
			rawInfo : {...DEF_RAW_INF},
      rawData : {
        gridId : GRID_ID_MAIN,
        nextRowIndex : 1,
        isDisabledAllSave : true,
        gridOptions : {
          isEditData : false,
          immutableData : true,
          defaultColDef : AG_GRID_COMMON_COLUMN_DEFS,
          getRowId: (params) => params.data.rowIndex, // 각 행의 ID를 'rowIndex' 로 지정
          localeText: {
            noRowsToShow: '등록된 원료가 없습니다.'
          },
        },
        columnDefs: AG_GRID_COLUMN_DEFS,
        frameworkComponents :{
          ButtonCellRenderer,
          ToggleCellRenderer,
        },
        list: [],
        pi: {...DEF_RAW.pi},
        sc: {...DEF_RAW.sc},
      },
      modalData:{
        rawDocList:[],
      },
      excel: {
        filelist: [],
      },
		}
	},
	async mounted() {
		const _vm = this;

    await _vm.loadCodes();
		_vm.docReady();
		_vm.fetchData();
	},
	beforeDestroy() {
		const _vm = this;
	},
	methods: {

		//데이터 가져오는 부분(사용하지 필요없어도 반드시 선언)
		async fetchData() {
			const _vm = this;

			_vm.loading(true);
			try {

        // URL에서 query 값이 있으면 검색 조건 복구
        if (_vm.$route.query.searchText) {
          _vm.rawData.sc.searchField = _vm.$route.query.searchField || "";
          _vm.rawData.sc.searchText = _vm.$route.query.searchText || "";
          _vm.rawData.sc.registYn = _vm.$route.query.registYn || "";
          _vm.rawData.sc.useYn = _vm.$route.query.useYn || "";
          _vm.rawData.pi.curPage = parseInt(_vm.$route.query.curPage) || 1;

          // 이전 검색조건으로 데이터 불러오기
          await _vm.loadList(_vm.rawData.pi.curPage);
        } else {
          // 일반 초기 데이터 로드
          await _vm.loadList(1);
        }
        
			} catch (err) {
				_vm.defaultApiErrorHandler(err); //기본에러처리
			} finally {
				_vm.loading(false);
			}
		},

    updateregistValue() {
      const _vm = this;
      const isregisted = _vm.rawData.sc.registY;
      const isNotregisted = _vm.rawData.sc.registN;

      if (isregisted && isNotregisted) {
        // 둘 다 선택된 경우
        _vm.rawData.sc.registYn = '';
      } else if (isregisted) {
        // 등록만 선택된 경우
        _vm.rawData.sc.registYn = 'Y';
      } else if (isNotregisted) {
        // 미등록만 선택된 경우
        _vm.rawData.sc.registYn = 'N';
      } else {
        // 둘 다 선택되지 않은 경우
        _vm.rawData.sc.registYn = '';
      }
      _vm.loadList(1);
    },

    // 노출/미노출 상태 업데이트
    updateUseValue() {
      const _vm = this;
      const isUseY = _vm.rawData.sc.useY;
      const isUseN = _vm.rawData.sc.useN;

      if (isUseY && isUseN) {
        _vm.rawData.sc.useYn = '';
      } else if (isUseY) {
        _vm.rawData.sc.useYn = 'Y';
      } else if (isUseN) {
        _vm.rawData.sc.useYn = 'N';
      } else {
        _vm.rawData.sc.useYn = '';
      }

      _vm.loadList(1);
    },

    //검색
		onClickSearch() {
      const _vm = this;

      _vm.loadList(1);
    },

    async loadList(pageNo) {
			const _vm = this;
      const dataMap = _vm.rawData, pInfo = _vm.rawData.pi, searchOp = _vm.rawData.sc;
      pInfo.curPage = pageNo || 1;

			_vm.loading(true);
      try {
          const params = _vm.getListSearchParam();
          const rawListRes = await getRawList(params);
          const resData = rawListRes.resultData;
          
          const pageInfo = resData.pageInfo;
          dataMap.pi = {
            ...dataMap.pi,
            curPage : pageInfo.currentPage,
            totalItem : pageInfo.totalItem,
            perPage : pageInfo.pageItemSize
          }

          //데이터 바인딩
          dataMap.list = resData.list.map((item, index) => {
            return {
              rowIndex : dataMap.nextRowIndex++, //rowIndex 설정
              index: _vm.$options.filters.eufRowNumberForPageInfo(index, pInfo),
              _isEditRow : false,
              ...item,
            };
          });
          dataMap.list = JSON.parse(JSON.stringify(dataMap.list));
          _vm.ernsAgGrid_resetOriginalData(dataMap.gridId, dataMap.list);   
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
		},
    

    async loadCodes(){
      const _vm = this;

      const codeRes = await getCodes({grpCode:"CH003", etc5:_vm.$i18n.locale, useYn: 'Y'});
      const { resultCode, resultFailMessage, resultData } = codeRes;

      _vm.CODES.CM_DOC = [...resultData.list];

      _vm.CODES.SC_001 = [
        {value:"", label : _vm.$t('') || '전체'},
        {value:"001", label : _vm.$t('') || '원료명'},
        {value:"002", label : _vm.$t('') || '담당자'}
      ];

    },

    //페이지 변경
		onChangePage(pageNo) {
      const _vm = this;

      _vm.loadList(pageNo);
    },

    //검색조건
		getListSearchParam() {
      const _vm = this;
      const pInfo = _vm.rawData.pi, searchOp = _vm.rawData.sc;

			const retParam = {
        ptnSeq: _vm.partnerInfo.ptnSeq,
				searchField : searchOp.searchField,
				searchText : $.trim("" + searchOp.searchText),
        useYn: searchOp.useYn,
        registYn : searchOp.registYn,
        membUseYn: 'Y',//사용자 계정 사용 여부
        delYn: 'N',//삭제여부

        page : pInfo.curPage,
        rowSize : pInfo.perPage                
      };

      if("004" == _vm.eumLoginUser.userType){
        retParam.membSeq = _vm.eumLoginUser.userSeq;
      }
			
			return retParam;
    },
    //등록하기
    onClickSetRaw(){
      const _vm = this;

      _vm.ermPushPage({name:'coching-raw-regist'});
    },
    //상세
    onClickDetail(event){
      const _vm = this;

      const item = event.node.data;
      const clickField = event.colDef.field;

      const goDetailFileds = ["index", "rawName", "membName", "prodCompany", "rawDetailSeq", "rgtDttm"];
        if(goDetailFileds.includes(clickField)){
          _vm.ermPushPage({name:'coching-raw-edit',
            query : {
              rawSeq : item.rawSeq,
              managerSeq : item.managerSeq,
              searchField: _vm.rawData.sc.searchField,
              searchText: _vm.rawData.sc.searchText,
              registYn: _vm.rawData.sc.registYn,
              useYn: _vm.rawData.sc.useYn,
              curPage: _vm.rawData.pi.curPage,
            }
          });
          return;
        }
    },
    //구비서류 팝업
    async onClickDocModal(item){
      const _vm = this;

      await _vm.loadRawDoc(item);

      $(_vm.$refs["rawDocModal"]).fadeIn(300, function() {

        // 모달창 스크롤 적용
        const isMobile = /iPhone|iPad|iPod|Android/i.test(navigator.userAgent);

        if (!isMobile) {
          // 데스크톱에서만 nicescroll 적용
          $(".scroller").niceScroll();

          // 리사이즈 시 스크롤 재조정
          $(window).on("resize", function () {
            $(".scroller").getNiceScroll().resize();
          });

          // 스크롤 이벤트에서 다른 스크롤 방지
          $(".scroller").on("scroll", function (e) {
            e.stopPropagation(); // 다른 스크롤 이벤트가 영향을 미치지 않도록 방지
          });
        } else {
          // 모바일에서는 기본 스크롤 사용
          $(".scroller").css("overflow", "auto");
        }

        // 뒷 페이지 스크롤 막기
        $("body").css("overflow", "hidden");
      });   
     
    },
    async loadRawDoc(item){
      const _vm = this;

      const res = await getDocList({rawSeq: item.rawSeq});
      const { resultCode, resultFailMessage, resultData } = res;

      const list = resultData.list;
      // 서버에서 가져온 list를 docList 저장
      _vm.modalData.rawDocList = list.map((item) => ({
        docCode: item.docCode,
      }));
    },
    onClickRawDocClose(){
      const _vm = this;
      $(_vm.$refs["rawDocModal"]).fadeOut(300);
      $("body").css("overflow", "visible");
      $(".scroller").scrollTop(0);
    },

    //원료 노출
    async updateUseYn(item, event){
      const _vm = this;

      if(_vm.eumLoginUser.userType != '002'){
        event.preventDefault(); // 기본 체크 동작 막기
        event.stopPropagation(); // 이벤트 전파 막기
        return;
      }

      item.useYn = item.useYn === 'Y' ? 'N' : 'Y'; 
      const rawListRes = await updateRawUseYn({rawSeq: item.rawSeq, useYn: item.useYn});
      const ret = await _vm.$refs["alertModal"].open({
              title: _vm.$t('') || '원료 노출',
              titleHtml : _vm.$t('') || '변경되었습니다.'
            });

      if(ret){
        const curPage = _vm.rawData.pi.curPage;
        _vm.loadList(curPage);
      }
    },

    //자료 노출
    async updateDetailUseYn(item){
      const _vm = this;

      item.detailUseYn = item.detailUseYn === 'Y' ? 'N' : 'Y'; 
      const rawListRes = await updateRawDetailUseYn({rawSeq: item.rawSeq,rawDetailSeq: item.rawDetailSeq, useYn: item.detailUseYn});
      const ret = await _vm.$refs["alertModal"].open({
              title: _vm.$t('') || '자료 노출',
              titleHtml : _vm.$t('') || '변경되었습니다.'
            });

      if(ret){
        const curPage = _vm.rawData.pi.curPage;
        _vm.loadList(curPage);
      }
    },

    //원료 삭제
    async onClickDelete(item){
      const _vm = this;

      const ret = await _vm.$refs["confirmModal"].open({
        title: _vm.$t('') || '원료 삭제',
        content : _vm.$t('') || '등록한 원료를 삭제하시겠습니까?'
      });
      
      if(ret){

        const rawListRes = await updateRawDelYn({rawSeq: item.rawSeq, delYn: "Y"});
        const ret = await _vm.$refs["alertModal"].open({
                title: _vm.$t('') || '원료 삭제',
                titleHtml : _vm.$t('') || '삭제되었습니다.'
              });

        if(ret){
          const curPage = _vm.rawData.pi.curPage;
          _vm.loadList(curPage);
        }
      }
    },

    //엑셀 업로드 클릭
    async onClickUpload(){
      const _vm = this;
     
      $(_vm.$refs["uploadModal"]).fadeIn(300);     
    },

    //엑셀 업로드 닫기
    onClickUploadClose(){
      const _vm = this;

      // 선택된 파일 초기화
      _vm.excel.filelist = [];
      if (_vm.$refs.fileInput) {
        _vm.$refs.fileInput.value = '';
      }

      $(_vm.$refs["uploadModal"]).fadeOut(300);
      $("body").css("overflow", "visible");
    },

    handleInputFile(){
      this.$refs.fileInput.click();
    },
    //File click
    handleFileChange(event) {
      this.handleFileUpload(event.target.files);
      event.target.value = ''; // 파일 입력 필드 초기화
    },
    //file drag
    handleDrop(event) {
      event.preventDefault();
      this.handleFileUpload(event.dataTransfer.files);
      event.target.value = ''; // 파일 입력 필드 초기화
    },
    handleDragOver(event) {
      this.isDragging = true;
      event.dataTransfer.dropEffect = 'copy'; // 드롭 효과를 'copy'로 설정
    },
    handleDragEnter(event) {
      this.isDragging = true;
    },
    handleDragLeave() {
      this.isDragging = false;
    },
    async handleFileUpload(files) {
      const _vm = this;

      if (files.length > 0) {
        this.file = files[0];

        // 기존 파일을 삭제하고 새 파일만 추가
        _vm.excel.filelist = [{
          file: this.file,
          fileName: this.file.name,
          fileSize: this.file.size
        }];
      }
    },
    async deleteFile(idx){
      const _vm = this;

      if (_vm.excel.filelist.length > 0) {
        _vm.excel.filelist = []; // 파일 리스트를 비움
      }
    },
    addFile(){
      const _vm = this;

      _vm.excel.filelist.push({...DEF_FILE});
    },

    //양식 다운로드
    async onClickExcelDownload(){
      // 파일 경로 설정 (public 폴더 내)
      const filePath = '/ext/raw_exceluploadSample.xlsx';

      // 링크 요소 생성
      const link = document.createElement('a');
      const subPath = process.env.VUE_APP_PUBLIC_PATH + filePath;
      link.href = location.origin + subPath.replace(/\/+/g, '/');
      link.setAttribute('download', "원료등록 엑셀업로드 양식.xlsx"); // 파일 이름 설정

      // 링크 클릭 트리거
      document.body.appendChild(link);
      link.click();

      // 다운로드 후 링크 요소 제거
      document.body.removeChild(link);
    },

    //엑셀 업로드
    async onClickExcelUpload() {
      const _vm = this;

      if (_vm.excel.filelist.length === 0) {
        _vm.$refs["alertModal"].open({
          title: _vm.$t('') || '엑셀 업로드',
          titleHtml: _vm.$t('') || '업로드할 파일을 선택해주세요.'
        });
        return;
      }

      _vm.loading(true);
      try{
        const formData = new FormData();
        formData.append('ptnSeq', _vm.partnerInfo.ptnSeq);
        formData.append('file', _vm.excel.filelist[0].file);

        const res = await uploadRawExcel(formData);

        _vm.loading(false);
        
        // fileId가 있으면 confirmModal 띄우기
        if(res.resultData && res.resultData.fileId){
          const confirmRet = await _vm.$refs["confirmModal"].open({
            title: _vm.$t('') || '원료 엑셀 업로드',
            content: _vm.$t('') || '업로드 하지 못한 성분리스트 엑셀을 내려받으시겠습니까?',
            okButtonText: _vm.$t('') || '내려받기'
          });

          if(confirmRet){
            // 파일 다운로드
            const fileId = res.resultData.fileId;
            const fileName = encodeURIComponent(res.resultData.fileName);

            const downloadPath = `${base}/file/rawResultDownload.do?fId=${fileId}&fName=${fileName}`;
            document.location.href = downloadPath;
          }
          _vm.loadList(1);
          _vm.onClickUploadClose();
        } else {
          // fileId가 없으면 기존처럼 alertModal 띄우기
          const ret = await _vm.$refs["alertModal"].open({
            title: _vm.$t('') || '원료 업로드',
            titleHtml : _vm.$t('') || '업로드되었습니다.'
          });

          if(ret){
            _vm.loadList(1);
            _vm.onClickUploadClose();
          }
        }

      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },

		docReady() {
			const _vm = this;
		},
	}
}
</script>

<style lang="scss" scoped>
.erns-ag-grid-vue{
  min-height: calc(90vh - 350px);
}

.upload-notice {
  color: #3d44ea;
  font-size: 14px;
  margin-top: 0.3rem;
  line-height: 1;
}
</style>

<style lang="scss">
#coching-mypage-main {

}
</style>
