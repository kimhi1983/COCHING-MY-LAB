<template>
 <!--footer-->
  <footer>
    <div class="inner">
      <div class="container">
        <!--top-->
        <div class="top">
          <div class="left">
            <div class="logo">
              <img src="@/assets/images/logo-gray.svg" alt="COCHING" />
            </div>
          </div>
          <div class="right">
            <a @click="onClickTerms(localeTermsCds[1])" href="javascript:;">{{ $t('') || '개인정보처리방침'}}</a>
            <a @click="onClickTerms(localeTermsCds[0])" href="javascript:;">{{ $t('') || '이용약관'}}</a>
          </div>
        </div>
        <!--bottom-->
        <div class="bottom">
          <div class="left">
            <!-- <ul>
              <li>{{ $t('') || '경기도 광명시 일직로 43, GIDC A2 602호'}}</li>
            </ul> -->
                <ul>
                  <li>COCHING Innovation 코칭이노베이션</li>
                </ul>
            <ul>
              <li>{{ $t('') || '고객센터 : 070-4323-2600'}}</li>
              <li>{{ $t('') || '의견제안 : coching@coching.co.kr'}}</li>
              <li>{{ $t('') || '사업자 등록번호 : 170-09-03221'}}</li>
              <!-- <li>{{ $t('') || '대표 : 오원석'}}</li> -->
              <!-- <li>{{ $t('') || '사업자등록번호 : 316-81-29819'}}</li> -->
              <!-- <li>{{ $t('') || '메일 : coching@coching.co.kr'}}</li> -->
              <!-- <li>{{ $t('') || '통신판매업 신고번호 : 2000-광명일직A-0000호'}}</li> -->
              <!-- <a href="javascript:;" class="btn-info">{{ $t('') || '사업자 정보'}}</a> -->
            </ul>
          </div>
          <div class="right">© COCHING Innovation All rights reserved.</div>
        </div>
      </div>

      <!-- 약관 modal -->
      <div ref="termsModal" class="modal">
       <ModalLayout :classOption="classOption">
        <!-- Header Content -->
          <template v-slot:header>
            <div @click="onClickTermsClose" class="modal-close"></div>
            <div class="title">{{termsModal.title}}</div>
          </template>

          <!-- Body Content -->
          <template v-slot:body>
            <div class="agree-box scroller">
              <div class="article" v-html="termsModal.content">
              </div>
            </div>
          </template>
       </ModalLayout>
       </div>
    </div>

  </footer>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import {getTermList} from '@/api/coching/comm/terms';
import ModalLayout from '@/components/dialog/ModalLayout.vue';

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
  components:{
    ModalLayout
  },
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
      classOption : 'modal-lg',
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

  }
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">
</style>
