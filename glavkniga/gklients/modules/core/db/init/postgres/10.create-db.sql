-- begin gklients_ORIGIN
create table gklients_ORIGIN (
    ID uuid,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ORIGIN_TEXT varchar(255),
    --
    primary key (ID)
)^-- end gklients_ORIGIN
-- begin GKLIENTS_TARIF
create table gklients_TARIF (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TARIF_NAME varchar(25),
    TARIF_PRICE double precision,
    TARIF_NUMBER integer not null,
    --
    primary key (ID)
)^-- end GKLIENTS_TARIF
-- begin GKLIENTS_MAGAZINE_ISSUE
create table gklients_MAGAZINE_ISSUE (
    ID uuid,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    MAGAZINE_ID uuid,
    MONTH_ varchar(2),
    YEAR_ varchar(4),
    NUMBER_ varchar(10),
    CODE varchar(20),
    ISSUE_PLANNED_DATE date,
    PRICE double precision,
    --
    primary key (ID)
)^-- end GKLIENTS_MAGAZINE_ISSUE
-- begin GKLIENTS_EMAIL_TEMPLATE
create table gklients_EMAIL_TEMPLATE (
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
    SUBJECT varchar(255) not null,
    BODY_ text not null,
    EMAIL_TYPE integer not null,
    --
    primary key (ID)
)^
-- end GKLIENTS_EMAIL_TEMPLATE
-- begin GKLIENTS_SCHEDULE
create table gklients_SCHEDULE (
    ID uuid,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    MAGAZINE_ISSUE_ID uuid,
    COMMENTS varchar(255),
    ISSUE_START date,
    ISSUE_FINISH date,
    ISSUE_SIGN date,
    PRINTING_RECEVIE date,
    SITE_UPLOAD date,
    DELIVERY_DATE date,
    --
    primary key (ID)
)^-- end GKLIENTS_SCHEDULE

-- begin GKLIENTS_ELVER_SUBSCRIPTION
create table GKLIENTS_ELVER_SUBSCRIPTION (
    ID uuid,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    CLIENT_ID uuid,
    RIZ_ID uuid,
    REGKEY varchar(16),
    REQUEST_DATE date,
    TARIF_ID uuid,
    ISSUE_START_ID uuid,
    ISSUE_END_ID uuid,
    IS_REG_KEY_SENT_TO_RIZ boolean,
    IS_REG_KEY_USED boolean,
    IS_PASS_SENT_TO_CUSTOMER boolean,
    IS_TARIF_CHECKED boolean,
    ACTIVATION_DATE date,
    --
    primary key (ID)
)^-- end GKLIENTS_ELVER_SUBSCRIPTION
-- begin GKLIENTS_CLIENT
create table GKLIENTS_CLIENT (
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
    EMAIL varchar(255),
    PASSWORD varchar(50),
    ITN varchar(20),
    PHONE varchar(255),
    EMAIL_HASH varchar(255),
    PASSWORD_HASH varchar(32),
    BAD_EMAIL boolean,
    IS_BLOCKED boolean,
    ORIGIN_ID uuid,
    --
    primary key (ID)
)^
-- end GKLIENTS_CLIENT
-- begin GKLIENTS_RIZ
create table gklients_RIZ (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NUMBER_ integer,
    NAME varchar(300),
    CITY varchar(50),
    PODHOST_ID uuid,
    PHONE varchar(300),
    ELVER_EMAIL varchar(300),
    DELIVERY_ADDRESS varchar(300),
    DELIVERY_EMAIL varchar(300),
    DELIVERY_COMPANY varchar(50),
    ORDER_PERSON varchar(300),
    ORDER_PHONE varchar(300),
    ORDER_EMAIL varchar(300),
    ORDER_ACCOUNTIST varchar(300),
    PR_PERSON varchar(300),
    PR_PHONE varchar(300),
    PR_EMAIL varchar(300),
    DISTR_PERSON varchar(300),
    DISTR_EMAIL varchar(300),
    WS_NAME varchar(255),
    WS_PERSON varchar(255),
    WS_ADDRESS varchar(300),
    WS_PHONE varchar(300),
    WS_EMAIL varchar(300),
    WS_WEBSITE varchar(255),
    WS_TERRITORY varchar(5000),
    DISCOUNT double precision,
    MAIL_NAME varchar(255),
    MAIL_PERSON varchar(255),
    MAIL_EMAIL varchar(255),
    MAIL_PHONE varchar(50),
    --
    primary key (ID)
)^
-- end GKLIENTS_RIZ
-- begin GKLIENTS_MUN_OBRAZ
create table GKLIENTS_MUN_OBRAZ (
    ID uuid,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    OKTMO varchar(20),
    TITLE varchar(255),
    CAPITAL varchar(255),
    REGION varchar(50),
    OSM_ID varchar(255),
    --
    primary key (ID)
)^-- end GKLIENTS_MUN_OBRAZ
-- begin GKLIENTS_RIZ_MUN_OBRAZ_LINK
create table GKLIENTS_RIZ_MUN_OBRAZ_LINK (
    RIZ_ID uuid,
    MUN_OBRAZ_ID uuid,
    primary key (RIZ_ID, MUN_OBRAZ_ID)
)^
-- end GKLIENTS_RIZ_MUN_OBRAZ_LINK
-- begin GKLIENTS_RIZ_CONTRACT
create table GKLIENTS_RIZ_CONTRACT (
    ID uuid,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    RIZ_ID uuid,
    CONTRACT_NUMBER varchar(255),
    CONTRACT_DATE date,
    --
    primary key (ID)
)^
-- end GKLIENTS_RIZ_CONTRACT
-- begin GKLIENTS_MAGAZINE
create table gklients_MAGAZINE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    MAGAZINE_NAME varchar(50),
    MAGAZINE_ABB varchar(5),
    MAGAZINE_I_D integer not null,
    --
    primary key (ID)
)^
-- end GKLIENTS_MAGAZINE

-- begin GKLIENTS_TEST
create table GKLIENTS_TEST (
    ID uuid,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer not null,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    TEST_ID integer,
    TEST_NAME varchar(255),
    --
    primary key (ID)
)^
-- end GKLIENTS_TEST
-- begin GKLIENTS_TEST_EMAIL
create table GKLIENTS_TEST_EMAIL (
    ID uuid,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer not null,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    EMAIL varchar(255),
    DATE_SAVE timestamp,
    EMAIL_INDEX varchar(255),
    --
    primary key (ID)
)^
-- end GKLIENTS_TEST_EMAIL
-- begin GKLIENTS_TEST_MARK
create table GKLIENTS_TEST_MARK (
    ID uuid,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer not null,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    MARK double precision,
    DATE_PASS timestamp,
    TEST_INDEX integer,
    EMAIL_INDEX varchar(255),
    TEST_ID uuid,
    EMAIL_ID uuid,
    MARK_INDEX integer,
    --
    primary key (ID)
)^
-- end GKLIENTS_TEST_MARK

-- begin GKLIENTS_CLIENT_SERVICE
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
)^
-- end GKLIENTS_CLIENT_SERVICE
-- begin GKLIENTS_WEBSTIE_QUEUE
create table GKLIENTS_WEBSTIE_QUEUE (
    ID uuid,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    --
    ENTITY_ID uuid,
    ENTITY_NAME varchar(255),
    OPERATION integer,
    --
    primary key (ID)
)^
-- end GKLIENTS_WEBSTIE_QUEUE
-- begin GKLIENTS_WEBSITE_QUEUE_FILTER
create table GKLIENTS_WEBSITE_QUEUE_FILTER (
    ID uuid,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    --
    WEBSITE_QUEUE_ITEM_ID uuid,
    PARAM_NAME text,
    VALUE_ text,
    --
    primary key (ID)
)^
-- end GKLIENTS_WEBSITE_QUEUE_FILTER
-- begin GKLIENTS_CLIENT_DISTRIBUTION_SETTINGS
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
)^
-- end GKLIENTS_CLIENT_DISTRIBUTION_SETTINGS
-- begin GKLIENTS_DISTRIBUTION
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
)^
-- end GKLIENTS_DISTRIBUTION
-- begin GKLIENTS_DISTRIBUTION_SUBSCRIPTION
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
    EMAIL varchar(255),
    --
    primary key (ID)
)^
-- end GKLIENTS_DISTRIBUTION_SUBSCRIPTION
-- begin GKLIENTS_ONETIME_MAILING
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
    SUBJECT varchar(255),
    BODY_ text,
    SENDING_DATE timestamp,
    STATUS integer,
    IMPORTANT boolean,
    PERSONAL boolean,
    TEMPLATE_ID uuid,
    --
    primary key (ID)
)^
-- end GKLIENTS_ONETIME_MAILING
-- begin GKLIENTS_ONETIME_MAILING_CLIENT_LINK
create table GKLIENTS_ONETIME_MAILING_CLIENT_LINK (
    ONETIME_MAILING_ID uuid,
    CLIENT_ID uuid,
    primary key (ONETIME_MAILING_ID, CLIENT_ID)
)^
-- end GKLIENTS_ONETIME_MAILING_CLIENT_LINK
-- begin GKLIENTS_ATTACHMENT
create table GKLIENTS_ATTACHMENT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    FILE_ID uuid,
    URL text,
    ATTACHMENT_METHOD integer not null,
    --
    primary key (ID)
)^
-- end GKLIENTS_ATTACHMENT
-- begin GKLIENTS_TOKEN
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
    IS_PERSONAL boolean,
    --
    primary key (ID)
)^
-- end GKLIENTS_TOKEN
-- begin GKLIENTS_EMAIL_TEMPLATE_ATTACHMENT_LINK
create table GKLIENTS_EMAIL_TEMPLATE_ATTACHMENT_LINK (
    ATTACHMENT_ID uuid,
    EMAIL_TEMPLATE_ID uuid,
    primary key (ATTACHMENT_ID, EMAIL_TEMPLATE_ID)
)^
-- end GKLIENTS_EMAIL_TEMPLATE_ATTACHMENT_LINK


-- begin GKLIENTS_DISTRIBUTION_ATTACHMENT_LINK
create table GKLIENTS_DISTRIBUTION_ATTACHMENT_LINK (
    ATTACHMENT_ID uuid,
    DISTRIBUTION_ID uuid,
    primary key (ATTACHMENT_ID, DISTRIBUTION_ID)
)^
-- end GKLIENTS_DISTRIBUTION_ATTACHMENT_LINK
-- begin GKLIENTS_ONETIME_MAILING_ATTACHMENT_LINK
create table GKLIENTS_ONETIME_MAILING_ATTACHMENT_LINK (
    ATTACHMENT_ID uuid,
    ONETIME_MAILING_ID uuid,
    primary key (ATTACHMENT_ID, ONETIME_MAILING_ID)
)^
-- end GKLIENTS_ONETIME_MAILING_ATTACHMENT_LINK

-- begin GKLIENTS_GK_NEWS
create table GKLIENTS_GK_NEWS (
    ID uuid,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    SITE_ID integer,
    TITLE varchar(255),
    URL text,
    LEAD_TEXT text,
    FULL_TEXT text,
    NEWS_DATE timestamp,
    PARAMS text,
    IMAGE varchar(255),
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
    IS_OTHER boolean,
    IS_SENT boolean,
    --
    primary key (ID)
)^
-- end GKLIENTS_GK_NEWS
-- begin GKLIENTS_GK_NEWS_IMAGE
create table GKLIENTS_GK_NEWS_IMAGE (
    ID uuid,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    --
    SITE_ID integer,
    IMAGE varchar(255),
    GK_NEWS_ID uuid,
    RECORD_ID integer,
    --
    primary key (ID)
)^
-- end GKLIENTS_GK_NEWS_IMAGE
-- begin GKLIENTS_MAILING_STATISTICS
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
)^
-- end GKLIENTS_MAILING_STATISTICS
-- begin GKLIENTS_FAILED_EMAILS
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
)^
-- end GKLIENTS_FAILED_EMAILS
-- begin GKLIENTS_ORIGIN
create table gklients_ORIGIN (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ORIGIN_TEXT varchar(255),
    --
    primary key (ID)
)^
-- end GKLIENTS_ORIGIN
