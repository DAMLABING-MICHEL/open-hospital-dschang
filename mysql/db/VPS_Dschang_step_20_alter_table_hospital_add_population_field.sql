ALTER TABLE hospital ADD HOS_POPULATION_AREA INT(11) NULL default NULL;
ALTER TABLE operation ADD OPE_IMPORTANCE INT(11) NULL default NULL;
ALTER TABLE pregnancydelivery ADD PDEL_HIV_STATUT varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT "";

ALTER TABLE OPD ADD OPD_PROG_MONTH INT NULL AFTER OPD_PROG_YEAR;
ALTER TABLE PATIENT ADD PAT_STATUS VARCHAR(2) NULL ;
ALTER TABLE PATIENT ADD PAT_OCCUPATION VARCHAR(100) NULL ;
