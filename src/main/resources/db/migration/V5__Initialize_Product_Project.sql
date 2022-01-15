create table project (
     id bigint primary key auto_increment,
     process_id varchar(64),
     name varchar(100),
     number varchar(100),
     type varchar(50),
     attachment varchar(100),
     ownerId bigint
);

create table product (
     id bigint primary key auto_increment,
     process_id varchar(64),
     userId bigint,
     percentage decimal,
     product decimal
);