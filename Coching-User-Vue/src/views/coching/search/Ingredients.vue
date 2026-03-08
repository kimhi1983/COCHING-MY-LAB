<template>
  <!--section은 3가지 타입 full, main-->  
  <section class="full" :class="{'active': isSmallGnb}">
    <div class="container h-100">
      <!--content-->
      <div class="content">
        <!--header-->
        <div class="header">
          <div class="h1">{{ titleName }}</div>
        </div>

        <!--main-content-->
        <div class="main-content">
          <!--ingredient-view-->
          <div class="ingredient-view">
            <!--link-box-->
            <!--전성분 리스트 -->
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
                >{{fmtRawElementName(item.korean)}}</button>                    
            </div>
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
              <!--card-wrap-->
              <!--main card 홀수 이기 때문에 21개까지 데이터 가져와야함-->
              <div v-show="searchResults.raws.pi.totalCount > 0"
                class="card-wrap main-card">
                <RawCard
                  v-for="(item, idx) of searchResults.raws.list"
                  :key="`${idx}_${item.rawSeq}`"
                  :rawInfo="item"                        
                  @onClickCard="onClickRawCard(item)"                          
                  @onClickHashTag="(hashtag)=>{goSearchKeywordV2(`${hashtag}`, {hintField : '1001', emo : true}, 'coching-search-main')}"
                />
              </div>

              <div class="empty-content "
                v-show="searchResults.raws.pi.totalCount <= 0">
                검색된 원료가 없습니다.
              </div>

              <!-- searchResults.raws.pi.totalCount:{{ searchResults.raws.pi.totalCount }}<br/> -->
              <!-- searchResults.raws.list:{{ searchResults.raws.list }}<br/>  -->
              <div v-show="searchResults.raws.pi.totalCount > 0"
                class="btn-area">
                <button
                  @click="onClickGotoRaw(ingd.focusIngd)"
                  type="button" class="btn btn-sm btn-gray">사용원료 바로가기</button>
              </div>
            </div>

            <!-- coIngdInfo:{{ingd.focusIngd.ingdDetail}} -->
            <!-- productId:{{ productId }}<br/>
            rawSeq:{{ rawSeq }}<br/>
            ingdId:{{ ingdId }}<br/> -->
          </div>
        </div>
      </div>

      <!-- ingd.focusIngd :{{ ingd.focusIngd }}  -->

      <RawInfoModal ref="rawInfoModal"
        @onClickRequest="onClickRequest"
      />
      <RequestRawModal
        v-if="isLoggedIn"
        ref="requestRawModal"></RequestRawModal>

      <!--오류-->
      <AlertModal ref="alertModal"></AlertModal>
      <ConfirmModal ref="confirmModal"></ConfirmModal>
    </div>

  </section>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import { DEF_INGD_DETAIL } from '@/constants/data';

import { getCodes } from '@/api/coching/comm/code';
import { getSearchIngredient, getSearchProd, getSearchRaw, getSearchRawList, getIngredientNationLimitList, getIngredientNationExpLimitList } from '@/api/coching/search/search';
import { isUserLoggedIn, getUserData, getHomeRouteForLoggedInUser } from '@/auth/utils';

import Advertise from '@/components/Advertise.vue';
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

const DEF_SEARCH_RAW_ROWS_PER_PAGE = 12;

export default {
	name: 'coching-ingredients',
	mixins: [ernsUtils],
	components: {
    Advertise,
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
    }
  },
	watch: {
    '$route': 'fetchData' //라우트가 변경되면 메소드를 다시 호출됩니다.  
	},
	props: {
    productId:{
      type: [String, Number], // 전달받는 데이터 타입
      default : 0,      
    },
    rawSeq:{
      type: [String, Number], // 전달받는 데이터 타입
      default : 0,      
    },
    ingdId:{
      type: [String, Number], // 전달받는 데이터 타입
      default : 0,      
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

      searchResults:{
        
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
      
      _vm.ermPushPage({
        name:'coching-search-main', 
        query : {
          keyword : (ingdInfo.korean || '').trim().replace(/,/g, "\\,"),
          initViewType : "raws",
          emo : true
        }
      });
    },

    //제품 클릭이벤트
    onClickGotoProduct(ingdInfo){
      const _vm = this;
      
      _vm.ermPushPage({
        name:'coching-search-main', 
        query : {
          keyword : (ingdInfo.korean || '').trim().replace(/,/g, "\\,"),
          initViewType : "prod",
          emo : true
        }
      });
    },

    //원료카드 클릭이벤트
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

    //전성분 로드
    async loadIngredientsList(){
      const _vm = this;

      _vm.loading(true);
      try{
        _vm.ingd.focusIngd = {};
        _vm.ingd.ingdList.splice(0, _vm.ingd.ingdList.length);

        console.debug(`isProduct : ${_vm.isProduct}`);

        if(_vm.isProduct){
          const params = {
            id : (""+(_vm.productId || "")).trim()
          };

          const prodRes = await getSearchProd(params);
          const {list, maxScore, total, version} = prodRes.resultData;
          if((total || 0) < 1){
            await _vm.$refs["alertModal"].open({
              title: _vm.$tt('test001') || '제품정보를 조회 할 수 없습니다.'
            });
            return;
          }
          const dataList = _vm.convertSearchResultByVersion(version, list);
          _vm.ingd.ingdList = dataList[0].ingredients;          
          _vm.ingd.ingdList = _vm.ingd.ingdList.filter(ingd=>ingd.id);
          _vm.titleName = dataList[0].product.name;
          
        }else{
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
          const rawIngList = dataList[0].rawElemInfo;
          _vm.ingd.ingdList = rawIngList.map(item=>{
            return {
              ...item,
              korean: item.repNameKo,
            };
          });
          _vm.ingd.ingdList = _vm.ingd.ingdList.filter(ingd=>ingd.id);
          _vm.titleName = dataList[0].rawName;
        }

        const fIngd = _vm.ingd.ingdList.find(ingd=> (""+ingd.id) == (""+_vm.ingdId));
        if(fIngd){
          _vm.ingd.focusIngd = {...fIngd, ingdDetail : {...DEF_INGD_DETAIL}};
        }else{
          _vm.ingd.focusIngd = {..._vm.ingd.ingdList[0], ingdDetail : {...DEF_INGD_DETAIL}};
        }
        await _vm.loadIngredient(_vm.ingd.focusIngd.id);

      }catch(err){
        console.error(err);
        _vm.defaultApiErrorHandler(err); //기본에러처리
      }finally{
        _vm.loading(false);
      }
    },

    //성분 로드
    async loadIngredient(pIngdId){
      const _vm = this;

      //성분 캐시처리
      const fIngd = _vm.ingd.ingdList.find(ingd=> (""+ingd.id) == (""+pIngdId));
      if(fIngd.ingdDetail && fIngd.ingdDetail.id){
        console.debug(`already loaded ${fIngd.ingdDetail.id}`);
        _vm.ingd.focusIngd.ingdDetail = fIngd.ingdDetail;

        await _vm.loadSearchRawResult();
        return;
      }

      _vm.loading(true);
      try{
        if(_vm.isProduct){
          const params = {
            idType : 'H'
            , id : (""+(pIngdId || "")).trim()
          };

          const ingdDetailRes = await getSearchIngredient(params);

          const {list, maxScore, total, version} = ingdDetailRes.resultData;
          const dataList = _vm.convertSearchResultByVersion(version, list);         
          const convertedData = _vm.convertKeysToCamelCase(dataList[0]);
          //console.debug(convertedData);

          const limitParams = {
            ingdId : convertedData.kcaiId
          };
          const ingdNationLimitRes = await getIngredientNationLimitList(limitParams);
          const ingdNationExpLimitRes = await getIngredientNationExpLimitList(limitParams);

          if(fIngd){
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
            _vm.ingd.focusIngd.ingdDetail = fIngd.ingdDetail = dataIngd;
          }

        }else{
          const params = {
            idType : 'K'
            , id : (""+(pIngdId || "")).trim()
          };

          const ingdDetailRes = await getSearchIngredient(params);
          
          const {list, maxScore, total, version} = ingdDetailRes.resultData;
          const dataList = _vm.convertSearchResultByVersion(version, list);
          const convertedData = _vm.convertKeysToCamelCase(dataList[0]);

          const limitParams = {
            ingdId : convertedData.kcaiId
          };
          const ingdNationLimitRes = await getIngredientNationLimitList(limitParams);
          const ingdNationExpLimitRes = await getIngredientNationExpLimitList(limitParams);
         
          if(fIngd){            
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
            _vm.ingd.focusIngd.ingdDetail = fIngd.ingdDetail = dataIngd;
          }
        }

      }catch(err){
        console.error(err);
        _vm.defaultApiErrorHandler(err); //기본에러처리
      }finally{

        await _vm.loadSearchRawResult();
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

    //성분데이터 정리 (미사용-구버전)
    adjustingdDetail(rawData){
      const _vm = this;

      const kdata = {...rawData.kdata};
      const edata = {...rawData.edata};
      const hdata = {...rawData.hdata};

      return {
        ...DEF_INGD_DETAIL

        //기본정보
        , id : rawData.id
        , repName : rawData.repName
        , inci : _vm.displayArrayVal([kdata.english, kdata.english_old, ...kdata.english_keywords
            //, ...hdata.english.split(", ")
          ])
        , korean : _vm.displayArrayVal([kdata.korean, kdata.korean_old, ...kdata.korean_keywords
            , ...(hdata.korean || '').split(", ")])
        , english : _vm.displayArrayVal([kdata.english, kdata.english_old, ...kdata.english_keywords
            , ...(hdata.english || '').split(", ")
          ])
        , formula : _vm.displayArrayVal([kdata.msd_formula])
        , purpose : _vm.displayArrayVal([...(kdata.mixing_purpose || '').split(", "), ...(hdata.purpose || '').split(", ")])
        , casNo : _vm.displayArrayVal(kdata.casNo_keywords)
        , ecNo : _vm.displayArrayVal(kdata.ecNo_keywords)
        , expUrl : _vm.replaceDimensions(edata.chemical_img_url || '', 420, 300)
        , expUrl2 : kdata.ms_exp_url || ''

        //EWG
        , scoreMin : edata.score_min
        , score : edata.score
        , skinDeepDataLevel : (edata.score_data_level || '').toLowerCase()
        , dependsOnUsage : (edata.score && edata.score_min) ? edata.score_min == edata.score : null
        , ewgUrl : edata.rawcno_id ? `https://www.ewg.org/skindeep/ingredients/${edata.rawcno_id}` : null
      }
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
            console.debug('raws', _vm.searchResults.raws.pi.totalCount);
          }
        }
      ]

      //여러 request 실행
      const results = await Promise.all(
        tasks.map(task => task.func(...task.params))
      );

      // 후처리 로직 실행
      await Promise.all(
        tasks.map((task, index) => task.processResult(results[index]))
      );
    },

    //원료검색 파라미터 리턴
    getRawSearchParam(){
      const _vm = this;

      const ingdIds = _vm.isProduct ?  (`${_vm.ingd.focusIngd.ingdDetail?.kcaiId || '0'}`).trim() : (`${_vm.ingd.focusIngd.id}`).trim();

      const param = {
        text : ("").trim() //메인은 검색키워드 없음
        , ingdIds : [ingdIds]
        , page : 1
        , rowSize : DEF_SEARCH_RAW_ROWS_PER_PAGE
      };

      const reSearchText = ("").trim();
      if(reSearchText.length > 0){
        param["reSearchText"] = reSearchText;
      }

      return param;
    },

		//데이터 가져오는 부분(사용하지 필요없어도 반드시 선언)
		async fetchData() {
			const _vm = this;

			_vm.loading(true);
			try {
        _vm.isLoggedIn = (isUserLoggedIn() || null) ? true : false;

        await _vm.loadIngredientsList();
       
			} catch (err) {
				_vm.defaultApiErrorHandler(err); //기본에러처리
			} finally {
				_vm.loading(false);
			}
		},

    async loadCodes() {
        const _vm = this;

        const codeRes = await getCodes({ grpCode: "CH017", etc5: _vm.$i18n.locale });
        const { resultCode, resultFailMessage, resultData } = codeRes;

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
</style>
