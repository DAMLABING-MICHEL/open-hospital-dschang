/*purge of bill & associate tables*/

SET FOREIGN_KEY_CHECKS = 0; 
truncate billpayments;
truncate billitems;
truncate bills;
SET FOREIGN_KEY_CHECKS = 1; 



	


