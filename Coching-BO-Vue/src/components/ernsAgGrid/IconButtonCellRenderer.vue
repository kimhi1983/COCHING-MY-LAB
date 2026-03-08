<template>
  <b-button
    :id="buttonId"
    @click="onClick"
    :size="size"
    :variant="variant"
    :disabled="isDisabled()"
    class="btn-icon d-inline-flex align-items-center justify-content-center p-0"
    :class="btnClass"
    v-b-tooltip.hover
    :title="tooltip"
  >
    <feather-icon :icon="icon" :size="iconSize" />
  </b-button>
</template>

<script>

const DEF_ICON = 'SettingsIcon'
const DEF_ICON_SIZE = '16'
const DEF_SIZE = 'sm'
const DEF_VARIANT = 'outline-primary'
const DEF_CHECKING_EDITROW = true

export default {
  name: 'IconButtonCellRenderer',
  props: {
    // params는 ag-grid가 주입합니다
  },
  data() {
    return {
      icon: DEF_ICON,
      iconSize: DEF_ICON_SIZE,
      size: DEF_SIZE,
      variant: DEF_VARIANT,
      checkEditRow: DEF_CHECKING_EDITROW,
      btnClass: '',
      buttonId: `icon-btn-${Math.random().toString(36).slice(2, 9)}`,
      tooltip: '',
    }
  },
  beforeMount() {
    const p = this.params || {}
    this.icon = p.icon || DEF_ICON
    this.iconSize = p.iconSize || DEF_ICON_SIZE
    this.size = p.size || DEF_SIZE
    this.variant = p.variant || DEF_VARIANT
    this.btnClass = p.btnClass || ''
    this.tooltip = p.tooltip || ''

    if (p.checkEditRow === undefined || p.checkEditRow === null) {
      this.checkEditRow = DEF_CHECKING_EDITROW
    } else {
      this.checkEditRow = p.checkEditRow
    }

    if(p.tooltip == undefined || p.tooltip == null){
      this.tooltip = ''
    }else{
      this.tooltip = p.tooltip
    }
  },
  methods: {
    isDisabled() {
      const p = this.params || {}
      if (p.isDisabled) return p.isDisabled(p)
      return false
    },
    onClick() {
      const p = this.params || {}
      if (p.action) p.action(p)
    },
  },
}
</script>

<style lang="scss" scoped>
// 버튼 크기를 아이콘에 맞추려면 아래 스타일을 사용하세요.
.rotate-left { transform: rotate(-90deg); }
.rotate-right { transform: rotate(90deg); }

.btn{
  &.btn-icon {
    cursor: pointer;
  }
  &:disabled {
    cursor: not-allowed;
  }

  &.btn-sm {
    line-height: 1;
  } 
  &.btn-md {
    line-height: 2;
  }
  &.btn-lg {
    line-height: 3;
  }

  &.btn-no-border {
    border: none !important;
  }

  &.btn-green { background-color: #28a745; color: white; }
  &.btn-red { background-color: #dc3545; color: white; }
  &.btn-blue { background-color: #007bff; color: white; }
  &.btn-yellow { background-color: #ffc107; color: white; }
  &.btn-purple { background-color: #6f42c1; color: white; }
  &.btn-orange { background-color: #fd7e14; color: white; }
  &.btn-gray { background-color: #6c757d; color: white; }
  &.btn-black { background-color: #343a40; color: white; }

  &.btn-green-outline { border-color: #28a745; color: #28a745; }
  &.btn-red-outline { border-color: #dc3545; color: #dc3545; }
  & .btn-blue-outline { border-color: #007bff; color: #007bff; }
  &.btn-yellow-outline { border-color: #ffc107; color: #ffc107; }
  &.btn-purple-outline { border-color: #6f42c1; color: #6f42c1; }
  &.btn-orange-outline { border-color: #fd7e14; color: #fd7e14; }
  &.btn-gray-outline { border-color: #6c757d; color: #6c757d; }
  &.btn-black-outline { border-color: #343a40; color: #343a40; }
}





</style>


