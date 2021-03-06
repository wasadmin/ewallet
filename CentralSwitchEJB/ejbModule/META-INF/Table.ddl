CREATE TABLE BANKIF.Configuration (key VARCHAR(254) NOT NULL, value VARCHAR(254), version INTEGER, PRIMARY KEY (key));
CREATE TABLE BANKIF.MessageTransaction (uuid VARCHAR(30) NOT NULL, amount BIGINT, balance BIGINT, bankReference VARCHAR(30), branchId VARCHAR(30), customerId VARCHAR(30), customerName VARCHAR(50), customerUtilityAccount VARCHAR(30), dateCreated TIMESTAMP, destinationBankAccount VARCHAR(30), messageReference VARCHAR(30), narrative VARCHAR(250), passcodePrompt VARCHAR(10), passwordRetryCount INTEGER, responseCode VARCHAR(30), reversalReason VARCHAR(250), secretCode VARCHAR(50), sourceBankAccount VARCHAR(30), sourceBankCode VARCHAR(30), sourceBankId VARCHAR(30), sourceBankPrefix VARCHAR(30), sourceMobileId VARCHAR(30), sourceMobileNumber VARCHAR(30), status VARCHAR(40), targetBankCode VARCHAR(30), targetBankId VARCHAR(30), targetMobileId VARCHAR(30), targetMobileNumber VARCHAR(30), tariffAmount BIGINT, tariffId VARCHAR(30), timeout TIMESTAMP, toCustomerName VARCHAR(50), transactionLocationId VARCHAR(30), transactionLocationType VARCHAR(40), transactionStatus VARCHAR(20), transactionType VARCHAR(40), transferType VARCHAR(20), utilityBankAccount VARCHAR(30), utilityName VARCHAR(50), valueDate TIMESTAMP, version INTEGER, PRIMARY KEY (uuid));
CREATE TABLE BANKIF.TransactionState (id VARCHAR(30) NOT NULL, dateCreated TIMESTAMP, narrative VARCHAR(254), status VARCHAR(40), version INTEGER, transactionUuid VARCHAR(254), PRIMARY KEY (id));
CREATE TABLE BANKIF.Tap (id VARCHAR(30) NOT NULL, status VARCHAR(5), version INTEGER, PRIMARY KEY(id));

ALTER TABLE BANKIF.TransactionState ADD FOREIGN KEY (transactionUuid) REFERENCES BANKIF.MessageTransaction (uuid);

ALTER TABLE BANKIF.MESSAGETRANSACTION ADD SUMMARYDATE TIMESTAMP;

ALTER TABLE BANKIF.MESSAGETRANSACTION ADD BANKREPLYQUEUENAME VARCHAR(30);
ALTER TABLE BANKIF.MESSAGETRANSACTION ADD BANKREQUESTQUEUENAME VARCHAR(30);
ALTER TABLE BANKIF.MESSAGETRANSACTION ADD 	MERCHANTREQUESTQUEUENAME VARCHAR(30);
ALTER TABLE BANKIF.MESSAGETRANSACTION ADD 	MERCHANTREPLYQUEUENAME VARCHAR(30);
ALTER TABLE BANKIF.MESSAGETRANSACTION ADD 	BANKNAME VARCHAR(30);
ALTER TABLE BANKIF.MESSAGETRANSACTION ADD 	ZSWCODE INTEGER;
ALTER TABLE BANKIF.MESSAGETRANSACTION ADD 	ZESAPAYCODE INTEGER;
ALTER TABLE BANKIF.MESSAGETRANSACTION ADD 	ZESAPAYBRANCH INTEGER;
ALTER TABLE BANKIF.MESSAGETRANSACTION ADD 	APPLYVENDORSIGNATURE SMALLINT;
ALTER TABLE BANKIF.MESSAGETRANSACTION ADD 	ACCOUNTVALIDATIONENABLED SMALLINT;
ALTER TABLE BANKIF.MESSAGETRANSACTION ADD 	STRAIGHTTHROUGHENABLED SMALLINT;
ALTER TABLE BANKIF.MESSAGETRANSACTION ADD 	NOTIFICATIONENABLED SMALLINT;

ALTER TABLE BANKIF.MESSAGETRANSACTION ALTER COLUMN CUSTOMERNAME SET DATA TYPE VARCHAR(100);
ALTER TABLE BANKIF.MESSAGETRANSACTION ALTER COLUMN TOCUSTOMERNAME SET DATA TYPE VARCHAR(100);

CREATE TABLE BANKIF.BOUQUET (id VARCHAR(30) NOT NULL, name VARCHAR(50), code VARCHAR(50), version INTEGER, PRIMARY KEY(id));