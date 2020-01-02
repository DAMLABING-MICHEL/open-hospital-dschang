ALTER TABLE `billitems` CHANGE `BLI_QTY` `BLI_QTY` DOUBLE NOT NULL;


update menuitem set `MNI_POSITION`=`MNI_POSITION`+1 where `MNI_POSITION`>0 and `MNI_SUBMENU`='pharmacy';

INSERT INTO MENUITEM VALUES ('medicalswithlot','angal.menu.btn.medicalwithlots','angal.menu.medicalwithlots','x','L','pharmacy','org.isf.medicals.gui.MedicalBrowserWithLot','N',1);
INSERT INTO GROUPMENU (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE) VALUES ('admin','medicalswithlot','Y');



