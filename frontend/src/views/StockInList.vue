<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue';
import { getStockIn, createStockIn } from '../api/stock';
import { getDrugs } from '../api/drug';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { useUserStore } from '../store/user';
import { Plus } from '@element-plus/icons-vue';

const userStore = useUserStore();
const tableData = ref<any[]>([]);
const drugs = ref<any[]>([]);
const loading = ref(false);
const dialogVisible = ref(false);
const formRef = ref<FormInstance>();

const form = reactive({
  drugId: undefined,
  quantity: 1,
  price: 0,
  supplier: '',
  batchNumber: '',
  remark: '',
  operator: userStore.username,
});

const rules = reactive<FormRules>({
  drugId: [{ required: true, message: '请选择药品', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
});

const fetchDrugs = async () => {
  const res: any = await getDrugs();
  drugs.value = res.data || res;
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res: any = await getStockIn();
    tableData.value = res.data || res;
  } catch (error) {
    // handled
  } finally {
    loading.value = false;
  }
};

const handleAdd = () => {
  Object.assign(form, {
    drugId: undefined,
    quantity: 1,
    price: 0,
    supplier: '',
    batchNumber: '',
    remark: '',
    operator: userStore.username,
  });
  dialogVisible.value = true;
};

const handleSubmit = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate(async (valid) => {
    if (valid) {
      try {
        await createStockIn(form);
        ElMessage.success('入库成功');
        dialogVisible.value = false;
        fetchData();
        fetchDrugs();
      } catch (error) {
        // handled
      }
    }
  });
};

onMounted(() => {
  fetchDrugs();
  fetchData();
});
</script>

<template>
  <div class="stock-in-list page-container">
    <div class="table-card">
      <div class="card-header">
        <h3>入库记录</h3>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增入库</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" style="width: 100%" :header-cell-style="{ background: '#f9fafb', color: '#374151' }">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="drugName" label="药品名称" min-width="120" />
        <el-table-column prop="quantity" label="数量" width="100">
          <template #default="scope">
            <span style="color: #10b981; font-weight: bold">+{{ scope.row.quantity }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="单价" width="100">
          <template #default="scope">¥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column prop="supplier" label="供应商" min-width="150" />
        <el-table-column prop="batchNumber" label="批号" width="120" />
        <el-table-column prop="createTime" label="入库时间" width="180" />
        <el-table-column prop="operator" label="操作人" width="100">
          <template #default="scope">
            <el-tag size="small">{{ scope.row.operator }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" title="新增入库" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="药品" prop="drugId">
          <el-select v-model="form.drugId" filterable placeholder="请选择药品" style="width: 100%">
            <el-option
              v-for="item in drugs"
              :key="item.id"
              :label="item.name + ' (当前库存: ' + item.stock + ')'"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="单价" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" :step="0.1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="供应商" prop="supplier">
          <el-input v-model="form.supplier" />
        </el-form-item>
        <el-form-item label="批号" prop="batchNumber">
          <el-input v-model="form.batchNumber" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
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
