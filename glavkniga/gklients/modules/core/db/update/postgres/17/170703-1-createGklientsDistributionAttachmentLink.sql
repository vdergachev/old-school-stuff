create table GKLIENTS_DISTRIBUTION_ATTACHMENT_LINK (
    ATTACHMENT_ID uuid,
    DISTRIBUTION_ID uuid,
    primary key (ATTACHMENT_ID, DISTRIBUTION_ID)
);