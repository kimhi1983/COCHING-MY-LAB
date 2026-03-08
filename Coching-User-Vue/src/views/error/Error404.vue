<template>

    <div class="container sm h-100">
      <div class="error-wrap">
        <div class="img">
          <div class="error"></div>
          <LottieAnimation 
            :animation-data="json"
            :auto-play="true"
            :loop="true"
            :speed="1"
            ref="error" />
        </div>
        <div class="title-wrap">
          <div class="title">페이지를 찾을 수 없습니다.</div>
          <div class="info">접근 주소 경로 오류 혹은 제공하지 않는 페이지입니다.</div>
        </div>
        <div class="btn-area">
          <button
          @click="ermHistoryBack" 
           type="button" class="btn btn-lg btn-primary-outline">이전페이지</button>
          <button 
          @click="onClickHome"
           type="button" class="btn btn-lg btn-primary">홈으로</button>
        </div>
      </div>
    </div>
  
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import LottieAnimation from "@/components/LottieAnimation"; 
import errorJson from "@/assets/js/error.json";

export default {
  name: 'coching-Error',
  mixins: [ernsUtils],
  components: {
    LottieAnimation,
  },
  data() {
    return {
        json: errorJson,
    }
  },
  computed: {
  },
  methods:{
    onClickHome(){
      const _vm = this;
      _vm.ermReplacePage({name:'coching-main'});
    },
  }
}
</script>

<style lang="scss">

</style>
