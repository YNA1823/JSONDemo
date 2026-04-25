-- 创建数据库
CREATE DATABASE IF NOT EXISTS test_demo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE test_demo;

-- 测试数据表
CREATE TABLE IF NOT EXISTS test_data (
    id INT AUTO_INCREMENT PRIMARY KEY,
    test_case VARCHAR(100) NOT NULL COMMENT '测试用例名称',
    endpoint VARCHAR(200) COMMENT 'API端点',
    method VARCHAR(10) COMMENT '请求方法',
    expected_status INT COMMENT '期望状态码',
    description VARCHAR(500) COMMENT '描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试数据表';

-- 测试结果表
CREATE TABLE IF NOT EXISTS test_results (
    id INT AUTO_INCREMENT PRIMARY KEY,
    test_name VARCHAR(200) NOT NULL COMMENT '测试方法名',
    test_class VARCHAR(200) COMMENT '测试类名',
    status VARCHAR(20) NOT NULL COMMENT '测试状态: PASSED/FAILED',
    duration_ms BIGINT COMMENT '执行耗时(毫秒)',
    error_message TEXT COMMENT '错误信息',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试结果表';

-- 插入示例测试数据
INSERT INTO test_data (test_case, endpoint, method, expected_status, description) VALUES
('posts_crud', '/posts', 'GET', 200, '获取所有帖子'),
('posts_crud', '/posts/{id}', 'GET', 200, '根据ID获取帖子'),
('posts_crud', '/posts', 'POST', 201, '创建新帖子'),
('posts_crud', '/posts/{id}', 'PUT', 200, '更新帖子'),
('posts_crud', '/posts/{id}', 'DELETE', 200, '删除帖子'),
('users_crud', '/users', 'GET', 200, '获取所有用户'),
('users_crud', '/users/{id}', 'GET', 200, '根据ID获取用户');

-- 查询测试结果统计视图
CREATE OR REPLACE VIEW v_test_stats AS
SELECT
    test_class,
    status,
    COUNT(*) as count,
    AVG(duration_ms) as avg_duration_ms
FROM test_results
GROUP BY test_class, status;
