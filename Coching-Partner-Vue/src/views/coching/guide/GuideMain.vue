<template>
  <!--section-->
  <section>
    <div class="container">
      <div class="content-wrap">
        <!--title-->
        <div class="h1">원료 등록 가이드</div>
        <!--content-->
        <div class="content-inner">
          <!--guide-->
          <div class="guide">
            <div class="img-box-wrap guide-box-wrap">
              <div class="title-wrap">
                <div class="step">01</div>
                <div class="title">원료등록 페이지 클릭</div>
                <div class="info">
                  Lorem ipsum dolor sit amet consectetur. Massa risus blandit augue natoque non. Porta accumsan faucibus sit lorem justo nibh. Sit tellus
                  malesuada felis integer. Ultrices viverra id justo lobortis semper elementum tincidunt. Sapien est neque aenean et vitae. Tempor ut sem
                  consequat pharetra porta nam donec sit.
                </div>
              </div>
              <div class="img-box md">
                <div class="item">
                  <div class="img">
                    <img src="@/assets/images/img-test-01.jpg" alt="" />
                  </div>
                </div>
              </div>
            </div>
            <div class="img-box-wrap guide-box-wrap">
              <div class="title-wrap">
                <div class="step">02</div>
                <div class="title">등록하기 버튼 클릭</div>
                <div class="info">
                  Lorem ipsum dolor sit amet consectetur. Massa risus blandit augue natoque non. Porta accumsan faucibus sit lorem justo nibh. Sit tellus
                  malesuada felis integer. Ultrices viverra id justo lobortis semper elementum tincidunt. Sapien est neque aenean et vitae. Tempor ut sem
                  consequat pharetra porta nam donec sit.
                </div>
              </div>
              <div class="img-box md">
                <div class="item">
                  <div class="img">
                    <img src="@/assets/images/img-test-02.jpg" alt="" />
                  </div>
                </div>
              </div>
            </div>
            <div class="img-box-wrap">
              <div class="title-wrap p-b-1">
                <div class="step">03</div>
                <div class="title">원료 정보 입력</div>
              </div>
              <div class="img-box responsive">
                <div class="item">
                  <div class="img">
                    <img src="@/assets/images/img-test-01.jpg" alt="" />
                  </div>
                  <div class="title-wrap">
                    <div class="title">Lorem ipsum dolor sit amet</div>
                    <div class="info">
                      Lorem ipsum dolor sit amet consectetur. Scelerisque pellentesque mauris leo in sed arcu justo sed et. Tellus id platea varius vitae
                      condimentum velit fringilla sociis.
                    </div>
                  </div>
                </div>
                <div class="item">
                  <div class="img">
                    <img src="@/assets/images/img-test-02.jpg" alt="" />
                  </div>
                  <div class="title-wrap">
                    <div class="title">Lorem ipsum dolor sit amet</div>
                    <div class="info">
                      Lorem ipsum dolor sit amet consectetur. Scelerisque pellentesque mauris leo in sed arcu justo sed et. Tellus id platea varius vitae
                      condimentum velit fringilla sociis.
                    </div>
                  </div>
                </div>
                <div class="item">
                  <div class="img">
                    <img src="@/assets/images/img-test-03.jpg" alt="" />
                  </div>
                  <div class="title-wrap">
                    <div class="title">Lorem ipsum dolor sit amet</div>
                    <div class="info">
                      Lorem ipsum dolor sit amet consectetur. Scelerisque pellentesque mauris leo in sed arcu justo sed et. Tellus id platea varius vitae
                      condimentum velit fringilla sociis.
                    </div>
                  </div>
                </div>
                <div class="item">
                  <div class="img">
                    <img src="@/assets/images/img-test-04.jpg" alt="" />
                  </div>
                  <div class="title-wrap">
                    <div class="title">Lorem ipsum dolor sit amet</div>
                    <div class="info">
                      Lorem ipsum dolor sit amet consectetur. Scelerisque pellentesque mauris leo in sed arcu justo sed et. Tellus id platea varius vitae
                      condimentum velit fringilla sociis.
                    </div>
                  </div>
                </div>
              </div>
            </div>
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

export default {
	name: 'coching-guide-main',
	mixins: [ernsUtils],
	components: {
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
    // _vm.navInfo.meta.mainNavId = 5;

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
#coching-mypage-main {

}
</style>
