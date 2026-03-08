<template>
  <!--section-->
  <section id="coching-main">
    <!--visual-->
    <div class="visual">
      <div class="title-wrap">
        <div class="title">
          새로운 원료를 소개하는 설레이는 경험을<br />
          COCHING에서 경험하세요
        </div>
        <div class="info">원료사와 연구원이 비대면으로 만나보세요~</div>
      </div>
    </div>

    <!--main-content-->
    <div class="main-content">
      <div class="container">
        <div class="img-box">
          <div class="item">
            <div class="img">
              <img src="@/assets/images/img-test-01.jpg" alt="" />
            </div>
            <div class="title-wrap">
              <div class="title">Lorem ipsum dolor sit amet</div>
              <div class="info">
                Lorem ipsum dolor sit amet consectetur. Scelerisque pellentesque mauris leo in sed arcu justo sed et. Tellus id platea varius vitae
                condimentum velit fringilla sociis.
              </div>
            </div>
          </div>
          <div class="item">
            <div class="img">
              <img src="@/assets/images/img-test-02.jpg" alt="" />
            </div>
            <div class="title-wrap">
              <div class="title">Lorem ipsum dolor sit amet</div>
              <div class="info">
                Lorem ipsum dolor sit amet consectetur. Scelerisque pellentesque mauris leo in sed arcu justo sed et. Tellus id platea varius vitae
                condimentum velit fringilla sociis.
              </div>
            </div>
          </div>
          <div class="item">
            <div class="img">
              <img src="@/assets/images/img-test-03.jpg" alt="" />
            </div>
            <div class="title-wrap">
              <div class="title">Lorem ipsum dolor sit amet</div>
              <div class="info">
                Lorem ipsum dolor sit amet consectetur. Scelerisque pellentesque mauris leo in sed arcu justo sed et. Tellus id platea varius vitae
                condimentum velit fringilla sociis.
              </div>
            </div>
          </div>
        </div>
        <div class="img-box lg">
          <div class="item">
            <div class="img">
              <img src="@/assets/images/img-test-04.jpg" alt="" />
            </div>
          </div>
        </div>
        <div class="img-box">
          <div class="title-box">
            <div class="title">Lorem ipsum dolor sit amet</div>
            <div class="info">
              Lorem ipsum dolor sit amet consectetur. Sit bibendum nam sed eu justo amet id. Pellentesque enim in erat fermentum pharetra neque. Ut
              malesuada pretium id euismod fermentum donec nunc quisque. Nisi dictumst tristique ipsum tortor. Tincidunt nisl nisl eget leo. Quis viverra
              gravida at nulla id amet sed sem. Gravida sit magna enim hendrerit in tellus viverra tempor pulvinar. Non pretium tempus tellus sem ut ut
              laoreet tincidunt in.Et ultrices mauris consectetur mauris aliquam dolor cursus. Sed scelerisque placerat euismod ut dapibus. Non tempus urna
              faucibus nec sed arcu tempor. Varius leo feugiat ullamcorper sagittis vel ut morbi congue. Pellentesque mollis risus vel sollicitudin nunc
              egestas quam. Mauris ac faucibus semper sed et diam sem.
            </div>
          </div>
        </div>
      </div>

      <!--오류-->
      <AlertModal ref="alertModal"></AlertModal>
      <ConfirmModal ref="confirmModal"></ConfirmModal>

      <!-- <div>
      다국어 테스트 : {{ $t('button.add') }}<br/>
      다국어 테스트 : {{ $t('button.delete') }}<br/>
      </div> -->
    </div>
    
  </section>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import Promise from "bluebird";

import { isUserLoggedIn, getUserData, getHomeRouteForLoggedInUser } from '@/auth/utils';
import useJwt from '@/auth/jwt/useJwt';



const DEF_USERINFO = {
  email: '',
  userPw: '',
  remainLogin : false,
}

export default {
  name: 'coching-main',
  mixins: [ernsUtils],
  components : {
  },
  computed : {

  },
  watch:{
    '$i18n.locale' : async function(){
      const _vm = this;
      _vm.$nextTick(() => _vm.$refs.loginForm.validate());
    },
  },
  props: {},
  data(){
    return {
      //로그인
      status: '',
      userInfo : {...DEF_USERINFO},
      isLoggedIn : false,

    }
  },
  mounted(){
    const _vm = this;

    _vm.docReady();
    _vm.fetchData();   

  },
  beforeDestroy(){
    const _vm = this;
	},
  methods: {
    async loadDataAll(){
      const _vm = this;

      // 로딩 작업 정의
      const dataLoadJobs = [
        // _vm.loadPopup(false),    //팝업
        // _vm.loadBanner(false),  //배너
      ];

      const loadErrors = await Promise.all(dataLoadJobs);
      //TODO : 모두 작업 끝났는데 오류 날경우.      
      //loadErrors
    },

    //데이터 가져오는 부분(사용하지 필요없어도 반드시 선언)
    async fetchData(){
      const _vm = this;

      _vm.isLoggedIn = (isUserLoggedIn() || null) ? true : false;
      
      _vm.loading(true);
      try{
        await _vm.loadDataAll();
      }catch(err){
        console.error(err);
      }finally{
        _vm.loading(false);
      }
    },

    docReady(){
      const _vm = this;
      
    },
  }  
}
</script>

<style lang="scss" scoped>
.wrap>section {
  .modal.modal-banner {
    display: none;
  }

  .modal.modal-banner.block {
    display: block;
  }
}

.swiper-button-next, .swiper-container-rtl .swiper-button-prev {
    background-image: none;
    
}

.swiper-button-prev, .swiper-container-rtl .swiper-button-next {
    background-image: none;
}

</style>

<style lang="scss">
#coching-main {

  
  .swiper-pagination-bullet.swiper-pagination-bullet-active {
      background: #222 !important;
  }
}
</style>
