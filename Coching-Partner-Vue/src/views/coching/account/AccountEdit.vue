<template>
  <!--section-->
  <section class="h-100">
    <div class="container h-100">
      <div class="content-wrap h-100">
        <!--title-->
        <div class="h1">{{ 'CREATE' == actType ? $t('') || '계정 정보 등록' : $t('') || '계정 정보 수정' }}</div>
        <!--content-->
        <div class="content-inner h-100 account-wrap">
          <!--account-edit-->
          <div class="account-edit">
            <!--img-->
            <div class="img">
              <img src="@/assets/images/ic-profile.svg" alt="profile" />
            </div>
            <!--form-->
            <validation-observer ref="membInfoForm" #default="{ invalid }">
              <div v-if="updateInvalidState(invalid)"></div>
              <!--input-wrap-->
              <div class="input-wrap">
                <div class="input-half">
                  <div class="label-set">
                    <label>{{ $t('') || '이름' }}<span class="text-primary">*</span></label>
                    <validation-provider #default="{ errors }" tag="div"
                      class="input-set" :name="$t('') || '이름'" rules="required">
                      <input v-model="membInfo.membName" type="text" placeholder="이름을 입력해 주세요" />
                      <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                    </validation-provider>
                  </div>
                  <div class="label-set">
                    <label>{{ $t('') || '아이디' }}<span class="text-primary">*</span></label>
                    <!-- 등록 -->
                    <validation-provider v-if="'CREATE' == actType"
                      #default="{ errors }" tag="div"
                      class="input-set" :name="$t('') || '아이디'" rules="required|userId">
                      <div class="btn-set">
                        <input v-model="membInfo.membId" type="text" placeholder="아이디를 입력해 주세요" />
                        <button @click="checkDuplicate" type="button" class="btn btn-sm btn-gray-outline">중복확인</button>
                      </div>
                      <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                      <div :class="{ 'active' : '' !== membInfo.membIdConfirm, 
                        'error': !membInfo.membIdConfirm, 'success': membInfo.membIdConfirm }" 
                        class="info">{{ MSG.duplicateId }}</div>
                    </validation-provider>

                    <!-- 수정 -->
                    <validation-provider v-if="'MODIFY' == actType"
                      #default="{ errors }" tag="div"
                      class="input-set" :name="$t('') || '아이디'" rules="required|userId">
                        <input v-model="membInfo.membId" type="text" disabled />
                    </validation-provider>
                  </div>
                </div>
                <validation-provider v-if="'CREATE' == actType" #default="{ errors }"
                      :name="$t('') || '아이디 체크'" rules="required|duplicate">
                      <input v-model="membInfo.membIdConfirm" type="hidden" />
                </validation-provider>
                <div v-if="'CREATE' == actType" class="input-half">
                  <div class="label-set">
                    <label>{{ $t('') || '비밀번호' }}<span class="text-primary">*</span></label>
                    <validation-provider #default="{ errors }" tag="div"
                    class="input-set" :name="$t('') || '비밀번호'" rules="required|no-whitespace">
                      <input v-model="membInfo.pswd" type="password" placeholder="새 비밀번호" />
                      <!--성공 메세지 노출 active-->
                      <div :class="{'disabled' : !pswdValid.isLength}" class="info success active">10자 이상 입력</div>
                      <!--조건 충족 시 disabled 삭제-->
                      <div :class="{'disabled' : !pswdValid.isText}" class="info success active">영어 대/소문자, 숫자, 특수문자 중 2가지 이상 조합</div>
                    </validation-provider>
                  </div>
                  <div class="label-set">
                    <label>{{ $t('') || '비밀번호 확인' }}<span class="text-primary">*</span></label>
                    <validation-provider #default="{ errors }" tag="div"
                    class="input-set" :name="$t('') || '비밀번호 확인'" rules="required|checkPass">
                      <input v-model="membInfo.pswdConfirm" type="password" placeholder="다시 한번 입력하세요" />
                      <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                    </validation-provider>
                  </div>
                </div>
                <div class="input-half">
                  <div class="label-set">
                    <label>{{ $t('') || '연락처' }}<span class="text-primary">*</span></label>
                    <validation-provider #default="{ errors }" tag="div"
                    class="input-set" :name="$t('') || '연락처'" rules="required|telOnlyNumber">
                      <input v-model="membInfo.phone" 
                        @input="filterNumber" type="tel" placeholder="'-'없이 입력해 주세요" />
                      <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                    </validation-provider>
                  </div>
                  <div class="label-set">
                    <label>{{ $t('') || '이메일' }}<span class="text-primary">*</span></label>
                    <validation-provider #default="{ errors }" tag="div"
                        class="input-set" :name="$t('') || '이메일'" rules="required|email">
                      <div class="btn-set">
                        <input v-model="membInfo.email" type="email" placeholder="이메일을 입력해 주세요" />
                        <button @click="checkDuplicateEmail" 
                          :disabled="membInfo.email === membBackup.email" type="button" class="btn btn-sm btn-gray-outline">중복확인</button>
                      </div>
                      <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                      <div :class="{'active' : '' !== membInfo.emailConfirm && membInfo.email !== membBackup.email, 
                        'error': !membInfo.emailConfirm, 'success': membInfo.emailConfirm }" 
                        class="info">{{ MSG.duplicateEmail }}</div>
                    </validation-provider>
                  </div>
                </div>
                <validation-provider #default="{ errors }"
                  :name="$t('') || '이메일 체크'" rules="required|duplicateEmail">
                  <input v-model="membInfo.emailConfirm" type="hidden" />
                </validation-provider>
                <div class="input-half">
                  <div class="label-set">
                    <label>{{ $t('') || '닉네임' }}<span class="text-primary">*</span></label>
                    <validation-provider #default="{ errors }" tag="div"
                      class="input-set" :name="$t('') || '닉네임'" rules="required|engHanNumber3">
                      <div class="btn-set">
                        <input v-model="membInfo.nickname" type="text" />
                        <button @click="checkDuplicateNick" 
                          :disabled="membInfo.nickname === membBackup.nickname" type="button" class="btn btn-sm btn-gray-outline">중복확인</button>
                      </div>
                      <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                      <div :class="{'active' : '' !== membInfo.nicknameConfirm && membInfo.nickname !== membBackup.nickname, 
                        'error': !membInfo.nicknameConfirm, 'success': membInfo.nicknameConfirm }" 
                        class="info">{{ MSG.duplicateNick }}</div>
                    </validation-provider>
                  </div>
                  <div class="label-set"></div>
                </div>
                <validation-provider #default="{ errors }"
                  :name="$t('') || '닉네임 체크'" rules="required|duplicateNick">
                  <input v-model="membInfo.nicknameConfirm" type="hidden" />
                </validation-provider>
                <div v-if="'MODIFY' == actType" class="title-wrap">
                  <div class="title">{{ $t('') || '계정 사용여부' }}</div>
                  <div class="chehck-radio-flex">
                    <div v-for="(item, idx) of CODES.UY_001" :key="idx">
                      <div class="radiobox">
                        <input v-model="membInfo.useYn" 
                          :id="'radio-'+item.value" type="radio" name="radio" :value="item.value"/>
                        <label :for="'radio-'+item.value" class="radiobox-label">{{ item.label }}</label>
                      </div>
                    </div>
                  </div>
                </div>
                <div v-if="'MODIFY' == actType" class="title-wrap">
                  <div class="title">{{ $t('') || '비밀번호 재설정' }}</div>
                  <a @click="onClickInitPasswd" href="javascript:;" class="text-link">{{ $t('') || '비밀번호 초기화' }}</a>
                </div>
                
              </div>
              <!--button-->
              <div class="btn-area">
                <button @click="onClickCancel" 
                  type="button" class="btn btn-lg btn-gray">{{ $t('') || '취소' }}</button>
                <button @click="onSubmit"
                  :class="{'btn-disabled': !membValid , 'btn-primary': membValid}"
                  type="button" class="btn btn-lg">{{ $t('') || '저장하기' }}</button>
              </div>
            </validation-observer>
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
import { extend } from 'vee-validate';
import { cochingValidation } from '@/@core/utils/validations/validations';
import { validatorNoSpacesMin10, validatorContainsAll } from '@/@core/utils/validations/validators';

import { getMemInfo, setUseYn, initPasswd } from '@/api/coching/member/member';
import { userJoin, userUpdate, chkDuplicate } from '@/api/coching/comm/join';
import { getCodes } from '@/api/coching/comm/code';


const DEF_MEMB_INF = {
    ptnSeq: '',
    membSeq: null,
    membId: '',
    membIdConfirm: '',
    email: '',
    emailConfirm: '',
    membName: '',
    pswd: '',
    pswdConfirm: '',
    phone: '',
    nickname: '',
    nicknameConfirm: '',
    useYn: '',
};

export default {
	name: 'coching-account-main',
	mixins: [ernsUtils],
	components: {
},
	computed: {    
  },
	watch: {
    'membInfo.membId' : function(val){
      const _vm = this;

      if(val.length == 0) {
        _vm.MSG.duplicateId = '';
        return;
      }
      
      _vm.membInfo.membIdConfirm = '';
    },
    'membInfo.nickname' : function(val){
      const _vm = this;

      if(val.length == 0) {
        _vm.MSG.duplicateNick = '';
        return;
      }

      if(val !== _vm.membBackup.nickname){
        _vm.membInfo.nicknameConfirm = '';
      }else{
        _vm.membInfo.nicknameConfirm = true;
      }

    },
    'membInfo.email' : function(val){
      const _vm = this;

      if(val.length == 0) {
        _vm.MSG.duplicateEmail = '';
        return;
      }

      if(val !== _vm.membBackup.email){
        _vm.membInfo.emailConfirm = '';
      }else{
        _vm.membInfo.emailConfirm = true;
      }

    },
    'membInfo.pswd' : function(val){
      const _vm = this;

      _vm.pswdValid.isLength = validatorNoSpacesMin10(val);

      _vm.pswdValid.isText = validatorContainsAll(val);
    },
	},
	props: {
    membSeq: {
      type: Number,
      default: 0,
    }
  },
	data() {
		return {
      localInvalidState: false,
      status: '',
      membValid : false,
      actType: 'CREATE',
      
      CODES: {
        UY_001 : [],
      },
			membInfo : {...DEF_MEMB_INF},
      membBackup: {...DEF_MEMB_INF},

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
	async mounted() {
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

    function duplicateEmail() {
      extend('duplicateEmail', {
        validate: value => {
          return value == true || _vm.$t('') || '이메일 중복확인을 해주세요.';
      },
		  });
	  } 
    // 메시지 업데이트 호출
    duplicateEmail();

    function duplicateNick() {
      extend('duplicateNick', {
        validate: value => {
          return value == true || _vm.$t('') || '닉네임 중복확인을 해주세요.';
      },
		  });
	  } 
    // 메시지 업데이트 호출
    duplicateNick();

    function checkPass() {
      extend('checkPass', {
        validate: value => {
          return _vm.membInfo.pswd == value || _vm.$t('') || '비밀번호가 일치하지 않습니다.';
      },
		  });
	  } 
    // 메시지 업데이트 호출
    checkPass();

    cochingValidation();

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

        await _vm.loadCodes();
        
        if( 0 < _vm.membSeq){
          await _vm.myInfo();
          _vm.actType = 'MODIFY';
          _vm.membInfo.nicknameConfirm = true;
        }else{
          _vm.actType = 'CREATE';
        }

			} catch (err) {
				_vm.defaultApiErrorHandler(err); //기본에러처리
			} finally {
				_vm.loading(false);
			}
		},
    //멤버 정보 조회
    async myInfo(){
      const _vm = this;
      
      const res = await getMemInfo({membSeq: _vm.membSeq});
      const { resultCode, resultFailMessage, resultData } = res;

      _vm.membInfo = {...DEF_MEMB_INF, ...resultData};
      _vm.membBackup = {...DEF_MEMB_INF, ...resultData};
      
    },

    async loadCodes(){
      const _vm = this;

      _vm.CODES.UY_001 = [
        {value:"Y", label : _vm.$t('') || '사용'},
        {value:"N", label : _vm.$t('') || '미사용'}
      ];
    },
    async checkDuplicate(){
      //아이디 중복체크
      const _vm = this;

      _vm.loading(true);
      try{
        const res = await chkDuplicate({membId:  _vm.membInfo.membId});
        const { resultCode, resultFailMessage, resultData } = res;

        _vm.loading(false);
        if(!resultData.isDuplicate){
          _vm.membInfo.membIdConfirm = true;
          _vm.MSG.duplicateId = _vm.$t('') || '사용 가능한 아이디입니다.';
        }else{
          _vm.membInfo.membIdConfirm = false;
          _vm.MSG.duplicateId = _vm.$t('') || '이미 사용 중인 아이디입니다.';
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

      _vm.loading(true);
      try{
        const res = await chkDuplicate({email:  _vm.membInfo.email});
        const { resultCode, resultFailMessage, resultData } = res;

        _vm.loading(false);
        if(!resultData.isDuplicate){
          _vm.membInfo.emailConfirm = true;
          _vm.MSG.duplicateEmail = _vm.$t('') || '사용 가능한 이메일입니다.';
        }else{
          _vm.membInfo.emailConfirm = false;
          _vm.MSG.duplicateEmail = _vm.$t('') || '이미 사용 중인 이메일입니다.';
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

      _vm.loading(true);
      try{
        const res = await chkDuplicate({nickname:  _vm.membInfo.nickname});
        const { resultCode, resultFailMessage, resultData } = res;

        _vm.loading(false);
        if(!resultData.isDuplicate){
          _vm.membInfo.nicknameConfirm = true;
          _vm.MSG.duplicateNick = _vm.$t('') || '사용 가능한 닉네임입니다.';
        }else{
          _vm.membInfo.nicknameConfirm = false;
          _vm.MSG.duplicateNick = _vm.$t('') || '이미 사용 중인 닉네임입니다.';
        }
        
      } catch(err) {
              _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },

    filterNumber(event) {
      const _vm = this;
      _vm.membInfo.phone = event.target.value.replace(/[^0-9]/g, "");
    },

    async onClickInitPasswd(){
      //비밀번호 초기화
      const _vm = this;
      _vm.loading(true);
      try{
        await initPasswd({membSeq: _vm.membSeq});

        _vm.$refs["alertModal"].open({
          title: _vm.$t('') || '비밀번호 초기화',
          content : _vm.$t('') || '초기화 되었습니다.'
        });

      } catch(err) {
              _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }

    },

    async onSubmit(){
      const _vm = this;

      if(_vm.membValid){
          //TODO 회원가입
          _vm.loading(true);

          try{
            const param = _vm.setParamData();        

            let res;
            if('CREATE' == _vm.actType){
              //등록
              res = await userJoin(param);
            }else{
              //수정
              res = await userUpdate(param);
            }
            const { resultCode, resultFailMessage, resultData } = res;

            _vm.loading(false);
            if(resultCode == '0000'){
              const ret = await _vm.$refs["alertModal"].open({
                title: 'CREATE' == _vm.actType ? _vm.$t('') || '계정 등록' : _vm.$t('') || '계정 수정',
                titleHtml : 'CREATE' == _vm.actType ? _vm.$t('') || '등록되었습니다.' : _vm.$t('') || '저장되었습니다.'
              });

              if(ret){
                this.$router.push({name:'coching-account-main'});
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
     
      _vm.membInfo.membType = '004';
      _vm.membInfo.ptnSeq = _vm.partnerInfo.ptnSeq;
    
      const param = {
              ..._vm.membInfo, 
            };

      return param; 
    },

    onClickCancel(){
      //취소 -> 계정관리 이동
      const _vm = this;

      _vm.ermPushPage({name:'coching-account-main'});
    },

    //양식의 상태값을 확인
    updateInvalidState(invalid) {
      const _vm = this;
      const isChanged = this.localInvalidState != invalid;
      this.localInvalidState = invalid;
      if(isChanged || !invalid){
        //상태값이 변경되면 부모에게 알림
        _vm.membValid = !this.localInvalidState;
      }
      return false; // v-if에서 항상 false를 반환하여 실제 DOM에 렌더링되지 않게 함
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
#coching-account-edit {

}
</style>
