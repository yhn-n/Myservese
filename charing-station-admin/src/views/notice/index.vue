<template>
  <div class="notice-manage">
    <el-card class="search-card">
      <el-form :inline="true">
        <el-form-item>
          <el-button type="success" @click="handleAdd">新增公告</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column label="序号" width="70" type="index" :index="(page.current - 1) * page.size + 1" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已发布' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">发布</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
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
import { getNoticeList, addNotice, updateNotice, deleteNotice } from '@/api/notice'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const page = reactive({ current: 1, size: 10, total: 0 })
const form = reactive({ id: null, title: '', content: '', status: 1 })

const loadData = async () => {
  loading.value = true
  try {
    const res = await getNoticeList({ pageNum: page.current, pageSize: page.size })
    tableData.value = res.data.records
    page.total = res.data.total
  } finally {
    loading.value = false
  }
}
const handleAdd = () => {
  dialogTitle.value = '新增公告'
  Object.assign(form, { id: null, title: '', content: '', status: 1 })
  dialogVisible.value = true
}
const handleEdit = (row) => { dialogTitle.value = '编辑公告'; Object.assign(form, row); dialogVisible.value = true }
const handleSubmit = async () => {
  if (form.id) { await updateNotice(form) } else { await addNotice(form) }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  loadData()
}
const handleDelete = async (id) => {
  await deleteNotice(id)
  ElMessage.success('删除成功')
  loadData()
}
onMounted(() => loadData())
</script>
<style scoped>
.search-card { margin-bottom: 16px; }
</style>
