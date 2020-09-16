DROP SCHEMA IF EXISTS ib;
CREATE SCHEMA ib DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE ib;

CREATE TABLE users(
	id INT AUTO_INCREMENT,
    email VARCHAR(30) NOT NULL,
	password VARCHAR(10) NOT NULL, 
	certificate VARCHAR(30) NOT NULL, 
	active TINYINT(1),
	authority VARCHAR(10) NOT NULL,
    PRIMARY KEY(id)
);

INSERT INTO USERS (id, email, password, certificate, active, authority) VALUES (1, 'user1@gmail.com','user1', 'cer1', 0, 'Regular');
INSERT INTO USERS (id, email, password, certificate, active, authority) VALUES (2, 'user2@gmail.com','user2', 'cer2' , 1, 'Admin');

CREATE TABLE userauthority(
	id INT AUTO_INCREMENT,
	name VARCHAR(10),
    PRIMARY KEY(id)
);
INSERT INTO USERAUTHORITY (id, name) VALUES (1, 'Regular');
INSERT INTO USERAUTHORITY (id, name) VALUES (2, 'Admin');

