create table GKLIENTS_ONETIME_MAILING (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    CLIENT_ID uuid,
    SUBJECT varchar(255),
    BODY_ text,
    SENDING_DATE timestamp,
    STATUS integer,
    IMPORTANT boolean,
    PERSONAL boolean,
    --
    primary key (ID)
);
