<template>
  <div class="tabs-panel active">

    <div class="board-wrap">
      <!--total-wrap-->
      <div class="total-wrap">
        <!--check-total-wrap-->
        <div class="check-total-wrap">
          <!--checkbox-->
          <div class="checkbox only">
            <input 
              :id="`message-${ownMode}-checkall`" 
              type="checkbox" 
              v-model="checkedAll"
              @change="onClickToggleAll" />
            <label :for="`message-${ownMode}-checkall`" class="checkbox-label"></label>
          </div>
          <div class="total-num">총<span>{{messageData.pi.totalItem}}</span></div>
        </div>
        <div class="right">
          <!--button-->
          <button type="button" class="btn btn-sm btn-gray-outline" @click="onClickDelete">삭제</button>
        </div>
      </div>
      <!--board-list-->
      <div class="board-list">
        <a href="javascript:;"
          v-for="(item, idx) of messageData.list" 
          :key="idx">
          <!--checkbox-->
          <div class="checkbox only">
            <input 
              :id="`message-${ownMode}-${item.messageSeq}`" 
              type="checkbox" 
              v-model="item.checked"/>
            <label :for="`message-${ownMode}-${item.messageSeq}`" class="checkbox-label"></label>
          </div>
          <!--content-wrap-->
          <div class="content-wrap"
            @click="onClickItem(item)">
            <div class="title-wrap">
              <div class="title">{{eufmtMaxLength50(item.content)}}</div>
            </div>
            <div class="info">{{item.content}}</div>
            <div class="date">
              <span>{{ ownMode == 'sent' ? item.receiverName : item.senderName }}</span>
              <span>{{item.rgtDttm | eFmtDateFormat('YY.MM.DD HH:mm')}}</span>
              <span class="chk-yn" v-if="item.state === '0'" style="color: var(--color--primary)">읽지않음</span>
              <span class="chk-yn" v-else>읽음</span>
            </div>
          </div>
        </a>
      </div>
      <!--no-data-->
      <div v-show="ownMode == 'recv' && messageData.list.length <= 0"
        class="result-none empty-content">
        받은 쪽지함이 없습니다.</div>
      <div v-show="ownMode == 'sent' && messageData.list.length <= 0"
        class="result-none empty-content">
        보낸 쪽지함이 없습니다.</div>

      <!--pagenation-->
      <Pagenation
        v-model="messageData.pi.curPage"
        :totalRows="messageData.pi.totalItem"
        :perPage="messageData.pi.perPage"
        @input="onChangePage"
      ></Pagenation>

    </div>

    <MessageReadModal ref="messageReadModal"></MessageReadModal>

    <!--오류-->
    <AlertModal ref="alertModal"></AlertModal>
    <ConfirmModal ref="confirmModal"></ConfirmModal>
  </div>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import messageMixin from './messageMixin';

import Pagenation from '@/components/Pagenation.vue';
import MessageReadModal from '@/components/modal/MessageReadModal.vue';

import { DEF_DATA, MESSAGE_OWN_MODE, DEF_MESSAGE_INF} from '@/constants/message';
import { getUserMessageList, removeUserMessage, getUserMessageDetail } from '@/api/coching/member/message';
import { EventBus } from '@/components/eventBus'; 


export default {
  name: 'coching-message-list',
  mixins: [ernsUtils, messageMixin],
  components: {
    Pagenation,
    MessageReadModal,
  },
  computed: {},
  watch: {
    async ownMode(newVal, oldVal) {
      const _vm = this;
      _vm.messageData.sc = { ...DEF_DATA.sc }; //검색조건 초기화
      await _vm.loadCodes();
      await _vm.fetchData();
    },
  },
  props:{
    ownMode: {
      type: String,
      default: MESSAGE_OWN_MODE.RECIVED
    }
  },
  async mounted(){
    const _vm = this;

    await _vm.loadCodes();
    await _vm.fetchData();  
  },
  created() {    
    const _vm = this;
    EventBus.$on('sent-message', (data)=>{
      if(_vm.ownMode != 'sent') {
        return;
      }

      const pInfo = _vm.messageData.pi;
      /* 게시글 목록 갱신 */
      _vm.loadList(pInfo.curPage || 1);
    });
  },
  data(){
    return {
      CODES: {
				SC_001 : [],
			},

      checkedAll : false,

			messageData : {
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

    //쪽지 선택 
    async onClickItem(item) {
      const _vm = this;

      _vm.$refs["messageReadModal"].open({
        ...DEF_MESSAGE_INF,
        ...item,
        mode: _vm.ownMode,
      });
      
      if(_vm.ownMode == MESSAGE_OWN_MODE.RECIVED && item.state == '0') {
        //안읽은 쪽지를 읽음으로 변경

        await getUserMessageDetail({
          mode : MESSAGE_OWN_MODE.RECIVED,
          messageSeq : item.messageSeq
        });

        const pInfo = _vm.messageData.pi;
        await _vm.loadList(pInfo.curPage || 1);
      }
    },

    //쪽지 보내기
    onClickWrite(){
      const _vm = this;
      _vm.goWrite();
    },

    // 페이지 변경 
		onChangePage(pageNo) {
      const _vm = this;
      _vm.loadList(pageNo);
    },    

    /* 체크박스 */
    onClickToggleAll() {
      const _vm = this;

      _vm.messageData.list.forEach(item => {
        item.checked = _vm.checkedAll;
      });      
    },

    //쪽지 삭제
    async onClickDelete() {
      const _vm = this;

      const checkedList = _vm.messageData.list.filter(item => item.checked);
      if(checkedList.length == 0) {
        _vm.$refs.alertModal.open({
          title: '삭제할 쪽지를 선택해주세요.',
          content : ''
        });
        return;
      }

      const ret = await _vm.$refs["confirmModal"].open({
        title: '쪽지 삭제',
        content : '선택한 쪽지를 삭제하시겠습니까?',
        okButtonText : '삭제',
        cancelButtonText : '취소'        
      });
      if(!ret){
        return;
      }

      _vm.loading(true);
      try{
        const params = {
          messageSeqs : checkedList.map(item => item.messageSeq)
        } 

        await removeUserMessage(params);
        const pInfo = _vm.messageData.pi;
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

    /* 게시글 목록 가져오기 */
    async loadList(pageNo) {
			const _vm = this;
      const dataMap = _vm.messageData, pInfo = _vm.messageData.pi, searchOp = _vm.messageData.sc;
      pInfo.curPage = pageNo || 1;

			_vm.loading(true);
      try {
        const params = _vm.getListSearchParam();
        const res = await getUserMessageList(params);
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
      const pInfo = _vm.messageData.pi, searchOp = _vm.messageData.sc;

			const retParam = {
        mode : _vm.ownMode,
        contentL : searchOp.contentL,

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
