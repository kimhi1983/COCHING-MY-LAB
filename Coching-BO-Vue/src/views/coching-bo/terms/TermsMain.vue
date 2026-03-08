<template>
  <b-card>
    <b-row class="mb-1">
      <b-col cols="12" md="3">
        <b-form-group label="언어" label-for="terms-main-etc5">
          <v-select
            id="terms-main-etc5"
            v-model="sc.etc5"
            :options="codes.ETC5_CODE" :reduce="op => op.value"
            :dir="$store.state.appConfig.isRTL ? 'rtl' : 'ltr'"
            :class="['w-100', 'select-size-sm', 'select-placeholder-gray', { 'has-value': sc.etc5 }]"
            placeholder="언어를 선택하세요"
            @option:selected="onChangeSearchCondition"
          >
            <span slot="no-options">검색된 항목이 없습니다.</span>
          </v-select>
        </b-form-group>
      </b-col>
    </b-row>

    <hr/>

    <b-tabs class="sysTermsTab"      
      fill pills 
      v-model="tabIndex">
      
      <b-tab
        v-for="(items, idx) in termsData"
        :key="items.termsCd"
        :title="items.name"
        lazy>

      <TermsContents
        :ref="'termsContentsForm_'+idx"
        :termsData="items"
        @update="updateData">
      </TermsContents>
      </b-tab>
    </b-tabs>

    <!-- _vm.codes.TERMS_CODE : {{ codes.TERMS_CODE }} -->

  </b-card>  
</template>

<script>
import { getCodes } from '@/api/coching-bo/comm/code';
import { getList } from '@/api/coching-bo/comm/terms';

import ernsUtils from '@/components/mixins/ernsUtils';

import Ripple from 'vue-ripple-directive';
import vSelect from 'vue-select';
import TermsContents from './TermsContents'; //card content 양식

const ECT5_CODES = [ //언어 검색 select 창
  {label:'ko', value:'ko'},
  {label:'en', value:'en'}
];

export default {
  name: 'coching-bo-terms-main',
  mixins: [ernsUtils],
  components: {
    vSelect, TermsContents
  },
  directives: {
    Ripple,
  },
  props: {
    termsType:{
      type : String,
      default : "TERMS"
    },
    termsCodes:{
      type:Array,
      default: ()=>[]
    }
  },
  watch: {
    $route : async (oldVak, newVal)=>{ //라우트가 변경되면 메소드를 다시 호출됩니다.
      await _vm.loadCodes();
      _vm.loadList();
    }
  },
  data(){
    return {
      tabIndex: 0,

      codes : {
        ETC5_CODE : [...ECT5_CODES],
        TERMS_CODE : [] //tab 목록 저장용
      },

      sc:{
        grpCode: '',
        etc5 : 'ko',
      },

      termsData: [],
    }
  },
  async mounted(){
    const _vm = this;

    await _vm.loadCodes();
    _vm.loadList();
  },
  beforeDestroy(){
    const _vm = this;
	},
  methods: {
    onSearch(){
      const _vm = this;      
    },

    //검색조건 변경
    async onChangeSearchCondition(){
      const _vm = this;

      await _vm.loadCodes();
      _vm.loadList();      
    },

    loadList() {
      const _vm = this;
      _vm.apiList();
    },

    //약관 로드
    async apiList() {
      const _vm = this;

      const termsCds = _vm.codes.TERMS_CODE.map(cd=>cd.code);
      const params = {
        termsCds : termsCds,
        termsType: 'TERMS',
        useYn : 'Y'
      };

      _vm.loading(true);      
      try {
        const res = await getList(params);
        const termsDataList = res.resultData.list;

        //약관 데이터 다시 세팅
        for(let idx=0; idx < _vm.termsData.length; idx++){
          const oldData = _vm.termsData[idx];
          const termsData = termsDataList.find((item)=>oldData.termsCd == item.termsCd);
          Object.assign(_vm.termsData[idx], oldData, termsData);
        }

        //console.debug(_vm.$refs);
        const refObj = _vm.$refs['termsContentsForm_'+_vm.tabIndex][0];
        //console.debug(refObj);
        refObj.setValue(_vm.termsData[_vm.tabIndex]);  

      } catch (err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
    },
    showAlert() {
      const _vm = this;

      _vm.$swal({
        title: '저장 안하고 이동',
        customClass: {
          confirmButton: 'btn btn-primary',
        },
        buttonsStyling: false,
      })
    },
    updateData(result) {
      const _vm = this;

      console.debug(`result:${result}`);

      if(result){
        _vm.loadList();
      }
    },

    async loadCodes(){
      const _vm = this;

      //약관 코드 로드
      const termsCdRes = await getCodes({
        grpCode:'CH004', etc5: _vm.sc.etc5, rowSize:-1
      });

      const termsList = termsCdRes.resultData.list;

      //약관 내용이 있어야 하는 것만 필터링
      _vm.codes.TERMS_CODE = termsList.filter((cd)=>{
        return _vm.termsCodes.includes(cd.etc1);
      });

      //기본값 세팅 약관 이름 찾아주기
      _vm.termsData.splice(0, _vm.termsData.length);//기존 데이터 초기화
      for(const termCd of _vm.codes.TERMS_CODE){
        _vm.termsData.push({
          termsType : _vm.termsType,
          cnt : 0,
          termsCd : termCd.code,
          name : termCd.codeName
        });
      }
    },

  }  
}
</script>

<style lang="scss">
  .sysTermsTab{
    .nav-tabs{
      border-bottom: 1px solid rgba(34, 41, 47, 0.5);
    }

  }
</style>