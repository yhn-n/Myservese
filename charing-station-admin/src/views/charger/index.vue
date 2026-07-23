<template>
  <div class="charger-manage">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="站点ID">
          <el-input v-model="searchForm.stationId" placeholder="站点ID" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="离线" :value="0" />
            <el-option label="空闲" :value="1" />
            <el-option label="充电中" :value="2" />
            <el-option label="故障" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button type="success" @click="handleAdd">新增充电桩</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column label="序号" width="70" type="index" :index="(page.current - 1) * page.size + 1" />
        <el-table-column prop="code" label="充电桩编号" min-width="130" />
        <el-table-column prop="name" label="名称" min-width="120" />
        <el-table-column prop="stationId" label="站点ID" width="80" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? 'primary' : 'success'">
              {{ row.type === 1 ? '直流快充' : '交流慢充' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="power" label="功率(kW)" width="90" />
        <el-table-column prop="gunCount" label="枪数" width="70" />
        <el-table-column prop="price" label="单价(元)" width="90" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="chargerStatusMap[row.status]?.type">
              {{ chargerStatusMap[row.status]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link @click="handleQrcode(row)">二维码</el-button>
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
    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="充电桩编号">
          <el-input v-model="form.code" placeholder="留空自动生成" />
        </el-form-item>
        <el-form-item label="名称">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="所属站点">
          <el-select v-model="form.stationId" placeholder="请选择站点" style="width: 100%">
            <el-option
              v-for="item in stationOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="form.type">
            <el-radio :label="1">直流快充</el-radio>
            <el-radio :label="2">交流慢充</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="功率(kW)">
          <el-input-number v-model="form.power" :min="0" :precision="1" />
        </el-form-item>
        <el-form-item label="枪数">
          <el-input-number v-model="form.gunCount" :min="1" />
        </el-form-item>
        <el-form-item label="单价(元)">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="离线" :value="0" />
            <el-option label="空闲" :value="1" />
            <el-option label="充电中" :value="2" />
            <el-option label="故障" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    <!-- 二维码对话框 -->
    <el-dialog v-model="qrcodeVisible" title="充电桩二维码" width="400px" align-center>
      <div class="qrcode-dialog-body">
        <p class="qrcode-info">编号: {{ qrcodeInfo.code }}</p>
        <p class="qrcode-tip">用户扫描此二维码即可开始充电</p>
        <img v-if="qrcodeImage" :src="qrcodeImage" class="qrcode-img" />
        <el-button type="primary" class="qrcode-download-btn" @click="downloadQrcode">下载二维码</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getChargerList, addCharger, updateCharger, deleteCharger, getChargerQrcode } from '@/api/charger'
import { getAllStations } from '@/api/station'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const searchForm = reactive({ stationId: '', status: '' })
const page = reactive({ current: 1, size: 10, total: 0 })
const form = reactive({ id: null, code: '', name: '', stationId: 1, type: 1, power: 60, gunCount: 1, price: 1.2, status: 1 })
const chargerStatusMap = {
  0: { label: '离线', type: 'info' },
  1: { label: '空闲', type: 'success' },
  2: { label: '充电中', type: 'primary' },
  3: { label: '故障', type: 'danger' }
}

const stationOptions = ref([])

const qrcodeVisible = ref(false)
const qrcodeImage = ref('')
const qrcodeInfo = reactive({ id: null, code: '' })

const loadData = async () => {
  loading.value = true
  try {
    const params = { pageNum: page.current, pageSize: page.size }
    if (searchForm.stationId) params.stationId = searchForm.stationId
    if (searchForm.status !== '') params.status = searchForm.status
    const res = await getChargerList(params)
    tableData.value = res.data.records
    page.total = res.data.total
  } finally {
    loading.value = false
  }
}
const handleSearch = () => { page.current = 1; loadData() }
const resetSearch = () => { searchForm.stationId = ''; searchForm.status = ''; handleSearch() }
const handleAdd = () => {
  dialogTitle.value = '新增充电桩'
  const defaultStationId = stationOptions.value.length > 0 ? stationOptions.value[0].id : null
  Object.assign(form, { id: null, code: '', name: '', stationId: defaultStationId, type: 1, power: 60, gunCount: 1, price: 1.2, status: 1 })
  dialogVisible.value = true
}
const handleEdit = (row) => { dialogTitle.value = '编辑充电桩'; Object.assign(form, row); dialogVisible.value = true }
const handleSubmit = async () => {
  if (!form.stationId) {
    ElMessage.warning('请选择所属站点')
    return
  }
  if (form.id) { await updateCharger(form) } else { await addCharger(form) }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  loadData()
}
const handleDelete = async (id) => {
  await deleteCharger(id)
  ElMessage.success('删除成功')
  loadData()
}
const handleQrcode = async (row) => {
  qrcodeInfo.id = row.id
  qrcodeInfo.code = row.code
  qrcodeImage.value = ''
  qrcodeVisible.value = true
  try {
    const res = await getChargerQrcode(row.id)
    qrcodeImage.value = res.data
  } catch (e) {
    ElMessage.error('获取二维码失败')
  }
}
const downloadQrcode = () => {
  if (!qrcodeImage.value) return
  const link = document.createElement('a')
  link.href = qrcodeImage.value
  link.download = 'QR_' + qrcodeInfo.code + '.png'
  link.click()
}
const loadStations = async () => {
  try {
    const res = await getAllStations()
    stationOptions.value = res.data || []
  } catch (e) {
    console.error('加载站点失败', e)
  }
}

onMounted(() => {
  loadData()
  loadStations()
})
</script>
<style scoped>
.search-card { margin-bottom: 16px; }
.qrcode-dialog-body {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px 20px;
}
.qrcode-info {
  margin: 0 0 8px;
  color: #666;
  text-align: center;
}
.qrcode-tip {
  margin: 0 0 16px;
  color: #999;
  font-size: 13px;
  text-align: center;
}
.qrcode-img {
  width: 256px;
  height: 256px;
  display: block;
}
.qrcode-download-btn {
  margin-top: 16px;
}
</style>
