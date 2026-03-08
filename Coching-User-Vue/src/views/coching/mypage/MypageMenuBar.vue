<template>
  <!--content-title-wrap-->
  <div class="title-wrap">


    <div class="h1 text-center m-none">
        마이페이지
    </div>
    <div class="h1 text-center pc-none">
      {{ menuData.activeMenu.codeName }}
    </div>

    <div class="link-box-wrap m-none">
      <button v-for="(item, idx) of menuData.list" :key="idx"
        @click="onClickMenu(item)"
        :class="[
          'btn',
          'btn-sm',
          menuData.activeMenu.router == item.router ? 'btn-bk' : 'btn-gray'
        ]" 
        type="button">
        {{ item.codeName }}
      </button>
    </div>
  </div>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';

export default {
  name: 'MypageTabNav',
  mixins: [ernsUtils],
  computed: {
    
  },
  watch: {
    '$route': 'fetchData' //라우트가 변경되면 메소드를 다시 호출됩니다.    
  },
  props:{},
  async mounted(){
    const _vm = this;

    _vm.fetchData();    
  },
  data(){
    const _vm = this;
    const DEF_MYPAGE_MENU = [
      {codeName: _vm.$tt('test001') || '내가 쓴 글', router: 'coching-mypage-myWrite'},
      {codeName: _vm.$tt('test001') || '개인 정보',  router: 'coching-mypage-myInfo'},
    ];

    return {
      menuData: {
        activeMenu: { ...DEF_MYPAGE_MENU[0] },
        list: [
          ...DEF_MYPAGE_MENU
        ],
      }
    }
  },
  methods : {
    async fetchData(){
      const _vm = this;

      _vm.loading(true);

      try{
        await _vm.loadCodes();
        
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },

    async loadCodes(){
      const _vm = this;

      const curRouteName = _vm.$route.name;
      const activeMenu =_vm.menuData.list.find(menu=> curRouteName.indexOf(menu.router) == 0);
      _vm.menuData.activeMenu = {...activeMenu};
    },

    onClickMenu(item){
      const _vm = this;

      const router = `${item.router}`;
      _vm.ermPushPage({name: router, params:{}});
    },
  }
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">
</style>
