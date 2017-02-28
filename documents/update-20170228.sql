ALTER TABLE t_customer ADD check_pwd VARCHAR(50) NULL COMMENT '消息发布审核密码';

/**所有用户的审核密码都初始设置成123456*/
update t_customer set check_pwd = '6khXbzC+FmmXFpnAmtBclA==';