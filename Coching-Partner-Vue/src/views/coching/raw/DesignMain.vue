<template>
  <!--section-->
  <section>
    <div class="container">
      <div class="content-wrap">
        <!--title-->
        <div class="h1">담당자 지정</div>
        <!--content-->
        <div class="content-inner">
          <!--total-wrap-->
          <div class="total-wrap">
            <div class="total-num">{{ $t('') || '총' }}<span>{{ rawData.pi.totalItem }}</span></div>
            <div class="right">
              <!--select-->
              <CochingSelect v-model="rawData.sc.searchField"
                name="sources"
                :placeholder="$t('') || '전체'"
                :options="CODES.SC_001"
                :label="'label'"
                :trackBy="'etc1'"
                >
              </CochingSelect>

              <!--search-->
              <div class="input-set">
                <div class="input-ic-set">
                  <input v-model="rawData.sc.searchText"
                    @keyup.enter="onClickSearch"
                    type="text" placeholder="검색" />
                  <button @click="onClickSearch"
                   type="button" class="input-ic ic-md ic-search-md"></button>
                </div>
              </div>

              <!--button-->
              <button @click="onClickDesignModal" type="button" class="btn btn-sm btn-primary modal-open user-change-modal">선택 변경</button>
            </div>
          </div>

          <!--table-->
          <table>
            <colgroup>
              <col width="50" />
              <col width="100" />
              <col />
              <col width="150" />
              <col width="150" />
              <col width="300" />
            </colgroup>
            <thead>
              <tr>
                <th>
                  <div class="checkbox only">
                    <input v-model="rawData.checkAll"
                      @change="toggleAllCheckboxes" 
                      id="checkall-manager" 
                      type="checkbox"
                      :disabled="rawData.list.length == 0"/>
                    <label for="checkall-manager" class="checkbox-label"></label>
                  </div>
                </th>
                <th>번호</th>
                <th class="text-left">제목</th>
                <th>담당자</th>
                <th>계정사용</th>
                <th>담당자 변경</th>
              </tr>
            </thead>
            <tbody v-if="rawData.list.length > 0">
              <tr v-for="(item, index) of rawData.list" :key="index" class="hover">
                <td>
                  <div class="checkbox only">
                    <input v-model="item.checked" 
                      @change="updateCheckAllStatus"
                      :id="'manager-'+item.rawDetailSeq" 
                      name="chk-checkall-manager" type="checkbox" />
                    <label :for="'manager-'+item.rawDetailSeq" 
                      class="checkbox-label"></label>
                  </div>
                </td>
                <td class="num">{{ item.rn }}</td>
                <td class="text-left">
                  <div class="title text-link">{{ item.rawName }}</div>
                </td>
                <td>{{ item.membName }}</td>
                <td>{{ item.membUseYn == 'Y' ? '사용' : '미사용' }}</td>
                <td>
                  <form>
                    <div class="btn-set">
                      <CochingSelect v-model="item.newManagerSeq"
                        :name="'sources'+item.rawDetailSeq"
                        :placeholder="$t('') || '담당자 선택'"
                        :options="filterMembers(item)"
                        :label="'membName'"
                        :trackBy="'membSeq'"
                        >
                      </CochingSelect>
                      <button @click="onDoChangeManager(item)"
                        type="button" class="btn btn-sm btn-gray-outline">변경하기</button>
                    </div>
                  </form>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-if="rawData.list.length == 0" class="result-none">검색결과가 없습니다.</div>

         <!--pagenation-->
         <Pagenation
            v-model="rawData.pi.curPage"
            :totalRows="rawData.pi.totalItem"
            :perPage="rawData.pi.perPage"
            @input="onChangePage"></Pagenation>
        </div>
      </div>

      <!--담당자 변경-->
      <div ref="rawDesignModal" class="modal for-user-change">
        <ModalLayout :classOption="classOptions.design">
          <!-- Header Content -->
          <template v-slot:header>
            <div class="title">변경할 담당자를 지정하세요</div>
          </template>

          <!-- Body Content -->
          <template v-slot:body>
            <CochingSearchSelect
                :placeholder="$t('') || '담당자 선택'"
                :value="rawData.newManagerSeq"
                :options="memberData.list"
                :label="'membName'"
                :trackBy="'membSeq'"
                :isMultiple="false"
                :isSearch="true"
                :minSearchCnt="5"
                :noSearchText="'일치하는 이름이 없습니다.'"
                @input="rawData.newManagerSeq = $event"
                ></CochingSearchSelect>
          </template>
          <template v-slot:footer>
            <button @click="onClickRawDesignClose" type="button" class="btn btn-md btn-gray bottom-modal-close">취소</button>
            <button @click="onDoChangeManager({newManagerSeq: rawData.newManagerSeq})" type="button" class="btn btn-md btn-primary">변경하기</button>
          </template>
        </ModalLayout>
      </div>
      
      <!--오류-->
      <AlertModal ref="alertModal"></AlertModal>
      <ConfirmModal ref="confirmModal"></ConfirmModal>
    </div>
  </section>
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';
import { required, email, bizNumOnlyNumber, min, max, password } from '@validations';

import { getCodes } from '@/api/coching/comm/code';
import { getMembList } from '@/api/coching/member/member';
import { getRawList, updateRawManager } from '@/api/coching/comm/raw';

import Pagenation from '@/components/Pagenation.vue';
import CochingSearchSelect from '@/components/CochingSearchSelect.vue';
import ModalLayout from '@/components/dialog/ModalLayout.vue';
import CochingSelect from '@/components/CochingSelect.vue';

const DEF_RAW = {
	sc : {
		searchField : '',
    searchText : '',
    compSeq: null,
    useYn: 'Y'
	},
	pi:{
		curPage : 1,
    totalItem : 0,
    perPage : 10
		//perPage : 1 //Test
	},
	summary : []
};

export default {
	name: 'coching-design-main',
	mixins: [ernsUtils],
	components: {
    Pagenation,
    CochingSelect,
    CochingSearchSelect,
    ModalLayout,
},
	computed: {    
  },
	watch: {
	},
	props: {
    
  },
	data() {
		return {
      CODES: {
            SC_001 : [],
          },
      classOptions: {
        design: 'modal-md',
      },
      rawData : {
        newManagerSeq: '',
        checkAll : false,
        checkedlist: [],
        list: [],
        pi: {...DEF_RAW.pi},
        sc: {...DEF_RAW.sc},
      },
      memberData: {
        list:[],
      },
		}
	},
	async mounted() {
		const _vm = this;

    await _vm.loadCodes();
		_vm.docReady();
		_vm.fetchData();
	},
	beforeDestroy() {
		const _vm = this;
	},
	methods: {

		//데이터 가져오는 부분(사용하지 필요없어도 반드시 선언)
		async fetchData() {
			const _vm = this;

			_vm.loading(true);
			try {

        const membListRes = await getMembList({ptnSeq: _vm.partnerInfo.ptnSeq});
        const resData = membListRes.resultData;

        _vm.memberData.list = [...resData.list];

        if(_vm.memberData.list.length > 1){
        await _vm.loadList(1);
        } else {
          _vm.rawData.list = [];
          _vm.rawData.pi.totalItem = 0;
        }

			} catch (err) {
				_vm.defaultApiErrorHandler(err); //기본에러처리
			} finally {
				_vm.loading(false);
			}
		},

    //검색
		onClickSearch() {
      const _vm = this;

      _vm.loadList(1);
    },

    async loadList(pageNo) {
      const _vm = this;
      const dataMap = _vm.rawData, pInfo = _vm.rawData.pi, searchOp = _vm.rawData.sc;
      pInfo.curPage = pageNo || 1;

			_vm.loading(true);
      try {
        const params = _vm.getListSearchParam();
          const rawListRes = await getRawList(params);
          const resData = rawListRes.resultData;
          
          const pageInfo = resData.pageInfo;
          dataMap.pi = {
            ...dataMap.pi,
            curPage : pageInfo.currentPage,
            totalItem : pageInfo.totalItem,
            perPage : pageInfo.pageItemSize
          }

          //데이터 바인딩
          dataMap.list = resData.list;
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
		},
    

    async loadCodes(){
      const _vm = this;

      _vm.CODES.SC_001 = [
        {value:"", label : _vm.$t('') || '전체'},
        {value:"001", label : _vm.$t('') || '원료명'},
        {value:"002", label : _vm.$t('') || '담당자'}
      ];

    },

    //페이지 변경
		onChangePage(pageNo) {
      const _vm = this;

      _vm.loadList(pageNo);
    },

    //검색조건
		getListSearchParam() {
      const _vm = this;
      const pInfo = _vm.rawData.pi, searchOp = _vm.rawData.sc;

			const retParam = {
        ptnSeq: _vm.partnerInfo.ptnSeq,
        exclude: 'rawDetailSeq',
				searchField : searchOp.searchField,
				searchText : $.trim("" + searchOp.searchText),

        page : pInfo.curPage,
        rowSize : pInfo.perPage                
      };
			
			return retParam;
    },

    //선택변경 팝업
    async onClickDesignModal(item){
      const _vm = this;

      if(_vm.rawData.list.length === 0){
        await _vm.$refs["alertModal"].open({
              title: _vm.$t('') || '담당자 지정',
              titleHtml : _vm.$t('') || '담당자 지정할 원료가 없습니다.'
            });
          
        return;
      }

      if(_vm.rawData.checkedlist.length === 0){
        await _vm.$refs["alertModal"].open({
              title: _vm.$t('') || '담당자 지정',
              titleHtml : _vm.$t('') || '지정할 원료를 선택해주세요.'
            });
          
        return;
      }

      $(_vm.$refs["rawDesignModal"]).fadeIn(300);
      
    },

    onClickRawDesignClose(){
      const _vm = this;
      $(_vm.$refs["rawDesignModal"]).fadeOut(300);
      $("body").css("overflow", "visible");
      _vm.rawData.checkedlist = [];
    },

    // 전체 체크박스를 선택/해제할 때 호출되는 메서드
    toggleAllCheckboxes() {
      const _vm = this;
      
      _vm.rawData.list.forEach(item => {
        item.checked = _vm.rawData.checkAll;
      });
      _vm.setCheckedItem();
    },

    // 개별 체크박스 상태 변경 시 전체 체크박스 상태를 갱신하는 메서드
    updateCheckAllStatus() {
      const _vm = this;
      const allChecked = _vm.rawData.list.every(item => item.checked);
      _vm.rawData.checkAll = allChecked;

      _vm.setCheckedItem();
    },

    setCheckedItem() {
      const _vm = this;

      _vm.rawData.checkedlist = [];  // 기존의 체크리스트를 초기화

      _vm.rawData.list.forEach(item => {
        if (item.checked) {
          // 체크된 항목을 checklist에 추가
          _vm.rawData.checkedlist.push({
            rawSeq: item.rawSeq,
            membSeq: item.managerSeq,
            rawDetailSeq: item.rawDetailSeq
          });
        }
      });
    },

    // 각 항목에 대해 담당자 목록 필터링
    filterMembers(item) {
      const _vm = this;

      const rawList = _vm.rawData.list.filter(raw => raw.rawSeq === item.rawSeq);

      const membSeqSet = new Set(rawList.map(item => item.managerSeq));

      return _vm.memberData.list.filter(item => !membSeqSet.has(item.membSeq));
    },

    async onDoChangeManager(item){
      const _vm = this;

      if(_vm.filterMembers(item).length === 0){ //담당자가 없을 경우
        const ret = await _vm.$refs["alertModal"].open({
          title:_vm.$t('') || '담당자 지정',
          titleHtml : _vm.$t('') || '지정할 담당자가 없습니다.'
        });
        return;
      }

      if(!item.newManagerSeq){
        const ret = await _vm.$refs["alertModal"].open({
          title:_vm.$t('') || '담당자 지정',
          titleHtml : _vm.$t('') || '담당자를 선택해주세요.'
        });
        return;
      }

      const params = _vm.getChangeManagerParam(item);

      const res = await updateRawManager(params);

      const ret = await _vm.$refs["alertModal"].open({
        title:_vm.$t('') || '담당자 지정',
        titleHtml : _vm.$t('') || '변경되었습니다.'
      });

      if(ret){
        _vm.onClickRawDesignClose();
        _vm.loadList(1);
      }

    },

     //검색조건
		getChangeManagerParam(item) {
      const _vm = this;
      const rawInfo = item;

      if(_vm.rawData.checkedlist.length === 0){
        _vm.rawData.checkedlist.push({
            rawSeq: rawInfo.rawSeq,
            membSeq: rawInfo.managerSeq,
            rawDetailSeq: rawInfo.rawDetailSeq
          });
      }

			const retParam = {
        ptnSeq: _vm.partnerInfo.ptnSeq,
        newManagerSeq: rawInfo.newManagerSeq,
        checkedlist: _vm.rawData.checkedlist
      };
			
			return retParam;
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
#coching-mypage-main {

}
</style>
