<template>
  <!--section-->
  <section>
    <div class="container h-100">
      <div class="content">
        <div class="common-content">
          <!--content-title-wrap-->
          <MypageMenuBar></MypageMenuBar>

          <div class="board-wrap">
            <!--total-wrap-->
            <div class="total-wrap">
              <div class="total-num">총<span>{{boardData.pi.totalItem}}</span></div>
            </div>
            <!--board-list-->
            <div class="board-list">
              <a href="javascript:;"
                v-for="(item, idx) of boardData.list"
                :key="item.boardSeq"
                @click="onClickItem(item)">
                <!--content-wrap-->
                <div class="content-wrap">
                  <div class="title-wrap">
                    <div class="title">
                      {{item.title}}
                      <span v-if="item.cmtCnt"
                        class="text-primary">({{item.cmtCnt}})</span>
                    </div>
                  </div>
                  <div class="info">{{ item.content }}</div>
                  <div class="date"><span>{{item.rgtDttm | fmtBoardDate}}</span></div>
                </div>
              </a>
            </div>
            <div v-show="boardData.list.length <= 0"
              class="result-none empty-content">
              등록된 게시물이 없습니다.
            </div> 
            <!--pagenation-->
            <Pagenation
              v-model="boardData.pi.curPage"
              :totalRows="boardData.pi.totalItem"
              :perPage="boardData.pi.perPage"
              @input="onChangePage"
            ></Pagenation>
          </div>
          
        </div>
      </div>

      <!--오류-->
			<AlertModal ref="alertModal"></AlertModal>
			<ConfirmModal ref="confirmModal"></ConfirmModal>

    </div>
  </section>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import boardMixin from '@/components/board/boardMixin';
import moment from 'moment';

import Pagenation from '@/components/Pagenation.vue';

import { DEF_BOARD, BOARD_ROUTES, BOARD_MODE } from '@/constants/board';
import { getCodes } from '@/api/coching/comm/code';
import { getMyBoardList } from '@/api/coching/comm/board';

import Advertise from '@/components/Advertise.vue';
import MypageMenuBar from './MypageMenuBar.vue';


export default {
	name: 'coching-mypage-myWrite',
	mixins: [ernsUtils, boardMixin],
	components: {
    Advertise,
    MypageMenuBar,
    Pagenation,
  },
	computed: {    
  },
	watch: {
	},
	props: {
    
  },
  async mounted() {
		const _vm = this;
    
    await _vm.loadCodes();
    await _vm.fetchData();  
	},
	beforeDestroy() {
		const _vm = this;
	},
	data() {
		return {
      CODES: {
				SC_001 : [],
			},

			boardData : {
        list: [],
        pi: {...DEF_BOARD.pi},
        sc: {...DEF_BOARD.sc},
		  }
    }
	},	
	methods: {
    //게시글 선택 
    onClickItem(item) {
      const _vm = this;
      
      // console.debug(item);
      const boardRouteName = BOARD_ROUTES[item.boardMstId];
      
      const viewRouteName = _vm.getRouteName(boardRouteName, BOARD_MODE.VIEW);
      const boardSeq = item instanceof Object 
        ? item.boardSeq 
        : item;

      const routeParam = {
        boardSeq: boardSeq,
      };

      // console.debug(viewRouteName);
      // console.debug(routeParam);

      _vm.ermPushPage({
        name:viewRouteName, 
        //params:routeParam,
        query:routeParam,
      });
    },

    // 페이지 변경 
		onChangePage(pageNo) {
      const _vm = this;
      _vm.loadList(pageNo);
    },    

    /* 게시글 목록 가져오기 */
    async loadList(pageNo) {
			const _vm = this;
      const dataMap = _vm.boardData, pInfo = _vm.boardData.pi, searchOp = _vm.boardData.sc;
      pInfo.curPage = pageNo || 1;

			_vm.loading(true);
      try {
        const params = _vm.getListSearchParam();
        const res = await getMyBoardList(params);
        const resData = res.resultData;
        
        const pageInfo = resData.pageInfo;
        dataMap.pi = {
          ...dataMap.pi,
          curPage : pageInfo.currentPage,
          totalItem : pageInfo.totalItem,
          perPage : pageInfo.pageItemSize
        }

        //데이터 바인딩
        dataMap.list = resData.list;
        //dataMap.backupList = JSON.parse(JSON.stringify(resData.list));
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
		},

    /* 검색 조건 가져오기 */
		getListSearchParam() {
      const _vm = this;
      const pInfo = _vm.boardData.pi, searchOp = _vm.boardData.sc;

			const retParam = {
        //boardMstId : _vm.boardMstId,
        delYn : searchOp.delYn,
        titleL : searchOp.titleL,

        page : pInfo.curPage,
        rowSize : pInfo.perPage                
      };
			
			return retParam;
    },

		//데이터 가져오는 부분(사용하지 필요없어도 반드시 선언)
		async fetchData() {
			const _vm = this;

			_vm.loading(true);
			try {

        await _vm.loadCodes();
        await _vm.loadList(1);

			} catch (err) {
				_vm.defaultApiErrorHandler(err); //기본에러처리
			} finally {
				_vm.loading(false);
			}
		},

    //코드 로드
	  async loadCodes(){
		  const _vm = this;
		  _vm.CODES.SC_001 = [
        {value:"", label : _vm.$t('') || '전체'},
        {value:"001", label : _vm.$t('') || '원료명'},
        {value:"002", label : _vm.$t('') || '담당자'}
      ];
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
#coching-mypage-myWrite {

}
</style>
