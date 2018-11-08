create table GKLIENTS_DISTRIBUTION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255),
    DESCRIPTION text,
    TEMPLATE_ID uuid,
    SUBJECT varchar(255),
    CONTENT text,
    CREATE_ALLOWED boolean,
    SIGNED_BY_DEFAULT boolean,
    PERSONAL boolean,
    IMPORTANT boolean,
    STATUS integer,
    --
    primary key (ID)
);
