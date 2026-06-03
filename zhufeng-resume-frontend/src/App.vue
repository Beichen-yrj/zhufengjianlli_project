<template>
  <div>
    <nav class="navbar">
      <div class="nav-left">
        <router-link to="/home">逐风简历</router-link>
      </div>
      <div class="nav-right">
        <router-link to="/templates">模板中心</router-link>
        <router-link to="/login">登录</router-link>
      </div>
    </nav>
    <router-view />
  </div>
</template>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 40px;
  height: 60px;
  background: white;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.navbar a {
  margin-left: 20px;
  text-decoration: none;
  color: #333;
}

.navbar a:first-of-type {
  margin-left: 0;
  font-weight: bold;
  color: #667eea;
}
</style>