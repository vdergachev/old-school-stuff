alter table GKLIENTS_ONETIME_MAILING_TOKEN_LINK add constraint FK_GOMTL_TOKEN foreign key (TOKEN_ID) references GKLIENTS_TOKEN(ID);
alter table GKLIENTS_ONETIME_MAILING_TOKEN_LINK add constraint FK_GOMTL_ONETIME_MAILING foreign key (ONETIME_MAILING_ID) references GKLIENTS_ONETIME_MAILING(ID);