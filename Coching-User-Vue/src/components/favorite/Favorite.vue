<template>
	<!-- <div>
		favoriteMode:{{ favoriteMode }}<br/>
		localData.favoriteMode:{{ localData.favoriteMode }}<br/>		
		messageSeq:{{ messageSeq }}<br/>		 -->
		<FavoriteList
			id="coching-favorite"
			v-bind="$props"
		></FavoriteList>
	<!-- </div> -->
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';
import favoriteMixin from './favoriteMixin';
import { FAVORITE_MODE, REF_CODES } from '@/constants/favorite';

import FavoriteList from './FavoriteList.vue';

const DEF_MODE = FAVORITE_MODE.LIST;

export default {
	name: 'coching-favorite-main',
	mixins: [ernsUtils, favoriteMixin],
	components: {
    FavoriteList
  },
	computed: {    
  },
	watch: {
		'$route.params'(newVal, oldVal) {
			this.settingApp();
		}
	},
	props: {
    refCode: {
      type: String,
      default: REF_CODES.RAW
    },
		favoriteMode:{
			type: String,
			default: DEF_MODE
		},    
  },
	data() {
		return {
			localData : {
				favoriteMode : DEF_MODE
			}
		}
	},
	async mounted() {
		const _vm = this;

		_vm.settingApp();
    _vm.docReady();
    _vm.fetchData();
	},
	beforeDestroy() {
		const _vm = this;
	},
	methods: {
		settingApp(){
			const _vm = this;
			
			switch(_vm.favoriteMode){					
				case FAVORITE_MODE.LIST:
					_vm.localData.favoriteMode = _vm.favoriteMode;
					break;

				default:
					_vm.localData.favoriteMode = FAVORITE_MODE.LIST;
					break;
			}
		},

		/* 데이터 가져오는 부분(사용하지 필요없어도 반드시 선언) */
		async fetchData() {      
			const _vm = this;

			_vm.loading(true);
			try {

			} catch (err) {
				_vm.defaultApiErrorHandler(err); //기본에러처리
			} finally {
				_vm.loading(false);
			}
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
#coching-favorite {

}
</style>
