<template>
  <!--section-->
  <section id="coching-mypage-recentView">
    <div class="container h-100">
      <div class="content">
        <div class="common-content">
          <!--content-title-wrap-->
          <MyRawMenuBar></MyRawMenuBar>

          <RecentInfo
            v-bind="$props"
            refCode="raw"
          >                
          </RecentInfo>
        </div>
      </div>
    </div>
  </section>
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';
import Advertise from '@/components/Advertise.vue';
import MyRawMenuBar from './MyRawMenuBar.vue';
import RecentInfo from '@/components/recent/RecentInfo.vue';


export default {
	name: 'coching-mypage-recentView',
	mixins: [ernsUtils],
	components: {
    Advertise,
    MyRawMenuBar,
    RecentInfo
  },
	computed: {    
  },
	watch: {
	},
	props: {
    
  },
	data() {
		return {			
		}
	},
	async mounted() {
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
#coching-mypage-recentView {

}
</style>
