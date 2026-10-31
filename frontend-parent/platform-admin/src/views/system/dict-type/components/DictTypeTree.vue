<template>
  <div class="dict-type-tree">
    <div class="tree-header">
      <div class="header-title">
        <el-icon class="title-icon"><FolderOpened /></el-icon>
        <span>字典类型</span>
      </div>
      <el-button type="primary" size="small" v-permission="'system:dict:add'" @click="handleAddRoot">
        <el-icon><Plus /></el-icon>
        新增
      </el-button>
    </div>

    <div class="tree-search">
      <el-input v-model="filterText" placeholder="搜索字典名称" clearable>
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <el-scrollbar class="tree-scrollbar">
      <el-tree
        ref="treeRef"
        :data="treeData"
        :props="treeProps"
        node-key="id"
        highlight-current
        default-expand-all
        :filter-node-method="filterNode"
        @node-click="handleNodeClick"
        @node-contextmenu="handleContextMenu"
      >
        <template #default="{ data, node }">
          <div class="tree-node" :class="{ 'is-selected': node.isCurrent }">
            <el-icon class="node-icon" :class="{ 'is-folder': data.children?.length }">
              <Folder v-if="data.children?.length" />
              <Document v-else />
            </el-icon>
            <span class="node-label">{{ data.dictName }}</span>
            <span class="node-code">{{ data.dictCode }}</span>
            <el-tag v-if="data.tenantId === 0" type="info" size="small" effect="plain">全局</el-tag>
          </div>
        </template>
      </el-tree>
    </el-scrollbar>

    <!-- 右键菜单 -->
    <div v-show="contextMenuVisible" class="context-menu" :style="{ left: contextMenuX + 'px', top: contextMenuY + 'px' }" @click.stop>
      <div class="menu-item" v-permission="'system:dict:add'" @click="handleAddChild">
        <el-icon><Plus /></el-icon>
        新增子字典
      </div>
      <div class="menu-item" v-permission="'system:dict:edit'" @click="handleEdit">
        <el-icon><Edit /></el-icon>
        编辑
      </div>
      <div class="menu-item" v-permission="'system:dict:delete'" @click="handleDelete">
        <el-icon><Delete /></el-icon>
        删除
      </div>
      <div class="menu-item" @click="handleRefresh">
        <el-icon><Refresh /></el-icon>
        刷新
      </div>
    </div>

    <DictTypeForm ref="formRef" @success="handleFormSuccess" />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { dictTypeApi } from '@/api/dict-type'
import DictTypeForm from './DictTypeForm.vue'
import type { DictTypeTreeVO } from '@/types/dict-type'

const emit = defineEmits<{
  (e: 'node-select', node: DictTypeTreeVO): void
}>()

const treeRef = ref()
const formRef = ref()

const treeData = ref<DictTypeTreeVO[]>([])
const treeProps = { children: 'children', label: 'dictName' }
const filterText = ref('')

const contextMenuVisible = ref(false)
const contextMenuX = ref(0)
const contextMenuY = ref(0)
const currentNode = ref<DictTypeTreeVO | null>(null)
const contextNode = ref<DictTypeTreeVO | null>(null)

async function loadTree() {
  try {
    treeData.value = await dictTypeApi.getTree()
  } catch {}
}

function filterNode(value: string, data: DictTypeTreeVO) {
  if (!value) return true
  return data.dictName.includes(value) || data.dictCode.includes(value)
}

watch(filterText, (val) => {
  treeRef.value?.filter(val)
})

function handleNodeClick(data: DictTypeTreeVO) {
  emit('node-select', data)
}

function handleContextMenu(event: MouseEvent, data: DictTypeTreeVO) {
  event.preventDefault()
  currentNode.value = data
  contextNode.value = data
  contextMenuX.value = event.clientX
  contextMenuY.value = event.clientY
  contextMenuVisible.value = true
}

function hideContextMenu() {
  contextMenuVisible.value = false
}

function handleAddRoot() {
  // 根节点的 parentId=0，parentName='', parentTenantId=0（全局）
  formRef.value?.open(undefined, 0, '', 0)
}

function handleAddChild() {
  const node = contextNode.value
  if (!node) return
  hideContextMenu()
  formRef.value?.open(undefined, node.id, node.dictName, node.tenantId)
}

function handleEdit() {
  const node = contextNode.value
  if (!node) return
  hideContextMenu()
  // 编辑模式：传入 node，以及 parentId 用于显示父节点信息
  // parentName 在编辑模式下不重要（disabled 显示），传空字符串
  formRef.value?.open(node, node.parentId, '', node.tenantId)
}

async function handleDelete() {
  const node = contextNode.value
  if (!node) return
  const nodeId = node.id
  const nodeName = node.dictName
  hideContextMenu()
  try {
    await ElMessageBox.confirm(`确定要删除字典类型 "${nodeName}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await dictTypeApi.delete(nodeId)
    ElMessage.success('删除成功')
    loadTree()
  } catch {}
}

function handleRefresh() {
  loadTree()
  hideContextMenu()
}

function handleFormSuccess() {
  loadTree()
}

function handleClickOutside() {
  hideContextMenu()
}

onMounted(() => {
  loadTree()
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

defineExpose({ refresh: loadTree })
</script>

<style lang="scss" scoped>
.dict-type-tree {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 8px;

  .tree-header {
    padding: 16px;
    border-bottom: 1px solid #ebeef5;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 16px;
      font-weight: 600;
      color: #303133;

      .title-icon {
        color: #409eff;
        font-size: 18px;
      }
    }
  }

  .tree-search {
    padding: 12px 16px;
    border-bottom: 1px solid #ebeef5;
  }

  .tree-scrollbar {
    flex: 1;
    overflow: hidden;
  }

  .el-tree {
    padding: 12px 8px;

    :deep(.el-tree-node__content) {
      height: 36px;
      border-radius: 4px;
      margin: 2px 0;
      transition: all 0.2s;

      &:hover {
        background: #ecf5ff;
      }
    }

    :deep(.el-tree-node.is-current > .el-tree-node__content) {
      background: #ecf5ff;
      border-left: 3px solid #409eff;
    }
  }

  .tree-node {
    display: flex;
    align-items: center;
    gap: 8px;
    width: 100%;
    padding: 0 4px;

    .node-icon {
      color: #c0c4cc;
      font-size: 16px;
      transition: color 0.2s;

      &.is-folder {
        color: #409eff;
      }
    }

    .node-label {
      flex: 1;
      font-size: 14px;
      color: #303133;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .node-code {
      color: #909399;
      font-size: 12px;
      max-width: 80px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    &.is-selected .node-label {
      color: #409eff;
      font-weight: 500;
    }
  }

  .context-menu {
    position: fixed;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
    z-index: 1000;
    padding: 8px 0;
    min-width: 140px;

    .menu-item {
      padding: 10px 16px;
      cursor: pointer;
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 14px;
      color: #606266;
      transition: all 0.2s;

      &:hover {
        background: #ecf5ff;
        color: #409eff;
      }

      .el-icon {
        font-size: 16px;
      }
    }
  }
}
</style>