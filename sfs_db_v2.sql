CREATE DATABASE IF NOT EXISTS sfs_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE sfs_db;

CREATE TABLE `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `uid` VARCHAR(32) NOT NULL,
  `nickname` VARCHAR(64) DEFAULT NULL,
  `avatar_url` VARCHAR(255) DEFAULT NULL,
  `gender` TINYINT DEFAULT 0,
  `status` TINYINT DEFAULT 1,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_uid` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE `user_identities` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `identity_type` VARCHAR(20) NOT NULL,
  `identifier` VARCHAR(128) NOT NULL,
  `credential` VARCHAR(255) DEFAULT NULL,
  `wx_openid` VARCHAR(64) DEFAULT NULL,
  `wx_unionid` VARCHAR(64) DEFAULT NULL,
  `verified` TINYINT DEFAULT 0,
  `is_primary` TINYINT DEFAULT 0,
  `bind_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_identifier` (`identity_type`, `identifier`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_wx_openid` (`wx_openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户身份凭证';

CREATE TABLE `activities` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `activity_no` VARCHAR(32) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `type` VARCHAR(20) DEFAULT 'SHAKE_COUNT',
  `creator_id` BIGINT NOT NULL,
  `state` TINYINT DEFAULT 1,
  `room_code` VARCHAR(10) DEFAULT NULL,
  `room_pwd` VARCHAR(20) DEFAULT NULL,
  `begin_time` TIMESTAMP NULL DEFAULT NULL,
  `end_time` TIMESTAMP NULL DEFAULT NULL,
  `duration_ms` BIGINT DEFAULT 120000,
  `max_participants` INT DEFAULT 400,
  `description` TEXT,
  `cover_img` VARCHAR(255) DEFAULT NULL,
  `location_name` VARCHAR(100) DEFAULT NULL,
  `lng` DECIMAL(10,6) DEFAULT NULL,
  `lat` DECIMAL(10,6) DEFAULT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_no` (`activity_no`),
  KEY `idx_creator_id` (`creator_id`),
  KEY `idx_room_code` (`room_code`),
  KEY `idx_state` (`state`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动表';

CREATE TABLE `activity_participants` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `activity_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `join_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `status` TINYINT DEFAULT 1,
  `device_id` VARCHAR(64) DEFAULT NULL,
  `platform` VARCHAR(20) DEFAULT NULL,
  `is_ready` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_user` (`activity_id`, `user_id`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动参与者';

CREATE TABLE `reward_configs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `activity_id` BIGINT NOT NULL,
  `rank_start` INT NOT NULL,
  `rank_end` INT NOT NULL,
  `reward_type` VARCHAR(20) DEFAULT 'COUPON',
  `reward_name` VARCHAR(100) DEFAULT NULL,
  `reward_value` VARCHAR(255) DEFAULT NULL,
  `reward_img` VARCHAR(255) DEFAULT NULL,
  `quantity` INT DEFAULT 0,
  `sort_order` INT DEFAULT 0,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_activity_id` (`activity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='奖励配置';

CREATE TABLE `reward_grants` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `activity_id` BIGINT NOT NULL,
  `reward_config_id` BIGINT NOT NULL,
  `rank` INT DEFAULT NULL,
  `grant_status` TINYINT DEFAULT 0,
  `claim_status` TINYINT DEFAULT 0,
  `grant_time` TIMESTAMP NULL DEFAULT NULL,
  `claim_time` TIMESTAMP NULL DEFAULT NULL,
  `expire_time` TIMESTAMP NULL DEFAULT NULL,
  `transaction_no` VARCHAR(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_activity_id` (`activity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='奖励发放记录';

CREATE TABLE `leaderboards` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `activity_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `score` BIGINT DEFAULT 0,
  `rank` INT DEFAULT NULL,
  `is_cheat` TINYINT DEFAULT 0,
  `snapshot_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_user` (`activity_id`, `user_id`),
  KEY `idx_activity_rank` (`activity_id`, `rank`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排行榜';
