<template>
	<!--section-->
	<section id="coching-empty">

		<!--내용-->
		<div class="container md"></div>
			

		<!--오류-->
		<AlertModal ref="alertModal"></AlertModal>
		<ConfirmModal ref="confirmModal"></ConfirmModal>

	</section>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';

export default {
	name: 'coching-empty',
	mixins: [ernsUtils],
	components: {
		
	},
	computed: {},
	watch: {},
	props: {},
	data() {
		return {
			
		}
	},
	mounted() {
		const _vm = this;

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
#coching-empty {

}
</style>
