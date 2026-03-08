<template>
  <section>
    <div class="container h-100">
      <!--content-->
      <div class="content">
        <!--common-content-->
        <div class="common-content">
          <!--title-wrap-->
          <div class="title-wrap flex">
            <div class="h1 text-center">코칭 사용자 매뉴얼</div>
            <a href="javascript:;" 
              @click="onClickDownFile()"
              class="btn btn-sm btn-pdf">다운로드</a>
          </div>
          <!--manual-wrap-->
          <div class="manual-wrap">
            <ContinuousPdfPageImageViewer class="contents-pdf"
              :fileId="fileInfo.fileId"
            />
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
import PdfImageViewer from '@/components/PdfImageViewer.vue';
import ContinuousPdfPageImageViewer from '@/components/ContinuousPdfPageImageViewer.vue';

import { loadIntroInfo } from '@/api/coching/comm/common';

export default {
	name: 'coching-user-manual',
	mixins: [ernsUtils],
	components: {
    PdfImageViewer,
    ContinuousPdfPageImageViewer,
  },
	computed: {
    errorImg(){
      return ERROR_IMG;
    },
  },
	watch: {
	},
	props: {
  },
	data() {
		return {
      version: new Date().getTime(),
      fileInfo : {
        fileId : "",
      },
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
    onClickDownFile(){
      const _vm = this;
      // const pdfUrl = `/pdf/coching_manual.pdf?v=${_vm.version}`;
      // const link = document.createElement('a');
      // link.href = pdfUrl;
      // link.download = "Coching 메뉴얼";
      // link.click();

      // document.body.removeChild(link);

      //PDF 다운로드로 변경
      const downloadPath = _vm.eumFileDownPath(_vm.fileInfo.fileId, false, true);
      document.location.href = downloadPath;
    },
    onImageError(event) {
      const targetObj = event.target;
      targetObj.src = this.errorImg; // 대체 이미지로 설정
      targetObj.style.width = '100px'; // 원하는 너비 설정
      targetObj.style.height = '100px'; // 원하는 높이 설정
    },

		//데이터 가져오는 부분(사용하지 필요없어도 반드시 선언)
		async fetchData() {
			const _vm = this;

			_vm.loading(true);
			try {
        const params = {
          introType : "2" // 1:코칭 소개, 2:사용자 메뉴얼, 3:파트너 메뉴얼
        };
        const res = await loadIntroInfo(params);
        const {list} = res.resultData;
        _vm.fileInfo = list[0];

			} catch (err) {
				_vm.defaultApiErrorHandler(err); //기본에러처리
			} finally {
				_vm.loading(false);
			}
		},

    async loadCodes(){
      const _vm = this;
    },

    onClickTabMenu(menu){
      const _vm = this;

      if(menu == 'introduce'){
        _vm.$router.push({name:'coching-introduce'});
      }else if(menu == 'partner'){
        _vm.$router.push({name:'coching-partner-manual'});
      }
    },

		docReady() {
			const _vm = this;

		},

	}
}
</script>

<style lang="scss" scoped>
#coching-user-manual {
  padding: 5.438rem 0 5rem;
}
@media screen and (max-width: 1023px) {
    section {
        padding: 3.438rem 0 5rem!important;
    }
}
</style>

<style lang="scss">
#coching-user-manual {
  .contents-pdf {
    img {
      width: 100%;
    }
  }
}
</style>