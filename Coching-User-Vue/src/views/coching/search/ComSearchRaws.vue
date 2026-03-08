<template>
  <!--main-content-->
  <div id="component-search-result-raws" class="main-content">
    <!--filter-content-->
    <div class="filter-content">
      <!--search-content-->
      <div class="search-content">
        <!--search-header-->
        <div class="search-header">
          <!--total-wrap-->
          <div class="total-wrap">
            <div class="total-num">총
              <span>{{(searchResults.raws.pi.totalCount) | eFmtNum}}</span>
            </div>
            <div class="right">
              <!--search-->
              <div class="input-set re-search m-none">
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
              <!--태블릿 이하 filter-->
              <div @click="onClickOpenMobileFilter"
                class="m-filter pc-none modal-open-full filter-modal">
                <div class="ic-md ic-filter-md"></div>
                필터
                <div class="filter-badge"
                  v-show="hasRawFilterOption()"
                  >
                  {{ (
                    searchResults.raws.filterOption.optionList.filter(item=>item.checked).length 
                    + (searchResults.raws.filterOption.singleRaw ? 1 : 0)
                    + (searchResults.raws.filterOption.multipleRaw ? 1 : 0)
                  )
                  }}
                </div>
              </div>
            </div>
          </div>
          <!--filter-box-->
          <!--디폴트는 노출안함, 필터 선택 하나라도 있으면 노출하고, 하나라도 없으면 삭제-->
          <!-- filterOption:{{ searchResults.raws.filterOption }} -->
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
        </div>
        
        <!--empty-wrap-->
        <div v-show="searchResults.raws.pi.totalCount <= 0"
          class="empty-wrap">검색결과가 없습니다.</div>
        <!--card-wrap-->
        <div v-show="searchResults.raws.pi.totalCount > 0"
          class="card-wrap base-card">
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
            type="button" class="btn btn-sm btn-gray">원료 더보기</button>
        </div>

        <!-- 상품쪽과 동일 처리 때문에 강제 padding-->
        <div style="margin-bottom: 3rem;"></div>
      </div>
      
      <!-- pc 필터  -->
      <!--filter-wrap-->
      <RawSearchFilter
        @update="onChangeRawFilter"
        :singleRaw="searchResults.raws.filterOption.singleRaw"
        :multipleRaw="searchResults.raws.filterOption.multipleRaw"
        ref="rawSearchFilter"
      ></RawSearchFilter>
    </div>

    <!--오류-->
    <AlertModal ref="alertModal"></AlertModal>
    <ConfirmModal ref="confirmModal"></ConfirmModal>

    <!--원료정보-->
    <RawInfoModal ref="rawInfoModal"
      @onClickRequest="onClickRequest"
    />

    <!--원료요청-->
    <RequestRawModal
      v-if="isLoggedIn"
      ref="requestRawModal"></RequestRawModal>

    <!--모바일 원료필터 모달-->
    <MobileRawFilterModal ref="mobileRawFilterModal"/>
  </div>
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';

import RawCard from '@/components/RawCard.vue';

import { getCodes } from '@/api/coching/comm/code';
import { getSearchRawList, getRecommendKeywords } from '@/api/coching/search/search';
import { isUserLoggedIn, getUserData, getHomeRouteForLoggedInUser } from '@/auth/utils';

import RawSearchFilter from '@/components/RawSearchFilter.vue';
import RawInfoModal from '@/components/modal/RawInfoModal.vue';
import MobileRawFilterModal from '@/components/modal/MobileRawFilterModal.vue';
import RequestRawModal from '@/components/modal/RequestRawModal.vue';

const DEF_SEARCH_ROWS_PER_PAGE = 20;

export default {
	name: 'component-search-result-raws',
	mixins: [ernsUtils],
	components: {
    RawCard,
    RawSearchFilter,
    MobileRawFilterModal,
    RawInfoModal,
    RequestRawModal,
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
    },
    isRawSearch:{
      type : Boolean,
      default : false
    }
  },
	watch: {
    // 여러 props를 하나의 computed property로 감시
    searchParams: {
      handler() {
        this.fetchData();
      },
      deep: true
    },
    isRawSearch(newIsRawSearch){
      this.onChangeIsRawSearch(newIsRawSearch);
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
            multipleRaw : false
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

      // Note : 원료사, 원료사 국가 필터링 예제
      //rawAddParams.ptnSeq = 12;
      //rawAddParams.ptnNation = "002";

      tasks.push(
        {
          func: getSearchRawList,
          params: [{
            ...rawParams,
            ...rawAddParams,
          }],
          processResult: rawRes => {
            //console.debug(rawRes);
            _vm.searchResults.raws.list.splice(0, _vm.searchResults.raws.list.length);
            
            const {list, maxScore, total, version} = rawRes.resultData;
            const dataList = _vm.convertSearchResultByVersion(version, list);
            _vm.searchResults.raws.list.push(...dataList);
            _vm.searchResults.raws.pi.curPage = 1;
            _vm.searchResults.raws.pi.totalCount = total;

            _vm.$emit("onLoaded", _vm.searchResults.raws.pi.totalCount);
          }
        }
      );

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
        , exactMatchOnly: _vm.exactMatchOnly
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

    async onChangeIsRawSearch(){
      const _vm = this;
      //console.log("isRawSearch", _vm.isRawSearch);  
      _vm.searchResults.raws.filterOption ={
        ..._vm.searchResults.raws.filterOption,
        singleRaw : true,
        multipleRaw : _vm.isRawSearch ? true : false
      };

      _vm.onClickRemoveRawFilterOption();
        await _vm.loadSearchResult();
    },

    async fetchData(){
      const _vm = this;

      try{
        _vm.isLoggedIn = (isUserLoggedIn() || null) ? true : false;
        await _vm.onChangeIsRawSearch();
        
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
#component-search-result-raws {
  
}
</style>
