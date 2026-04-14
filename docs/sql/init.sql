-- =====================================================
-- 校园二手交易平台 - 数据库初始化脚本
-- 数据库版本: MySQL 8.0+
-- 创建时间: 2024-01-01
-- =====================================================

CREATE DATABASE IF NOT EXISTS campus_market DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE campus_market;

-- =====================================================
-- 1. 学校表
-- =====================================================
CREATE TABLE IF NOT EXISTS `school` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '学校ID',
    `name`        VARCHAR(100) NOT NULL COMMENT '学校名称',
    `logo`        VARCHAR(500)          COMMENT '学校Logo地址',
    `city`        VARCHAR(50)           COMMENT '所在城市',
    `province`    VARCHAR(50)           COMMENT '所在省份',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序（越小越靠前）',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0=禁用 1=启用',
    `remark`      VARCHAR(255)          COMMENT '备注',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除 1=已删除',
    PRIMARY KEY (`id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_sort` (`sort`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '学校表';

-- =====================================================
-- 2. 校区表
-- =====================================================
CREATE TABLE IF NOT EXISTS `campus` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '校区ID',
    `school_id`   BIGINT       NOT NULL COMMENT '所属学校ID',
    `name`        VARCHAR(100) NOT NULL COMMENT '校区名称',
    `address`     VARCHAR(255)          COMMENT '校区地址',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0=禁用 1=启用',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    INDEX `idx_school_id` (`school_id`),
    INDEX `idx_status` (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '校区表';

-- =====================================================
-- 3. 用户表
-- =====================================================
CREATE TABLE IF NOT EXISTS `user` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`     VARCHAR(50)  NOT NULL COMMENT '用户名（登录账号）',
    `password`     VARCHAR(255) NOT NULL COMMENT '密码（bcrypt加密）',
    `nickname`     VARCHAR(50)           COMMENT '昵称',
    `avatar`       VARCHAR(500)          COMMENT '头像地址',
    `gender`       TINYINT               COMMENT '性别：0=未知 1=男 2=女',
    `phone`        VARCHAR(20)           COMMENT '手机号',
    `email`        VARCHAR(100)          COMMENT '邮箱',
    `school_id`    BIGINT                COMMENT '所在学校ID',
    `campus_id`    BIGINT                COMMENT '所在校区ID',
    `student_id`   VARCHAR(50)           COMMENT '学号（预留校园认证）',
    `real_name`    VARCHAR(50)           COMMENT '真实姓名（预留校园认证）',
    `verified`     TINYINT      NOT NULL DEFAULT 0 COMMENT '是否校园认证：0=未认证 1=已认证',
    `status`       TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0=封禁 1=正常',
    `last_login_at` DATETIME              COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(50)           COMMENT '最后登录IP',
    `bio`          VARCHAR(255)          COMMENT '个人简介',
    `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `updated_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_username` (`username`),
    INDEX `idx_phone` (`phone`),
    INDEX `idx_school_id` (`school_id`),
    INDEX `idx_status` (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

-- =====================================================
-- 4. 管理员用户表
-- =====================================================
CREATE TABLE IF NOT EXISTS `admin_user` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
    `username`     VARCHAR(50)  NOT NULL COMMENT '登录账号',
    `password`     VARCHAR(255) NOT NULL COMMENT '密码（bcrypt加密）',
    `nickname`     VARCHAR(50)           COMMENT '昵称',
    `avatar`       VARCHAR(500)          COMMENT '头像',
    `role`         VARCHAR(20)  NOT NULL DEFAULT 'admin' COMMENT '角色：super_admin=超管 admin=普通管理员',
    `school_id`    BIGINT                COMMENT '管辖学校ID（null表示全部）',
    `status`       TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0=禁用 1=启用',
    `last_login_at` DATETIME              COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(50)           COMMENT '最后登录IP',
    `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_username` (`username`),
    INDEX `idx_status` (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '管理员用户表';

-- =====================================================
-- 5. 商品分类表
-- =====================================================
CREATE TABLE IF NOT EXISTS `product_category` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name`        VARCHAR(50)  NOT NULL COMMENT '分类名称',
    `icon`        VARCHAR(500)          COMMENT '分类图标',
    `parent_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '父分类ID（0表示顶级）',
    `level`       TINYINT      NOT NULL DEFAULT 1 COMMENT '层级：1=一级 2=二级',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0=禁用 1=启用',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    INDEX `idx_parent_id` (`parent_id`),
    INDEX `idx_sort` (`sort`),
    INDEX `idx_status` (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '商品分类表';

-- =====================================================
-- 6. 商品表
-- =====================================================
CREATE TABLE IF NOT EXISTS `product` (
    `id`              BIGINT         NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `user_id`         BIGINT         NOT NULL COMMENT '发布用户ID',
    `category_id`     BIGINT         NOT NULL COMMENT '分类ID',
    `school_id`       BIGINT         NOT NULL COMMENT '所在学校ID',
    `campus_id`       BIGINT                  COMMENT '所在校区ID',
    `title`           VARCHAR(100)   NOT NULL COMMENT '商品标题',
    `description`     TEXT                    COMMENT '商品描述',
    `price`           DECIMAL(10, 2) NOT NULL COMMENT '价格（元）',
    `original_price`  DECIMAL(10, 2)          COMMENT '原价（元）',
    `cover_image`     VARCHAR(500)            COMMENT '封面图片',
    `condition_level` TINYINT        NOT NULL DEFAULT 1 COMMENT '成色：1=全新 2=几乎全新 3=轻微使用 4=明显使用 5=有瑕疵',
    `trade_type`      TINYINT        NOT NULL DEFAULT 1 COMMENT '交易方式：1=线下交易 2=快递邮寄 3=均可',
    `trade_location`  VARCHAR(255)            COMMENT '交易地点',
    `tags`            VARCHAR(255)            COMMENT '标签，逗号分隔（预留）',
    `view_count`      INT            NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `favorite_count`  INT            NOT NULL DEFAULT 0 COMMENT '收藏次数',
    `message_count`   INT            NOT NULL DEFAULT 0 COMMENT '留言次数',
    `status`          TINYINT        NOT NULL DEFAULT 0 COMMENT '商品状态：0=待审核 1=已上架 2=已下架 3=已售出 4=审核拒绝',
    `audit_status`    TINYINT        NOT NULL DEFAULT 0 COMMENT '审核状态：0=待审核 1=审核通过 2=审核拒绝',
    `audit_remark`    VARCHAR(500)            COMMENT '审核备注',
    `audited_by`      BIGINT                  COMMENT '审核管理员ID',
    `audited_at`      DATETIME                COMMENT '审核时间',
    `is_top`          TINYINT        NOT NULL DEFAULT 0 COMMENT '是否置顶：0=否 1=是',
    `created_at`      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `updated_at`      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT        NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_school_id` (`school_id`),
    INDEX `idx_campus_id` (`campus_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_audit_status` (`audit_status`),
    INDEX `idx_price` (`price`),
    INDEX `idx_created_at` (`created_at`),
    FULLTEXT INDEX `ft_title_desc` (`title`, `description`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '商品表';

-- =====================================================
-- 7. 商品图片表
-- =====================================================
CREATE TABLE IF NOT EXISTS `product_image` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '图片ID',
    `product_id`  BIGINT       NOT NULL COMMENT '商品ID',
    `url`         VARCHAR(500) NOT NULL COMMENT '图片地址',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_product_id` (`product_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '商品图片表';

-- =====================================================
-- 8. 收藏表
-- =====================================================
CREATE TABLE IF NOT EXISTS `favorite` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id`     BIGINT   NOT NULL COMMENT '用户ID',
    `product_id`  BIGINT   NOT NULL COMMENT '商品ID',
    `created_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_user_product` (`user_id`, `product_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_product_id` (`product_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '收藏表';

-- =====================================================
-- 9. 商品留言表
-- =====================================================
CREATE TABLE IF NOT EXISTS `product_message` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '留言ID',
    `product_id`  BIGINT   NOT NULL COMMENT '商品ID',
    `user_id`     BIGINT   NOT NULL COMMENT '留言用户ID',
    `parent_id`   BIGINT   NOT NULL DEFAULT 0 COMMENT '父留言ID（0表示顶级）',
    `content`     TEXT     NOT NULL COMMENT '留言内容',
    `status`      TINYINT  NOT NULL DEFAULT 1 COMMENT '状态：0=隐藏 1=正常',
    `created_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '留言时间',
    `updated_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT  NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    INDEX `idx_product_id` (`product_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_parent_id` (`parent_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '商品留言表';

-- =====================================================
-- 10. 举报表
-- =====================================================
CREATE TABLE IF NOT EXISTS `report` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '举报ID',
    `reporter_id`  BIGINT       NOT NULL COMMENT '举报用户ID',
    `product_id`   BIGINT       NOT NULL COMMENT '被举报商品ID',
    `reason`       TINYINT      NOT NULL COMMENT '举报原因：1=虚假信息 2=商品违规 3=价格欺诈 4=重复发布 5=其他',
    `description`  VARCHAR(500)          COMMENT '举报说明',
    `images`       VARCHAR(1000)         COMMENT '举报凭证图片，逗号分隔',
    `status`       TINYINT      NOT NULL DEFAULT 0 COMMENT '处理状态：0=待处理 1=已处理 2=已忽略',
    `handle_remark` VARCHAR(500)         COMMENT '处理备注',
    `handled_by`   BIGINT                COMMENT '处理管理员ID',
    `handled_at`   DATETIME              COMMENT '处理时间',
    `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '举报时间',
    `updated_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_reporter_id` (`reporter_id`),
    INDEX `idx_product_id` (`product_id`),
    INDEX `idx_status` (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '举报表';

-- =====================================================
-- 11. 公告表
-- =====================================================
CREATE TABLE IF NOT EXISTS `notice` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    `title`        VARCHAR(200) NOT NULL COMMENT '公告标题',
    `content`      TEXT         NOT NULL COMMENT '公告内容（富文本）',
    `cover`        VARCHAR(500)          COMMENT '封面图',
    `type`         TINYINT      NOT NULL DEFAULT 1 COMMENT '类型：1=通知 2=活动 3=规则',
    `is_top`       TINYINT      NOT NULL DEFAULT 0 COMMENT '是否置顶：0=否 1=是',
    `school_id`    BIGINT                COMMENT '关联学校ID（null表示全平台）',
    `status`       TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0=草稿 1=已发布',
    `view_count`   INT          NOT NULL DEFAULT 0 COMMENT '阅读次数',
    `created_by`   BIGINT       NOT NULL COMMENT '创建管理员ID',
    `published_at` DATETIME              COMMENT '发布时间',
    `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    INDEX `idx_type` (`type`),
    INDEX `idx_status` (`status`),
    INDEX `idx_school_id` (`school_id`),
    INDEX `idx_is_top` (`is_top`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '公告表';

-- =====================================================
-- 初始化数据
-- =====================================================

-- 默认超级管理员（密码: admin123456，bcrypt加密）
INSERT INTO `admin_user` (`username`, `password`, `nickname`, `role`, `status`)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '超级管理员', 'super_admin', 1);

-- 默认学校
INSERT INTO `school` (`name`, `city`, `province`, `sort`, `status`)
VALUES
    ('北京大学', '北京', '北京', 1, 1),
    ('清华大学', '北京', '北京', 2, 1),
    ('浙江大学', '杭州', '浙江', 3, 1),
    ('复旦大学', '上海', '上海', 4, 1),
    ('中国人民大学', '北京', '北京', 5, 1);

-- 默认校区（以北京大学为例）
INSERT INTO `campus` (`school_id`, `name`, `address`, `sort`, `status`)
VALUES
    (1, '燕园校区', '北京市海淀区颐和园路5号', 1, 1),
    (1, '医学部', '北京市海淀区学院路38号', 2, 1),
    (2, '紫荆园校区', '北京市海淀区清华园1号', 1, 1),
    (2, '胜因院校区', '北京市海淀区', 2, 1);

-- 默认商品分类
INSERT INTO `product_category` (`name`, `icon`, `parent_id`, `level`, `sort`, `status`)
VALUES
    ('数码电子', '📱', 0, 1, 1, 1),
    ('教材书籍', '📚', 0, 1, 2, 1),
    ('生活用品', '🏠', 0, 1, 3, 1),
    ('服装鞋帽', '👕', 0, 1, 4, 1),
    ('运动户外', '⚽', 0, 1, 5, 1),
    ('美妆护肤', '💄', 0, 1, 6, 1),
    ('自行车/电动车', '🚲', 0, 1, 7, 1),
    ('乐器', '🎵', 0, 1, 8, 1),
    ('零食/饮品', '🍎', 0, 1, 9, 1),
    ('其他', '📦', 0, 1, 99, 1);
