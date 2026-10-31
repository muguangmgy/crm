<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search/></el-icon>搜索
          </el-button>
          <el-button type="success" v-permission="'crm:customer:tag:add'" @click="handleAdd">
            <el-icon><Plus/></el-icon>新增
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column type="index" label="序号" width="60"/>
        <el-table-column prop="tagName" label="标签名称" min-width="150"/>
        <el-table-column prop="tagColor" label="颜色" width="100">
          <template #default="{ row }">
            <el-tag :color="row.tagColor" effect="dark" v-if="row.tagColor">{{ row.tagName }}</el-tag>
            <span v-else>{{ row.tagName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="100"/>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"/>
        <el-table-column label="操作" width="150" fixed="right" v-if="hasOperPermission">
          <template #default="{ row }">
            <el-button type="primary" link v-permission="'crm:customer:tag:edit'" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link v-permission="'crm:customer:tag:delete'" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="标签名称" prop="tagName">
          <el-input v-model="formData.tagName" placeholder="请输入标签名称"/>
        </el-form-item>
        <el-form-item label="标签颜色">
          <el-color-picker v-model="formData.tagColor"/>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sort" :min="0" :max="999"/>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { tagApi } from '@/api/crm/tag'
import type { TagVO, TagSaveDTO } from '@/types/crm/customer'
import type { FormInstance } from 'element-plus'

const userStore = useUserStore()

const queryParams = reactive({
  pageNum: 1,
  pageSize: 100
})

const loading = ref(false)
const tableData = ref<TagVO[]>([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const formData = reactive<TagSaveDTO>({
  tagName: '',
  tagColor: '#409EFF',
  sort: 0,
  status: 1
})

const hasOperPermission = computed(() =>
  userStore.hasPermission('crm:customer:tag:edit') || userStore.hasPermission('crm:customer:tag:delete')
)

const formRules = {
  tagName: [{ required: true, message: '请输入标签名称', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    tableData.value = await tagApi.getList()
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  loadData()
}

const handleAdd = () => {
  dialogTitle.value = '新增标签'
  formData.id = undefined
  formData.tagName = ''
  formData.tagColor = '#409EFF'
  formData.sort = 0
  formData.status = 1
  dialogVisible.value = true
}

const handleEdit = (row: TagVO) => {
  dialogTitle.value = '编辑标签'
  formData.id = row.id
  formData.tagName = row.tagName
  formData.tagColor = row.tagColor || '#409EFF'
  formData.sort = row.sort
  formData.status = row.status
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    await tagApi.save(formData)
    ElMessage.success(formData.id ? '修改成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {
    console.error(e)
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row: TagVO) => {
  await ElMessageBox.confirm('确定删除此标签?', '提示')
  await tagApi.delete(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 20px;
}
</style>