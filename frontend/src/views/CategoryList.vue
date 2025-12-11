<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue';
import { getCategories, createCategory, updateCategory, deleteCategory } from '../api/category';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { Plus, Edit, Delete } from '@element-plus/icons-vue';

const tableData = ref([]);
const loading = ref(false);
const dialogVisible = ref(false);
const dialogTitle = ref('新增分类');
const formRef = ref<FormInstance>();

const form = reactive({
  id: undefined,
  name: '',
  description: '',
});

const rules = reactive<FormRules>({
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
});

const fetchData = async () => {
  loading.value = true;
  try {
    const res: any = await getCategories();
    tableData.value = res.data || res;
  } catch (error) {
    // handled
  } finally {
    loading.value = false;
  }
};

const handleAdd = () => {
  dialogTitle.value = '新增分类';
  form.id = undefined;
  form.name = '';
  form.description = '';
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑分类';
  form.id = row.id;
  form.name = row.name;
  form.description = row.description;
  dialogVisible.value = true;
};

const handleDelete = async (id: number) => {
  try {
    await deleteCategory(id);
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
          await updateCategory(form.id, form);
          ElMessage.success('更新成功');
        } else {
          await createCategory(form);
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
  fetchData();
});
</script>

<template>
  <div class="category-list page-container">
    <div class="table-card">
      <div class="card-header">
        <h3>分类列表</h3>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增分类</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" style="width: 100%" :header-cell-style="{ background: '#f9fafb', color: '#374151' }">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="分类名称" width="200">
          <template #default="scope">
            <span style="font-weight: 600">{{ scope.row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入分类描述" />
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

.card-header h3 {
  margin: 0;
  font-size: 18px;
  color: #374151;
}
</style>
