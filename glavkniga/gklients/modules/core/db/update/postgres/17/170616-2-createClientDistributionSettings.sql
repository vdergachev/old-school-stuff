alter table GKLIENTS_CLIENT_DISTRIBUTION_SETTINGS add constraint FK_GKLIENTS_CLIENT_DISTRIBUTION_SETTINGS_CLIENT foreign key (CLIENT_ID) references GKLIENTS_CLIENT(ID);
