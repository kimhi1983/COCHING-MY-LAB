<template>
  <!--section-->
  <section id="coching-main" class="full main" :class="{'active': isSmallGnb}">
    <div class="container h-100">
      <!--content-->
      <div class="content">
        <!--main-content-->
        <div class="main-content">
          <div class="main-section">
            <!--h2-->
            <div class="h2 flex">
              <div class="ic-lg ic-tv"></div>
              코칭 TV
            </div>

            <div class="empty-content"
              v-show="searchResults.tv.pi.totalCount <= 0">
              등록된 코칭TV가 없습니다.
            </div>
            <!--card-wrap-->
            <div class="card-wrap tv-card main-tv-card">
              <!--item-->
              <CochingTvCard 
                v-for="(item, idx) of searchResults.tv.list" 
                :key="idx"
                :tvInfo="item"
                :useThumbnail="true">
              </CochingTvCard>
            </div>
          </div>
        </div>

        <!--header-->
        <div v-if="false && recommendKeywords.length > 0"
          class="header" style="padding-top: 1.5rem;">
          <div class="tag-badge scroll-x-area">
            <a v-for="(item, idx) of recommendKeywords" 
              :key="idx"
              @click="goSearchKeyword(item.name)"
              href="javascript:;" class="item">{{ item.name}}</a>            
          </div>
        </div>

        <div class="main-content" style="padding-top: 2.0rem;">
          <!--h2-->
          <div class="h2 flex">
            <div class="ic-lg ic-xlg ic-drop"></div>
            추천 원료
          </div>

          <!--신규 원료-->
          <div class="main-section" style="padding-top: 0.1rem;">
            <div class="empty-content"
              v-show="searchResults.raws.newRawPi.totalCount <= 0">
              검색된 원료가 없습니다.
            </div>

            <!--card-wrap-->
            <!--main card 홀수이기 때문에 21개까지 데이터 가져와야함-->
            <div class="card-wrap main-card">                      
              <RawCard
                v-for="(item, idx) of searchResults.raws.newRawList"
                :key="idx"
                :rawInfo="item"                        
                @onClickCard="onClickRawCard(item)"
                @onClickHashTag="(hashtag)=>{goSearchKeywordV2(hashtag, {hintField : '1001', emo : true})}"
              />
            </div>
          </div>
        </div>
      </div>

      <!--오류-->
      <AlertModal ref="alertModal"></AlertModal>
      <ConfirmModal ref="confirmModal"></ConfirmModal>

      <RawInfoModal ref="rawInfoModal"
        @onClickRequest="onClickRequest"
      />
      <RequestRawModal
        v-if="isLoggedIn"
        ref="requestRawModal"></RequestRawModal>

    </div>
  </section>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import '@/assets/css/swiper-bundle.min.css';
import { Swiper, SwiperSlide } from 'vue-awesome-swiper';
import AOS from 'aos';
import 'aos/dist/aos.css';
import Promise from "bluebird";

import { isUserLoggedIn, getUserData, getHomeRouteForLoggedInUser } from '@/auth/utils';
import { getSearchRawMainList, getRecommendKeywords, getSearchTvList } from '@/api/coching/search/search';

import { getBannerList, setBannerClick } from '@/api/coching/comm/banner';
import { getPopupList, setPopupClick } from '@/api/coching/comm/popup';
import { getYoutubeInfo } from '@/api/coching/comm/cochingtv';

import AuthView from './Auth.vue';
import RawCard from '@/components/RawCard.vue';
import CochingTvCard from '@/components/CochingTvCard.vue';

import RawInfoModal from '@/components/modal/RawInfoModal.vue';
import RequestRawModal from '@/components/modal/RequestRawModal.vue';

const DEF_popupImage = require("@/assets/images/bg-visual-01.jpg");
const DEF_SEARCH_RAW_ROWS_PER_PAGE = 21;
const DEF_SEARCH_TV_ROWS_PER_PAGE = 7;


export default {
  name: 'coching-main',
  mixins: [ernsUtils],
  components : {
    Swiper,
    SwiperSlide,

    AuthView,
    RawCard,
    CochingTvCard,

    RawInfoModal,
    RequestRawModal,
},
  computed : { },
  watch:{
    '$i18n.locale' : async function(){
      const _vm = this;
      _vm.$nextTick(() => _vm.$refs.loginForm.validate());
    },
  },
  props: {},
  data(){
    return {
      //로그인
      status: '',
      isLoggedIn : false,

      recommendKeywords : [],
      
      //검색
      searchResults:{
        //원료        
        raws:{
          filterOption : {
            optionList:[],
            singleRaw : true,
            multipleRaw : true
          },
          newRawList : [],
          popularRawList : [],
          
          //신규원료
          newRawPi:{
            curPage : 1,
            totalCount : 0,
            perPage : DEF_SEARCH_RAW_ROWS_PER_PAGE       
          },
          //인기원료
          popularRawPi:{
            curPage : 1,
            totalCount : 0,
            perPage : DEF_SEARCH_RAW_ROWS_PER_PAGE       
          },
        },

        //코칭 TV
        tv:{
          list:[],
          pi:{
            curPage : 1,
            totalCount : 0,
            perPage : DEF_SEARCH_RAW_ROWS_PER_PAGE       
          },
        },
      },

      // //팝업
      // popupData : {
      //   swiperOption: {
      //     init : false,
      //     pagination: {
      //       el: '.swiper-pagination-popup',
      //       clickable: true,
      //     },
      //     autoplay: {
      //       delay: 3000,
      //       disableOnInteraction: false,
      //     },
      //     loop: true,
      //     //effect: "fade",
      //   },

      //   replaceImge : DEF_popupImage,
      //   slideList : [
      //     //{name:"팝업1" , src:DEF_popupImage, bannerUrl:"http://www.naver.com"}
      //   ],
      //   resData : {},
      // }
    }
  },
  mounted(){
    const _vm = this;

    _vm.docReady();
    _vm.fetchData();   

    window.addEventListener(
      'resize',
      this.debounce(() => {
        console.log('Resize event triggered!');
      }, 200)
    );
  },
  beforeDestroy(){
    const _vm = this;
	},
  methods: {
    onSwiper(swiper){
      //console.log(swiper);
    },

    // 배너클릭
    async onClickBanner(bannerItem){
      const _vm = this;

      try {
        _vm.loading(true);

        const params = {
					bannerSeq : bannerItem.bannerSeq
				};
        const res = await setBannerClick(params);
        const resData = res.resultData;

      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }

      const targetUrl = (bannerItem.bannerUrl || "").trim();
      if("" == targetUrl){
        return;
      }

      _vm.eumLocationUrl(targetUrl);
    },

    // 팝업클릭
    async onClickPopup(popupItem){
      const _vm = this;

      try {
        _vm.loading(true);

        const params = {
					popupSeq : popupItem.popupSeq
				};
        const res = await setPopupClick(params);
        const resData = res.resultData;

      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }

      const targetUrl = (popupItem.popupUrl || "").trim();
      if("" == targetUrl){
        return;
      }      
      _vm.eumLocationUrl(targetUrl);
    },

    //원료 클릭이벤트
    async onClickRawCard(rawInfo){
      const _vm = this;

      await _vm.$refs.rawInfoModal.open({
        rawInfo: rawInfo
      });
    },

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

    async loadDataAll(){
      const _vm = this;

      // 로딩 작업 정의
      const dataLoadJobs = [
        _vm.loadIniternalSearchRawResult(), //신규 원료, 인기원료
        _vm.loadICochingTvList(),     //코칭TV
        _vm.loadRecommendKeywords(),  //추천검색어
        // _vm.loadPopup(false),    //팝업
        // _vm.loadBanner(false),  //배너
      ];

      const loadErrors = await Promise.all(dataLoadJobs);
      //TODO : 모두 작업 끝났는데 오류 날경우.      
      //loadErrors
    },

    //원료검색    
    async loadIniternalSearchRawResult(){
      const _vm = this;

      const filterOption = _vm.searchResults.raws.filterOption;
      if(filterOption.singleRaw){
        _vm.searchResults.raws.newRawList.splice(0, _vm.searchResults.raws.newRawList.length);
      }

      if(filterOption.multipleRaw){
        _vm.searchResults.raws.popularRawList.splice(0, _vm.searchResults.raws.popularRawList.length);
      }

      // Fisher–Yates 셔플
      const getRandomSample = function(list, count) {
        const array = [...list]; // 원본 보호를 위해 복사
        for (let i = array.length - 1; i > 0; i--) {
          const j = Math.floor(Math.random() * (i + 1));
          [array[i], array[j]] = [array[j], array[i]]; // swap
        }
        return array.slice(0, count);
      };

      const rawParams = _vm.getRawSearchParam();
      const tasks = [
        { //신규원료
          func: getSearchRawMainList,
          params: [{
            ...rawParams
            , sortField : "rgtDttm"            
            , sortOrder : "desc"
            , rowSize : DEF_SEARCH_RAW_ROWS_PER_PAGE
          }],
          processResult: rawRes => {
            
            //console.debug(rawRes);

            const {list, maxScore, total, version} = rawRes.resultData;
            const dataList = _vm.convertSearchResultByVersion(version, list);

            // API 자체에서 랜덤처리로 변경
            // const randomList = getRandomSample(dataList, DEF_SEARCH_RAW_ROWS_PER_PAGE);
            // _vm.searchResults.raws.newRawList.push(...randomList);

            _vm.searchResults.raws.newRawList.push(...dataList);

            _vm.searchResults.raws.newRawPi.curPage = 1;
            _vm.searchResults.raws.newRawPi.totalCount = total;
          }
        },
        // { //인기원료
        //   func: getSearchRawList,
        //   params: [{
        //     ...rawParams
        //     , "sortField" : "viewSumCnt"
        //     , "sortOrder" : "desc"
        //     , rowSize : DEF_SEARCH_ROWS_PER_PAGE
        //   }],
        //   processResult: rawRes => {            
        //     //console.debug(rawRes);

        //     const {list, maxScore, total, version} = rawRes.resultData;
        //     const dataList = _vm.convertSearchResultByVersion(version, list);
        //     _vm.searchResults.raws.popularRawList.push(...dataList);
        //     _vm.searchResults.raws.popularRawPi.curPage = 1;
        //     _vm.searchResults.raws.popularRawPi.totalCount = total;
        //   }
        // }
      ]

      //여러 request 실행
      const results = await Promise.all(
        tasks.map(task => task.func(...task.params))
      );

      // 후처리 로직 실행
      tasks.forEach((task, index) => {
          task.processResult(results[index]);
      });
    },

    //원료검색 파라미터 리턴
    getRawSearchParam(){
      const _vm = this;

      const param = {
        text : ("").trim() //메인은 검색키워드 없음
        , page : 1
        , rowSize : DEF_SEARCH_RAW_ROWS_PER_PAGE
      };

      //카테고리 추가
      {
        const checkList = _vm.searchResults.raws.filterOption.optionList.filter(item=>item.checked);
        const categories = checkList.map(item=>{
          return `${item.grpCode}:${item.code}`;
        });

        if(categories.length > 0){
          param["categories"] = [...categories];
        }
      }

      const reSearchText = ("").trim();
      if(reSearchText.length > 0){
        param["reSearchText"] = reSearchText;
      }

      return param;
    },

    //검색 코칭 TV 로드
    async loadICochingTvList(){
      const _vm = this;

      _vm.loading(true);
      try{
        _vm.searchResults.tv.list.splice(0, _vm.searchResults.tv.list.length);
        const params = _vm.getCochingTvSearchParam();

        const tvRes = await getSearchTvList(params);
        const {list, maxScore, total, version} = tvRes.resultData;
        //console.debug(maxScore);

        const dataList = _vm.convertSearchResultByVersion(version, list).map(item=>{
          //임시로 조회수 0인것 렌덤처리
          return {
            ...item,
            // views: 3 + Math.floor(Math.random() * 40)
            views: '-',
            ytDttm: '-',
          }
        });
        

        //Note 메인화면만 배열 셔플 (Fisher-Yates 방식)
        for (let i = dataList.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [dataList[i], dataList[j]] = [dataList[j], dataList[i]];
        }

        _vm.searchResults.tv.list.push(...dataList.slice(0, DEF_SEARCH_TV_ROWS_PER_PAGE));
        _vm.searchResults.tv.pi.curPage = 1;
        _vm.searchResults.tv.pi.totalCount = total;
       
      }catch(err){
        console.error(err);
        _vm.defaultApiErrorHandler(err); //기본에러처리
      }finally{
        _vm.loading(false);

        _vm.$emit("onLoaded", _vm.searchResults.tv.pi.totalCount);
      }
    },

    //검색 코칭 TV 파라미터 리턴
    getCochingTvSearchParam(){
      const _vm = this;

      const param = {
        text : ("").trim().replace(/#/g, "")
        , page : 1
        , rowSize : DEF_SEARCH_TV_ROWS_PER_PAGE * 20
        , hintField: _vm.hintField
      };

      return param;
    },

    //코칭 TV 정보 Youtube API 결과로 셋팅
    async setYoutubeInfos(tvList){
      const _vm = this;
      const list = Array.isArray(tvList)
        ? tvList
        : Object.values(tvList || {});

      if (list.length === 0) return;

      try {
        await Promise.all(list.map(v => _vm.setYoutubeInfo(v)));
      } catch (err) {
        console.warn('Youtube API 가져오기 오류:', err);
      }
    },

    async setYoutubeInfo(tvInfo){
      const params = {
        ytUrl : tvInfo.ytUrl,
      };
      const youtubeInfoRes = await getYoutubeInfo(params);
      const youtubeInfo = youtubeInfoRes.resultData;

      if(youtubeInfo) {
        tvInfo.views = youtubeInfo.viewCount;
        tvInfo.ytDttm = youtubeInfo.publishedAt;
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

    //배너목록
    async loadBanner(showError){
      const _vm = this;

      let isError = false;
      const bannerData = _vm.bannerData;

      bannerData.isWaveLoading = true;
      try{
        await Promise.delay(_vm.delayTime);

        { //데이터 초기화
          bannerData.slideList.splice(0, bannerData.slideList.length);                    
        }

        //데이터 로드
        const params = {
          bannerMstCd : "BANNER_0001"
        };

        const res = await getBannerList(params);
        //console.debug(res);

        const { resultCode, resultFailMessage, resultData } = res;
        if(resultCode != "0000"){
          if(showError){
            await _vm.$refs["alertModal"].open({
              title: _vm.$t('mypage.main.label.001') || '오류',
              content : resultFailMessage || _vm.$t('mypage.main.label.002') || '알수 없는 오류가 발생했습니다.'
            });
          }
          return;
        } 

        //렌더링 데이터 설정
        bannerData.resData = resultData;
        bannerData.slideList.splice(0, bannerData.slideList.length, ...bannerData.resData.list);
        //console.debug(_vm.$refs["mainVisualSwiper"]);

        // _vm.$nextTick(function() { 
        //   this.$refs["mainVisualSwiper"].swiper.init();
        // }); 
        

      } catch (err) {
				console.error(err);
        isError = true;

        if(showError){
          await _vm.$refs["alertModal"].open({
            title: _vm.$t('mypage.main.label.001') || '오류',
            content : resultFailMessage || _vm.$t('mypage.main.label.002') || '알수 없는 오류가 발생했습니다.'
          });
        }

			} finally {
        bannerData.isWaveLoading = false;
			}
      
      return isError;
    },

    //배너 No Image
    bannerNoImage(e) {
      e.target.src = DEF_bannerImage;
    },

    //팝업목록
    async loadPopup(showError){
      const _vm = this;

      let isError = false;
      const popupData = _vm.popupData;
      try{
        await Promise.delay(_vm.delayTime);

        { //데이터 초기화
          popupData.slideList.splice(0, popupData.slideList.length);                    
        }

        // 오늘 하루 보지 않기
        if("1" == _vm.getPopupTodayDoNotShow('POPUP_0001')){          
          return;
        }

        //데이터 로드
        const params = {
          popupMstCd : "POPUP_0001"
        };

        const res = await getPopupList(params);
        //console.debug(res);

        const { resultCode, resultFailMessage, resultData } = res;
        if(resultCode != "0000"){
          if(showError){
            await _vm.$refs["alertModal"].open({
              title: _vm.$t('mypage.main.label.001') || '오류',
              content : resultFailMessage || _vm.$t('mypage.main.label.002') || '알수 없는 오류가 발생했습니다.'
            });
          }
          return;
        } 

        //렌더링 데이터 설정
        popupData.resData = resultData;
        popupData.slideList.splice(0, popupData.slideList.length, ...popupData.resData.list);   

        // _vm.$nextTick(function() { 
        //   this.$refs["mainPopupSwiper"].swiper.init();
        // });
        
      } catch (err) {
				console.error(err);
        isError = true;

        if(showError){
          await _vm.$refs["alertModal"].open({
            title: _vm.$t('mypage.main.label.001') || '오류',
            content : resultFailMessage || _vm.$t('mypage.main.label.002') || '알수 없는 오류가 발생했습니다.'
          });
        }

			} finally {
        
			}
      
      return isError;
    },

    //팝업 No Image
    popupNoImage(e) {
      e.target.src = DEF_popupImage;
    },

    //데이터 가져오는 부분(사용하지 필요없어도 반드시 선언)
    async fetchData(){
      const _vm = this;

      _vm.isLoggedIn = (isUserLoggedIn() || null) ? true : false;
      
      _vm.loading(true);
      try{
        await _vm.loadDataAll();

        //코칭 TV 정보 Youtube API 결과로 셋팅
        await _vm.setYoutubeInfos(_vm.searchResults.tv.list);
      }catch(err){
        console.error(err);
      }finally{
        _vm.loading(false);
      }
    },

    debounce(func, wait) {
      let timeout;
      return function (...args) {
        const context = this;
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(context, args), wait);
      };
    },

    docReady(){
      const _vm = this;      

      // AOS.init({
      //   // easing: 'ease-out-back',
      //   duration: 1000,
      //   // offset: 120,
      //   // delay: 0,
      //   // duration: 400,
      //   // easing: 'ease',
      //   // once: true,
      //   // mirror: false,
      //   // anchorPlacement: 'top-bottom'
      // });

      $(_vm.$refs["mainPopupModal"]).find(".bottom-modal-close").on("click", function(){
        $(_vm.$refs["mainPopupModal"]).on("click").fadeOut(300);
        $("body").css("overflow", "visible");
      });
      
    },
  }  
}
</script>

<style lang="scss" scoped>


</style>

<style lang="scss">
#coching-main {

}
</style>
