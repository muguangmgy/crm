<template>
  <div class="page-container" v-loading="!dictLoaded">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="产品名称">
          <el-input v-model="queryParams.productName" placeholder="请输入产品名称" clearable/>
        </el-form-item>
        <el-form-item label="产品编码">
          <el-input v-model="queryParams.productCode" placeholder="请输入产品编码" clearable/>
        </el-form-item>
        <el-form-item label="分类">
          <el-tree-select
              v-model="queryParams.categoryId"
              :data="categoryTree"
              :props="{ label: 'categoryName', value: 'id', children: 'children' }"
              placeholder="请选择分类"
              clearable
              check-strictly
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option v-for="item in productStatusDict" :key="item.itemValue" :label="item.itemLabel"
                       :value="Number(item.itemValue)"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search/></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh/></el-icon>
            重置
          </el-button>
          <el-button type="success" v-permission="'crm:product:add'" @click="handleAdd">
            <el-icon><Plus/></el-icon>
            新增
          </el-button>
          <el-button type="info" v-permission="'crm:product:category:add'" @click="handleCategoryManage">
            <el-icon><Grid/></el-icon>
            分类管理
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column type="index" label="序号" width="60"/>
        <el-table-column prop="productNo" label="产品编号" width="150"/>
        <el-table-column prop="productName" label="产品名称" min-width="150"/>
        <el-table-column prop="productCode" label="产品编码" width="120"/>
        <el-table-column prop="categoryName" label="分类" width="120"/>
        <el-table-column prop="unit" label="单位" width="80">
          <template #default="{ row }">
            {{ getDictLabelFromData(productUnitDict, row.unit) || row.unit || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="price" label="标准价格" width="120">
          <template #default="{ row }">
            ¥{{ row.price?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="minPrice" label="最低价格" width="120">
          <template #default="{ row }">
            <span v-if="row.minPrice">¥{{ row.minPrice?.toFixed(2) }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ getDictLabelFromData(productStatusDict, row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"/>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link v-permission="'crm:product:edit'" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link v-permission="'crm:product:delete'" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSearch"
          @current-change="handleSearch"
      />
    </el-card>

    <!-- 产品表单弹窗 -->
    <ProductForm ref="productFormRef" @success="handleSearch"/>

    <!-- 分类管理弹窗 -->
    <CategoryTree ref="categoryTreeRef" @success="loadCategoryTree"/>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Grid } from '@element-plus/icons-vue'
import { productApi } from '@/api/crm/product'
import { useDicts, getDictLabelFromData } from '@/composables/useDict'
import ProductForm from './components/ProductForm.vue'
import CategoryTree from './components/CategoryTree.vue'
import type { ProductVO, ProductQueryDTO, CategoryVO } from '@/types/crm/product'

// 需要的字典列表
const requiredDicts = ['crm_product_unit', 'crm_product_status']

// 预加载字典
const { dataMap, loaded: dictLoaded } = useDicts(requiredDicts)

// 字典数据
const productUnitDict = computed(() => dataMap.value['crm_product_unit'] || [])
const productStatusDict = computed(() => dataMap.value['crm_product_status'] || [])

// 查询参数
const queryParams = reactive<ProductQueryDTO>({
  pageNum: 1,
  pageSize: 10
})

// 数据
const tableData = ref<ProductVO[]>([])
const total = ref(0)
const loading = ref(false)
const categoryTree = ref<CategoryVO[]>([])

// 弹窗引用
const productFormRef = ref()
const categoryTreeRef = ref()

// 加载分类树
const loadCategoryTree = async () => {
  try {
    categoryTree.value = await productApi.getCategoryTree()
  } catch (error) {
    console.error('加载分类树失败', error)
  }
}

// 加载列表
const loadList = async () => {
  loading.value = true
  try {
    const result = await productApi.getList(queryParams)
    tableData.value = result.list
    total.value = result.total
  } catch (error) {
    console.error('加载产品列表失败', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  queryParams.pageNum = 1
  loadList()
}

// 重置
const handleReset = () => {
  queryParams.productName = undefined
  queryParams.productCode = undefined
  queryParams.categoryId = undefined
  queryParams.status = undefined
  queryParams.pageNum = 1
  loadList()
}

// 新增
const handleAdd = () => {
  productFormRef.value?.open(undefined, categoryTree.value)
}

// 编辑
const handleEdit = (row: ProductVO) => {
  productFormRef.value?.open(row.id, categoryTree.value)
}

// 删除
const handleDelete = async (row: ProductVO) => {
  try {
    await ElMessageBox.confirm(`确定删除产品【${row.productName}】？`, '提示', { type: 'warning' })
    await productApi.delete(row.id)
    ElMessage.success('删除成功')
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 分类管理
const handleCategoryManage = () => {
  categoryTreeRef.value?.open(categoryTree.value)
}

// 初始化
onMounted(() => {
  loadCategoryTree()
  loadList()
})
</script>

<style scoped lang="scss">
.page-container {
  padding: 20px;

  .search-card {
    margin-bottom: 20px;
  }

  .table-card {
    .el-pagination {
      margin-top: 20px;
      justify-content: flex-end;
    }
  }
}
</style>