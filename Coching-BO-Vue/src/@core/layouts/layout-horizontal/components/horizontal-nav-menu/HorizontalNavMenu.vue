<template>
  <div class="navbar-container main-menu-content">
    <horizontal-nav-menu-items :items="navMenuItems" />
  </div>
</template>

<script>
import { getNavMenuItems } from '@/navigation/horizontal'
import HorizontalNavMenuItems from './components/horizontal-nav-menu-items/HorizontalNavMenuItems.vue'
import { getUserData } from '@/auth/utils';

export default {
  components: {
    HorizontalNavMenuItems,
  },
  setup() {
    const userData = getUserData();

    return {
      navMenuItems : getNavMenuItems(userData),
    }
  },
}
</script>

<style lang="scss">
@import "~@core/scss/base/core/menu/menu-types/horizontal-menu.scss";
</style>
