create table GKLIENTS_FAILED_EMAILS (
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
    ONETIME_MAILING_ID uuid,
    MAILING_STATISTICS_ID uuid,
    REASON varchar(255),
    --
    primary key (ID)
);
