<template>
  <ModalFull ref="modal_full"
    :isActive="localData.isActive" 
    :classOption="localData.classOption"
    @onClickDim="onClickClose()"
    @close="onClickOk()">

    <!-- Header Content -->
    <template v-slot:header>
      <div class="title">원료 필터</div>
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
          v-show="localData.optionList.filter(item=>item.checked).length > 0
            || localData.singleRaw || localData.multipleRaw
          ">
          <div v-if="localData.singleRaw" class="item">단일원료
            <button type="button" class="ic-xsm ic-close-xsm"
              @click="localData.singleRaw = false;"
            ></button>
          </div>

          <div v-if="localData.multipleRaw" class="item">복합원료
            <button type="button" class="ic-xsm ic-close-xsm"
              @click="localData.multipleRaw = false;"
            ></button>
          </div>

          <div v-for="(item, idx) of localData.optionList.filter(item=>item.checked)"
            :key="`mobile-raw-filter-box-item-${idx}`" class="item">{{item.codeNmKo}}
            <button type="button" class="ic-xsm ic-close-xsm"
              @click.stop ="item.checked = false;"
            ></button>
          </div>
        </div>

        <div class="filter-check scroller">
          <!--checkbox id값 변경하세요-->
          <div class="item">
            <div class="check-wrap">
              <div>
                <div class="checkbox">
                  <input id="m-raw-singleRaw-01" type="checkbox" 
                    v-model="localData.singleRaw"
                  />
                  <label for="m-raw-singleRaw-01" class="checkbox-label">단일원료</label>
                </div>
              </div>
              <div>
                <div class="checkbox">
                  <input id="m-raw-multipleRaw-02" type="checkbox" 
                    v-model="localData.multipleRaw"
                  />
                  <label for="m-raw-multipleRaw-02" class="checkbox-label">복합원료</label>
                </div>
              </div>
            </div>
          </div>
          
          <!--첫번째는 펼쳐주세요 block-->
          <div v-for="(grpItem, grpIdx) of CODES.cmTypeData.grpList" :key="grpIdx"
            class="item filter-acd"
            :class="{'block': grpItem.isOpen}"
          >
            <div class="title"
              @click="onToggleGroupItem(grpItem)"
            >{{grpItem.codeName}}
              <!-- 선택된 개수 표시-->
              <span v-if="CODES.cmTypeData[`TYPE${grpItem.etc1}`].filter(item=>item.checked).length > 0">
                {{CODES.cmTypeData[`TYPE${grpItem.etc1}`].filter(item=>item.checked).length}}
              </span>
            </div>

            <div class="check-wrap">

              <div v-for="(item, itemIdx) of CODES.cmTypeData[`TYPE${grpItem.etc1}`]"
                :key="itemIdx"
              >
                <div class="checkbox">
                  <input :id="`m-raw-checkbox-${grpItem.etc1}-${itemIdx}`"
                    type="checkbox"
                    v-model="item.checked"
                  />
                  <label :for="`m-raw-checkbox-${grpItem.etc1}-${itemIdx}`" class="checkbox-label">{{item.codeNmKo}}</label>
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
import { getCodes, getRawCmTypeList } from '@/api/coching/comm/code';
import _ from 'lodash';

export default {
  name: 'coching-mobile-raw-filter-modal',
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
        cmTypeData:{
          grpList: [],
          
          list: [],
          TYPE001: [],
          TYPE002: [],
          TYPE003: [],
          TYPE004: [],
          TYPE005: [],
        }
      },

      localData: {
        isActive : false,
        classOption: "modal-md for-filter btn-fixed-modal",
        result : undefined,

        singleRaw : false,
        multipleRaw : false,
        optionList: [],
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
      //console.debug(options);
      ld.reSearchText = options.initReSearchText || '';
      const initFilterOption = options.initFilterOption;
      ld.singleRaw = initFilterOption.singleRaw;
      ld.multipleRaw = initFilterOption.multipleRaw;      
      initFilterOption.optionList.forEach(iCd=>{
        const find = ld.optionList.find(item =>{
          return item.grpCode == iCd.grpCode && item.code == iCd.code
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

      _vm.localData.singleRaw = true;
      _vm.localData.multipleRaw = true;
      _vm.localData.optionList.forEach(item =>{
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
        , optionList : ld.optionList
        , singleRaw : ld.singleRaw
        , multipleRaw : ld.multipleRaw
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

      const codeRes = await getCodes({grpCode:"CH001", etc5:_vm.$i18n.locale});
      const { resultCode, resultFailMessage, resultData } = codeRes;

      _vm.CODES.cmTypeData.grpList = [...resultData.list.map(item=>{
        return {
          ...item,
          isOpen : false
        }
      })];
      _vm.CODES.cmTypeData.grpList[0].isOpen = true;

      await _vm.loadCmType();
    },

    async loadCmType(){
      const _vm = this;

      const res = await getRawCmTypeList({});
      const { resultCode, resultFailMessage, resultData } = res;

      const list = resultData.list;
      _vm.localData.optionList =  _vm.CODES.cmTypeData.list = list.map(item=>{
        return {
          ...item,
          checked : false,
          isOpen : false
        }
      });

      _vm.CODES.cmTypeData.grpList.forEach(code => {
        _vm.CODES.cmTypeData[`TYPE${code.etc1}`] = _vm.CODES.cmTypeData.list.filter(item => item.grpCode === code.etc1);
      });
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