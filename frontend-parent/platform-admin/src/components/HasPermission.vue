<script setup lang="ts">
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'

const props = defineProps<{
  permission: string | string[]
}>()

const userStore = useUserStore()

const hasPermission = computed(() => {
  const permissions = Array.isArray(props.permission) ? props.permission : [props.permission]
  return permissions.some(perm => userStore.hasPermission(perm))
})
</script>

<template>
  <slot v-if="hasPermission" />
</template>