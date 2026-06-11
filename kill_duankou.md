# 前后端常用端口被占用 - 解决方案手册

> 适用平台：Windows（PowerShell / CMD）
> 适用场景：开发 / 调试阶段，端口被占用导致服务无法启动

---

## 一、本项目常用端口

| 端口 | 用途 | 配置文件 |
|------|------|----------|
| `8081` | 后端 Spring Boot | `zhufeng-resume-backend/src/main/resources/application.yml` |
| `5173` | 前端 Vite Dev（默认） | `zhufeng-resume-frontend/vite.config.js` |
| `5174` | 前端 Vite Dev（5173 被占用时自动切换） | 同上 |
| `3306` | MySQL 数据库 | `application.yml` |
| `6379` | Redis 缓存 | `application.yml` |

---

## 二、报错信息识别

### 1. 后端（Spring Boot）
```
Web server failed to start. Port 8081 was already in use.

Action: Identify and stop the process that's listening on port 8081
or configure this application to listen on another port.
```

### 2. 前端（Vite）
```
Port 5173 is in use, trying another one...
  ➜  Local:   http://localhost:5174/
（端口被占用时 Vite 会自动切换下一个可用端口，但 API 代理可能失败）
```

---

## 三、定位占用进程

### 方法 1：`netstat` + `findstr`（最常用）
```powershell
netstat -ano | findstr :8081
```
**输出示例：**
```
TCP    0.0.0.0:8081    0.0.0.0:0    LISTENING    28956
TCP    [::]:8081       [::]:0        LISTENING    28956
```
最后一列的数字（`28956`）就是 **PID（进程 ID）**。

### 方法 2：`Get-NetTCPConnection`（PowerShell 推荐）
```powershell
Get-NetTCPConnection -LocalPort 8081 | Select-Object LocalPort, OwningProcess, State
```

### 方法 3：任务管理器
1. 按 `Ctrl + Shift + Esc` 打开任务管理器
2. 切到 **详细信息** 选项卡
3. 找到对应 PID 的进程 → 右键 → 结束任务

---

## 四、释放端口（杀掉进程）

### 方法 1：`taskkill`（最直接）
```powershell
# 根据 PID 杀掉进程
taskkill /F /PID 28956
```
- `/F` 表示强制终止
- **输出**：`成功: 已终止 PID 为 28956 的进程。`

### 方法 2：按进程名杀掉
```powershell
# 杀掉所有 java 进程（后端）
taskkill /F /IM java.exe

# 杀掉所有 node 进程（前端）
taskkill /F /IM node.exe
```
⚠️ **慎用** `node.exe`：可能同时杀掉其他 Node 工具。

### 方法 3：按端口自动查杀（一行命令）
```powershell
# 找到占用 8081 的 PID 并杀掉
$pid = (netstat -ano | findstr :8081 | Select-Object -First 1).Trim().Split(' ')[-1]; taskkill /F /PID $pid
```

### 方法 4：PowerShell 一步到位
```powershell
Get-NetTCPConnection -LocalPort 8081 -State Listen |
  ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }
```

---

## 五、验证端口已释放
```powershell
netstat -ano | findstr :8081
```
- **无输出**（退出码 1）= 端口已释放 ✅
- **仍有 LISTENING** = 未杀干净，重新执行

---

## 六、避免冲突：直接换端口

### 1. 后端 Spring Boot

#### 方式 A：修改 `application.yml`
```yaml
server:
  port: 8082   # 改成空闲端口
```

#### 方式 B：启动时命令行指定
```powershell
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8082
```

#### 方式 C：IDEA 启动配置
`Run → Edit Configurations → Environment → VM options` 加：
```
-Dserver.port=8082
```

### 2. 前端 Vite

#### 修改 `vite.config.js`
```javascript
export default defineConfig({
  server: {
    port: 3000,           // 指定固定端口
    strictPort: true,     // 端口被占时直接失败（不自动切换）
    host: '0.0.0.0'       // 允许局域网访问（可选）
  }
})
```

#### ⚠️ 换端口后同步修改前端 API baseURL
文件：[`src/api/request.js`](file:///D:/TARE-project/zhufengaijianli-project/zhufeng-resume-frontend/src/api/request.js)
```javascript
const request = axios.create({
  baseURL: 'http://localhost:8082',   // 同步改为新端口
  timeout: 10000
})
```

---

## 七、本项目一键排查脚本

保存为 `scripts/kill-port.ps1`：
```powershell
param(
  [Parameter(Mandatory=$true)][int]$Port
)

Write-Host "=== 查找占用 $Port 的进程 ===" -ForegroundColor Cyan
$conns = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue

if (-not $conns) {
  Write-Host "端口 $Port 空闲 ✅" -ForegroundColor Green
  exit 0
}

foreach ($c in $conns) {
  $pid_proc = $c.OwningProcess
  $proc = Get-Process -Id $pid_proc -ErrorAction SilentlyContinue
  Write-Host "PID: $pid_proc    名称: $($proc.ProcessName)" -ForegroundColor Yellow
}

$confirm = Read-Host "确认杀掉以上进程？(y/n)"
if ($confirm -eq 'y') {
  foreach ($c in $conns) {
    Stop-Process -Id $c.OwningProcess -Force
    Write-Host "已终止 PID $($c.OwningProcess)" -ForegroundColor Red
  }
  Write-Host "完成" -ForegroundColor Green
}
```

**用法：**
```powershell
.\scripts\kill-port.ps1 -Port 8081
```

---

## 八、常见问题

### Q1：`taskkill` 提示"拒绝访问"？
进程可能以管理员/系统身份运行。**用管理员身份打开 PowerShell** 重试。

### Q2：杀掉后又自动启动？
说明有服务监控（如 `spring-boot-devtools` 热部署、Windows Service）。需要：
- IDEA 中先点停止 → 再杀进程
- 或者禁用 devtools

### Q3：改了端口前端还是连不上？
99% 是 `request.js` 的 `baseURL` 没同步改。要么改代码，要么配置 Vite 代理：

```javascript
// vite.config.js
export default defineConfig({
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8082',   // 代理到新端口
        changeOrigin: true
      }
    }
  }
})
```
配合 `request.js`：
```javascript
baseURL: ''   // 走 Vite 代理，不需要写完整地址
```

### Q4：找不到 PID 对应的进程？
可能进程已退出（端口 TIME_WAIT 状态）。等 30~60 秒自动释放，或：
```powershell
netsh int ipv4 set dynamicport tcp start=49152 num=16384
# 调整动态端口范围（一般不需要）
```

---

## 九、完整操作流程（标准 SOP）

1. **看到端口占用错误** → `netstat -ano | findstr :8081` 拿 PID
2. **确认是哪个进程** → 任务管理器 / `Get-Process -Id <PID>`
3. **结束进程** → `taskkill /F /PID <PID>`
4. **验证释放** → 再次 `netstat -ano | findstr :8081`（应无输出）
5. **重新启动服务** → `mvn spring-boot:run` / `npm run dev`
6. **仍冲突** → 改端口（参考第六章）

---

> 📌 **黄金法则：**
> 看到端口占用先查 PID → 杀进程 → 验证 → 重启。
> 不要盲目重启服务，僵尸进程会一直占着端口。
