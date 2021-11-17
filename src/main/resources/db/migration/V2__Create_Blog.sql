create table blog (
    id bigint primary key auto_increment,
    user_id bigint,
    title varchar(100),
    description varchar(100),
    content text,
    updated_at datetime,
    created_at datetime
)