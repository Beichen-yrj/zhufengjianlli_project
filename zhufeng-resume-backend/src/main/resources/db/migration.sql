-- ============================================================
-- zhufeng-resume 数据库迁移脚本
-- 将旧的 resume_data (单一 JSONB) 拆分为独立列
-- ============================================================

-- 1. 添加新列
ALTER TABLE t_resume ADD COLUMN IF NOT EXISTS basic JSONB DEFAULT '{}';
ALTER TABLE t_resume ADD COLUMN IF NOT EXISTS education JSONB DEFAULT '[]';
ALTER TABLE t_resume ADD COLUMN IF NOT EXISTS experience JSONB DEFAULT '[]';
ALTER TABLE t_resume ADD COLUMN IF NOT EXISTS projects JSONB DEFAULT '[]';
ALTER TABLE t_resume ADD COLUMN IF NOT EXISTS skill_content TEXT DEFAULT '';
ALTER TABLE t_resume ADD COLUMN IF NOT EXISTS self_evaluation_content TEXT DEFAULT '';

-- 2. 从旧 resume_data JSON 迁移数据到新列 (如果 resume_data 列存在)
-- 注意：旧 resume_data 的键名可能是 basicInfo / skills 等旧格式
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='t_resume' AND column_name='resume_data') THEN
        UPDATE t_resume SET
            basic = COALESCE(resume_data->'basic', resume_data->'basicInfo', '{}'::jsonb),
            education = COALESCE(resume_data->'education', '[]'::jsonb),
            experience = COALESCE(resume_data->'experience', '[]'::jsonb),
            projects = COALESCE(resume_data->'projects', '[]'::jsonb),
            skill_content = COALESCE(resume_data->>'skillContent', resume_data->>'skills', ''),
            self_evaluation_content = COALESCE(resume_data->>'selfEvaluationContent', resume_data->>'selfEvaluation', '')
        WHERE resume_data IS NOT NULL;
    END IF;
END $$;

-- 3. 可选：删除旧 resume_data 列
-- ALTER TABLE t_resume DROP COLUMN IF EXISTS resume_data;
