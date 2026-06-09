import html2canvas from 'html2canvas'
import jsPDF from 'jspdf'

/**
 * 将指定 DOM 元素导出为 A4 PDF
 * @param {HTMLElement} element - 要导出的 DOM 元素
 * @param {String} filename - 导出文件名
 */
export async function exportToPdf(element, filename = '简历') {
  // 第1步：截图
  const canvas = await html2canvas(element, {
    scale: 2,          // 2倍清晰度
    useCORS: true,     // 支持跨域图片
    backgroundColor: '#ffffff'
  })

  // 第2步：计算尺寸
  const imgWidth = 210   // A4 宽度(mm)
  const imgHeight = (canvas.height * imgWidth) / canvas.width

  // 第3步：创建 PDF
  const pdf = new jsPDF('p', 'mm', 'a4')
  pdf.addImage(canvas.toDataURL('image/png'), 'PNG', 0, 0, imgWidth, imgHeight)

  // 第4步：如果超过一页，自动分页
  let heightLeft = imgHeight
  let position = 0
  const pageHeight = 297  // A4 高度(mm)

  while (heightLeft > pageHeight) {
    position = heightLeft - imgHeight
    pdf.addPage()
    pdf.addImage(canvas.toDataURL('image/png'), 'PNG', 0, position, imgWidth, imgHeight)
    heightLeft -= pageHeight
  }

  // 第5步：保存下载
  pdf.save(`${filename}.pdf`)
}