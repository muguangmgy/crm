<template>
  <el-dialog v-model="visible" :title="formTitle" width="600px" :close-on-click-modal="false">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
      <el-form-item label="产品名称" prop="productName">
        <el-input v-model="formData.productName" placeholder="请输入产品名称" maxlength="100"/>
      </el-form-item>
      <el-form-item label="产品编码" prop="productCode">
        <el-input v-model="formData.productCode" placeholder="请输入产品编码" maxlength="50"/>
      </el-form-item>
      <el-form-item label="分类" prop="categoryId">
        <el-tree-select
            v-model="formData.categoryId"
            :data="categoryTree"
            :props="{ label: 'categoryName', value: 'id', children: 'children' }"
            placeholder="请选择分类"
            clearable
            check-strictly
        />
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="单位" prop="unit">
            <el-select v-model="formData.unit" placeholder="请选择单位" clearable>
              <el-option v-for="item in productUnitDict" :key="item.itemValue" :label="item.itemLabel"
                         :value="item.itemValue"/>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-select v-model="formData.status" placeholder="请选择状态">
              <el-option v-for="item in productStatusDict" :key="item.itemValue" :label="item.itemLabel"
                         :value="Number(item.itemValue)"/>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="标准价格" prop="price">
            <el-input-number
              v-model="formData.price"
              :precision="2"
              :min="0"
              :controls="false"
              placeholder="标准价格"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="最低价格">
            <el-input-number
              v-model="formData.minPrice"
              :precision="2"
              :min="0"
              :controls="false"
              placeholder="最低价格"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="成本价格">
            <el-input-number
              v-model="formData.costPrice"
              :precision="2"
              :min="0"
              :controls="false"
              placeholder="成本价格"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="排序">
            <el-input-number
              v-model="formData.sort"
              :min="0"
              :controls="false"
              placeholder="排序"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="规格">
            <el-input v-model="formData.spec" placeholder="请输入规格" maxlength="100"/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="型号">
            <el-input v-model="formData.model" placeholder="请输入型号" maxlength="50"/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="产品描述">
        <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入产品描述" maxlength="500"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { productApi } from '@/api/crm/product'
import { useDicts } from '@/composables/useDict'
import type { ProductSaveDTO, CategoryVO } from '@/types/crm/product'

const emit = defineEmits(['success'])

// 字典
const { dataMap } = useDicts(['crm_product_unit', 'crm_product_status'])
const productUnitDict = computed(() => dataMap.value['crm_product_unit'] || [])
const productStatusDict = computed(() => dataMap.value['crm_product_status'] || [])

// 弹窗状态
const visible = ref(false)
const saving = ref(false)
const productId = ref<number | undefined>()
const categoryTree = ref<CategoryVO[]>([])

// 表单标题
const formTitle = computed(() => productId.value ? '编辑产品' : '新增产品')

// 表单数据
const formData = reactive<ProductSaveDTO>({
  productName: '',
  price: 0,
  status: 1,
  sort: 0
})

// 表单验证规则
const rules = {
  productName: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入标准价格', trigger: 'blur' }]
}

const formRef = ref()

// 打开弹窗
const open = async (id: number | undefined, categories: CategoryVO[]) => {
  categoryTree.value = categories
  productId.value = id
  visible.value = true

  // 重置表单
  formRef.value?.resetFields()
  Object.assign(formData, {
    productName: '',
    productCode: '',
    categoryId: undefined,
    unit: '',
    spec: '',
    model: '',
    price: 0,
    minPrice: undefined,
    costPrice: undefined,
    description: '',
    imageUrl: '',
    status: 1,
    sort: 0
  })

  // 加载详情
  if (id) {
    try {
      const detail = await productApi.getById(id)
      Object.assign(formData, detail)
    } catch (error) {
      ElMessage.error('加载产品详情失败')
    }
  }
}

// 保存
const handleSave = async () => {
  try {
    await formRef.value?.validate()
    saving.value = true

    if (productId.value) {
      formData.id = productId.value
      await productApi.update(formData)
      ElMessage.success('修改成功')
    } else {
      await productApi.create(formData)
      ElMessage.success('新增成功')
    }

    visible.value = false
    emit('success')
  } catch (error) {
    console.error('保存失败', error)
  } finally {
    saving.value = false
  }
}

// 暴露方法
defineExpose({ open })
</script>