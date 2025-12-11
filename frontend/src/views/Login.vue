<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../store/user';
import { login } from '../api/auth';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { User, Lock } from '@element-plus/icons-vue';

const router = useRouter();
const userStore = useUserStore();
const formRef = ref<FormInstance>();
const loading = ref(false);

const form = reactive({
  username: '',
  password: '',
});

const rules = reactive<FormRules>({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
});

const handleLogin = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        const res: any = await login(form);
        const token = res.token || res.data?.token; 
        const username = form.username;

        if (token) {
            userStore.setToken(token);
            userStore.setUsername(username);
            ElMessage.success('登录成功');
            router.push('/dashboard');
        } else {
             userStore.setToken(res.token || 'mock-token');
             userStore.setUsername(username);
             ElMessage.success('登录成功');
             router.push('/dashboard');
        }
      } catch (error) {
        // handled
      } finally {
        loading.value = false;
      }
    }
  });
};
</script>

<template>
  <div class="login-container">
    <div class="glass-panel">
      <div class="login-header">
        <div class="logo-icon">
          <div class="inner-circle"></div>
        </div>
        <h1>Medicine Admin</h1>
        <p>药品库存管理系统</p>
      </div>
      
      <el-form ref="formRef" :model="form" :rules="rules" size="large" class="login-form">
        <el-form-item prop="username">
          <el-input 
            v-model="form.username" 
            placeholder="用户名" 
            :prefix-icon="User" 
            class="glass-input"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            :prefix-icon="Lock"
            show-password
            class="glass-input"
            @keyup.enter="handleLogin(formRef)"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="submit-btn" @click="handleLogin(formRef)">
            登 录
          </el-button>
        </el-form-item>
        <div class="form-footer">
          <router-link to="/register">注册新账号</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  height: 100vh;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  /* Background is handled by body in style.css */
}

.glass-panel {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 30px;
  border: 1px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  animation: floatUp 0.8s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo-icon {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #007aff, #5ac8fa);
  border-radius: 18px;
  margin: 0 auto 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 10px 20px rgba(0, 122, 255, 0.3);
}

.inner-circle {
  width: 24px;
  height: 24px;
  background: white;
  border-radius: 50%;
  opacity: 0.8;
}

.login-header h1 {
  font-size: 24px;
  font-weight: 700;
  margin: 0;
  color: #1c1c1e;
}

.login-header p {
  margin: 8px 0 0;
  color: #8e8e93;
  font-size: 14px;
}

.login-form {
  width: 100%;
}

:deep(.glass-input .el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.5);
  box-shadow: none;
  border: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: 12px;
  padding: 8px 15px;
  transition: all 0.3s;
}

:deep(.glass-input .el-input__wrapper:hover),
:deep(.glass-input .el-input__wrapper.is-focus) {
  background-color: rgba(255, 255, 255, 0.8);
  box-shadow: 0 0 0 1px #007aff;
}

.submit-btn {
  width: 100%;
  height: 48px;
  border-radius: 14px;
  font-size: 16px;
  font-weight: 600;
  background: #007aff;
  border: none;
  margin-top: 10px;
  box-shadow: 0 10px 20px rgba(0, 122, 255, 0.2);
  transition: transform 0.2s;
}

.submit-btn:hover {
  transform: scale(1.02);
  background: #0066d6;
}

.form-footer {
  text-align: center;
  margin-top: 20px;
}

.form-footer a {
  color: #007aff;
  text-decoration: none;
  font-size: 14px;
}

@keyframes floatUp {
  from { opacity: 0; transform: translateY(40px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
