CREATE DATABASE IF NOT EXISTS ads_system
DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

USE ads_system;

--t_admin管理员表
DROP TABLE IF EXISTS t_admin;
CREATE TABLE t_admin(
  id VARCHAR(128) PRIMARY KEY COMMENT '管理员id',
  email VARCHAR(100) NOT NULL UNIQUE COMMENT '登录邮箱',
  password VARCHAR(50) NOT NULL COMMENT '登录密码',
  name VARCHAR(20) NOT NULL COMMENT '姓名',
  phone VARCHAR(11) NOT NULL COMMENT '手机号',
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

--t_customer客户信息表
DROP TABLE IF EXISTS t_customer;
CREATE TABLE t_customer (
  id VARCHAR(128) PRIMARY KEY COMMENT '客户id',
  email VARCHAR(100) NOT NULL UNIQUE COMMENT '登录邮箱',
  password VARCHAR(50) NOT NULL COMMENT '登录密码',
  name VARCHAR(20) COMMENT '姓名',
  address VARCHAR(20) COMMENT '地址',
  phone VARCHAR(11) COMMENT '手机号',
  create_time DATETIME DEFAULT current_timestamp COMMENT '创建时间',
  last_login_time DATETIME COMMENT '最近一次登录时间',
  login_time DATETIME COMMENT '登录时间',
  last_update_time DATETIME COMMENT '最近一次修改时间',
  last_update_by_role VARCHAR(10) COMMENT '最近一次被哪个角色修改,可选admin或self',
  last_update_by_admin VARCHAR(128) COMMENT '最近一次被哪个管理员修改',
  status VARCHAR(2) NOT NULL DEFAULT 'Y' COMMENT '客户是否在可用状态'
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

--为last_update_by_role添加检查约束,只能是admin或self
ALTER TABLE t_customer ADD CONSTRAINT ck_customer_last_update_by_role
CHECK(last_update_by_role in ('admin', 'self'));

--如果last_update_by_role为admin,则说明由管理员修改了客户信息,此时需要记录管理员id到last_update_by_admin字段
ALTER TABLE t_customer ADD CONSTRAINT fk_last_update_by_admin
FOREIGN KEY(last_update_by_admin) REFERENCES t_admin(id);

ALTER TABLE t_customer ADD CONSTRAINT ck_customer_status
CHECK (status in ('Y', 'N'));

--t_resource_type资源类型表
DROP TABLE IF EXISTS t_resource_type;
CREATE TABLE t_resource_type (
  id VARCHAR(128) PRIMARY KEY COMMENT '资源类型id',
  name VARCHAR(50) NOT NULL UNIQUE COMMENT '资源类型名称',
  des VARCHAR(200) COMMENT '资源类型描述',
  create_time DATETIME DEFAULT current_timestamp COMMENT '资源类型创建时间',
  status VARCHAR(2) NOT NULL DEFAULT 'Y' COMMENT '资源类型是否在可用状态'
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

--t_resource资源表,每个用户的资源都不一样
DROP TABLE IF EXISTS t_resource;
CREATE TABLE t_resource(
  id VARCHAR(128) PRIMARY KEY COMMENT '资源id',
  name VARCHAR(100) NOT NULL COMMENT '资源名称',
  resource_type_id VARCHAR(128) NOT NULL COMMENT '资源类型id',
  path VARCHAR(500) NOT NULL COMMENT '资源路径',
  file_name VARCHAR(100) NOT NULL COMMENT '资源文件的名称',
  des VARCHAR(500) COMMENT '资源描述,或者当用户选择文本信息时保存的文本',
  create_time DATETIME DEFAULT current_timestamp COMMENT '资源添加的时间',
  customer_id VARCHAR(128) NOT NULL COMMENT '客户id'
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

ALTER TABLE t_resource ADD CONSTRAINT fk_resource_type_id
FOREIGN KEY (resource_type_id) REFERENCES t_resource_type(id);

ALTER TABLE t_resource ADD CONSTRAINT fk_resource_customer_id
FOREIGN KEY (customer_id) REFERENCES t_customer(id);

--t_device_group终端设备分组信息表,每个客户都可以有多个分组
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

--t_device终端设备表,每个客户可以有多个终端设备
DROP TABLE IF EXISTS t_device;
CREATE TABLE t_device(
  id VARCHAR(128) PRIMARY KEY COMMENT '终端设备id',
  name VARCHAR(50) NOT NULL COMMENT '终端设备名称',
  des VARCHAR(500) COMMENT '终端设置描述信息',
  install_time DATETIME COMMENT '安装时间',
  bus_no VARCHAR(10) COMMENT '公交车路数',
  bus_plate_no VARCHAR(10) COMMENT '公交车牌号',
  area VARCHAR(50) COMMENT '运行区域',
  customer_id VARCHAR(128) NOT NULL COMMENT '客户id',
  create_time DATETIME DEFAULT current_timestamp COMMENT '终端添加时间'
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

ALTER TABLE t_device ADD CONSTRAINT fk_device_customer_id
FOREIGN KEY (customer_id) REFERENCES t_customer(id);

--t_device_resource设备资源表,不同的资源下发到不同的设备
DROP TABLE IF EXISTS t_device_resource;
CREATE TABLE t_device_resource(
  id VARCHAR(128) PRIMARY KEY COMMENT '终端设备与资源关联id',
  device_id VARCHAR(128) COMMENT '终端设备id',
  device_group_id VARCHAR(128) COMMENT '终端设备分组id',
  resource_id VARCHAR(128) NOT NULL COMMENT '资源id',
  show_type VARCHAR(2) NOT NULL COMMENT '显示方式，包括即时显示，定时显示,不定时显示',
  start_time DATETIME COMMENT '定时播放的开始时间',
  end_time DATETIME COMMENT '定时播放的结束时间',
  stay_time INT COMMENT '停留时间,以秒为单位',
  check_comment VARCHAR(200) COMMENT '审核批注',
  checked VARCHAR(2) NOT NULL DEFAULT 'N' COMMENT '是否审核通过',
  create_time DATETIME DEFAULT current_timestamp COMMENT '资源发布添加时间',
  publish_time DATETIME COMMENT '资源下发时间',
  status VARCHAR(2) NOT NULL DEFAULT 'Y' COMMENT '资源状态,是否在用或删除'
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

ALTER TABLE t_device_resource ADD CONSTRAINT ck_device_resource_checked
CHECK (checked in ('Y', 'N'));

ALTER TABLE t_device_resource ADD CONSTRAINT ck_device_resource_status
CHECK (status in ('Y', 'N'));

ALTER TABLE t_device_resource ADD CONSTRAINT fk_device_resource_device_id
FOREIGN KEY (device_id) REFERENCES t_device(id);

ALTER TABLE t_device_resource ADD CONSTRAINT fk_device_resource_device_group_id
FOREIGN KEY (device_group_id) REFERENCES t_device_group(id);

ALTER TABLE t_device_resource ADD CONSTRAINT fk_device_resource_resource_id
FOREIGN KEY (resource_id) REFERENCES t_resource(id);

