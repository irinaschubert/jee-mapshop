select * from mapshop.customer;
delete from mapshop.item;
select * from mapshop.status;
select id, title, description, price, stockNumber, sold, buyer_id, seller_id, status_id from mapshop.item;