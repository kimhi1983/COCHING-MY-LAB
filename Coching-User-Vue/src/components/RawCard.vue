<template>
  <div class="item">
    <div class="inner" @click="onClickCard()">
      <div class="thum">
        <img v-if="rawInfo.thumbnailId"
            :src="eumFileImagePath(rawInfo.thumbnailId, '0', errorImg, '0')"
            @error="onImageErrorRaw"
            :alt="rawInfo.rawName"/> <!-- 원료사 이미지 : 이미지는 캐시처리-->
        <img v-else 
          src="@/assets/images/raw_thumb_noImg.jpg" alt="img" />
      </div>
      <div class="content-wrap">
        <div class="profile">
          <img v-if="rawInfo.ptnMembProfileId"
            :src="eumFileImagePath(rawInfo.ptnMembProfileId, '0', errorImg, '0')"
            @error="onImageError"
            :alt="rawInfo.ptnName"/>      <!-- 원료사 프로필 : 이미지는 캐시처리-->
          <img v-else
             src="@/assets/images/profile_noImg.png" alt="profile" />
        </div>
        <div class="title-wrap">
          <div class="title">{{rawInfo.rawName}}</div>
          <div class="info">
            <span>{{rawInfo.ptnName || '-'}}</span><!--원료사-->
            <span>{{rawInfo.prodCompany || '-'}}</span><!--제조사-->            
          </div>
          <!-- <div class="date">
            <span>조회수 {{(rawInfo.viewSumCnt || '0') | fmtViews }}</span>
          </div> -->
        </div>
      </div>
      <div class="hash-tag">
        <a v-for="(item, idx) of localData.hashTags" :key="idx"
          href="javascript:;" @click="onClickHashTag(item)">#{{item}}</a>        
      </div>
    </div>

    <!-- {{ rawInfo }} -->
    <!-- {{ rawInfo.rawTypeInfo.filter(item=>'001' == item.grpCode) }} -->
  </div>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';

import ERROR_IMG_RAW from '@/assets/images/raw_thumb_noImg.jpg';
import ERROR_IMG from '@/assets/images/profile_noImg.png';

export default {
  name: 'coching-raw-card',
  mixins: [ernsUtils],
  components: {},
  props: {
    rawInfo : {
      type : Object,
      default: () => ({}),
      //require : true
    }
  },
  computed: {
    errorImgRaw(){
      return ERROR_IMG_RAW;
    },
    errorImg(){
      return ERROR_IMG;
    }
  },
  watch: {
    '$i18n.locale' : async function(){
      const _vm = this;
      await _vm.loadCodes();
      _vm.$nextTick(() => {});
    },
    rawInfo(){
      const _vm = this;
      _vm.adjustHashTags();
    }
  },
  data() {
    return {
      localData:{
        hashTags:[],

        cate001:[],
        cate002:[],
        cate003:[],
        cate004:[],
        cate005:[],
      }
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
    onImageErrorRaw(event) {
      const targetObj = event.target;
      targetObj.src = this.errorImgRaw; // 대체 이미지로 설정      
    },

    onImageError(event) {
      const targetObj = event.target;
      targetObj.src = this.errorImg; // 대체 이미지로 설정      
    },

    //원료카드 클릭이벤트
    onClickCard(){
      const _vm = this;
      _vm.$emit("onClickCard", _vm.localData.rawInfo);
    },

    //hashtag 클릭
    onClickHashTag(hashtag){
      const _vm = this;
      _vm.$emit("onClickHashTag", `#${hashtag}`);
    },

    //hashtag 정리
    adjustHashTags(){
      const _vm = this;

      const rawHashTags = [];
      const detailList = _vm.rawInfo.rawDetailInfo || [];
      for(const dItem of detailList){
        const strHashTags = dItem.hashtag || '';
        rawHashTags.push(...strHashTags.split('#'));
      }

      //console.debug(rawHashTags);
      _vm.localData.hashTags = 
        _vm.cleanArrayWithReduce(
          rawHashTags.filter(tag=>{
            return !('#' == tag || '' == tag);
          })
        );
    },

    //fetchData
    async fetchData(){
      const _vm = this;

      _vm.loading(true);
      try{
        _vm.localData.cate001 = (_vm.rawInfo.rawTypeInfo || []).filter(item=>'001' == item.grpCode);
        _vm.localData.cate002 = (_vm.rawInfo.rawTypeInfo || []).filter(item=>'002' == item.grpCode);
        _vm.localData.cate003 = (_vm.rawInfo.rawTypeInfo || []).filter(item=>'003' == item.grpCode);
        _vm.localData.cate004 = (_vm.rawInfo.rawTypeInfo || []).filter(item=>'004' == item.grpCode);
        _vm.localData.cate005 = (_vm.rawInfo.rawTypeInfo || []).filter(item=>'005' == item.grpCode);
        _vm.adjustHashTags();
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },    

    async loadCodes(){
      const _vm = this;
     
    },

    docReady(){
      const _vm = this;   

    },
  }  
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">
</style>