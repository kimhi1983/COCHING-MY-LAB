<template>
    <div ref="lottie"></div>
  </template>
  
  <script>
  import lottie from "lottie-web";
  
  export default {
    props: {
      animationData: {
        type: Object,
        required: true
      },
      autoplay: {
        type: Boolean,
        default: true
      },
      loop: {
        type: Boolean,
        default: true
      },
      speed: {
        type: Number,
        default: 1
      }
    },
    mounted() {
      this.lottieInstance = lottie.loadAnimation({
        container: this.$refs.lottie,
        renderer: "svg",
        loop: this.loop,
        autoplay: this.autoplay,
        animationData: this.animationData
      });
  
      this.lottieInstance.setSpeed(this.speed);
    },
    beforeDestroy() {
      if (this.lottieInstance) {
        this.lottieInstance.destroy();
      }
    }
  };
  </script>