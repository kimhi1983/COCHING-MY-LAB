<template>
  <!--main-content-->
  <div id="component-search-result-products" class="main-content">
    <!--filter-content-->
    <div class="filter-content">
      <!--search-content-->
      <div class="search-content">
        <!--search-header-->
        <div class="search-header">
          <!--total-wrap-->
          <div class="total-wrap">
            <div class="total-num">총
              <span>{{searchResults.product.pi.totalCount | eFmtNum}}</span>
            </div>
            <div class="right">
              <!--search-->
              <div class="input-set re-search m-none">
                <div class="input-ic-set">
                  <input type="text" placeholder="결과 내 재검색"
                    v-model="search.product.reSearchText"
                    @keyup.enter="loadSearchResult"
                  />
                  <button type="button" 
                    class="input-ic ic-md ic-search-md"
                    @click="loadSearchResult"
                  ></button>
                </div>
              </div>
              <!--태블릿 이하 filter-->
              <div @click="onClickOpenMobileFilter"
                class="m-filter pc-none modal-open-full filter-modal">
                <div class="ic-md ic-filter-md"></div>
                필터
                <div v-show="hasProdFilterOption()"
                  class="filter-badge">
                  {{ searchResults.product.filterOption.cateList.filter(item=>item.checked).length }}
                </div>
              </div>
            </div>
          </div>
          <!--filter-box-->
          <!--디폴트는 노출안함, 필터 선택 하나라도 있으면 노출하고, 하나라도 없으면 삭제-->
          <!-- filterOption:{{ searchResults.product.filterOption }} -->
          <div v-show="hasProdFilterOption()"
            class="filter-box m-none">
            <div v-for="(item, idx) of searchResults.product.filterOption.cateList.filter(item=>item.checked)"
              :key="idx" class="item">{{item.name}}
              <button type="button" class="ic-xsm ic-close-xsm"
                @click="onClickRemoveProdFilterOption(item)"
              ></button>
            </div>   
          </div>
        </div>
        
        <!--empty-wrap-->
        <div v-show="searchResults.product.pi.totalCount <= 0"
          class="empty-wrap">검색결과가 없습니다.</div>

        <!--card-wrap-->
        <div v-show="searchResults.product.pi.totalCount > 0"          
          class="card-wrap pr-card">
          <ProdCard v-for="(item, idx) of searchResults.product.list" 
            :key="idx"
            :prodInfo="item"
            @onClickCard="onClickProdCard"
            @onClickCompare="onClickProdCompare">
          </ProdCard>
        </div>
        <!--더보기-->
        <div class="card-more"
          v-show="searchResults.product.pi.totalCount > searchResults.product.list.length"
        >
          <button @click="loadSearchResultProdMore(0)"
            type="button" class="btn btn-sm btn-gray">제품 더보기</button>
        </div>

        <!-- 비교하기 때문에 강제 padding-->
        <div style="margin-bottom: 3rem;"></div>
      </div>
      
      <!-- pc 필터  -->
      <!--filter-wrap-->
      <ProdSearchFilter
        @update="onChangeProdFilter"
        ref="prodSearchFilter"
      ></ProdSearchFilter>
    </div>

    <!--오류-->
    <AlertModal ref="alertModal"></AlertModal>
    <ConfirmModal ref="confirmModal"></ConfirmModal>

    <!--제품 정보보-->
    <ProductInfoModal ref="prodInfoModal"/>

    <!--제품비교 선택 -->
    <ProductCompareBottomSheet 
      ref="productCompareBottomSheet"
      @onClickDoCompare="onClickDoCompare"
    />

    <!--제품비교 모달-->
    <ProductCompareModal 
      ref="productCompareModal"
    />

    <!--모바일 제품필터 모달-->
    <MobileProdFilterModal ref="mobileProdFilterModal"/>
  </div>
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';

import { getCodes } from '@/api/coching/comm/code';
import { getSearchProdList } from '@/api/coching/search/search';
import { isUserLoggedIn, getUserData, getHomeRouteForLoggedInUser } from '@/auth/utils';

import ProdCard from '@/components/ProdCard.vue';
import ProdSearchFilter from '@/components/ProdSearchFilter.vue';
import ProductInfoModal from '@/components/modal/ProductInfoModal.vue';
import MobileProdFilterModal from '@/components/modal/MobileProdFilterModal.vue';
import ProductCompareBottomSheet from '@/views/coching/search/ProductCompareBottomSheet.vue';
import ProductCompareModal from '@/components/modal/ProductCompareModal.vue';

const DEF_SEARCH_ROWS_PER_PAGE = 20;

export default {
	name: 'component-search-result-products',
	mixins: [ernsUtils],
	components: {
    ProdCard,
    ProdSearchFilter,
    MobileProdFilterModal,
    ProductInfoModal,
    ProductCompareBottomSheet,
    ProductCompareModal,
  },
	computed: { 
    compareProdList(){
      return this.$store.state.coching.compareProdList || [];
    },
    // 검색 관련 props들을 하나로 묶어서 감시
    searchParams() {
      return {
        keyword: this.keyword,
        hintField: this.hintField,
        exactMatchOnly: this.exactMatchOnly
      };
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
	watch: {
    // 방법 1: 여러 props를 하나의 computed property로 감시
    searchParams: {
      handler() {
        this.fetchData();
      },
      deep: true
    }
	},
	data() {
		return {
      //로그인
      isLoggedIn : false,

      search :{
        product : {
          reSearchText : "",
        },
      },

      recommendKeywords : [],

      compareItems : [],

      searchResults:{
        product:{
          filterOption : {
            cateList:[],            
          },
          list : [],

          //표시영역 구분안함
          list:[],  
          pi:{
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
    //상품 클릭이벤트
    async onClickProdCard(prodInfo){
      const _vm = this;

      await _vm.$refs.prodInfoModal.open({
        prodInfo : prodInfo
      });
    },

    //상품 비교하기 클릭 이벤트
    async onClickProdCompare(prodInfo){
      const _vm = this;

      _vm.$refs.productCompareBottomSheet.setProdInfo(prodInfo);
    },

    //비교하기
    async onClickDoCompare(){
      const _vm = this;

      await _vm.$refs.productCompareModal.open({
        prodInfo1 : _vm.compareProdList[0],
        prodInfo2 : _vm.compareProdList[1]
      });
    },

    //모바일 필터 클릭
    async onClickOpenMobileFilter(){
      const _vm = this;

      const prodSearchFilter = _vm.$refs.prodSearchFilter;
      _vm.searchResults.product.filterOption = {...prodSearchFilter.getValue()};

      const ret = await _vm.$refs.mobileProdFilterModal.open({
        initFilterOption : _vm.searchResults.product.filterOption
        , initReSearchText : _vm.search.reSearchText
      });

      //console.debug(ret);
      if(!ret){
        return;
      }

      const {reSearchText, cateList} = ret;
      _vm.search.reSearchText = reSearchText;

      cateList.forEach(code =>{
        prodSearchFilter.setOption(code, code.checked || false);       
      });

      const filterValue = prodSearchFilter.getValue();
      await _vm.onChangeProdFilter(filterValue);
    },

    //제품 필터 변경시
    async onChangeProdFilter(filterOption){
      const _vm = this;

      _vm.searchResults.product.filterOption = filterOption;

      await _vm.loadIniternalSearchProdResult();
    },

    //제품 필터 제거
    async onClickRemoveProdFilterOption(pItem){
      const _vm = this;

      const prodSearchFilter = _vm.$refs.prodSearchFilter;

      if(typeof pItem === 'object'){
        prodSearchFilter.setOption(pItem, false);       
      }

      if(pItem){
        const filterValue = prodSearchFilter.getValue();
        await _vm.onChangeProdFilter(filterValue);
      }
    },

    //제품필터 체크 항목 유무
    hasProdFilterOption(){
      const _vm = this;

      const fOpt = _vm.searchResults.product.filterOption;
      
      if(fOpt.cateList && fOpt.cateList.filter(item=>item.checked).length > 0){return true;}
      
      return false;
    },

    //전체 검색
    async loadSearchResult(){
      const _vm = this;

      _vm.loading(true);
      try{
        const jobList = [];
        
        jobList.push(_vm.loadIniternalSearchProdResult());
        
        // Promise.all로 두 API 호출 병렬 처리
        await Promise.all(jobList);
        
      }catch(err){
        console.error(err);
        _vm.defaultApiErrorHandler(err); //기본에러처리
      }finally{
        _vm.loading(false);
      }
    },

    //제품검색 
    async loadIniternalSearchProdResult(){
      const _vm = this;

      _vm.searchResults.product.list.splice(0, _vm.searchResults.product.list.length);
      const prodParams = _vm.getProdSearchParam();

      const prodRes = await getSearchProdList(prodParams);
    
      //제품
      //console.debug(prodRes);
      const {list, maxScore, total, version} = prodRes.resultData;
      const dataList = _vm.convertSearchResultByVersion(version, list);
      _vm.searchResults.product.list.push(...dataList);
      _vm.searchResults.product.pi.curPage = 1;
      _vm.searchResults.product.pi.totalCount = total;

      _vm.$emit("onLoaded", _vm.searchResults.product.pi.totalCount);
    },

    //제품 더보기
    async loadSearchResultProdMore(){
      const _vm = this;

      _vm.loading(true);
      try{
        const pi = _vm.searchResults.product.pi;        
        const params = _vm.getProdSearchParam();
        params.page = ++pi.curPage;
        
        const prodRes = await getSearchProdList(params);

        const {list, maxScore, total, version} = prodRes.resultData;
        const dataList = _vm.convertSearchResultByVersion(version, list);
        _vm.searchResults.product.list.push(...dataList);

      }catch(err){
        console.error(err);
        _vm.defaultApiErrorHandler(err); //기본에러처리
      }finally{
        _vm.loading(false);
      }
    },

    //제품검색 파라미터 리턴
    getProdSearchParam(){
      const _vm = this;

      const param = {
        text : (_vm.keyword || "").trim().replace(/#/g, "")
        , page : 1
        , rowSize : DEF_SEARCH_ROWS_PER_PAGE
        , hintField: _vm.hintField
        , exactMatchOnly: _vm.exactMatchOnly
      };

      //카테고리 추가
      if(_vm.hasProdFilterOption()){
        const checkList = _vm.searchResults.product.filterOption.cateList.filter(item=>item.checked);
        const categories = checkList.map(item=>item.etc3);
        param["categories"] = [...categories];
      }

      const reSearchText = (_vm.search.product.reSearchText || "").trim();
      if(reSearchText.length > 0){
        param["reSearchText"] = reSearchText;
      }

      return param;
    },

    async fetchData(){
      const _vm = this;

      try{
        _vm.isLoggedIn = (isUserLoggedIn() || null) ? true : false;

        _vm.onClickRemoveProdFilterOption();
        await _vm.loadSearchResult();      
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
.tag-badge.primary {
  .item.link {
    cursor: pointer;
  }
}
</style>

<style lang="scss">
#component-search-result-products {
  
}
</style>
