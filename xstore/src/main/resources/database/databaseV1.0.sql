CREATE SCHEMA xStore ;

-- Basic Details --
CREATE  TABLE BASIC_DETAIL (
  ID INT NOT NULL AUTO_INCREMENT,
  FIRST_NAME VARCHAR(45) NOT NULL,
  MIDDLE_NAME VARCHAR(45),
  LAST_NAME VARCHAR(45) NOT NULL,
  PHONE_NUMBER VARCHAR(45) NOT NULL,
  EMAIL VARCHAR(45) NOT NULL,
  ADDRESS VARCHAR(45) NOT NULL,
  CITY VARCHAR(45) NOT NULL,
  STATE VARCHAR(45) NOT NULL,
  COUNTRY VARCHAR(45) NOT NULL,
  PIN_CODE VARCHAR(45) NOT NULL,
  FAX VARCHAR(45),
  UPDATED_BY VARCHAR(45) NOT NULL,
  CREATED_BY VARCHAR(45) NOT NULL,
  UPDATED_DATE TIMESTAMP,
  CREATED_DATE TIMESTAMP,
  PRIMARY KEY (ID)
  );

 -- Client --
 CREATE TABLE CLIENT (
	ID INT NOT NULL AUTO_INCREMENT,
	BASIC_DETAIL_ID INT NOT NULL,
	USER_NAME VARCHAR(45) NOT NULL,
	PASSWORD VARCHAR(45) NOT NULL,
	UPDATED_BY VARCHAR(45) NOT NULL,
  	CREATED_BY VARCHAR(45) NOT NULL,
  	UPDATED_DATE TIMESTAMP,
  	CREATED_DATE TIMESTAMP,
  	STATUS BOOLEAN NOT NULL, -- Enable Shopping
	PRIMARY KEY (ID),
	UNIQUE KEY (USER_NAME),
	CONSTRAINT FK_CLIENT FOREIGN KEY (BASIC_DETAIL_ID) REFERENCES BASIC_DETAIL(ID)
	);
	
-- Store --
CREATE TABLE STORE (
	ID INT NOT NULL AUTO_INCREMENT,
	NAME VARCHAR(45) NOT NULL,
	CLIENT_ID INT NOT NULL,
	CURRENCY VARCHAR(45) NOT NULL,
	TIME_ZONE VARCHAR(45) NOT NULL,
	UPDATED_BY VARCHAR(45) NOT NULL,
  	CREATED_BY VARCHAR(45) NOT NULL,
  	UPDATED_DATE TIMESTAMP,
  	CREATED_DATE TIMESTAMP,
	PRIMARY KEY (ID),
	CONSTRAINT FK_STORE FOREIGN KEY (CLIENT_ID) REFERENCES CLIENT(ID)
);
 -- Store Users --
CREATE TABLE STORE_USER (
	ID INT NOT NULL AUTO_INCREMENT,
	BASIC_DETAIL_ID INT NOT NULL,
	STORE_ID INT NOT NULL,
	USER_NAME VARCHAR(45) NOT NULL,
	PASSWORD VARCHAR(45) NOT NULL,
	USER_TYPE VARCHAR(45) NOT NULL,
	NEWSLETTER BOOLEAN NOT NULL DEFAULT false,
	UPDATED_BY VARCHAR(45) NOT NULL,
  	CREATED_BY VARCHAR(45) NOT NULL,
  	UPDATED_DATE TIMESTAMP,
  	CREATED_DATE TIMESTAMP,
	PRIMARY KEY (ID),
	CONSTRAINT FK_STORE_USER FOREIGN KEY (BASIC_DETAIL_ID) REFERENCES BASIC_DETAIL(ID),
	CONSTRAINT FK_STORE_FOR_STORE_USER FOREIGN KEY (STORE_ID) REFERENCES STORE(ID)
	);
	
 -- Products --
CREATE TABLE PRODUCT (
	ID INT NOT NULL AUTO_INCREMENT,
	PRODUCT_NAME VARCHAR(45) NOT NULL,
	DESCRIPTION VARCHAR(45),
	STORE_ID INT NOT NULL,
	UPDATED_BY VARCHAR(45) NOT NULL,
  	CREATED_BY VARCHAR(45) NOT NULL,
  	UPDATED_DATE TIMESTAMP,
  	CREATED_DATE TIMESTAMP,
	PRIMARY KEY (ID),
	CONSTRAINT FK_PRODUCT FOREIGN KEY (STORE_ID) REFERENCES STORE(ID)
	);
	
 -- Product Properties --
CREATE TABLE PRODUCT_PROPERTY (
	ID INT NOT NULL AUTO_INCREMENT,
	PRODUCT_ID INT NOT NULL,
	SIZE VARCHAR(45),
	PROPERTY VARCHAR(45),
	QUANTITY VARCHAR(45),
	UNIT VARCHAR(45),
	PRICE FLOAT(10,2),
	UPDATED_BY VARCHAR(45) NOT NULL,
  	CREATED_BY VARCHAR(45) NOT NULL,
  	UPDATED_DATE TIMESTAMP,
  	CREATED_DATE TIMESTAMP,
	PRIMARY KEY (ID),
	CONSTRAINT FK_PRODUCT_PROPERTY FOREIGN KEY (PRODUCT_ID) REFERENCES PRODUCT(ID)
	);

	-- DROP SCRIPTS --
	DROP TABLE PRODUCT_PROPERTY;
	DROP TABLE PRODUCT;
	DROP TABLE STORE_USER;
	DROP TABLE STORE;
	DROP TABLE CLIENT;
	DROP TABLE BASIC_DETAIL;
	
	-- INSERT SCRIPT --
