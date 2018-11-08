create table GKLIENTS_GK_NEWS_IMAGE (
    ID uuid,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    --
    SITE_ID integer,
    IMAGE varchar(255),
    GK_NEWS_ID uuid,
    --
    primary key (ID)
);
