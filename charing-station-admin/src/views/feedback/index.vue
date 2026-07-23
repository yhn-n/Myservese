<template>
  <div class="feedback-manage">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="待处理" :value="0" />
            <el-option label="处理中" :value="1" />
            <el-option label="已解决" :value="2" />
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
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="stationId" label="站点ID" width="80" />
        <el-table-column prop="content" label="反馈内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="feedbackStatusMap[row.status]?.type">
              {{ feedbackStatusMap[row.status]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="反馈时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleReply(row)">回复</el-button>
            <el-popconfirm title="确定删除吗？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
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
    <el-dialog v-model="dialogVisible" title="回复反馈" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="处理中" :value="1" />
            <el-option label="已解决" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="回复内容">
          <el-input v-model="form.reply" type="textarea" :rows="4" placeholder="请输入回复内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getFeedbackList, updateFeedback, deleteFeedback } from '@/api/feedback'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const searchForm = reactive({ status: '' })
const page = reactive({ current: 1, size: 10, total: 0 })
const form = reactive({ id: null, status: 1, reply: '' })
const feedbackStatusMap = {
  0: { label: '待处理', type: 'warning' },
  1: { label: '处理中', type: 'primary' },
  2: { label: '已解决', type: 'success' }
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { pageNum: page.current, pageSize: page.size }
    if (searchForm.status !== '') params.status = searchForm.status
    const res = await getFeedbackList(params)
    tableData.value = res.data.records
    page.total = res.data.total
  } finally {
    loading.value = false
  }
}
const handleSearch = () => { page.current = 1; loadData() }
const resetSearch = () => { searchForm.status = ''; handleSearch() }
const handleReply = (row) => { Object.assign(form, { id: row.id, status: row.status || 1, reply: row.reply || '' }); dialogVisible.value = true }
const handleSubmit = async () => {
  await updateFeedback(form)
  ElMessage.success('回复成功')
  dialogVisible.value = false
  loadData()
}
const handleDelete = async (id) => {
  await deleteFeedback(id)
  ElMessage.success('删除成功')
  loadData()
}
onMounted(() => loadData())
</script>
<style scoped>
.search-card { margin-bottom: 16px; }
</style>
