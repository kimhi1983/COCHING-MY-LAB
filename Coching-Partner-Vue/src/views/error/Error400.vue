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
          <div class="title">잘 못된 접근입니다.</div>
          <div class="info">정상적인 방법으로 접근하지 않았습니다.</div>
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
