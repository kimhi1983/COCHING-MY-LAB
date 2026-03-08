<template>
  <div class="modal" ref="erns_reset_passwd_modal">
    <div class="layer">
      <div class="layer-content"><div class="modal-inner">
          <validation-observer ref="resetPasswdForm" #default="{invalid}"
            tag="div" class="modal-content">

            <div class="modal-header">
              <div @click="cancel" class="modal-close"></div>
              <div class="title" v-html="localData.titleHtml"></div>
            </div>            

            <div class="modal-body">              

              <div class="input-wrap">

                <div class="label-set">
                  <label>{{ $t('') || '비밀번호' }}<span class="text-pink">*</span></label>
                  <validation-provider #default="{ errors }"
                    class="form-input" name="비밀번호" rules="required|no-whitespace|passwordcochingJoin">
                    <div class="input-set">
                      <input v-model="userInfo.userPw" type="password" placeholder="비밀번호를 입력해 주세요">
                      <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] }}</div>
                    </div>
                  </validation-provider>
                </div>

                <div class="label-set">
                  <label>{{ $t('') || '비밀번호 확인' }}<span class="text-pink">*</span></label>
                  <validation-provider #default="{ errors }"
                    class="form-input" name="비밀번호 확인" rules="required|checkPass">
                    <div class="input-set">
                      <input v-model="userInfo.userPwConfirm" type="password" placeholder="다시 입력해 주세요">
                      <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] }}</div>
                    </div>
                  </validation-provider>
                </div>
              </div>
            
            </div>
            <div class="modal-footer">
              <button type="button" @click="cancel"
                class="btn btn-md btn-gray bottom-modal-close">{{localData.cancelButtonText}}</button>
              <button type="button" @click="ok"
                :disabled="invalid"
                :class="{
                  'btn-disabled': invalid,
                  'btn-primary': !invalid
                }"
                class="btn btn-md bottom-modal-close">{{localData.okButtonText}}</button>
            </div>
          </validation-observer>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';

import { extend } from 'vee-validate';
import { cochingValidation } from '@/@core/utils/validations/validations';
import { required, passwordcochingJoin, noWhiteSpace, min, max, telOnlyNumber } from '@validations';

const DEF_USERINFO = {
  email: '',
  userPw: '',
  userPwConfirm: '',
};

export default {
  name: 'coching-Reset-UserPasswdModal',
  mixins: [ernsUtils],
  components : {},
  props: {},
  watch :{
    "localData.isShow" : "onChangeShow"
  },

  data(){
    return {
      localData : {
        isShow : false,
        titleAlignCenter : false,
        alignCenter : true,

        title : "확인",
        titleHtml : "",
        okButtonText : "확인",
        cancelButtonText : "취소",

        result : undefined
      },

      userInfo : {...DEF_USERINFO},
    }
  },

  mounted() {
    const _vm = this;

    function checkPass() {
			extend('checkPass', {
			validate: value => {
				return _vm.userInfo.userPw == value || _vm.$t('') || '비밀번호가 일치하지 않습니다.';
			},
			});
		}
		checkPass();

    cochingValidation();

    _vm.init();
  },

  methods : {
    init() {
      const _vm = this;
    },

    convertToHtml(pPlanText, isTitle){
      const _vm = this;
      
      const pt = (pPlanText || "").trim();
      if("" == pt){
        return "";
      }

      let retVal = "";
      const spLine = pPlanText.split("\n");
      if(isTitle){
        retVal = spLine.join("<br/>\n");
      }else{
        for(const tLine of spLine){
          retVal += `<p>${tLine}</p>\n`;
        }
      }
      

      return retVal
    },

    open(options){
      const _vm = this;

      const ld = _vm.localData;
      ld.isShow = true;
      ld.titleAlignCenter = options.titleAlignCenter == false ? false : true;
      ld.alignCenter = options.alignCenter == false ? false : true;
      ld.title = (options.title || "확인").trim();
      ld.okButtonText = (options.okButtonText || "확인").trim();
      ld.cancelButtonText = (options.cancelButtonText || "취소").trim();
      
      if("" == ld.titleHtml){
        ld.titleHtml = _vm.convertToHtml(ld.title, true);
      }

      _vm.userInfo = {...DEF_USERINFO};
      _vm.$refs.resetPasswdForm.reset();
      
      return new Promise( (resolve, reject) => {
        ld.result = resolve;
      })
    },

    async ok(){
      const _vm = this;

      const isValid = await _vm.$refs.resetPasswdForm.validate();
      if(!isValid){
        return;
      }

      const ld = _vm.localData;
      ld.isShow = false;
      ld.result({..._vm.userInfo});
    },

    cancel(){
      const _vm = this;

      const ld = _vm.localData;
      ld.isShow = false;
      ld.result(null);
    },

    onChangeShow(){
      const _vm = this;

      const newVal =_vm.localData.isShow;
      
      if(newVal == true){
        $(_vm.$refs["erns_reset_passwd_modal"]).fadeIn(300);
        return;
      }

      $(_vm.$refs["erns_reset_passwd_modal"]).fadeOut(300);
      $("body").css("overflow", "visible");
    },
  }
}
</script>
<style lang="scss" scoped>
.modal-header .title{
  padding-right: 0rem;
}     
</style>

<style lang="scss">

</style>
