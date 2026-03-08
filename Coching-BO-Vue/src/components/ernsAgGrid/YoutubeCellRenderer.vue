<template>
  <div class="youtube-container">
    <iframe 
      v-if="embedUrl"
      :src="embedUrl"
      :width="imageWidth" 
      :height="imageHeight" 
      class="cell-ract"
      title="YouTube video player" 
      frameborder="0" 
      allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" 
      referrerpolicy="strict-origin-when-cross-origin" allowfullscreen>
    </iframe>    
    <div>{{ params.value }}</div>
  </div>
</template>

<script>
export default {
  computed: {
    imageWidth() {
      return this.params.width || 300; // 기본값 300px
    },
    imageHeight() {
      return this.params.height || 300;
    },
    embedUrl() {
      const url = this.params.value;
      const match = url.match(/(?:youtu\.be\/|v=)([\w-]+)/);
      if (match && match[1]) {
        return `https://www.youtube.com/embed/${match[1]}`;
      }
      return null;
    }
  }
};
</script>

<style scoped>
.youtube-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.cell-ract {
	padding: 5px;
  border-radius: 5px;
  max-width: 100%;
  height: auto;
}
</style>