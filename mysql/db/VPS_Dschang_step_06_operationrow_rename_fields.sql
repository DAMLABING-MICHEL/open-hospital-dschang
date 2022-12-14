ALTER TABLE OPERATIONROW CHANGE ID OPER_ID_A int (11) NOT NULL auto_increment;
ALTER TABLE OPERATIONROW CHANGE OPROW_ID OPER_ID varchar (11)  NOT NULL;
ALTER TABLE OPERATIONROW CHANGE OPROW_PRESCRIBER OPER_PRESCRIBER varchar (150)  NOT NULL;
ALTER TABLE OPERATIONROW CHANGE OPROW_RESULT OPER_RESULT varchar (250)  NOT NULL;
ALTER TABLE OPERATIONROW CHANGE OPROW_OPDATE OPER_OPDATE datetime NOT NULL default '2016-01-01 00:00:00';
ALTER TABLE OPERATIONROW CHANGE OPROW_REMARKS OPER_REMARKS varchar (250) NOT NULL;
ALTER TABLE OPERATIONROW CHANGE OPROW_ADMISSION_ID OPER_ADMISSION_ID int(11) default 0;
ALTER TABLE OPERATIONROW CHANGE OPROW_OPD_ID OPER_OPD_ID int(11)  default 0;
ALTER TABLE OPERATIONROW CHANGE OPROW_BILL_ID OPER_BILL_ID int(11)  default 0;
ALTER TABLE OPERATIONROW CHANGE OPROW_TRANS_UNIT OPER_TRANS_UNIT float NULL default 0;

ALTER TABLE OPERATIONROW DROP INDEX OPROW_ID;

