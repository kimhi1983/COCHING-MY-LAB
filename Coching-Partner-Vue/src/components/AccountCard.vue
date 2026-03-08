<template>
  <!--card-account-->
  <div class="card-account">
    <div @click="onChangeUseYn"
      :class="{'active' : 'Y' === localData.membInfo.useYn}" class="badge-use">
      {{ 'Y' === localData.membInfo.useYn ? ($t('') || '사용') : ($t('') || '미사용')}}
    </div>
    <div @click="onClickMemb" class="item">
      <!--header-->
      <div class="header">
        <div class="title">{{ localData.membInfo.membName }}</div>
      </div>
      <!--body-->
      <div class="body">
        <div class="card-table">
          <table>
            <colgroup>
              <col width="70" />
            </colgroup>
            <tbody>
              <tr>
                <th>{{ $t('') || '아이디' }}</th>
                <td>{{ localData.membInfo.membId }}</td>
              </tr>
              <tr>
                <th>{{ $t('') || '이메일' }}</th>
                <td>{{ localData.membInfo.email }}</td>
              </tr>
              <tr>
                <th>{{ $t('') || '연락처' }}</th>
                <td>{{ localData.membInfo.phone | eufmtPhoneDash }}</td>
              </tr>
              <tr>
                <th>{{ $t('') || '등록일' }}</th>
                <td>{{ localData.membInfo.rgtDate }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <AlertModal ref="alertModal"></AlertModal>
    <ConfirmModal ref="confirmModal"></ConfirmModal>
  </div>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import { setUseYn } from '@/api/coching/member/member';

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
    name: 'coching-membInfo',
    mixins: [ernsUtils],
    components: {
    },
    props: {
        membInfo: {
          type : Object,
          defalt: {}
        }
    },
    computed: {
        
    },
    watch: {
      '$i18n.locale' : function(){
        const _vm = this;
      },
    },
    data() {
        return {
          localData:{
            membInfo: {...DEF_MEMB_INF}
          },
        }
    },
  async mounted(){
    const _vm = this;

    // await _vm.loadCodes();
    _vm.docReady();
    _vm.fetchData();    
  },
	beforeDestroy() {
    const _vm = this;
	},
  methods: {
    //fetchData
    async fetchData(){
      const _vm = this;

      _vm.loading(true);

      try{
        
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },    

    async onChangeUseYn(){
      const _vm = this;

      const ret = await _vm.$refs["confirmModal"].open({
        title: _vm.$t('') || '사용 여부 변경',
        content : _vm.$t('') || '변경 하시겠습니까?'
      });
      
      if(ret){
        _vm.loading(true);
        try {
            const param = _vm.setParam();
            const res = await setUseYn(param);
            const { resultCode, resultFailMessage, resultData } = res;
            _vm.loading(false);

            _vm.localData.membInfo.useYn = param.useYn;

            await _vm.$refs["alertModal"].open({
              title: _vm.$t('') || '변경되었습니다.',
              content : ''
            });
            return;

        } catch(err) {
          _vm.defaultApiErrorHandler(err); //기본에러처리
        } finally {
          _vm.loading(false);
        }
      }
    },

    setParam(){
      const _vm = this;

      const useYn = 'Y' === _vm.localData.membInfo.useYn ? 'N' : 'Y';
      
      return {
        membSeq: _vm.localData.membInfo.membSeq,
        useYn: useYn
      };
    },

    onClickMemb() {
			const _vm = this;
			
			_vm.$emit('click', _vm.membInfo);
		},

    docReady(){
      const _vm = this;

      _vm.localData.membInfo = {..._vm.membInfo};
    },

  }  
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">

</style>