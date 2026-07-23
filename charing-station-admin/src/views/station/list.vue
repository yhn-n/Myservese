<template>
  <div class="station-list">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="站点名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入站点名称" clearable />
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="searchForm.city" placeholder="请输入城市" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="营业" :value="1" />
            <el-option label="停业" :value="0" />
            <el-option label="维护中" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button type="success" @click="handleAdd">新增站点</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <!-- 数据表格 -->
    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column label="站点序号" width="80" type="index" :index="(page.current - 1) * page.size + 1" />
        <el-table-column prop="name" label="站点名称" min-width="150" />
        <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="city" label="城市" width="100" />
        <el-table-column prop="totalPorts" label="总端口数" width="100" />
        <el-table-column prop="availablePorts" label="可用端口" width="100" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 0 ? 'danger' : 'warning'">
              {{ row.status === 1 ? '营业' : row.status === 0 ? '停业' : '维护中' }}
            </el-tag>
          </template>
        </el-table-column>
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
          :page-sizes="[10, 20, 50, 100]"
          :total="page.total"
          layout="sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
      />
    </el-card>
    <!-- 新增/编辑对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="dialogTitle"
        width="780px"
        @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="站点名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入站点名称" />
            </el-form-item>
            <el-form-item label="详细地址" prop="address">
              <el-input v-model="form.address" placeholder="点击右侧地图自动定位" />
            </el-form-item>
            <el-form-item label="城市" prop="city">
              <el-input v-model="form.city" placeholder="自动填充" />
            </el-form-item>
            <el-form-item label="省份">
              <el-input v-model="form.province" placeholder="自动填充" />
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
            <el-form-item label="总端口数">
              <el-input-number v-model="form.totalPorts" :min="0" />
            </el-form-item>
            <el-form-item label="可用端口">
              <el-input-number v-model="form.availablePorts" :min="0" />
            </el-form-item>
            <el-form-item label="经度">
              <el-input v-model="form.longitude" readonly />
            </el-form-item>
            <el-form-item label="纬度">
              <el-input v-model="form.latitude" readonly />
            </el-form-item>
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio :label="1">营业</el-radio>
                <el-radio :label="0">停业</el-radio>
                <el-radio :label="2">维护中</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <div class="map-picker-label">搜索地址 / 点击地图选取位置</div>
            <div class="map-search-box">
              <el-input v-model="mapSearchKeyword" placeholder="输入地址搜索定位，如：武汉市洪山区"
                        clearable @keyup.enter="handleMapSearch">
                <template #append>
                  <el-button @click="handleMapSearch">
                    <el-icon><Search /></el-icon>
                  </el-button>
                </template>
              </el-input>
            </div>
            <div id="station-picker-map" class="station-picker-map"></div>
            <div class="map-picker-tip">点击地图或搜索地址后自动填充详细地址、城市、经纬度</div>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, onUnmounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getStationList, addStation, updateStation, deleteStation } from '@/api/station'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const searchForm = reactive({ keyword: '', city: '', status: '' })
const page = reactive({ current: 1, size: 10, total: 0 })

const form = reactive({
  id: null, name: '', address: '', city: '', province: '', phone: '',
  totalPorts: 0, availablePorts: 0,
  longitude: null, latitude: null, status: 1
})

const formRules = {
  name: [{ required: true, message: '请输入站点名称', trigger: 'blur' }],
  address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }]
}

const validateLocation = () => {
  if (!form.longitude || !form.latitude) {
    ElMessage.warning('请在右侧地图上点击选取位置')
    return false
  }
  return true
}

let pickerMap = null
let pickerMarker = null
const mapSearchKeyword = ref('')
let geocoder = null

const initPickerMap = async () => {
  await nextTick()
  const el = document.getElementById('station-picker-map')
  if (!el) return
  if (pickerMap) { pickerMap.destroy(); pickerMap = null; pickerMarker = null }

  pickerMap = new AMap.Map('station-picker-map', {
    zoom: 12,
    center: form.longitude && form.latitude
      ? [parseFloat(form.longitude), parseFloat(form.latitude)]
      : [114.305, 30.593]
  })

  geocoder = new AMap.Geocoder()

  if (form.longitude && form.latitude) {
    addPickerMarker([parseFloat(form.longitude), parseFloat(form.latitude)])
  }

  pickerMap.on('click', async (e) => {
    const lnglat = e.lnglat
    addPickerMarker([lnglat.lng, lnglat.lat])
    form.longitude = parseFloat(lnglat.lng.toFixed(7))
    form.latitude = parseFloat(lnglat.lat.toFixed(7))
    reverseGeocode(lnglat)
  })
}

const handleMapSearch = async () => {
  const keyword = mapSearchKeyword.value.trim()
  if (!keyword) { ElMessage.warning('请输入地址'); return }

  try {
    const res = await fetch(`/api/map/geocode?address=${encodeURIComponent(keyword)}&output=json`)
    const data = await res.json()
    if (data.status === '1' && data.geocodes && data.geocodes.length > 0) {
      const geo = data.geocodes[0]
      const [lng, lat] = geo.location.split(',').map(Number)
      pickerMap.setZoomAndCenter(15, [lng, lat], false, 500)
      addPickerMarker([lng, lat])
      form.longitude = parseFloat(lng.toFixed(7))
      form.latitude = parseFloat(lat.toFixed(7))
      isAutoFill = true
      form.address = geo.formatted_address || keyword
      form.city = geo.city ? geo.city.replace('市', '') : ''
      form.province = geo.province || ''
      setTimeout(() => { isAutoFill = false }, 200)
      ElMessage.success('定位成功：' + (geo.formatted_address || keyword))
    } else {
      ElMessage.warning('未找到该地址，请尝试更精确的地址')
    }
  } catch (e) {
    ElMessage.error('搜索请求失败，请检查网络')
  }
}

const addPickerMarker = (pos) => {
  if (pickerMarker) { pickerMap.remove(pickerMarker) }
  pickerMarker = new AMap.Marker({
    position: pos,
    offset: new AMap.Pixel(-12, -12),
    content: '<div style="width:24px;height:24px;background:#409eff;border-radius:50%;border:2px solid #fff;box-shadow:0 2px 6px rgba(0,0,0,.3);display:flex;align-items:center;justify-content:center;color:#fff;font-size:12px;">⚡</div>'
  })
  pickerMap.add(pickerMarker)
}

const reverseGeocode = (lnglat) => {
  if (!geocoder) return
  isAutoFill = true
  geocoder.getAddress([lnglat.lng, lnglat.lat], (status, result) => {
    if (status === 'complete' && result && result.regeocode) {
      const addr = result.regeocode
      form.address = addr.formattedAddress || ''
      const cityInfo = addr.addressComponent
      form.city = cityInfo.city || ''
      form.province = cityInfo.province || ''
    }
    setTimeout(() => { isAutoFill = false }, 200)
  })
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getStationList({ pageNum: page.current, pageSize: page.size, ...searchForm })
    tableData.value = res.data.records
    page.total = res.data.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { page.current = 1; loadData() }
const resetSearch = () => { searchForm.keyword = ''; searchForm.city = ''; searchForm.status = ''; handleSearch() }

const handleAdd = () => {
  dialogTitle.value = '新增站点'
  mapSearchKeyword.value = ''
  Object.assign(form, {
    id: null, name: '', address: '', city: '', province: '', phone: '',
    totalPorts: 0, availablePorts: 0, longitude: null, latitude: null, status: 1
  })
  dialogVisible.value = true
  nextTick(() => initPickerMap())
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑站点'
  mapSearchKeyword.value = ''
  Object.assign(form, row)
  dialogVisible.value = true
  nextTick(() => initPickerMap())
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate()
  if (!valid) return
  if (!validateLocation()) return
  submitLoading.value = true
  try {
    if (form.id) {
      await updateStation(form)
      ElMessage.success('更新成功')
    } else {
      await addStation(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (id) => {
  await deleteStation(id)
  ElMessage.success('删除成功')
  loadData()
}

const resetForm = () => {
  formRef.value?.resetFields()
  mapSearchKeyword.value = ''
  clearTimeout(searchTimer)
  if (pickerMap) { pickerMap.destroy(); pickerMap = null; pickerMarker = null; geocoder = null }
}

let searchTimer = null
let isAutoFill = false

const autoSearchAddress = (val) => {
  if (!val || !pickerMap || !geocoder || isAutoFill) return
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    mapSearchKeyword.value = val
    handleMapSearch()
  }, 1000)
}

watch(() => form.address, autoSearchAddress)

onMounted(() => loadData())
</script>

<style scoped>
.search-card { margin-bottom: 16px; }
.map-search-box { margin-bottom: 8px; }
.station-picker-map { width: 100%; height: 360px; border: 1px solid #dcdfe6; border-radius: 4px; }
.map-picker-label { font-size: 13px; color: #409eff; margin-bottom: 6px; font-weight: bold; }
.map-picker-tip { font-size: 12px; color: #999; margin-top: 6px; }
</style>
