alter table GKLIENTS_ONETIME_MAILING add constraint FK_GKLIENTS_ONETIME_MAILING_CLIENT foreign key (CLIENT_ID) references GKLIENTS_CLIENT(ID);
create index IDX_GKLIENTS_ONETIME_MAILING_CLIENT on GKLIENTS_ONETIME_MAILING (CLIENT_ID);