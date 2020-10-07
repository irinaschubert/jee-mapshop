DROP TABLE item cascade constraints;
DROP TABLE customer cascade constraints;
DROP TABLE cart cascade constraints;
DROP TABLE status cascade constraints;

CREATE TABLE customer (
id         NUMBER(19) PRIMARY KEY,
email      VARCHAR2(40) NOT NULL UNIQUE,
password   VARCHAR2(10) NOT NULL
           CHECK(LENGTH(password)>=6)
);
GRANT SELECT, INSERT, UPDATE, DELETE 
ON customer TO onlineshop_user; 

CREATE UNIQUE INDEX customer_index 
ON customer(
    email,
    password
);

CREATE TABLE item (
id           NUMBER(19) PRIMARY KEY,
title        VARCHAR2(40) NOT NULL,
year         DATE,
publisher    VARCHAR(40),
isbn         VARCHAR(20),
price        NUMBER(12,2) NOT NULL,
foto         BLOB,
seller_id    NUMBER(19) NOT NULL,
buyer_id     NUMBER(19) NOT NULL,
sold         TIMESTAMP(3),
CONSTRAINT fk_seller_item 
    FOREIGN KEY (seller_id) REFERENCES customer (id),
CONSTRAINT fk_buyer_item 
    FOREIGN KEY (buyer_id) REFERENCES customer (id)
);
GRANT SELECT, INSERT, UPDATE, DELETE 
ON item TO onlineshop_user; 

CREATE TABLE status (
id           NUMBER(19) PRIMARY KEY,
description         VARCHAR2(20) NOT NULL
);
GRANT SELECT, INSERT, UPDATE, DELETE 
ON status TO onlineshop_user; 

CREATE TABLE cart (
id           NUMBER(19) PRIMARY KEY,
item_id      NUMBER(19) NOT NULL,
buyer_id     NUMBER(19) NOT NULL,
status_id    NUMBER(1) NOT NULL,
CONSTRAINT fk_item_cart 
    FOREIGN KEY (item_id) REFERENCES item (id),
CONSTRAINT fk_buyer_cart 
    FOREIGN KEY (buyer_id) REFERENCES customer (id),
CONSTRAINT fk_status_cart 
    FOREIGN KEY (status_id) REFERENCES status (id)
);
GRANT SELECT, INSERT, UPDATE, DELETE 
ON cart TO onlineshop_user; 

DROP SEQUENCE seq_customer;
CREATE SEQUENCE seq_customer;
GRANT ALL ON seq_customer TO onlineshop_user;

DROP SEQUENCE seq_item;
CREATE SEQUENCE seq_item;
GRANT ALL ON seq_item TO onlineshop_user;

DROP SEQUENCE seq_cart;
CREATE SEQUENCE seq_cart;
GRANT ALL ON seq_cart TO onlineshop_user;

DROP SEQUENCE seq_status;
CREATE SEQUENCE seq_status;
GRANT ALL ON seq_status TO onlineshop_user;

CREATE OR REPLACE TRIGGER tri_customer
BEFORE INSERT ON customer
FOR EACH ROW
BEGIN :NEW.id := seq_customer.NEXTVAL;
END;
/

CREATE OR REPLACE TRIGGER tri_item
BEFORE INSERT ON item
FOR EACH ROW
BEGIN :NEW.id := seq_item.NEXTVAL;
END;
/

CREATE OR REPLACE TRIGGER tri_cart
BEFORE INSERT ON cart
FOR EACH ROW
BEGIN :NEW.id := seq_cart.NEXTVAL;
END;
/

CREATE OR REPLACE TRIGGER tri_status
BEFORE INSERT ON status
FOR EACH ROW
BEGIN :NEW.id := seq_status.NEXTVAL;
END;
/

commit;
