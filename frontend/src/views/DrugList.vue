<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue';
import { getDrugs, createDrug, updateDrug, deleteDrug } from '../api/drug';
import { getCategories } from '../api/category';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { Search, Refresh, Plus, Delete, Edit } from '@element-plus/icons-vue';

const tableData = ref<any[]>([]);
const categories = ref<any[]>([]);
const loading = ref(false);
const dialogVisible = ref(false);
const dialogTitle = ref('新增药品');
const formRef = ref<FormInstance>();

const searchKeyword = ref('');

const form = reactive({
  id: undefined,
  name: '',
  categoryId: undefined,
  specification: '',
  unit: '',
  price: 0,
  stock: 0,
  manufacturer: '',
  productionDate: '',
  expiryDate: '',
  status: 1, // 1: 上架, 0: 下架
});

const rules = reactive<FormRules>({
  name: [{ required: true, message: '请输入药品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入单价', trigger: 'blur' }],
});

const fetchCategories = async () => {
  const res: any = await getCategories();
  categories.value = res.data || res;
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res: any = await getDrugs({ keyword: searchKeyword.value });
    tableData.value = res.data || res;
  } catch (error) {
    // handled
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  fetchData();
};

const handleReset = () => {
  searchKeyword.value = '';
  fetchData();
};

const handleAdd = () => {
  dialogTitle.value = '新增药品';
  Object.assign(form, {
    id: undefined,
    name: '',
    categoryId: undefined,
    specification: '',
    unit: '',
    price: 0,
    stock: 0,
    manufacturer: '',
    productionDate: '',
    expiryDate: '',
    status: 1,
  });
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑药品';
  Object.assign(form, row);
  dialogVisible.value = true;
};

const handleDelete = async (id: number) => {
  try {
    await deleteDrug(id);
    ElMessage.success('删除成功');
    fetchData();
  } catch (error) {
    // handled
  }
};

const handleSubmit = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await updateDrug(form.id, form);
          ElMessage.success('更新成功');
        } else {
          await createDrug(form);
          ElMessage.success('创建成功');
        }
        dialogVisible.value = false;
        fetchData();
      } catch (error) {
        // handled
      }
    }
  });
};

onMounted(() => {
  fetchCategories();
  fetchData();
});
</script>

<template>
  <div class="drug-list page-container">
    <div class="table-card">
      <div class="card-header">
        <div class="left">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索药品名称..."
            class="search-input"
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button :icon="Refresh" circle @click="handleReset" />
        </div>
        <div class="right">
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增药品</el-button>
        </div>
      </div>

      <el-table :data="tableData" v-loading="loading" style="width: 100%" :header-cell-style="{ background: '#f9fafb', color: '#374151' }">
        <el-table-column prop="name" label="名称" min-width="120" />
        <el-table-column prop="categoryName" label="分类" width="100">
          <template #default="scope">
            <el-tag effect="plain" type="info">{{ scope.row.categoryName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="specification" label="规格" width="100" />
        <el-table-column prop="unit" label="单位" width="60" />
        <el-table-column prop="price" label="单价" width="100">
          <template #default="scope">
            ¥{{ scope.row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="90">
          <template #default="scope">
            <span :style="{ color: scope.row.stock < 10 ? '#ef4444' : 'inherit', fontWeight: scope.row.stock < 10 ? 'bold' : 'normal' }">
              {{ scope.row.stock }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="manufacturer" label="生产厂家" min-width="150" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" effect="dark" size="small">
              {{ scope.row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button link type="primary" :icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-popconfirm title="确定删除吗？" @confirm="handleDelete(scope.row.id)">
              <template #reference>
                <el-button link type="danger" :icon="Delete">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="custom-form">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="名称" prop="name">
              <el-input v-model="form.name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
                <el-option
                  v-for="item in categories"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="规格" prop="specification">
              <el-input v-model="form.specification" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="form.unit" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单价" prop="price">
              <el-input-number v-model="form.price" :precision="2" :step="0.1" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存" prop="stock">
              <el-input-number v-model="form.stock" :min="0" :precision="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="生产厂家" prop="manufacturer">
          <el-input v-model="form.manufacturer" />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="生产日期" prop="productionDate">
              <el-date-picker
                v-model="form.productionDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="有效期至" prop="expiryDate">
              <el-date-picker
                v-model="form.expiryDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1" border>上架销售</el-radio>
            <el-radio :label="0" border>下架停售</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit(formRef)">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.left {
  display: flex;
  gap: 12px;
}

.search-input {
  width: 240px;
}

.custom-form .el-input-number {
  width: 100%;
}
</style>
