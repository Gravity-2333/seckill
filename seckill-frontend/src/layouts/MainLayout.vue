<template>
    <div class="layout">
      <!-- 顶部导航 -->
      <header class="header">
        <div class="brand" @click="$router.push('/products')">
          <div class="logo">秒</div>
          <div class="brand-text">
            <div class="title">Seckill</div>
            <div class="sub">在线秒杀系统</div>
          </div>
        </div>
  
        <div class="actions">
          <el-tag v-if="isAdmin" type="danger" effect="light">管理员</el-tag>
          <el-tag v-else type="info" effect="plain">用户</el-tag>
  
          <el-dropdown trigger="hover" @command="onCommand">
            <div class="avatar">
              <span class="circle">{{ avatarText }}</span>
              <span class="name">{{ user?.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
  
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item v-if="isAdmin" command="admin">
                  管理员面板
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
  
      <!-- 主内容区（⚠️ 关键：这里不再有深色背景） -->
      <main class="content">
        <router-view />
      </main>
    </div>
  </template>
  
  <script setup>
  import { computed } from 'vue'
  import { useRouter } from 'vue-router'
  import { ArrowDown } from '@element-plus/icons-vue'
  import { useUserStore } from '@/store/user'
  
  const router = useRouter()
  const userStore = useUserStore()
  
  const user = computed(() => userStore.user)
  const isAdmin = computed(() => Number(user.value?.role) === 1)
  const avatarText = computed(() =>
    (user.value?.username || '?').slice(0, 1).toUpperCase()
  )
  
  const onCommand = (cmd) => {
    if (cmd === 'profile') router.push('/profile')
    if (cmd === 'admin') router.push('/admin/users')
    if (cmd === 'logout') {
      userStore.logout()
      router.push('/login')
    }
  }
  </script>
  
  <style scoped>
  /* 整个布局：明确浅色，不给黑色任何机会 */
  .layout {
    min-height: 100vh;
    background-color: #f5f7fa;
    display: flex;
    flex-direction: column;
  }
  
  /* 顶部栏：白色电商后台风 */
  .header {
    height: 64px;
    padding: 0 20px;
    background: #ffffff;
    border-bottom: 1px solid #ebeef5;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  
  /* 品牌区 */
  .brand {
    display: flex;
    align-items: center;
    gap: 12px;
    cursor: pointer;
  }
  
  .logo {
    width: 40px;
    height: 40px;
    border-radius: 10px;
    background: linear-gradient(135deg, #ff4d4f, #ff7a45);
    color: #fff;
    font-weight: 900;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .brand-text .title {
    font-size: 16px;
    font-weight: 700;
    color: #303133;
  }
  .brand-text .sub {
    font-size: 12px;
    color: #909399;
  }
  
  /* 右侧操作区 */
  .actions {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  
  .avatar {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
  }
  
  .circle {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: #ff4d4f;
    color: #fff;
    font-weight: 700;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .name {
    font-weight: 600;
    color: #303133;
  }
  
  /* ⚠️ 核心：主内容区没有边框、没有深色 */
  .content {
    flex: 1;
    padding: 20px;
    background-color: #f5f7fa;
  }
  </style>
  