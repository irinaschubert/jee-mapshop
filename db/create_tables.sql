DROP TABLE item CASCADE constraints;
DROP TABLE customer CASCADE constraints;
DROP TABLE status CASCADE constraints;


CREATE TABLE customer (
id         NUMBER(19) PRIMARY KEY,
email      VARCHAR2(40) NOT NULL UNIQUE,
password   VARCHAR2(10) NOT NULL
           CHECK(LENGTH(password)>=6)
);
GRANT SELECT, INSERT, UPDATE, DELETE 
ON customer TO mapshop_user; 

CREATE UNIQUE INDEX customer_index 
ON customer(
    email,
    password
);

CREATE TABLE status (
id                  NUMBER(2) PRIMARY KEY,
description         VARCHAR2(20) NOT NULL
);
GRANT SELECT, INSERT, UPDATE, DELETE 
ON status TO mapshop_user; 

CREATE TABLE item (
id           NUMBER(19) PRIMARY KEY,
product_id   NUMBER(10),
title        VARCHAR2(40) NOT NULL,
description  VARCHAR2(1000),
price        NUMBER(12,2) NOT NULL,
stockNumber  NUMBER(6) NOT NULL,
foto         BLOB,
sold         TIMESTAMP(3),
seller_id    NUMBER(19),
buyer_id     NUMBER(19),
status_id    NUMBER(2) NOT NULL,
CONSTRAINT fk_seller FOREIGN KEY (seller_id) REFERENCES customer(id),
CONSTRAINT fk_buyer FOREIGN KEY (buyer_id) REFERENCES customer(id),
CONSTRAINT fk_status FOREIGN KEY (status_id) REFERENCES status(id)
);
GRANT SELECT, INSERT, UPDATE, DELETE 
ON item TO mapshop_user; 

DROP SEQUENCE seq_customer;

CREATE SEQUENCE seq_customer;
GRANT ALL ON seq_customer TO mapshop_user;

DROP SEQUENCE seq_item;
CREATE SEQUENCE seq_item;
GRANT ALL ON seq_item TO mapshop_user;

DROP SEQUENCE seq_status;
CREATE SEQUENCE seq_status;
GRANT ALL ON seq_status TO mapshop_user;

commit;
