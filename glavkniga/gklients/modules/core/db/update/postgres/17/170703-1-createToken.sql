create table GKLIENTS_TOKEN (
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
    DESCRIPTION varchar(255),
    TOKEN varchar(255),
    ENTITY_NAME varchar(255),
    ENTITY_FIELD varchar(255),
    --
    primary key (ID)
);
