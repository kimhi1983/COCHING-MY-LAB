<template>
	<div class="custom-select-wrapper">
		<select
			ref="chosenSelect"
			class="chosen-select"
			:class="{'chosen-single': isSingleLine}"
			:data-placeholder="placeholder"
			:disabled="disabled"
			v-model="value"
			:multiple="isMultiple"
		>
			<option v-for="(option, index) in localOptions" 
				:key="index" :value="option[trackBy]">
				{{ option[label] }}
			</option>
		</select>
		<!-- trackBy:{{ trackBy }}<br/> -->
		<!-- localData.value:{{ localData.value }}<br/> -->
		<!-- localData.selItem:{{ localData.selItem }} -->
		<!-- options:{{ options }}<br/> -->
	</div>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import "chosen-js";

const DEF_LOCAL_VAL = {
	opened : false,
	value : null,
	selItem : null,

	options : []
};

export default {
	name: 'coching-select-search-components',
	mixins: [ernsUtils],
	components: {
		
	},
	computed: {
		localOptions() {
				let vm = this,
						options = []

				if (Array.isArray(this.options)) {
						return options.concat(this.options)
				}

				Object.keys(this.options).forEach(function (key) {
						options.push({
								[vm.trackBy]: key,
								[vm.label]: vm.options[key]
						})
				})
				return this.allowEmpty
						? [{ [this.trackBy]: null, [this.label]: '' }].concat(options)
						: options
		},		
	},
	watch: {
		value(newVal, oldVal){
			const _vm = this;
			if(!_vm.isEmptyValue(newVal)){
				if (JSON.stringify(newVal) !== JSON.stringify(oldVal)) {
					_vm.onChangeValue(newVal);
				}
			}
		},
		options(newVal, oldVal) {
			const _vm = this;
			console.debug('watch:options');
			// options 변경 시 Chosen.js 업데이트
			_vm.updateChosenOptions();
			_vm.onSetValue(_vm.value);
		},    
		'localData.value' : function(newVal, oldVal) {
			const _vm = this;
			if (JSON.stringify(newVal) !== JSON.stringify(oldVal)) {
				_vm.onClickOption(newVal);
			}
		},
	},
	props: {
		value: {
				type: [String, Number, Array, Object],
				default: null
		},
		options: {
				type: [Array, Object],
				default: () => []
		},
		label: {
				type: String,
				default: 'label'
		},
		trackBy: {
				type: String,
				default: 'value'
		},
		placeholder: {
				type: String,
				default: 'Select'
		},
		allowEmpty: {
				type: Boolean,
				default: true
		},
		disabled: {
				type: Boolean,
				default: false
		},
		isMultiple:{
			type: Boolean,
			default: false
		},
		isSearch: {
			type: Boolean,
			default: false
		},
		minSearchCnt: {
			type: Number,
			default: 5
		},
		noSearchText: {
			type: String,
			default: '결과가 없습니다'
		},
		onValueReturn: {
				type: Object,
				default: () => ({})
		},
		isSingleLine:{
			type: Boolean,
			default: false
		}
	},
	data() {
		return {
			rawTerms : [],

			localData : {
				...DEF_LOCAL_VAL,
				value: this.isMultiple ? [] : null, // multiple일 경우 빈 배열로 초기화
			},
			isDrop: false,
		}
	},
	mounted() {
		const _vm = this;

		_vm.init();			
	},
	beforeDestroy() {
		const _vm = this;
		$(this.$refs.chosenSelect).chosen("destroy");
	},
	methods: {
		//초기화 함수
		async init(){
    		const _vm = this;

			$(_vm.$refs.chosenSelect).chosen({
			search_contains: _vm.isSearch, // 검색 기능 활성화
			disable_search_threshold: _vm.minSearchCnt, // 최소 옵션 개수 5개 이상일 때 검색 가능
			no_results_text: _vm.noSearchText, // 검색 결과 없음 메시지
			}).on("change", () => {
				const chosenVal = $(this.$refs.chosenSelect).val();
				// Vue 상태 업데이트
				_vm.localData.value = _vm.isMultiple
					? Array.isArray(chosenVal)
						? chosenVal.filter(item => item !== '')
						: []
					: chosenVal || null;
			});
				
			if(_vm.isEmptyValue(_vm.value)){
				_vm.localData.value = this.isMultiple ? [] : '';
       			$(this.$refs.chosenSelect).val(this.isMultiple ? [] : '').trigger("chosen:updated");
			}

			_vm.localData = {...DEF_LOCAL_VAL, value: this.isMultiple ? [] : null}

			_vm.onSetValue(_vm.value);
    },

		onSetValue(valCode){
			const _vm = this;

			if(!_vm.isMultiple){
				const sItem = _vm.localOptions.find(item=>{
					return item[_vm.trackBy] == valCode;
				});
				if(sItem){
					_vm.localData.value = sItem[_vm.trackBy];
					_vm.localData.selItem = sItem;
				}
			}else{
				const sItems = _vm.localOptions.filter(item => valCode.includes(item[_vm.trackBy]));
				if(sItems.length > 0){
					_vm.localData.value = sItems.map((item) => item[_vm.trackBy]);
					_vm.localData.selItem = sItems;
				}
			}

			this.$nextTick(() => {
				$(_vm.$refs.chosenSelect).val(_vm.localData.value).trigger("chosen:updated");
			});

			//console.debug(sItem);
		},

		onChangeValue(valCode){
			const _vm = this;

			if(!_vm.isMultiple){
				_vm.localData.value = valCode;
			}else{
				_vm.localData.value = [...valCode];
			}
		},

		//옵션 선택시
		onClickOption(item){
			const _vm = this;

			_vm.onSetValue(item);

			_vm.$emit('input', _vm.localData.value);
		},

		//option 변경시
		updateChosenOptions() {
			const _vm = this;
			
			this.$nextTick(() => {
					// Chosen.js 업데이트
					$(_vm.$refs.chosenSelect).trigger("chosen:updated");
			});
    	},
		isEmptyValue(value) {
			if (value === null || value === undefined || value === '') {
				return true; // null, undefined, 빈 문자열
			}
			if (typeof value === 'string' && value.trim() === '') {
				return true; // 공백 문자열
			}
			if (typeof value === 'number' && isNaN(value)) {
				return true; // NaN 확인
			}
			if (Array.isArray(value) && value.length === 0) {
				return true; // 빈 배열
			}
			if (typeof value === 'object' && Object.keys(value).length === 0) {
				return true; // 빈 객체
			}
			return false; // 빈 값이 아님
		}
	}
}
</script>

<style lang="scss">
///* Chosen.js 선택된 텍스트 스타일 */
.chosen-container .chosen-single {
   display: block;
   overflow: hidden;
   text-overflow: ellipsis;
   white-space: nowrap;
}

/* Chosen.js 옵션 리스트 스타일 */
.chosen-container .chosen-results li {
   display: block;
   overflow: hidden;
   text-overflow: ellipsis;
   white-space: nowrap;
   max-width: 100%; /* 컨테이너 너비에 맞게 설정 */
}
</style>

<style lang="scss" scoped>

</style>
