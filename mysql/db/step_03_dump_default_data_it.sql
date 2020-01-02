-- version 10-06-2014
delete from HOSPITAL;
delete from MEDICALDSR;
delete from MEDICALDSRTYPE;
delete from MEDICALDSRSTOCKMOVTYPE;
delete from DELIVERYTYPE;
delete from DELIVERYRESULTTYPE;
delete from PREGNANTTREATMENTTYPE;
delete from EXAMROW;
delete from EXAM;
delete from EXAMTYPE;
delete from OPERATION;
delete from OPERATIONTYPE;
delete from DISEASE;
delete from DISEASETYPE;
delete from VACCINE;
delete from ADMISSIONTYPE;
delete from DISCHARGETYPE;
delete from WARD;

-- HOSPITAL
INSERT INTO HOSPITAL (HOS_ID_A,HOS_NAME,HOS_ADDR,HOS_CITY,HOS_TELE,HOS_FAX,HOS_EMAIL,HOS_LOCK) VALUES 
 ('STLUKE','St. Luke HOSPITAL - Angal','P.O. BOX 85 - NEBBI','ANGAL','+256 0472621076','+256 0','angal@ucmb.ug.co.',0);

-- MEDICALDSRTYPE
LOAD DATA LOCAL INFILE './data_it/medicaldsrtype.csv'
	INTO TABLE MEDICALDSRTYPE 
	FIELDS TERMINATED BY ';' 
	LINES TERMINATED BY '\r\n';

-- MEDICALDSRSTOCKMOVTYPE
LOAD DATA LOCAL INFILE './data_it/medicaldsrstockmovtype.csv'
	INTO TABLE MEDICALDSRSTOCKMOVTYPE 
	FIELDS TERMINATED BY ';' 
	LINES TERMINATED BY '\r\n';

-- DELIVERYTYPE
LOAD DATA LOCAL INFILE './data_it/deliverytype.csv'
	INTO TABLE DELIVERYTYPE 
	FIELDS TERMINATED BY ';' 
	LINES TERMINATED BY '\r\n';

-- DELIVERYRESULTTYPE
LOAD DATA LOCAL INFILE './data_it/deliveryresulttype.csv'
	INTO TABLE DELIVERYRESULTTYPE 
	FIELDS TERMINATED BY ';' 
	LINES TERMINATED BY '\r\n';

-- PREGNANTTREATMENTTYPE
LOAD DATA LOCAL INFILE './data_it/pregnanttreatmenttype.csv'
	INTO TABLE PREGNANTTREATMENTTYPE 
	FIELDS TERMINATED BY ';' 
	LINES TERMINATED BY '\r\n';

-- EXAMTYPE
LOAD DATA LOCAL INFILE './data_it/examtype.csv'
	INTO TABLE EXAMTYPE 
	FIELDS TERMINATED BY ';' 
	LINES TERMINATED BY '\r\n';

-- EXAM
LOAD DATA LOCAL INFILE './data_it/exam.csv'
	INTO TABLE EXAM 
	FIELDS TERMINATED BY ';' 
	LINES TERMINATED BY '\r\n';

-- EXAMROW
LOAD DATA LOCAL INFILE './data_it/examrow.csv'
	INTO TABLE EXAMROW 
	FIELDS TERMINATED BY ';' 
	LINES TERMINATED BY '\r\n';

-- OPERATIONTYPE
LOAD DATA LOCAL INFILE './data_it/operationtype.csv'
	INTO TABLE OPERATIONTYPE 
	FIELDS TERMINATED BY ';' 
	LINES TERMINATED BY '\r\n';

-- VACCINE
LOAD DATA LOCAL INFILE './data_it/vaccine.csv'
	INTO TABLE VACCINE 
	FIELDS TERMINATED BY ';' 
	LINES TERMINATED BY '\r\n';

-- ADMISSIONTYPE
LOAD DATA LOCAL INFILE './data_it/admissiontype.csv'
	INTO TABLE ADMISSIONTYPE 
	FIELDS TERMINATED BY ';' 
	LINES TERMINATED BY '\r\n';

-- DISCHARGETYPE
LOAD DATA LOCAL INFILE './data_it/dischargetype.csv'
	INTO TABLE DISCHARGETYPE 
	FIELDS TERMINATED BY ';' 
	LINES TERMINATED BY '\r\n';

-- DISEASETYPE
LOAD DATA LOCAL INFILE './data_it/diseasetype.csv'
	INTO TABLE DISEASETYPE 
	FIELDS TERMINATED BY ';' 
	LINES TERMINATED BY '\r\n';

-- DISEASE
LOAD DATA LOCAL INFILE './data_it/disease.csv'
	INTO TABLE DISEASE 
	FIELDS TERMINATED BY ';' 
	LINES TERMINATED BY '\r\n';

-- OPERATION
LOAD DATA LOCAL INFILE './data_it/operation.csv'
	INTO TABLE OPERATION 
	FIELDS TERMINATED BY ';' 
	LINES TERMINATED BY '\r\n';

-- MEDICALDSR
LOAD DATA LOCAL INFILE './data_it/medicaldsr.csv'
	INTO TABLE MEDICALDSR 
	FIELDS TERMINATED BY ';' 
	LINES TERMINATED BY '\r\n';

-- WARD
LOAD DATA LOCAL INFILE './data_it/ward.csv'
	INTO TABLE WARD 
	FIELDS TERMINATED BY ';' 
	LINES TERMINATED BY '\r\n';
