<template>
  <div class="ai-config">
    <el-card>
      <template #header><span>AI客服配置</span></template>
      <el-form :model="config" label-width="120px" style="max-width:600px">
        <el-form-item label="AI服务商">
          <el-select v-model="config.provider" style="width:100%">
            <el-option label="DeepSeek" value="deepseek" />
            <el-option label="GLM" value="glm" />
          </el-select>
        </el-form-item>
        <el-form-item label="API Key">
          <el-input v-model="config.apiKey" placeholder="请输入API Key" show-password />
        </el-form-item>
        <el-form-item label="API地址">
          <el-input v-model="config.apiUrl" placeholder="API请求地址" />
        </el-form-item>
        <el-form-item label="模型名称">
          <el-input v-model="config.model" placeholder="如 deepseek-chat" />
        </el-form-item>
        <el-form-item label="系统提示词">
          <el-input v-model="config.systemPrompt" type="textarea" :rows="4" placeholder="AI角色提示词" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave">保存配置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>
<script setup>
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'

const config = reactive({
  provider: 'deepseek',
  apiKey: '',
  apiUrl: 'https://api.deepseek.com/v1/chat/completions',
  model: 'deepseek-chat',
  systemPrompt: '你是一个专业的新能源充电站客服助手'
})

const handleSave = () => {
  ElMessage.success('配置已保存（提示：需后端支持持久化存储）')
}
</script>
