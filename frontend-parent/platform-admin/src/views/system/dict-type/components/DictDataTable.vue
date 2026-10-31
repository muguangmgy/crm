<template>
  <div class="dict-data-table">
    <div v-if="!dictCode" class="empty-state">
      <div class="empty-content">
        <el-icon class="empty-icon"><FolderOpened /></el-icon>
        <p class="empty-title">请选择字典类型</p>
        <p class="empty-desc">从左侧树中选择一个字典类型，查看和编辑其字典项</p>
      </div>
    </div>

    <div v-else class="table-container">
      <div class="table-header">
        <div class="header-info">
          <el-icon class="dict-icon"><Collection /></el-icon>
          <span class="dict-title">{{ dictName }}</span>
          <el-tag size="small" type="primary">{{ dictCode }}</el-tag>
          <el-tag size="small" :type="tenantId === 0 ? 'info' : 'success'">{{ tenantName }}</el-tag>
        </div>
        <el-button type="primary" v-permission="'system:dict:add'" @click="handleAddRow">
          <el-icon><Plus /></el-icon>
          添加行
        </el-button>
      </div>

      <el-scrollbar class="table-scrollbar">
        <el-table :data="tableData" border stripe>
          <el-table-column prop="itemLabel" label="字典标签" min-width="150">
            <template #default="{ row }">
              <el-input v-model="row.itemLabel" placeholder="请输入字典标签" />
            </template>
          </el-table-column>
          <el-table-column prop="itemValue" label="字典值" min-width="150">
            <template #default="{ row }">
              <el-input v-model="row.itemValue" placeholder="请输入字典值" />
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-switch v-model="row.status" :active-value="1" :inactive-value="0" />
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="200">
            <template #default="{ row }">
              <el-input v-model="row.remark" placeholder="请输入备注" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center" fixed="right" v-if="hasPermission">
            <template #default="{ row, $index }">
              <el-button type="danger" link @click="handleDeleteRow(row, $index)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-scrollbar>

      <div class="table-footer">
        <el-button type="primary" v-permission="'system:dict:edit'" :loading="saveLoading" @click="handleSave">
          <el-icon><Check /></el-icon>
          保存
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { dictDataApi } from '@/api/dict-data'
import { useUserStore } from '@/stores/user'
import type { DictDataItemDTO } from '@/types/dict-data'

const props = defineProps<{
  dictCode: string
  tenantId: number
  tenantName: string
  dictName: string
}>()

const userStore = useUserStore()
const hasPermission = computed(() => userStore.hasPermission('system:dict:add') || userStore.hasPermission('system:dict:edit'))

const tableData = ref<DictDataItemDTO[]>([])
const deleteIds = ref<number[]>([])
const saveLoading = ref(false)

async function loadData() {
  if (!props.dictCode) {
    tableData.value = []
    deleteIds.value = []
    return
  }
  try {
    const res = await dictDataApi.getByCode(props.dictCode, props.tenantId)
    tableData.value = res.map(item => ({
      id: item.id,
      itemLabel: item.itemLabel,
      itemValue: item.itemValue,
      status: item.status,
      remark: item.remark || ''
    }))
    deleteIds.value = []
  } catch {}
}

function handleAddRow() {
  tableData.value.push({ id: undefined, itemLabel: '', itemValue: '', status: 1, remark: '' })
}

function handleDeleteRow(row: DictDataItemDTO, index: number) {
  if (row.id) deleteIds.value.push(row.id)
  tableData.value.splice(index, 1)
}

async function handleSave() {
  for (const item of tableData.value) {
    if (!item.itemLabel) { ElMessage.error('字典标签不能为空'); return }
    if (!item.itemValue) { ElMessage.error('字典值不能为空'); return }
  }
  saveLoading.value = true
  try {
    await dictDataApi.batchSave({
      dictCode: props.dictCode,
      tenantId: props.tenantId,
      items: tableData.value,
      deleteIds: deleteIds.value
    })
    ElMessage.success('保存成功')
    loadData()
  } catch {} finally { saveLoading.value = false }
}

watch(() => [props.dictCode, props.tenantId], () => loadData())
defineExpose({ refresh: loadData })
</script>

<style lang="scss" scoped>
.dict-data-table {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 8px;

  .empty-state {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;

    .empty-content {
      text-align: center;
      padding: 40px;

      .empty-icon {
        font-size: 64px;
        color: #c0c4cc;
        margin-bottom: 16px;
      }

      .empty-title {
        font-size: 18px;
        color: #606266;
        margin-bottom: 8px;
      }

      .empty-desc {
        font-size: 14px;
        color: #909399;
      }
    }
  }

  .table-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-height: 0;

    .table-header {
      padding: 16px;
      border-bottom: 1px solid #ebeef5;
      display: flex;
      justify-content: space-between;
      align-items: center;

      .header-info {
        display: flex;
        align-items: center;
        gap: 8px;

        .dict-icon {
          color: #409eff;
          font-size: 18px;
        }

        .dict-title {
          font-size: 16px;
          font-weight: 600;
          color: #303133;
        }
      }
    }

    .table-scrollbar {
      flex: 1;
      overflow: hidden;
      padding: 0 16px;

      :deep(.el-table) {
        --el-table-border-color: #ebeef5;
        --el-table-header-bg-color: #f5f7fa;

        th.el-table__cell {
          font-weight: 600;
          color: #303133;
        }

        .el-table__row:hover > td {
          background: #ecf5ff !important;
        }
      }
    }

    .table-footer {
      padding: 16px;
      border-top: 1px solid #ebeef5;
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style>