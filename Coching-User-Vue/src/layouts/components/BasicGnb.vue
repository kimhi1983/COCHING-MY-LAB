<template>
  <div class="gnb">
    <!--group-->
    <div class="group">
      <a @click="onClickHome" :class="{ 'active' : menuType == 'HOME'}"
        href="javascript:;" class="ic-home-lg">홈</a>
      <a @click="onClickTv" :class="{ 'active' : menuType == 'TV'}"
        href="javascript:;" class="ic-tv-lg">코칭 TV</a>
      <!-- <a @click="onClickForeignPtn" :class="{ 'active' : menuType == 'FOREIGNPTN'}"
        href="javascript:;" class="ic-partner-lg">해외 원료사</a> -->
    </div>
    <!--group-->
    <div class="group gnb-acd"
      :class="{ 'active' : menuGroup.NOTI.active }"      
    >
      <div class="title" 
        @click="menuGroup.NOTI.active = !menuGroup.NOTI.active"
      >알림함</div>
      <div class="gnb-wrap">
        <a @click="onClickNoti" :class="{ 'active' : menuType == 'NOTI'}"
          href="javascript:;" class="ic-noti-lg">알림함</a>
        <a @click="onClickRequest" :class="{ 'active' : menuType == 'REQUEST'}"
          href="javascript:;" class="ic-list-lg">원료자료요청</a>
        <a @click="onClickMessage" :class="{ 'active' : menuType == 'MESSAGE'}"
          href="javascript:;" class="ic-message-lg">쪽지함</a>
      </div>
    </div>

    <!--group-->
    <div class="group">
      <a @click="onClickSourcing" :class="{ 'active' : menuType == 'SOURCING'}"
        href="javascript:;" class="ic-drop-lg">원료소싱</a>
      <a @click="onClickNotice" :class="{ 'active' : menuType == 'NOTICE'}"
        href="javascript:;" class="ic-speaker-lg">공지사항</a>
      <a @click="onClickWeeklyNews" :class="{ 'active' : menuType == 'WEEKLYNEWS'}"
        href="javascript:;" class="ic-news-lg">Weekly News</a>
      <a @click="onClickInquiry" :class="{ 'active' : menuType == 'INQR'}"
        href="javascript:;" class="ic-chats-lg">1:1 문의하기</a>
      <!-- 임시 가림처리 -->
      <!-- <a @click="onClickUserManual" :class="{ 'active' : menuType == 'MANUAL'}"
        href="javascript:;" class="ic-book-lg">사용자 매뉴얼</a> -->
    </div>
  </div>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';

export default {
  name: 'BasicGnb',
  mixins: [ernsUtils],
  computed: {
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
  },
  data(){
    return {
      menuGroup : {
        NOTI : {
          active : true,
        },
      }
    }
  },
  methods: {
    
  },
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">
</style>
