<template>
  
  <div class="container sm h-100">
      <div class="error-wrap">
        <div class="img">
          <div class="error"></div>
          <LottieAnimation 
            :animation-data="errorJson"
            :auto-play="true"
            :loop="true"
            :speed="1"
            ref="error" />
        </div>
        <div class="title-wrap">
          <div class="title">만료된 페이지 입니다.</div>
          <div class="info">제공된 주소 경로가 만료되었습니다.</div>
        </div>
        <div class="btn-area">
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
      _vm.ermReplacePage({name:'coching-raw-main'});
    },
  }
}
</script>

<style lang="scss">

</style>
