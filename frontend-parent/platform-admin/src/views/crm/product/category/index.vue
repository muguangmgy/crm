<template>
  <div class="page-container">
    <!-- 工具栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" class="search-form">
        <el-form-item label="分类名称">
          <el-input v-model="filterText" placeholder="请输入分类名称" clearable/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" v-permission="'crm:product:category:add'" @click="handleAdd">
            <el-icon><Plus/></el-icon>
            新增分类
          </el-button>
          <el-button @click="loadTree">
            <el-icon><Refresh/></el-icon>
            刷新
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 分类树 -->
    <el-card class="table-card" shadow="never">
      <el-tree
          ref="treeRef"
          v-loading="loading"
          :data="categoryTree"
          :props="{ label: 'categoryName', children: 'children' }"
          :filter-node-method="filterNode"
          node-key="id"
          default-expand-all
          :expand-on-click-node="false"
      >
        <template #default="{ data }">
          <div class="tree-node">
            <span class="node-label">{{ data.categoryName }}</span>
            <el-tag size="small" :type="data.status === 1 ? 'success' : 'danger'" class="node-tag">
              {{ data.status === 1 ? '启用' : '禁用' }}
            </el-tag>
            <span class="node-sort">排序: {{ data.sort }}</span>
            <span class="tree-actions">
              <el-button type="primary" link size="small" v-permission="'crm:product:category:add'"
                         @click="handleAddChild(data)">添加子级</el-button>
              <el-button type="primary" link size="small" v-permission="'crm:product:category:edit'"
                         @click="handleEdit(data)">编辑</el-button>
              <el-button type="danger" link size="small" v-permission="'crm:product:category:delete'"
                         @click="handleDelete(data)">删除</el-button>
            </span>
          </div>
        </template>
      </el-tree>

      <el-empty v-if="!loading && categoryTree.length === 0" description="暂无分类数据"/>
    </el-card>

    <!-- 分类表单弹窗 -->
    <el-dialog v-model="formVisible" :title="formTitle" width="500px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="上级分类">
          <el-tree-select
              v-model="formData.parentId"
              :data="parentTree"
              :props="{ label: 'categoryName', value: 'id', children: 'children' }"
              placeholder="请选择上级分类（空为顶级）"
              clearable
              check-strictly
          />
        </el-form-item>
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="formData.categoryName" placeholder="请输入分类名称" maxlength="50"/>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sort" :min="0" placeholder="排序"/>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="formData.status" placeholder="请选择状态">
            <el-option label="启用" :value="1"/>
            <el-option label="禁用" :value="0"/>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'
import { productApi } from '@/api/crm/product'
import type { CategoryVO, CategorySaveDTO } from '@/types/crm/product'

// 数据
const loading = ref(false)
const categoryTree = ref<CategoryVO[]>([])
const filterText = ref('')

// 表单
const formVisible = ref(false)
const saving = ref(false)
const categoryId = ref<number | undefined>()
const formData = reactive<CategorySaveDTO>({
  categoryName: '',
  parentId: 0,
  sort: 0,
  status: 1
})

const formTitle = computed(() => categoryId.value ? '编辑分类' : '新增分类')

const parentTree = computed(() => {
  if (!categoryId.value) return categoryTree.value
  return filterCategoryTree(categoryTree.value, categoryId.value)
})

const rules = {
  categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const formRef = ref()
const treeRef = ref()

// 搜索过滤
watch(filterText, (val) => {
  treeRef.value?.filter(val)
})

const filterNode = (value: string, data: CategoryVO) => {
  if (!value) return true
  return data.categoryName.includes(value)
}

// 加载分类树
const loadTree = async () => {
  loading.value = true
  try {
    categoryTree.value = await productApi.getCategoryTree()
  } catch (error) {
    console.error('加载分类树失败', error)
  } finally {
    loading.value = false
  }
}

// 新增顶级分类
const handleAdd = () => {
  categoryId.value = undefined
  Object.assign(formData, {
    categoryName: '',
    parentId: 0,
    sort: 0,
    status: 1
  })
  formVisible.value = true
  formRef.value?.resetFields()
}

// 新增子级分类
const handleAddChild = (data: CategoryVO) => {
  categoryId.value = undefined
  Object.assign(formData, {
    categoryName: '',
    parentId: data.id,
    sort: 0,
    status: 1
  })
  formVisible.value = true
  formRef.value?.resetFields()
}

// 编辑
const handleEdit = (data: CategoryVO) => {
  categoryId.value = data.id
  Object.assign(formData, {
    categoryName: data.categoryName,
    parentId: data.parentId || 0,
    sort: data.sort,
    status: data.status
  })
  formVisible.value = true
}

// 删除
const handleDelete = async (data: CategoryVO) => {
  try {
    await ElMessageBox.confirm(`确定删除分类【${data.categoryName}】？`, '提示', { type: 'warning' })
    await productApi.deleteCategory(data.id)
    ElMessage.success('删除成功')
    loadTree()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 保存
const handleSave = async () => {
  try {
    await formRef.value?.validate()
    saving.value = true

    if (categoryId.value) {
      formData.id = categoryId.value
      await productApi.updateCategory(formData)
      ElMessage.success('修改成功')
    } else {
      await productApi.createCategory(formData)
      ElMessage.success('新增成功')
    }

    formVisible.value = false
    loadTree()
  } catch (error) {
    console.error('保存失败', error)
  } finally {
    saving.value = false
  }
}

// 过滤分类树（移除指定分类及其子分类，避免选自己为父级）
const filterCategoryTree = (tree: CategoryVO[], excludeId: number): CategoryVO[] => {
  return tree
      .filter(item => item.id !== excludeId)
      .map(item => ({
        ...item,
        children: item.children ? filterCategoryTree(item.children, excludeId) : undefined
      }))
}

// 初始化
loadTree()
</script>

<style scoped lang="scss">
.page-container {
  padding: 20px;

  .search-card {
    margin-bottom: 20px;
  }

  .table-card {
    min-height: 400px;
  }

  .tree-node {
    display: flex;
    align-items: center;
    width: 100%;
    padding: 4px 0;

    .node-label {
      font-size: 14px;
    }

    .node-tag {
      margin-left: 10px;
    }

    .node-sort {
      margin-left: 10px;
      font-size: 12px;
      color: #909399;
    }

    .tree-actions {
      margin-left: auto;
      white-space: nowrap;
    }
  }
}
</style>
