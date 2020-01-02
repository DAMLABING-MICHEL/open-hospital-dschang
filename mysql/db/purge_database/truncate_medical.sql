#purge of associate tables of medicaldsr

SET FOREIGN_KEY_CHECKS = 0; 
truncate medicaldsrlot;
truncate medicaldsrstockmov;
truncate medicaldsrstockmovward;
truncate medicaldsrward;
SET FOREIGN_KEY_CHECKS = 1;

UPDATE medicaldsr set MDSR_INI_STOCK_QTI=0 , MDSR_IN_QTI=0, MDSR_OUT_QTI=0;


	


