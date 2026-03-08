<template>
<!--footer-->
  <div class="footer">
    <div class="top">
      <div @click="onClickTerms(localeTermsCds[0])">이용약관</div>
      <div @click="onClickTerms(localeTermsCds[1])">개인정보처리방침</div>
    </div>
    <div class="info">
      <ul>
        <li>COCHING Innovation 코칭이노베이션</li>
      </ul>
      <ul>
        <li>고객센터 : 070-4323-2600</li>
        <li>의견제안 : coching@coching.co.kr</li>
        <li>사업자 등록번호 : 170-09-03221</li>
      </ul>
    </div>
    <div class="info">© COCHING All rights reserved.</div>

    <!--이용약관-->
    <div ref="termsModal" class="modal for-agree">
      <div class="layer">
        <div class="layer-content">
          <div class="modal-inner modal-lg">
            <div class="modal-content">
              <div class="modal-header">
                <div @click="onClickTermsClose" class="modal-close"></div>
                <div class="title">이용약관</div>
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
 <!--footer-->
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import {getTermList} from '@/api/coching/comm/terms';

const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;


const DEF_TERMS = {
	title : "",
	termsCd: "TERMS_01",
	content: "",
}

//이용약관
const TERMS_001 = "001";

//개인정보처리방침
const TERMS_999 = "002";

export default {
  name: 'BottomTabNav',
  mixins: [ernsUtils],
  computed: {
    
  },
  watch: {
    '$route': 'fetchData' //라우트가 변경되면 메소드를 다시 호출됩니다.
    /*locale 변경감지*/
    ,'$i18n.locale' : function(){this.isLoadTerms=false;} //다시로드하도록 처리
  },
  mounted(){
    const _vm = this;

    _vm.fetchData();    
  },
  data(){
    return {
      navInfo:{
        curPath : ""        
      },

      isLoadTerms : false,
      localeTermsCds : [TERMS_001, TERMS_999],

      term001:{
				...DEF_TERMS
				, title : ""
				, termsCd : TERMS_001
			},
			term999:{
				...DEF_TERMS
				, title : ""
				, termsCd : TERMS_999
			},

      termsModal:{
        ...DEF_TERMS
      }
    }
  },
  methods : {
    //회사소개 클릭
    async onClickIntroduceComp(){
      //const _vm = this;

      window.open('https://www.kpros.kr', '_blank');      
    },

    //약관 클릭
    async onClickTerms(termsCd){
      const _vm = this;

      if(!_vm.isLoadTerms){
        await _vm.loadTerms();
      }

      switch(termsCd){
        case TERMS_001:
          _vm.termsModal = {..._vm.term001};
          break;

        case TERMS_999:
        default:          
          _vm.termsModal = {..._vm.term999};
          break;
      }
      
      console.debug(_vm.$refs["termsModal"]);
      //$(_vm.$refs["termsModal"]).addClass("on");     
      $(_vm.$refs["termsModal"]).fadeIn(300, function() {
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

      $(_vm.$refs["termsModal"]).fadeOut(300);
      $("body").css("overflow", "visible");
    },

    //약관로드
    async loadTerms() {
      const _vm = this;

			// _vm.loading(true);
      try {
				const res = await getTermList({
          locale: _vm.$i18n.locale
          , localeTermsCds : [TERMS_001, TERMS_999]
        });
				const resData = res.resultData;
				
				_vm.rawTerms = resData;
				const term001Data = _vm.rawTerms.list.find(elem=>elem.etc1 == TERMS_001);
				const term999Data = _vm.rawTerms.list.find(elem=>elem.etc1 == TERMS_999);

				_vm.term001 = {
					..._vm.term001
					, ...term001Data
          , title : term001Data.codeName
				};

				_vm.term999 = {
					..._vm.term999
          , ...term999Data
          , title : term999Data.codeName
				};

        _vm.isLoadTerms = true;

      } catch(err) {
        _vm.defaultApiErrorHandler(err);
      } finally {
        // _vm.loading(false);
      }
    },

    fetchData(){
      const _vm = this;

      const curRoute = _vm.$router.currentRoute;
      const meta = curRoute.meta;

      _vm.navInfo.curPath = curRoute.path;

      //lazy loading 으로 변경
      //_vm.loadTerms();
    },

    onClickNavHome(){
      const _vm = this;
      _vm.ermReplacePage({name:'coching-main'});
    },
  }
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">
</style>
