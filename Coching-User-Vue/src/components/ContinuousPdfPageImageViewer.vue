<template>
  <div>
     <div v-if="loading" class="pdf-loader-wrap">
      <div class="loader"></div>
      <div class="loader-inner">자료를 불러오는 중입니다.</div>
    </div>   
    <img v-for="(item, idx) in pdfPages" :key="idx"
      :src="eumFileEImagePath(item.path, errorImg)"
      @error="onImageError"
      :alt="''+item.pageNo+'.png'"/>
    <div v-if="loadError" style="color: #ccc;">PDF 파일을 불러오는 데 실패했습니다.</div>
    <!-- loadError:{{ loadError }} -->
    <!-- pdfPages:{{ pdfPages }} -->
  </div>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import { getPdfImageInfo } from '@/api/coching/comm/common';

import ERROR_IMG from '@/assets/images/ic-img-lg.svg';

export default {
  name: 'coching-continuous-pdf-page-image-viewer',
  mixins: [ernsUtils],
  computed: {
    errorImg(){
      return ERROR_IMG;
    }    
  },
  props: {
    fileId: {
      type: String,
      required: true,
    },    
  },
  watch: {
    fileId(){
      this.fetchTotalPages();
    }
  },
  data() {
    return {
      totalPages : 0,
      pdfPages: [],
      loadError: false,
    };
  },
  methods: {
    async fetchTotalPages() {
      const _vm = this;
      _vm.totalPages = 0;

      if(!_vm.fileId){
        _vm.loadError = true;
        _vm.$emit("onFetchedPages", _vm.totalPages);
        return;
      }

      _vm.loading = true;

      try {
        const params = {
          fileId : _vm.fileId
        };        
        const pdfImagesRes = await getPdfImageInfo(params);
        //console.debug(pdfImagesRes);
        _vm.pdfPages = pdfImagesRes.resultData;
        _vm.totalPages = _vm.pdfPages.length;
        _vm.loadError = false;
        
      } catch (error) {
        console.error(error);
        _vm.totalPages = 0;
        _vm.loadError = true;
      } finally {
        _vm.loading = false;
      }

      _vm.$emit("onFetchedPages", _vm.totalPages);
    },

    onImageError(event) {
      const targetObj = event.target;
      targetObj.src = this.errorImg; // 대체 이미지로 설정
      targetObj.style.width = '100px'; // 원하는 너비 설정
      targetObj.style.height = '100px'; // 원하는 높이 설정
    },
  },

  async mounted() {
    await this.fetchTotalPages();
  },
};
</script>

<style lang="scss" scoped>
.pdf-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.pdf-loader-wrap{
  min-height: 18.75rem;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  gap: 2.625rem;
  font-weight: 400;
  color: #999;
  background-color: rgba(255, 255, 255, 0.4);
  
  .loader-inner {
    color: #aaa;
  }
}

</style>
