CREATE TABLE IF NOT EXISTS `message_send`  (
   `message_id` bigint UNSIGNED NOT NULL COMMENT '消息id',
   `group_id` bigint NULL DEFAULT 0 COMMENT '群id',
   `sender_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '发送者id',
   `receiver_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '接收者id',
   `chat_type` enum('SINGLE','GROUP') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'SINGLE' COMMENT '聊天类型',
   `message_type` int NULL DEFAULT 0 COMMENT '消息类型',
   `request_type` int NULL DEFAULT 0 COMMENT '请求类型',
   `media_type` enum('TEXT','IMAGE_AND_TEXT','IMAGE','VOICE','VIDEO','EMOJI') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'TEXT' COMMENT '媒体类型',
   `sequence` bigint not NULL DEFAULT 0 COMMENT '消息序号',
   `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '消息内容',
   `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表情/图片/语音/视频等文件地址',

   `deleted` bit(1) NULL DEFAULT b'0' COMMENT '是否以删除',
   `version` int NULL DEFAULT 1 COMMENT '版本号',
   `init_by` int NOT NULL DEFAULT 0 COMMENT '创建人',
   `init_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   `last_upd_by` int NOT NULL DEFAULT 0 COMMENT '更新人',
   `last_upd_time` timestamp(3) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',

   PRIMARY KEY (`message_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;


CREATE TABLE IF NOT EXISTS `message_receive`  (
      `message_id` bigint UNSIGNED NOT NULL COMMENT '消息id',
      `group_id` bigint NULL DEFAULT 0 COMMENT '群id',
      `chat_type` enum('SINGLE','GROUP') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'SINGLE' COMMENT '聊天类型: 单聊或群聊',
      `sender_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '发送者id',
      `receiver_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '接收者id',
      `message_type` int NULL DEFAULT 0 COMMENT '消息类型',
      `request_type` int NULL DEFAULT 0 COMMENT '请求类型',
      `media_type` enum('TEXT','IMAGE_AND_TEXT','IMAGE','VOICE','VIDEO','EMOJI') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'TEXT' COMMENT '媒体类型',
      `sequence` bigint not NULL DEFAULT 0 COMMENT '消息序号',
      `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '消息内容',
      `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表情/图片/语音/视频等文件地址',
      `is_delivered` bit(1) NULL DEFAULT b'0' COMMENT '是否已投递当前接收人',

      `deleted` bit(1) NULL DEFAULT b'0' COMMENT '是否以删除',
      `version` int NULL DEFAULT 1 COMMENT '版本号',
      `init_by` int NOT NULL DEFAULT 0 COMMENT '创建人',
      `init_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
      `last_upd_by` int NOT NULL DEFAULT 0 COMMENT '更新人',
      `last_upd_time` timestamp(3) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
      PRIMARY KEY (`message_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;