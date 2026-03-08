<template>
  <!--main-content-->
  <div id="component-search-result-tv" class="main-content">
    <!--search-content-->
    <div class="search-content">
      <!--search-header-->
      <div class="search-header">
        <!--total-wrap-->
        <div class="total-wrap">
          <div class="total-num">총
            <span>{{(searchResults.tv.pi.totalCount) | eFmtNum}}</span>
          </div>
          <div class="right">
            <!--select-->
            <!-- <select class="custom-select" placeholder="전체">
              <option value="">전체</option>
              <option value="">조회수</option>
              <option value="">등록순</option>
            </select> -->
            <!--search-->
            <div class="input-set re-search">
              <div class="input-ic-set">
                <input type="text" placeholder="결과 내 재검색" 
                  v-model="search.tv.reSearchText"
                  @keyup.enter="loadSearchResult"
                />
                <button type="button"
                  class="input-ic ic-md ic-search-md"
                  @click="loadSearchResult"
                ></button>
              </div>
            </div>
          </div>
        </div>
      </div>
    
      <!--img-empty-wrap-->
      <div v-show="searchResults.tv.pi.totalCount <= 0"
        class="img-empty-wrap">
        <div class="img">
          <img src="@/assets/images/img-empty.svg" alt="검색결과 없음" />
        </div>
        <div class="text-wrap">
          <div class="title">검색 결과가 없습니다.</div>
          <div class="info">다른 검색어를 입력하시거나 철자와 띄어쓰기를 확인해 보세요.</div>
        </div>
        <div v-if="recommendKeywords.length > 0"
          class="tag-badge">
          <a v-for="(item, idx) of recommendKeywords" 
            :key="idx"
            @click="goSearchKeyword(item.name)"
            href="javascript:;" class="item">{{ item.name}}</a>  
        </div>
      </div>

      <!--card-wrap-->
      <div v-show="searchResults.tv.pi.totalCount > 0"
        class="card-wrap tv-card">
        <!--item-->
        <CochingTvCard v-for="(item, idx) of searchResults.tv.list" 
          :key="idx"
          :tvInfo="item"
          :useThumbnail="true"
          @onClickCard="onClickCochingTvCard">
        </CochingTvCard>
      </div>

      <!--더보기-->
      <div class="card-more"
        v-show="searchResults.tv.pi.totalCount > searchResults.tv.list.length"
      >
        <button @click="loadSearchResultTvMore"
          type="button" class="btn btn-sm btn-gray">코칭TV 더보기</button>
      </div>

      <!-- 상품쪽과 동일 처리 때문에 강제 padding-->
      <div style="margin-bottom: 3rem;"></div>
    </div>

    <!-- searchResults.tv.list : {{ searchResults.tv.list }} -->

    <!--오류-->
    <AlertModal ref="alertModal"></AlertModal>
    <ConfirmModal ref="confirmModal"></ConfirmModal>

  </div>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';

import { getCodes } from '@/api/coching/comm/code';
import { getSearchTv, getSearchTvList, getRecommendKeywords } from '@/api/coching/search/search';
import { isUserLoggedIn, getUserData, getHomeRouteForLoggedInUser } from '@/auth/utils';
import { getYoutubeInfo } from '@/api/coching/comm/cochingtv';

import CochingTvCard from '@/components/CochingTvCard.vue'; 


const DEF_SEARCH_ROWS_PER_PAGE = 15;

export default {
  name: 'component-search-result-tv',
  mixins: [ernsUtils],
  components: {
    CochingTvCard,
  },
  computed: {
    // 검색 관련 props들을 하나로 묶어서 감시
    searchParams() {
      return {
        keyword: this.keyword,
        hintField: this.hintField,
        exactMatchOnly: this.exactMatchOnly
      };
    },
  },
  watch: {
    // 여러 props를 하나의 computed property로 감시
    searchParams: {
      handler() {
        this.fetchData();
      },
      deep: true
    }
  },
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
  data() {
    return {
      //로그인
      isLoggedIn : false,

      recommendKeywords : [],

      search :{
        tv : {
          reSearchText : "",
        },
      },

      searchResults:{
        tv:{
          list:[],
          pi:{
            curPage : 1,
            totalCount : 0,
            perPage : DEF_SEARCH_ROWS_PER_PAGE       
          },
        },
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
    //코칭TV 카드 클릭이벤트
    onClickCochingTvCard(tvInfo){
      const _vm = this;
      
      //TODO : 코칭TV 카드 클릭이벤트
    },

    //전체 검색
    async loadSearchResult(){
      const _vm = this;

      _vm.loadICochingTvList();
    },

    //검색 코칭 TV 로드
    async loadICochingTvList(){
      const _vm = this;

      _vm.loading(true);
      try{
        _vm.searchResults.tv.list.splice(0, _vm.searchResults.tv.list.length);
        const params = _vm.getCochingTvSearchParam();

        const ingdRes = await getSearchTvList(params);
        const {list, maxScore, total, version} = ingdRes.resultData;
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
        _vm.searchResults.tv.list.push(...dataList);
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
        text : (_vm.keyword || "").trim().replace(/#/g, "")
        , page : 1
        , rowSize : DEF_SEARCH_ROWS_PER_PAGE
        , hintField: _vm.hintField
        , "sortField" : "sortOrd"
        , "sortOrder" : "asc"
      };

      const reSearchText = (_vm.search.tv.reSearchText || "").trim();
      if(reSearchText.length > 0){
        param["reSearchText"] = reSearchText;
      }

      return param;
    },

    //제품 더보기
    async loadSearchResultTvMore(){
      const _vm = this;

      _vm.loading(true);
      try{
        const pi = _vm.searchResults.tv.pi;        
        const params = _vm.getCochingTvSearchParam();
        params.page = ++pi.curPage;
        
        const prodRes = await getSearchTvList(params);

        const {list, maxScore, total, version} = prodRes.resultData;
        
        const dataList = _vm.convertSearchResultByVersion(version, list).map(item=>{
          return {
            ...item,
            views: '-',
            ytDttm: '-',
          }
        });
        _vm.searchResults.tv.list.push(...dataList);
        
        // 새로 추가된 데이터만 YouTube 정보 설정
        _vm.setYoutubeInfos(dataList);

      }catch(err){
        console.error(err);
        _vm.defaultApiErrorHandler(err); //기본에러처리
      }finally{
        _vm.loading(false);
      }
    },

    replaceDimensions(str, newWidth, newHeight) {
      if (typeof str !== "string") {
        throw new TypeError("Input must be a string");
      }

      // 정규식을 사용하여 width와 height 부분을 대체
      return str
        .replace(/width=\d+/g, `width=${newWidth}`)  // width 교체
        .replace(/height=\d+/g, `height=${newHeight}`); // height 교체
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

    //데이터 가져오는 부분(사용하지 필요없어도 반드시 선언)
    async fetchData() {
      const _vm = this;

      _vm.loading(true);
      try {
        _vm.isLoggedIn = (isUserLoggedIn() || null) ? true : false;

        await _vm.loadICochingTvList();

        //코칭 TV 정보 Youtube API 결과로 셋팅
        await _vm.setYoutubeInfos(_vm.searchResults.tv.list);

        _vm.loadRecommendKeywords();
      
      } catch (err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
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

    docReady() {
      const _vm = this;
    },
  }
}
</script>

<style lang="scss" scoped>
.white-space-nowrap{
  white-space: nowrap;
}

</style>

<style lang="scss">
#component-search-result-tv {

}
</style>
