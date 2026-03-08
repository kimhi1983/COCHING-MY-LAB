<template>
  <!--title-wrap-->
  <div class="title-wrap">
    <!-- 제목 조건 분기
    <div class="h1 text-center m-none">
      <span v-if="boardMode === 'write'">
        {{ menuData.activeMenu.codeName }}
      </span>
      <span v-else>
        이용정보 & 커뮤니티
      </span>
    </div>
    <div class="h1 text-center pc-none">
      {{ menuData.activeMenu.codeName }}
    </div>
     -->
    <div class="h1 text-center">
      {{ menuData.activeMenu.codeName }}
    </div>

    <!-- 메뉴 조건 분기 -->
    <div v-if="boardMode !== 'write'" class="link-box-wrap m-none">
      <button
        v-for="(item, idx) of menuData.list"
        :key="idx"
        @click="onClickMenu(item)"
        :class="[
          'btn',
          'btn-sm',
          menuData.activeMenu.router == item.router ? 'btn-bk' : 'btn-gray'
        ]"
        type="button"
      >
        {{ item.codeName }}
      </button>
    </div>
  </div>

</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import { BOARD_MODE } from '@/constants/board';

export default {
  name: 'BoardTabNav',
  mixins: [ernsUtils],
  computed: {
    
  },
  watch: {
    '$route': 'fetchData' //라우트가 변경되면 메소드를 다시 호출됩니다.    
  },
  props:{
    boardMode: {
      type: String,
      required: true,
    },
  },
  async mounted(){
    const _vm = this;

    _vm.fetchData();    
  },
  data(){
    const _vm = this;
    const DEF_BOARD_MENU = [
      {codeName: _vm.$tt('test001') || '원료소싱', router: 'coching-rawSourcing-board'},
      {codeName: _vm.$tt('test001') || '공지사항', router: 'coching-notice-board'},
      {codeName: _vm.$tt('test001') || 'Weekly News', router: 'coching-weeklyNews-board'},
      {codeName: _vm.$tt('test001') || '1:1 문의하기', router: 'coching-inqr-board'},
      // {codeName: _vm.$tt('test001') || 'FAQ'    , router: 'coching-faq-board'},
    ];

    return {
      menuData: {
        activeMenu: { ...DEF_BOARD_MENU[0] },
        list: [
          ...DEF_BOARD_MENU
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

      const router = `${item.router}-${BOARD_MODE.LIST}`;
      _vm.ermPushPage({name: router, params:{}});
    },
  }
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">
</style>
