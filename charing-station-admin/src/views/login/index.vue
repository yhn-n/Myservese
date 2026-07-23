<template>
  <div class="login-container">
    <div class="form-container">
      <p class="title">新能源充电站管理平台</p>
      <form class="form" @submit.prevent="handleLogin">
        <div class="input-group">
          <label for="username">用户名</label>
          <input
            ref="usernameRef"
            v-model="loginForm.username"
            type="text"
            id="username"
            placeholder="请输入用户名"
          />
          <p v-if="errors.username" class="field-error">{{ errors.username }}</p>
        </div>
        <div class="input-group">
          <label for="password">密码</label>
          <input
            ref="passwordRef"
            v-model="loginForm.password"
            type="password"
            id="password"
            placeholder="请输入密码"
            @keyup.enter="handleLogin"
          />
          <p v-if="errors.password" class="field-error">{{ errors.password }}</p>
        </div>
        <div class="forgot">
          <a rel="noopener noreferrer" href="#">忘记密码 ?</a>
        </div>
        <button type="submit" class="sign" :disabled="loading">
          {{ loading ? '登录中...' : '登 录' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/admin'

const router = useRouter()
const loading = ref(false)
const usernameRef = ref(null)
const passwordRef = ref(null)

const loginForm = reactive({
  username: '',
  password: ''
})

const errors = reactive({
  username: '',
  password: ''
})

const validate = () => {
  errors.username = loginForm.username.trim() ? '' : '请输入用户名'
  errors.password = loginForm.password.trim() ? '' : '请输入密码'
  return !errors.username && !errors.password
}

const handleLogin = async () => {
  if (!validate()) return
  loading.value = true
  try {
    const res = await login(loginForm)
    localStorage.setItem('admin_token', res.data.token)
    localStorage.setItem('admin_info', JSON.stringify(res.data.admin))
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    // error handled by axios interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.form-container {
  width: 320px;
  border-radius: 0.75rem;
  background-color: rgba(17, 24, 39, 1);
  padding: 2rem;
  color: rgba(243, 244, 246, 1);
}

.title {
  text-align: center;
  font-size: 1.5rem;
  line-height: 2rem;
  font-weight: 700;
  margin: 0;
}

.form {
  margin-top: 1.5rem;
}

.input-group {
  margin-top: 0.25rem;
  font-size: 0.875rem;
  line-height: 1.25rem;
}

.input-group label {
  display: block;
  color: rgba(156, 163, 175, 1);
  margin-bottom: 4px;
}

.input-group input {
  width: 100%;
  border-radius: 0.375rem;
  border: 1px solid rgba(55, 65, 81, 1);
  outline: 0;
  background-color: rgba(17, 24, 39, 1);
  padding: 0.75rem 1rem;
  color: rgba(243, 244, 246, 1);
  font-size: 0.875rem;
  box-sizing: border-box;
}

.input-group input:focus {
  border-color: rgba(167, 139, 250);
}

.input-group input::placeholder {
  color: rgba(107, 114, 128, 1);
}

.field-error {
  color: #f87171;
  font-size: 0.75rem;
  margin: 4px 0 0;
}

.forgot {
  display: flex;
  justify-content: flex-end;
  font-size: 0.75rem;
  line-height: 1rem;
  color: rgba(156, 163, 175, 1);
  margin: 8px 0 14px 0;
}

.forgot a {
  color: rgba(243, 244, 246, 1);
  text-decoration: none;
  font-size: 14px;
}

.forgot a:hover {
  text-decoration: underline rgba(167, 139, 250, 1);
}

.sign {
  display: block;
  width: 100%;
  background-color: rgba(167, 139, 250, 1);
  padding: 0.75rem;
  text-align: center;
  color: rgba(17, 24, 39, 1);
  border: none;
  border-radius: 0.375rem;
  font-weight: 600;
  font-size: 0.9rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.sign:hover {
  background-color: rgba(139, 92, 246, 1);
}

.sign:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
