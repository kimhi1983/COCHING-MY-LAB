<template>
  <div class="main-search"
    ref="mainSearch"
    :class="{'focus':search.isFocus}">
      <!--search-wrap-->
      <div class="search-wrap">
        <button type="button" class="ic-lg ic-arrow-left-long"></button>

        <!--hash-->
        <div v-show="search.isHashOn" 
          class="hash">#</div>

        <!--tag-->        
        <button v-show="search.weightSearch.type" 
          @click="onClickClearWeightsSearch"
          type="button" 
          class="tag">{{search.weightSearch.name}}</button>

        <!--input-->
        <div class="search-input-wrap">
          <SearchInput type="search" 
            placeholder="글리세린(glycerin)"
            ref="searchInput"
            v-model="search.keyword"
            @focus="onFocusSearchKeyword(true)"
            
            @compositionstart="isComposing = true"
            @compositionend="onCompositionEnd"
            @input="onUpdateKeyword"
            @keyup.esc="resetSearchQuery"
            @keyup.enter.native="doSearchKeyword"
          />          
          <button ref="clearButton" type="button" class="ic-md ic-delete"
            v-show="search.keyword.trim().length > 0"
            @touchstart="resetSearchQuery"
            @mousedown="resetSearchQuery"
          ></button>          
        </div>

        <!--btn-->
        <div class="btn-wrap">
          <!-- 검색 버튼-->
          <button 
            ref="searchButton"
            @click.stop="doSearchKeyword" 
            type="button" class="ic-lg ic-search"
          ></button>

          <!-- HashTag-->
          <button 
            v-show="!search.weightSearch.type"
            ref="hashTagButton"
            @click.stop="search.isHashOn = !search.isHashOn"
            :class="{'active' : search.isHashOn}"
            type="button" class="ic-lg ic-hash"></button>

          <!-- <button 
            ref="helpButton"
            type="button" 
            class="ic-lg ic-help"
            v-tippy="{
              content:'콤마(,)로 구분하여 복수의 검색어로 검색할 수 있습니다.',
              trigger:'click',
              placement:'bottom',
              animation:'shift-toward',
              distance:10, //placement 이 수직 일경우 높이, 수평일 경우 좌우 간견
              offset:0 //placement 이 수직 일경우 좌우, 수평일 경우 높이
            }"
          ></button>           -->
        </div>
      </div>

      <!--tag-layer-->
      <div class="tag-layer"         
        v-show="search.isFocus && search.keyword.trim().length <= 0"
      >
        <div class="inner">
          <!--tag-wrap-->
          <div class="tag-wrap scroll-x-area" v-show="true">

            <!--가중치 검색 타입-->
            <button v-for="item of search.weightSearchTypes" :key="item.type"
              @click="onClickWeightSearch(item)"
              class="btn btn-xsm"
              :class="{'btn-primary' : search.weightSearch.type == item.type, 'btn-gray' : search.weightSearch.type != item.type}"
            >{{item.name}}</button>
            <!-- weightSearchTypes:{{ search.weightSearch }} -->

          </div>
          <!--search-list-->
          <div class="search-list">
            <div class="top">
              <div class="title">최근 검색어</div>
              <a v-show="recentSearchKeywords != null && recentSearchKeywords.length > 0"
                ref="deleteRecentSearchKeywords"
                @click="onClickDeleteRecentSearchKeywords" 
                href="javascript:;" 
                class="delete modal-open search-delete-modal">전체 삭제</a>
            </div>
            <div class="body scroller">
              <div v-for="(recentText, idx) of recentSearchKeywords.slice(0, 10)" :key="idx"
                class="div-recent-seachkeyword-item item">
                <a href="javascript:;"
                  @mousedown.stop="onClickRecentSearchKeyword(recentText)"
                  @touchstart.stop="onClickRecentSearchKeyword(recentText)"
                >{{recentText}}</a>
                <div class="item-delete div-recent-seachkeyword-delete" 
                  @touchstart.stop="onClickDeleteRecentSearchKeyword(recentText)"
                  @mousedown.stop="onClickDeleteRecentSearchKeyword(recentText)">
                  <button
                    @touchstart.stop="onClickDeleteRecentSearchKeyword(recentText)"
                    @mousedown.stop="onClickDeleteRecentSearchKeyword(recentText)"
                    type="button" 
                    class="ic-sm ic-close-gray-sm btn-recent-seachkeyword-delete"></button>
                </div>
              </div>
              <a v-show="recentSearchKeywords.length <= 0 && search.keyword.trim().length <= 0"
                class="error-message">최근검색 기록이 없습니다.</a>
            </div>
            <div class="foot">
              <div class="title">검색기록 저장</div>
              <label class="switch label-save-recent-search-keywords">
                <input 
                  ref="btnSaveRecentSearchKeywords"
                  v-model="saveRecentSearchKeywords"
                  type="checkbox" />
                <span class="slider"></span>
              </label>
            </div>
          </div>
        </div>
      </div>

      <!--auto-layer-->
      <div class="auto-layer scroller"
         ref="autoLayer"
         v-show="search.isFocus && search.keyword.trim().length > 0">
        
         <div v-show="search.isLoading"
          class="search-loader-wrap">
          <div class="loader"></div>
        </div>         

        <div v-if="!search.isLoading && (!search.rawResSuggestions || getSuggestionResultCount() <= 0)"
          class="error-message">
          <template v-if="'' == (search.weightSearch.type || '')">
            검색된 데이터가 없습니다.
          </template>            
          <template v-else-if="isShowSuggestionType('prd_name')">
            상품명은 검색 제안어를 제공하지 않습니다.
          </template>
          <template v-else>
            검색된 데이터가 없습니다.
          </template>
        </div>
          
        <SuggestionIngedient
          v-show="isShowSuggestionType('ingd')"
          v-for="(item, idx) of search.ingdSuggestions" 
          :key="'ingd_' + idx"
          :keyword="search.keyword"
          :suggestion="item"
          @onClickTitle="onClickSuggestionTitle"
          @onClickInfo="onClickSuggestionInfo"
          class="item">          
        </SuggestionIngedient>

        <SuggestionRaw
          v-show="isShowSuggestionType('raw')"
          v-for="(item, idx) of search.rawsSuggestions" 
          :key="'raw_' + idx"
          :keyword="search.keyword"
          :suggestion="item"
          @onClickTitle="onClickSuggestionTitle"
          @onClickInfo="onClickSuggestionInfo"
          class="item">          
        </SuggestionRaw>

        <SuggestionHwBrand  
          v-show="isShowSuggestionType('hw_brand')"
          v-for="(item, idx) of search.hwBrandSuggestions" 
          :key="'hwBrand_' + idx"
          :keyword="search.keyword"
          :suggestion="item"
          @onClickTitle="onClickSuggestionTitle"
          @onClickInfo="onClickSuggestionInfo"
          class="item">          
        </SuggestionHwBrand>

        <SuggestionPrtName
          v-show="isShowSuggestionType('pt_name')"  
          v-for="(item, idx) of search.prtNameSuggestions" 
          :key="'prtName_' + idx"
          :keyword="search.keyword"
          :suggestion="item"
          @onClickTitle="onClickSuggestionTitle"
          @onClickInfo="onClickSuggestionInfo"
          class="item">          
        </SuggestionPrtName>

        <!-- <a href="javascript:;" class="item">
          <div class="title-wrap">
            <div class="title"><span class="highlight">글리</span>세린</div>
            <div class="info">one two three,four five six seven eight nine ten</div>
          </div>
          <div class="badge green">성분</div>
        </a>
        <a href="javascript:;" class="item">
          <div class="title-wrap">
            <div class="title"><span class="highlight">글리</span>세린</div>
            <div class="info"><span>(주)케이프로스</span><span>삼정트레이딩</span></div>
          </div>
          <div class="badge blue">원료</div>
        </a>
        <a href="javascript:;" class="item">
          <div class="title-wrap">
            <div class="title">원료<span class="highlight">글리</span>세린</div>
            <div class="info"><span>(주)케이프로스</span><span>삼정트레이딩</span></div>
          </div>
          <div class="badge blue">원료</div>
        </a>
        <a href="javascript:;" class="item">
          <div class="title-wrap">
            <div class="title">원료<span class="highlight">글리</span>세린</div>
            <div class="info"><span>(주)케이프로스</span><span>(주)케이프로스</span><span>삼정트레이딩</span></div>
          </div>
          <div class="badge blue">원료</div>
        </a>
        <a href="javascript:;" class="item">
          <div class="title-wrap">
            <div class="title">원료<span class="highlight">글리</span>세린</div>
            <div class="info"><span>(주)케이프로스</span><span>삼정트레이딩</span></div>
          </div>
          <div class="badge blue">원료</div>
        </a> -->
      </div>
   
    <!--오류-->
    <AlertModal ref="alertModal"></AlertModal>
    <ConfirmModal ref="confirmModal"></ConfirmModal>
  </div>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import { debounce }from 'lodash';

import { getSuggestionList } from '@/api/coching/search/search';

import SearchInput from '@/components/search/SearchInput.vue';
import SuggestionRaw from '@/components/search/SuggestionRaw.vue';
import SuggestionIngedient from '@/components/search/SuggestionIngedient.vue';
import SuggestionHwBrand from '@/components/search/SuggestionHwBrand.vue';
import SuggestionPrtName from '@/components/search/SuggestionPrtName.vue';

const DEBOUNCE_DELAY = 300;
const WEIGHT_SEARCH_TYPES = [
  {type : 'ingd', name : '성분', hintField : '0001,1021'},
  {type : 'raw', name : '원료', hintField : '1002'},  
  {type : 'hw_brand', name : '브랜드', hintField : '2001'},
  {type : 'prd_name', name : '상품명', hintField : '2002'},
  {type : 'pt_name', name : '원료사', hintField : '1014'},
];

export default {
  name: 'SearchBar',
  mixins: [ernsUtils],
  components: { 
    SearchInput,
    SuggestionRaw,
    SuggestionIngedient,
    SuggestionHwBrand,
    SuggestionPrtName,
	},
  computed: {
    saveRecentSearchKeywords: {
      get() {
        return this.$store.state.coching.saveRecentSearchKeywords;
      },
      set(value) {
        this.$store.dispatch('coching/setSaveRecentSearchKeywords', value);
      }
    },
    recentSearchKeywords(){
      return this.$store.state.coching.recentSearchKeywords;
    },
  },
  props:{    
  },
  watch:{
    $route(to, from) {
      const _vm = this;
      try {
        //console.debug(to);
        //console.debug(from);

        //검색어 유지 처리
        const toName = to.name;
        const fromName = from.name;
        let resetSearchKeyword = false;

        //let resetSearchKeyword = true;
        // if (toName == 'coching-search-ingredients' && fromName == 'coching-search-ingredients') {
        //   resetSearchKeyword = false;
        // }

        // if (toName == 'coching-search-raws' && fromName == 'coching-search-raws') {
        //   resetSearchKeyword = false;
        // }

        // if (toName == 'coching-search-main' && (
        //     fromName == 'coching-search-main' ||
        //     fromName == 'coching-main'
        //   )
        //   ) {
        //   resetSearchKeyword = false;
        // }

        _vm.initSearchKeyword();
        if (resetSearchKeyword) {
          _vm.search.keyword = "";
        }

        //_vm.onFocusSearchKeyword(true);

      } catch (err) {
        console.error(err);
      }    
    }    
  },
  data(){
    return {
      search : {
        isFocus : false,
        isHashOn : false,
        keyword : "",

        weightSearchTypes : [...WEIGHT_SEARCH_TYPES],

        //가중치 검색
        weightSearch : {
          type : "",
          name : "",
          hintField : "",
        },

        //검색 제안어
        rawResSuggestions : [], //raw RES data

        rawsSuggestions : [], //raw 제안어(원료)
        ingdSuggestions : [], //ingd 제안어(성분)
        hwBrandSuggestions : [], //hw_brand 제안어(브랜드)
        prtNameSuggestions : [], //prt_name 제안어(원료사)

        isLoading : false,
      },

      debounceTimer: null,
      isComposing: false, // 한글 조합 중 여부
    }
  },
  created(){
    // debounce 함수 초기화 (300ms 지연)
    this.debouncedLoadSuggestions = debounce(this.loadSuggestions, DEBOUNCE_DELAY);
  },   
  mounted() {
    const _vm = this;

    _vm.fetchData();    
    _vm.docReady();
    document.addEventListener('click', this.handleClickOutside);
    document.addEventListener('touchstart', this.handleClickOutside);
    document.addEventListener('keydown', this.handleKeyDown);

    //console.log(this.$router.currentRoute);
    _vm.initSearchKeyword();   
  },
  beforeDestroy() {
    document.removeEventListener('click', this.handleClickOutside);
    document.removeEventListener('touchstart', this.handleClickOutside);
    document.removeEventListener('keydown', this.handleKeyDown);
  },
  methods:{
    //가중치 검색 초기화
    onClickClearWeightsSearch(){
      const _vm = this;
      _vm.search.weightSearch = {
        type : "",
        name : "",
        hintField : "",
      };
    },

    //가중치 검색 선택
    onClickWeightSearch(searchTypeItem){
      const _vm = this;      
      _vm.search.weightSearch ={
        ...searchTypeItem
      };
      _vm.search.isHashOn = false;
    },

    //제안단어 클릭 이벤트
    onClickSuggestionTitle(suggestion){
      const _vm = this;
      _vm.search.keyword = suggestion.data2;

      let hintField = '0000';
      for(let item of WEIGHT_SEARCH_TYPES){
        if(suggestion.datatype == item.type){
          hintField = item.hintField;
          break;
        }
      }

      const searchKeyword = (suggestion.data2 || "").replace(/,/g, "\\,"); //데이터 자체에 , 가 있는경우 , -> \, 로 변경
      const isRawSearch = suggestion.datatype == 'raw' || suggestion.datatype == 'pt_name';
      const searchOption = {hintField : hintField, emo : true, rs : isRawSearch};

      _vm.goSearchKeywordV2(searchKeyword, searchOption);
      _vm.onFocusSearchKeyword(false);
    },

    //제안단어 타입 표시 여부
    isShowSuggestionType(searchType){
      const _vm = this;

      const weightSearchType = _vm.search.weightSearch.type || "";

      if(weightSearchType == ""){
        return true;
      }

      return weightSearchType == searchType;
    },

    //제안단어 검색 결과 여부
    getSuggestionResultCount(){
      const _vm = this;

      const weightSearchType = _vm.search.weightSearch.type || "";

      if(weightSearchType == ""){
        return _vm.search.rawResSuggestions.length;
      }

      switch(weightSearchType){
        case 'ingd':
          return _vm.search.ingdSuggestions.length;
        case 'raw':
          return _vm.search.rawsSuggestions.length;
        case 'hw_brand':
          return _vm.search.hwBrandSuggestions.length;
        case 'prd_name':
          return 0;
        case 'pt_name':
          return _vm.search.prtNameSuggestions.length;
        default:
      }      
    },

    //제안단어 클릭 이벤트
    onClickSuggestionInfo(suggestion){
      const _vm = this;

      //NOTE : 정보클릭시 별도 이벤트 처리 필요하면 구현 필요
      _vm.onClickSuggestionTitle(suggestion);
    },

    //검색어 초기화
    initSearchKeyword(){
      const _vm = this;

      const keyword = _vm.$route.params.keyword || _vm.$route.query.keyword || "";
      _vm.search.keyword = keyword.trim();
    },

    //검색영역 외부 클릭 이벤트
    handleClickOutside(event) {
      const _vm = this;
      const mainSearchEl = _vm.$refs.mainSearch;
      if (mainSearchEl && !mainSearchEl.contains(event.target)) {
        // .main-search 외부가 클릭/터치됨
        _vm.search.isFocus = false;
      }
    },
    
    handleKeyDown(event) {
      const _vm = this;
      if (event.key === 'Escape' || event.key === 'Esc') {
        if(_vm.search.isFocus){
          _vm.search.isFocus = false;
          _vm.$refs.searchInput.blur();          
        }        
      }
    },

    // 조합 중 끝
    onCompositionEnd() {
      const _vm = this;
      
      _vm.isComposing = false;
      _vm.onUpdateKeyword(); // 조합 끝났을 때 실행
    },

    //검색초기화
    resetSearchQuery(){
      const _vm = this;

      _vm.search.keyword = "";
      setTimeout(() => {
        _vm.$refs.searchInput.focus();
        _vm.search.isFocus = true;
      }, 100);
    },

    //검색어 갱신
    onUpdateKeyword(pIsIncreaseIndex){
      const _vm = this;
      if (_vm.isComposing) return;

      const searchWord = _vm.search.keyword.trim();

      if(pIsIncreaseIndex){
        _vm.debouncedLoadSuggestions(searchWord);
      }
    },

    //검색어 focus
    async onFocusSearchKeyword(isFocus){
      const _vm = this;
      _vm.search.isFocus = isFocus;

      if(_vm.search.isFocus){        
        setTimeout(() => {
          _vm.$refs.searchInput.focus();
          _vm.search.isFocus = true;
        }, 100);        
      }else{
        setTimeout(() => {
          _vm.$refs.searchInput.blur();
          _vm.search.isFocus = false;
        }, 100);        
      }
    },

    //최근 검색어 전체 삭제
    async onClickDeleteRecentSearchKeywords(){
      const _vm = this;

      _vm.$store.dispatch('coching/setRecentSearchKeywords', []);
    },

    // search-layer 영역에서 손가락 제스처 시 키패드 내리기
    onTouchStartSearchLayer(){
      const _vm = this;
      _vm.$refs.searchInput.blur(); // searchInput의 blur() 호출
    },

    //최근 검색어 삭제
    async onClickDeleteRecentSearchKeyword(recentText){
      const _vm = this;

      //console.debug(recentText);

      const recentSearchKeywords = _vm.recentSearchKeywords;
      if(recentSearchKeywords.includes(recentText)){
        recentSearchKeywords.splice(recentSearchKeywords.indexOf(recentText), 1);
        _vm.$store.dispatch('coching/setRecentSearchKeywords', recentSearchKeywords);
      }
    },

    onInputBlur(event){
      const _vm = this;

      let isExclude = false;

      //relatedTarget이 존재하고 특정 요소를 클릭한 경우 blur 무시
      const refs = [
        this.$refs.clearButton,   //input 입력검색어 삭제
        this.$refs.searchButton,  //검색버튼
        this.$refs.hashTagButton, //도움말버튼
        this.$refs.helpButton,    //해시태그 버튼
        this.$refs.deleteRecentSearchKeywords, //최근 검색어 삭제버튼
        this.$refs.btnSaveRecentSearchKeywords, //검색기록 저장버튼
      ];

      refs.forEach(ref=>{
        if (ref && ref.contains(event.relatedTarget)) {
          isExclude = true;
          return;
        }
      });

      if(isExclude){
        return;
      } 

      //최근 검색어 삭제버튼
      if (event.relatedTarget ){
        const excludeClasses = [
          'div-recent-seachkeyword-item',   //최근 검색에 영역
          'btn-recent-seachkeyword-delete', //최근 검색어 삭제
          'div-recent-seachkeyword-delete', //최근 검색어 삭제
          'label-save-recent-search-keywords', //검색기록 저장
        ];

        excludeClasses.forEach(cls=>{
          if(event.relatedTarget.classList.contains(cls)){
            isExclude = true;
            return;
          }
        });
      }

      if(isExclude){
        return;
      } 

      //최근검색어 레이어 위치 할경우
      if(event.target.closest('.tag-layer')){
        return;
      }

      console.debug(event);

      // blur 직후 이벤트 발생 위치가 .tag-layer 내부인지 확인
      setTimeout(() => {
        const active = document.activeElement;
        const tagLayer = this.$el.querySelector('.tag-layer');

        if (tagLayer && tagLayer.contains(active)) {
          // .tag-layer 안으로 focus가 옮겨졌다면 blur 처리 무시
          return;
        }

        // 그 외 영역으로 나갔을 경우 blur 처리 실행
        _vm.onFocusSearchKeyword(false);
      }, 0);

      //_vm.onFocusSearchKeyword(false);
    },

    //검색어 검색
    doSearchKeyword(){
      const _vm = this;

      console.debug("doSearchKeyword:"+_vm.search.keyword);

      let searchWord = (_vm.search.keyword || '').trim();

      let hintField = '0000';
      //해시태그 활성화 시
      if(searchWord.length > 0){
        if(_vm.search.isHashOn) {
          const formatWithHash = (input) => 
          input.trim()
              .split(/\s+/)
              .map(word => `#${word}`)
              .join(' ')
              .replace(/#{2,}/g, '#');

          searchWord = formatWithHash(searchWord);
          hintField = '1001'; //raw hashtag
        }       

        if(_vm.search.weightSearch.type){
          hintField = _vm.search.weightSearch.hintField;
        }
      }

      if(_vm.saveRecentSearchKeywords && searchWord.length > 0){
        //검색기록에 저장
        const newRecentSearchKeywords = 
        _vm.cleanArrayWithReduce([searchWord, ..._vm.recentSearchKeywords])
        .slice(0, 10);
        
        _vm.$store.dispatch('coching/setRecentSearchKeywords', newRecentSearchKeywords);
        //_vm.search.suggestions = [..._vm.recentSearchKeywords];

      }else{
        if(!_vm.search.isFocus){
          _vm.onFocusSearchKeyword(true);
          return;
        }        
      }

      _vm.goSearchKeyword(searchWord, hintField);

      _vm.onFocusSearchKeyword(false);
      //_vm.resetSearchQuery();
    },

    //최근검색어 클릭 이벤트
    async onClickRecentSearchKeyword(keyword){
      const _vm = this;

      console.debug(keyword);
      _vm.search.keyword = keyword;      
      await _vm.doSearchKeyword();
    },

    //제안단어 로드
    async loadSuggestions(inputKeyword){
      const _vm = this;

      _vm.search.isLoading = true;

      _vm.search.rawResSuggestions.splice(0, _vm.search.rawResSuggestions.length);
      const searchQuery = (inputKeyword || "").trim();
      if(!searchQuery){
        _vm.search.isLoading = false;
        _vm.formatSuggestion();
        return [];
      }

      const sleep = function(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
      }

      await sleep(20);
      
      try{       
        const params = {
          keyword : searchQuery,
          limitIngd : 10,
          limitRaw : 10,
        };

        const suggestionsRes = await getSuggestionList(params);
        const {list, maxScore, total, version} = suggestionsRes.resultData;
        _vm.search.rawResSuggestions = [...list];
        _vm.formatSuggestion();
        
        // 검색 제안어 영역 스크롤을 맨 위로 이동
        _vm.scrollToTopAutoLayer();
        
      }catch(err){
        console.error(err);
        //_vm.defaultApiErrorHandler(err); //기본에러처리
      }finally{
        _vm.search.isLoading = false;
      }
      return _vm.search.suggestions;
    },

    //제안단어 처리
    formatSuggestion(){
      const _vm = this;

      const keword = (_vm.keyword || "").trim().toLowerCase;

      //원료
      const rawsSuggestions = _vm.search.rawResSuggestions.filter(item=>{
        return item.datatype == 'raw';
      }).map(item=>{
        const data2Index = (item.data2 || '').toLowerCase().indexOf(keword); 
        const data5Index = (item.data5 || '').toLowerCase().indexOf(keword); 
        const data4Index = (item.data4 || '').toLowerCase().indexOf(keword); 
        let score = 9999999 - (data2Index + data5Index + data4Index);
        
        return {
          ...item,
          score : score,
          nameLength : item.data2.length
        };
      });

      rawsSuggestions.sort((a, b) => {
        if(a.score === b.score){  
          return a.nameLength - b.nameLength;
        }

        return b.score - a.score;
      }); 
      //console.debug(rawsSuggestions);

      _vm.search.rawsSuggestions.splice(0, _vm.search.rawsSuggestions.length);
      _vm.search.rawsSuggestions.push(...rawsSuggestions);

      //성분
      const ingdSuggestions = _vm.search.rawResSuggestions.filter(item=>{
        return item.datatype == 'ingd';
      }).map(item=>{
        const data2Index = (item.data2 || '').toLowerCase().indexOf(keword); 
        const data3Index = (item.data3 || '').toLowerCase().indexOf(keword); 
        const data4Index = (item.data4 || '').toLowerCase().indexOf(keword); 
        const data5Index = (item.data5 || '').toLowerCase().indexOf(keword); 
        
        let score = 9999999 - (data2Index + data3Index + data4Index + data5Index);
        
        return {
          ...item,
          score : score,
          nameLength : item.data2.length
        };
      });

      ingdSuggestions.sort((a, b) => {
        if(a.score === b.score){  
          return a.nameLength - b.nameLength;
        }

        return b.score - a.score;
      }); 
      //console.debug(ingdSuggestions);
      
      _vm.search.ingdSuggestions.splice(0, _vm.search.ingdSuggestions.length);
      _vm.search.ingdSuggestions.push(...ingdSuggestions);

      //브랜드
      const hwBrandSuggestions = _vm.search.rawResSuggestions.filter(item=>{
        return item.datatype == 'hw_brand';
      }).map(item=>{
        const data2Index = (item.data2 || '').toLowerCase().indexOf(keword); 
        const data3Index = (item.data3 || '').toLowerCase().indexOf(keword); 
        const data4Index = (item.data4 || '').toLowerCase().indexOf(keword); 
        
        let score = 9999999 - (data2Index + data3Index + data4Index);
        
        return {
          ...item,
          score : score,
          nameLength : item.data2.length
        };
      });

      hwBrandSuggestions.sort((a, b) => {
        if(a.score === b.score){  
          return a.nameLength - b.nameLength;
        }

        return b.score - a.score;
      }); 
      //console.debug(hwBrandSuggestions);
      
      _vm.search.hwBrandSuggestions.splice(0, _vm.search.hwBrandSuggestions.length);
      _vm.search.hwBrandSuggestions.push(...hwBrandSuggestions);

      //원료사
      const prtNameSuggestions = _vm.search.rawResSuggestions.filter(item=>{
        return item.datatype == 'pt_name';
      }).map(item=>{
        const data2Index = (item.data2 || '').toLowerCase().indexOf(keword); 
        let score = 9999999 - (data2Index);
        
        return {
          ...item,
          score : score,
          nameLength : item.data2.length
        };
      });

      prtNameSuggestions.sort((a, b) => {
        if(a.score === b.score){  
          return a.nameLength - b.nameLength;
        }

        return b.score - a.score;
      }); 
      //console.debug(prtNameSuggestions);
      
      _vm.search.prtNameSuggestions.splice(0, _vm.search.prtNameSuggestions.length);
      _vm.search.prtNameSuggestions.push(...prtNameSuggestions);
    },

    //자동완성 영역 스크롤을 맨 위로 이동
    scrollToTopAutoLayer(){
      const _vm = this;
      
      _vm.$nextTick(() => {
        const autoLayerEl = _vm.$refs.autoLayer;
        if (autoLayerEl) {
          autoLayerEl.scrollTop = 0;
        }
      });
    },

    //자동완성 영역 스크롤을 맨 위로 이동
    scrollToTopAutoLayer(){
      const _vm = this;
      
      _vm.$nextTick(() => {
        const autoLayerEl = _vm.$refs.autoLayer;
        if (autoLayerEl) {
          autoLayerEl.scrollTop = 0;
        }
      });
    },

    //제안단어 하이라이트 처리
    highlightText(pOrgText, pSearchQuery){
      const searchQuery = (pSearchQuery || "").trim();
      const text = (pOrgText || "").trim();
      if (text.toLowerCase().includes(searchQuery) && searchQuery !== "") {
        const highlightedText = text.replace(new RegExp(`(${searchQuery})`, "gi"), '<span class="highlight">$1</span>');
        return highlightedText;
      }

      return pOrgText
    },

    async fetchData(){
      const _vm = this;

      _vm.search.keyword = "";
    },

    docReady(){
      const _vm = this;
    },
  }
}
</script>

<style lang="scss" scoped>
.main-search {
  .search-wrap {
    .ic-delete {
      display: block;
    } 
  } 

  .error-message{
    color: #f43333;
  }

  .auto-layer {
    min-height: 25rem;

    .search-loader-wrap{
      text-align: center;
      height: 20rem;
      align-content: center;
    }

    .error-message{
      color: #aaaaaa;
      text-align: center;
    }
  }
} 
</style>

<style lang="scss">
.tippy-popper[x-placement^=bottom] .tippy-arrow{
  height: 0px !important;
}

</style>
