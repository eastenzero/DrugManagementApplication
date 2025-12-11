<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { register } from '../api/auth';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { User, Lock, Check } from '@element-plus/icons-vue';

const router = useRouter();
const formRef = ref<FormInstance>();
const loading = ref(false);

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
});

const validatePass2 = (_rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'));
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致!'));
  } else {
    callback();
  }
};

const rules = reactive<FormRules>({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [{ validator: validatePass2, trigger: 'blur', required: true }],
});

const handleRegister = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        await register({ username: form.username, password: form.password });
        ElMessage.success('注册成功，请登录');
        router.push('/login');
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
  <div class="register-container">
    <div class="glass-panel">
      <div class="register-header">
        <div class="logo-icon">
          <div class="inner-circle"></div>
        </div>
        <h1>Create Account</h1>
        <p>注册您的 Medicine Admin 账号</p>
      </div>
      
      <el-form ref="formRef" :model="form" :rules="rules" size="large" class="register-form">
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
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="确认密码"
            :prefix-icon="Check"
            show-password
            class="glass-input"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="submit-btn" @click="handleRegister(formRef)">
            注 册
          </el-button>
        </el-form-item>
        <div class="form-footer">
          <router-link to="/login">已有账号？去登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.register-container {
  height: 100vh;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
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

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo-icon {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #34c759, #30d158);
  border-radius: 18px;
  margin: 0 auto 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 10px 20px rgba(52, 199, 89, 0.3);
}

.inner-circle {
  width: 24px;
  height: 24px;
  background: white;
  border-radius: 50%;
  opacity: 0.8;
}

.register-header h1 {
  font-size: 24px;
  font-weight: 700;
  margin: 0;
  color: #1c1c1e;
}

.register-header p {
  margin: 8px 0 0;
  color: #8e8e93;
  font-size: 14px;
}

.register-form {
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
  box-shadow: 0 0 0 1px #34c759;
}

.submit-btn {
  width: 100%;
  height: 48px;
  border-radius: 14px;
  font-size: 16px;
  font-weight: 600;
  background: #34c759;
  border: none;
  margin-top: 10px;
  box-shadow: 0 10px 20px rgba(52, 199, 89, 0.2);
  transition: transform 0.2s;
}

.submit-btn:hover {
  transform: scale(1.02);
  background: #2dbd50;
}

.form-footer {
  text-align: center;
  margin-top: 20px;
}

.form-footer a {
  color: #34c759;
  text-decoration: none;
  font-size: 14px;
}

@keyframes floatUp {
  from { opacity: 0; transform: translateY(40px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
