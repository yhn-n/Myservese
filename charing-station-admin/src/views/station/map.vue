<template>
  <div class="station-map">
    <div class="map-layout">
      <div class="map-sidebar">
        <div class="sidebar-header">
          <span>站点列表 ({{ stations.length }})</span>
        </div>
        <div class="station-items">
          <div v-for="item in stations" :key="item.id" class="station-item"
               :class="{ active: selectedId === item.id }" @click="selectStation(item)">
            <div class="station-name">{{ item.name }}</div>
            <div class="station-addr">{{ item.address }}</div>
            <div class="station-meta">
              <el-tag size="small" :type="item.status === 1 ? 'success' : 'info'">
                {{ item.status === 1 ? '营业' : '停业' }}
              </el-tag>
              <span>端口: {{ item.totalPorts || 0 }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="map-main">
        <div id="amap-container" class="amap-container"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { getStationList } from '@/api/station'

const stations = ref([])
const selectedId = ref(null)
let map = null
let markers = []
let infoWindow = null

const initMap = () => {
  map = new AMap.Map('amap-container', {
    zoom: 11,
    center: [114.305, 30.593],
    viewMode: '2D'
  })
  infoWindow = new AMap.InfoWindow({ offset: new AMap.Pixel(0, -36) })
  loadStations()
}

const loadStations = async () => {
  try {
    const res = await getStationList({ pageNum: 1, pageSize: 200 })
    stations.value = res.data.records || []
    await nextTick()
    addMarkers()
  } catch (e) { /* ignore */ }
}

const addMarkers = () => {
  markers.forEach(m => map.remove(m))
  markers = []
  stations.value.forEach(item => {
    if (!item.longitude || !item.latitude) return
    const lnglat = [parseFloat(item.longitude), parseFloat(item.latitude)]

    const content = `<div class="custom-marker">
      <div class="marker-icon ${item.status === 1 ? 'active' : 'inactive'}">⚡</div>
    </div>`

    const marker = new AMap.Marker({
      position: lnglat,
      content: content,
      offset: new AMap.Pixel(-12, -12),
      extData: item
    })

    marker.on('click', () => {
      showInfoWindow(item, lnglat)
      selectedId.value = item.id
    })

    marker.on('mouseover', () => {
      showInfoWindow(item, lnglat)
    })

    map.add(marker)
    markers.push(marker)
  })

  if (markers.length > 0) {
    map.setFitView(markers, false, [100, 100, 100, 100])
  }
}

const showInfoWindow = (item, lnglat) => {
  const content = `
    <div class="info-window">
      <div class="iw-title">${item.name}</div>
      <div class="iw-row">地址：${item.address || '-'}</div>
      <div class="iw-row">城市：${item.city || '-'}</div>
      <div class="iw-row">总端口：${item.totalPorts || 0} | 可用：${item.availablePorts || 0}</div>
      <div class="iw-row">电话：${item.phone || '-'}</div>
      <div class="iw-row">状态：<span style="color:${item.status === 1 ? '#67c23a' : '#999'}">${item.status === 1 ? '营业' : '停业'}</span></div>
    </div>`
  infoWindow.setContent(content)
  infoWindow.open(map, lnglat)
}

const selectStation = (item) => {
  selectedId.value = item.id
  if (item.longitude && item.latitude) {
    const lnglat = [parseFloat(item.longitude), parseFloat(item.latitude)]
    map.setZoomAndCenter(15, lnglat, false, 300)
    setTimeout(() => showInfoWindow(item, lnglat), 350)
  }
}

onMounted(() => {
  if (window.AMap) {
    initMap()
  } else {
    const script = document.createElement('script')
    script.src = 'https://webapi.amap.com/maps?v=2.0&key=6e5817f8c6dbb4ff5dd331df5a3f1b01'
    script.onload = () => initMap()
    document.head.appendChild(script)
  }
})

onUnmounted(() => {
  if (map) { map.destroy(); map = null }
})
</script>

<style scoped>
.station-map { height: calc(100vh - 100px); }
.map-layout { display: flex; height: 100%; gap: 0; }
.map-sidebar {
  width: 320px; flex-shrink: 0; background: #fff;
  border-right: 1px solid #e4e7ed; display: flex; flex-direction: column;
  overflow: hidden;
}
.sidebar-header {
  padding: 14px 16px; font-weight: bold; font-size: 15px;
  background: #f5f7fa; border-bottom: 1px solid #e4e7ed;
}
.station-items { flex: 1; overflow-y: auto; }
.station-item {
  padding: 12px 16px; border-bottom: 1px solid #f0f0f0;
  cursor: pointer; transition: background .2s;
}
.station-item:hover { background: #f5f7fa; }
.station-item.active { background: #ecf5ff; border-left: 3px solid #409eff; }
.station-name { font-weight: bold; color: #333; font-size: 14px; }
.station-addr { font-size: 12px; color: #999; margin: 4px 0; }
.station-meta { display: flex; align-items: center; gap: 8px; font-size: 12px; color: #666; }
.map-main { flex: 1; position: relative; }
.amap-container { width: 100%; height: 100%; }
</style>

<style>
.custom-marker .marker-icon {
  width: 24px; height: 24px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 14px; color: #fff; cursor: pointer;
  box-shadow: 0 2px 6px rgba(0,0,0,.3);
  transition: transform .2s;
}
.custom-marker .marker-icon:hover { transform: scale(1.2); }
.custom-marker .marker-icon.active { background: #409eff; }
.custom-marker .marker-icon.inactive { background: #999; }
.info-window { padding: 4px; min-width: 200px; }
.info-window .iw-title { font-size: 15px; font-weight: bold; color: #333; margin-bottom: 8px; border-bottom: 1px solid #eee; padding-bottom: 6px; }
.info-window .iw-row { font-size: 13px; color: #666; line-height: 22px; }
</style>
