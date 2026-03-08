<template>
	<div class="form-section agree">
		<!--Title-->
		<div class="title-wrap">
			<div class="title">{{ $t('my.label004') || '비밀번호 변경' }}</div>
		</div>

		<!--Contents-->
		<validation-observer ref="pswdInfoForm" #default="{ invalid }">
			<div v-if="updateInvalidState(invalid)"></div>
			<div class="inner">
				<div class="input-wrap">
					<div class="input-flex">
						<div class="label-set">
							<label>{{ $t('my.content02.label007') || '현재 비밀번호' }}<span class="text-pink">*</span></label>
							<div class="input-set">
								<validation-provider #default="{ errors }" class="form-input" :name="$t('myinfo.password.name001') || '비밀번호'"
									rules="required|no-whitespace|passwordcochingJoin">
									<div class="input-set">
										<input v-model="localData.pswdInfo.crrentPswd" type="password" :placeholder="$t('myinfo.password.label001') || '비밀번호를 입력해 주세요'">
										<div class="info error" :class="{ 'active': errors.length > 0 }">{{ errors[0] }}</div>
									</div>
								</validation-provider>
							</div>
						</div>
					</div>
					<div class="input-flex">
						<div class="label-set">
							<label>{{ $t('my.content02.label008') || '새로운 비밀번호' }} <span class="text-pink">*</span></label>
							<validation-provider #default="{ errors }" class="form-input" :name="$t('my.content02.label008') || '새로운 비밀번호'"
								rules="required|no-whitespace|passwordcochingJoin">
								<div class="input-set">
									<input v-model="localData.pswdInfo.pswd" type="password" :placeholder="$t('myinfo.password.label002') || '새로운 비밀번호를 입력해 주세요'">
									<div class="info error" :class="{ 'active': errors.length > 0 }">{{ errors[0] }}
									</div>
								</div>
							</validation-provider>
						</div>
						<div class="label-set">
							<label>{{ $t('my.content02.label004') || '비밀번호 확인' }}<span class="text-pink">*</span></label>
							<validation-provider #default="{ errors }" class="form-input" :name="$t('my.content02.label004') || '비밀번호 확인'"
								rules="required|checkPass">
								<div class="input-set">
									<input v-model="localData.pswdInfo.pswdConfirm" type="password" :placeholder="$t('myinfo.password.label003') || '다시 입력해 주세요'">
									<div class="info error" :class="{ 'active': errors.length > 0 }">{{ errors[0] }}</div>
								</div>
							</validation-provider>
						</div>
					</div>
				</div>
			</div>
    	</validation-observer>
		<!-- useAllCheckbox:{{ useAllCheckbox }}<br/> -->
		<!-- termsData:{{ termsData }}<br/> -->
		<!-- localData:{{ localData }}<br/> -->

		<!--오류-->
		<AlertModal ref="alertModal"></AlertModal>
		<ConfirmModal ref="confirmModal"></ConfirmModal>

	</div>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import { getTermList } from '@/api/coching/comm/terms';
import { extend } from 'vee-validate';
import { cochingValidation } from '@/@core/utils/validations/validations';

const DEFAULT_PSWD_INFO = {
	crrentPswd: "",
  pswd: "",
  pswdConfirm: ""
}

export default {
	name: 'change-pswd-components',
	mixins: [ernsUtils],
	components: {

	},
	computed: {},
	watch: {
    //입력양식 데이터가 변경될 경우를 캐치
    'localData.pswdInfo': {
      handler(newVal, oldVal) {
        this.onChangeValue();
      },
      deep: true
    },
	'$i18n.locale' : function(){
        const _vm = this;
        _vm.$nextTick(() => _vm.$refs.pswdInfoForm.validate());
    },
  },
	props: {
		//전체선택 체크박스
		useCheckbox: {
			type: Boolean,
			default: false
		}
	},
	data() {
		return {
      localInvalidState: false,
      status: '',
			localData: {
				check: false,
        pswdInfo:{...DEFAULT_PSWD_INFO}
			}
		}
	},
	mounted() {
		const _vm = this;

		_vm.init();

		function checkPass() {
			extend('checkPass', {
			validate: value => {
				return _vm.localData.pswdInfo.pswd == value || _vm.$t('myinfo.password.label004') || '비밀번호가 일치하지 않습니다.';
			},
			});
		}
		checkPass();

		cochingValidation();
	},
	beforeDestroy() {
		const _vm = this;
	},
	methods: {
		//초기화 함수
		async init() {
			const _vm = this;

		},

		//입력값이 유효한지 판단
		async isValid() {
			const _vm = this;

			let retVal = true;
			for (const item of _vm.localData.terms) {
				if ("Y" == item.requiredYn && !item.value) {
					retVal = false;
					break;
				}
			}

			return retVal;
		},

		//선택된 값 리턴
		async getData() {
			const _vm = this;

			const retVal = _vm.localData.map(item => {
				return {
					crrentPswd: localData.crrentPswd
					, pswd: localData.pswd
					, pswdConfirm: localData.pswdConfirm
				}
			});

			return retVal;
		},

		async onChangeValue() {
			const _vm = this;

			const data = {..._vm.localData.pswdInfo}

			if(_vm.isPswdInfoComplete()){
				_vm.$emit("update:data", data);
			}else{
				_vm.$emit("update:data", DEFAULT_PSWD_INFO);
			}
		},

		//선택
		async onClickCheckPassword() {
     		 const _vm = this;
			const data = {..._vm.localData.pswdInfo}
			data.check = _vm.localData.check;

			_vm.$emit("update:data", data);
		},


		async updateInvalidState(invalid) {
			const _vm = this;
			const isChanged = this.localInvalidState != invalid;
			this.localInvalidState = invalid;
			if(_vm.isPswdInfoComplete()){
				_vm.$emit("update:valid", !this.localInvalidState);
			}else{
				_vm.$emit("update:valid", true);
			}
      		return false; // v-if에서 항상 false를 반환하여 실제 DOM에 렌더링되지 않게 함
		},
		isPswdInfoComplete() {
			const _vm = this;
			return Object.values(_vm.localData.pswdInfo).every(value => value !== '');
		}

	}
}
</script>

<style lang="scss" scoped></style>

<style lang="scss">
#terms-agree-components1 {}
</style>
