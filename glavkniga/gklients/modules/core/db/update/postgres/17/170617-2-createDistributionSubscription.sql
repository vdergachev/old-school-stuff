alter table GKLIENTS_DISTRIBUTION_SUBSCRIPTION add constraint FK_GKLIENTS_DISTRIBUTION_SUBSCRIPTION_DISTRIBUTION foreign key (DISTRIBUTION_ID) references GKLIENTS_DISTRIBUTION(ID);
alter table GKLIENTS_DISTRIBUTION_SUBSCRIPTION add constraint FK_GKLIENTS_DISTRIBUTION_SUBSCRIPTION_CLIENT foreign key (CLIENT_ID) references GKLIENTS_CLIENT(ID);
create index IDX_GKLIENTS_DISTRIBUTION_SUBSCRIPTION_DISTRIBUTION on GKLIENTS_DISTRIBUTION_SUBSCRIPTION (DISTRIBUTION_ID);
create index IDX_GKLIENTS_DISTRIBUTION_SUBSCRIPTION_CLIENT on GKLIENTS_DISTRIBUTION_SUBSCRIPTION (CLIENT_ID);
