<template>
  <!--section-->
  <section>
    <div class="container">
      <div class="content-wrap">
        <!--title-->
        <div class="h1">{{ $t('') || '계정관리' }}</div>
        <!--content-->
        <div class="content-inner">
          <!--total-wrap-->
          <div class="total-wrap">
            <div class="total-num">{{ $t('') || '총' }}<span>{{ membData.totalCnt }}</span></div>
            <div class="right">
              <!--button-->
              <a @click="onClickRegisterAccount()" 
                href="javascript:;" class="btn btn-sm btn-primary ic ic-plus">{{ $t('') || '계정 등록' }}</a>
            </div>
          </div>
          <!--account-wrap-->
          <div v-if="membData.list.length > 0" class="account-wrap">
            <!--card-wrap-->
            <div class="card-wrap">
              
              <AccountCard
                v-for="(item, idx) of membData.list" :key="idx"
								:membInfo="item"
								@click="onClickMemb"
              ></AccountCard>
              
            </div>
          </div>
          <div v-if="membData.list.length == 0" class="result-none">등록된 계정이 없습니다.</div>
        </div>
      </div>

      <AlertModal ref="alertModal"></AlertModal>
      <ConfirmModal ref="confirmModal"></ConfirmModal>

    </div>
  </section>
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';
import { required, email, bizNumOnlyNumber, min, max, password } from '@validations';

import { getMembList } from '@/api/coching/member/member';

import AccountCard from '@/components/AccountCard.vue';

const DEF_MEMB_INF = {
    membSeq: null,
    membId: '',
    email: '',
    membName: '',
    phone: '',
    useYn: '',
    rgtDate: '',
};

export default {
	name: 'coching-account-main',
	mixins: [ernsUtils],
	components: {
    AccountCard,
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
      membData : {
        list: [],
      },
		}
	},
	async mounted() {
		const _vm = this;

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
        
        await _vm.loadList();

			} catch (err) {
				_vm.defaultApiErrorHandler(err); //기본에러처리
			} finally {
				_vm.loading(false);
			}
		},

    async loadList() {
			const _vm = this;
      const dataMap = _vm.membData;
			_vm.loading(true);
      try {
          const membListRes = await getMembList({ptnSeq: _vm.partnerInfo.ptnSeq, membType: '004'});
          const resData = membListRes.resultData;

          //데이터 바인딩
          dataMap.list = resData.list;
          //TODO 총 개수 적용
          dataMap.totalCnt = resData.list.length;

      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
		},

     //계정 등록
		onClickRegisterAccount() {
      const _vm = this;

      _vm.ermPushPage({name:'coching-account-register'});
    },

    //계약 클릭시
		onClickMemb(membInfo) {
			const _vm = this;
  
      _vm.ermPushPage({name:'coching-account-edit',
        query : {
          membSeq : membInfo.membSeq
          }
      });
			
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
