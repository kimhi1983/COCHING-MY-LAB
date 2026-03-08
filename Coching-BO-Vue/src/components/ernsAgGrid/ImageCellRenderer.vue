<template>
  <div class="image-container">
    <img :src="params.value" alt="Image" 
         :width="imageWidth" 
         :height="imageHeight" 
         class="cell-image" />
  </div>
</template>

<script>
export default {
  computed: {
    imageWidth() {
      return this.params.width || 100; // 기본값 100px
    },
    imageHeight() {
      return this.params.height || 100; // 기본값 100px
    }
  }
};
</script>

<style scoped>
.image-container {
  display: flex;
  align-items: center;
  justify-content: center;  
}
.cell-image {
	padding: 5px 5px;
  max-width: 200px;
  max-height: 200px;
  border-radius: 5px;
}
</style>
