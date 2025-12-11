<script setup lang="ts">
import { useUserStore } from '../store/user';
import { ref, onMounted } from 'vue';
import { getDrugs } from '../api/drug';
import { getCategories } from '../api/category';
import { 
  FirstAidKit, 
  Warning, 
  Money, 
  Files,
  Plus,
  Minus,
  Search
} from '@element-plus/icons-vue';

const userStore = useUserStore();
const totalDrugs = ref(0);
const lowStockCount = ref(0);
const totalValue = ref(0);
const categoryCount = ref(0);

const initStats = async () => {
  try {
    const drugsRes: any = await getDrugs();
    const drugs = drugsRes.data || drugsRes;
    totalDrugs.value = drugs.length;
    lowStockCount.value = drugs.filter((d: any) => d.stock < 10).length;
    totalValue.value = drugs.reduce((acc: number, curr: any) => acc + (curr.price * curr.stock), 0);
    
    const catRes: any = await getCategories();
    const cats = catRes.data || catRes;
    categoryCount.value = cats.length;
  } catch (e) {
    console.error(e);
  }
};

onMounted(() => {
  initStats();
});
</script>

<template>
  <div class="dashboard page-container">
    <div class="welcome-section">
      <h1>Good Morning, {{ userStore.username }}</h1>
      <p>今日概览</p>
    </div>

    <div class="stats-grid">
      <div class="stat-card glass-card">
        <div class="icon-wrapper blue">
          <el-icon><FirstAidKit /></el-icon>
        </div>
        <div class="stat-info">
          <span class="label">药品总数</span>
          <span class="value">{{ totalDrugs }}</span>
        </div>
      </div>

      <div class="stat-card glass-card">
        <div class="icon-wrapper red">
          <el-icon><Warning /></el-icon>
        </div>
        <div class="stat-info">
          <span class="label">库存预警</span>
          <span class="value">{{ lowStockCount }}</span>
        </div>
      </div>

      <div class="stat-card glass-card">
        <div class="icon-wrapper green">
          <el-icon><Money /></el-icon>
        </div>
        <div class="stat-info">
          <span class="label">库存总值</span>
          <span class="value">¥{{ totalValue.toFixed(2) }}</span>
        </div>
      </div>

      <div class="stat-card glass-card">
        <div class="icon-wrapper purple">
          <el-icon><Files /></el-icon>
        </div>
        <div class="stat-info">
          <span class="label">药品分类</span>
          <span class="value">{{ categoryCount }}</span>
        </div>
      </div>
    </div>

    <div class="dashboard-content">
      <div class="content-card glass-card">
        <h3>快速操作</h3>
        <div class="quick-actions">
          <div class="action-btn" @click="$router.push('/stock-in')">
            <div class="btn-icon blue-bg"><el-icon><Plus /></el-icon></div>
            <span>入库</span>
          </div>
          <div class="action-btn" @click="$router.push('/stock-out')">
            <div class="btn-icon orange-bg"><el-icon><Minus /></el-icon></div>
            <span>出库</span>
          </div>
          <div class="action-btn" @click="$router.push('/drugs')">
            <div class="btn-icon gray-bg"><el-icon><Search /></el-icon></div>
            <span>查询</span>
          </div>
        </div>
      </div>
      
      <div class="content-card glass-card">
        <h3>系统公告</h3>
        <div class="empty-state">
          <p>暂无新公告</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.welcome-section {
  margin-bottom: 30px;
}

.welcome-section h1 {
  font-size: 34px;
  font-weight: 700;
  color: #1c1c1e;
  margin: 0 0 4px 0;
  letter-spacing: -0.5px;
}

.welcome-section p {
  color: #8e8e93;
  font-size: 17px;
  margin: 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  transition: transform 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.stat-card:hover {
  transform: scale(1.02);
}

.icon-wrapper {
  width: 50px;
  height: 50px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.blue { background: #007aff; box-shadow: 0 8px 16px rgba(0, 122, 255, 0.25); }
.red { background: #ff3b30; box-shadow: 0 8px 16px rgba(255, 59, 48, 0.25); }
.green { background: #34c759; box-shadow: 0 8px 16px rgba(52, 199, 89, 0.25); }
.purple { background: #af52de; box-shadow: 0 8px 16px rgba(175, 82, 222, 0.25); }

.stat-info {
  display: flex;
  flex-direction: column;
}

.label {
  font-size: 13px;
  font-weight: 600;
  color: #8e8e93;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 4px;
}

.value {
  font-size: 28px;
  font-weight: 700;
  color: #1c1c1e;
  letter-spacing: -0.5px;
}

.dashboard-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
}

.content-card {
  padding: 24px;
  min-height: 200px;
}

.content-card h3 {
  margin: 0 0 24px 0;
  font-size: 17px;
  font-weight: 600;
  color: #1c1c1e;
}

.quick-actions {
  display: flex;
  gap: 30px;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: transform 0.2s;
}

.action-btn:hover {
  transform: translateY(-2px);
}

.btn-icon {
  width: 60px;
  height: 60px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.blue-bg { background: linear-gradient(135deg, #007aff, #5ac8fa); }
.orange-bg { background: linear-gradient(135deg, #ff9500, #ffcc00); }
.gray-bg { background: linear-gradient(135deg, #8e8e93, #aeaeb2); }

.action-btn span {
  font-size: 13px;
  font-weight: 500;
  color: #3c3c43;
}

.empty-state {
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #8e8e93;
  font-size: 15px;
}

@media (max-width: 1024px) {
  .dashboard-content {
    grid-template-columns: 1fr;
  }
}
</style>
