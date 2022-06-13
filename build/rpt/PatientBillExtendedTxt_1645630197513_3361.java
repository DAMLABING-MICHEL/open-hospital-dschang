/*
 * Generated by JasperReports - 23/02/22 16:29
 */
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.fill.*;

import java.util.*;
import java.math.*;
import java.text.*;
import java.io.*;
import java.net.*;

import net.sf.jasperreports.engine.*;
import java.util.*;
import net.sf.jasperreports.engine.data.*;


/**
 *
 */
public class PatientBillExtendedTxt_1645630197513_3361 extends JREvaluator
{


    /**
     *
     */
    private JRFillParameter parameter_IS_IGNORE_PAGINATION = null;
    private JRFillParameter parameter_REPORT_CONNECTION = null;
    private JRFillParameter parameter_Email = null;
    private JRFillParameter parameter_Address = null;
    private JRFillParameter parameter_Telephone = null;
    private JRFillParameter parameter_REPORT_LOCALE = null;
    private JRFillParameter parameter_REPORT_TIME_ZONE = null;
    private JRFillParameter parameter_REPORT_TEMPLATES = null;
    private JRFillParameter parameter_SUBREPORT_DIR = null;
    private JRFillParameter parameter_City = null;
    private JRFillParameter parameter_REPORT_MAX_COUNT = null;
    private JRFillParameter parameter_REPORT_SCRIPTLET = null;
    private JRFillParameter parameter_REPORT_FILE_RESOLVER = null;
    private JRFillParameter parameter_REPORT_FORMAT_FACTORY = null;
    private JRFillParameter parameter_REPORT_PARAMETERS_MAP = null;
    private JRFillParameter parameter_REPORT_RESOURCE_BUNDLE = null;
    private JRFillParameter parameter_billID = null;
    private JRFillParameter parameter_REPORT_DATA_SOURCE = null;
    private JRFillParameter parameter_REPORT_CLASS_LOADER = null;
    private JRFillParameter parameter_REPORT_URL_HANDLER_FACTORY = null;
    private JRFillParameter parameter_REPORT_VIRTUALIZER = null;
    private JRFillParameter parameter_Hospital = null;
    private JRFillField field_BLL_USR_ID_A = null;
    private JRFillField field_BLI_ITEM_AMOUNT = null;
    private JRFillField field_BLL_ID_PAT = null;
    private JRFillField field_BLL_PAT_NAME = null;
    private JRFillField field_BLL_ID = null;
    private JRFillField field_BLL_UPDATE = null;
    private JRFillField field_BLI_ID_PRICE = null;
    private JRFillField field_BLI_QTY = null;
    private JRFillField field_BLP_USR_ID_A = null;
    private JRFillField field_BLI_ITEM_DESC = null;
    private JRFillVariable variable_PAGE_NUMBER = null;
    private JRFillVariable variable_COLUMN_NUMBER = null;
    private JRFillVariable variable_REPORT_COUNT = null;
    private JRFillVariable variable_PAGE_COUNT = null;
    private JRFillVariable variable_COLUMN_COUNT = null;
    private JRFillVariable variable_TOTAL_ITEM = null;
    private JRFillVariable variable_TOTAL_BILL = null;


    /**
     *
     */
    public void customizedInit(
        Map pm,
        Map fm,
        Map vm
        )
    {
        initParams(pm);
        initFields(fm);
        initVars(vm);
    }


    /**
     *
     */
    private void initParams(Map pm)
    {
        parameter_IS_IGNORE_PAGINATION = (JRFillParameter)pm.get("IS_IGNORE_PAGINATION");
        parameter_REPORT_CONNECTION = (JRFillParameter)pm.get("REPORT_CONNECTION");
        parameter_Email = (JRFillParameter)pm.get("Email");
        parameter_Address = (JRFillParameter)pm.get("Address");
        parameter_Telephone = (JRFillParameter)pm.get("Telephone");
        parameter_REPORT_LOCALE = (JRFillParameter)pm.get("REPORT_LOCALE");
        parameter_REPORT_TIME_ZONE = (JRFillParameter)pm.get("REPORT_TIME_ZONE");
        parameter_REPORT_TEMPLATES = (JRFillParameter)pm.get("REPORT_TEMPLATES");
        parameter_SUBREPORT_DIR = (JRFillParameter)pm.get("SUBREPORT_DIR");
        parameter_City = (JRFillParameter)pm.get("City");
        parameter_REPORT_MAX_COUNT = (JRFillParameter)pm.get("REPORT_MAX_COUNT");
        parameter_REPORT_SCRIPTLET = (JRFillParameter)pm.get("REPORT_SCRIPTLET");
        parameter_REPORT_FILE_RESOLVER = (JRFillParameter)pm.get("REPORT_FILE_RESOLVER");
        parameter_REPORT_FORMAT_FACTORY = (JRFillParameter)pm.get("REPORT_FORMAT_FACTORY");
        parameter_REPORT_PARAMETERS_MAP = (JRFillParameter)pm.get("REPORT_PARAMETERS_MAP");
        parameter_REPORT_RESOURCE_BUNDLE = (JRFillParameter)pm.get("REPORT_RESOURCE_BUNDLE");
        parameter_billID = (JRFillParameter)pm.get("billID");
        parameter_REPORT_DATA_SOURCE = (JRFillParameter)pm.get("REPORT_DATA_SOURCE");
        parameter_REPORT_CLASS_LOADER = (JRFillParameter)pm.get("REPORT_CLASS_LOADER");
        parameter_REPORT_URL_HANDLER_FACTORY = (JRFillParameter)pm.get("REPORT_URL_HANDLER_FACTORY");
        parameter_REPORT_VIRTUALIZER = (JRFillParameter)pm.get("REPORT_VIRTUALIZER");
        parameter_Hospital = (JRFillParameter)pm.get("Hospital");
    }


    /**
     *
     */
    private void initFields(Map fm)
    {
        field_BLL_USR_ID_A = (JRFillField)fm.get("BLL_USR_ID_A");
        field_BLI_ITEM_AMOUNT = (JRFillField)fm.get("BLI_ITEM_AMOUNT");
        field_BLL_ID_PAT = (JRFillField)fm.get("BLL_ID_PAT");
        field_BLL_PAT_NAME = (JRFillField)fm.get("BLL_PAT_NAME");
        field_BLL_ID = (JRFillField)fm.get("BLL_ID");
        field_BLL_UPDATE = (JRFillField)fm.get("BLL_UPDATE");
        field_BLI_ID_PRICE = (JRFillField)fm.get("BLI_ID_PRICE");
        field_BLI_QTY = (JRFillField)fm.get("BLI_QTY");
        field_BLP_USR_ID_A = (JRFillField)fm.get("BLP_USR_ID_A");
        field_BLI_ITEM_DESC = (JRFillField)fm.get("BLI_ITEM_DESC");
    }


    /**
     *
     */
    private void initVars(Map vm)
    {
        variable_PAGE_NUMBER = (JRFillVariable)vm.get("PAGE_NUMBER");
        variable_COLUMN_NUMBER = (JRFillVariable)vm.get("COLUMN_NUMBER");
        variable_REPORT_COUNT = (JRFillVariable)vm.get("REPORT_COUNT");
        variable_PAGE_COUNT = (JRFillVariable)vm.get("PAGE_COUNT");
        variable_COLUMN_COUNT = (JRFillVariable)vm.get("COLUMN_COUNT");
        variable_TOTAL_ITEM = (JRFillVariable)vm.get("TOTAL_ITEM");
        variable_TOTAL_BILL = (JRFillVariable)vm.get("TOTAL_BILL");
    }


    /**
     *
     */
    public Object evaluate(int id) throws Throwable
    {
        Object value = null;

        switch (id)
        {
            case 0 : 
            {
                value = (java.lang.String)("H�pital Saint vincent de Paul");//$JR_EXPR_ID=0$
                break;
            }
            case 1 : 
            {
                value = (java.lang.String)("Tinfem");//$JR_EXPR_ID=1$
                break;
            }
            case 2 : 
            {
                value = (java.lang.String)("Dschang");//$JR_EXPR_ID=2$
                break;
            }
            case 3 : 
            {
                value = (java.lang.String)("hsvp@gmail.com");//$JR_EXPR_ID=3$
                break;
            }
            case 4 : 
            {
                value = (java.lang.String)("237677354920");//$JR_EXPR_ID=4$
                break;
            }
            case 5 : 
            {
                value = (java.lang.String)("./rpt/");//$JR_EXPR_ID=5$
                break;
            }
            case 6 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=6$
                break;
            }
            case 7 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=7$
                break;
            }
            case 8 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=8$
                break;
            }
            case 9 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=9$
                break;
            }
            case 10 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=10$
                break;
            }
            case 11 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=11$
                break;
            }
            case 12 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=12$
                break;
            }
            case 13 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=13$
                break;
            }
            case 14 : 
            {
                value = (java.lang.Double)(new Double((((java.lang.Double)field_BLI_ITEM_AMOUNT.getValue()).doubleValue()) *(((java.lang.Double)field_BLI_QTY.getValue()).doubleValue())));//$JR_EXPR_ID=14$
                break;
            }
            case 15 : 
            {
                value = (java.lang.Double)(((java.lang.Double)variable_TOTAL_ITEM.getValue()));//$JR_EXPR_ID=15$
                break;
            }
            case 16 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_Hospital.getValue()));//$JR_EXPR_ID=16$
                break;
            }
            case 17 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_Address.getValue()) + " - " + ((java.lang.String)parameter_City.getValue()));//$JR_EXPR_ID=17$
                break;
            }
            case 18 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_Telephone.getValue()));//$JR_EXPR_ID=18$
                break;
            }
            case 19 : 
            {
                value = (java.lang.String)(((java.util.ResourceBundle)parameter_REPORT_RESOURCE_BUNDLE.getValue()).getString( "angal.report.patientbill.billn") + ((java.lang.String)parameter_billID.getValue()));//$JR_EXPR_ID=19$
                break;
            }
            case 20 : 
            {
                value = (java.lang.String)(((java.sql.Timestamp)field_BLL_UPDATE.getValue()).toLocaleString());//$JR_EXPR_ID=20$
                break;
            }
            case 21 : 
            {
                value = (java.lang.String)(((java.util.ResourceBundle)parameter_REPORT_RESOURCE_BUNDLE.getValue()).getString( "angal.report.patientbillextendedtxt.patn" ) + ((java.lang.Integer)field_BLL_ID_PAT.getValue()) + " - " + ((java.lang.String)field_BLL_PAT_NAME.getValue()));//$JR_EXPR_ID=21$
                break;
            }
            case 22 : 
            {
                value = (java.lang.String)(((java.util.ResourceBundle)parameter_REPORT_RESOURCE_BUNDLE.getValue()).getString( "angal.report.patientbill.cashier" ) + ((java.lang.String)field_BLL_USR_ID_A.getValue()));//$JR_EXPR_ID=22$
                break;
            }
            case 23 : 
            {
                value = (java.lang.String)("TOTAL");//$JR_EXPR_ID=23$
                break;
            }
            case 24 : 
            {
                value = (java.lang.String)("QTE");//$JR_EXPR_ID=24$
                break;
            }
            case 25 : 
            {
                value = (java.lang.String)("DESIGNATION");//$JR_EXPR_ID=25$
                break;
            }
            case 26 : 
            {
                value = (java.lang.String)(((java.lang.String)field_BLI_ITEM_DESC.getValue())//$JR_EXPR_ID=26$
.replaceAll("Consultation", "Cons.")//$JR_EXPR_ID=26$
.replaceAll("Paracetamol", "Para.")//$JR_EXPR_ID=26$
.replaceAll("Culture Antibiogramme", "Cul.A.")//$JR_EXPR_ID=26$
.replaceAll("Glucose", "Gluc.")//$JR_EXPR_ID=26$
.replaceAll("H�moglobine", "H�mogl.")//$JR_EXPR_ID=26$
.replaceAll("H�patite", "H�pt.")//$JR_EXPR_ID=26$
.replaceAll("Temps", "Tps.")//$JR_EXPR_ID=26$
.replaceAll("Accouchement", "Accouch.")//$JR_EXPR_ID=26$
.replaceAll("Couveuse", "Couv.")//$JR_EXPR_ID=26$
.replaceAll("Curetage", "Curet.")//$JR_EXPR_ID=26$
.replaceAll("Echographie", "Echo.")//$JR_EXPR_ID=26$
.replaceAll("Episiotomie", "Episio.")//$JR_EXPR_ID=26$
.replaceAll("Extraction", "Extr.")//$JR_EXPR_ID=26$
.replaceAll("Manipulation", "Manip.")//$JR_EXPR_ID=26$
.replaceAll("Obturation", "Obtu.")//$JR_EXPR_ID=26$
.replaceAll("Th�rapie par UV", "Th�ra. UV.")//$JR_EXPR_ID=26$
.replaceAll("Vaccin", "Vacc.")//$JR_EXPR_ID=26$
.replaceAll("Amoxicilline", "Amox.")//$JR_EXPR_ID=26$
.replaceAll("Artemether", "Arth.")//$JR_EXPR_ID=26$
.replaceAll("Arthemeter", "Arth.")//$JR_EXPR_ID=26$
.replaceAll("Betadine", "Beta.")//$JR_EXPR_ID=26$
.replaceAll("Chlorpromazine", "Chlorpro.")//$JR_EXPR_ID=26$
.replaceAll("Ciprofloxacine", "Ciproflo.")//$JR_EXPR_ID=26$
.replaceAll("Cloxacilline", "Cloxa.")//$JR_EXPR_ID=26$
.replaceAll("Cotrimoxazole", "Cotrim.")//$JR_EXPR_ID=26$
.replaceAll("Depakine", "Depak.")//$JR_EXPR_ID=26$
.replaceAll("Dexamethasone", "Dexa.")//$JR_EXPR_ID=26$
.replaceAll("Volgalene", "Volga.")//$JR_EXPR_ID=26$
.replaceAll("Vitamine", "Vit.")//$JR_EXPR_ID=26$
.replaceAll("Tubulure", "Tubu.")//$JR_EXPR_ID=26$
.replaceAll("Spasfon(R)", "Spasf. R")//$JR_EXPR_ID=26$
.replaceAll("Salbutamol", "Salbu.")//$JR_EXPR_ID=26$
.replaceAll("Sachet", "Sc.")//$JR_EXPR_ID=26$
.replaceAll("Quinine", "Quin.")//$JR_EXPR_ID=26$
.replaceAll("Phenobarbital", "Phenob.")//$JR_EXPR_ID=26$
.replaceAll("Paracetamol", "Para.")//$JR_EXPR_ID=26$
.replaceAll("Omeprazole", "Omepra.")//$JR_EXPR_ID=26$
.replaceAll("Ofloxacine", "Ofloxa.")//$JR_EXPR_ID=26$
.replaceAll("Nystanine", "Nista.")//$JR_EXPR_ID=26$
.replaceAll("Normoptic", "Normo.")//$JR_EXPR_ID=26$
.replaceAll("Nifluril", "Niflu.")//$JR_EXPR_ID=26$
.replaceAll("Multivitamine", "Multivit.")//$JR_EXPR_ID=26$
.replaceAll("Metronidazole", "Metroni.")//$JR_EXPR_ID=26$
.replaceAll("Metoclopramide", "Metoclo.")//$JR_EXPR_ID=26$
.replaceAll("Mebendazole", "Mebend.")//$JR_EXPR_ID=26$
.replaceAll("Gentamicine", "Genta.")//$JR_EXPR_ID=26$
.replaceAll("Furosemide", "Furo.")//$JR_EXPR_ID=26$
.replaceAll("Frakidex", "Fraki.")//$JR_EXPR_ID=26$
.replaceAll("Fleming", "Flem.")//$JR_EXPR_ID=26$
.replaceAll("Erythromicine", "Erythro.")//$JR_EXPR_ID=26$
.replaceAll("Efferalgan", "Eff.")//$JR_EXPR_ID=26$
.replaceAll("Eau pour preparation injecetable,", "Eau prepa inj.")//$JR_EXPR_ID=26$
.replaceAll("Eau pour preparation injectable", "Eau prepa inj.")//$JR_EXPR_ID=26$
.replaceAll("Diclofenac", "Diclof.")//$JR_EXPR_ID=26$
.replaceAll("Dexamethasone", "Dexameth."));//$JR_EXPR_ID=26$
                break;
            }
            case 27 : 
            {
                value = (java.lang.Double)(((java.lang.Double)variable_TOTAL_ITEM.getValue()));//$JR_EXPR_ID=27$
                break;
            }
            case 28 : 
            {
                value = (java.lang.Double)(((java.lang.Double)field_BLI_QTY.getValue()));//$JR_EXPR_ID=28$
                break;
            }
            case 29 : 
            {
                value = (java.lang.Double)(((java.lang.Double)variable_TOTAL_BILL.getValue()));//$JR_EXPR_ID=29$
                break;
            }
            case 30 : 
            {
                value = (java.lang.String)("TOTAL");//$JR_EXPR_ID=30$
                break;
            }
            case 31 : 
            {
                value = (java.lang.Object)(((java.util.ResourceBundle)parameter_REPORT_RESOURCE_BUNDLE.getValue()));//$JR_EXPR_ID=31$
                break;
            }
            case 32 : 
            {
                value = (java.lang.Object)(((java.lang.String)parameter_billID.getValue()));//$JR_EXPR_ID=32$
                break;
            }
            case 33 : 
            {
                value = (java.sql.Connection)(((java.sql.Connection)parameter_REPORT_CONNECTION.getValue()));//$JR_EXPR_ID=33$
                break;
            }
            case 34 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_SUBREPORT_DIR.getValue()) + "PatientBillPaymentsSubTXT.jasper");//$JR_EXPR_ID=34$
                break;
            }
           default :
           {
           }
        }
        
        return value;
    }


    /**
     *
     */
    public Object evaluateOld(int id) throws Throwable
    {
        Object value = null;

        switch (id)
        {
            case 0 : 
            {
                value = (java.lang.String)("H�pital Saint vincent de Paul");//$JR_EXPR_ID=0$
                break;
            }
            case 1 : 
            {
                value = (java.lang.String)("Tinfem");//$JR_EXPR_ID=1$
                break;
            }
            case 2 : 
            {
                value = (java.lang.String)("Dschang");//$JR_EXPR_ID=2$
                break;
            }
            case 3 : 
            {
                value = (java.lang.String)("hsvp@gmail.com");//$JR_EXPR_ID=3$
                break;
            }
            case 4 : 
            {
                value = (java.lang.String)("237677354920");//$JR_EXPR_ID=4$
                break;
            }
            case 5 : 
            {
                value = (java.lang.String)("./rpt/");//$JR_EXPR_ID=5$
                break;
            }
            case 6 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=6$
                break;
            }
            case 7 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=7$
                break;
            }
            case 8 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=8$
                break;
            }
            case 9 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=9$
                break;
            }
            case 10 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=10$
                break;
            }
            case 11 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=11$
                break;
            }
            case 12 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=12$
                break;
            }
            case 13 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=13$
                break;
            }
            case 14 : 
            {
                value = (java.lang.Double)(new Double((((java.lang.Double)field_BLI_ITEM_AMOUNT.getOldValue()).doubleValue()) *(((java.lang.Double)field_BLI_QTY.getOldValue()).doubleValue())));//$JR_EXPR_ID=14$
                break;
            }
            case 15 : 
            {
                value = (java.lang.Double)(((java.lang.Double)variable_TOTAL_ITEM.getOldValue()));//$JR_EXPR_ID=15$
                break;
            }
            case 16 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_Hospital.getValue()));//$JR_EXPR_ID=16$
                break;
            }
            case 17 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_Address.getValue()) + " - " + ((java.lang.String)parameter_City.getValue()));//$JR_EXPR_ID=17$
                break;
            }
            case 18 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_Telephone.getValue()));//$JR_EXPR_ID=18$
                break;
            }
            case 19 : 
            {
                value = (java.lang.String)(((java.util.ResourceBundle)parameter_REPORT_RESOURCE_BUNDLE.getValue()).getString( "angal.report.patientbill.billn") + ((java.lang.String)parameter_billID.getValue()));//$JR_EXPR_ID=19$
                break;
            }
            case 20 : 
            {
                value = (java.lang.String)(((java.sql.Timestamp)field_BLL_UPDATE.getOldValue()).toLocaleString());//$JR_EXPR_ID=20$
                break;
            }
            case 21 : 
            {
                value = (java.lang.String)(((java.util.ResourceBundle)parameter_REPORT_RESOURCE_BUNDLE.getValue()).getString( "angal.report.patientbillextendedtxt.patn" ) + ((java.lang.Integer)field_BLL_ID_PAT.getOldValue()) + " - " + ((java.lang.String)field_BLL_PAT_NAME.getOldValue()));//$JR_EXPR_ID=21$
                break;
            }
            case 22 : 
            {
                value = (java.lang.String)(((java.util.ResourceBundle)parameter_REPORT_RESOURCE_BUNDLE.getValue()).getString( "angal.report.patientbill.cashier" ) + ((java.lang.String)field_BLL_USR_ID_A.getOldValue()));//$JR_EXPR_ID=22$
                break;
            }
            case 23 : 
            {
                value = (java.lang.String)("TOTAL");//$JR_EXPR_ID=23$
                break;
            }
            case 24 : 
            {
                value = (java.lang.String)("QTE");//$JR_EXPR_ID=24$
                break;
            }
            case 25 : 
            {
                value = (java.lang.String)("DESIGNATION");//$JR_EXPR_ID=25$
                break;
            }
            case 26 : 
            {
                value = (java.lang.String)(((java.lang.String)field_BLI_ITEM_DESC.getOldValue())//$JR_EXPR_ID=26$
.replaceAll("Consultation", "Cons.")//$JR_EXPR_ID=26$
.replaceAll("Paracetamol", "Para.")//$JR_EXPR_ID=26$
.replaceAll("Culture Antibiogramme", "Cul.A.")//$JR_EXPR_ID=26$
.replaceAll("Glucose", "Gluc.")//$JR_EXPR_ID=26$
.replaceAll("H�moglobine", "H�mogl.")//$JR_EXPR_ID=26$
.replaceAll("H�patite", "H�pt.")//$JR_EXPR_ID=26$
.replaceAll("Temps", "Tps.")//$JR_EXPR_ID=26$
.replaceAll("Accouchement", "Accouch.")//$JR_EXPR_ID=26$
.replaceAll("Couveuse", "Couv.")//$JR_EXPR_ID=26$
.replaceAll("Curetage", "Curet.")//$JR_EXPR_ID=26$
.replaceAll("Echographie", "Echo.")//$JR_EXPR_ID=26$
.replaceAll("Episiotomie", "Episio.")//$JR_EXPR_ID=26$
.replaceAll("Extraction", "Extr.")//$JR_EXPR_ID=26$
.replaceAll("Manipulation", "Manip.")//$JR_EXPR_ID=26$
.replaceAll("Obturation", "Obtu.")//$JR_EXPR_ID=26$
.replaceAll("Th�rapie par UV", "Th�ra. UV.")//$JR_EXPR_ID=26$
.replaceAll("Vaccin", "Vacc.")//$JR_EXPR_ID=26$
.replaceAll("Amoxicilline", "Amox.")//$JR_EXPR_ID=26$
.replaceAll("Artemether", "Arth.")//$JR_EXPR_ID=26$
.replaceAll("Arthemeter", "Arth.")//$JR_EXPR_ID=26$
.replaceAll("Betadine", "Beta.")//$JR_EXPR_ID=26$
.replaceAll("Chlorpromazine", "Chlorpro.")//$JR_EXPR_ID=26$
.replaceAll("Ciprofloxacine", "Ciproflo.")//$JR_EXPR_ID=26$
.replaceAll("Cloxacilline", "Cloxa.")//$JR_EXPR_ID=26$
.replaceAll("Cotrimoxazole", "Cotrim.")//$JR_EXPR_ID=26$
.replaceAll("Depakine", "Depak.")//$JR_EXPR_ID=26$
.replaceAll("Dexamethasone", "Dexa.")//$JR_EXPR_ID=26$
.replaceAll("Volgalene", "Volga.")//$JR_EXPR_ID=26$
.replaceAll("Vitamine", "Vit.")//$JR_EXPR_ID=26$
.replaceAll("Tubulure", "Tubu.")//$JR_EXPR_ID=26$
.replaceAll("Spasfon(R)", "Spasf. R")//$JR_EXPR_ID=26$
.replaceAll("Salbutamol", "Salbu.")//$JR_EXPR_ID=26$
.replaceAll("Sachet", "Sc.")//$JR_EXPR_ID=26$
.replaceAll("Quinine", "Quin.")//$JR_EXPR_ID=26$
.replaceAll("Phenobarbital", "Phenob.")//$JR_EXPR_ID=26$
.replaceAll("Paracetamol", "Para.")//$JR_EXPR_ID=26$
.replaceAll("Omeprazole", "Omepra.")//$JR_EXPR_ID=26$
.replaceAll("Ofloxacine", "Ofloxa.")//$JR_EXPR_ID=26$
.replaceAll("Nystanine", "Nista.")//$JR_EXPR_ID=26$
.replaceAll("Normoptic", "Normo.")//$JR_EXPR_ID=26$
.replaceAll("Nifluril", "Niflu.")//$JR_EXPR_ID=26$
.replaceAll("Multivitamine", "Multivit.")//$JR_EXPR_ID=26$
.replaceAll("Metronidazole", "Metroni.")//$JR_EXPR_ID=26$
.replaceAll("Metoclopramide", "Metoclo.")//$JR_EXPR_ID=26$
.replaceAll("Mebendazole", "Mebend.")//$JR_EXPR_ID=26$
.replaceAll("Gentamicine", "Genta.")//$JR_EXPR_ID=26$
.replaceAll("Furosemide", "Furo.")//$JR_EXPR_ID=26$
.replaceAll("Frakidex", "Fraki.")//$JR_EXPR_ID=26$
.replaceAll("Fleming", "Flem.")//$JR_EXPR_ID=26$
.replaceAll("Erythromicine", "Erythro.")//$JR_EXPR_ID=26$
.replaceAll("Efferalgan", "Eff.")//$JR_EXPR_ID=26$
.replaceAll("Eau pour preparation injecetable,", "Eau prepa inj.")//$JR_EXPR_ID=26$
.replaceAll("Eau pour preparation injectable", "Eau prepa inj.")//$JR_EXPR_ID=26$
.replaceAll("Diclofenac", "Diclof.")//$JR_EXPR_ID=26$
.replaceAll("Dexamethasone", "Dexameth."));//$JR_EXPR_ID=26$
                break;
            }
            case 27 : 
            {
                value = (java.lang.Double)(((java.lang.Double)variable_TOTAL_ITEM.getOldValue()));//$JR_EXPR_ID=27$
                break;
            }
            case 28 : 
            {
                value = (java.lang.Double)(((java.lang.Double)field_BLI_QTY.getOldValue()));//$JR_EXPR_ID=28$
                break;
            }
            case 29 : 
            {
                value = (java.lang.Double)(((java.lang.Double)variable_TOTAL_BILL.getOldValue()));//$JR_EXPR_ID=29$
                break;
            }
            case 30 : 
            {
                value = (java.lang.String)("TOTAL");//$JR_EXPR_ID=30$
                break;
            }
            case 31 : 
            {
                value = (java.lang.Object)(((java.util.ResourceBundle)parameter_REPORT_RESOURCE_BUNDLE.getValue()));//$JR_EXPR_ID=31$
                break;
            }
            case 32 : 
            {
                value = (java.lang.Object)(((java.lang.String)parameter_billID.getValue()));//$JR_EXPR_ID=32$
                break;
            }
            case 33 : 
            {
                value = (java.sql.Connection)(((java.sql.Connection)parameter_REPORT_CONNECTION.getValue()));//$JR_EXPR_ID=33$
                break;
            }
            case 34 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_SUBREPORT_DIR.getValue()) + "PatientBillPaymentsSubTXT.jasper");//$JR_EXPR_ID=34$
                break;
            }
           default :
           {
           }
        }
        
        return value;
    }


    /**
     *
     */
    public Object evaluateEstimated(int id) throws Throwable
    {
        Object value = null;

        switch (id)
        {
            case 0 : 
            {
                value = (java.lang.String)("H�pital Saint vincent de Paul");//$JR_EXPR_ID=0$
                break;
            }
            case 1 : 
            {
                value = (java.lang.String)("Tinfem");//$JR_EXPR_ID=1$
                break;
            }
            case 2 : 
            {
                value = (java.lang.String)("Dschang");//$JR_EXPR_ID=2$
                break;
            }
            case 3 : 
            {
                value = (java.lang.String)("hsvp@gmail.com");//$JR_EXPR_ID=3$
                break;
            }
            case 4 : 
            {
                value = (java.lang.String)("237677354920");//$JR_EXPR_ID=4$
                break;
            }
            case 5 : 
            {
                value = (java.lang.String)("./rpt/");//$JR_EXPR_ID=5$
                break;
            }
            case 6 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=6$
                break;
            }
            case 7 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=7$
                break;
            }
            case 8 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=8$
                break;
            }
            case 9 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=9$
                break;
            }
            case 10 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=10$
                break;
            }
            case 11 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=11$
                break;
            }
            case 12 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=12$
                break;
            }
            case 13 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=13$
                break;
            }
            case 14 : 
            {
                value = (java.lang.Double)(new Double((((java.lang.Double)field_BLI_ITEM_AMOUNT.getValue()).doubleValue()) *(((java.lang.Double)field_BLI_QTY.getValue()).doubleValue())));//$JR_EXPR_ID=14$
                break;
            }
            case 15 : 
            {
                value = (java.lang.Double)(((java.lang.Double)variable_TOTAL_ITEM.getEstimatedValue()));//$JR_EXPR_ID=15$
                break;
            }
            case 16 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_Hospital.getValue()));//$JR_EXPR_ID=16$
                break;
            }
            case 17 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_Address.getValue()) + " - " + ((java.lang.String)parameter_City.getValue()));//$JR_EXPR_ID=17$
                break;
            }
            case 18 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_Telephone.getValue()));//$JR_EXPR_ID=18$
                break;
            }
            case 19 : 
            {
                value = (java.lang.String)(((java.util.ResourceBundle)parameter_REPORT_RESOURCE_BUNDLE.getValue()).getString( "angal.report.patientbill.billn") + ((java.lang.String)parameter_billID.getValue()));//$JR_EXPR_ID=19$
                break;
            }
            case 20 : 
            {
                value = (java.lang.String)(((java.sql.Timestamp)field_BLL_UPDATE.getValue()).toLocaleString());//$JR_EXPR_ID=20$
                break;
            }
            case 21 : 
            {
                value = (java.lang.String)(((java.util.ResourceBundle)parameter_REPORT_RESOURCE_BUNDLE.getValue()).getString( "angal.report.patientbillextendedtxt.patn" ) + ((java.lang.Integer)field_BLL_ID_PAT.getValue()) + " - " + ((java.lang.String)field_BLL_PAT_NAME.getValue()));//$JR_EXPR_ID=21$
                break;
            }
            case 22 : 
            {
                value = (java.lang.String)(((java.util.ResourceBundle)parameter_REPORT_RESOURCE_BUNDLE.getValue()).getString( "angal.report.patientbill.cashier" ) + ((java.lang.String)field_BLL_USR_ID_A.getValue()));//$JR_EXPR_ID=22$
                break;
            }
            case 23 : 
            {
                value = (java.lang.String)("TOTAL");//$JR_EXPR_ID=23$
                break;
            }
            case 24 : 
            {
                value = (java.lang.String)("QTE");//$JR_EXPR_ID=24$
                break;
            }
            case 25 : 
            {
                value = (java.lang.String)("DESIGNATION");//$JR_EXPR_ID=25$
                break;
            }
            case 26 : 
            {
                value = (java.lang.String)(((java.lang.String)field_BLI_ITEM_DESC.getValue())//$JR_EXPR_ID=26$
.replaceAll("Consultation", "Cons.")//$JR_EXPR_ID=26$
.replaceAll("Paracetamol", "Para.")//$JR_EXPR_ID=26$
.replaceAll("Culture Antibiogramme", "Cul.A.")//$JR_EXPR_ID=26$
.replaceAll("Glucose", "Gluc.")//$JR_EXPR_ID=26$
.replaceAll("H�moglobine", "H�mogl.")//$JR_EXPR_ID=26$
.replaceAll("H�patite", "H�pt.")//$JR_EXPR_ID=26$
.replaceAll("Temps", "Tps.")//$JR_EXPR_ID=26$
.replaceAll("Accouchement", "Accouch.")//$JR_EXPR_ID=26$
.replaceAll("Couveuse", "Couv.")//$JR_EXPR_ID=26$
.replaceAll("Curetage", "Curet.")//$JR_EXPR_ID=26$
.replaceAll("Echographie", "Echo.")//$JR_EXPR_ID=26$
.replaceAll("Episiotomie", "Episio.")//$JR_EXPR_ID=26$
.replaceAll("Extraction", "Extr.")//$JR_EXPR_ID=26$
.replaceAll("Manipulation", "Manip.")//$JR_EXPR_ID=26$
.replaceAll("Obturation", "Obtu.")//$JR_EXPR_ID=26$
.replaceAll("Th�rapie par UV", "Th�ra. UV.")//$JR_EXPR_ID=26$
.replaceAll("Vaccin", "Vacc.")//$JR_EXPR_ID=26$
.replaceAll("Amoxicilline", "Amox.")//$JR_EXPR_ID=26$
.replaceAll("Artemether", "Arth.")//$JR_EXPR_ID=26$
.replaceAll("Arthemeter", "Arth.")//$JR_EXPR_ID=26$
.replaceAll("Betadine", "Beta.")//$JR_EXPR_ID=26$
.replaceAll("Chlorpromazine", "Chlorpro.")//$JR_EXPR_ID=26$
.replaceAll("Ciprofloxacine", "Ciproflo.")//$JR_EXPR_ID=26$
.replaceAll("Cloxacilline", "Cloxa.")//$JR_EXPR_ID=26$
.replaceAll("Cotrimoxazole", "Cotrim.")//$JR_EXPR_ID=26$
.replaceAll("Depakine", "Depak.")//$JR_EXPR_ID=26$
.replaceAll("Dexamethasone", "Dexa.")//$JR_EXPR_ID=26$
.replaceAll("Volgalene", "Volga.")//$JR_EXPR_ID=26$
.replaceAll("Vitamine", "Vit.")//$JR_EXPR_ID=26$
.replaceAll("Tubulure", "Tubu.")//$JR_EXPR_ID=26$
.replaceAll("Spasfon(R)", "Spasf. R")//$JR_EXPR_ID=26$
.replaceAll("Salbutamol", "Salbu.")//$JR_EXPR_ID=26$
.replaceAll("Sachet", "Sc.")//$JR_EXPR_ID=26$
.replaceAll("Quinine", "Quin.")//$JR_EXPR_ID=26$
.replaceAll("Phenobarbital", "Phenob.")//$JR_EXPR_ID=26$
.replaceAll("Paracetamol", "Para.")//$JR_EXPR_ID=26$
.replaceAll("Omeprazole", "Omepra.")//$JR_EXPR_ID=26$
.replaceAll("Ofloxacine", "Ofloxa.")//$JR_EXPR_ID=26$
.replaceAll("Nystanine", "Nista.")//$JR_EXPR_ID=26$
.replaceAll("Normoptic", "Normo.")//$JR_EXPR_ID=26$
.replaceAll("Nifluril", "Niflu.")//$JR_EXPR_ID=26$
.replaceAll("Multivitamine", "Multivit.")//$JR_EXPR_ID=26$
.replaceAll("Metronidazole", "Metroni.")//$JR_EXPR_ID=26$
.replaceAll("Metoclopramide", "Metoclo.")//$JR_EXPR_ID=26$
.replaceAll("Mebendazole", "Mebend.")//$JR_EXPR_ID=26$
.replaceAll("Gentamicine", "Genta.")//$JR_EXPR_ID=26$
.replaceAll("Furosemide", "Furo.")//$JR_EXPR_ID=26$
.replaceAll("Frakidex", "Fraki.")//$JR_EXPR_ID=26$
.replaceAll("Fleming", "Flem.")//$JR_EXPR_ID=26$
.replaceAll("Erythromicine", "Erythro.")//$JR_EXPR_ID=26$
.replaceAll("Efferalgan", "Eff.")//$JR_EXPR_ID=26$
.replaceAll("Eau pour preparation injecetable,", "Eau prepa inj.")//$JR_EXPR_ID=26$
.replaceAll("Eau pour preparation injectable", "Eau prepa inj.")//$JR_EXPR_ID=26$
.replaceAll("Diclofenac", "Diclof.")//$JR_EXPR_ID=26$
.replaceAll("Dexamethasone", "Dexameth."));//$JR_EXPR_ID=26$
                break;
            }
            case 27 : 
            {
                value = (java.lang.Double)(((java.lang.Double)variable_TOTAL_ITEM.getEstimatedValue()));//$JR_EXPR_ID=27$
                break;
            }
            case 28 : 
            {
                value = (java.lang.Double)(((java.lang.Double)field_BLI_QTY.getValue()));//$JR_EXPR_ID=28$
                break;
            }
            case 29 : 
            {
                value = (java.lang.Double)(((java.lang.Double)variable_TOTAL_BILL.getEstimatedValue()));//$JR_EXPR_ID=29$
                break;
            }
            case 30 : 
            {
                value = (java.lang.String)("TOTAL");//$JR_EXPR_ID=30$
                break;
            }
            case 31 : 
            {
                value = (java.lang.Object)(((java.util.ResourceBundle)parameter_REPORT_RESOURCE_BUNDLE.getValue()));//$JR_EXPR_ID=31$
                break;
            }
            case 32 : 
            {
                value = (java.lang.Object)(((java.lang.String)parameter_billID.getValue()));//$JR_EXPR_ID=32$
                break;
            }
            case 33 : 
            {
                value = (java.sql.Connection)(((java.sql.Connection)parameter_REPORT_CONNECTION.getValue()));//$JR_EXPR_ID=33$
                break;
            }
            case 34 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_SUBREPORT_DIR.getValue()) + "PatientBillPaymentsSubTXT.jasper");//$JR_EXPR_ID=34$
                break;
            }
           default :
           {
           }
        }
        
        return value;
    }


}