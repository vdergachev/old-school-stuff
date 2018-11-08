create table GKLIENTS_GK_NEWS (
    ID uuid,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    --
    SITE_ID integer,
    TITLE varchar(255),
    URL text,
    LEAD_TEXT text,
    FULL_TEXT text,
    NEWS_DATE timestamp,
    PARAMS text,
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
    --
    primary key (ID)
);
