ALTER TABLE opd ADD OPD_DATE_NEXT_VIS datetime NULL;
ALTER TABLE opd ADD OPD_PAT_COMPLAINT TEXT NULL DEFAULT NULL;
ALTER TABLE opd ADD OPD_IS_PREGNANT tinyint(1) NOT NULL DEFAULT '0';
ALTER TABLE therapies ADD THR_PRESCRIPTION_DATE datetime NULL;
