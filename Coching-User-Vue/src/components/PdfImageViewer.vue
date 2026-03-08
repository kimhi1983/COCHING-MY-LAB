<template>
  <div>
    <div v-for="(img, index) in imageList" :key="index">
      <img :src="img" alt="PDF page image" style="width: 100%; margin-bottom: 1rem;" />
    </div>
  </div>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import * as pdfjsLib from 'pdfjs-dist/build/pdf';
import pdfjsWorker from 'pdfjs-dist/build/pdf.worker.entry';

pdfjsLib.GlobalWorkerOptions.workerSrc = pdfjsWorker;
import ERROR_IMG from '@/assets/images/ic-img-lg.svg';

export default {
  mixins: [ernsUtils],
  props: {
    pdfUrl: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      imageList: []
    };
  },
  computed: {
    errorImg(){
      return ERROR_IMG;
    }    
  },
  async mounted() {
    const _vm = this;

    _vm.docReady();
    _vm.loadPdf(); // PDF 로드
  },
  methods: {
    async loadPdf(){
      const _vm = this;
      const pdf = await pdfjsLib.getDocument(_vm.pdfUrl).promise;
      const images = [];
      _vm.loading(true); // 로딩 시작
      for (let i = 1; i <= pdf.numPages; i++) {
        const page = await pdf.getPage(i);
        const scale = 1.5;
        const viewport = page.getViewport({ scale });

        const canvas = document.createElement("canvas");
        const context = canvas.getContext("2d");
        canvas.width = viewport.width;
        canvas.height = viewport.height;

        await page.render({
          canvasContext: context,
          viewport: viewport
        }).promise;

        const imgData = canvas.toDataURL("image/png");
        images.push(imgData);
      }

      this.imageList = images;
      _vm.loading(false); // 로딩 종료
    },
    onImageError(event) {
      const targetObj = event.target;
      targetObj.src = this.errorImg; // 대체 이미지로 설정
      targetObj.style.width = '100px'; // 원하는 너비 설정
      targetObj.style.height = '100px'; // 원하는 높이 설정
    },
    docReady() {
			const _vm = this;
    },
  }
};
</script>

<style scoped>

</style>
