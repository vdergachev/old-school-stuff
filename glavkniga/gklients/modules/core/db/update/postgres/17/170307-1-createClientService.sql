create table GKLIENTS_CLIENT_SERVICE (
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
    SERVICE varchar(3),
    ACTIVATION_DATE timestamp,
    --
    primary key (ID)
);
