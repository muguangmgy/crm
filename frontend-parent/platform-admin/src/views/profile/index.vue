<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <span>个人中心</span>
      </template>

      <el-tabs v-model="activeTab">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="info">
          <el-form :model="profileForm" label-width="80px" style="max-width: 400px">
            <el-form-item label="用户名">
              <el-input :value="userInfo?.userCode" disabled/>
            </el-form-item>
            <el-form-item label="姓名">
              <el-input v-model="profileForm.userName" placeholder="请输入姓名"/>
            </el-form-item>
            <el-form-item label="头像">
              <el-avatar :size="60" :src="profileForm.avatar || defaultAvatar">
                <el-icon>
                  <User/>
                </el-icon>
              </el-avatar>
              <el-input
                  v-model="profileForm.avatar"
                  placeholder="头像URL"
                  style="margin-top: 10px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdateProfile" :loading="profileLoading">
                保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 修改密码 -->
        <el-tab-pane label="修改密码" name="password">
          <el-form
              ref="passwordFormRef"
              :model="passwordForm"
              :rules="passwordRules"
              label-width="100px"
              style="max-width: 400px"
          >
            <el-form-item label="旧密码" prop="oldPassword">
              <el-input
                  v-model="passwordForm.oldPassword"
                  type="password"
                  placeholder="请输入旧密码"
                  show-password
              />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                  v-model="passwordForm.newPassword"
                  type="password"
                  placeholder="请输入新密码(6-20位)"
                  show-password
              />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                  v-model="passwordForm.confirmPassword"
                  type="password"
                  placeholder="请再次输入新密码"
                  show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleChangePassword" :loading="passwordLoading">
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import {computed, reactive, ref} from 'vue'
import type {FormInstance, FormRules} from 'element-plus'
import {ElMessage} from 'element-plus'
import {User} from '@element-plus/icons-vue'
import {authApi} from '@/api/auth'
import {useUserStore} from '@/stores/user'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const activeTab = ref('info')
const profileLoading = ref(false)
const passwordLoading = ref(false)
const passwordFormRef = ref<FormInstance>()

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 个人信息表单
const profileForm = reactive({
  userName: userInfo.value?.userName || '',
  avatar: userInfo.value?.avatar || ''
})

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码校验规则
const passwordRules: FormRules = {
  oldPassword: [
    {required: true, message: '请输入旧密码', trigger: 'blur'}
  ],
  newPassword: [
    {required: true, message: '请输入新密码', trigger: 'blur'},
    {min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur'}
  ],
  confirmPassword: [
    {required: true, message: '请确认新密码', trigger: 'blur'},
    {
      validator: (val, callback) => {
        if (val !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 修改个人信息
async function handleUpdateProfile() {
  profileLoading.value = true
  try {
    await authApi.updateProfile(profileForm)
    ElMessage.success('修改成功')
    await userStore.fetchUserInfo()
    profileForm.userName = userInfo.value?.userName || ''
    profileForm.avatar = userInfo.value?.avatar || ''
  } catch {
    ElMessage.error('修改失败')
  } finally {
    profileLoading.value = false
  }
}

// 修改密码
async function handleChangePassword() {
  const valid = await passwordFormRef.value?.validate()
  if (!valid) return

  passwordLoading.value = true
  try {
    await authApi.changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    // 清空表单
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    // 退出登录
    await userStore.logout()
  } catch {
    ElMessage.error('密码修改失败')
  } finally {
    passwordLoading.value = false
  }
}
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card {
  max-width: 600px;
}
</style>