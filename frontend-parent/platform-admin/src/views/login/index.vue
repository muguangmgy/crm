<template>
  <div class="login-container">
    <!-- 动态背景 -->
    <div class="animated-bg">
      <div class="grid-overlay"></div>
      <div class="gradient-overlay"></div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-wrapper">
      <div class="login-left">
        <div class="brand-section">
          <div class="logo-container">
            <div class="logo-icon">
              <svg
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
              >
                <path
                    d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                />
                <circle cx="9" cy="7" r="4"/>
                <path
                    d="M23 21v-2a4 4 0 0 0-3-3.87"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                />
                <path
                    d="M16 3.13a4 4 0 0 1 0 7.75"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                />
              </svg>
            </div>
            <h1 class="brand-title">CRM 客户关系管理系统</h1>
          </div>
          <p class="brand-desc">客户全生命周期管理 · 让销售更高效</p>
        </div>

        <!-- 销售业绩增长插图 -->
        <div class="illustration">
          <svg viewBox="0 0 300 170" class="illustration-svg">
            <!-- 卡片背景 -->
            <rect
                x="6"
                y="6"
                width="288"
                height="158"
                rx="14"
                fill="rgba(255,255,255,0.08)"
                stroke="rgba(255,255,255,0.25)"
                stroke-width="1"
            />

            <!-- 卡片标题 -->
            <text
                x="22"
                y="32"
                fill="rgba(255,255,255,0.95)"
                font-size="13"
                font-weight="600"
            >
              销售业绩趋势
            </text>
            <text x="22" y="48" fill="rgba(255,255,255,0.6)" font-size="10">
              本季度营收增长
            </text>

            <!-- 增长徽章 -->
            <g class="badge-group">
              <rect
                  x="232"
                  y="20"
                  width="50"
                  height="22"
                  rx="11"
                  fill="rgba(82, 196, 26, 0.25)"
                  stroke="rgba(82, 196, 26, 0.5)"
                  stroke-width="1"
              />
              <path
                  d="M244 34l4-5 3 3 5-7"
                  stroke="#52c41a"
                  stroke-width="1.6"
                  fill="none"
                  stroke-linecap="round"
                  stroke-linejoin="round"
              />
              <text
                  x="262"
                  y="35"
                  fill="#b7eb8f"
                  font-size="11"
                  font-weight="600"
              >
                +15%
              </text>
            </g>

            <!-- 基线 -->
            <line
                x1="22"
                y1="140"
                x2="278"
                y2="140"
                stroke="rgba(255,255,255,0.2)"
                stroke-width="1"
            />

            <!-- 柱状图（递增）-->
            <g class="bar-group">
              <rect x="40" y="108" width="26" height="32" rx="3" fill="rgba(255,255,255,0.25)"/>
              <rect x="86" y="92" width="26" height="48" rx="3" fill="rgba(255,255,255,0.32)"/>
              <rect x="132" y="74" width="26" height="66" rx="3" fill="rgba(255,255,255,0.42)"/>
              <rect x="178" y="56" width="26" height="84" rx="3" fill="rgba(255,255,255,0.55)"/>
            </g>

            <!-- 上升趋势线 -->
            <polyline
                points="53,108 99,92 145,74 191,56 220,40"
                fill="none"
                stroke="#bae7ff"
                stroke-width="2.5"
                stroke-linecap="round"
                stroke-linejoin="round"
                class="trend-line"
            />
            <!-- 数据点 -->
            <circle cx="53" cy="108" r="3.5" fill="#bae7ff"/>
            <circle cx="99" cy="92" r="3.5" fill="#bae7ff"/>
            <circle cx="145" cy="74" r="3.5" fill="#bae7ff"/>
            <circle cx="191" cy="56" r="3.5" fill="#bae7ff"/>
            <!-- 箭头 -->
            <path
                d="M214 36 L224 40 L218 46"
                fill="none"
                stroke="#bae7ff"
                stroke-width="2.5"
                stroke-linecap="round"
                stroke-linejoin="round"
            />

            <!-- 客户网络节点 -->
            <g class="network-group">
              <line x1="245" y1="100" x2="265" y2="120" stroke="rgba(255,255,255,0.4)" stroke-width="1.2"/>
              <line x1="245" y1="100" x2="263" y2="80" stroke="rgba(255,255,255,0.4)" stroke-width="1.2"/>
              <line x1="245" y1="100" x2="225" y2="118" stroke="rgba(255,255,255,0.4)" stroke-width="1.2"/>
              <circle cx="245" cy="100" r="6" fill="#40a9ff" class="node-center"/>
              <circle cx="265" cy="120" r="4" fill="rgba(255,255,255,0.7)"/>
              <circle cx="263" cy="80" r="4" fill="rgba(255,255,255,0.7)"/>
              <circle cx="225" cy="118" r="4" fill="rgba(255,255,255,0.7)"/>
            </g>
          </svg>
        </div>

        <!-- CRM 核心特性 -->
        <div class="features">
          <div class="feature-item">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                <circle cx="9" cy="7" r="4"/>
                <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
                <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
              </svg>
            </div>
            <div class="feature-text">
              <div class="feature-title">客户管理</div>
              <div class="feature-desc">全渠道客户档案</div>
            </div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M3 6h18l-3 5H6L3 6z" stroke-linejoin="round"/>
                <path d="M6 11l2.5 5h7L18 11" stroke-linejoin="round"/>
                <path d="M9.5 16l-1 4M14.5 16l1 4"/>
                <circle cx="12" cy="21" r="0.5" fill="currentColor"/>
              </svg>
            </div>
            <div class="feature-text">
              <div class="feature-title">商机跟进</div>
              <div class="feature-desc">销售漏斗转化</div>
            </div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="20" x2="18" y2="10"/>
                <line x1="12" y1="20" x2="12" y2="4"/>
                <line x1="6" y1="20" x2="6" y2="14"/>
              </svg>
            </div>
            <div class="feature-text">
              <div class="feature-title">销售分析</div>
              <div class="feature-desc">数据驱动决策</div>
            </div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                <polyline points="14 2 14 8 20 8"/>
                <line x1="9" y1="15" x2="15" y2="15"/>
                <line x1="9" y1="18" x2="13" y2="18"/>
              </svg>
            </div>
            <div class="feature-text">
              <div class="feature-title">合同收款</div>
              <div class="feature-desc">回款闭环管理</div>
            </div>
          </div>
        </div>
      </div>
      <div class="login-right">
        <div class="login-box">
          <div class="login-header">
            <div class="login-icon">
              <svg
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
              >
                <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"/>
                <polyline points="10 17 15 12 10 7"/>
                <line x1="15" y1="12" x2="3" y2="12"/>
              </svg>
            </div>
            <h2 class="login-title">欢迎登录</h2>
            <p class="login-subtitle">请输入您的账号信息</p>
          </div>
          <el-form
              ref="formRef"
              :model="loginForm"
              :rules="rules"
              class="login-form"
              @keyup.enter="handleLogin"
          >
            <el-form-item prop="userCode">
              <el-input
                  v-model="loginForm.userCode"
                  placeholder="请输入用户名"
                  size="large"
                  class="custom-input"
              >
                <template #prefix>
                  <svg
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                      class="input-icon"
                  >
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                    <circle cx="12" cy="7" r="4"/>
                  </svg>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                  v-model="loginForm.password"
                  type="password"
                  placeholder="请输入密码"
                  size="large"
                  class="custom-input"
                  show-password
              >
                <template #prefix>
                  <svg
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                      class="input-icon"
                  >
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                    <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                  </svg>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="captcha">
              <div class="captcha-row">
                <el-input
                    v-model="loginForm.captcha"
                    placeholder="请输入验证码"
                    size="large"
                    class="custom-input"
                >
                  <template #prefix>
                    <svg
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        class="input-icon"
                    >
                      <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                    </svg>
                  </template>
                </el-input>
                <div class="captcha-box" @click="refreshCaptcha">
                  <img :src="captchaImage" class="captcha-img"/>
                  <div class="captcha-refresh">
                    <svg
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        class="refresh-icon"
                    >
                      <polyline points="23 4 23 10 17 10"/>
                      <polyline points="1 20 1 14 7 14"/>
                      <path
                          d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"
                      />
                    </svg>
                  </div>
                </div>
              </div>
            </el-form-item>
            <el-form-item>
              <div class="login-options">
                <el-checkbox
                    v-model="loginForm.rememberMe"
                    class="custom-checkbox"
                >
                  记住密码
                </el-checkbox>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button
                  type="primary"
                  :loading="loading"
                  class="login-btn"
                  size="large"
                  @click="handleLogin"
              >
                <span class="btn-text">{{
                    loading ? "登录中..." : "登 录"
                  }}</span>
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {onMounted, reactive, ref} from "vue";
import {useRouter} from "vue-router";
import type {FormInstance, FormRules} from "element-plus";
import {ElMessage} from "element-plus";
import {authApi} from "@/api/auth";
import {useUserStore} from "@/stores/user";

const router = useRouter();
const userStore = useUserStore();

const formRef = ref<FormInstance>();
const loading = ref(false);
const captchaImage = ref("");

const loginForm = reactive({
  userCode: "",
  password: "",
  captcha: "",
  rememberMe: false,
});

const rules: FormRules = {
  userCode: [{required: true, message: "请输入用户名", trigger: "blur"}],
  password: [{required: true, message: "请输入密码", trigger: "blur"}],
  captcha: [{required: true, message: "请输入验证码", trigger: "blur"}],
};

async function refreshCaptcha() {
  try {
    const res = await authApi.getCaptcha();
    captchaImage.value = res.captchaImage;
  } catch {
    // 错误已在 request.ts 拦截器中显示，无需重复提示
  }
}

async function handleLogin() {
  const valid = await formRef.value?.validate();
  if (!valid) return;

  loading.value = true;
  try {
    const success = await userStore.login({
      userCode: loginForm.userCode,
      password: loginForm.password,
      captcha: loginForm.captcha,
      rememberMe: loginForm.rememberMe,
    });

    if (success) {
      ElMessage.success("登录成功");
      // 交由路由守卫加载菜单后，跳到有权限的首页（优先仪表盘）
      router.push("/");
    } else {
      refreshCaptcha();
    }
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  refreshCaptcha();
});
</script>

<style lang="scss" scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

// 动态背景
.animated-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #0a1929 0%, #0d2137 50%, #0a2540 100%);
}

// 细网格纹理
.grid-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: linear-gradient(
      rgba(255, 255, 255, 0.04) 1px,
      transparent 1px
  ),
  linear-gradient(90deg, rgba(255, 255, 255, 0.04) 1px, transparent 1px);
  background-size: 40px 40px;
  // 中心淡出，避免遮挡卡片
  -webkit-mask-image: radial-gradient(
      ellipse at center,
      black 20%,
      transparent 75%
  );
  mask-image: radial-gradient(ellipse at center, black 20%, transparent 75%);
}

// 聚焦光晕（卡片后方）
.gradient-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(
      ellipse 60% 50% at center,
      rgba(24, 144, 255, 0.28) 0%,
      rgba(9, 109, 217, 0.12) 35%,
      transparent 70%
  );
  animation: haloPulse 8s ease-in-out infinite;
}

@keyframes haloPulse {
  0%,
  100% {
    opacity: 0.85;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.05);
  }
}

// 登录卡片
.login-wrapper {
  display: flex;
  width: 950px;
  height: 580px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24px;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.4);
  overflow: hidden;
  position: relative;
  z-index: 10;
  animation: cardAppear 0.6s ease-out;
}

@keyframes cardAppear {
  from {
    opacity: 0;
    transform: translateY(30px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.login-left {
  width: 480px;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  padding: 44px 36px;
  display: flex;
  flex-direction: column;
  color: #fff;
  position: relative;
  overflow: hidden;

  &::before {
    content: "";
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(
        circle,
        rgba(255, 255, 255, 0.1) 0%,
        transparent 50%
    );
    animation: shimmer 8s ease-in-out infinite;
  }
}

@keyframes shimmer {
  0%,
  100% {
    transform: translate(0, 0);
  }
  50% {
    transform: translate(25%, 25%);
  }
}

.brand-section {
  margin-bottom: 24px;
  position: relative;
  z-index: 1;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 10px;
}

.logo-icon {
  width: 46px;
  height: 46px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: logoPulse 2.5s ease-in-out infinite;

  svg {
    width: 26px;
    height: 26px;
    color: #fff;
  }
}

@keyframes logoPulse {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.brand-title {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 1px;
  animation: titleFade 0.8s ease-out;
}

@keyframes titleFade {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.brand-desc {
  font-size: 13px;
  opacity: 0.85;
  animation: titleFade 0.8s ease-out 0.2s backwards;
}

// 销售业绩插图
.illustration {
  margin: 8px 0 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 1;
  animation: illustFade 0.8s ease-out 0.3s backwards;
}

@keyframes illustFade {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.illustration-svg {
  width: 100%;
  max-width: 340px;
  height: auto;
}

// 趋势线绘制动画
.trend-line {
  stroke-dasharray: 300;
  stroke-dashoffset: 300;
  animation: drawLine 2s ease-out 0.6s forwards;
}

@keyframes drawLine {
  to {
    stroke-dashoffset: 0;
  }
}

// 客户网络节点呼吸
.node-center {
  animation: nodePulse 2s ease-in-out infinite;
  transform-origin: 245px 100px;
  transform-box: fill-box;
}

@keyframes nodePulse {
  0%,
  100% {
    opacity: 0.9;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.15);
  }
}

// CRM 核心特性
.features {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  position: relative;
  z-index: 1;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 10px;
  backdrop-filter: blur(4px);
  animation: featureSlide 0.5s ease-out backwards;
  transition: all 0.25s ease;

  &:hover {
    background: rgba(255, 255, 255, 0.2);
    transform: translateY(-2px);
  }

  &:nth-child(1) {
    animation-delay: 0.5s;
  }

  &:nth-child(2) {
    animation-delay: 0.6s;
  }

  &:nth-child(3) {
    animation-delay: 0.7s;
  }

  &:nth-child(4) {
    animation-delay: 0.8s;
  }
}

@keyframes featureSlide {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.feature-icon {
  width: 32px;
  height: 32px;
  background: rgba(255, 255, 255, 0.18);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  svg {
    width: 18px;
    height: 18px;
    color: #fff;
  }
}

.feature-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.feature-title {
  font-size: 13px;
  font-weight: 600;
  color: #fff;
  line-height: 1.2;
}

.feature-desc {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.75);
  line-height: 1.2;
}

// 右侧登录区
.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 50px;
  background: #fff;
}

.login-box {
  width: 100%;
  max-width: 340px;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
  animation: headerAppear 0.6s ease-out;
}

@keyframes headerAppear {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-icon {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  animation: iconBounce 0.6s ease-out;

  svg {
    width: 28px;
    height: 28px;
    color: #fff;
  }
}

@keyframes iconBounce {
  0% {
    transform: scale(0);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}

.login-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.login-subtitle {
  font-size: 14px;
  color: #909399;
}

.login-form {
  animation: formAppear 0.6s ease-out 0.2s backwards;
}

@keyframes formAppear {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.custom-input {
  :deep(.el-input__wrapper) {
    border-radius: 12px;
    padding: 8px 16px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
    transition: all 0.25s ease;

    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
      border-color: #c0c4cc;
    }

    &.is-focus {
      box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
      border-color: #1890ff;
    }
  }

  :deep(.el-input__inner) {
    height: 44px;
    line-height: 44px;

    // 自动填充背景色透明
    &:-webkit-autofill,
    &:-webkit-autofill:hover,
    &:-webkit-autofill:focus,
    &:-webkit-autofill:active {
      -webkit-box-shadow: 0 0 0 1000px #fff inset;
      -webkit-text-fill-color: #303133;
      transition: background-color 5000s ease-in-out 0s;
    }
  }

  // Focus states for keyboard navigation
  :deep(.el-input__prefix) {
    transition: color 0.2s ease;
  }

  :deep(.el-input__wrapper.is-focus .el-input__prefix .input-icon) {
    color: #1890ff;
  }
}

.input-icon {
  width: 18px;
  height: 18px;
  color: #909399;
}

.captcha-row {
  display: flex;
  gap: 12px;
  width: 100%;

  .custom-input {
    flex: 1;
  }
}

.captcha-box {
  width: 110px;
  height: 60px;
  cursor: pointer;
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.25s ease;
  border: 1px solid #dcdfe6;

  &:hover {
    transform: scale(1.02);
    border-color: #c0c4cc;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  }

  &:focus-visible {
    outline: 2px solid rgba(24, 144, 255, 0.5);
    outline-offset: 2px;
  }

  &:hover .captcha-refresh {
    opacity: 1;
  }
}

.captcha-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 12px;
}

.captcha-refresh {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.refresh-icon {
  width: 20px;
  height: 20px;
  color: #fff;
}

.login-options {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.custom-checkbox {
  :deep(.el-checkbox__label) {
    color: #606266;
    font-size: 14px;
  }
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 12px;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  cursor: pointer;
  transition: all 0.25s ease;

  &:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(24, 144, 255, 0.4);
  }

  &:active:not(:disabled) {
    transform: translateY(0);
    box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
  }

  &:focus-visible {
    outline: 2px solid rgba(24, 144, 255, 0.5);
    outline-offset: 2px;
  }
}

.btn-icon {
  width: 18px;
  height: 18px;
}

.btn-text {
  font-weight: 500;
}

// 响应式
@media (max-width: 768px) {
  .login-wrapper {
    width: 90%;
    max-width: 420px;
    height: auto;
    flex-direction: column;
  }

  .login-left {
    width: 100%;
    padding: 36px 28px;

    &::before {
      display: none;
    }
  }

  .login-right {
    padding: 30px;
  }

  .brand-title {
    font-size: 18px;
  }

  .illustration {
    display: none;
  }

  .features {
    grid-template-columns: 1fr 1fr;
    gap: 10px;
  }
}

// 减少动画偏好
@media (prefers-reduced-motion: reduce) {
  .gradient-overlay,
  .login-wrapper,
  .logo-icon,
  .feature-item,
  .login-header,
  .login-form,
  .illustration,
  .trend-line,
  .node-center {
    animation: none;
  }

  .login-btn:hover,
  .feature-item:hover,
  .captcha-box:hover {
    transform: none;
  }
}
</style>
