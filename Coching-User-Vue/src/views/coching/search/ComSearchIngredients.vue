<template>
  <!--main-content-->
  <div id="component-search-result-ingredients" class="main-content">
    <!--img-empty-wrap-->
    <div v-show="searchResults.ingredient.pi.totalCount <= 0"
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
    
    <!--Result : ingredient-view-->
    <div v-show="searchResults.ingredient.pi.totalCount > 0"
      class="ingredient-view">

      <!--link-box-->
      <div class="link-box-wrap sm scroller-x t-scroll-hidden">        
        <!--해당 페이지 btn-gray-outline->btn-bk-outline-->

        <button v-for="(item, idx) of ingd.ingdList" :key="idx"
          type="button"
          class="btn"
          :class="{
            'btn-bk-outline' : (ingd.focusIngd.id == item.id)
            , 'btn-gray-outline' : (ingd.focusIngd.id != item.id)
          }"
          @click="onClickIngredient(item)"
          >{{fmtRawElementName(item.rep_name)}}</button>
      </div>

      <!--ingredient-content-->
      <div class="ingredient-content">
        <!--top-->
        <IngdBasicInfo
          class="top"
          :ingdInfo="ingd.focusIngd"
          :coIngdInfo="ingd.focusIngd.ingdDetail"
          :enabledRawButton="true"
          :enabledProductButton="true"
          @onClickGotoRaw="onClickGotoRaw"
          @onClickGotoProduct="onClickGotoProduct"
          >
        </IngdBasicInfo>

        <!--bottom-->
        <IngdEtcInfo
          class="bottom"
          :ingdInfo="ingd.focusIngd"
          :coIngdInfo="ingd.focusIngd.ingdDetail">
        </IngdEtcInfo>

        <!--사용원료-->
        <div>
          <!--h2-->
          <div class="h2 flex">사용원료</div>
          <!-- searchResults.raws.pi:{{searchResults.raws.pi}} -->

          <!--empty-wrap-->
          <div v-show="searchResults.raws.pi.totalCount <= 0"
            class="empty-wrap">사용원료가 없습니다.</div>

          <!--card-wrap-->
          <!--main card 홀수 이기 때문에 21개까지 데이터 가져와야함-->
          <div v-show="searchResults.raws.pi.totalCount > 0"
            class="card-wrap main-card">
            <!--main-card-->
            <!--item-->
            <RawCard
              v-for="(item, idx) of searchResults.raws.list"
              :key="idx"
              :rawInfo="item"                        
              @onClickCard="onClickRawCard(item)"                          
              @onClickHashTag="(hashtag)=>{goSearchKeywordV2(`${hashtag}`, {hintField : '1001', emo : true}, 'coching-search-main')}"
            />            
          </div>

          <div v-show="searchResults.raws.pi.totalCount > 0"
            class="btn-area">
            <button @click="onClickGotoRaw(ingd.focusIngd)"
              type="button" class="btn btn-sm btn-gray">사용원료 바로가기</button>
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
      ref="requestRawModal"/>
      
  </div>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import { DEF_INGD_DETAIL } from '@/constants/data';

import { getCodes } from '@/api/coching/comm/code';
import { getSearchIngredient, getSearchIngredients
  , getRecommendKeywords, getSearchRawList
  , getIngredientNationLimitList, getIngredientNationExpLimitList} from '@/api/coching/search/search';
import { isUserLoggedIn, getUserData, getHomeRouteForLoggedInUser } from '@/auth/utils';

import IngdBasicInfo from '@/components/ingredient/IngdBasicInfo.vue';
import IngdEtcInfo from '@/components/ingredient/IngdEtcInfo.vue';
import RawCard from '@/components/RawCard.vue';
import RawInfoModal from '@/components/modal/RawInfoModal.vue';
import RequestRawModal from '@/components/modal/RequestRawModal.vue';

const DEF_INGD = {
 ingdList : [],
 focusIngd : {
   id:0,
   ingdDetail : {...DEF_INGD_DETAIL}
 }
}

const DEF_SEARCH_ROWS_PER_PAGE = 30;
const DEF_SEARCH_RAW_ROWS_PER_PAGE = 21;

export default {
  name: 'component-search-result-ingredients',
  mixins: [ernsUtils],
  components: {
    IngdBasicInfo,
    IngdEtcInfo,

    RawCard,
    RawInfoModal,
    RequestRawModal
  },
  computed: {
    isProduct(){
      return this.productId && parseInt(""+this.productId, 10) > 0;
    },
    isRaw(){
      return this.rawSeq && parseInt(""+this.rawSeq, 10) > 0;
    },
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
    },
    exactMatchOnly:{
      type : Boolean,
      default : false
    }
  },
  data() {
    return {
      CODES : {
        nationRegu : [],
        nationExpRegu : []
      },

      //로그인
      isLoggedIn : false,

      ingd : {...DEF_INGD},
      titleName: '',

      recommendKeywords : [],

      searchResults:{
        ingredient:{
          list:[],
          pi:{
            curPage : 1,
            totalCount : 0,
            perPage : DEF_SEARCH_ROWS_PER_PAGE       
          },
        },

        raws:{
          list:[],
          pi:{
            curPage : 1,
            totalCount : 0,
            perPage : DEF_SEARCH_RAW_ROWS_PER_PAGE       
          },
        },
      },
      
      hasBanner: false, // 광고 배너 유무
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
    //성분명 표시
    fmtRawElementName(pRawElem){
      let retVal = pRawElem || "";
      retVal = retVal.split(", ")[0];      
      return retVal;
    },

    //원료 클릭이벤트
    onClickGotoRaw(ingdInfo){
      const _vm = this;

      // _vm.ermPushPage({
      //   name:'coching-search-main', 
      //   query : {
      //     keyword : ingdInfo.rep_name,          
      //     initViewType : "raws",
      //     ingdIds : [`${ingdInfo.id}`]
      //   }
      // });

      // Note: 디자인 변경에 따라 부모에서 처리하도록 변경
      _vm.$emit("onClickGotoRaw", ingdInfo);
    },

    //제품 클릭이벤트
    onClickGotoProduct(ingdInfo){
      const _vm = this;
      
      // _vm.ermPushPage({
      //   name:'coching-search-main', 
      //   query : {
      //     keyword : ingdInfo.rep_name,
      //     initViewType : "prod"
      //   }
      // });

      // Note: 디자인 변경에 따라 부모에서 처리하도록 변경
      _vm.$emit("onClickGotoProduct", ingdInfo);
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

    //성분 클릭
    async onClickIngredient(ingdObj){
      const _vm = this;
      _vm.ingd.focusIngd = {...ingdObj, ingdDetail : {...DEF_INGD_DETAIL}};
      await _vm.loadIngredient(ingdObj.id);
    },    

    //검색 성분 로드
    async loadIngredientsList(){
      const _vm = this;

      _vm.loading(true);
      try{
        _vm.ingd.focusIngd = {};
        _vm.ingd.ingdList.splice(0, _vm.ingd.ingdList.length);
        _vm.searchResults.ingredient.list.splice(0, _vm.searchResults.ingredient.list.length);

        const params = _vm.getIngredientsSearchParam();

        const ingdRes = await getSearchIngredients(params);
        const {list, maxScore, total, version} = ingdRes.resultData;
        //console.debug(maxScore);

        const dataList = _vm.convertSearchResultByVersion(version, list);
        _vm.searchResults.ingredient.list.push(...dataList);
        _vm.searchResults.ingredient.pi.curPage = 1;
        _vm.searchResults.ingredient.pi.totalCount = total;

        _vm.ingd.ingdList = _vm.searchResults.ingredient.list;
        _vm.titleName = _vm.keyword;

        //console.debug(_vm.ingd.ingdList);      

        if(_vm.ingd.ingdList.length > 0){
          const fIngd = _vm.ingd.ingdList.find(ingd=> (""+ingd.id) == (""+_vm.ingdId));
          if(fIngd){
            _vm.ingd.focusIngd = {...fIngd, ingdDetail : {...DEF_INGD_DETAIL}};
          }else{
            _vm.ingd.focusIngd = {..._vm.ingd.ingdList[0], ingdDetail : {...DEF_INGD_DETAIL}};
          }
          await _vm.loadIngredient(_vm.ingd.focusIngd.id);
        }
      }catch(err){
        console.error(err);
        _vm.defaultApiErrorHandler(err); //기본에러처리
      }finally{
        _vm.loading(false);

        _vm.$emit("onLoaded", _vm.searchResults.ingredient.pi.totalCount);
      }
    },

    //성분검색 파라미터 리턴
    getIngredientsSearchParam(){
      const _vm = this;

      const param = {
        text : (_vm.keyword || "").trim()
        , page : 1
        , rowSize : DEF_SEARCH_ROWS_PER_PAGE
        , hintField: _vm.hintField
        , exactMatchOnly: _vm.exactMatchOnly
      };

      return param;
    },

    //성분 로드
    async loadIngredient(pIngdId){
      const _vm = this;

      await _vm.loadSearchRawResult();

      //성분 캐시처리
      const fIngd = _vm.ingd.ingdList.find(ingd=> (""+ingd.id) == (""+pIngdId));
      if(fIngd.ingdDetail && fIngd.ingdDetail.id){
        console.debug(`already loaded ${fIngd.ingdDetail.id}`);
        _vm.ingd.focusIngd.ingdDetail = fIngd.ingdDetail;
        return;
      }
      
      _vm.loading(true);
      try{
        const params = {
          idType : 'K'
          , id : (""+(pIngdId || "")).trim()
        };

        const ingdDetailRes = await getSearchIngredient(params);

        const limitParams = {
          ingdId : params.id
        };

        const ingdNationLimitRes = await getIngredientNationLimitList(limitParams);
        const ingdNationExpLimitRes = await getIngredientNationExpLimitList(limitParams);
        
        if(fIngd){
          const {list, maxScore, total, version} = ingdDetailRes.resultData;
          const dataList = _vm.convertSearchResultByVersion(version, list);
          const convertedData = _vm.convertKeysToCamelCase(dataList[0]);
          
          const mfdsSearchParam = {
            searchValue : convertedData?.inci[0] || convertedData?.repName ||""
            , searchCondition : convertedData?.inci[0] ? 'ingrEngName' : 'ingrKorName'
          };
          const nationLimitsData = _vm.convertIngredientNationLimitData(ingdNationLimitRes.resultData, mfdsSearchParam, _vm.CODES.nationRegu);
          const nationExpLimitsData = _vm.convertIngredientNationExpLimitData(ingdNationExpLimitRes.resultData, mfdsSearchParam, _vm.CODES.nationExpRegu);
          
          const dataIngd = {
            ...convertedData,
            nationLimits : nationLimitsData,
            nationExpLimits : nationExpLimitsData
          };

          console.debug(dataIngd);

          _vm.ingd.focusIngd.ingdDetail = fIngd.ingdDetail = dataIngd;

        }

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

    //사용 원료검색    
    async loadSearchRawResult(){
      const _vm = this;

      const rawParams = _vm.getRawSearchParam();
      const tasks = [
        { //사용 원료
          func: getSearchRawList,
          params: [{
            ...rawParams
            , "sortField" : "viewSumCnt"
            , "sortOrder" : "desc"
          }],
          processResult: rawRes => {            
            //console.debug(rawRes);

            _vm.searchResults.raws.list.splice(0, _vm.searchResults.raws.list.length);

            const {list, maxScore, total, version} = rawRes.resultData;
            const dataList = _vm.convertSearchResultByVersion(version, list);
            _vm.searchResults.raws.list.push(...dataList);
            _vm.searchResults.raws.pi.curPage = 1;
            _vm.searchResults.raws.pi.totalCount = total;
          }
        }
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
        , ingdIds : [`${_vm.ingd.focusIngd.id}`]
        , page : 1
        , rowSize : DEF_SEARCH_RAW_ROWS_PER_PAGE
      };

      const reSearchText = ("").trim();
      if(reSearchText.length > 0){
        param["reSearchText"] = reSearchText;
      }

      return param;
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

        await _vm.loadIngredientsList();

        _vm.loadRecommendKeywords();
      
      } catch (err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },

    async loadCodes() {
        const _vm = this;

        const nationReguRes = await getCodes({ grpCode: "CH017", etc5: _vm.$i18n.locale });
        var { resultCode, resultFailMessage, resultData } = nationReguRes;

        _vm.CODES.nationRegu = resultData.list.map(item => ({
            ...item,
        }));

        const nationExpReguRes = await getCodes({ grpCode: "CH018", etc5: _vm.$i18n.locale });
        _vm.CODES.nationExpRegu = [...nationExpReguRes.resultData.list];
    },

    docReady() {
      const _vm = this;

      $(".scroller-x").scrollbar();

      // 마우스 휠 이벤트로 가로 스크롤 동작 추가
      $(".scroller-x").on("mousewheel DOMMouseScroll", function (e) {
        var delta = e.originalEvent.wheelDelta || -e.originalEvent.detail;
        var scrollAmount = 30; // 스크롤 이동 거리 설정

        if (delta > 0) {
          // 마우스 휠을 위로 스크롤할 때 (왼쪽으로 이동)
          $(this).scrollLeft($(this).scrollLeft() - scrollAmount);
        } else {
          // 마우스 휠을 아래로 스크롤할 때 (오른쪽으로 이동)
          $(this).scrollLeft($(this).scrollLeft() + scrollAmount);
        }

        // 기본 동작을 막아 세로 스크롤이 작동하지 않게 함
        e.preventDefault();
      });
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
#component-search-result-ingredients {

}
</style>
