ALTER TABLE BILLS ADD BLL_WARD varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE LABORATORY ADD LAB_BLL_ID INT;

INSERT INTO MENUITEM VALUES ('opdexam','angal.opd.exams','angal.opd.exams','x','X','opd','none','N',3);
INSERT INTO GROUPMENU (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE) VALUES ('admin','opdexam','Y');

INSERT INTO MENUITEM VALUES ('opdeope','angal.opd.operation','angal.opd.operation','x','O','opd','none','N',4);
INSERT INTO GROUPMENU (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE) VALUES ('admin','opdeope','Y');

CREATE TABLE OPERATIONROW(
	OPER_ID_A  int (11) NOT NULL auto_increment,
	OPER_ID varchar (11)  NOT NULL ,
	OPER_PRESCRIBER varchar (150)  NOT NULL ,
	OPER_RESULT varchar (250)  NOT NULL ,
	OPER_OPDATE datetime NOT NULL default '2016-01-01 00:00:00',
	OPER_REMARKS varchar (250) NOT NULL,
	OPER_ADMISSION_ID int(11) default 0, 
	OPER_OPD_ID int(11)  default 0,
	OPER_BILL_ID int(11)  default 0,
	OPER_TRANS_UNIT float NULL default 0,	
	PRIMARY KEY (OPER_ID_A)
);





