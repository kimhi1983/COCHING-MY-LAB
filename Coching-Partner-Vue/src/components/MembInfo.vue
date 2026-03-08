<template>
  <validation-observer ref="memberInfoForm" #default="{ invalid }">
    <!-- invalid 값을 로컬 데이터에 바인딩 -->
    <div v-if="updateInvalidState(invalid)"></div>
    <div class="inner">
      <div class="input-wrap">

        <div class="input-flex">
          <div class="label-set">
            <label>{{ $t('my.content02.label001') || '이메일' }}<span class="text-pink">*</span></label>
            <validation-provider #default="{ errors }"
                  class="form-input" :name="$t('my.content02.label001') || '이메일'" rules="required|email">
              <div class="input-set">
                <input v-model="localData.memberInfo.email" :disabled="actType != 'JOIN'" type="email" :placeholder="$t('join.memb.label001') || '이메일을 입력해 주세요'">
                <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                <div v-if="actType == 'JOIN'" class="info text-pink">{{ emailMsg }}</div>
              </div>
            </validation-provider>
          </div>
          <div class="label-set">
            <label>{{ $t('my.content02.label002') || '이름' }}<span class="text-pink">*</span></label>
            <validation-provider #default="{ errors }"
                  class="form-input" :name="$t('my.content02.label002') || '이름'" rules="required">
              <div class="input-set">
                <input v-model="localData.memberInfo.membName" type="text" :placeholder="$t('join.memb.label002') || '이름을 입력해 주세요'">
                <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
              </div>
            </validation-provider>
          </div>
        </div>

        <div v-if="isSetPass" class="input-flex">
          <div class="label-set">
            <label>{{ $t('my.content02.label003') || '비밀번호' }}<span class="text-pink">*</span></label>
            <validation-provider #default="{ errors }"
              class="form-input" :name="$t('my.content02.label003') || '비밀번호'" rules="required|no-whitespace|passwordcochingJoin">
              <div class="input-set">
                <input v-model="localData.memberInfo.pswd" type="password" :placeholder="$t('join.memb.label003') || '비밀번호를 입력해 주세요'">
                <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
              </div>
            </validation-provider>
          </div>
          <div class="label-set">
            <label>{{ $t('my.content02.label004') || '비밀번호 확인' }}<span class="text-pink">*</span></label>
            <validation-provider #default="{ errors }"
              class="form-input" :name="$t('my.content02.label004') || '비밀번호 확인'" rules="required|checkPass">
              <div class="input-set">
                <input v-model="localData.memberInfo.pswdConfirm" type="password" :placeholder="$t('join.memb.label004') || 'Please enter it again'">
                <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
              </div>
            </validation-provider>
          </div>
        </div>

        <div class="input-flex">
          <div class="label-set">
            <label>{{ $t('my.content02.label005') || '전화번호' }}<span class="text-pink">*</span></label>
            <validation-provider #default="{ errors }"
                  class="form-input" :name="$t('my.content02.label005') || '전화번호'" rules="required|telOnlyNumber">
              <div class="input-set">
                <input v-model="localData.memberInfo.phone" type="tel" :placeholder="$t('join.memb.label005') || '‘-’없이 입력해 주세요'">
                <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
              </div>
            </validation-provider>
          </div>
          <div class="label-set">
            <label>{{ $t('my.content02.label006') || '부서' }}<span class="text-pink">*</span></label>
            <validation-provider #default="{ errors }"
                  class="form-input" :name="$t('my.content02.label006') || '부서'" rules="required">
              <div class="input-set">
                <input v-model="localData.memberInfo.department" type="text" :placeholder="$t('join.memb.label006') || '부서를 입력해 주세요'">
                <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
              </div>
            </validation-provider>
          </div>
        </div>

      </div>
    </div>
  </validation-observer>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import { extend } from 'vee-validate';
import { getEmailList } from '@/api/coching/member/member';
import { required, passwordcochingJoin, noWhiteSpace, min, max, telOnlyNumber } from '@validations';
import { cochingValidation } from '@/@core/utils/validations/validations';

const DEF_MEMB_INF = {
    membSeq: null,
    email: '',
    membName: '',
    pswd: '',
    pswdConfirm: '',
    phone: '',
    department: '',
    auth: '',
};

export default {
    name: 'coching-membInfo',
    mixins: [ernsUtils],
    components: {
    },
    props: {
        isSetPass:{
          type : Boolean,
          default : false
        },
        actType:{
            type : String,
            default : ""
        },
        membInfo: {
          type : Object,
          defalt: {}
        }
    },
    computed: {
        
    },
    watch: {
       //입력양식 데이터가 변경될 경우를 캐치
      'localData.memberInfo' : {
        handler(newVal, oldVal){
          this.onChangeValue();
        },
        deep: true
      },   
      'localData.memberInfo.email' : function(val){
        var _vm = this;

        if(val.length == 0) {
          _vm.emailMsg = '';
          return;
        }

        const matchingComp = _vm.list.find(elem=>elem.email === val);

        if(matchingComp) {
          // 일치하는 회사 존재 => 찾아서 setComp 해주기
          _vm.emailMsg = _vm.$t('common.join.msg003') || '이미 사용 중인 이메일입니다.';
        } else {
         _vm.emailMsg = '';
        }
      },
      membInfo:  function(val){
        const _vm = this;
        if(_vm.actType == 'MYPAGE'){
          const mergedData = Object.keys(_vm.localData.memberInfo).reduce((result, key) => {
            result[key] = val[key] || _vm.localData.memberInfo[key];
            return result;
          }, {});
          _vm.localData.memberInfo = {...mergedData};
        }
      },
      '$i18n.locale' : function(){
        const _vm = this;
        _vm.$nextTick(() => _vm.$refs.memberInfoForm.validate());
      },
    },
    data() {
        return {
            localInvalidState: false, //양식폼의 validate 상태

            status: '',
            localData: {
              memberInfo: {...DEF_MEMB_INF},
            },

            list : [],
            emailMsg : '',
        }
    },
  async mounted(){
    const _vm = this;

    function checkPass() {
      extend('checkPass', {
        validate: value => {
          return _vm.localData.memberInfo.pswd == value || _vm.$t('join.memb.label007') || '비밀번호가 일치하지 않습니다.';
      },
		  });
	  } 
    // 메시지 업데이트 호출
    checkPass();
    cochingValidation();

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

      const res = await getEmailList({});
      const { resultCode, resultFailMessage, resultData } = res;

      _vm.list = [...resultData.list];

      try{
        
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },    

    async loadCodes(){
      const _vm = this;
    },

    async onChangeValue(){
      const _vm = this;

      _vm.$emit("update:data", _vm.localData.memberInfo);      
    },

    docReady(){
      const _vm = this;      
    },

    //양식의 상태값을 확인
    updateInvalidState(invalid) {
      const _vm = this;
      const isChanged = this.localInvalidState != invalid;
      this.localInvalidState = invalid;
      if(isChanged){
        //상태값이 변경되면 부모에게 알림
        _vm.$emit("update:valid", !this.localInvalidState);
      }
      return false; // v-if에서 항상 false를 반환하여 실제 DOM에 렌더링되지 않게 함
    },
  }  
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">

</style>