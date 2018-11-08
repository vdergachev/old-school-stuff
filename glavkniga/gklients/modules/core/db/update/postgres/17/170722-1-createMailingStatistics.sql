create table GKLIENTS_MAILING_STATISTICS (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ONETIME_MAILING_ID uuid,
    PLANNED integer,
    SENDING integer,
    COMPLETED integer,
    FAILED integer,
    --
    primary key (ID)
);
