<template>
  <nav class="scroller m-none" :class="{'active': isSmallGnb}">
    <div class="lg">
      <BasicGnb />
      <BottomFooter />
    </div>
    <!--sm-->
    <div class="sm">
      <a @click="onClickHome" :class="{ 'active' : menuType == 'HOME'}"
        href="javascript:;" class="ic-home-lg">홈</a>
      <a @click="onClickTv" :class="{ 'active' : menuType == 'TV'}" 
        href="javascript:;" class="ic-tv-lg">코칭 TV</a> 
      <a @click="onClickNoti" :class="{ 'active' : menuType == 'NOTI'}" 
        href="javascript:;" class="ic-noti-lg">알림함</a> 
      <a @click="onClickSourcing" :class="{ 'active' : menuType == 'SOURCING'}"
        href="javascript:;" class="ic-drop-lg">원료소싱</a>
      <a @click="onClickNotice" :class="{ 'active' : menuType == 'NOTICE'}"
        href="javascript:;" class="ic-speaker-lg">공지사항</a>
      <a @click="onClickInquiry" :class="{ 'active' : menuType == 'INQR'}"
        href="javascript:;" class="ic-chats-lg">문의하기</a>
    </div>
  </nav>
</template>

<script>

import ernsUtils from '@/components/mixins/ernsUtils'; 
import BasicGnb from './BasicGnb.vue';
import BottomFooter from './BottomFooter.vue';

export default {
  name: 'BasicNav',
  mixins: [ernsUtils],
  components: {
    BasicGnb,
    BottomFooter
  },
  computed: {
    isSmallGnb() {
      const _vm = this;
      return _vm.$store.state.coching.isSmallGnb;
    },
    menuType(){
      const _vm = this;
      const menuType = _vm.$route.meta.menuType;
      let ret = '';
      if(!menuType){
        return 'HOME';
      }

      ret = menuType;

      return ret;
    },  
  }
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">
</style>
