CREATE TABLE MEDICALDSRINVENTORY  (
	INVT_ID  int NOT NULL auto_increment,
	INVT_STATE  varchar(50) default NULL,
	INVT_DATE  datetime NOT NULL, 
	INVT_US_ID_A  varchar(50) NOT NULL,
	PRIMARY KEY (INVT_ID)
);

CREATE TABLE MEDICALDSRINVENTORYROW  (
	INVTR_ID  int NOT NULL auto_increment,
	INVTR_THEORETIC_QTY float default NULL,
	INVTR_REAL_QTY float default NULL,
	INVTR_COST float default 0,
	INVTR_INVT_ID int NOT NULL,
	INVTR_MDSR_ID int NOT NULL,
	INVTR_LT_ID_A varchar(50) default NULL,	
	PRIMARY KEY (INVTR_ID),
	UNIQUE(INVTR_INVT_ID, INVTR_MDSR_ID, INVTR_LT_ID_A)
);


INSERT INTO MENUITEM VALUES ('inventory','angal.menu.btn.inventory','angal.menu.inventory','x','I','pharmacy','org.isf.medicalinventory.gui.InventoryBrowser','N',3);
INSERT INTO GROUPMENU (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE) VALUES ('admin','inventory','Y');

ALTER TABLE MEDICALDSRINVENTORY ADD INVT_REFERENCE varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL;

INSERT INTO MENUITEM VALUES ('inventoryward','angal.menu.btn.inventory.ward','angal.menu.inventory.ward','x','P','pharmacy','org.isf.medicalinventory.gui.InventoryWardBrowser','N',4);
INSERT INTO GROUPMENU (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE) VALUES ('admin','inventoryward','Y'); 
ALTER TABLE MEDICALDSRINVENTORY ADD INVT_TYPE varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL;
ALTER TABLE MEDICALDSRINVENTORY ADD INVT_WRD_ID_A varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL;
ALTER TABLE LOG CHANGE LOG_METHOD LOG_METHOD VARCHAR(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL;
ALTER TABLE LOG CHANGE LOG_MESS LOG_MESS VARCHAR(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL;


