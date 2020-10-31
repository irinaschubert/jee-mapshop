insert into mapshop.status (id,description) values (mapshop.seq_status.NEXTVAL,'active');
insert into mapshop.status (id,description) values (mapshop.seq_status.NEXTVAL,'inactive');
insert into mapshop.status (id,description) values (mapshop.seq_status.NEXTVAL,'sold');
insert into mapshop.status (id,description) values (mapshop.seq_status.NEXTVAL,'reserved');
select * from mapshop.status;