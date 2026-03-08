<template>
   <!--section-->
   <!--section은 3가지 타입 full, main-->
   <section id="coching-raw-detail" class="full" :class="{'active': isSmallGnb}">
      <div class="container h-100">
        <!--content-->
        <div class="content">
          <!--main-content-->
          <div class="main-content">
            <!--base-view-->
            <div class="base-view">
              <!--title-wrap-->
              <div class="title-wrap">
                <!--left-->
                <div class="left">
                  <div class="h1">{{rawInfo.rawName || '-'}}</div>
                  <div class="info">
                    <span v-if="rawInfo.supplier">
                      공급사 : 
                      <span class="search-text  default-color addIcon"
                        @click="goSearchKeywordV2(`${rawInfo.supplier}`, {hintField : '1011', emo : true})"
                      >{{rawInfo.supplier}}</span>
                    </span>
                    <span v-else>공급사 : {{rawInfo.supplier || '-'}} </span>

                    <span v-if="rawInfo.prodCompany">
                      제조사 : 
                      <span class="search-text  default-color addIcon"
                        @click="goSearchKeywordV2(`${rawInfo.prodCompany}`, {hintField : '1012', emo : true})"
                      >{{rawInfo.prodCompany}}</span>
                    </span>
                    <span v-else>제조사 : {{rawInfo.prodCompany || '-'}}</span>

                    <span v-if="rawInfo.prodCountryName">
                      제조국 : 
                      <span class="search-text  default-color addIcon"
                        @click="goSearchKeywordV2(`${rawInfo.prodCountryName}`, {hintField : '1013', emo :false})"
                      >{{rawInfo.prodCountryName}}</span>
                    </span>
                    <span v-else>제조국 : {{rawInfo.prodCountryName || '-'}}</span>
                  
                    <span>등록일 : {{activeRawDetailInfo.rgtDttm | eFmtDateFormat('YY.MM.DD') }}</span>
                    <span>최종수정일 : {{activeRawDetailInfo.chngDttm | eFmtDateFormat('YY.MM.DD') }}</span>
                    <span>조회수 : {{(activeRawDetailInfo.viewCnt || '0') | fmtViews}}</span>
                  </div>
                </div>
                <!-- {{ activeRawDetailInfo }} -->

                <!--right-->
                <div class="right">
                  <div @click="onClickSendMessage(activeRawDetailInfo)"
                    class="ic-wrap">
                    <div class="ic-lg ic-folded-note"></div>
                  </div>

                  <div @click="onClickRequest"
                    class="ic-wrap modal-open send-modal">
                    <div class="ic-lg ic-message"></div>
                  </div>
                  
                  <div 
                    @click="onClickFavorite"
                    class="ic-wrap heart-wrap"
                    :class="{'active' : activeRawDetailInfo.isFavorite}">
                    <div class="ic-lg ic-heart"></div>
                  </div>
                </div>

              </div>

              <!--sales-wrap-->
              <div class="sales-wrap">
                <!--sales-info-->
                <div class="sales-info">
                  <div class="title">{{ activeRawDetailInfo.title || '-' }}</div>
                  <div class="info">
                    <div>{{ activeRawDetailInfo.membName || '-' }}</div>
                    <!-- <div>{{ activeRawDetailInfo.chngDttm | eFmtDateFormat('YY.MM.DD') }}</div> -->
                    <div class="tag-wrap">
                      <span v-for="(item, idx) of activeRawDetailInfo.hashTags" :key="idx"
                        class="hashtag"
                        @click="goSearchKeywordV2(`#${item}`, {hintField : '1001', emo : true})"
                      >#{{item}}</span>
                    </div>
                  </div>
                </div>

                <div v-if='rawInfo.rawDetailInfo.length > 1' 
                  class="register-area">                    
                  <label 
                    v-if="rawInfo.supplier">{{rawInfo.supplier || '-'}}의 동일 원료
                  </label>

                  <!--register-wrap-->
                  <div class="register-wrap scroller-x">
                    <!--item-->
                    <div 
                      v-for="(dItem, idx) of rawInfo.rawDetailInfo"                        
                      class="item"
                      :class="{'active' : (dItem.rawDetailSeq == activeRawDetailInfo.rawDetailSeq)}"
                      :key="dItem.rawDetailSeq"
                      @click="onClickRawDetail(dItem)"
                      >
                      <div class="content">
                        <div class="title">{{ dItem.title || '-' }}</div>
                        <div class="info">
                          <div class="date-wrap">
                            <div>{{ dItem.membName || '-' }}</div>
                            <div>{{ dItem.chngDttm | eFmtDateFormat('YY.MM.DD') }}</div>
                          </div>
                          <div class="inquiry">{{ (rawInfo.viewCnt || '0') | fmtViews}}</div>
                        </div>
                      </div>
                    </div>

                  </div>
                </div>
              </div>

              <!--view-tabs-->
              <div class="view-tabs">
                <a :class="{'active': activeViewTab=='info'}" @click="onClickTab('info')">기본정보</a>
                <a :class="{'active': activeViewTab=='data'}" @click="onClickTab('data')">원료사 자료</a>
                <a :class="{'active': activeViewTab=='document'}" @click="onClickTab('document')">구비서류</a>
              </div>

              <div class="base-view-content">
                <!--info-->
                <div id="info" ref="info_contents">
                  <div class="h2">기본 정보</div>
                  <div class="info-content">
                    <!--ewg-table-->
                    <div class="ewg-table">
                      <table>
                        <colgroup>
                          <col width="80" />
                        </colgroup>
                        <thead>
                          <tr>
                            <th>EWG</th>
                            <th>성분명</th>
                          </tr>
                        </thead>
                        <tbody>

                          <tr 
                            v-for="(ingd, idx) of rawInfo.rawElemInfo" 
                            :key="idx" class="link"
                            @click="onClickIngdList(ingd)">
                            <td>
                              <div class="ewg" 
                              :class="getEwgClass(getEwgInfo(`${ingd.ewgScoreMin}-${ingd.ewgScore}`).max)">
                              {{getEwgInfo(`${ingd.ewgScoreMin}-${ingd.ewgScore}`).scoreText}}</div>
                            </td>
                            <td>
                              {{ingd.repNameKo}}
                              <template v-if="ingd.repNameEn">
                                ({{ingd.repNameEn}})
                              </template>
                            </td>
                          </tr>  

                        </tbody>
                      </table>
                    </div>

                    <!--info-box-->
                    <div class="info-box">
                      <div class="info-table">
                        <table>
                          <colgroup>
                            <col width="60" />
                          </colgroup>
                          <tbody>
                            <tr>
                              <th>효능</th>
                              <td>{{displayCateNames((rawInfo.rawTypeInfo || []).filter(item=>'001' == item.grpCode), 'nameKo')}}</td>
                            </tr>
                            <tr>
                              <th>기능</th>
                              <td>{{displayCateNames((rawInfo.rawTypeInfo || []).filter(item=>'002' == item.grpCode), 'nameKo')}}</td>
                            </tr>
                            <tr>
                              <th>제품</th>
                              <td>{{displayCateNames((rawInfo.rawTypeInfo || []).filter(item=>'003' == item.grpCode), 'nameKo')}}</td>
                            </tr>
                            <tr>
                              <th>성상</th>
                              <td>{{displayCateNames((rawInfo.rawTypeInfo || []).filter(item=>'004' == item.grpCode), 'nameKo')}}</td>
                            </tr>
                            <tr>
                              <th>원물</th>
                              <td>{{displayCateNames((rawInfo.rawTypeInfo || []).filter(item=>'005' == item.grpCode), 'nameKo')}}</td>
                            </tr>
                            <tr>
                              <th>제조사</th>
                              <td>{{rawInfo.prodCompany || '-'}}</td>
                            </tr>
                            <tr>
                              <th>제조국</th>
                              <td>{{rawInfo.prodCountryName || '-'}}</td>
                            </tr>                                              
                          </tbody>
                        </table>
                      </div>
                    </div>
                  </div>
                </div>

                <!--data-->
                <div id="data" ref="data_contents" class="raw-detail-content">
                  <div class="h2">원료사 자료</div>

                  <div id="contents-zoom-control">
                    <button class="zoom-in"
                      :disabled="zoomLevel >= 100"
                      @click="onClickZoomIn"
                    >+</button>
                    <button class="reset"
                      :disabled="zoomLevel == 100"
                      @click="onClickZoomReset"
                    >↻</button>
                    <button class="zoom-out"
                      :disabled="zoomLevel <= 50"
                      @click="onClickZoomOut"
                    >-</button>
                  </div>

                  <div v-for="(file, fIdx) of detailContents.files" :key="fIdx"
                    class="data-content"
                    :class="['zoom-level-' + zoomLevel]"
                  >

                  <!-- <ContinuousPdfViewer class="contents-pdf"
                    v-if="'PDF' == file.fileExt.toUpperCase()"
                    :pdfUrl="eumFileDownPath(file.fileId)"
                    @onFetchedPages="onFetchedPages"
                  /> -->
                  <ContinuousPdfPageImageViewer class="contents-pdf"
                    v-if="isPdfViewContents(file.fileExt)"
                    :fileId="file.fileId"
                    @onFetchedPages="onFetchedPages"
                  />

                  <div v-else class="contents-img contents-center">
                    <img 
                    :src="eumFileImagePath(file.fileId, '0', errorImg)"
                    @error="onImageError"
                    :alt="file.fileName"/>

                  </div>
                </div>
                
                <div v-if="detailContents.detail.rawDetail"
                  class="data-content ql-editor"
                  :class="['zoom-level-' + zoomLevel]"
                  v-html="detailContents.detail.rawDetail"
                ></div>

                <div v-if="!hasRawDetailContent()" class="empty-content raw-empty">
                    원료사 자료 내용이 없습니다.                      
                </div>

              </div>

              <!--document-->
              <div v-show="showDocument" id="document" ref="document_contents">
                <div class="h2">구비서류</div>
                <div class="document-content">
                  <div 
                    v-for="(docItem, idx) of rawInfo.docinfo"
                    :key="idx"
                    class="item"
                    :class="{'empty': docItem.yn != 'Y'}"
                  >{{ docItem.nameKo }}</div>                      
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- rawInfo : {{rawInfo}} -->
      <!-- activeRawDetailInfo:{{ activeRawDetailInfo }} -->

      <!--오류-->
      <AlertModal ref="alertModal"></AlertModal>
      <ConfirmModal ref="confirmModal"></ConfirmModal>

      <RequestRawModal
        v-if="isLoggedIn"
        ref="requestRawModal"></RequestRawModal>
    </div>
  </section>
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';
import favoriteMixin from '@/components/favorite/favoriteMixin';
import { EventBus } from '@/components/eventBus'; 

import { getSearchRaw } from '@/api/coching/search/search';
import { getDetail } from '@/api/coching/raw/raw';
import { isUserLoggedIn, getUserData, getHomeRouteForLoggedInUser } from '@/auth/utils';
import { REF_CODES } from '@/constants/favorite';


import Advertise from '@/components/Advertise.vue';
import RequestRawModal from '@/components/modal/RequestRawModal.vue';
import ContinuousPdfViewer from '@/components/ContinuousPdfViewer.vue';
import ContinuousPdfPageImageViewer from '@/components/ContinuousPdfPageImageViewer.vue';

//import { VueEditor } from 'vue2-quill-editor';
//import 'quill/dist/quill.core.css'; // Core 테마 (테마 없이 최소 스타일)
import 'quill/dist/quill.snow.css'; // CSS만 가져옴
import ERROR_IMG from '@/assets/images/ic-img-lg.svg';
import { init } from 'aos';

export default {
	name: 'coching-raw-detail',
	mixins: [ernsUtils, favoriteMixin],
	components: {
    Advertise,
    RequestRawModal,
    ContinuousPdfViewer,
    ContinuousPdfPageImageViewer,
  },
	computed: {
    errorImg(){
      return ERROR_IMG;
    }   
  },
	watch: {
		'$route'(newVal, oldVal) {
			this.docReady();
      this.fetchData();
		}
	},
	props: {
    rawSeq:{
      type: [String, Number], // 전달받는 데이터 타입
      default : 0,      
    },
    rawDetailSeq:{
      type: [String, Number], // 전달받는 데이터 타입
      default : 0,  //Note: 없으면 첫번째로 자동처리    
    },
  },
	data() {
		return {
      //로그인
      isLoggedIn : false,

      CODES: {        
      },
      rawInfo: {
        rawElemInfo : [],
        rawDetailInfo : [],
        rawTypeInfo: [],
        docinfo:[],
      },
      activeRawDetailInfo : {
        hashTags:[],
      },      
      detailContents:{
        detail:{},
        files:[],
      },

      activeViewTab : "",

      showDocument : false,

      zoomLevel : 100,
      isZoomControlInitialized : false,

      hasBanner: false, // 광고 배너 유무
		}
	},
	async mounted() {
		const _vm = this;

		_vm.docReady();
		_vm.fetchData();
	},
	beforeDestroy() {
		const _vm = this;
	},
	methods: {
    isPdfViewContents(fileExt){
      const _vm = this;

      const upperfileExt = fileExt.toUpperCase();
      switch(upperfileExt){
        case "PDF":
        case "PPT":
        case "PPTX":
        case "DOC":
        case "DOCX":
          return true;
        default:
          return false;
      }      
    },

    hasRawDetailContent(){
      const _vm = this;

      if(_vm.detailContents?.files?.length > 0){
        return true;
      }
      
      if(_vm.detailContents?.detail?.rawDetail){
        return true;
      }
      
      return false;
    },

    onImageError(event) {
      const targetObj = event.target;
      targetObj.src = this.errorImg; // 대체 이미지로 설정
      targetObj.style.width = '100px'; // 원하는 너비 설정
      targetObj.style.height = '100px'; // 원하는 높이 설정
    },

    onFetchedPages(totalPages){
      const _vm = this;
      console.debug(`totalPages :${totalPages}`);

      _vm.showDocument = true;
      _vm.initZoomControl();
    },

    onClickZoomIn(){
      const _vm = this;
      _vm.zoomLevel += 10;
      
    },

    onClickZoomOut(){
      const _vm = this;
      _vm.zoomLevel -= 10;      
    },

    onClickZoomReset(){
      const _vm = this;
      _vm.zoomLevel = 100;

    },

    //텝 클릭
    onClickTab(prefixSection){
      const _vm = this;

      const target = `${prefixSection}_contents`;      
      const targetObject = _vm.$refs[target];

      const offset = 80; // 원하는 보정값 (예: 50px 위로 이동)
      if (targetObject) {
        const topPosition = targetObject.getBoundingClientRect().top + window.pageYOffset - offset;
        window.scrollTo({
          top: topPosition,
          behavior: 'smooth'
        });
      }
    },

    //쪽지보내기
    onClickSendMessage(detailItem){
      const _vm = this;

      //_vm.onClickClose();
      EventBus.$emit('open-message-modal', {
        membSeq : detailItem.membSeq,
        membName : detailItem.membName,
      });      
    },

    //원료요청
    async onClickRequest(){
      const _vm = this;

      if(!_vm.isLoggedIn){
        _vm.onClickConfirmLogin();
        return;
      }

      await _vm.$refs["requestRawModal"].open({
        rawInfo:{
          rawSeq: _vm.rawInfo.rawSeq,
          rawDetailSeq: _vm.activeRawDetailInfo.rawDetailSeq,
          membSeq: _vm.activeRawDetailInfo.membSeq
        }
      });      
    },

    //즐겨찾기
    async onClickFavorite(){
      const _vm = this;

      const ret = await _vm.setToggleMyFavorite({
        refCode : REF_CODES.RAW,
        refSeq : _vm.activeRawDetailInfo.rawDetailSeq
      });

      if(ret == null){
        return;
      }
      
      for(const dItem of _vm.rawInfo.rawDetailInfo){
        if(dItem.rawDetailSeq == _vm.activeRawDetailInfo.rawDetailSeq){
          dItem.isFavorite = ret.resultData == 1;          
          break;
        }
      }
    },

    //원료상세 로드
    async onClickRawDetail(dItem){
      const _vm = this;

      _vm.activeRawDetailInfo = dItem;
      _vm.adjustHashTags();
      await _vm.loadRawDetail();
    },

    //전성분 상세보기 클릭
    onClickIngdList(ingdItem){
      const _vm = this;
      
      _vm.ermPushPage({
        name:'coching-ingredients', 
        query : {
          rawSeq : _vm.rawInfo.rawSeq,
          ingdId : ingdItem.id
        }
      });
    },

    //hashtag 정리
    adjustHashTags(){
      const _vm = this;

      const rawHashTags = [];
      const strHashTags = _vm.activeRawDetailInfo.hashtag || '';
      rawHashTags.push(...strHashTags.split('#'));

      //console.debug(rawHashTags);
      _vm.activeRawDetailInfo.hashTags = 
        _vm.cleanArrayWithReduce(
          rawHashTags.filter(tag=>{
            return !('#' == tag || '' == tag);
          })
        );
    },

    //즐겨찾기 설정
    async settingMyFavorite(){
      const _vm = this;

      const refSeqs = (_vm.rawInfo.rawDetailInfo || []).map(item=>item.rawDetailSeq);
      const ret = await _vm.getMyFavorites(REF_CODES.RAW, refSeqs);

      if(ret == null){
        return;
      }

      const detailList = _vm.rawInfo.rawDetailInfo || [];
      for(const dItem of detailList){
        const favItem = ret.resultData.list.find(item=>item.refSeq == dItem.rawDetailSeq);
        if(favItem){
          dItem.isFavorite = true;
        }
      }
    },

    //원료정보 로드
    async loadRawInfo(){
      const _vm = this;

      _vm.loading(true);
      try{
        // _vm.rawInfo = {};
        // _vm.activeRawDetailInfo = {};

        const params = {
          id : (""+(_vm.rawSeq || "")).trim()
        };

        const rawRes = await getSearchRaw(params);
        const {list, maxScore, total, version} = rawRes.resultData;
        if((total || 0) < 1){
          await _vm.$refs["alertModal"].open({
            title: _vm.$tt('test001') || '원료정보를 조회 할 수 없습니다.'
          });
          return;
        }

        const dataList = _vm.convertSearchResultByVersion(version, list);
        _vm.rawInfo = dataList[0];
        _vm.rawInfo.rawDetailInfo = (_vm.rawInfo.rawDetailInfo || []).map(item=>{
          return {
            ...item,
            isFavorite: false
          }
        });
        _vm.activeRawDetailInfo = _vm.rawInfo.rawDetailInfo
          .find(item=>item.rawDetailSeq == _vm.rawDetailSeq);
        if(!_vm.activeRawDetailInfo){
          //Note: 없으면 첫번째로 자동처리
          _vm.activeRawDetailInfo = _vm.rawInfo.rawDetailInfo[0];
        }
        //임시처리 조회수
        if((_vm.activeRawDetailInfo.viewCnt || 0) <= 0){
          _vm.activeRawDetailInfo.viewCnt = _vm.rawInfo.viewSumCnt;
        }

        _vm.adjustHashTags();
        await _vm.settingMyFavorite();
        await _vm.loadRawDetail();

        _vm.docReady();

      }catch(err){
        console.error(err);
        _vm.defaultApiErrorHandler(err); //기본에러처리
      }finally{
        _vm.loading(false);
      }
    },

    //원료 상세 로드
    async loadRawDetail(){
      const _vm = this;

      _vm.loading(true);
      try{
        const params = {
          rawDetailSeq : (""+(_vm.activeRawDetailInfo.rawDetailSeq || "")).trim()
        };

        const rawDetailRes = await getDetail(params);
        _vm.detailContents = rawDetailRes.resultData;
        
        const contFiles = _vm.detailContents.files || [];
        const hasPdf = contFiles.find(file=>file.fileExt.toUpperCase() == "PDF");
        if(!hasPdf) { //PDF 가 없는 경우 zoom 바로 초기화
          _vm.showDocument = true;
          _vm.initZoomControl();
        }        

      }catch(err){
        console.error(err);
        _vm.defaultApiErrorHandler(err); //기본에러처리
      }finally{
        _vm.loading(false);
      }
    },


		//데이터 가져오는 부분(사용하지 필요없어도 반드시 선언)
		async fetchData() {
			const _vm = this;

			_vm.loading(true);
			try {
        _vm.isLoggedIn = (isUserLoggedIn() || null) ? true : false;

        await _vm.loadRawInfo();

			} catch (err) {
				_vm.defaultApiErrorHandler(err); //기본에러처리
			} finally {
				_vm.loading(false);
			}
		},

    async loadCodes(){
      const _vm = this;

    },

    docReady() {
			const _vm = this;

      // $(".scroller-x").each(function () {
      //   if (!$(this).data("scrollbar-initialized")) {
      //     $(this).scrollbar();
      //     $(this).data("scrollbar-initialized", true); // 초기화 여부 플래그 설정
      //   }
      // });

      // $(".scroller-x").off("mousewheel DOMMouseScroll").on("mousewheel DOMMouseScroll", function (e) {
      //   var delta = e.originalEvent.wheelDelta || -e.originalEvent.detail;
      //   var scrollAmount = 30; // 스크롤 이동 거리 설정

      //   if (delta > 0) {
      //     // 마우스 휠을 위로 스크롤할 때 (왼쪽으로 이동)
      //     $(this).scrollLeft($(this).scrollLeft() - scrollAmount);
      //   } else {
      //     // 마우스 휠을 아래로 스크롤할 때 (오른쪽으로 이동)
      //     $(this).scrollLeft($(this).scrollLeft() + scrollAmount);
      //   }

      //   // 기본 동작을 막아 세로 스크롤이 작동하지 않게 함
      //   e.preventDefault();
      // });
      _vm.initZoomControl();
		},

    initZoomControl() {
      const _vm = this;

      // 이미 이벤트 핸들러가 등록되었는지 확인하는 변수
      if (_vm.isZoomControlInitialized) {
        return; // 이미 초기화된 경우 함수 종료
      }

      // console.debug("initZoomControl");

      const zoomControl = $("#contents-zoom-control");
      const infoArea = $("#info");
      const dataArea = $("#data");      
      const documentArea = $("#document");

      // 디바운스 함수 (이벤트를 연속으로 호출할 때 한번만 실행하도록 지연)
      const debounce = (func, delay) => {
        let timeout;
        return function () {
          const context = this;
          const args = arguments;
          clearTimeout(timeout);
          timeout = setTimeout(() => func.apply(context, args), delay);
        };
      };

      // 스크롤 이벤트 핸들러
      const handleScroll = debounce(function () {
        const scrollPosition = $(window).scrollTop();
        const windowHeight = $(window).height();
        const infoAreaOffset = infoArea.offset().top;
        const dataAreaOffset = dataArea.offset().top;
        const documentAreaOffset = documentArea.offset().top;  
        
        // 정보 영역에 도달하면 표시
        if(scrollPosition <= 30) {
          _vm.activeViewTab = ""; //초기 진입시 이상현상 대응
        }else if (scrollPosition > documentAreaOffset - 180) {
          _vm.activeViewTab = "document";
        }else if (scrollPosition > dataAreaOffset - 180) {
          _vm.activeViewTab = "data";
        }else if (scrollPosition > infoAreaOffset - 180) {
          _vm.activeViewTab = "info";
        }else{
          _vm.activeViewTab = "";
        }
        
        // data 영역에 도달하면 표시
        if (scrollPosition >= dataAreaOffset - 200) {
          if (scrollPosition >= documentAreaOffset - 100) {
            zoomControl.removeClass("show");
          } else {
            zoomControl.addClass("show");
          }
        } else {
          zoomControl.removeClass("show");
        }
      }, 100); // 100ms 지연 후 이벤트 실행

      // 스크롤 이벤트에 디바운스 적용
      $(window).scroll(handleScroll);

      // 이벤트 핸들러가 등록되었음을 표시
      this.isZoomControlInitialized = true;
    }
	}
}
</script>

<style lang="scss" scoped>
.tag-wrap{
  .hashtag + .hashtag {
    margin-left: 3px; 
  }
}

.register-wrap {
  .item.active {
    box-shadow: 0 0 0 2px var(--color--bk) inset;
  }
}

.raw-detail-content {
  //position: relative;

  .data-content{

    .contents-pdf {
      margin: 1rem 0;
    }

    .contents-img img {
      width: 100%;
    }

    .contents-center {
      margin: 1rem 0;
      display: flex;
      justify-content: center;
      align-items: center;
    }
  }
}

.document-content {
  .item.empty-doc {
    color: rgba(0, 0, 0, 0.4);
  }
}

.zoom-level-100 {
  width: 100%;
  margin: 0 auto;
}

.zoom-level-90 {
  width: 90%;
  margin: 0 auto;
}

.zoom-level-80 {
  width: 80%;
  margin: 0 auto;
}

.zoom-level-70 {
  width: 70%;
  margin: 0 auto;
}

.zoom-level-60 {
  width: 60%;
  margin: 0 auto;
}

.zoom-level-50 {
  width: 50%;
  margin: 0 auto;
}

@media screen and (max-width: 1400px) {
  .section-content.including #contents-zoom-control {
    right: 20px;
  }  
}

@media screen and (min-width: 1399px) {
  .section-content.including #contents-zoom-control {
    right: 22.5rem;
  }  
}

@media screen and (min-width: 1920px) {
  .section-content.including #contents-zoom-control {
    right: calc(50vw - 38.5rem);
  }  
}

@media screen and (max-width: 1023px) {
  #contents-zoom-control {
    opacity: 0;
    visibility: hidden !important;
  }
}
</style>

<style lang="scss">
#coching-raw-detail {
  .raw-detail-content {
    .ql-editor {
      // 리스트 스타일
      > *:nth-child(n + 2) {
        padding-top: 0 !important;
      }
      
      // 링크 스타일
      a {
        color: inherit !important;
        text-decoration: inherit !important;
      }

      strong, b {
        font-weight: bold !important;
        font-style: normal !important;
      }

      em, i {
        font-weight: normal !important;
        font-style: italic !important;
      }

      // 헤딩 태그들을 위한 특별한 스타일 (reset.css 우선순위 높이기)
      h1 {
        font-size: 2em !important;
      }
      
      h2 {
        font-size: 1.5em !important;
      }
      
      h3 {
        font-size: 1.17em !important;
      }
      
      h4 {
        font-size: 1em !important;
      }
      
      h5 {
        font-size: 0.83em !important;
      }
      
      h6 {
        font-size: 0.75em !important;
      }

      .raw-company{
        max-width: 200px;
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
    }
  }
}
</style>
