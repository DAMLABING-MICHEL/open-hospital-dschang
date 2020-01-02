/*requetes de correction pour le produit: IND_1                   ok*/

update medicaldsrstockmov set MMV_QTY = 10 where MMV_ID = 5414;
update medicaldsrstockmov set MMV_QTY = 20 where MMV_ID = 7614 ;


/*requetes de correction pour le produit: CHLO+NA_1               ok*/

update medicaldsrstockmov set MMV_QTY = 6 where MMV_ID = 3360;
update medicaldsrstockmov set MMV_QTY = 54 where MMV_ID = 71;
update medicaldsrstockmov set MMV_QTY = 22 where MMV_ID = 6397;


/*requetes de correction pour le produit: CIP  (a revoir encore)  ok*/

update medicaldsrstockmov set MMV_QTY = 19 where MMV_ID = 4797 ;
update medicaldsrstockmov set MMV_QTY = 41 where MMV_ID = 7370  ;

/*requetes de correction pour le produit: GLU_2                   ok*/

update medicaldsrstockmov set MMV_QTY = 3 where MMV_ID = 4478  ;
update medicaldsrstockmov set MMV_QTY = 11 where MMV_ID = 6212  ;


/*requetes de correction pour le produit: MET_7                   ok*/

update medicaldsrstockmov set MMV_QTY = 20 where MMV_ID = 3546  ;
update medicaldsrstockmov set MMV_QTY = 40 where MMV_ID = 6493   ;


/*requetes de correction pour le produit: MIX (a revoir encore)   ok*/

update medicaldsrstockmov set MMV_QTY = 19 where MMV_ID = 3550   ;
update medicaldsrstockmov set MMV_QTY = 41 where MMV_ID = 6519  ;


/*requetes de correction pour le produit: PARA_2                  ok*/

update medicaldsrstockmov set MMV_QTY = 5 where MMV_ID = 3488  ; 
update medicaldsrstockmov set MMV_QTY = 15 where MMV_ID = 7656   ;


/*requetes de correction pour le produit: VIT_3                   ok*/

update medicaldsrstockmov set MMV_QTY = 99 where MMV_ID = 4206  ; 
update medicaldsrstockmov set MMV_QTY = 101 where MMV_ID = 7292   ;



/* requetes de correction pour le produit: PAP (a revoir encore)  */

update medicaldsrstockmov set MMV_QTY = 5 where MMV_ID = 2157  ; 
update medicaldsrstockmov set MMV_QTY = 8 where MMV_ID = 5717   ;



/* produits avec stock total differents somme des lots: 
CIP    	discordance total lots
PAP		discordance total lots
MIX		discordance total lots 
*/