ALTER TABLE billitems CHANGE BLI_ID_PRICE BLI_ID_PRICE VARCHAR(28) 
CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL;

ALTER TABLE billitems CHANGE BLI_ITEM_ID BLI_ITEM_ID VARCHAR(25) 
CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL;

ALTER TABLE prices CHANGE PRC_ITEM PRC_ITEM VARCHAR(25) 
CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL;