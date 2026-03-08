<template>
	<div class="custom-select-wrapper">
		<!-- localData.value:{{ localData.value }} -->
		<select class="custom-select"
			ref="rawSelect" 
			:placeholder="placeholder" 
			:disabled="disabled"
			style="display: none;">

			<option v-for="(option, index) in localOptions" v-bind:value="option[trackBy]"
				:key="index">{{ option[label] }}</option>
			
		</select>
		<div class="custom-select" :class="{'opened' : localData.opened, 'disabled': disabled}">
			<span class="custom-select-trigger"
				@click="onClickCustomSelectTrigger"
			>{{localData.selItem ? localData.selItem[label] : placeholder}}</span>
			<div class="custom-options">
				<span v-for="(option, index) in localOptions" 
					@click="onClickOption(option)"
					class="custom-option" :class="{'selection':localData.value && localData.value == option[trackBy]}"
					:key="index">{{ option[label]  }}</span>
			</div>
		</div>
		<!-- trackBy:{{ trackBy }}<br/> -->
		<!-- localData.value:{{ localData.value }}<br/> -->
		<!-- localData.selItem:{{ localData.selItem }} -->
		<!-- options:{{ options }}<br/> -->
	</div>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';

const DEF_LOCAL_VAL = {
	opened : false,
	value : null,
	selItem : null,

	options : []
};

export default {
	name: 'coching-select-components1',
	mixins: [ernsUtils],
	components: {
		
	},
	computed: {
		localOptions() {
				let vm = this,
						options = []

				if (this.allowAll) {
						options.push({
								[this.trackBy]: -1,
								[this.label]: 'All'
						})
				}

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
		value(val){
      const _vm = this;
      _vm.onSetValue(val);
    },
    localOptions() {
				
		},
		options(val){
			//console.debug('watch:options');
			if(this.value != null){
				this.onSetValue(this.value);
			}
		}
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
		allowAll: {
				type: Boolean,
				default: false
		},
		disabled: {
				type: Boolean,
				default: false
		},
		onValueReturn: {
				type: Object,
				default: () => ({})
		}
	},
	data() {
		return {
			rawTerms : [],

			localData : {...DEF_LOCAL_VAL}
		}
	},
	mounted() {
		const _vm = this;

		_vm.init();				
	},
	beforeDestroy() {
		const _vm = this;
	},
	methods: {
		//초기화 함수
		async init(){
      const _vm = this;

			_vm.localData = {...DEF_LOCAL_VAL};

			_vm.onSetValue(_vm.value);
    },

		onSetValue(valCode){
			const _vm = this;

			const sItem = _vm.localOptions.find(item=>{
				return item[_vm.trackBy] == valCode;
			});

			//console.debug(sItem);

			if(sItem){
				_vm.localData.value = sItem[_vm.trackBy];
				_vm.localData.selItem = sItem;
			}
		},

		//트리거 클릭시
		onClickCustomSelectTrigger(){
			const _vm = this;
			if(_vm.disabled){
				return;
			}

			//SelectBox 이외 클릭시
			$('html').one('click', function () {
				_vm.localData.opened = false;
      });

			_vm.localData.opened = true;
			event.stopPropagation();			
		},

		//옵션 선택시
		onClickOption(item){
			const _vm = this;

			_vm.setLocalValue(item);

			_vm.$emit('input', _vm.localData.value);
		},

		setLocalValue(item){
			const _vm = this;

			$(_vm.$refs.rawSelect).val(item[_vm.trackBy]);
			_vm.localData.value = item[_vm.trackBy];
			_vm.localData.selItem = item;
		},
	}
}
</script>

<style lang="scss" scoped>

</style>
