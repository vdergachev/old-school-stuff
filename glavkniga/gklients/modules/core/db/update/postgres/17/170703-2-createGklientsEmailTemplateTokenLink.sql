alter table GKLIENTS_EMAIL_TEMPLATE_TOKEN_LINK add constraint FK_GETTL_TOKEN foreign key (TOKEN_ID) references GKLIENTS_TOKEN(ID);
alter table GKLIENTS_EMAIL_TEMPLATE_TOKEN_LINK add constraint FK_GETTL_EMAIL_TEMPLATE foreign key (EMAIL_TEMPLATE_ID) references gklients_EMAIL_TEMPLATE(ID);
