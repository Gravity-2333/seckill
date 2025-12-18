<template>
    <div class="wrap">
      <el-card class="hero">
        <div class="hero-title">🔥 秒杀专区</div>
        <div class="hero-sub">好货限时抢，手慢无</div>
      </el-card>
  
      <el-card class="panel">
        <div class="panel-title">商品列表</div>
        <div class="empty">这里下一步对接 product 模块接口 /api/product/list（我可以继续一次性给你生成）</div>
      </el-card>
    </div>
  </template>
  
  <script setup></script>
  
  <style scoped>
  .wrap { display: grid; gap: 12px; }
  .hero {
    border-radius: 14px;
    border: 1px solid rgba(255,255,255,0.10);
    background: linear-gradient(135deg, rgba(255,45,85,0.20), rgba(255,149,0,0.16));
    backdrop-filter: blur(10px);
  }
  .hero-title { color: #fff; font-size: 20px; font-weight: 900; }
  .hero-sub { margin-top: 6px; color: rgba(255,255,255,0.75); }
  
  .panel {
    border-radius: 14px;
    border: 1px solid rgba(255,255,255,0.10);
    background: rgba(255,255,255,0.06);
    backdrop-filter: blur(10px);
  }
  .panel-title { color: #fff; font-weight: 800; margin-bottom: 10px; }
  .empty { color: rgba(255,255,255,0.7); }
  </style>
  