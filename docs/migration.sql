-- ==========================================
-- 数据库迁移脚本：zhufeng-resume 扩展
-- 执行前请先备份数据库！
-- ==========================================

-- 1. 修改 template_id 为字符串类型（匹配前端模板注册表）
ALTER TABLE t_resume 
  ALTER COLUMN template_id TYPE VARCHAR(50) USING template_id::VARCHAR(50);

-- 2. 新增全局设置字段
ALTER TABLE t_resume 
  ADD COLUMN IF NOT EXISTS global_settings JSONB DEFAULT '{}';

-- 3. 新增模块排序字段
ALTER TABLE t_resume 
  ADD COLUMN IF NOT EXISTS menu_sections JSONB DEFAULT '[]';

-- 4. 新增自定义模块数据
ALTER TABLE t_resume 
  ADD COLUMN IF NOT EXISTS custom_data JSONB DEFAULT '{}';

-- 5. 新增证书数据
ALTER TABLE t_resume 
  ADD COLUMN IF NOT EXISTS certificates JSONB DEFAULT '[]';
