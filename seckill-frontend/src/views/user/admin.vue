<template>
  <div class="page">
    <!-- 页面标题 -->
    <div class="header">
      <div>
        <h2 class="title">用户管理</h2>
        <p class="desc">管理员可查看、管理系统内所有用户</p>
      </div>
    </div>

    <!-- 搜索区 -->
    <el-card class="card">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="用户名">
          <el-input
            v-model="query.username"
            placeholder="输入用户名"
            clearable
            style="width: 220px"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="reloadFirstPage">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格区 -->
    <el-card class="card">
      <el-table
        :data="tableData"
        border
        stripe
        v-loading="loading"
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="140" />
        <el-table-column prop="phone" label="手机号" min-width="140" />
        <el-table-column prop="email" label="邮箱" min-width="200" />
        <el-table-column label="角色" width="120">
          <template #default="{ row }">
            <el-tag v-if="Number(row.role) === 1" type="danger">管理员</el-tag>
            <el-tag v-else type="info">普通用户</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" min-width="180" />

        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-switch
              :model-value="Number(row.status) === 1"
              active-color="#f56c6c"
              @change="(val) => onToggleStatus(row, val)"
            />
          </template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <el-pagination
          background
          layout="total, prev, pager, next, sizes"
          :total="total"
          :page-size="query.pageSize"
          :current-page="query.pageNum"
          :page-sizes="[5, 10, 20, 50]"
          @current-change="onPageChange"
          @size-change="onSizeChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { userApi } from '@/api/userApi'

const loading = ref(false)

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
})

const tableData = ref([])
const total = ref(0)

const load = async () => {
  loading.value = true
  try {
    const page = await userApi.page(query)
    tableData.value = page.records || page.list || []
    total.value = page.total ?? tableData.value.length
  } catch (e) {
    ElMessage.error(e?.msg || '加载失败')
  } finally {
    loading.value = false
  }
}

const reloadFirstPage = async () => {
  query.pageNum = 1
  await load()
}

const reset = async () => {
  query.username = ''
  query.pageNum = 1
  query.pageSize = 10
  await load()
}

const onPageChange = async (p) => {
  query.pageNum = p
  await load()
}

const onSizeChange = async (s) => {
  query.pageSize = s
  query.pageNum = 1
  await load()
}

const onToggleStatus = async (row, val) => {
  try {
    await userApi.updateStatus(row.id, val ? 1 : 0)
    ElMessage.success('状态已更新')
    row.status = val ? 1 : 0
  } catch {
    ElMessage.error('状态更新失败')
  }
}

onMounted(load)
</script>

<style scoped>
.page {
  padding: 16px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 64px);
}

.header {
  margin-bottom: 16px;
}

.title {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  color: #303133;
}

.desc {
  margin-top: 4px;
  font-size: 14px;
  color: #909399;
}

.card {
  margin-bottom: 16px;
  border-radius: 8px;
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
