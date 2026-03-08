<template>
  <layout-full>
    <router-view :key="$route.name"/>
  </layout-full>
</template>

<script>
import LayoutFull from '@core/layouts/layout-full/LayoutFull.vue'

export default {
  components: {
    LayoutFull,
  },
}
</script>
