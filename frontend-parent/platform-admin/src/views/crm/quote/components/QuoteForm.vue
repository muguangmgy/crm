<template>
  <el-dialog
    v-model="visible"
    :title="isEdit ? '编辑报价单' : '新增报价单'"
    width="800px"
    :close-on-click-modal="false"
    destroy-on-close
  >
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="关联商机" prop="opportunityId">
            <el-select
              v-model="formData.opportunityId"
              placeholder="请选择商机"
              filterable
              @change="handleOpportunityChange"
            >
              <el-option
                v-for="item in opportunityList"
                :key="item.id"
                :label="item.oppName"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="客户名称">
            <el-input v-model="formData.customerName" disabled placeholder="自动带出" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="报价单名称" prop="quoteName">
            <el-input v-model="formData.quoteName" placeholder="请输入报价单名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="报价有效期" prop="expireDate">
            <el-date-picker
              v-model="formData.expireDate"
              type="date"
              placeholder="请选择有效期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="整体折扣率">
            <el-input-number
              v-model="formData.discountRate"
              :min="0"
              :max="100"
              :precision="2"
              placeholder="请输入折扣率"
              @change="calculateTotal"
            />
            <span class="ml-2 text-gray-500">%</span>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="负责人" prop="ownerId">
            <el-input v-model="formData.ownerName" disabled placeholder="自动带出" />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 产品明细 -->
      <el-form-item label="产品明细" prop="products">
        <div class="product-table-wrapper">
          <el-table :data="formData.products" border style="width: 100%">
            <el-table-column label="产品" min-width="180">
              <template #default="{ row, $index }">
                <el-select
                  v-model="row.productId"
                  placeholder="请选择产品"
                  filterable
                  @change="handleProductChange(row, $index)"
                >
                  <el-option
                    v-for="item in productList"
                    :key="item.id"
                    :label="item.productName"
                    :value="item.id"
                  />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="单位" width="80">
              <template #default="{ row }">
                {{ row.unit || '-' }}
              </template>
            </el-table-column>
            <el-table-column label="数量" width="120">
              <template #default="{ row, $index }">
                <el-input-number
                  v-model="row.quantity"
                  :min="0.01"
                  :precision="2"
                  size="small"
                  @change="calculateRowTotal(row, $index)"
                />
              </template>
            </el-table-column>
            <el-table-column label="单价" width="130">
              <template #default="{ row, $index }">
                <el-input-number
                  v-model="row.unitPrice"
                  :min="0"
                  :precision="2"
                  size="small"
                  @change="handleUnitPriceChange(row, $index)"
                />
                <div v-if="row.priceWarning" class="price-warning">
                  <el-icon color="#E6A23C"><Warning /></el-icon>
                  <span class="warning-text">低于最低价</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="行折扣(%)" width="100">
              <template #default="{ row, $index }">
                <el-input-number
                  v-model="row.discountRate"
                  :min="0"
                  :max="100"
                  :precision="2"
                  size="small"
                  @change="calculateRowTotal(row, $index)"
                />
              </template>
            </el-table-column>
            <el-table-column label="行总价" width="120">
              <template #default="{ row }">
                ¥{{ row.totalPrice?.toFixed(2) || '0.00' }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" fixed="right">
              <template #default="{ $index }">
                <el-button type="danger" link @click="removeProduct($index)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-button type="primary" plain class="mt-2" @click="addProduct">
            <el-icon><Plus /></el-icon>
            添加产品
          </el-button>
        </div>
      </el-form-item>

      <!-- 金额汇总 -->
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="报价总金额">
            <div class="amount-display">¥{{ formData.totalAmount?.toFixed(2) || '0.00' }}</div>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="折扣后金额">
            <div class="amount-display">¥{{ formData.finalAmount?.toFixed(2) || '0.00' }}</div>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="备注">
        <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Warning } from '@element-plus/icons-vue'
import { quoteApi } from '@/api/crm/quote'
import { productApi } from '@/api/crm/product'
import type { QuoteSaveDTO, QuoteProductSaveDTO } from '@/types/crm/quote'
import type { OpportunityVO } from '@/types/crm/opportunity'
import type { ProductVO } from '@/types/crm/product'

const emit = defineEmits(['success'])

const visible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref()

const opportunityList = ref<OpportunityVO[]>([])
const productList = ref<ProductVO[]>([])

// 表单数据
const formData = reactive<QuoteSaveDTO & { customerName?: string; ownerName?: string }>({
  opportunityId: undefined,
  customerId: undefined,
  quoteName: '',
  totalAmount: 0,
  discountRate: 0,
  finalAmount: 0,
  expireDate: '',
  remark: '',
  ownerId: undefined,
  products: [],
  customerName: '',
  ownerName: ''
})

// 表单验证规则
const rules = {
  opportunityId: [{ required: true, message: '请选择商机', trigger: 'change' }],
  ownerId: [{ required: true, message: '负责人不能为空', trigger: 'change' }],
  products: [{ required: true, message: '产品明细不能为空', trigger: 'change' }]
}

// 加载产品列表
const loadProductList = async () => {
  try {
    const result = await productApi.getList({ pageNum: 1, pageSize: 100, status: 1 })
    productList.value = result.list
  } catch (error) {
    console.error('加载产品列表失败', error)
  }
}

// 商机选择变化
const handleOpportunityChange = (opportunityId: number) => {
  const opportunity = opportunityList.value.find(item => item.id === opportunityId)
  if (opportunity) {
    formData.customerId = opportunity.customerId
    formData.customerName = opportunity.customerName || ''
    formData.ownerId = opportunity.ownerId
    formData.ownerName = opportunity.ownerName || ''
    // 自动生成报价单名称
    if (!formData.quoteName && opportunity.oppName) {
      formData.quoteName = `${opportunity.oppName}-报价单`
    }
  }
}

// 产品选择变化
const handleProductChange = (row: QuoteProductSaveDTO, index: number) => {
  const product = productList.value.find(item => item.id === row.productId)
  if (product) {
    row.productName = product.productName
    row.unit = product.unit
    row.unitPrice = product.price
    // 检查价格区间
    checkPriceRange(row, product)
    calculateRowTotal(row, index)
  }
}

// 单价变化处理
const handleUnitPriceChange = (row: QuoteProductSaveDTO, index: number) => {
  const product = productList.value.find(item => item.id === row.productId)
  if (product) {
    checkPriceRange(row, product)
  }
  calculateRowTotal(row, index)
}

// 检查价格区间
const checkPriceRange = (row: QuoteProductSaveDTO, product: ProductVO) => {
  const minPrice = product.minPrice || 0
  const standardPrice = product.price
  row.priceWarning = row.unitPrice < minPrice
}

// 计算行总价
const calculateRowTotal = (row: QuoteProductSaveDTO, index: number) => {
  const quantity = row.quantity || 0
  const unitPrice = row.unitPrice || 0
  const discountRate = row.discountRate || 0

  // 行总价 = 数量 * 单价 * (1 - 行折扣率/100)
  row.totalPrice = quantity * unitPrice * (1 - discountRate / 100)

  // 计算总金额
  calculateTotal()
}

// 计算总金额
const calculateTotal = () => {
  // 报价总金额 = 所有行总价之和
  const totalAmount = formData.products.reduce((sum, row) => sum + (row.totalPrice || 0), 0)
  formData.totalAmount = totalAmount

  // 最终金额 = 报价总金额 * (1 - 整体折扣率/100)
  const discountRate = formData.discountRate || 0
  formData.finalAmount = totalAmount * (1 - discountRate / 100)
}

// 添加产品
const addProduct = () => {
  formData.products.push({
    productId: undefined,
    productName: '',
    unit: '',
    quantity: 1,
    unitPrice: 0,
    discountRate: 0,
    totalPrice: 0,
    remark: ''
  })
}

// 删除产品
const removeProduct = (index: number) => {
  formData.products.splice(index, 1)
  calculateTotal()
}

// 打开弹窗
const open = async (id?: number, opportunities?: OpportunityVO[]) => {
  // 设置商机列表
  if (opportunities) {
    opportunityList.value = opportunities
  }

  // 加载产品列表
  await loadProductList()

  // 重置表单
  formData.id = undefined
  formData.opportunityId = undefined
  formData.customerId = undefined
  formData.quoteName = ''
  formData.totalAmount = 0
  formData.discountRate = 0
  formData.finalAmount = 0
  formData.expireDate = ''
  formData.remark = ''
  formData.ownerId = undefined
  formData.products = []
  formData.customerName = ''
  formData.ownerName = ''

  isEdit.value = !!id

  // 如果是编辑，加载详情
  if (id) {
    try {
      const detail = await quoteApi.getById(id)
      formData.id = detail.id
      formData.opportunityId = detail.opportunityId
      formData.customerId = detail.customerId
      formData.quoteName = detail.quoteName || ''
      formData.totalAmount = detail.totalAmount
      formData.discountRate = detail.discountRate || 0
      formData.finalAmount = detail.finalAmount || 0
      formData.expireDate = detail.expireDate || ''
      formData.remark = detail.remark || ''
      formData.ownerId = detail.ownerId
      formData.customerName = detail.customerName || ''
      formData.ownerName = detail.ownerName || ''

      // 设置产品明细
      if (detail.products && detail.products.length > 0) {
        formData.products = detail.products.map(p => ({
          id: p.id,
          productId: p.productId,
          productName: p.productName || '',
          unit: p.unit || '',
          quantity: p.quantity,
          unitPrice: p.unitPrice,
          discountRate: p.discountRate || 0,
          totalPrice: p.totalPrice || 0,
          remark: p.remark || ''
        }))
      }
    } catch (error) {
      ElMessage.error('加载报价单详情失败')
      return
    }
  }

  visible.value = true
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate()

    // 检查产品明细
    if (formData.products.length === 0) {
      ElMessage.warning('请添加产品明细')
      return
    }

    // 检查产品是否已选择
    const invalidProducts = formData.products.filter(p => !p.productId)
    if (invalidProducts.length > 0) {
      ElMessage.warning('请选择所有产品的产品名称')
      return
    }

    submitting.value = true

    if (isEdit.value) {
      await quoteApi.update(formData)
      ElMessage.success('修改成功')
    } else {
      await quoteApi.create(formData)
      ElMessage.success('新增成功')
    }

    emit('success')
    visible.value = false
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  } finally {
    submitting.value = false
  }
}

// 暴露方法
defineExpose({ open })
</script>

<style scoped lang="scss">
.product-table-wrapper {
  width: 100%;

  .price-warning {
    display: flex;
    align-items: center;
    margin-top: 4px;

    .warning-text {
      color: #E6A23C;
      font-size: 12px;
      margin-left: 4px;
    }
  }
}

.amount-display {
  font-size: 16px;
  font-weight: 600;
  color: #409EFF;
}

.ml-2 {
  margin-left: 8px;
}

.text-gray-500 {
  color: #909399;
}

.mt-2 {
  margin-top: 8px;
}
</style>