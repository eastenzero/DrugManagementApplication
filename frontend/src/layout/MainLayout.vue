<script setup lang="ts">
import { computed } from 'vue';
import { useUserStore } from '../store/user';
import { useRouter, useRoute } from 'vue-router';
import { ElMessageBox } from 'element-plus';
import { 
  Odometer, 
  Files, 
  FirstAidKit, 
  Download, 
  Upload, 
  PieChart, 
  SwitchButton,
  UserFilled,
  Expand,
  Fold
} from '@element-plus/icons-vue';
import { ref } from 'vue';

const userStore = useUserStore();
const router = useRouter();
const route = useRoute();
const isCollapse = ref(false);

const activeMenu = computed(() => route.path);

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    userStore.logout();
    router.push('/login');
  });
};

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value;
};
</script>

<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '260px'" class="aside glass-sidebar">
      <div class="logo-container">
        <div class="logo-icon"></div>
        <div class="logo-text" v-if="!isCollapse">
          <span>Medicine</span>
        </div>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        :collapse="isCollapse"
        background-color="transparent"
        text-color="#3c3c43"
        active-text-color="#007aff"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>仪表盘</template>
        </el-menu-item>
        
        <div class="menu-category" v-if="!isCollapse">管理模块</div>
        
        <el-menu-item index="/categories">
          <el-icon><Files /></el-icon>
          <template #title>药品分类</template>
        </el-menu-item>
        <el-menu-item index="/drugs">
          <el-icon><FirstAidKit /></el-icon>
          <template #title>药品信息</template>
        </el-menu-item>
        
        <div class="menu-category" v-if="!isCollapse">库存操作</div>
        
        <el-menu-item index="/stock-in">
          <el-icon><Download /></el-icon>
          <template #title>入库管理</template>
        </el-menu-item>
        <el-menu-item index="/stock-out">
          <el-icon><Upload /></el-icon>
          <template #title>出库管理</template>
        </el-menu-item>
        <el-menu-item index="/inventory">
          <el-icon><PieChart /></el-icon>
          <template #title>库存总览</template>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container class="main-wrapper">
      <el-header class="header glass-header">
        <div class="header-left">
          <el-icon class="trigger" @click="toggleCollapse">
            <component :is="isCollapse ? Expand : Fold" />
          </el-icon>
          <span class="page-title">{{ route.name }}</span>
        </div>
        
        <div class="header-right">
          <div class="user-profile">
            <el-avatar :size="32" :icon="UserFilled" class="avatar" />
            <span class="username">{{ userStore.username }}</span>
          </div>
          <el-button type="danger" circle plain size="small" @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>
          </el-button>
        </div>
      </el-header>
      
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout-container {
  height: 100vh;
}

.glass-sidebar {
  background: rgba(255, 255, 255, 0.65);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-right: 1px solid rgba(255, 255, 255, 0.4);
  transition: width 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  display: flex;
  flex-direction: column;
  overflow-x: hidden;
  z-index: 100;
}

.logo-container {
  height: 80px;
  display: flex;
  align-items: center;
  padding-left: 24px;
  gap: 12px;
}

.logo-icon {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #007aff, #5ac8fa);
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0, 122, 255, 0.3);
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: #1c1c1e;
  letter-spacing: -0.5px;
}

.el-menu-vertical {
  border-right: none;
  background: transparent;
}

.el-menu-item {
  margin: 4px 16px;
  border-radius: 10px;
  height: 44px;
  line-height: 44px;
  color: #3c3c43;
}

.el-menu-item:hover {
  background-color: rgba(0, 0, 0, 0.05) !important;
}

.el-menu-item.is-active {
  background-color: rgba(0, 122, 255, 0.1) !important;
  color: #007aff !important;
  font-weight: 600;
}

.menu-category {
  padding: 24px 24px 8px;
  font-size: 11px;
  font-weight: 600;
  color: #8e8e93;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.main-wrapper {
  display: flex;
  flex-direction: column;
  background: transparent; /* Let body background show through */
}

.glass-header {
  background: rgba(255, 255, 255, 0.65);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.4);
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  z-index: 99;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.trigger {
  font-size: 20px;
  cursor: pointer;
  color: #1c1c1e;
}

.page-title {
  font-size: 17px;
  font-weight: 600;
  color: #1c1c1e;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.4);
}

.username {
  font-size: 14px;
  color: #1c1c1e;
  font-weight: 500;
}

.avatar {
  background: #e5e5ea;
  color: #8e8e93;
}

.main-content {
  padding: 24px;
  overflow-y: auto;
}

/* Transition */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: scale(0.98);
}
</style>
