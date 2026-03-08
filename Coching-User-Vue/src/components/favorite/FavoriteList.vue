<template>
<div class="board-wrap">
  <!--total-wrap-->
  <div class="total-wrap">
    <!--check-total-wrap-->
    <div class="check-total-wrap">
      <!--checkbox-->
      <div class="checkbox only">
        <input 
          :id="`favorite-${refCode}-checkall`" 
          type="checkbox" 
          v-model="checkedAll"
          @change="onClickToggleAll" />
        <label :for="`favorite-${refCode}-checkall`" class="checkbox-label"></label>
      </div>
      <div class="total-num">총<span>{{favoriteData.pi.totalItem}}</span></div>
    </div>
    <div class="right">
      <!--button-->
      <button type="button" 
        class="btn btn-sm btn-primary"
        @click="onClickRequestRawDoc">원료자료 요청</button>
      <button type="button" 
        class="btn btn-sm btn-gray-outline"
        @click="onClickDelete">삭제</button>
    </div>
  </div>
  <!--board-list-->
  <div class="board-list">
    <a href="javascript:;"
      v-for="(item, idx) of favoriteData.list" 
      :key="idx"
    >
      <!--checkbox-->
      <div class="checkbox only">
        <input 
          :id="`favorite-${refCode}-${item.refSeq}`" 
          type="checkbox" 
          v-model="item.checked"/>
        <label :for="`favorite-${refCode}-${item.refSeq}`" class="checkbox-label"></label>
      </div>
      <!--content-wrap-->
      <div class="content-wrap"
        @click="onClickItem(item)">
        <div class="title-wrap">
          <div class="title">
            {{ refCode == 'raw' 
              ? (`[${item.rawName}] ${item.title}` || '-') 
              : '-' 
            }}
          </div>
        </div>
        <!-- <div class="info"></div> -->
        <div class="date">
          <span>{{ refCode == 'raw' ? (item.managerName || '-') : '-' }}</span>
          <span>{{item.rgtDttm | eFmtDateFormat('YY.MM.DD HH:mm')}}</span>
        </div>
      </div>
    </a>
  </div>

  <div v-show="refCode == 'raw' && favoriteData.list.length <= 0"
    class="result-none empty-content">
    찜한 원료가 없습니다.
  </div>    

  <!--pagenation-->
  <Pagenation
    v-model="favoriteData.pi.curPage"
    :totalRows="favoriteData.pi.totalItem"
    :perPage="favoriteData.pi.perPage"
    @input="onChangePage"
  ></Pagenation>

  <!--오류-->
  <AlertModal ref="alertModal"></AlertModal>
  <ConfirmModal ref="confirmModal"></ConfirmModal>
  <RequestRawModal
    v-if="isLoggedIn"
    ref="requestRawModal"></RequestRawModal>
</div>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import favoriteMixin from './favoriteMixin';

import Pagenation from '@/components/Pagenation.vue';
import RequestRawModal from '@/components/modal/RequestRawModal.vue';

import { DEF_DATA, REF_CODES, DEF_FAVORITE_INF} from '@/constants/favorite';
import { getMyFavoriteList, removeMyFavorite } from '@/api/coching/member/favorite';
import { EventBus } from '@/components/eventBus'; 


export default {
  name: 'coching-favorite-list',
  mixins: [ernsUtils, favoriteMixin],
  components: {
    Pagenation,
    RequestRawModal,
  },
  computed: {},
  watch: {
    async refCode(newVal, oldVal) {
      const _vm = this;
      _vm.favoriteData.sc = { ...DEF_DATA.sc }; //검색조건 초기화
      await _vm.loadCodes();
      await _vm.fetchData();
    },
  },
  props:{
    refCode: {
      type: String,
      default: REF_CODES.RAW
    },
  },
  async mounted(){
    const _vm = this;

    await _vm.loadCodes();
    await _vm.fetchData();  
  },
  data(){
    return {
      CODES: {
				SC_001 : [],
			},

      checkedAll : false,

			favoriteData : {
        list: [],
        pi: {...DEF_DATA.pi},
        sc: {...DEF_DATA.sc},
      },
    }
  },
  methods : {
    // 검색 
		onSearch() {
      const _vm = this;
      _vm.loadList(1);
    },

    onClickItem(item) {
      const _vm = this;

      if(_vm.refCode == 'raw') {
        _vm.ermPushPage({
          name:'coching-raw-detail', 
          query : {
            rawSeq : item.rawSeq,
            rawDetailSeq : item.refSeq
          }
        });
      }
    },  

    // 페이지 변경 
		onChangePage(pageNo) {
      const _vm = this;
      _vm.loadList(pageNo);
    },    

    /* 체크박스 */
    onClickToggleAll() {
      const _vm = this;

      _vm.favoriteData.list.forEach(item => {
        item.checked = _vm.checkedAll;
      });      
    },

    //원료 자료 요청
    async onClickRequestRawDoc() {
      const _vm = this;

      const checkedList = _vm.favoriteData.list.filter(item => item.checked);
      if(checkedList.length == 0) {
        _vm.$refs.alertModal.open({
          title: '자료 요청할 원료를 선택해주세요.',
          content : ''
        });
        return;
      }

      const requestList = checkedList.map(item => ({
          rawSeq: item.rawSeq,
          rawDetailSeq: item.refSeq,
          membSeq: item.membSeq
      }));
      await _vm.$refs["requestRawModal"].open({
        rawInfo: [...requestList],
      });
    },

    //찜 내역 삭제
    async onClickDelete() {
      const _vm = this;

      const checkedList = _vm.favoriteData.list.filter(item => item.checked);
      if(checkedList.length == 0) {
        _vm.$refs.alertModal.open({
          title: '삭제할 찜 목록을 선택해주세요.',
          content : ''
        });
        return;
      }

      const ret = await _vm.$refs["confirmModal"].open({
        title: '찜한 원료 삭제',
        content : '선택한 짐 목록을 삭제하시겠습니까?',
        okButtonText : '삭제',
        cancelButtonText : '취소'        
      });
      if(!ret){
        return;
      }

      _vm.loading(true);
      try{
        const params = {
          refCode : _vm.refCode,
          refSeqs : checkedList.map(item => item.refSeq)
        }; 

        await removeMyFavorite(params);
        const pInfo = _vm.favoriteData.pi;
        await _vm.loadList(pInfo.curPage || 1);

      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }      
    },

    async fetchData(){      
      const _vm = this;
      _vm.loading(true);

      try{
        await _vm.loadList(1);

      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },

    /* 찜 목록 가져오기 */
    async loadList(pageNo) {
			const _vm = this;
      const dataMap = _vm.favoriteData, pInfo = _vm.favoriteData.pi, searchOp = _vm.favoriteData.sc;
      pInfo.curPage = pageNo || 1;

			_vm.loading(true);
      try {
        const params = _vm.getListSearchParam();
        const res = await getMyFavoriteList(params);
        const resData = res.resultData;
        
        const pageInfo = resData.pageInfo;
        dataMap.pi = {
          ...dataMap.pi,
          curPage : pageInfo.currentPage,
          totalItem : pageInfo.totalItem,
          perPage : pageInfo.pageItemSize
        }

        //데이터 바인딩
        //dataMap.list = resData.list;
        dataMap.list = resData.list.map(item => {
          return {
            ...item,
            checked : false
          }
        });        
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
		},

    /* 검색 조건 가져오기 */
		getListSearchParam() {
      const _vm = this;
      const pInfo = _vm.favoriteData.pi, searchOp = _vm.favoriteData.sc;

			const retParam = {
        refCode : _vm.refCode,
        
        page : pInfo.curPage,
        rowSize : pInfo.perPage                
      };
			
			return retParam;
    },

    //코드 로드
	  async loadCodes(){
		  const _vm = this;
		  _vm.CODES.SC_001 = [];
    },

		docReady() {
			const _vm = this;
		},
  }
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">
</style>
