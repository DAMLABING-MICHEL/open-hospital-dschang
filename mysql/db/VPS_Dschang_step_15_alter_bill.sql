ALTER TABLE BILLS ADD BLL_IS_CLOSED_MANUALLY boolean not NULL default 0;

INSERT INTO MENUITEM VALUES ('btnbillclosebill','angal.billbrowser.closebill','angal.billbrowser.closebill','x','C','billsmanager','none','N',5);
INSERT INTO GROUPMENU (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE) VALUES ('admin','btnbillclosebill','Y');
