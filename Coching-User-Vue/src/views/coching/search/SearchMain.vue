<template>
  <!--section은 3가지 타입 full, main-->
  <section id="coching-search-result" class="full main" :class="{'active': isSmallGnb}">    
    <div class="container h-100">
      <!--content-->
      <div class="content">

        <!--link-box-sticky-->        
        <div class="link-box-sticky scroll-x-area">
          <!--해당 페이지 btn-gray->btn-bk-->
          <button v-for="(item, idx) of tabs"
            :key="idx"
            type="button"
            class="btn btn-sm"
            :class="{
              'btn-gray' : !item.isActive,
              'btn-bk' : item.isActive
            }"
            @click="onClickTab(item)"
            >{{item.lable}} ({{getTabResultCount(item)}})</button>
        </div>

        <!-- 검색결과 컴포넌트 -->
        
        <!-- 성분 -->
        <ComSearchIngredients         
          v-show="activeTab.tabId == 'ingredient'"
          :keyword="keyword"
          :hintField="hintField"
          :exactMatchOnly="exactMatchOnly"
          ref="ingredientView"
          @onLoaded="(data)=>onLoadedTab('ingredient', data)"
          @onClickGotoRaw="onClickGotoRaw"
          @onClickGotoProduct="onClickGotoProduct"
        ></ComSearchIngredients>
        
        <!-- 원료 -->
        <ComSearchRaws
          v-show="activeTab.tabId == 'raw'"
          :keyword="keyword"
          :hintField="hintField"
          :exactMatchOnly="exactMatchOnly"
          :isRawSearch="isRawSearch"
          ref="rawView"
          @onLoaded="(data)=>onLoadedTab('raw', data)"
        ></ComSearchRaws>

        <!-- 제품-->
        <ComSearchProducts
          v-show="activeTab.tabId == 'product'" 
          :keyword="keyword"
          :hintField="hintField"  
          :exactMatchOnly="exactMatchOnly"
          ref="productView"
          @onLoaded="(data)=>onLoadedTab('product', data)"
        ></ComSearchProducts>

        <!-- 코칭 TV -->
        <ComSearchTv
          v-show="activeTab.tabId == 'coching-tv'" 
          :keyword="keyword"
          :hintField="hintField"
          :exactMatchOnly="false"
          ref="cochingTvView"
          @onLoaded="(data)=>onLoadedTab('coching-tv', data)"
        ></ComSearchTv>
        
      </div>

      <!--오류-->
      <AlertModal ref="alertModal"></AlertModal>
      <ConfirmModal ref="confirmModal"></ConfirmModal>

    </div>    
  </section>
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';

import { getCodes } from '@/api/coching/comm/code';
import { isUserLoggedIn, getUserData, getHomeRouteForLoggedInUser } from '@/auth/utils';

import ComSearchIngredients from './ComSearchIngredients.vue';
import ComSearchRaws from './ComSearchRaws.vue';
import ComSearchProducts from './ComSearchProducts.vue';
import ComSearchTv from './ComSearchTv.vue';
import { get } from 'lodash';

const TABS = [
  {lable: "성분"  , isActive: false, count: 0, paramVal: "ingd", tabId:"ingredient"},
  {lable: "원료"  , isActive: false, count: 0, paramVal: "raws", tabId:"raw"},
  {lable: "제품"  , isActive: false, count: 0, paramVal: "prod", tabId:"product"},
  {lable: "코칭TV", isActive: false, count: 0, paramVal: "ctv" , tabId:"coching-tv"},
];

export default {
	name: 'coching-search-result',
	mixins: [ernsUtils],
	components: {
    ComSearchIngredients,
    ComSearchRaws,
    ComSearchProducts,
    ComSearchTv,
  },
	computed: {
    activeTab(){
      const activeTab = this.tabs.find(tab=>tab.isActive);
      if(activeTab){  
        return activeTab;
      }
      return {lable: "none"  , isActive: false, count: 0, tabId:"none"};
    },
  },
  props: {
    keyword :{
      type : String,
      require : true
    },
    ingdIds:{
      type : Array,
      default : function(){
        return [];
      }
    },
    initViewType:{
      type : String,
      default : ''
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
    // 쿼리스트링 변경 감지
    '$route.query': {
      handler(newQuery, oldQuery) {
        // console.debug("newQuery", newQuery);
        // console.debug("oldQuery", oldQuery);

        // 검색 관련 쿼리 파라미터들
        const searchParams = ['keyword', 'emo','exactMatchOnly', 'isRawSearch', 'rs', 'ingdIds', 'hintField', 'hf'];
        const viewParams = ['initViewType', 'hf', 'rs'];
        
        // 검색 관련 파라미터가 변경되었는지 확인
        const searchChanged = searchParams.some(param => {
          return newQuery[param] !== oldQuery[param];
        });
        
        // 뷰 타입 파라미터가 변경되었는지 확인
        const viewTypeChanged = viewParams.some(param => {
          return newQuery[param] !== oldQuery[param];
        });
        
        if (searchChanged) {
          this.loadSearchResult();
        }
        
        if (viewTypeChanged) {
          this.setInitView(newQuery.initViewType);
        }
      },
      deep: true,
      immediate: false
    },
    
    // // 개별 props watch (fallback용 - props가 직접 전달되는 경우)
    // keyword(newKeyword) { 
    //   this.loadSearchResult();
    // },
    // exactMatchOnly(newExactMatchOnly) { 
    //   this.loadSearchResult();
    // },
    // isRawSearch(newIsRawSearch){
    //   this.loadSearchResult();
    // },
    initViewType(newViewType){
      this.setInitView(newViewType);
    },
	},
	data() {
		return {
      //로그인
      isLoggedIn : false,

      tabs : [...TABS],

      //데이터 로딩 확인
      compData: {
        loadedTabs: {},
        expectedTabs: ['ingredient', 'raw', 'product', 'coching-tv'],
        isAllTabsLoaded: false,
        timeoutMs: 5000, // 최대 대기 시간 5초
        timeoutHandler: null,
        isTimeout: false
      }      
		}
	},
	async mounted() {
		const _vm = this;

		_vm.docReady();
		_vm.fetchData();
    _vm.startLoading();
	},
	beforeDestroy() {
		const _vm = this;
	},
	methods: {
    //탭 클릭
    async onClickTab(tab){
      const _vm = this;

      _vm.tabs.forEach(t=>{
        t.isActive = false;
      });

      tab.isActive = true;
    },

    //표시할 검색결과 수
    getTabResultCount(tabItem){
      const tabId = tabItem.tabId;

      if(tabId == 'raw'){
        return tabItem.count >=600 ? "600+" : tabItem.count || "0";
      } else if(tabId == 'product'){
        return tabItem.count >=600 ? "600+" : tabItem.count || "0";
      } else if(tabId == 'ingredient'){
        return tabItem.count >=600 ? "30+" 
          : (tabItem.count <= 30 ? tabItem.count || "0" : "30+");
      } else {
        return tabItem.count || "0";
      }
    },

    //원료 클릭이벤트
    onClickGotoRaw(ingdInfo){
      const _vm = this;
      
      const initViewType = "raws";
      _vm.ermPushPage({
        name:'coching-search-main', 
        query : {
          keyword : (ingdInfo.rep_name || '').trim().replace(/,/g, "\\,"),          
          initViewType : initViewType,
          ingdIds : [`${ingdInfo.id}`],
          emo : true
        }
      });

      _vm.setInitView(initViewType);
    },

    //제품 클릭이벤트
    onClickGotoProduct(ingdInfo){
      const _vm = this;
      console.log("onClickGotoProduct", ingdInfo);
      
      const initViewType = "prod";
      _vm.ermPushPage({
        name:'coching-search-main', 
        query : {
          keyword : (ingdInfo.rep_name || '').trim().replace(/,/g, "\\,"),
          initViewType : initViewType,
          emo : true
        }
      });

      _vm.setInitView(initViewType);
    },

    //로드 후 이벤트
    onLoadedTab(tabId, data){
      const _vm = this;

      //console.debug(`onLoaded ${tabId} : ${data}`);      
      _vm.tabs.find(tab=>tab.tabId == tabId).count = data;

      _vm.$set(_vm.compData.loadedTabs, tabId, data);

      const allLoaded = _vm.compData.expectedTabs.every(tab => _vm.compData.loadedTabs.hasOwnProperty(tab));
      
      if (allLoaded && !_vm.compData.isAllTabsLoaded) {
        _vm.compData.isAllTabsLoaded = true;

        // 모든 탭이 로드된 후 실행할 작업
        _vm.onAllTabsLoaded();
      }
    },

    async loadSearchResult(loadType){
      const _vm = this;
    },

    //초기뷰 설정
    setInitView(initViewType){
      const _vm = this;

      let cInitViewType = initViewType;
      if(!cInitViewType){
        cInitViewType = "ingd";
      }

      for(const tab of _vm.tabs){
        tab.isActive = false;
        if(tab.paramVal == cInitViewType){
          tab.isActive = true;
        }          
      }
    },

    //로딩 대기
    startLoading() {
      const _vm = this;
      
      _vm.compData.loadedTabs = {};
      _vm.compData.isAllTabsLoaded = false;
      _vm.compData.isTimeout = false;

      // 타임아웃 시작
      clearTimeout(_vm.compData.timeoutHandler);
      _vm.compData.timeoutHandler = setTimeout(() => {
        _vm.compData.isTimeout = true;
        _vm.onLoadTimeout();
      }, _vm.compData.timeoutMs);
    },

    onLoadTimeout() {
      const _vm = this;
      //console.warn("일부 탭에서 onLoaded 이벤트가 오지 않았습니다.");
      
      // 예외 케이스 처리
      const missingTabs = _vm.compData.expectedTabs.filter(tab => !_vm.compData.loadedTabs.hasOwnProperty(tab));
      //console.warn("로딩 실패한 탭들:", missingTabs);

      // 필요한 경우 fallback 처리
      _vm.onAllTabsLoaded({ isPartial: true });
    },

    onAllTabsLoaded({ isPartial = false } = {}) {
      const _vm = this;
      if (isPartial) {
        // 일부 실패 처리
        //console.log("부분 로딩 완료", _vm.compData.loadedTabs);
      } else {
        // 전부 정상 완료
        //console.log("전체 로딩 완료", _vm.compData.loadedTabs);
      }

      if(!_vm.initViewType){        
        const haveResultTab = _vm.tabs.find(tab=>tab.count > 0);
        let targetTab = haveResultTab
        if(!haveResultTab){
          targetTab = _vm.tabs[0];
        }

        for(const tab of _vm.tabs){
          tab.isActive = false;            
        }
        haveResultTab.isActive = true;
      }
    },

    async fetchData(){
      const _vm = this;

      try{
        _vm.isLoggedIn = (isUserLoggedIn() || null) ? true : false;

        _vm.setInitView(_vm.initViewType);

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
