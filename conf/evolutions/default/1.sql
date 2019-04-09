# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table event (
  id                            bigint auto_increment not null,
  type                          integer,
  ean                           varchar(255),
  constraint ck_event_type check ( type in (0,1,2,3,4,5,6)),
  constraint pk_event primary key (id)
);

create table product (
  ean                           varchar(255) not null,
  name                          varchar(255),
  description                   varchar(255),
  picture                       varbinary(883647),
  picture_url                   varchar(255),
  constraint pk_product primary key (ean)
);


# --- !Downs

drop table if exists event;

drop table if exists product;
