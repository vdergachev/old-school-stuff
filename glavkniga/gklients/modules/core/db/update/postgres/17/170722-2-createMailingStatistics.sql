alter table GKLIENTS_MAILING_STATISTICS add constraint FK_GKLIENTS_MAILING_STATISTICS_ONETIME_MAILING foreign key (ONETIME_MAILING_ID) references GKLIENTS_ONETIME_MAILING(ID);
create index IDX_GKLIENTS_MAILING_STATISTICS_ONETIME_MAILING on GKLIENTS_MAILING_STATISTICS (ONETIME_MAILING_ID);