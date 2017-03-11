ALTER TABLE t_customer ADD check_pwd VARCHAR(50) NULL COMMENT '消息发布审核密码';

/**所有用户的审核密码都初始设置成123456*/
update t_customer set check_pwd = '6khXbzC+FmmXFpnAmtBclA==';

/**t_publis表增加删除时间*/
ALTER TABLE t_publish ADD delete_time DATETIME NULL COMMENT '广告删除时间';
