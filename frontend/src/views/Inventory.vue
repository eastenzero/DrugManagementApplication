<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { getDrugs } from '../api/drug';
import { getCategories } from '../api/category';

const tableData = ref<any[]>([]);
const categories = ref<any[]>([]);
const loading = ref(false);
const filterCategory = ref<number | undefined>(undefined);

const fetchData = async () => {
  loading.value = true;
  try {
    const res: any = await getDrugs();
    tableData.value = res.data || res;
  } catch (error) {
    // handled
  } finally {
    loading.value = false;
  }
};

const fetchCategories = async () => {
  const res: any = await getCategories();
  categories.value = res.data || res;
};

const filteredData = computed(() => {
  if (!filterCategory.value) return tableData.value;
  return tableData.value.filter((item) => item.categoryId === filterCategory.value);
});

const getRowClassName = ({ row }: { row: any }) => {
  if (row.stock < 10) {
    return 'warning-row';
  }
  return '';
};

onMounted(() => {
  fetchData();
  fetchCategories();
});
</script>

<template>
  <div class="inventory">
    <div class="toolbar">
      <el-select v-model="filterCategory" placeholder="按分类筛选" clearable style="width: 200px">
        <el-option
          v-for="item in categories"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
      <el-button icon="Refresh" @click="fetchData" style="margin-left: 10px">刷新</el-button>
    </div>

    <el-table
      :data="filteredData"
      v-loading="loading"
      style="width: 100%"
      border
      :row-class-name="getRowClassName"
    >
      <el-table-column prop="name" label="药品名称" />
      <el-table-column prop="categoryName" label="分类" />
      <el-table-column prop="specification" label="规格" />
      <el-table-column prop="unit" label="单位" width="80" />
      <el-table-column prop="stock" label="当前库存" width="100">
        <template #default="scope">
          <span :class="{ 'low-stock': scope.row.stock < 10 }">
            {{ scope.row.stock }}
            <el-tag v-if="scope.row.stock < 10" type="danger" size="small" effect="dark">
              库存不足
            </el-tag>
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
            {{ scope.row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style>
.el-table .warning-row {
  --el-table-tr-bg-color: var(--el-color-warning-light-9);
}
</style>

<style scoped>
.toolbar {
  margin-bottom: 20px;
}
.low-stock {
  color: red;
  font-weight: bold;
}
</style>
