# 逐风 AI 简历平台 — 开发笔记（第八部分）

> 更新日期：2026-05-12
> 内容：简历导出 PDF

---

## 一、模块概述

将用户在编辑器中制作的简历导出为 PDF 文件进行下载。

### 导出流程

```
用户点击"导出PDF"
    ↓
html2canvas 对预览区域截图
    ↓
转换为 Canvas（图片）
    ↓
jsPDF 将图片写入 PDF
    ↓
自动分页处理（多页简历）
    ↓
浏览器下载 PDF 文件
```

---

## 二、技术方案

| 方案 | 技术 | 优缺点 |
|------|------|--------|
| **前端导出**（当前） | html2canvas + jsPDF | 简单，无需后端；大批量性能差 |
| 后端导出 | iText / Puppeteer | 专业稳定；需要后端代码 |

### 依赖安装

```bash
npm install html2canvas jspdf
```

---

## 三、文件结构

```
前端 - 导出模块
└── utils/
    └── exportPdf.js          # 导出工具函数
```

### 修改文件

| 文件 | 修改内容 |
|------|----------|
| `ResumePreview.vue` | 添加 ref 暴露 DOM 节点 |
| `Editor.vue` | 添加导出按钮逻辑 |

---

## 四、exportPdf.js 详解

```javascript
import html2canvas from 'html2canvas'
import jsPDF from 'jspdf'

export async function exportToPdf(element, filename = '简历') {
  // 1. html2canvas 截图
  const canvas = await html2canvas(element, {
    scale: 2,          // 2倍清晰度（retina 效果）
    useCORS: true,     // 支持跨域图片
    backgroundColor: '#ffffff'
  })

  // 2. 计算 A4 尺寸
  const imgWidth = 210   // A4 宽度 (mm)
  const imgHeight = (canvas.height * imgWidth) / canvas.width

  // 3. 创建 PDF 并添加图片
  const pdf = new jsPDF('p', 'mm', 'a4')
  pdf.addImage(canvas.toDataURL('image/png'), 'PNG', 0, 0, imgWidth, imgHeight)

  // 4. 自动分页
  let heightLeft = imgHeight
  let position = 0
  const pageHeight = 297  // A4 高度 (mm)

  while (heightLeft > pageHeight) {
    position = heightLeft - imgHeight
    pdf.addPage()
    pdf.addImage(canvas.toDataURL('image/png'), 'PNG', 0, position, imgWidth, imgHeight)
    heightLeft -= pageHeight
  }

  // 5. 保存下载
  pdf.save(`${filename}.pdf`)
}
```

---

## 五、关键参数说明

| 参数 | 值 | 说明 |
|------|-----|------|
| `scale` | 2 | 截图清晰度，2 代表 2x 分辨率 |
| `imgWidth` | 210 | A4 纸宽度（mm） |
| `pageHeight` | 297 | A4 纸高度（mm） |
| `jsPDF('p', 'mm', 'a4')` | | 纵向、毫米单位、A4 大小 |

---

## 六、后续优化

| 优化项 | 说明 |
|--------|------|
| 后端导出 | 使用 iText 或 Puppeteer 服务端生成 |
| 导出预览 | 导出前预览 PDF 效果 |
| 水印 | 添加"本简历由逐风平台生成"水印 |
| 多格式 | 支持导出 Word、图片格式 |
