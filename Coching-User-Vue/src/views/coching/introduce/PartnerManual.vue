<template>
  <div>
  <header>
    <div class="inner">
        <div class="container">
          <a @click="onClickHome" href="javascript:;" class="logo">
            <img src="@/assets/images/logo.svg" alt="COCHING" />
          </a>
          <div class="unit">
            <div class="flex right">
              <div @click="onClickDownFile()" class="ic-lg ic-download-sm"></div>
            </div>
          </div>
        </div>
      </div>
  </header>
   <!--section-->
   <section id="coching-partner-manual">
    <div class="inner">
      <div class="container">
        <div>
          <div class="tabs style-box">
          <div class="tabs-nav">
              <ul>
                <li>
                  <a @click="onClickTabMenu('introduce')" href="javascript:;">코칭 소개</a>
                </li>
                <li>
                  <a @click="onClickTabMenu('user')" href="javascript:;">사용자 메뉴얼</a>
                </li>
                <li class="active">
                  <a href="javascript:;">파트너 메뉴얼</a>
                </li>
              </ul>
            </div>
            <!--컨텐츠-->
            <div class="tabs-content board-tabs-content">
              <div class="tabs-panel active">
                <!--total-wrap-->
                <div class="right">
                    <!--button-->
                </div>
                <div class="responsive-table check-table">
                  <!--real-content-->
                  <!-- <PdfImageViewer class="contents-pdf"
                    :key="`partner_${version}`"
                    :pdfUrl="`/pdf/coching_partner_manual.pdf?v=${version}`"
                  /> -->
                  <ContinuousPdfPageImageViewer class="contents-pdf"
                    :fileId="fileInfo.fileId"
                  />
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
</div>
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';
import PdfImageViewer from '@/components/PdfImageViewer.vue';
import ContinuousPdfPageImageViewer from '@/components/ContinuousPdfPageImageViewer.vue';

import { loadIntroInfo } from '@/api/coching/comm/common';

export default {
	name: 'coching-partner-manual',
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
      // const pdfUrl = `/pdf/coching_partner_manual.pdf?v=${_vm.version}`;
      // const link = document.createElement('a');
      // link.href = pdfUrl;
      // link.download = "Coching 파트너 메뉴얼";
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
          introType : "3" // 1:코칭 소개, 2:사용자 메뉴얼, 3:파트너 메뉴얼
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
      }else if(menu == 'user'){
        _vm.$router.push({name:'coching-user-manual'});
      }
    },

		docReady() {
			const _vm = this;

		},
	}
}
</script>

<style lang="scss" scoped>
#coching-partner-manual {
  padding: 5.438rem 0 5rem;
}
@media screen and (max-width: 1023px) {
    section {
        padding: 3.438rem 0 5rem!important;
    }
}
</style>

<style lang="scss">
#coching-partner-manual {
  .contents-pdf {
    img {
      width: 100%;
    }
  }
}
</style>