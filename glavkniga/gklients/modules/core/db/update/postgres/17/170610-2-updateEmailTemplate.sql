update gklients_EMAIL_TEMPLATE set SUBJECT = '' where SUBJECT is null ;
alter table gklients_EMAIL_TEMPLATE alter column SUBJECT set not null ;
update gklients_EMAIL_TEMPLATE set BODY_ = '' where BODY_ is null ;
alter table gklients_EMAIL_TEMPLATE alter column BODY_ set not null ;
update gklients_EMAIL_TEMPLATE set EMAIL_TYPE = 1 where EMAIL_TYPE is null ;
alter table gklients_EMAIL_TEMPLATE alter column EMAIL_TYPE set not null ;
