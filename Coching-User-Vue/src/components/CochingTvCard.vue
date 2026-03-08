<template>
  <div class="item">
     <div class="inner" @click="onCardClick">
       <div class="thum">
         <img v-if="useThumbnail" :src="thumbnailUrl" :alt="tvInfo.title"/>
         <iframe 
           v-else
           @click.stop
           :src="embedUrl"
           :title="tvInfo.title" 
           frameborder="0" 
           allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
           referrerpolicy="strict-origin-when-cross-origin" 
           allowfullscreen>
         </iframe>
       </div>
       <div class="content-wrap">
        <div class="title-wrap">
          <div class="title">{{tvInfo.title}}</div>
          <div class="date">
            <span>조회수 {{(tvInfo.views || '0') | eFmtNum }}회</span>
            <span>{{tvInfo.ytDttm | eFmtDate('.')}}</span>
          </div>
        </div>
      </div>
    </div>
    <!-- TODO : 추후 지정-->
    <div v-show="false" class="hash-tag">
      <a href="javascript:;">#신원료</a>
      <a href="javascript:;">#신원료</a>
      <a href="javascript:;">#신원료</a>
      <a href="javascript:;">#신원료</a>
    </div>
  </div>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';

export default {
  name: 'coching-tv-card',
  mixins: [ernsUtils],
  components: {},
  props: {
    tvInfo: {
      type: Object, // 전달받는 데이터 타입
      required: true // 필수 값
    },
    useThumbnail: {
      type: Boolean,
      default: false // 기본값은 iframe 사용
    }
  },
  computed: {
    embedUrl() {
      const url = this.tvInfo.ytUrl;
      const match = url.match(/(?:youtu\.be\/|v=)([\w-]+)/);
      if (match && match[1]) {
        return `https://www.youtube.com/embed/${match[1]}`;
      }
      return null;
    },
    thumbnailUrl() {
      const url = this.tvInfo.ytUrl;
      const match = url.match(/(?:youtu\.be\/|v=)([\w-]+)/);
      if (match && match[1]) {
        return `https://img.youtube.com/vi/${match[1]}/hqdefault.jpg`;
      }
      return null;
    }
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
      imageStyle : {} // 동적으로 설정할 스타일
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
    //fetchData
    async fetchData(){
      const _vm = this;
    },    

    async loadCodes(){
      const _vm = this;
     
    },

    docReady(){
      const _vm = this;
    },

    onCardClick(){
      const _vm = this;
      
      const tvSeq = _vm.tvInfo.tvSeq;

      const routeParam = {
        tvSeq: tvSeq,
      };

      _vm.ermPushPage({
        name: 'coching-tv-detail', 
        query: routeParam,
      });
    }
  }  
}
</script>

<style lang="scss" scoped>
.inner {
  cursor: pointer !important;
}

.aspect-ratio-container {
  overflow: hidden;
  position: relative;

}

.aspect-ratio-container img {
  display: block;
  object-fit: cover; /* 비율 유지 및 초과 부분 잘림 */
}
</style>

<style lang="scss">

</style>