CREATE DATABASE IF NOT EXISTS ads_system
DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

USE ads_system;

/**t_admin管理员表*/
DROP TABLE IF EXISTS t_admin;
CREATE TABLE t_admin(
  id VARCHAR(128) PRIMARY KEY COMMENT '管理员id',
  email VARCHAR(100) NOT NULL UNIQUE COMMENT '登录邮箱',
  password VARCHAR(50) NOT NULL COMMENT '登录密码',
  name VARCHAR(20) NOT NULL COMMENT '姓名',
  phone VARCHAR(13) NOT NULL COMMENT '手机号，用空格或-隔开',
  create_time DATETIME DEFAULT current_timestamp COMMENT '创建时间',
  last_login_time DATETIME COMMENT '最近一次登录时间',
  login_time DATETIME COMMENT '登录时间',
  role VARCHAR(20) NOT NULL COMMENT '管理员角色',
  status VARCHAR(2) NOT NULL DEFAULT 'Y' COMMENT '管理员是否可用'
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

ALTER TABLE t_admin ADD CONSTRAINT ck_admin_status
CHECK (status in ('Y', 'N'));

ALTER TABLE t_admin ADD CONSTRAINT ck_admin_role
CHECK (role in('super', 'normal'));

INSERT INTO t_admin(id, email, password, name, phone, role) VALUES (uuid(), 'admin@126.com', '6khXbzC+FmmXFpnAmtBclA==', 'admin', '188-8888-8888', 'super');

/**t_customer客户信息表*/
DROP TABLE IF EXISTS t_customer;
CREATE TABLE t_customer (
  id VARCHAR(128) PRIMARY KEY COMMENT '客户id',
  email VARCHAR(100) NOT NULL UNIQUE COMMENT '登录邮箱',
  password VARCHAR(50) NOT NULL COMMENT '登录密码',
  check_pwd VARCHAR(50) NOT NULL COMMENT '消息发布审核密码',
  name VARCHAR(20) COMMENT '姓名',
  company VARCHAR(50) COMMENT '公司名称',
  address VARCHAR(100) COMMENT '地址',
  phone VARCHAR(13) COMMENT '手机号，用空格或-隔开',
  create_time DATETIME DEFAULT current_timestamp COMMENT '创建时间',
  last_login_time DATETIME COMMENT '最近一次登录时间',
  login_time DATETIME COMMENT '登录时间',
  last_update_time DATETIME COMMENT '最近一次修改时间',
  last_update_by_role VARCHAR(10) COMMENT '最近一次被哪个角色修改,可选admin或self',
  last_update_by_admin VARCHAR(128) COMMENT '最近一次被哪个管理员修改',
  status VARCHAR(2) NOT NULL DEFAULT 'Y' COMMENT '客户是否在可用状态'
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

/**为last_update_by_role添加检查约束,只能是admin或self*/
ALTER TABLE t_customer ADD CONSTRAINT ck_customer_last_update_by_role
CHECK(last_update_by_role in ('admin', 'self'));

/**如果last_update_by_role为admin,则说明由管理员修改了客户信息,此时需要记录管理员id到last_update_by_admin字段*/
ALTER TABLE t_customer ADD CONSTRAINT fk_last_update_by_admin
FOREIGN KEY(last_update_by_admin) REFERENCES t_admin(id);

ALTER TABLE t_customer ADD CONSTRAINT ck_customer_status
CHECK (status in ('Y', 'N'));

/**t_resource_type资源类型表*/
DROP TABLE IF EXISTS t_resource_type;
CREATE TABLE t_resource_type (
  id VARCHAR(128) PRIMARY KEY COMMENT '资源类型id',
  name VARCHAR(50) NOT NULL UNIQUE COMMENT '资源类型名称',
  extension VARCHAR(100) COMMENT '类型文件后缀',
  des VARCHAR(200) COMMENT '资源类型描述',
  show_detail_setting VARCHAR(2) NOT NULL DEFAULT 'Y' COMMENT '是否显示设置详情',
  create_time DATETIME DEFAULT current_timestamp COMMENT '资源类型创建时间',
  status VARCHAR(2) NOT NULL DEFAULT 'Y' COMMENT '资源类型是否在可用状态'
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

ALTER TABLE t_resource_type ADD CONSTRAINT ck_resource_type_status
CHECK (status in ('Y', 'N'));

/**t_resource资源表,每个用户的资源都不一样*/
DROP TABLE IF EXISTS t_resource;
CREATE TABLE t_resource(
  id VARCHAR(128) PRIMARY KEY COMMENT '资源id',
  name VARCHAR(100) NOT NULL COMMENT '资源名称',
  resource_type_id VARCHAR(128) NOT NULL COMMENT '资源类型id',
  path VARCHAR(500) NOT NULL DEFAULT '无' COMMENT '资源路径',
  full_path VARCHAR(500) NOT NULL DEFAULT '无' COMMENT '完整路径',
  ofile_name VARCHAR(100) NOT NULL DEFAULT '无' COMMENT '资源原件的原始名称',
  file_name VARCHAR(100) NOT NULL DEFAULT '无' COMMENT '资源文件的名称',
  file_size bigint NOT NULL DEFAULT 0 COMMENT '文件大小,以字节为单位',
  des VARCHAR(500) COMMENT '资源描述,或者当用户选择文本信息时保存的文本',
  create_time DATETIME DEFAULT current_timestamp COMMENT '资源添加的时间',
  customer_id VARCHAR(128) NOT NULL COMMENT '客户id',
  status VARCHAR(2) NOT NULL DEFAULT 'Y' COMMENT '资源是否可用'
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

ALTER TABLE t_resource ADD CONSTRAINT fk_resource_type_id
FOREIGN KEY (resource_type_id) REFERENCES t_resource_type(id);

ALTER TABLE t_resource ADD CONSTRAINT fk_resource_customer_id
FOREIGN KEY (customer_id) REFERENCES t_customer(id);

ALTER TABLE t_resource ADD CONSTRAINT ck_resource_status
CHECK (status in ('Y', 'N'));

DROP TABLE IF EXISTS t_version;
CREATE TABLE t_version(
  id VARCHAR(128) PRIMARY KEY COMMENT '版本编号',
  name VARCHAR(50) NOT NULL COMMENT '版本名称',
  area_count int NOT NULL COMMENT '区域数',
  path VARCHAR(500) NOT NULL COMMENT '区域说明图片路径',
  full_path VARCHAR(500) NOT NULL COMMENT '区域说明图片完整路径',
  ofile_name VARCHAR(100) NOT NULL COMMENT '区域说明图片的原始名称',
  file_name VARCHAR(100) NOT NULL COMMENT '区域说明图片的名称',
  des VARCHAR(500) COMMENT '描述信息',
  create_time DATETIME DEFAULT current_timestamp COMMENT '版本发布时间',
  status VARCHAR(2) NOT NULL DEFAULT 'Y' COMMENT '版本是否可用'
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

ALTER TABLE t_version ADD CONSTRAINT ck_version_status
CHECK (status in ('Y', 'N'));

/**t_device_group终端设备分组信息表,每个客户都可以有多个分组*/
DROP TABLE IF EXISTS t_device_group;
CREATE TABLE t_device_group(
  id VARCHAR(128) PRIMARY KEY COMMENT '终端分组',
  name VARCHAR(50) NOT NULL COMMENT '终端分组名称',
  des VARCHAR(200) COMMENT '终端分组描述',
  customer_id VARCHAR(128) NOT NULL COMMENT '终端分组属于哪个客户',
  create_time DATETIME DEFAULT current_timestamp COMMENT '终端分组创建时间',
  status VARCHAR(2) NOT NULL DEFAULT 'Y' COMMENT '设备分组是否可用'
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

ALTER TABLE t_device_group ADD CONSTRAINT fk_device_group_customer_id
FOREIGN KEY (customer_id) REFERENCES t_customer(id);

ALTER TABLE t_device_group ADD CONSTRAINT ck_device_group_status
CHECK (status in ('Y', 'N'));

/**t_device终端设备表,每个客户可以有多个终端设备*/
DROP TABLE IF EXISTS t_device;
CREATE TABLE t_device(
  id VARCHAR(128) PRIMARY KEY COMMENT '终端设备id',
  code VARCHAR(200) NOT NULL COMMENT '终端号',
  des VARCHAR(500) COMMENT '终端设备描述信息',
  version_id VARCHAR(128) NOT NULL COMMENT '终端设备的版本号',
  install_time DATETIME COMMENT '安装时间',
  bus_no VARCHAR(10) COMMENT '公交车路数',
  bus_plate_no VARCHAR(10) COMMENT '公交车牌号',
  driver VARCHAR(50) COMMENT '驾驶员',
  phone VARCHAR(13) COMMENT '手机号，用空格或-隔开',
  customer_id VARCHAR(128) NOT NULL COMMENT '客户id',
  device_group_id VARCHAR(128) COMMENT '终端设备分组id',
  create_time DATETIME DEFAULT current_timestamp COMMENT '终端添加时间',
  ads_update_time DATETIME COMMENT '广告最近一次更新时间',
  status VARCHAR(2) NOT NULL DEFAULT 'Y' COMMENT '终端是否可用',
  online VARCHAR(2) NOT NULL DEFAULT 'N' COMMENT '终端是否在线,默认为不在线',
  online_time DATETIME COMMENT '上线时间',
  offline_time DATETIME COMMENT '离线时间'
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

ALTER TABLE t_device ADD CONSTRAINT fk_device_version_id
FOREIGN KEY (version_id) REFERENCES t_version(id);

ALTER TABLE t_device ADD CONSTRAINT fk_device_customer_id
FOREIGN KEY (customer_id) REFERENCES t_customer(id);

ALTER TABLE t_device ADD CONSTRAINT fk_device_group_id
FOREIGN KEY (device_group_id) REFERENCES t_device_group(id);

ALTER TABLE t_device ADD CONSTRAINT ck_device_status
CHECK (status in ('Y', 'N'));

ALTER TABLE t_device ADD CONSTRAINT ck_device_online
CHECK (online in ('Y', 'N'));

/**t_publish_plan发布计划表,三种发布计划，单个终端,分组终端和全部终端*/
DROP TABLE IF EXISTS t_publish_plan;
CREATE TABLE t_publish_plan(
  id VARCHAR(128) PRIMARY KEY COMMENT '发布计划编号',
  plan_name VARCHAR(50) COMMENT '计划名称',
  name VARCHAR(50) NOT NULL COMMENT '发布计划的名称,如果是单个终端则为终端号,多个终端,分组终端和全部终端',
  customer_id VARCHAR(128) NOT NULL COMMENT '客户id',
  group_name VARCHAR(50) COMMENT '终端分组名称',
  version_id VARCHAR(128) COMMENT '终端版本号',
  version_name VARCHAR(50) COMMENT '版本名',
  type VARCHAR(20) NOT NULL COMMENT '计划类型,单个终端,多个终端,分组终端,全部终端',
  dev_count INT COMMENT '终端总数',
  finish_count INT COMMENT '完成消息发布终端总数',
  not_finish_count INT COMMENT '未完成消息发布终端总数',
  des VARCHAR(500) COMMENT '描述信息',
  submit_check_time DATETIME COMMENT '审核提交时间',
  check_comment VARCHAR(200) COMMENT '审核批注',
  check_time DATETIME COMMENT '审核时间',
  check_status VARCHAR(10) NOT NULL DEFAULT 'not_submit' COMMENT '审核状态',
  status VARCHAR(2) NOT NULL DEFAULT 'Y' COMMENT '是否在用或删除',
  create_time DATETIME DEFAULT current_timestamp COMMENT '创建时间'
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

ALTER TABLE t_publish_plan ADD CONSTRAINT fk_pub_plan_customer_id
FOREIGN KEY (customer_id) REFERENCES t_customer(id);

ALTER TABLE t_publish_plan ADD CONSTRAINT fk_pub_plan_version_id
FOREIGN KEY (version_id) REFERENCES t_version(id);

ALTER TABLE t_publish_plan ADD CONSTRAINT ck_publish_type
CHECK (type in ('one', 'multiple', 'group', 'all'));

ALTER TABLE t_publish_plan ADD CONSTRAINT ck_publish_plan_check_status
CHECK (check_status in ('not_submit', 'checking', 'checked', 'finish'));

ALTER TABLE t_publish_plan ADD CONSTRAINT ck_publish_plan_status
CHECK (status in ('Y', 'N'));

/**t_publish设备资源表,不同的资源下发到不同的设备*/
DROP TABLE IF EXISTS t_publish;
CREATE TABLE t_publish(
  id VARCHAR(128) PRIMARY KEY COMMENT '终端设备与资源关联id',
  device_id VARCHAR(128) COMMENT '终端设备id',
  resource_id VARCHAR(128) COMMENT '资源id',
  publish_log VARCHAR(50) NOT NULL COMMENT '发布日志记录',
  publish_time DATETIME COMMENT '资源下发时间',
  delete_time DATETIME NULL COMMENT '广告删除时间',
  pub_plan_id VARCHAR(128) NOT NULL COMMENT '发布计划编号',
  area INT NOT NULL COMMENT '屏幕区域',
  show_type VARCHAR(20) NOT NULL COMMENT '显示方式，包括即时显示,定时显示(定时),不定时显示(顺序)',
  start_time DATETIME COMMENT '定时播放的开始时间',
  end_time DATETIME COMMENT '定时播放的结束时间',
  stay_time VARCHAR(10) COMMENT '停留时间,以秒为单位',
  show_count VARCHAR(10) COMMENT '播放次数',
  segments VARCHAR(500) COMMENT '所有时段组成的字符串'
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

ALTER TABLE t_publish ADD CONSTRAINT fk_publish_device_id
FOREIGN KEY (device_id) REFERENCES t_device(id);

ALTER TABLE t_publish ADD CONSTRAINT fk_publish_resource_id
FOREIGN KEY (resource_id) REFERENCES t_resource(id);

ALTER TABLE t_publish ADD CONSTRAINT fk_publish_pub_plan_id
FOREIGN KEY (pub_plan_id) REFERENCES t_publish_plan(id);

ALTER TABLE t_publish ADD CONSTRAINT ck_publish_show_type
CHECK (show_type in ('now', 'order', 'segment'));

