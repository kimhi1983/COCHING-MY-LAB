<template>

    <div class="container sm h-100">
      <div class="error-wrap system">
        <div class="img">
          <div class="error"></div>
          <LottieAnimation 
            :animation-data="json"
            :auto-play="true"
            :loop="true"
            :speed="1"
            ref="system" />
        </div>
        <div class="title-wrap">
          <div class="title">시스템 <span class="text-primary">점검 중</span>입니다.</div>
          <div class="date">2024/09/20 12:00 ~ 2024/09/21 13:00</div>
          <div class="info">
            서비스 이용에 불편을 드려 죄송합니다.<br />
            보다 나은 서비스를 위해서 점검 작업을 진행합니다.
          </div>
        </div>
      </div>
    </div>

</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import LottieAnimation from "@/components/LottieAnimation"; 
import systemJson from "@/assets/js/system.json";

export default {
  name: 'coching-Error',
  mixins: [ernsUtils],
  components: {
    LottieAnimation,
  },
  data() {
    return {
      json: systemJson,
    }
  },
  computed: {
  },

  methods:{

  }
}
</script>

<style lang="scss">

</style>
