<template>
  <!--banner-wrap-->
  <!--배너는 2가지 타입 type-text / type-img-->
  <div class="banner-wrap scroller type-text m-scroll-hidden banner-m">
    <div v-for="(item, idx) of listData.list"
      :key="idx"
      @click="clickDoAction(item)" class="item">
      <div 
        v-if="item.file"
        class="img">
        <img
          :src="eumFileImagePath(item.file.fileId, '0', errorImg)"
          @error="onImageError"
          :alt="item.file.fileName"/>
      </div>
      <div class="text-wrap">
        <div class="title">{{item.title}}</div>
        <div class="info">{{item.content}}</div>
      </div>
    </div>
    <!--pagenation
    <Pagenation
      v-model="listData.pi.curPage"
      :totalRows="listData.pi.totalItem"
      :perPage="listData.pi.perPage"
      @input="onChangePage"
    ></Pagenation>
    -->
  </div>

  <!-- <div class="banner-wrap scroller type-img m-scroll-hidden">
    <div class="item"><img src="images/img-banner-box.png" alt="" /></div>
    <div class="item"><img src="images/img-banner-box.png" alt="" /></div>
    <div class="item"><img src="images/img-banner-box.png" alt="" /></div>
    <div class="item"><img src="images/img-banner-box.png" alt="" /></div>
    <div class="item"><img src="images/img-banner-box.png" alt="" /></div>
    <div class="item"><img src="images/img-banner-box.png" alt="" /></div>
  </div> -->
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';

//import Pagenation from '@/components/Pagenation.vue';

import ERROR_IMG from '@/assets/images/ic-img-lg.svg';

import {  
  getBannerAdList,
  setBannerAdClick
} from '@/api/coching/comm/banner';
import { BANNER_INFO, DEF_SEARCH_OPT } from '@/constants/banner';

export default {
    name: 'coching-advertise-card',
    mixins: [ernsUtils],
    components: {
      //Pagenation,
    },
    props: {
     
    },
    computed: {
      errorImg(){
        return ERROR_IMG;
      } 
    },
    watch: {
      '$i18n.locale' : async function(){
        const _vm = this;
        await _vm.loadCodes();
      },
      
    },
    data() {
        return {
          localInvalidState: false, //양식폼의 validate 상태
          status: '',
          CODES: {
            QP_004: [],
            QP_005: [],
          },
          localData: {
            
          },
          isChecked: false,
          toggle: false,
          panelMaxHeight: '34px',

          listData: {
            list: [],
            sc: { ...DEF_SEARCH_OPT.sc, bannerMstCd : BANNER_INFO.BANNER_0002.id },
            pi: { ...DEF_SEARCH_OPT.pi },
          },
        }
    },
  async mounted(){
    const _vm = this;

    _vm.docReady();
    _vm.fetchData();    

  },
	beforeDestroy() {
    const _vm = this;
	},
  methods: {

    onImageError(event) {
      const targetObj = event.target;
      targetObj.src = this.errorImg; // 대체 이미지로 설정
      targetObj.style.width = '100px'; // 원하는 너비 설정
      targetObj.style.height = '100px'; // 원하는 높이 설정
    },
    
    // 페이지 변경 
		onChangePage(pageNo) {
      const _vm = this;
      _vm.loadList(pageNo);
    },  

    //fetchData
    async fetchData(){
      const _vm = this;

      _vm.loading(true);
      try{
        await _vm.loadList(1);
        _vm.checkHasBanner(); //배너 유무 체크
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },    

    checkHasBanner() {
      const _vm = this;
      this.$emit('update-banner-status', this.listData.list.length > 0);
    },

    async loadCodes(){
      const _vm = this;
     
    },

    async loadList(pageNo) {
			const _vm = this;
      const listData = _vm.listData, pInfo = _vm.listData.pi, searchOp = _vm.listData.sc;
      pInfo.curPage = pageNo || 1;

			_vm.loading(true);
      try {
        const params = _vm.getListSearchParam();
        const res = await getBannerAdList(params);
        const resData = res.resultData;

        const pageInfo = resData.pageInfo;
        listData.pi = {
          ...listData.pi,
          curPage : pageInfo.currentPage,
          totalItem : pageInfo.totalItem,
          perPage : pageInfo.pageItemSize
        }

        listData.list = resData.list;
        
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
		},

    getListSearchParam() {
      const _vm = this;
      const pInfo = _vm.listData.pi, searchOp = _vm.listData.sc;
      
      return {
        page : pInfo.curPage,
        rowSize : pInfo.perPage,

        //배너 아이디
        bannerMstCd : searchOp.bannerMstCd,
        useYn : searchOp.useYn,
        delYn : searchOp.delYn,
        dispYn : searchOp.dispYn,
      }
    },

    //TODO 나중에 수정
    async clickDoAction(item){
      const _vm = this;
      let link = "";
      link = item.refUrl;

      if(!link) {
        return;
      }

      
      /*
      const params = {
        bannerSeq : bannerItem.bannerSeq
      };
      const res = await setBannerAdClick(params);
      const resData = res.resultData;
      if('TAESUNG' == val){
        link = "http://www.tschem.kr/";
      }else if('GOLDLEBEN' == val){
        link = "https://www.goldleben.com/kor/";
      }else if('TWIN' == val){
        link = "https://twinchem.net/";
      }else if('DONGJIN' == val){
        link = 'http://www.dongjinsc.com/index.php';
      }
      window.open(link, '_blank');
      */

      try {
        _vm.loading(true);

        //클릭 수 증가
        const params = {
          bannerMstCd : item.bannerMstCd,
					bannerSeq : item.bannerSeq
				};
        const res = await setBannerAdClick(params);
        const resData = res.resultData;

      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }

      _vm.eumLocationUrl(link);

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