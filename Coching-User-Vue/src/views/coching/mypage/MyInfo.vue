<template>
  <!--section-->
  <!--section-->
  <section>
    <div class="container h-100">
      <!--content-->
      <div class="content">
        <!--common-content-->
        <div class="common-content">
          <!--content-title-wrap-->
          <MypageMenuBar></MypageMenuBar>

          <!--my-info-wrap-->
            <validation-observer ref="myInfoForm" #default="{invalid}"
              tag="div" class="my-info-wrap">
              <!--my-profile-->
              <div class="my-profile">
                <div class="img-wrap">
                  <div class="img">
                    <img v-if="userData.myInfo.profileId == null || eumLoginUser.userType == '004'" src="@/assets/images/ic-profile.svg" alt="profile" />
                    <img
                        :src="eumFileImagePath(userData.myInfo.profileId, '0', errorImg)"
                        @error="onImageError"
                        alt=""/>
                  </div>
                  <div v-if="eumLoginUser.userType != '004'" class="btn-camera">
                    <div @click="handleInputFile" class="ic-md ic-camera-gray-md"></div>
                    <input @change="handleFileChange" ref="fileInput" type="file" accept="image/*" class="img-upload" style="display: none" />
                  </div>
                </div>

                <div class="my-id">{{ eumLoginUser.userId }}</div>
              </div>
              <form>
                <!--input-->
                <div class="input-wrap">
                  <div class="label-set">
                    <label>{{ $t('') || '이름' }}<span class="text-primary">*</span></label>
                    <validation-provider #default="{ errors }" tag="div"
                      class="input-set" :name="$t('') || '이름'" rules="required">
                        <input v-model="userData.myInfo.membName" type="text" />
                        <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                    </validation-provider>
                  </div>
                  <div class="label-set">
                    <label>{{ $t('') || '닉네임' }}<span class="text-primary">*</span></label>
                    <validation-provider #default="{ errors }" tag="div"
                      class="input-set" :name="$t('') || '닉네임'" rules="required|engHanNumber3">
                      <div class="btn-set">
                        <input v-model="userData.myInfo.nickname" type="text" />
                        <button @click="checkDuplicateNick" 
                          :disabled="userData.myInfo.nickname === membBackup.nickname || errors.length > 0" type="button" class="btn btn-sm btn-gray-outline">중복확인</button>
                      </div>
                      <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                      <div :class="{'active' : '' !== userData.myInfo.nicknameConfirm && userData.myInfo.nickname !== membBackup.nickname, 
                        'error': !userData.myInfo.nicknameConfirm, 'success': userData.myInfo.nicknameConfirm }" 
                        class="info">{{ MSG.duplicateNick }}</div>
                    </validation-provider>
                  </div>
                  <validation-provider #default="{ errors }"
                    :name="$t('') || '닉네임 체크'" rules="required|duplicateNick">
                    <input v-model="userData.myInfo.nicknameConfirm" type="hidden" />
                  </validation-provider>
                  <div v-if="'002' === eumLoginUser.userType && '001' === partnerInfo.nation" class="label-set">
                    <label>{{ $t('') || '연락처' }}<span class="text-primary">*</span></label>
                    <validation-provider #default="{ errors }" tag="div"
                      class="input-set" :name="$t('') || '연락처'" rules="required|telOnlyNumber">
                        <input v-model="userData.myInfo.phone" type="tel" :placeholder="$t('') || '‘-’없이 입력해 주세요'" />
                        <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                    </validation-provider>
                  </div>
                  <div class="label-set">
                    <label>{{ $t('') || '이메일' }}<span class="text-primary">*</span></label>
                    <validation-provider #default="{ errors }" tag="div"
                        class="input-set" :name="$t('') || '이메일'" rules="required|email">
                      <div class="btn-set">
                        <input v-model="userData.myInfo.email" type="email" placeholder="이메일을 입력해 주세요" />
                        <button @click="checkDuplicateEmail" 
                          :disabled="userData.myInfo.email === membBackup.email || errors.length > 0" type="button" class="btn btn-sm btn-gray-outline">중복확인</button>
                      </div>
                      <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                      <div :class="{'active' : '' !== userData.myInfo.emailConfirm && userData.myInfo.email !== membBackup.email, 
                        'error': !userData.myInfo.emailConfirm, 'success': userData.myInfo.emailConfirm }" 
                        class="info">{{ MSG.duplicateEmail }}</div>
                    </validation-provider>
                  </div>
                  <validation-provider #default="{ errors }"
                    :name="$t('') || '이메일 체크'" rules="required|duplicateEmail">
                    <input v-model="userData.myInfo.emailConfirm" type="hidden" />
                  </validation-provider>
                  <div v-if="'002' === eumLoginUser.userType" class="label-set">
                    <label>{{ $t('') || '원료사명' }}<span class="text-primary">*</span></label>
                    <validation-provider #default="{ errors }" tag="div"
                      class="input-set" :name="$t('') || '원료사명'" rules="required">
                        <input v-model="userData.compInfo.ptnName" type="text" />
                        <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                    </validation-provider>
                  </div>
                  <div v-if="'002' === eumLoginUser.userType && '001' === partnerInfo.nation" class="label-set">
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
                  <div v-if="'002' === eumLoginUser.userType && '002' === partnerInfo.nation" class="label-set">
                    <label>{{ $t('') || '국가' }}</label>
                    <input v-model="userData.compInfo.nationName"/>
                  </div>
                  <div v-if="'002' === eumLoginUser.userType" class="label-set">
                    <label>{{ $t('') || '홈페이지' }}</label>
                    <validation-provider #default="{ errors }" tag="div"
                      class="input-set" :name="$t('') || '홈페이지'" rules="">
                        <input v-model="userData.compInfo.pageUrl"  type="url" :placeholder="$t('') || 'https://'" />
                        <div class="info error" :class="{ 'active': errors.length > 0 }">{{ errors[0] || status }}</div>
                    </validation-provider>
                  </div>
                  <div v-if="'002' === eumLoginUser.userType && '002' === partnerInfo.nation" class="label-set">
                    <label>{{ $t('') || 'Youtube URL' }}</label>
                    <input v-model="userData.compInfo.ytUrl"  type="url" :placeholder="$t('') || 'https://'" />
                  </div>
                  <div v-if="'002' === eumLoginUser.userType && '002' === partnerInfo.nation" class="label-set">
                    <label>{{ $t('') || '원료사 소개' }}</label>
                    <textarea v-model="userData.compInfo.ptnIntro" type="text" rows="5"/>
                  </div>
                  <div class="pw-change-wrap">
                    <button @click="onClickChangePswd" type="button" class="btn btn-sm btn-gray">비밀번호 변경하기</button>
                    <div v-if="userData.isChangePswd" class="input-wrap active">
                      <div class="label-set">
                        <label>현재 비밀번호<span class="text-primary">*</span> </label>
                        <validation-provider #default="{ errors }" tag="div"
                          class="input-set" :name="$t('') || '비밀번호'" rules="required|no-whitespace">
                            <input v-model="userData.myInfo.currentPswd" type="password" />
                            <div class="info error" :class="{ 'active': errors.length > 0 }">{{ errors[0] || status }}</div>
                        </validation-provider>
                      </div>
                      <div class="label-set">
                        <label>새 비밀번호<span class="text-primary">*</span></label>
                        <validation-provider #default="{ errors }" tag="div"
                          class="input-set" :name="$t('') || '비밀번호'" rules="required|no-whitespace">
                            <input v-model="userData.myInfo.pswd" type="password" />
                            <!--성공 메세지 노출 active-->
                            <div :class="{'disabled' : !pswdValid.isLength}" class="info success active">10자 이상 입력</div>
                            <!--조건 충족 시 disabled 삭제-->
                            <div :class="{'disabled' : !pswdValid.isText}" class="info success active">영어 대/소문자, 숫자, 특수문자 중 2가지 이상 조합</div>
                        </validation-provider>
                      </div>
                      <div class="label-set">
                        <label>새 비밀번호 확인<span class="text-primary">*</span> </label>
                        <validation-provider ref="pswdConfirm" #default="{ errors }" tag="div"
                          class="input-set" :name="$t('') || '비밀번호 확인'" rules="required|checkPass">
                          <input v-model="userData.myInfo.pswdConfirm" type="password" />
                          <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                        </validation-provider>
                      </div>
                    </div>
                  </div>
                </div>
                <!--button-->
                <div class="btn-area">
                  <button @click="onSubmit"
                    type="button"
                    :disabled="invalid"
                    :class="{
                      'btn-disabled': (
                        invalid || (userData.isChangePswd && !(pswdValid.isLength && pswdValid.isText))
                      )
                      , 'btn-primary': (
                        !invalid && (!userData.isChangePswd || (pswdValid.isLength && pswdValid.isText))
                      )
                    }"
                    class="btn btn-lg w-100">저장하기</button>
                </div>
              </form>
            </validation-observer>
        </div>
      </div>
    </div>

    <!--썸네일 cropper 모달-->
    <ImageCropperModal ref="cropperModal"></ImageCropperModal>

    <AlertModal ref="alertModal"></AlertModal>
    <ConfirmModal ref="confirmModal"></ConfirmModal>
  </section>
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';
import { extend } from 'vee-validate';
import { cochingValidation } from '@/@core/utils/validations/validations';
import { validatorNoSpacesMin10, validatorContainsAll } from '@/@core/utils/validations/validators';

import { getMyInfo, updateMemb, updatePartner } from '@/api/coching/member/member';
import { chkDuplicate } from '@/api/coching/comm/join';
import InputBizNumber from '@/components/InputBizNumber.vue';
import Advertise from '@/components/Advertise.vue';
import MypageMenuBar from './MypageMenuBar.vue';
import ImageCropperModal from '@/components/modal/ImageCropperModal.vue';

import ERROR_IMG from '@/assets/images/profile_noImg.png';

const DEF_FILE ={
  fileId: null,
  fileName: '',
  file: null,
  blobUrl: null, // Blob URL 추가
};

const DEF_MEMB_INF = {
  ptnSeq: '',
  membSeq: null,
  membId: '',
  email: '',
  emailConfirm: true,
  membName: '',
  currentPswd: "",
  pswd: '',
  pswdConfirm: '',
  phone: '',
  nickname: '',
  nicknameConfirm: true,
  useYn: '',
  profileFile: {...DEF_FILE},
  profileId: null,
};

const DEF_COMP_INF = {
  ptnSeq: '',
  ptnName: '',
  ptnLic: '',
  pageUrl: '',
  nationName: '',
  ytUrl: '',
  ptnIntro: '',
}

export default {
	name: 'coching-mypage-info',
	mixins: [ernsUtils],
	components: {
    InputBizNumber,
    Advertise,
    MypageMenuBar,
    ImageCropperModal,
},
	computed: {
    errorImg(){
      return ERROR_IMG;
    }
  },
	watch: {
    'userData.myInfo.nickname' : function(val){
      const _vm = this;

      if(val.length == 0) {
        _vm.MSG.duplicateNick = '';
      }

      if(val !== _vm.membBackup.nickname){
        _vm.userData.myInfo.nicknameConfirm = '';
      }else{
        _vm.userData.myInfo.nicknameConfirm = true;
      }

    },
    'userData.myInfo.email' : function(val){
      const _vm = this;

      if(val.length == 0) {
        _vm.MSG.duplicateEmail = '';
      }

      if(val !== _vm.membBackup.email){
        _vm.userData.myInfo.emailConfirm = '';
      }else{
        _vm.userData.myInfo.emailConfirm = true;
      }
    },
    'userData.myInfo.pswd' : function(val){
      const _vm = this;

      _vm.pswdValid.isLength = validatorNoSpacesMin10(val);

      _vm.pswdValid.isText = validatorContainsAll(val);

      if(val != ''){
        _vm.$refs.pswdConfirm.validate();
      }
    },
	},
	props: {
    
  },
	data() {
		return {
      status: '',
      userData :{
        myInfo : {...DEF_MEMB_INF},
        compInfo : {...DEF_COMP_INF},
        isChangePswd: false,
      },
      membBackup: {...DEF_MEMB_INF},
      //중복체크 문구
      MSG: {
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

    //이메일중복체크
    function duplicateEmail() {
      extend('duplicateEmail', {
        validate: value => {
          return value == true || _vm.$t('') || '이메일 중복확인을 해주세요.';
      },
		  });
	  } 
    duplicateEmail();

    //닉네임중복체크
    function duplicateNick() {
      extend('duplicateNick', {
        validate: value => {
          return value == true || _vm.$t('') || '닉네임 중복확인을 해주세요.';
      },
		  });
	  } 
    duplicateNick();

    //비밀번호 확인
    function checkPass() {
      extend('checkPass', {
        validate: value => {
          return _vm.userData.myInfo.pswd == value || _vm.$t('') || '비밀번호가 일치하지 않습니다.';
      },
		  });
	  } 
    checkPass();

    cochingValidation();

		_vm.docReady();
		_vm.fetchData();
	},
	beforeDestroy() {
		const _vm = this;
	},
	methods: {

     onImageError(event) {
      const targetObj = event.target;
      targetObj.src = this.errorImg; // 대체 이미지로 설정
      targetObj.style.width = '84px'; // 원하는 너비 설정
      targetObj.style.height = '84px'; // 원하는 높이 설정
    },

		//데이터 가져오는 부분(사용하지 필요없어도 반드시 선언)
		async fetchData() {
			const _vm = this;

			_vm.loading(true);
			try {

				await _vm.loadMyInfo();

			} catch (err) {
				_vm.defaultApiErrorHandler(err); //기본에러처리
			} finally {
				_vm.loading(false);
			}
		},

    //내정보 로드
    async loadMyInfo() {
      const _vm = this;
      
      const myInfoRes = await getMyInfo({});
      const { resultCode, resultFailMessage, resultData } = myInfoRes;
      _vm.userData.myInfo = {...DEF_MEMB_INF, ...resultData};
      _vm.membBackup = {...DEF_MEMB_INF, ...resultData};
      if('002' === _vm.eumLoginUser.userType){
        _vm.userData.compInfo = {...DEF_COMP_INF, ...resultData};
      }
    },

    handleInputFile(){
      this.$refs.fileInput.click();
    },
    //File click
    handleFileChange(event) {
      this.handleFileUpload(event.target.files);
      event.target.value = ''; // 파일 입력 필드 초기화
    },

    async handleFileUpload(files) {
      const _vm = this;
      const allowedExtensions = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'];

      if (files.length > 0) {
        for(let i = 0; i < files.length; i++) {
          const file = files[i];
          const extension = file.name.split('.').pop().toLowerCase();

          if (!allowedExtensions.includes(extension)) {
            alert(`허용되지 않는 파일 형식입니다: ${file.name}`);
            continue; // 다음 파일로 넘어감
          }

          const MAX_SIZE_MB = 50 * 1024 * 1024;
          if (file.size > MAX_SIZE_MB) {
            alert("파일 크기는 50MB 이하만 업로드할 수 있습니다.");
            return;
          }

          const blobUrl = URL.createObjectURL(file);
          _vm.userData.myInfo.profileFile = {
            fileId: _vm.userData.myInfo.profileId || null,
            fileName: file.name,
            file: file,
            blobUrl: blobUrl, // Cropper의 src로 사용
          };
          
          _vm.onclickCopperModal();
         
        }
      }
    },

    //썸네일 cropper 모달 열기
    async onclickCopperModal() {
      const _vm = this;
      
      const result = await _vm.$refs["cropperModal"].open({
        file: _vm.userData.myInfo.profileFile,
      });

      if(result){
        await _vm.loadMyInfo();
      }
    },

    async checkDuplicateEmail(){
      //이메일 중복체크
      const _vm = this;

      _vm.loading(true);
      try{
        const res = await chkDuplicate({email: _vm.userData.myInfo.email});
        const { resultCode, resultFailMessage, resultData } = res;

        _vm.loading(false);
        if(!resultData.isDuplicate){
          _vm.userData.myInfo.emailConfirm = true;
          _vm.MSG.duplicateEmail = _vm.$t('') || '사용 가능한 이메일입니다.';
        }else{
          _vm.userData.myInfo.emailConfirm = false;
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
        const res = await chkDuplicate({nickname:  _vm.userData.myInfo.nickname});
        const { resultCode, resultFailMessage, resultData } = res;

        _vm.loading(false);
        if(!resultData.isDuplicate){
          _vm.userData.myInfo.nicknameConfirm = true;
          _vm.MSG.duplicateNick = _vm.$t('') || '사용 가능한 닉네임입니다.';
        }else{
          _vm.userData.myInfo.nicknameConfirm = false;
          _vm.MSG.duplicateNick = _vm.$t('') || '이미 사용 중인 닉네임입니다.';
        }
        
      } catch(err) {
              _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },

    onClickChangePswd(){
      const _vm = this;
      _vm.userData.isChangePswd = !_vm.userData.isChangePswd;
    },

    //업데이트
    async onSubmit(){
      const _vm = this;
          
      _vm.loading(true);

      try{
          const params = _vm.getUpdateParam();

          if (Object.keys(params).length === 0) {
            // 변경된 값이 없는 경우
            _vm.loading(false);
            await _vm.$refs["alertModal"].open({
              title: _vm.$t('') || '개인정보 수정',
              titleHtml: _vm.$t('') || '변경된 정보가 없습니다.',
            });
            return;
          }

          let res;
          if('002' === _vm.eumLoginUser.userType){
            res = await updatePartner(params);  
          }else{
            res = await updateMemb(params);
          }
          
          const { resultCode, resultFailMessage, resultData } = res;

          _vm.loading(false);
          const ret = await _vm.$refs["alertModal"].open({
            title: _vm.$t('') || '수정 완료',
            titleHtml : _vm.$t('') || '수정이 완료되었습니다.'
          });

          if(ret){
            _vm.userData.isChangePswd = false;
            _vm.fetchData();
          }

        } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
      
    },

    //업데이트 params
    getUpdateParam(){
      const _vm = this;
      // 변경된 값만 저장할 객체
      const updatedParams = {};
      // 제외할 키 정의
      const excludeKeys = ["emailConfirm", "nicknameConfirm"];
      let params = {
        ..._vm.userData.myInfo, 
      };

      if('002' === _vm.eumLoginUser.userType){
        params = {
          ...params,
          ...Object.keys(DEF_COMP_INF).reduce((acc, key) => {
            acc[key] = _vm.userData.compInfo[key] ?? '';  // null/undefined 방지
            return acc;
          }, {}),
        };
      }

      Object.keys(params).forEach((key) => {
        if (!excludeKeys.includes(key) && params[key] !== _vm.membBackup[key]) {
          updatedParams[key] = params[key];
        }
      });

      return Object.keys(updatedParams).length === 0 ? {} : params;

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
#coching-mypage-myInfo {

}
</style>
