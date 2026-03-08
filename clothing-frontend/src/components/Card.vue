<template>
  <el-card :class="['card', customClass]" :shadow="shadow">
    <template #header>
      <div v-if="showHeader" class="card-header">
        <el-icon v-if="icon" class="header-icon">
          <component :is="icon" />
        </el-icon>
        <span>{{ title }}</span>
        <el-badge v-if="badge" :value="badge" class="item-count" type="primary" />
      </div>
    </template>
    
    <slot></slot>
  </el-card>
</template>

<script setup>
const props = defineProps({
  title: {
    type: String,
    default: ''
  },
  icon: {
    type: String,
    default: ''
  },
  badge: {
    type: [String, Number],
    default: ''
  },
  shadow: {
    type: String,
    default: 'hover'
  },
  showHeader: {
    type: Boolean,
    default: true
  },
  customClass: {
    type: String,
    default: ''
  }
})
</script>

<style lang="scss" scoped>
// 样式已经在全局样式中定义
</style>