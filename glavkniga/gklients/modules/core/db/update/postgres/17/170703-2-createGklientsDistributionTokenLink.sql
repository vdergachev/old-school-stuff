alter table GKLIENTS_DISTRIBUTION_TOKEN_LINK add constraint FK_GDTL_TOKEN foreign key (TOKEN_ID) references GKLIENTS_TOKEN(ID);
alter table GKLIENTS_DISTRIBUTION_TOKEN_LINK add constraint FK_GDTL_DISTRIBUTION foreign key (DISTRIBUTION_ID) references GKLIENTS_DISTRIBUTION(ID);
