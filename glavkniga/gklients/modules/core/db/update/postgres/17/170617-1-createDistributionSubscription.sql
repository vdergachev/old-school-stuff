create table GKLIENTS_DISTRIBUTION_SUBSCRIPTION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    DISTRIBUTION_ID uuid,
    CLIENT_ID uuid,
    DATE_BEGIN date,
    DATE_STATUS_UPDATE timestamp,
    STATUS integer,
    --
    primary key (ID)
);
