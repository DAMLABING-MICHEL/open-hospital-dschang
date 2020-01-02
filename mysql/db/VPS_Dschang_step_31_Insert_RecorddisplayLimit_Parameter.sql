
INSERT INTO `parameters` (`PRMS_CODE`, `PRMS_VALUE`, `PRMS_DEFAULT_VALUE`, `PRMS_DESCRIPTION`, `PRMS_CREATE_BY`, `PRMS_MODIFY_BY`, `PRMS_CREATE_DATE`, `PRMS_MODIFY_DATE`, `PRMS_DELETED_BY`, `PRMS_DELETED_DATE`, `PRMS_DELETED`) VALUES

('LIMITRECORDDISPLAY', '30', '20', 'nombre limite des enregistrements a afficher', 'admin', NULL, NULL, NULL, NULL, NULL, NULL),
('ALLOWLIMITRECORDDISPLAY', '@true', '@false', 'Appliquons nous une nombre limite des enregistrements a afficher ?', 'admin', NULL, NULL, NULL, '2018-08-25 00:00:00', NULL, NULL);