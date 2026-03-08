<template>
   <!--section-->
   <section>
        <div class="inner">
          <div class="container">
            <div class="content-wrap">
              <!--join form-->
              <div class="join-form">
                <div class="title-wrap">
                  <div v-if="'PARTNER' === membType" class="h1">{{ $t('') || '원료사' }} 
                    <span class="text-normal">{{ $t('') || '회원가입' }}</span>
                  </div>
                  <div v-if="'USER' === membType" class="h1">{{ $t('') || '일반' }} 
                    <span class="text-normal">{{ $t('') || '회원가입' }}</span>
                  </div>
                  <div class="info"><span class="text-primary">*</span>{{ $t('') || '필수 입력 정보' }}</div>
                </div>
                <validation-observer ref="membInfoForm" #default="{ invalid }">
                  <div v-if="updateInvalidState(invalid)"></div>
                  <!--input-->
                  <div class="input-wrap">
                    <div class="label-set">
                      <label>{{ $t('') || '아이디' }}<span class="text-primary">*</span></label>
                      <validation-provider #default="{ errors }" tag="div"
                        class="input-set" :name="$t('') || '아이디'" rules="required|cochingUserId">
                          <div class="btn-set">
                            <input v-model="userData.membInfo.membId" type="text" />
                            <button @click="checkDuplicate" 
                              :disabled="errors.length > 0" type="button" class="btn btn-sm btn-gray-outline">중복확인</button>
                          </div>
                          <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                          <div :class="{ 'active' : '' !== userData.membInfo.membIdConfirm, 
                            'error': !userData.membInfo.membIdConfirm, 'success': true === userData.membInfo.membIdConfirm }" 
                            class="info">{{ MSG.duplicateId }}</div>
                      </validation-provider>
                    </div>
                    <validation-provider #default="{ errors }"
                      :name="$t('') || '아이디 체크'" rules="required|duplicate">
                      <input v-model="userData.membInfo.membIdConfirm" type="hidden" />
                    </validation-provider>

                    <div class="label-set">
                      <label>{{ $t('') || '이름' }}<span class="text-primary">*</span></label>
                      <validation-provider #default="{ errors }" tag="div"
                        class="input-set" :name="$t('') || '이름'" rules="required">
                          <input v-model="userData.membInfo.membName" type="text" />
                          <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                      </validation-provider>
                    </div>

                    <div class="label-set">
                      <label>{{ $t('') || '닉네임' }}<span class="text-primary">*</span></label>
                      <validation-provider #default="{ errors }" tag="div"
                        class="input-set" :name="$t('') || '닉네임'" rules="required|engHanNumber3">
                          <div class="btn-set">
                            <input v-model="userData.membInfo.nickname" type="text" />
                            <button @click="checkDuplicateNick" 
                              :disabled="errors.length > 0" type="button" class="btn btn-sm btn-gray-outline">중복확인</button>
                          </div>
                          <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                          <div :class="{'active' : '' !== userData.membInfo.nicknameConfirm, 
                            'error': !userData.membInfo.nicknameConfirm, 'success': userData.membInfo.nicknameConfirm }" 
                            class="info">{{ MSG.duplicateNick }}</div>
                      </validation-provider>
                    </div>
                    <validation-provider #default="{ errors }"
                      :name="$t('') || '닉네임 체크'" rules="required|duplicateNick">
                      <input v-model="userData.membInfo.nicknameConfirm" type="hidden" />
                    </validation-provider>

                    <div class="label-set">
                      <label>{{ $t('') || '이메일' }}<span class="text-primary">*</span></label>
                      <validation-provider #default="{ errors }" tag="div"
                        class="input-set" :name="$t('') || '이메일'" rules="required|email">
                        <div class="btn-set">
                          <input v-model="userData.membInfo.email" type="email" />
                          <button @click="checkDuplicateEmail" 
                            :disabled="errors.length > 0" type="button" class="btn btn-sm btn-gray-outline">중복확인</button>
                        </div>
                        <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                          <div :class="{'active' : '' !== userData.membInfo.emailConfirm, 
                            'error': !userData.membInfo.emailConfirm, 'success': userData.membInfo.emailConfirm }" 
                            class="info">{{ MSG.duplicateEmail }}</div>
                      </validation-provider>
                    </div>
                    <validation-provider #default="{ errors }"
                      :name="$t('') || '이메일 체크'" rules="required|duplicateEmail">
                      <input v-model="userData.membInfo.emailConfirm" type="hidden" />
                    </validation-provider>

                    <div class="label-set">
                      <label>{{ $t('') || '비밀번호' }}<span class="text-primary">*</span></label>
                      <validation-provider #default="{ errors }" tag="div"
                        class="input-set" :name="$t('') || '비밀번호'" rules="required|no-whitespace">
                          <input v-model="userData.membInfo.pswd" type="password" />
                          <!--성공 메세지 노출 active-->
                          <div :class="{'disabled' : !pswdValid.isLength}" class="info success active">10자 이상 입력</div>
                          <!--조건 충족 시 disabled 삭제-->
                          <div :class="{'disabled' : !pswdValid.isText}" class="info success active">영어 대/소문자, 숫자, 특수문자 중 2가지 이상 조합</div>
                      </validation-provider>
                    </div>
                    <div class="label-set">
                      <label>{{ $t('') || '비밀번호 확인' }}<span class="text-primary">*</span></label>
                      <validation-provider ref="pswdConfirm" #default="{ errors }" tag="div"
                        class="input-set" :name="$t('') || '비밀번호 확인'" rules="required|checkPass">
                        <input v-model="userData.membInfo.pswdConfirm" type="password" />
                        <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                      </validation-provider>
                    </div>
                    <div v-if="'PARTNER' === membType" class="label-set">
                      <label>{{ $t('') || '원료사' }}<span class="text-primary">*</span></label>
                      <div class="input-set">
                        <validation-provider tag="div" #default="{ errors }" rules="" class="chehck-radio-flex">
                          <div v-for="(cItem, idx) of CODES.CH007">
                            <div class="radiobox">
                              <input :id="'type-'+cItem.etc1"
                                v-model="userData.compInfo.nation" type="radio" name="type" :value="cItem.etc1">
                              <label :for="'type-'+cItem.etc1" class="radiobox-label">{{ cItem.codeName }}</label>
                            </div>
                          </div>
                        </validation-provider>
                      </div>
                    </div>
                    <div v-if="'PARTNER' === membType" class="label-set">
                      <label>{{ $t('') || '원료사명' }}<span class="text-primary">*</span></label>
                      <validation-provider #default="{ errors }" tag="div"
                        class="input-set" :name="$t('') || '원료사명'" rules="required">
                          <input v-model="userData.compInfo.ptnName" type="text" />
                          <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                      </validation-provider>
                    </div>
                    <div v-if="membType === 'USER' || (membType === 'PARTNER' && userData.compInfo.nation === '001')" class="label-set">
                      <label>{{ $t('') || '연락처' }}<span class="text-primary">*</span></label>
                      <validation-provider #default="{ errors }" tag="div"
                        class="input-set" :name="$t('') || '연락처'" rules="required|telOnlyNumber">
                          <input v-model="userData.membInfo.phone" 
                            @input="filterNumber" type="tel" :placeholder="$t('') || '‘-’없이 입력해 주세요'" />
                          <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                      </validation-provider>
                    </div>
                    <div v-if="'PARTNER' === membType && '001' === userData.compInfo.nation" class="label-set">
                      <label>{{ $t('') || '사업자 등록번호' }}<span class="text-primary">*</span></label>
                      <validation-provider #default="{ errors }" tag="div"
                       class="input-set" :name="$t('') || '사업자 등록번호'" rules="required|digits:10">
                          <InputBizNumber
                            v-model="userData.compInfo.ptnLic" 
                            :placeholder="$t('') || '‘-’없이 입력해 주세요'">
                          </InputBizNumber>
                          <div class="info error" :class="{ 'active': errors.length > 0 }">{{ errors[0] || status }}</div>
                      </validation-provider>
                    </div>
                    <div v-if="'PARTNER' === membType && '002' === userData.compInfo.nation" class="label-set">
                      <label>{{ $t('') || '국가' }}</label>
                      <input v-model="userData.compInfo.nationName" type="text" />
                    </div>
                    <div v-if="'PARTNER' === membType" class="label-set">
                      <label>{{ $t('') || '홈페이지 URL' }}</label>
                      <validation-provider #default="{ errors }" tag="div"
                        class="input-set" :name="$t('') || '홈페이지 URL'" rules="">
                          <input v-model="userData.compInfo.pageUrl"  type="url" :placeholder="$t('') || 'https://'" />
                          <div class="info error" :class="{ 'active': errors.length > 0 }">{{ errors[0] || status }}</div>
                      </validation-provider>
                    </div>
                    <div v-if="'PARTNER' === membType && '002' === userData.compInfo.nation" class="label-set">
                      <label>{{ $t('') || 'Youtube URL' }}</label>
                      <input v-model="userData.compInfo.ytUrl"  type="url" :placeholder="$t('') || 'https://'" />
                    </div>
                    <div v-if="'PARTNER' === membType && '002' === userData.compInfo.nation" class="label-set">
                      <label>{{ $t('') || '원료사 소개' }}</label>
                      <textarea v-model="userData.compInfo.ptnIntro" type="text" rows="5"/>
                    </div>
                    <!-- <div class="label-set">
                      <label>로고 이미지 업로드</label>
                      <div class="img-upload-container">
                        <input type="file" id="fileInput" accept="image/*" />
                        <div class="drop-area">
                          <p class="upload-text">파일을 마우스로 끌어 오거나 클릭하세요</p>
                          <img id="previewImage" src="" alt="Image Preview" />
                          <button id="deleteButton" type="button" class="ic-xsm ic-delete-wh"></button>
                        </div>
                      </div>
                    </div> -->
                  </div>
                  
                  <!-- agree -->
                  <TermsAgree
                    ref="termsAgree"
                    :useAllCheckbox='true'
                    :title="$t('') || '전체 약관 동의'"
                    :termsData = termsData
                    @change="onChangeTermData"
                  ></TermsAgree>

                  <!--button-->
                  <div class="btn-area">
                    <button @click="onSubmit"
                      :class="{'btn-disabled': 
                        !(
                          userData.joinValid 
                          && userData.termsValid
                          && pswdValid.isLength
                          && pswdValid.isText
                        ) 
                      , 'btn-primary': 
                        (
                          userData.joinValid 
                          && userData.termsValid
                          && pswdValid.isLength
                          && pswdValid.isText
                        )
                      }"
                      type="button" class="btn btn-lg">회원가입</button>
                  </div>
                </validation-observer>
              </div>
            </div>
          </div>

          <AlertModal ref="alertModal"></AlertModal>
          <ConfirmModal ref="confirmModal"></ConfirmModal>

        </div>
      </section>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import { extend } from 'vee-validate';
import { cochingValidation } from '@/@core/utils/validations/validations';
import { validatorNoSpacesMin10, validatorContainsAll } from '@/@core/utils/validations/validators';

import { getCodes } from '@/api/coching/comm/code';
import { userJoin, partnerJoin, chkDuplicate } from '@/api/coching/comm/join';
import TermsAgree from '@/components/termsAgree.vue';
import InputBizNumber from '@/components/InputBizNumber.vue';

const DEF_MEMB_INF = {
    membSeq: null,
    membId: '',
    membIdConfirm: '',
    membType: '',
    membName: '',
    nickname: '',
    nicknameConfirm: '',
    email: '',
    emailConfirm: '',
    pswd: '',
    pswdConfirm: '',
    phone: '',
};

const DEF_COMPANY_INF= {
  ptnSeq: 0,
  nation: '001',
  ptnName: '',
  ptnLic: '',
  pageUrl: '',
  logoFileId: null, 
  nationName: '',
  ytUrl: '',
  ptnIntro: '',
}

export default {
  name: 'coching-user-join',
  mixins: [ernsUtils],
  components: {
    InputBizNumber,
    TermsAgree,
},
  computed: {
    
	},
	watch: {
    'userData.membInfo.membId' : function(val){
      const _vm = this;

      if(val.length == 0) {
        _vm.MSG.duplicateId = '';
      }
      
      _vm.userData.membInfo.membIdConfirm = '';
    },
    'userData.membInfo.nickname' : function(val){
      const _vm = this;

      if(val.length == 0) {
        _vm.MSG.duplicateNick = '';
      }
      
      _vm.userData.membInfo.nicknameConfirm = '';
    },
    'userData.membInfo.email' : function(val){
      const _vm = this;

      if(val.length == 0) {
        _vm.MSG.duplicateEmail = '';
      }
      
      _vm.userData.membInfo.emailConfirm = '';
    },
    'userData.membInfo.pswd' : function(val){
      const _vm = this;

      _vm.pswdValid.isLength = validatorNoSpacesMin10(val);

      _vm.pswdValid.isText = validatorContainsAll(val);

      if(val != ''){
        _vm.$refs.pswdConfirm.validate();
      }
    },
  },
	props: {
    membType: {
        type: String,
        default: ''
     }
  },
	data() {
		return {
      localInvalidState: false,
      status: '',

      CODES: {
        CH007: [],
      },
      termsData : [
        {localeTermsCd : "001", requiredYn : "Y", value : false},
        {localeTermsCd : "002", requiredYn : "Y", value : false},
      ],

      userData :{
        joinValid : false,

        compInfo : {...DEF_COMPANY_INF},
        membInfo : {...DEF_MEMB_INF},

        termsValid : false,
        termsInfo : null,

      },

      //중복체크 문구
      MSG: {
        duplicateId: '',
        duplicateNick: '',
        duplicateEmail: '',
      },

      //비밀번호 validation
      pswdValid:{
        isLength: false,
        isText: false,
      },

    }
  },
  async mounted(){
    const _vm = this;

    function duplicate() {
      extend('duplicate', {
        validate: value => {
          return value == true || _vm.$t('') || '아이디 중복확인을 해주세요.';
      },
		  });
	  } 
    // 메시지 업데이트 호출
    duplicate();

    function duplicateNick() {
      extend('duplicateNick', {
        validate: value => {
          return value == true || _vm.$t('') || '닉네임 중복확인을 해주세요.';
      },
		  });
	  } 
    // 메시지 업데이트 호출
    duplicateNick();

    function duplicateEmail() {
      extend('duplicateEmail', {
        validate: value => {
          return value == true || _vm.$t('') || '이메일 중복확인을 해주세요.';
      },
		  });
	  } 
    // 메시지 업데이트 호출
    duplicateEmail();

    function checkPass() {
      extend('checkPass', {
        validate: value => {
          return _vm.userData.membInfo.pswd == _vm.userData.membInfo.pswdConfirm || _vm.$t('') || '비밀번호가 일치하지 않습니다.';
      },
		  });
	  } 
    // 메시지 업데이트 호출
    checkPass();

    cochingValidation();

    await _vm.loadCodes();
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
    async loadCodes(){
      const _vm = this;

      const codeRes = await getCodes({grpCode:"CH007", etc5:_vm.$i18n.locale});
        const { resultCode, resultFailMessage, resultData } = codeRes;

        _vm.CODES.CH007 = [...resultData.list];
    },
    async checkDuplicate(){
      //아이디 중복체크
      const _vm = this;

      if('' == _vm.userData.membInfo.membId){
        await _vm.$refs["alertModal"].open({
          title: _vm.$t('') || '중복확인',
          titleHtml : _vm.$t('') || '아이디를 입력해주세요.'
        });

        return;
      }

      _vm.loading(true);
      try{
        const res = await chkDuplicate({membId: _vm.userData.membInfo.membId});
        const { resultCode, resultFailMessage, resultData } = res;

        _vm.loading(false);
        if(!resultData.isDuplicate){
          _vm.userData.membInfo.membIdConfirm = true;
          _vm.MSG.duplicateId  = _vm.$t('') || '사용 가능한 아이디입니다.';
        }else{
          _vm.userData.membInfo.membIdConfirm = false;
          _vm.MSG.duplicateId  = _vm.$t('') || '이미 사용 중인 아이디입니다.';
        }
        
      } catch(err) {
              _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },
    async checkDuplicateNick(){
      //닉네임 중복체크
      const _vm = this;

      if('' == _vm.userData.membInfo.nickname){
        await _vm.$refs["alertModal"].open({
          title: _vm.$t('') || '중복확인',
          titleHtml : _vm.$t('') || '닉네임을 입력해주세요.'
        });

        return;
      }

      _vm.loading(true);
      try{
        const res = await chkDuplicate({nickname:  _vm.userData.membInfo.nickname});
        const { resultCode, resultFailMessage, resultData } = res;

        _vm.loading(false);
        if(!resultData.isDuplicate){
          _vm.userData.membInfo.nicknameConfirm = true;
          _vm.MSG.duplicateNick = _vm.$t('') || '사용 가능한 닉네임입니다.';
        }else{
          _vm.userData.membInfo.nicknameConfirm = false;
          _vm.MSG.duplicateNick = _vm.$t('') || '이미 사용 중인 닉네임입니다.';
        }
        
      } catch(err) {
              _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },

    async checkDuplicateEmail(){
      //닉네임 중복체크
      const _vm = this;

      if('' == _vm.userData.membInfo.email){
        await _vm.$refs["alertModal"].open({
          title: _vm.$t('') || '중복확인',
          titleHtml : _vm.$t('') || '이메일을을 입력해주세요.'
        });

        return;
      }

      _vm.loading(true);
      try{
        const res = await chkDuplicate({email:  _vm.userData.membInfo.email});
        const { resultCode, resultFailMessage, resultData } = res;

        _vm.loading(false);
        if(!resultData.isDuplicate){
          _vm.userData.membInfo.emailConfirm = true;
          _vm.MSG.duplicateEmail = _vm.$t('') || '사용 가능한 이메일입니다.';
        }else{
          _vm.userData.membInfo.emailConfirm = false;
          _vm.MSG.duplicateEmail = _vm.$t('') || '이미 사용 중인 이메일입니다.';
        }
        
      } catch(err) {
              _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },

    filterNumber(event) {
      const _vm = this;
      _vm.userData.membInfo.phone = event.target.value.replace(/[^0-9]/g, "");
    },

    onChangeTermData(data){
      const _vm = this;

      _vm.userData.termsValid = data.valid;
      _vm.userData.termsData = data.data;
    },

    //양식의 상태값을 확인
    updateInvalidState(invalid) {
      const _vm = this;
      const isChanged = this.localInvalidState != invalid;
      this.localInvalidState = invalid;
      if(isChanged || !invalid){
        //상태값이 변경되면 부모에게 알림
        _vm.userData.joinValid = !this.localInvalidState;
      }
      return false; // v-if에서 항상 false를 반환하여 실제 DOM에 렌더링되지 않게 함
    },


    async onSubmit(){
      const _vm = this;

      if(_vm.userData.joinValid 
          && _vm.userData.termsValid){
          //TODO 회원가입
          _vm.loading(true);

          try{
            const param = _vm.setParamData();        

            let res;
            if('PARTNER' == _vm.membType){
              //파트너사
              res = await partnerJoin(param);
            }else{
              //일반 사용자
              res = await userJoin(param);
            }
            const { resultCode, resultFailMessage, resultData } = res;

            _vm.loading(false);
            if(resultCode == '0000'){
              const ret = await _vm.$refs["alertModal"].open({
                title: _vm.$t('') || '회원가입 완료',
                titleHtml : _vm.$t('') || '회원가입이 완료되었습니다.'
              });

              if(ret){
                this.$router.push({name:'coching-login'});
              }
            }else{
              await _vm.$refs["alertModal"].open({
                title: _vm.$t('join.instead.label008') || '오류',
                content : resultFailMessage || _vm.$t('join.instead.label009') || '알수 없는 오류가 발생했습니다.'
              });
            }

          } catch(err) {
            _vm.defaultApiErrorHandler(err); //기본에러처리
          } finally {
            _vm.loading(false);
          }
      }
    },

    setParamData(){
      const _vm = this;
      if('PARTNER' == _vm.membType){
        //파트너사
        _vm.userData.membInfo.membType = '002';
      }else{
        //일반 사용자
        _vm.userData.membInfo.membType = '003';
      }

      const termsCds = _vm.userData.termsData.map(term => term.localeTermsCd);
      const arrTermsCds = termsCds.join(",");

      const agreeYn = _vm.userData.termsData.map(term => term.value == true ? 'Y' : 'N' );
      const arrAgreeYn = agreeYn.join(",");

      const param = {
              ..._vm.userData.compInfo, 
              ..._vm.userData.membInfo, 
            };

      param.termsCd = arrTermsCds;
      param.agreeYn = arrAgreeYn;

      return param; 
    },

    docReady(){
      const _vm = this;      
    }
  }  
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">

</style>