<template>
  <div class="order-list">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.keyword" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="待支付" :value="0" />
            <el-option label="充电中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column label="序号" width="70" type="index" :index="(page.current - 1) * page.size + 1" />
        <el-table-column prop="orderNo" label="订单号" min-width="180" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="stationId" label="站点ID" width="80" />
        <el-table-column prop="chargerId" label="充电桩ID" width="90" />
        <el-table-column prop="electricity" label="电量(kWh)" width="100" />
        <el-table-column prop="totalAmount" label="总金额(元)" width="100" />
        <el-table-column prop="payAmount" label="实付(元)" width="90" />
        <el-table-column prop="duration" label="时长(分)" width="90" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type">
              {{ statusMap[row.status]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="170" />
        <el-table-column prop="endTime" label="结束时间" width="170" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
      </el-table>
      <el-pagination
          v-model:current-page="page.current"
          v-model:page-size="page.size"
          :page-sizes="[10, 20, 50]"
          :total="page.total"
          layout="sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
      />
    </el-card>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getOrderList } from '@/api/admin_order'

const loading = ref(false)
const tableData = ref([])
const searchForm = reactive({ keyword: '', status: '' })
const page = reactive({ current: 1, size: 10, total: 0 })
const statusMap = {
  0: { label: '待支付', type: 'warning' },
  1: { label: '充电中', type: 'primary' },
  2: { label: '已完成', type: 'success' },
  3: { label: '已取消', type: 'info' }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getOrderList({ pageNum: page.current, pageSize: page.size, ...searchForm })
    tableData.value = res.data.records
    page.total = res.data.total
  } finally {
    loading.value = false
  }
}
const handleSearch = () => { page.current = 1; loadData() }
const resetSearch = () => { searchForm.keyword = ''; searchForm.status = ''; handleSearch() }
onMounted(() => loadData())
</script>
<style scoped>
.search-card { margin-bottom: 16px; }
</style>
