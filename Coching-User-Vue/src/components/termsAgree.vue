<template>
  <!--agree-->
  <div class="agree">
    <div class="title-wrap"  v-if="useAllCheckbox">
      <div class="checkbox">
        <input id="checkall-agree" type="checkbox" 
          v-model="localData.checkAll" @change="onClickAllCheckTerms"/>
        <label for="checkall-agree" class="checkbox-label">{{title}}</label>
      </div>
    </div>
    <div class="agree-content">
      <div v-for="(item) of localData.terms" :key="item.localeTermsCd"
        class="item">
        <div class="checkbox">
          <input :id="'agree-'+item.termsCd" 
            v-model="item.value"
						@change="onClickEachCheckTerms(item)" 
            name="chk-checkall-agree" type="checkbox" />
          <label :for="'agree-'+item.termsCd" class="checkbox-label">{{item.title}}</label>
        </div>
        <a @click="onClickTermsModal(item.termsCd)" href="javascript:;" class="btn-agree">보기</a>
      </div>
    </div>
    
    <div ref="agreeTermsModal" class="modal for-agree">
      <div class="layer">
        <div class="layer-content">
          <div class="modal-inner modal-lg">
            <div class="modal-content">
              <div class="modal-header">
                <div @click="onClickTermsClose" class="modal-close"></div>
                <div class="title">{{ termsModal.title }}</div>
              </div>
              <div class="modal-body">
                <div class="agree-box scroller">
                  <div class="article" v-html="termsModal.content"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import { getTermList } from '@/api/coching/comm/terms';

const DEFAULT_TERM_INFO = {
	termsCd : "" //ex) 001 
	, localeTermsCd	: "" //로케일용
	, requiredYn : "Y"	 //필수여부 Y/N
	, title: ""	 //ex) 서비스 이용약관
	, content: ""	// 내용
}

const DEF_TERMS = {
	title : "",
	termsCd: "TERMS_01",
	content: "",
}

export default {
    name: 'coching-membInfo',
    mixins: [ernsUtils],
    components: {
    },
    props: {
      useAllCheckbox : {
        type : Boolean,
        default : false
      },
      title : {
        type : String,
        default : '약관동의'
      },
      termsData:{
			type : Array,
      default: () =>{return [];}
      },
    },
    watch: {
      '$i18n.locale' : function(){
        const _vm = this;
        _vm.$nextTick(() => _vm.$refs.memberInfoForm.validate());
      },
    },
    data() {
        return {
          rawTerms : [],

          localData : {
            checkAll : false,
            terms : []
          },

          termsModal:{
            ...DEF_TERMS
          },
        }
    },
  async mounted(){
    const _vm = this;

    _vm.init();
  },
	beforeDestroy() {
    const _vm = this;
	},
  methods: {
    
    async init(){
      const _vm = this;
      if(_vm.termsData.length > 0){
        _vm.localData.terms = JSON.parse(JSON.stringify(_vm.termsData)); //Deep copy
        _vm.localData.terms = _vm.localData.terms.map((item)=>{
          return {...DEFAULT_TERM_INFO, ...item};
        });

        await _vm.loadTerms();
      }
    },

    //약관로드
    async loadTerms() {
      const _vm = this;

			_vm.loading(true);
      try {
				const res = await getTermList({
          locale: _vm.$i18n.locale
          , localeTermsCds : _vm.localData.terms.map((item)=>item.localeTermsCd)
        });
				const resData = res.resultData;
				
				_vm.rawTerms = resData;				
				_vm.localData.terms = _vm.localData.terms.map((item)=>{
					const rawTermItem = _vm.rawTerms.list.find(elem=>elem.etc1 == item.localeTermsCd);
					if(!rawTermItem){
						return item;	//못찾은 경우
					}

					const retVal = {
						...item
						, ...rawTermItem
						, value : false
						, title : rawTermItem.codeName												
					};

					const paramItem = _vm.termsData.find(elem=>elem.localeTermsCd == item.localeTermsCd);
					if(paramItem){
						//부모에게서 넘어온 값이 우선인것
						if(paramItem.hasOwnProperty('value')){
							retVal.value = paramItem.value;
						}

						if(paramItem.hasOwnProperty('title')){
							retVal.title = paramItem.title;
						}

						if(paramItem.hasOwnProperty('requiredYn')){
							retVal.requiredYn = paramItem.requiredYn;
						}
					}

					return retVal;
				});
				
      } catch(err) {
        _vm.defaultApiErrorHandler(err);
      } finally {
        _vm.loading(false);
      }
    },

		//입력값이 유효한지 판단
		async isValid(){
      const _vm = this;

			let retVal = true;
      for(const item of _vm.localData.terms){        
				if("Y" == item.requiredYn && !item.value){
					retVal = false;
					break;
				}
      }

			return retVal;
    },

		//선택된 값 리턴
		async getData(){
      const _vm = this;

			const retVal = _vm.localData.terms.map(item=>{
				return {
					termsCd : item.termsCd
					, localeTermsCd : item.localeTermsCd
					, requiredYn : item.requiredYn
					, title: item.title
					, value : item.value
				}
			});
      
			return retVal;
    },

		//전체 동의
    async onClickAllCheckTerms(){
      const _vm = this;

      const ck = _vm.localData.checkAll;
      for(const item of _vm.localData.terms){        
        item.value = ck;
      }

			//부모에게 onChange 전달
			_vm.$emit('change',{
				valid : await _vm.isValid()
				, data : await _vm.getData()
			});			
    },

		//개별 약관 클릭
    async onClickEachCheckTerms(termObj){
      const _vm = this;

      let allCheck = true;
			for(const item of _vm.localData.terms){ 
				if(!item.value){
					allCheck = false;
          break;
				}
      }

     	_vm.localData.checkAll = allCheck;

			//부모에게 onChange 전달
			_vm.$emit('change',{
				valid : await _vm.isValid()
				, data : await _vm.getData()
			});
    },

    onClickTermsModal(termsCd){
      const _vm = this;

      _vm.termsModal = _vm.localData.terms.find(elem=>elem.termsCd == termsCd);

      $(_vm.$refs["agreeTermsModal"]).fadeIn(300, function() {
        // 모달창 스크롤 적용
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

        // 뒷 페이지 스크롤 막기
        $("body").css("overflow", "hidden");
      });   
    },

    //약관 닫기
    onClickTermsClose(){
      const _vm = this;

      $(_vm.$refs["agreeTermsModal"]).fadeOut(300);
      $("body").css("overflow", "visible");
    },
  }  
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">

</style>