<template>
  <transition name="fade">
    <!-- <keep-alive :include="cachedViews">
      <router-view :key="key" />
    </keep-alive> -->
    <router-view class="base-contents"/>
  </transition>
</template>

<script>
import device from '@/components/mixins/device';

export default {
  name: 'BasicContent',
  mixins: [device],
  computed: {
    /*
    cachedViews() {
      return this.$store.state.tagsView.cachedViews
    },
    key() {
      return this.$route.path
    }*/
  }
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">
</style>
