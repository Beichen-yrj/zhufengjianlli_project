import html2canvas from 'html2canvas'
import jsPDF from 'jspdf'

/**
 * 将指定 DOM 元素导出为 A4 PDF（强制单页）
 * @param {HTMLElement} element - 要导出的 DOM 元素（.jl-template）
 * @param {String} filename - 导出文件名
 */
export async function exportToPdf(element, filename = '简历') {
  // 保存原始样式，导出后恢复
  const originalStyle = {
    height: element.style.height,
    overflow: element.style.overflow,
    maxHeight: element.style.maxHeight,
  }

  try {
    // === 关键：强制将画布限制为精确 A4 尺寸（210mm × 297mm）===
    // html2canvas 会捕获实际渲染高度，如果不限制就会超出 A4 变成多页
    const a4Width = 210  // mm
    const a4Height = 297 // mm

    // 转换为 px（按 96dpi: 1mm ≈ 3.7795px）
    const mmToPx = 3.7795275591
    const a4WidthPx = Math.round(a4Width * mmToPx)   // ~794px
    const a4HeightPx = Math.round(a4Height * mmToPx)  // ~1123px

    // 临时设置固定尺寸，确保内容不溢出
    element.style.height = `${a4Height}px`
    element.style.overflow = 'hidden'
    element.style.maxHeight = `${a4Height}px`

    // 等待浏览器重排
    await new Promise(r => requestAnimationFrame(r))
    await new Promise(r => setTimeout(r, 50))

    // 第1步：截图（使用精确的 A4 尺寸）
    const canvas = await html2canvas(element, {
      scale: 2,
      useCORS: true,
      backgroundColor: '#ffffff',
      width: a4WidthPx,        // 明确指定宽度，防止 html2canvas 自动扩展
      height: a4HeightPx,       // 明确指定高度 = 一页 A4
      windowWidth: a4WidthPx,
      windowHeight: a4HeightPx,
    })

    // 第2步：创建 PDF（严格一页 A4）
    const pdf = new jsPDF('p', 'mm', 'a4')
    const imgData = canvas.toDataURL('image/png')
    pdf.addImage(imgData, 'PNG', 0, 0, a4Width, a4Height)

    // 第3步：保存下载
    pdf.save(`${filename}.pdf`)
  } finally {
    // 恢复原始样式
    element.style.height = originalStyle.height || ''
    element.style.overflow = originalStyle.overflow || ''
    element.style.maxHeight = originalStyle.maxHeight || ''
  }
}
