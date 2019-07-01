/*==============================================================*/
/* Table: broker_message_log                                    */
/*==============================================================*/
create table broker_message_log
(
   message_id           varchar(32) not null,
   message              varchar(400),
   try_count            int(5),
   status               varchar(10),
   next_retry           timestamp,
   create_time          timestamp not null,
   update_time          timestamp not null,
   primary key (message_id)
);

/*==============================================================*/
/* Table: t_customer_account                                    */
/*==============================================================*/
create table t_customer_account
(
   account_id           varchar(32) not null,
   account_no           varchar(32),
   date_time            timestamp,
   current_balance      decimal(15,2),
   version              int(10),
   create_time          timestamp not null,
   update_time          timestamp not null,
   primary key (account_id)
);

/*==============================================================*/
/* Table: t_order                                               */
/*==============================================================*/
create table t_order
(
   order_id             varchar(32) not null,
   order_type           varchar(10),
   city_id              varchar(32),
   platform_id          varchar(32),
   user_id              varchar(32),
   supplier_id          varchar(32),
   goods_id             varchar(32),
   order_status         varchar(32),
   remark               varchar(200),
   create_by            varchar(50) not null,
   create_time          timestamp not null,
   update_by            varchar(50) not null,
   update_time          timestamp not null,
   primary key (order_id)
);

/*==============================================================*/
/* Index: order_index                                           */
/*==============================================================*/
create unique index order_index on t_order
(
   order_id
);

/*==============================================================*/
/* Table: t_package                                             */
/*==============================================================*/
create table t_package
(
   package_id           varchar(32) not null,
   order_id             varchar(32),
   supplier_id          varchar(32),
   address_id           varchar(32),
   remark               varchar(40),
   package_status       varchar(10),
   create_time          timestamp not null,
   update_time          timestamp not null,
   primary key (package_id)
);

/*==============================================================*/
/* Table: t_platform_account                                    */
/*==============================================================*/
create table t_platform_account
(
   account_id           varchar(32) not null,
   account_no           varchar(32),
   date_time            timestamp,
   current_balance      decimal(15,2),
   version              int(10),
   create_time          timestamp not null,
   update_time          timestamp not null,
   primary key (account_id)
);

/*==============================================================*/
/* Table: t_store                                               */
/*==============================================================*/
create table t_store
(
   store_id             varchar(32) not null,
   goods_id             varchar(32),
   supplier_id          varchar(32),
   goods_name           varchar(40),
   store_count          int(10) not null,
   version              int(10) not null,
   create_by            varchar(50) not null,
   create_time          timestamp not null,
   update_by            varchar(50) not null,
   update_time          timestamp not null,
   primary key (store_id)
);

