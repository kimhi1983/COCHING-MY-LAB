<template>
  <ModalFull ref="modal_full"
    :isActive="localData.isActive" 
    :classOption="localData.classOption"
    @onClickDim="onClickClose()"
    @close="onClickOk()">

    <!-- Header Content -->
    <template v-slot:header>
      <div class="title">제품 필터</div>           
    </template>

    <!-- Body Content -->
    <template v-slot:body>
      <!--filter-common-->
      <div class="filter-common">
        
        <div class="title-wrap">
          <div class="input-set">
            <div class="input-ic-set">
              <input type="text" 
                placeholder="결과 내 재검색" 
                v-model="localData.reSearchText"
                @keyup.enter="onClickDetailSearch"/>
              <button type="button" 
                class="input-ic ic-md ic-search-md"
                @click="onClickDetailSearch"/><!--결과내 검색-->
            </div>
          </div>
          <button type="button" class="deselect"
            @click="onClickResetFilter"/><!--필터 초기화-->
        </div>

        <!--filter-box-->
        <!--디폴트는 노출안함, 필터 선택 하나라도 있으면 노출하고, 하나라도 없으면 삭제-->
        <div class="filter-box"
          v-show="localData.cateList.filter(item=>item.checked).length > 0">        
          <div v-for="(item, idx) of localData.cateList.filter(item=>item.checked)"
            :key="`mobile-prod-filter-box-item-${idx}`" class="item">{{item.name}}
            <button type="button" class="ic-xsm ic-close-xsm"
              @click.stop ="item.checked = false;"
            ></button>
          </div>
        </div>

        <div class="filter-check scroller">
          <!--첫번째는 펼쳐주세요 block-->
          <div v-for="(grpItem, grpIdx) of CODES.grpList" :key="grpIdx"
            class="item filter-acd"
            :class="{'block': grpItem.isOpen}"
          >
            <div class="title"
              @click="onToggleGroupItem(grpItem)"
            >{{grpItem.name}}
              <!-- 선택된 개수 표시-->
              <span v-if="CODES[`CATE${grpItem.etc3}`].filter(item=>item.checked).length > 0">
                {{CODES[`CATE${grpItem.etc3}`].filter(item=>item.checked).length}}
              </span>
            </div>

            <div class="check-wrap">
              <div v-for="(item, itemIdx) of CODES[`CATE${grpItem.etc3}`]"
                :key="itemIdx"
              >
                <div class="checkbox">
                  <input :id="`m-prod-cate-checkbox-${item.etc3}`" 
                    type="checkbox" 
                    v-model="item.checked"
                  />
                  <label :for="`m-prod-cate-checkbox-${item.etc3}`" 
                    class="checkbox-label">{{item.name}}</label>
                </div>
              </div>            
            </div>
          </div>        
        </div>
        
      </div>
    </template>
    
    <template v-slot:footer>
      <button @click="onClickOk"
        type="button" class="btn btn-lg btn-primary w-100">적용하기</button>
    </template>

  </ModalFull>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import ModalFull from '@/components/modal/ModalFull.vue';
import { PROD_CATES } from '@/constants/testCode';
import _ from 'lodash';

export default {
  name: 'coching-mobile-prod-filter-modal',
  mixins: [ernsUtils],
  components: {
    ModalFull,
  },
  props: {
  },
  computed: {
      
  },
  watch: {
    '$i18n.locale' : async function(){
      const _vm = this;
      await _vm.loadCodes();
      _vm.$nextTick(() => {});
    },
  },
  data() {
    return {
      CODES : {
        prodCateList : [...PROD_CATES],

        grpList: [],
        list: [],
        CATE001 : [],
      },

      localData: {
        isActive : false,
        classOption: "modal-md for-filter btn-fixed-modal",
        result : undefined,

        cateList : [],        
        reSearchText : ""

      },          
    }
  },
  async mounted(){
    const _vm = this;

    await _vm.loadCodes();
    await _vm.fetchData();    
    _vm.docReady();
  },
	beforeDestroy() {
    const _vm = this;
	},
  methods: {
    open(options){
      const _vm = this;

      const ld = _vm.localData;
      ld.isActive = true;
      ld.reSearchText = options.initReSearchText || '';
      const initFilterOption = options.initFilterOption;
      initFilterOption.cateList.forEach(iCd=>{
        const find = ld.cateList.find(item =>{
          return item.etc3 == iCd.etc3
        });      

        if(find){
          find.checked = iCd.checked || false;
        }
      });
      
      _vm.$refs['modal_full'].bodyScrollToTop(500);
      _vm.docReady();

      return new Promise( (resolve, reject) => {
        ld.result = resolve;
      })
    },

    onClickOk(){
      const _vm = this;

      const ld = _vm.localData;
      const retValue = _vm.getValue();
      ld.isActive = false;
      ld.result({...retValue});
    },

    //상세검색
    onClickDetailSearch(){
      const _vm = this;
      
      _vm.onClickOk();
    },

    //필터초기화
    onClickResetFilter(){
      const _vm = this;

      _vm.localData.cateList.forEach(item =>{
        item.checked = false;
      });
    },

    //토글
    onToggleGroupItem(grpItem){
      if(grpItem.isOpen){
        grpItem.isOpen = false;
      }else{
        grpItem.isOpen = true;
      }
    },

    //설정된 값을 리턴
    getValue(){
      const _vm = this;

      const ld = _vm.localData;
      const retValue = {
        reSearchText : (ld.reSearchText || '').trim()
        , cateList : ld.cateList
      }

      return JSON.parse(JSON.stringify(retValue));
    },

    //fetchData
    async fetchData(){
      const _vm = this;

      _vm.loading(true);
      try{
        
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },    

    async loadCodes(){
      const _vm = this;

      let filterList = _vm.CODES.prodCateList
        .filter(cd => cd.name != '전체')
        .filter(cd => cd.code != 2);

      filterList = _.sortBy(filterList, item => item.sortOrder);

      _vm.localData.cateList = _vm.CODES.list = [...filterList.map(item=>{
        return {
          ...item,
          checked : false,
          isOpen : false
        }
      })];
      _vm.CODES.grpList = _vm.CODES.list.filter(cd=>cd.etc1==1);
      _vm.CODES.grpList.forEach(code => {
        _vm.CODES[`CATE${code.etc3}`] = _vm.CODES.list.filter(cd=>{
          return cd.etc3.indexOf(code.etc3)==0 && cd.etc3 != code.etc3;
        });
      });
      _vm.CODES.grpList[0].isOpen = true;
    },

    onClickClose() {
      const _vm = this;

      const ld = _vm.localData;
      ld.isActive = false;
      ld.result(false);
    },

    docReady(){
      const _vm = this;

      const isMobile = /iPhone|iPad|iPod|Android/i.test(navigator.userAgent);

      if (!isMobile) {
        // 데스크톱에서만 nicescroll 적용
        $(".scroller").niceScroll();

        // 리사이즈 시 스크롤 재조정
        $(window).on("resize", function () {
          $(".scroller").getNiceScroll().resize();
        });

        // 스크롤 이벤트에서 다른 스크롤 방지
        $(".scroller").on("scroll", function (e) {
          e.stopPropagation(); // 다른 스크롤 이벤트가 영향을 미치지 않도록 방지
        });
      } else {
        // 모바일에서는 기본 스크롤 사용
        $(".scroller").css("overflow", "auto");
      }

    },
  }  
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">

</style>