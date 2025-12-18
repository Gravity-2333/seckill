<template>
  <el-card style="width: 420px; margin: 100px auto;">
    <el-form :model="form" @submit.prevent>
      <el-form-item label="用户名">
        <el-input v-model="form.username" autocomplete="username" />
      </el-form-item>

      <el-form-item label="密码">
        <el-input v-model="form.password" type="password" autocomplete="current-password" />
      </el-form-item>

      <el-button
        type="primary"
        style="width: 100%"
        :loading="loading"
        native-type="button"
        @click="onLogin"
      >
        登录
      </el-button>
    </el-form>

    <div style="margin-top: 12px; font-size: 12px; opacity: 0.8;">
      <div>调试：打开 F12 → Network → 过滤 Fetch/XHR 看 /api/user/login</div>
    </div>
  </el-card>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { userApi } from '@/api/userApi'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const form = reactive({ username: '', password: '' })

const onLogin = async () => {
  loading.value = true
  try {
    const data = await userApi.login(form.username, form.password)
    userStore.setAuth(data.token, data.user)
    ElMessage.success('登录成功')

    const me = await userApi.current()
    console.log('current:', me)

    router.push('/products')
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>
