<template>
  <div class="dict-management">
    <div class="page-header">
      <span class="page-title">字典管理</span>
      <span class="page-desc">管理系统字典类型和字典项数据</span>
    </div>
    <div class="page-content">
      <div class="tree-panel">
        <DictTypeTree ref="treeRef" @node-select="handleNodeSelect" />
      </div>
      <div class="table-panel">
        <DictDataTable
          ref="tableRef"
          :dict-code="selectedDictCode"
          :tenant-id="selectedTenantId"
          :tenant-name="selectedTenantName"
          :dict-name="selectedDictName"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import DictTypeTree from './components/DictTypeTree.vue'
import DictDataTable from './components/DictDataTable.vue'
import type { DictTypeTreeVO } from '@/types/dict-type'

const selectedDictCode = ref('')
const selectedTenantId = ref(0)
const selectedTenantName = ref('')
const selectedDictName = ref('')

function handleNodeSelect(node: DictTypeTreeVO) {
  selectedDictCode.value = node.dictCode
  selectedTenantId.value = node.tenantId
  selectedTenantName.value = node.tenantName
  selectedDictName.value = node.dictName
}
</script>

<style lang="scss" scoped>
.dict-management {
  height: calc(100vh - 100px);
  display: flex;
  flex-direction: column;
  padding: 16px;
  background: #f5f7fa;

  .page-header {
    margin-bottom: 16px;
    display: flex;
    align-items: baseline;
    gap: 12px;

    .page-title {
      font-size: 20px;
      font-weight: 600;
      color: #303133;
    }

    .page-desc {
      font-size: 14px;
      color: #909399;
    }
  }

  .page-content {
    flex: 1;
    display: flex;
    gap: 16px;
    min-height: 0;

    .tree-panel {
      width: 300px;
      flex-shrink: 0;
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    }

    .table-panel {
      flex: 1;
      min-width: 0;
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    }
  }
}
</style>