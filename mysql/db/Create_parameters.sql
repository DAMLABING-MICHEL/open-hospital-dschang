INSERT INTO `parameters` (`PRMS_CODE`, `PRMS_VALUE`, `PRMS_DEFAULT_VALUE`, `PRMS_DESCRIPTION`, `PRMS_CREATE_BY`, `PRMS_MODIFY_BY`, `PRMS_CREATE_DATE`, `PRMS_MODIFY_DATE`, `PRMS_DELETED_BY`, `PRMS_DELETED_DATE`, `PRMS_DELETED`) VALUES

('SINGLEUSER', '@false', '@false', '', 'admin', NULL, '2017-11-17 11:36:28', NULL, NULL, NULL, NULL),
('AUTOMATICLOT', '@false', '@false', '', 'admin', NULL, '2017-11-17 11:37:13', NULL, NULL, NULL, NULL),
('ADMCHART', 'patient_adm_chart', 'patient_adm_chart', '', 'admin', NULL, '2017-11-17 11:39:13', NULL, NULL, NULL, NULL),
('ALLOWFILTERBILLBYMEDICAL', '@true', '@true', '', 'admin', NULL, '2017-11-17 11:39:36', NULL, NULL, NULL, NULL),
('ALLOWMULTIPLEOPENEDBILL', '@false', '@true', '', 'admin', 'admin', '2017-11-17 11:39:51', '2017-11-21 09:08:33', NULL, NULL, NULL),
('ALLOWPRINTOPENEDBILL', '@true', '@true', '', 'admin', NULL, '2017-11-17 11:40:04', NULL, NULL, NULL, NULL),
('AUTOMATICCLOSEBILL', '@true', '@true', '', 'admin', NULL, '2017-11-17 11:40:16', NULL, NULL, NULL, NULL),
('AUTOMATICLOTDISCHARGE', '@false', '@false', '', 'admin', NULL, '2017-11-17 11:40:31', NULL, NULL, NULL, NULL),
('BILLSPAYMENTREPORT', 'BillsPaymentReportUserAllInDate', 'BillsPaymentReportUserAllInDate', '', 'admin', NULL, '2017-11-17 11:40:50', NULL, NULL, NULL, NULL),
('BILLSREPORT', 'BillsReport', 'BillsReport', '', 'admin', NULL, '2017-11-17 11:41:10', NULL, NULL, NULL, NULL),
('BILLSREPORTMONTH', 'BillsReportMonth', 'BillsReportMonth', '', 'admin', NULL, '2017-11-17 11:41:28', NULL, NULL, NULL, NULL),
('CLOSE_BILL_WITHOUD_ASK', '@false', '@false', '', 'admin', NULL, '2017-11-17 11:41:59', NULL, NULL, NULL, NULL),
('COST_WITH_REDUCTION', '@true', '@true', '', 'admin', NULL, '2017-11-17 11:42:12', NULL, NULL, NULL, NULL),
('DEBUG', '@false', '@false', '', 'admin', NULL, '2017-11-17 11:42:52', NULL, NULL, NULL, NULL),
('DEFAULTSKIN', '@false', '@false', '', 'admin', NULL, '2017-11-17 11:43:10', NULL, NULL, NULL, NULL),
('DICOMMODULEENABLED', '@true', '@true', '', 'admin', NULL, '2017-11-17 11:43:22', NULL, NULL, NULL, NULL),
('DISCHART', 'patient_dis_chart', 'patient_dis_chart', '', 'admin', NULL, '2017-11-17 11:43:38', NULL, NULL, NULL, NULL),
('INTERNALPHARMACIES', '@true', '@true', '', 'admin', NULL, '2017-11-17 11:44:12', NULL, NULL, NULL, NULL),
('INTERNALVIEWER', '@true', '@true', '', 'admin', NULL, '2017-11-17 11:44:24', NULL, NULL, NULL, NULL),
('INVENTORYREPORT', 'InventoryReport', 'InventoryReport', '', 'admin', NULL, '2017-11-17 11:44:36', NULL, NULL, NULL, NULL),
('INVENTORYWARDREPORT', 'InventoryWardReport', 'InventoryWardReport', '', 'admin', NULL, '2017-11-17 11:44:49', NULL, NULL, NULL, NULL),
('LABEXTENDED', '@true', '@true', '', 'admin', NULL, '2017-11-17 11:46:54', NULL, NULL, NULL, NULL),
( 'LABMULTIPLEINSERT', '@true', '@true', '', 'admin', NULL, '2017-11-17 11:47:04', NULL, NULL, NULL, NULL),
( 'LOTWITHCOST', '@true', '@true', '', 'admin', NULL, '2017-11-17 11:47:19', NULL, NULL, NULL, NULL),
('MATERNITYRESTARTINJUNE', '@false', '@false', '', 'admin', NULL, '2017-11-17 11:47:46', NULL, NULL, NULL, NULL),
('MERGEFUNCTION', '@true', '@true', '', 'admin', NULL, '2017-11-17 11:48:02', NULL, NULL, NULL, NULL),
('OPDCHART', 'patient_opd_chart', 'patient_opd_chart', '', 'admin', NULL, '2017-11-17 11:48:18', NULL, NULL, NULL, NULL),
('OPDEXTENDED', '@true', '@true', '', 'admin', NULL, '2017-11-17 11:48:31', NULL, NULL, NULL, NULL),
('PATIENTBILL', 'PatientBillExtended', 'PatientBillExtended', '', 'admin', NULL, '2017-11-17 11:48:49', NULL, NULL, NULL, NULL),
('PATIENTBILLGROUPED', 'PatientBillGrouped', 'PatientBillGrouped', '', 'admin', NULL, '2017-11-17 11:49:01', NULL, NULL, NULL, NULL),
('PATIENTEXTENDED', '@true', '@true', '', 'admin', NULL, '2017-11-17 11:49:36', NULL, NULL, NULL, NULL),
('PATIENTSHEET', 'patient_clinical_sheet_ver2', 'patient_clinical_sheet_ver2', '', 'admin', NULL, '2017-11-17 11:49:49', NULL, NULL, NULL, NULL),
('PATIENTVACCINEEXTENDED', '@true', '@true', '', 'admin', NULL, '2017-11-17 11:50:08', NULL, NULL, NULL, NULL),
('PHARMACEUTICALORDER', 'PharmaceuticalOrder', 'PharmaceuticalOrder', '', 'admin', NULL, '2017-11-17 11:50:46', NULL, NULL, NULL, NULL),
('PHARMACEUTICALSTOCK', 'PharmaceuticalStock', 'PharmaceuticalStock', '', 'admin', NULL, '2017-11-17 11:51:26', NULL, NULL, NULL, NULL),
('PHONELENGTH', '9', '9', '', 'admin', NULL, '2017-11-17 11:51:44', NULL, NULL, NULL, NULL),
('PHOTOSDIR', 'G:/data/photos', 'G:/data/photos', '', 'admin', NULL, '2017-11-17 11:52:01', NULL, NULL, NULL, NULL),
('PREGNANCYCARE', '@true', '@true', '', 'admin', NULL, '2017-11-17 11:52:18', NULL, NULL, NULL, NULL),
('RECEIPTPRINTER', '@true', '@true', '', 'admin', NULL, '2017-11-17 11:52:33', NULL, NULL, NULL, NULL),
('SHOWDESCRIPTIONOPTION', '@false', '@false', '', 'admin', NULL, '2017-11-17 11:52:51', NULL, NULL, NULL, NULL),
('SMSENABLED', '@false', '@false', '', 'admin', NULL, '2017-11-17 11:53:04', NULL, NULL, NULL, NULL),
('STOCKMVTONBILLSAVE', '@true', '@true', '', 'admin', NULL, '2017-11-17 11:53:15', NULL, NULL, NULL, NULL),
('VIDEOMODULEENABLED', '@false', '@false', '', 'admin', NULL, '2017-11-17 11:53:29', NULL, NULL, NULL, NULL),
('LOGINFORMWITHCHOOSINGUSERNAME', '@false', '@false', '', 'admin', NULL, '2017-11-17 11:54:08', NULL, NULL, NULL, NULL),
('CREATELABORATORYAUTO', '@true', '@false', '', 'admin', NULL, '2017-11-17 11:54:21', NULL, NULL, NULL, NULL),
('CREATELABORATORYAUTOWITHOPENEDBILL', '@true', '@false', '', 'admin', NULL, '2017-11-17 11:54:40', NULL, NULL, NULL, NULL),
('INVENTORYSTATE_PROPERTIES', 'inventorystate.properties', 'inventorystate.properties', '', 'admin', NULL, '2017-11-18 11:56:13', NULL, NULL, NULL, NULL),
('MATERIALS_PROPERTIES', 'materials.properties', 'materials.properties', '', 'admin', NULL, '2017-11-18 12:00:54', NULL, NULL, NULL, NULL),
('SAGE_PROPERTIES', 'sage.properties', 'sage.properties', '', 'admin', NULL, '2017-11-18 12:02:26', NULL, NULL, NULL, NULL),
('SMS_PROPERTIES', 'sms.properties', 'sms.properties', '', 'admin', NULL, '2017-11-18 12:03:37', NULL, NULL, NULL, NULL),
('TXTPRINTER_PROPERTIES', 'txtPrinter.properties', 'txtPrinter.properties', '', 'admin', NULL, '2017-11-18 12:08:33', NULL, NULL, NULL, NULL),
('VERSION_PROPERTIES', 'version.properties', 'version.properties', '', 'admin', NULL, '2017-11-18 12:10:04', NULL, NULL, NULL, NULL),
('GSM_PROPERTIES', 'GSM.properties', 'GSM.properties', '', 'admin', NULL, '2017-11-18 12:11:05', NULL, NULL, NULL, NULL),
('SKEBBY_PROPERTIES', 'Skebby.properties', 'Skebby.properties', '', 'admin', NULL, '2017-11-18 12:12:05', NULL, NULL, NULL, NULL),
('GENERALDATA_PROPERTIES', 'generalData.properties', 'generalData.properties', 'local parameters', 'admin', NULL, '2017-11-18 12:12:05', NULL, NULL, NULL, NULL);

