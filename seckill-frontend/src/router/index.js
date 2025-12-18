import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  { path: '/login', component: () => import('@/views/login/index.vue') },

  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      { path: '', redirect: '/products' },
      { path: 'products', component: () => import('@/views/product/index.vue') },
      { path: 'profile', component: () => import('@/views/profile/index.vue') },

      // 管理员面板（先放用户管理）
      {
        path: 'admin/users',
        component: () => import('@/views/user/admin.vue'),
        meta: { requiresAdmin: true },
      },
    ],
  },

  { path: '/:pathMatch(.*)*', redirect: '/products' },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to) => {
  const userStore = useUserStore()

  // 未登录：除 login 外全部拦回登录页
  if (to.path !== '/login' && !userStore.token) return '/login'

  // 管理员页：role != 1 拦回商品页
  if (to.meta?.requiresAdmin && Number(userStore.user?.role) !== 1) {
    return '/products'
  }
})

export default router
