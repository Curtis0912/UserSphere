create table user
(
    id           int auto_increment comment 'id'
        primary key,
    username     varchar(255)                       null comment '用户名',
    userAccount  varchar(255)                       null comment '登录账号',
    userPassword varchar(255)                       not null comment '密码',
    avatarUrl    varchar(255)                       null comment '头像',
    gender       int                                null comment '性别',
    phone        varchar(255)                       null comment '手机号',
    email        varchar(255)                       null comment '邮箱',
    userStatus   int      default 0                 null comment '用户状态 0-正常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDeleted    tinyint  default 0                 not null comment '是否删除 ',
    userRole     int      default 0                 not null comment '用户角色  0 - 普通用户   1 - 管理员'
);

