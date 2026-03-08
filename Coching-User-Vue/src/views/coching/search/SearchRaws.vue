<template>
  <!--section-->
  <section id="coching-search-result">
    <div class="inner">
      <div class="container">
        <div class="content-wrap">
          <!--search result-->
          <div v-show="keyword && keyword.trim().length > 0" 
            class="search-result-wrap link-box-sticky">
            <div class="title sticky-none">{{keyword}}
              <span>에 대한 검색결과</span>
            </div>
          </div>

          <!--section-content-->
          <!--광고 배너 없을 경우 including 삭제-->
          <div :class="['section-content search-content', { 'including': hasBanner }]">  
            <!--real-content-->
            <div class="real-content filter-content">
              
              <!-- pc 필터  -->
              <RawSearchFilter
                @update="onChangeRawFilter"
                ref="rawSearchFilter"
              ></RawSearchFilter>
              
              <!--search-raws -->
              <div class="main-content"
                :class="{
                  'search-base' : true
                }"  
              >
                <!--search-top-->
                <div class="search-top">
                  <!--total-wrap-->
                  <div class="total-wrap">

                    <div class="total-num">총
                      <span>{{(searchResults.raws.pi.totalCount) | eFmtNum}}</span>
                    </div>
                    
                    <div class="right">
                      <!--태블릿 이하 filter-->
                      <div class="m-filter pc-none modal-open-full filter-modal"
                        @click="onClickOpenMobileFilter"
                      >
                        <div class="ic-md ic-filter-md"></div>
                        필터
                        <div v-show="hasRawFilterOption()"
                          class="filter-badge">
                          {{ (
                            searchResults.raws.filterOption.optionList.filter(item=>item.checked).length 
                            + (searchResults.raws.filterOption.singleRaw ? 1 : 0)
                            + (searchResults.raws.filterOption.multipleRaw ? 1 : 0)
                          )
                          }}
                        </div>                        
                      </div>

                      <!--search-->
                      <div class="input-set m-none">
                        <div class="input-ic-set">
                          <input type="text" placeholder="결과 내 재검색"
                            v-model="search.raws.reSearchText"
                            @keyup.enter="loadSearchResult('raws')"
                          />
                          <button type="button" 
                            class="input-ic ic-md ic-search-md"
                            @click="loadSearchResult('raws')"
                          ></button>
                        </div>
                      </div>

                    </div>
                  </div>

                  <!--filter-box-->
                  <!--디폴트는 노출안함, 필터 선택 하나라도 있으면 노출하고, 하나라도 없으면 삭제-->
                  <div v-show="hasRawFilterOption()"
                    class="filter-box m-none">
                    <div v-if="searchResults.raws.filterOption.singleRaw" class="item">단일원료
                      <button type="button" class="ic-xsm ic-close-xsm"
                        @click="onClickRemoveRawFilterOption('singleRaw')"
                      ></button>
                    </div>

                    <div v-if="searchResults.raws.filterOption.multipleRaw" class="item">복합원료
                      <button type="button" class="ic-xsm ic-close-xsm"
                        @click="onClickRemoveRawFilterOption('multipleRaw')"
                      ></button>
                    </div>

                    <div v-for="(item, idx) of searchResults.raws.filterOption.optionList.filter(item=>item.checked)"
                      :key="idx" class="item">{{item.codeNmKo}}
                      <button type="button" class="ic-xsm ic-close-xsm"
                        @click="onClickRemoveRawFilterOption(item)"
                      ></button>
                    </div>                    
                  </div>

                  <!-- filterOption:{{ searchResults.raws.filterOption }} -->

                </div>

                <!--단일/복합 원료-->
                <div class="main-section">
                  <!-- <div class="h2">단일/복합 원료</div> -->

                  <div class="empty-content raw-empty"
                    v-show="searchResults.raws.pi.totalCount <= 0">
                    검색된 원료가 없습니다.
                  </div>

                  <!--card wrap-->
                  <div class="card-wrap">
                    <RawCard v-for="(item, idx) of searchResults.raws.list"
                      :key="idx"
                      :rawInfo="item"
                      @onClickCard="onClickRawCard(item)"
                      @onClickHashTag="(hashtag)=>{goSearchKeywordV2(hashtag, {hintField : '1001', emo : true})}"
                    />                    
                  </div>
                  <!--더보기-->
                  <div class="card-more"
                    v-show="searchResults.raws.pi.totalCount > searchResults.raws.list.length"
                  >
                    <button @click="loadSearchResultRawMore(0)"
                      type="button" class="btn btn-sm btn-gray">더보기</button>
                  </div>
                </div>
                <!--//단일/복합 원료-->
                
              </div>
              <!--//search-raws -->

            </div>

            <!--banner-wrap-->
            <!--광고배너는 2가지 타입 type-text / type-img-->
            <Advertise v-show="hasBanner" @update-banner-status="hasBanner = $event"></Advertise>

          </div>
        </div>
      </div>

      <!--오류-->
      <AlertModal ref="alertModal"></AlertModal>
      <ConfirmModal ref="confirmModal"></ConfirmModal>

    </div>    

    <RawInfoModal ref="rawInfoModal"
      @onClickRequest="onClickRequest"
    />
    <RequestRawModal
      v-if="isLoggedIn"
      ref="requestRawModal"></RequestRawModal>

    <MobileRawFilterModal ref="mobileRawFilterModal"/>    
    
  </section>
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';

import RawCard from '@/components/RawCard.vue';

import { getCodes } from '@/api/coching/comm/code';
import { getSearchRawList, getRecommendKeywords } from '@/api/coching/search/search';
import { isUserLoggedIn, getUserData, getHomeRouteForLoggedInUser } from '@/auth/utils';

import Advertise from '@/components/Advertise.vue';
import RawSearchFilter from '@/components/RawSearchFilter.vue';
import RawInfoModal from '@/components/modal/RawInfoModal.vue';
import MobileRawFilterModal from '@/components/modal/MobileRawFilterModal.vue';
import RequestRawModal from '@/components/modal/RequestRawModal.vue';

const DEF_SEARCH_ROWS_PER_PAGE = 12;

export default {
	name: 'coching-search-result',
	mixins: [ernsUtils],
	components: {
    Advertise,

    RawCard,

    RawSearchFilter,
    MobileRawFilterModal,
    RawInfoModal,
    RequestRawModal,
  },
	computed: {  },
  props: {
    keyword :{
      type : String,
      require : true
    },
    hintField:{
      type : String,
      default : '0000'
    }    
  },
	watch: {
    keyword(newKeyword) { //검색어 변경 
      this.loadSearchResult();
    }
	},
	data() {
		return {
      //로그인
      isLoggedIn : false,

      search :{
        raws : {
          reSearchText : "",
        },
      },

      recommendKeywords : [],

      compareItems : [],

      searchResults:{
        raws:{
          filterOption : {
            optionList:[],
            singleRaw : true,
            multipleRaw : true
          },

          //표시영역 구분안함
          list:[],  
          pi:{
            curPage : 1,
            totalCount : 0,
            perPage : DEF_SEARCH_ROWS_PER_PAGE       
          },

          //단일원료
          singleList : [],
          singlePi:{
            curPage : 1,
            totalCount : 0,
            perPage : DEF_SEARCH_ROWS_PER_PAGE       
          },

          //복합원료
          multipleList : [],
          multiplePi:{
            curPage : 1,
            totalCount : 0,
            perPage : DEF_SEARCH_ROWS_PER_PAGE       
          },
        }
      },

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
    //원료요청
    async onClickRequest(rawInfo, detailItem){
      const _vm = this;

      if(!_vm.isLoggedIn){
        _vm.onClickConfirmLogin();
        return;
      }

      await _vm.$refs["requestRawModal"].open({
        rawInfo:{
          rawSeq: rawInfo.rawSeq,
          rawDetailSeq: detailItem.rawDetailSeq,
          membSeq: detailItem.membSeq
        }
      });
    },

    //원료 클릭이벤트
    async onClickRawCard(rawInfo){
      const _vm = this;

      await _vm.$refs.rawInfoModal.open({
        rawInfo : rawInfo
      });
    },    

    //모바일 필터 클릭
    async onClickOpenMobileFilter(){
      const _vm = this;

      const rawSearchFilter = _vm.$refs.rawSearchFilter;
      _vm.searchResults.raws.filterOption = {...rawSearchFilter.getValue()};

      const ret = await _vm.$refs.mobileRawFilterModal.open({
          initFilterOption : _vm.searchResults.raws.filterOption
        , initReSearchText : _vm.search.reSearchText
      });

      //console.debug(ret);
      if(!ret){
        return;
      }

      const {reSearchText, optionList, singleRaw, multipleRaw} = ret;
      _vm.search.reSearchText = reSearchText;

      rawSearchFilter.setSingleRaw(singleRaw);
      rawSearchFilter.setMultipleRaw(multipleRaw);
      optionList.forEach(code =>{
        rawSearchFilter.setOption(code, code.checked || false);       
      });

      const filterValue = rawSearchFilter.getValue();
      await _vm.onChangeRawFilter(filterValue);
    },

    //원료 필터 변경시
    async onChangeRawFilter(filterOption){
      const _vm = this;
      _vm.searchResults.raws.filterOption = filterOption;

      await _vm.loadIniternalSearchRawResult();      
    },

    //원료 필터 제거
    async onClickRemoveRawFilterOption(pItem){
      const _vm = this;

      const rawSearchFilter = _vm.$refs.rawSearchFilter;

      if(typeof pItem === 'string' && pItem == "singleRaw"){
        rawSearchFilter.setSingleRaw(false);        
      }

      if(typeof pItem === 'string' && pItem == "multipleRaw"){
        rawSearchFilter.setMultipleRaw(false);  
      }

      if(typeof pItem === 'object'){
        rawSearchFilter.setOption(pItem, false);       
      }

      if(pItem){
        const filterValue = rawSearchFilter.getValue();
        await _vm.onChangeRawFilter(filterValue);
      }
    },

    //원료필터 체크 항목 유무
    hasRawFilterOption(){
      const _vm = this;

      const fOpt = _vm.searchResults.raws.filterOption;
      
      if(fOpt.singleRaw){return true;}
      if(fOpt.multipleRaw){return true;}
      if(fOpt.optionList && fOpt.optionList.filter(item=>item.checked).length > 0){return true;}
      
      return false;
    },

    //전체 검색
    //loadType : raws
    async loadSearchResult(loadType){
      const _vm = this;

      _vm.loading(true);
      try{
        const jobList = [];
        
        jobList.push(_vm.loadIniternalSearchRawResult());
        
        // Promise.all로 두 API 호출 병렬 처리
        await Promise.all(jobList);
        
      }catch(err){
        console.error(err);
        _vm.defaultApiErrorHandler(err); //기본에러처리
      }finally{
        _vm.loading(false);
      }
    },

    //원료검색    
    async loadIniternalSearchRawResult(){
      const _vm = this;

      _vm.searchResults.raws.list.splice(0, _vm.searchResults.raws.list.length);
      _vm.searchResults.raws.pi.curPage = 1;
      _vm.searchResults.raws.pi.totalCount = 0;

      _vm.searchResults.raws.singleList.splice(0, _vm.searchResults.raws.singleList.length);
      _vm.searchResults.raws.singlePi.curPage = 1;
      _vm.searchResults.raws.singlePi.totalCount = 0;

      _vm.searchResults.raws.multipleList.splice(0, _vm.searchResults.raws.multipleList.length);
      _vm.searchResults.raws.multiplePi.curPage = 1;
      _vm.searchResults.raws.multiplePi.totalCount = 0;
      
      const filterOption = _vm.searchResults.raws.filterOption;
      const bothLoad = (!filterOption.singleRaw && !filterOption.multipleRaw) || (filterOption.singleRaw && filterOption.multipleRaw);
      const rawParams = _vm.getRawSearchParam();
      const rawAddParams = {};
      const tasks = [];

      if(!bothLoad){
        if(filterOption.singleRaw){
          rawAddParams.maxIngdCount = 1; //단일원료만
          rawAddParams.minIngdCount = 1; //단일원료만
        }
        if(filterOption.multipleRaw){
          rawAddParams.minIngdCount = 2; //복합원료만
        }
      }

      tasks.push(
        {
          func: getSearchRawList,
          params: [{
            ...rawParams,
            ...rawAddParams,
          }],
          processResult: rawRes => {
            //console.debug(rawRes);
            const {list, maxScore, total, version} = rawRes.resultData;
            const dataList = _vm.convertSearchResultByVersion(version, list);
            _vm.searchResults.raws.list.push(...dataList);
            _vm.searchResults.raws.pi.curPage = 1;
            _vm.searchResults.raws.pi.totalCount = total;
          }
        }
      );

      // //단일 원료 만
      // if(bothLoad || filterOption.singleRaw){
      //   tasks.push(
      //     {
      //       func: getSearchRawList,
      //       params: [{
      //         ...rawParams,
      //         maxIngdCount: 1 //단일원료
      //         minIngdCount: 1 //단일원료
      //       }],
      //       processResult: rawRes => {
      //         //단일 원료
      //         //console.debug(rawRes);
      //         const {list, maxScore, total, version} = rawRes.resultData;
      //         const dataList = _vm.convertSearchResultByVersion(version, list);
      //         _vm.searchResults.raws.singleList.push(...dataList);
      //         _vm.searchResults.raws.singlePi.curPage = 1;
      //         _vm.searchResults.raws.singlePi.totalCount = total;
      //       }
      //     }
      //   );
      // }

      // //복합 원료 만
      // if(bothLoad || filterOption.multipleRaw){
      //   tasks.push(
      //     {
      //       func: getSearchRawList,
      //       params: [{
      //         ...rawParams,
      //         minIngdCount: 2 //복합원료
      //       }],
      //       processResult: rawRes => {              
      //         //console.debug(rawRes);
      //         const {list, maxScore, total, version} = rawRes.resultData;
      //         const dataList = _vm.convertSearchResultByVersion(version, list);
      //         _vm.searchResults.raws.multipleList.push(...dataList);
      //         _vm.searchResults.raws.multiplePi.curPage = 1;
      //         _vm.searchResults.raws.multiplePi.totalCount = total;
      //       }
      //     }
      //   );
      // }

      //여러 request 실행
      const results = await Promise.all(
        tasks.map(task => task.func(...task.params))
      );

      // 후처리 로직 실행
      tasks.forEach((task, index) => {
          task.processResult(results[index]);
      });
    },

    //원료 더보기
    async loadSearchResultRawMore(pLoadType){
      const _vm = this;

      _vm.loading(true);      
      try{
        const filterOption = _vm.searchResults.raws.filterOption;
        const bothLoad = (!filterOption.singleRaw && !filterOption.multipleRaw) || (filterOption.singleRaw && filterOption.multipleRaw);
        const rawParams = _vm.getRawSearchParam();
        const tasks = [];
        
        if(pLoadType == 0){
          const pi = _vm.searchResults.raws.pi;   
          rawParams.page = ++pi.curPage;
          if(!bothLoad){
            if(filterOption.singleRaw){
              rawParams.maxIngdCount = 1; //단일원료만
              rawParams.minIngdCount = 1; //단일원료만
            }
            if(filterOption.multipleRaw){
              rawParams.minIngdCount = 2; //복합원료만
            }
          }          
          tasks.push(
            {
              func: getSearchRawList,
              params: [rawParams],
              processResult: rawRes => {
                //단일/복합 원료
                //console.debug(rawRes);
                const {list, maxScore, total, version} = rawRes.resultData;
                const dataList = _vm.convertSearchResultByVersion(version, list);
                _vm.searchResults.raws.list.push(...dataList);
              }
            }
          );
        } else if(pLoadType == 1){
          const pi = _vm.searchResults.raws.singlePi;   
          rawParams.page = ++pi.curPage;
          if(filterOption.singleRaw){
            rawParams.maxIngdCount = 1; //단일원료만
            rawParams.minIngdCount = 1; //단일원료만
          }
          tasks.push(
            {
              func: getSearchRawList,
              params: [rawParams],
              processResult: rawRes => {
                //단일 원료
                //console.debug(rawRes);

                const {list, maxScore, total, version} = rawRes.resultData;
                const dataList = _vm.convertSearchResultByVersion(version, list);
                _vm.searchResults.raws.singleList.push(...dataList);
              }
            }
          );
        }else{
          const pi = _vm.searchResults.raws.multiplePi;
          rawParams.page = ++pi.curPage;
          if(filterOption.multipleRaw){
            rawParams.minIngdCount = 2; //복합원료만
          }
          tasks.push(
            {
              func: getSearchRawList,
              params: [rawParams],
              processResult: rawRes => {
                //복합 원료
                //console.debug(rawRes);

                const {list, maxScore, total, version} = rawRes.resultData;
                const dataList = _vm.convertSearchResultByVersion(version, list);
                _vm.searchResults.raws.multipleList.push(...dataList);                
              }
            }
          );
        }        

        //여러 request 실행
        const results = await Promise.all(
          tasks.map(task => task.func(...task.params))
        );

        // 후처리 로직 실행
        tasks.forEach((task, index) => {
          task.processResult(results[index]);
        });

      }catch(err){
        console.error(err);
        _vm.defaultApiErrorHandler(err); //기본에러처리
      }finally{
        _vm.loading(false);
      }
    },

    //원료검색 파라미터 리턴
    getRawSearchParam(){
      const _vm = this;

      const keyword = (_vm.keyword || "").trim();
      const param = {
        ingdIds: _vm.ingdIds
        , page : 1
        , rowSize : DEF_SEARCH_ROWS_PER_PAGE
        , hintField: _vm.hintField
      };

      if(keyword.indexOf('#') >= 0){  
        param["hashtag"] = keyword;
      }else{
        param["text"] = `${keyword}`;
      }

      //카테고리 추가
      if(_vm.hasRawFilterOption()){
        const checkList = _vm.searchResults.raws.filterOption.optionList.filter(item=>item.checked);
        const categories = checkList.map(item=>{
          return `${item.grpCode}:${item.code}`;
        });
        param["categories"] = [...categories];
      }

      const reSearchText = (_vm.search.raws.reSearchText || "").trim();
      if(reSearchText.length > 0){
        param["reSearchText"] = reSearchText;
      }

      return param;
    },

    async fetchData(){
      const _vm = this;

      try{
        _vm.isLoggedIn = (isUserLoggedIn() || null) ? true : false;

        _vm.onClickRemoveRawFilterOption();
        await _vm.loadSearchResult();
        await _vm.loadRecommendKeywords();        
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },

    //추천검색어 로드
    async loadRecommendKeywords(){
      const _vm = this;

      //_vm.loading(true);
      try{
        const params = {};
        const res = await getRecommendKeywords(params);

        const {list} = res.resultData;
        _vm.recommendKeywords = [...list];

      }catch(err){
        console.error(err);
        _vm.defaultApiErrorHandler(err); //기본에러처리
      }finally{
        //_vm.loading(false);
      }
    },

    

		docReady() {
			const _vm = this;

      /*
      var $filter = $(".filter-wrap");
      var $footer = $("footer .inner");
      var $section = $(".section-content");
      var $banner = $(".banner-wrap"); // 배너 선택
      var $header = $("header .inner"); // 헤더 선택
      var $stickyBox = $(".link-box-sticky"); // link-box-sticky 선택
      var headerHeight = $header.outerHeight(); // 헤더 높이 계산
      var filterHeight = $filter.outerHeight();
      var bannerHeight = $banner.outerHeight(); // 배너 높이 계산

      // 페이지에 따라 다른 동작을 수행
      if ($filter.length && $section.length && $footer.length && $banner.length) {
        // 스크롤 이벤트 처리
        $(window).on("scroll", function () {
          var windowScrollTop = $(window).scrollTop();
          var sectionTop = $section.offset().top;
          var footerTop = $footer.offset().top;
          var bannerTop = $banner.offset().top; // 배너의 상단 위치

          var stickyBoxHeight = $stickyBox.outerHeight() || 0; // sticky 박스 높이
          var referenceHeight = headerHeight + stickyBoxHeight;
          var filterTopPosition = windowScrollTop - sectionTop + referenceHeight + 16;

          // 1400px 이하일 경우
          if ($(window).width() <= 1383) {
            // 필터가 배너에 닿기 전까지 움직이도록 설정
            if (windowScrollTop + filterHeight + referenceHeight < bannerTop) {
              // 필터가 배너에 닿기 전까지는 기존 로직대로 움직이도록
              if (windowScrollTop > sectionTop - referenceHeight) {
                $filter.css({
                  position: "absolute",
                  top: filterTopPosition + "px",
                });
              } else {
                $filter.css({
                  position: "absolute",
                  top: 0,
                });
              }
            } else {
              // 필터가 배너에 닿으면 필터 위치를 고정
              $filter.css({
                position: "absolute",
                top: bannerTop - sectionTop - filterHeight + "px", // 배너의 상단 위치에 고정
              });
            }
          } else {
            // 1400px 이상일 경우 기존 로직 유지
            if (windowScrollTop + filterHeight + referenceHeight < footerTop - 96) {
              if (windowScrollTop > sectionTop - referenceHeight) {
                $filter.css({
                  position: "absolute",
                  top: filterTopPosition + "px",
                });
              } else {
                $filter.css({
                  position: "absolute",
                  top: 0,
                });
              }
            } else {
              // footer 위에서 멈춤
              $filter.css({
                position: "absolute",
                top: footerTop - sectionTop - filterHeight - 96 + "px",
              });
            }
          }
        });

        // 초기 필터 위치 업데이트
        $(window).trigger("scroll");
      }
      */
      
		},
	}
}
</script>

<style lang="scss" scoped>
.empty-content.raw-empty {
  min-height: 8rem;
}

.tag-badge.primary {
  .item.link {
    cursor: pointer;
  }
}
</style>

<style lang="scss">
#coching-search-result {
  
}
</style>
