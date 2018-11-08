alter table GKLIENTS_CLIENT add constraint FK_GKLIENTS_CLIENT_ORIGIN foreign key (ORIGIN_ID) references gklients_ORIGIN(ID);
create index IDX_GKLIENTS_CLIENT_ORIGIN on GKLIENTS_CLIENT (ORIGIN_ID);
