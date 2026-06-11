import html2canvas from 'html2canvas'
import jsPDF from 'jspdf'

/**
 * 将指定 DOM 元素导出为 A4 PDF（强制单页）
 *
 * 核心策略：克隆节点 → 离屏渲染 → 截图 → 生成 PDF
 * 不修改原始 DOM，避免布局重排导致文字重叠
 *
 * @param {HTMLElement} element - 要导出的 DOM 元素（.jl-template）
 * @param {String} filename - 导出文件名
 */
export async function exportToPdf(element, filename = '简历') {
  // === A4 尺寸常量 ===
  const a4Width = 210   // mm
  const a4Height = 297  // mm
  const mmToPx = 3.7795275591  // 96dpi
  const a4WidthPx = Math.round(a4Width * mmToPx)   // 794px
  const a4HeightPx = Math.round(a4Height * mmToPx)  // 1123px

  // === 1. 深度克隆节点（不影响原始 DOM）===
  const clone = element.cloneNode(true)

  // === 2. 将克隆节点放入离屏容器（必须插入 DOM 才能正确渲染）===
  const wrapper = document.createElement('div')
  wrapper.style.cssText = `
    position: fixed;
    left: -9999px;
    top: 0;
    width: ${a4WidthPx}px;
    height: ${a4HeightPx}px;
    overflow: hidden;
    background: #ffffff;
    z-index: -9999;
    pointer-events: none;
  `
  wrapper.appendChild(clone)
  document.body.appendChild(wrapper)

  try {
    // === 3. 对克隆体设置精确 A4 尺寸（不影响编辑器）===
    clone.style.cssText = `
      width: ${a4WidthPx}px;
      height: ${a4HeightPx};
      overflow: hidden;
      box-sizing: border-box;
      padding: 8mm 14mm;
      background: #ffffff;
      /* 关键补偿：防止 html2canvas 渲染时中英文混排文字重叠 */
      letter-spacing: 0.02em;
    `

    // 等待克隆体完成渲染和字体加载
    await new Promise(r => requestAnimationFrame(r))
    await new Promise(r => setTimeout(r, 100))

    // === 4. 用 html2canvas 截图克隆体 ===
    const canvas = await html2canvas(clone, {
      scale: 2,
      useCORS: true,
      backgroundColor: '#ffffff',
      width: a4WidthPx,
      height: a4HeightPx,
      windowWidth: a4WidthPx,
      windowHeight: a4HeightPx,
      // 允许跨域图片
      allowTaint: false,
      // 移除可能的模糊问题
      imageTimeout: 15000,
      // 确保文字清晰
      logging: false,
    })

    // === 5. 生成单页 A4 PDF ===
    const pdf = new jsPDF('p', 'mm', 'a4')
    const imgData = canvas.toDataURL('image/png', 1.0)
    pdf.addImage(imgData, 'PNG', 0, 0, a4Width, a4Height)

    // === 6. 保存下载 ===
    pdf.save(`${filename}.pdf`)
  } finally {
    // 清理离屏容器（必须移除，避免内存泄漏）
    if (wrapper.parentNode) {
      wrapper.parentNode.removeChild(wrapper)
    }
  }
}
