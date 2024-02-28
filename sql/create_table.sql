-- auto-generated definition
-- auto-generated definition
create table user
(
    id               bigint                       not null comment '主键ID'
        primary key,
    username         varchar(50)                  null,
    avatarUrl        varchar(256) charset latin1  null comment '头像',
    userAccount      varchar(256) charset latin1  null comment '登录账号',
    gender           tinyint default 0            not null comment '性别(男-0/女-1)',
    userPassword     varchar(256) charset latin1  not null comment '用户密码',
    phone            varchar(256) charset latin1  null comment '手机号',
    email            varchar(256) charset latin1  null comment '邮箱',
    userStatus       int     default 0            not null comment '用户状态(0 启用,1 禁用)',
    createTime       datetime                     null comment '创建时间',
    updateTime       datetime                     null comment '修改时间',
    isDelete         int     default 1            null comment '是否删除(0 是,1 否)',
    userRole         tinyint default 0            null comment '用户角色 0 代表普通角色 1代表管理员',
    tags             varchar(1024) charset latin1 null comment '标签列表',
    connectionId     bigint                       null comment '关注id',
    connectionIdList varchar(1024) charset latin1 null comment '多个关注用户Id'
)
    charset = utf8;

create index user__index_connectionId
    on user (connectionId);

create index user_id_index
    on user (id);




-- auto-generated definition
create table tag
(
    id         bigint            not null comment '主键ID'
        primary key,
    tagName    varchar(256)      null comment '标签名称',
    userId     bigint            null comment '用户 id',
    parentId   bigint            null comment '父级 id',
    isParent   tinyint default 0 not null comment '父标签(不-0/是-1)',
    createTime datetime          null comment '创建时间',
    updateTime datetime          null comment '修改时间',
    isDelete   int     default 1 null comment '是否删除(0 是,1 否)',
    constraint unildx_tagName
        unique (tagName)
);

create index idx_userId
    on tag (id);


-- auto-generated definition
create table team
(
    id          bigint auto_increment comment 'id'
        primary key,
    name        varchar(50) charset utf8           null,
    description varchar(50) charset utf8           null,
    maxNum      int      default 1                 not null comment '最大人数',
    expireTime  datetime                           null comment '过期时间',
    userId      bigint                             null comment '用户id',
    status      int      default 0                 not null comment '0 - 公开，1 - 私有，2 - 加密',
    password    varchar(512)                       null comment '密码',
    createTime  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete    int      default 1                 null comment '是否删除(0 是,1 否)'
)
    comment '队伍';

-- auto-generated definition
create table user_team
(
    id         bigint auto_increment comment 'id'
        primary key,
    userId     bigint                             null comment '用户id',
    teamId     bigint                             null comment '队伍id',
    joinTime   datetime                           null comment '加入时间',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete   int      default 1                 null comment '是否删除(0 是,1 否)'
)
    comment '用户队伍关系';

-- auto-generated definition
create table information
(
    id         bigint auto_increment comment 'id'
        primary key,
    userId     bigint                             null comment '用户id',
    teamId     bigint                             null comment '队伍id',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete   int      default 1                 null comment '是否删除(0 是,1 否)'
)
    comment '消息主表';

-- auto-generated definition
create table chat
(
    chatId        bigint                             null comment '聊天id',
    id            bigint auto_increment comment 'id'
        primary key,
    informationId bigint                             null comment '聊天主表id',
    userId        bigint                             null comment '用户id',
    anotherId     bigint                             null comment '对方id',
    isOnline      int      default 1                 not null comment '是否在线(0 是,1 否)',
    unread        int                                null comment '未读数量',
    time          datetime default CURRENT_TIMESTAMP null comment '发送消息时间',
    content       varchar(50) charset utf8           null,
    createTime    datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isLatest      int      default 1                 not null comment '是否是最后一个消息(0 是,1 否)',
    isDelete      int      default 1                 null comment '是否删除(0 是,1 否)',
    avatarUrl     varchar(256)                       null comment '头像',
    username      varchar(50) charset utf8           null,
    isConnection  int      default 1                 null comment '0 已连接 1 未连接 (是否第一次建立连接)'
)
    comment '聊天列表';

