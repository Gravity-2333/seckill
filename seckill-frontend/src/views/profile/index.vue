<template>
    <el-card class="panel">
      <div class="title">个人中心</div>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名">{{ user?.username }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ Number(user?.role) === 1 ? 'ADMIN' : 'USER' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ user?.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ user?.email }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ user?.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </template>
  
  <script setup>
  import { computed } from 'vue'
  import { useUserStore } from '@/store/user'
  const userStore = useUserStore()
  const user = computed(() => userStore.user)
  </script>
  
  <style scoped>
  .panel {
    border-radius: 14px;
    border: 1px solid rgba(255,255,255,0.10);
    background: rgba(255,255,255,0.06);
    backdrop-filter: blur(10px);
  }
  .title { color: #fff; font-weight: 900; margin-bottom: 12px; }
  </style>
  