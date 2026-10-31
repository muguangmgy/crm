<template>
  <div class="page-container" v-loading="!dictLoaded">
    <!-- 客户基本信息 -->
    <el-card class="info-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="title">{{ customer.customerName }}</span>
          <div class="actions">
            <el-button type="warning" v-permission="'crm:customer:pool:release'" @click="handleRelease">释放</el-button>
            <el-button type="info" v-permission="'crm:customer:pool:transfer'" @click="handleTransfer">转移</el-button>
            <el-button @click="handleBack">返回</el-button>
          </div>
        </div>
      </template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="客户编号">{{ customer.customerNo }}</el-descriptions-item>
        <el-descriptions-item label="客户类型">
          <el-tag :type="customer.customerType === 1 ? 'primary' : 'success'">
            {{ getDictLabelFromData(customerTypeDict, customer.customerType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="客户等级">
          <el-tag :type="getLevelTagType(customer.level)">{{
              getDictLabelFromData(customerLevelDict, customer.level)
            }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="行业">
          <span v-if="customer.customerType === 1 && customer.industry">{{
              getDictLabelFromData(industryDict, customer.industry || '') || customer.industry
            }}</span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="企业规模">
          <span v-if="customer.customerType === 1 && customer.companySize">{{
              getDictLabelFromData(companySizeDict, customer.companySize || '') || customer.companySize
            }}</span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="客户来源">
          {{ getDictLabelFromData(customerSourceDict, customer.source || '') || customer.source }}
        </el-descriptions-item>
        <el-descriptions-item label="省份">{{ customer.province }}</el-descriptions-item>
        <el-descriptions-item label="城市">{{ customer.city }}</el-descriptions-item>
        <el-descriptions-item label="详细地址" :span="2">{{ customer.address || '-' }}</el-descriptions-item>
        <el-descriptions-item label="电话">{{ customer.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ customer.email }}</el-descriptions-item>
        <el-descriptions-item label="负责人">{{ customer.ownerName }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ customer.createTime }}</el-descriptions-item>
        <el-descriptions-item label="标签">
          <el-tag v-for="tag in customer.tags" :key="tag.id" :color="tag.tagColor" effect="dark"
                  style="margin-right: 4px">
            {{ tag.tagName }}
          </el-tag>
          <span v-if="!customer.tags?.length">无</span>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ customer.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 联系人列表 -->
    <el-card class="contact-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>联系人</span>
          <el-button type="primary" size="small" v-permission="'crm:customer:edit'" @click="handleAddContact">新增
          </el-button>
        </div>
      </template>
      <el-table :data="contacts" border stripe size="small">
        <el-table-column type="index" label="序号" width="60"/>
        <el-table-column prop="contactName" label="姓名" width="200"/>
        <el-table-column prop="position" label="职位" width="200"/>
        <el-table-column prop="department" label="部门" width="200"/>
        <el-table-column prop="phone" label="电话" width="180"/>
        <el-table-column prop="mobile" label="手机" width="180"/>
        <el-table-column prop="email" label="邮箱" width="180"/>
        <el-table-column prop="wechat" label="微信" width="180"/>
        <el-table-column prop="isPrimary" label="主要" width="60">
          <template #default="{ row }">
            <el-tag :type="row.isPrimary === 1 ? 'danger' : 'info'" size="small">
              {{ row.isPrimary === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEditContact(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDeleteContact(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 跟进记录 -->
    <el-card class="follow-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>跟进记录</span>
          <el-button type="primary" size="small" v-permission="'crm:customer:edit'" @click="handleAddFollow">新增
          </el-button>
        </div>
      </template>
      <el-table :data="followRecords" border stripe size="small">
        <el-table-column type="index" label="序号" width="60"/>
        <el-table-column prop="followType" label="跟进方式" width="100">
          <template #default="{ row }">
            {{ getDictLabelFromData(followTypeDict, row.followType) || row.followType }}
          </template>
        </el-table-column>
        <el-table-column prop="followContent" label="跟进内容" min-width="200"/>
        <el-table-column prop="nextFollowTime" label="下次跟进" width="180"/>
        <el-table-column prop="ownerName" label="跟进人" width="100"/>
        <el-table-column prop="createTime" label="跟进时间" width="180"/>
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" link size="small" @click="handleDeleteFollow(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 联系人对话框 -->
    <el-dialog v-model="contactVisible" :title="contactTitle" width="500px" destroy-on-close>
      <el-form ref="contactFormRef" :model="contactForm" :rules="contactRules" label-width="80px">
        <el-form-item label="姓名" prop="contactName">
          <el-input v-model="contactForm.contactName" placeholder="请输入姓名"/>
        </el-form-item>
        <el-form-item label="职位">
          <el-input v-model="contactForm.position" placeholder="请输入职位"/>
        </el-form-item>
        <el-form-item label="部门">
          <el-input v-model="contactForm.department" placeholder="请输入部门"/>
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="contactForm.phone" placeholder="请输入电话"/>
        </el-form-item>
        <el-form-item label="手机">
          <el-input v-model="contactForm.mobile" placeholder="请输入手机"/>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="contactForm.email" placeholder="请输入邮箱"/>
        </el-form-item>
        <el-form-item label="微信">
          <el-input v-model="contactForm.wechat" placeholder="请输入微信"/>
        </el-form-item>
        <el-form-item label="主要联系人">
          <el-switch v-model="contactForm.isPrimary" :active-value="1" :inactive-value="0"/>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="contactForm.remark" type="textarea" :rows="2" placeholder="请输入备注"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="contactVisible = false">取消</el-button>
        <el-button type="primary" :loading="contactLoading" @click="handleContactSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 跟进记录对话框 -->
    <el-dialog v-model="followVisible" :title="followTitle" width="500px" destroy-on-close>
      <el-form ref="followFormRef" :model="followForm" :rules="followRules" label-width="80px">
        <el-form-item label="跟进方式" prop="followType">
          <el-select v-model="followForm.followType" placeholder="请选择跟进方式">
            <el-option v-for="item in followTypeDict" :key="item.itemValue" :label="item.itemLabel"
                       :value="item.itemValue"/>
          </el-select>
        </el-form-item>
        <el-form-item label="跟进内容" prop="followContent">
          <el-input v-model="followForm.followContent" type="textarea" :rows="3" placeholder="请输入跟进内容"/>
        </el-form-item>
        <el-form-item label="下次跟进">
          <el-date-picker v-model="followForm.nextFollowTime" type="datetime" placeholder="请选择下次跟进时间"
                          value-format="YYYY-MM-DD HH:mm:ss"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="followVisible = false">取消</el-button>
        <el-button type="primary" :loading="followLoading" @click="handleFollowSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 转移对话框 -->
    <el-dialog v-model="transferVisible" title="转移客户" width="400px">
      <el-form>
        <el-form-item label="新负责人">
          <el-select v-model="transferForm.newOwnerId" placeholder="请选择新负责人" style="width: 100%">
            <el-option v-for="user in userOptions" :key="user.id" :label="user.userName" :value="user.id"/>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="transferVisible = false">取消</el-button>
        <el-button type="primary" @click="handleTransferSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {computed, onMounted, reactive, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import type {FormInstance} from 'element-plus'
import {ElMessage, ElMessageBox} from 'element-plus'
import {customerApi} from '@/api/crm/customer'
import {getDictLabelFromData, useDicts} from '@/composables/useDict'
import type {
  ContactSaveDTO,
  ContactVO,
  CustomerDetailVO,
  CustomerVO,
  FollowSaveDTO,
  FollowVO
} from '@/types/crm/customer'

const router = useRouter()
const route = useRoute()

// 需要的字典列表
const requiredDicts = [
  'crm_customer_type',
  'crm_customer_level',
  'crm_industry',
  'crm_company_size',
  'crm_customer_source',
  'crm_follow_type'
]

// 预加载所有需要的字典
const {dataMap, loaded: dictLoaded} = useDicts(requiredDicts)

// 字典数据
const customerTypeDict = computed(() => dataMap.value['crm_customer_type'] || [])
const customerLevelDict = computed(() => dataMap.value['crm_customer_level'] || [])
const industryDict = computed(() => dataMap.value['crm_industry'] || [])
const companySizeDict = computed(() => dataMap.value['crm_company_size'] || [])
const customerSourceDict = computed(() => dataMap.value['crm_customer_source'] || [])
const followTypeDict = computed(() => dataMap.value['crm_follow_type'] || [])

// 客户ID
const customerId = Number(route.params.id)

// 数据
const customer = ref<CustomerVO>({} as CustomerVO)
const contacts = ref<ContactVO[]>([])
const followRecords = ref<FollowVO[]>([])
const userOptions = ref<{ id: number; userName: string }[]>([])

// 联系人对话框
const contactVisible = ref(false)
const contactTitle = ref('')
const contactLoading = ref(false)
const contactFormRef = ref<FormInstance>()
const contactForm = reactive<ContactSaveDTO>({
  customerId: customerId,
  contactName: '',
  isPrimary: 0
})

// 跟进记录对话框
const followVisible = ref(false)
const followTitle = ref('')
const followLoading = ref(false)
const followFormRef = ref<FormInstance>()
const followForm = reactive<FollowSaveDTO>({
  customerId: customerId,
  followContent: ''
})

// 转移对话框
const transferVisible = ref(false)
const transferForm = reactive({customerId: customerId, newOwnerId: undefined as number | undefined})

// 表单验证规则
const contactRules = {
  contactName: [{required: true, message: '请输入姓名', trigger: 'blur'}]
}

const followRules = {
  followContent: [{required: true, message: '请输入跟进内容', trigger: 'blur'}]
}

// 加载详情
const loadDetail = async () => {
  try {
    const res: CustomerDetailVO = await customerApi.getById(customerId)
    customer.value = res.customer
    contacts.value = res.contacts || []
    followRecords.value = res.followRecords || []
  } catch (e) {
    console.error(e)
    ElMessage.error('获取客户详情失败')
  }
}

// 释放到公海池
const handleRelease = async () => {
  await ElMessageBox.confirm('确定将此客户释放到公海池?', '提示')
  await customerApi.releaseToPool(customerId)
  ElMessage.success('释放成功')
  handleBack()
}

// 转移
const handleTransfer = () => {
  transferForm.newOwnerId = undefined
  transferVisible.value = true
}

// 转移提交
const handleTransferSubmit = async () => {
  if (!transferForm.newOwnerId) {
    ElMessage.warning('请选择新负责人')
    return
  }
  await customerApi.transfer({customerId: transferForm.customerId, newOwnerId: transferForm.newOwnerId})
  ElMessage.success('转移成功')
  transferVisible.value = false
  loadDetail()
}

// 返回
const handleBack = () => {
  router.back()
}

// 新增联系人
const handleAddContact = () => {
  contactTitle.value = '新增联系人'
  contactForm.id = undefined
  contactForm.contactName = ''
  contactForm.position = undefined
  contactForm.department = undefined
  contactForm.phone = undefined
  contactForm.mobile = undefined
  contactForm.email = undefined
  contactForm.wechat = undefined
  contactForm.isPrimary = 0
  contactForm.remark = undefined
  contactVisible.value = true
}

// 编辑联系人
const handleEditContact = (row: ContactVO) => {
  contactTitle.value = '编辑联系人'
  contactForm.id = row.id
  contactForm.contactName = row.contactName
  contactForm.position = row.position
  contactForm.department = row.department
  contactForm.phone = row.phone
  contactForm.mobile = row.mobile
  contactForm.email = row.email
  contactForm.wechat = row.wechat
  contactForm.isPrimary = row.isPrimary
  contactForm.remark = row.remark
  contactVisible.value = true
}

// 删除联系人
const handleDeleteContact = async (row: ContactVO) => {
  await ElMessageBox.confirm('确定删除此联系人?', '提示')
  await customerApi.deleteContact(row.id)
  ElMessage.success('删除成功')
  loadDetail()
}

// 提交联系人
const handleContactSubmit = async () => {
  await contactFormRef.value?.validate()
  contactLoading.value = true
  try {
    await customerApi.saveContact(contactForm)
    ElMessage.success(contactForm.id ? '修改成功' : '新增成功')
    contactVisible.value = false
    loadDetail()
  } catch (e) {
    console.error(e)
  } finally {
    contactLoading.value = false
  }
}

// 新增跟进记录
const handleAddFollow = () => {
  followTitle.value = '新增跟进记录'
  followForm.id = undefined
  followForm.followType = undefined
  followForm.followContent = ''
  followForm.nextFollowTime = undefined
  followVisible.value = true
}

// 删除跟进记录
const handleDeleteFollow = async (row: FollowVO) => {
  await ElMessageBox.confirm('确定删除此跟进记录?', '提示')
  await customerApi.deleteFollow(row.id)
  ElMessage.success('删除成功')
  loadDetail()
}

// 提交跟进记录
const handleFollowSubmit = async () => {
  await followFormRef.value?.validate()
  followLoading.value = true
  try {
    await customerApi.saveFollow(followForm)
    ElMessage.success('新增成功')
    followVisible.value = false
    loadDetail()
  } catch (e) {
    console.error(e)
  } finally {
    followLoading.value = false
  }
}

// 等级标签样式
const getLevelTagType = (level: number) => {
  const types: Record<number, string> = {1: '', 2: 'warning', 3: 'danger'}
  return types[level] || ''
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.info-card {
  margin-bottom: 20px;
}

.contact-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header .title {
  font-size: 18px;
  font-weight: bold;
}
</style>