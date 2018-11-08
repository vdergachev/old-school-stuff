create table GKLIENTS_CLIENT_DISTRIBUTION_SETTINGS (
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
    IS_COMPANY boolean,
    IS_IP boolean,
    IS_CIVIL boolean,
    IS_WITH_WORKERS boolean,
    IS_WITHOUT_WORKERS boolean,
    IS_OSNO boolean,
    IS_USN boolean,
    IS_ENVD boolean,
    IS_ESHN boolean,
    IS_PSN boolean,
    MARKER varchar(255),
    --
    primary key (ID)
);
