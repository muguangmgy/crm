<template>
  <el-dialog v-model="visible" title="分类管理" width="500px" :close-on-click-modal="false">
    <div class="category-toolbar">
      <el-button type="primary" size="small" v-permission="'crm:product:category:add'" @click="handleAddCategory">
        <el-icon><Plus/></el-icon>
        新增分类
      </el-button>
    </div>

    <el-tree
        ref="treeRef"
        :data="localTree"
        :props="{ label: 'categoryName', children: 'children' }"
        node-key="id"
        default-expand-all
        :expand-on-click-node="false"
    >
      <template #default="{ data }">
        <div class="tree-node">
          <span>{{ data.categoryName }}</span>
          <span class="tree-actions">
            <el-button type="primary" link size="small" v-permission="'crm:product:category:edit'"
                       @click="handleEditCategory(data)">编辑</el-button>
            <el-button type="danger" link size="small" v-permission="'crm:product:category:delete'"
                       @click="handleDeleteCategory(data)">删除</el-button>
          </span>
        </div>
      </template>
    </el-tree>

    <!-- 分类表单弹窗 -->
    <el-dialog v-model="formVisible" :title="formTitle" width="400px" append-to-body :close-on-click-modal="false">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="上级分类">
          <el-tree-select
              v-model="formData.parentId"
              :data="parentTree"
              :props="{ label: 'categoryName', value: 'id', children: 'children' }"
              placeholder="请选择上级分类"
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
        <el-button type="primary" :loading="saving" @click="handleSaveCategory">保存</el-button>
      </template>
    </el-dialog>

    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { productApi } from '@/api/crm/product'
import type { CategoryVO, CategorySaveDTO } from '@/types/crm/product'

const emit = defineEmits(['success'])

// 弹窗状态
const visible = ref(false)
const localTree = ref<CategoryVO[]>([])

// 分类表单
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

// 上级分类树（不含当前分类）
const parentTree = computed(() => {
  if (!categoryId.value) return localTree.value
  // 过滤掉当前分类及其子分类
  return filterCategoryTree(localTree.value, categoryId.value)
})

// 验证规则
const rules = {
  categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const formRef = ref()

// 打开弹窗
const open = (categories: CategoryVO[]) => {
  localTree.value = categories
  visible.value = true
}

// 新增分类
const handleAddCategory = () => {
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

// 编辑分类
const handleEditCategory = (data: CategoryVO) => {
  categoryId.value = data.id
  Object.assign(formData, {
    categoryName: data.categoryName,
    parentId: data.parentId || 0,
    sort: data.sort,
    status: data.status
  })
  formVisible.value = true
}

// 删除分类
const handleDeleteCategory = async (data: CategoryVO) => {
  try {
    await ElMessageBox.confirm(`确定删除分类【${data.categoryName}】？`, '提示', { type: 'warning' })
    await productApi.deleteCategory(data.id)
    ElMessage.success('删除成功')
    emit('success')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 保存分类
const handleSaveCategory = async () => {
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
    emit('success')
  } catch (error) {
    console.error('保存失败', error)
  } finally {
    saving.value = false
  }
}

// 过滤分类树（移除指定分类及其子分类）
const filterCategoryTree = (tree: CategoryVO[], excludeId: number): CategoryVO[] => {
  return tree
      .filter(item => item.id !== excludeId)
      .map(item => ({
        ...item,
        children: item.children ? filterCategoryTree(item.children, excludeId) : undefined
      }))
}

// 暴露方法
defineExpose({ open })
</script>

<style scoped lang="scss">
.category-toolbar {
  margin-bottom: 15px;
}

.tree-node {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding-right: 10px;

  .tree-actions {
    margin-left: auto;
  }
}
</style>