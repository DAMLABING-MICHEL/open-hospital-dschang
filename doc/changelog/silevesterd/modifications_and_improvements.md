# Modifications, bugs fixed and improvements performed by Silevestre D.

## 13/06/2022

1. Fix bug related to automatic cut off receipt after printing
    - Update service printing manager [org.isf.serviceprinting.manager.PrintReceipt ].

## 14/06/2022

2. Refunds update
    - Add column 'BLL_PARENT_ID' in bills's table [mysql/db/migrations/refunds_update/update_bills_table_structure_add_parent_bill_id.sql ]. 
    - Add props 'parentId' and 'refundAmount' in bill model [org.isf.accounting.model.Bill ];
    - Add new constructor with parentId param.
    - Modify refund button in BillBrowser UI;
    - Implements [org.isf.accounting.gui.BillRefund.PatientRefundBillListener ].
    - Add new translation keys [
        angal.billbrowser.pleaseselectabill, angal.billbrowser.onlyclosedbillcanberefunded, angal.billrefund.title,
        angal.billrefund.amountorefound, angal.billrefund.helptext, angal.billrefund.refundqty, angal.billrefund.qty,
        angal.billrefund.item, angal.billrefund.amount, angal.billrefund.cancel, angal.billrefund.cancelrefund,
        angal.billrefund.pleaseselectitemtoberefunded, angal.billrefund.failedtosaverefund, 
        angal.billrefund.refundqtygreatthanrefundableqty, angal.billbrowser.cannotrefundarefundbill, angal.billrefund.refundedqty, angal.billbrowser.refunded, angal.billbrowser.todaypayments, angal.billbrowser.periodpayments, angal.billbrowser.userpayments, angal.report.patientbill.unitpriceshort, 
        angal.report.billsreport.refundedquantityshort, angal.report.patientbillextended.refunds
    ].
    - Add method getGroupItems() [org.isf.accounting.manager.BillBrowserManager.getGroupItems(int billID)];
    - Add method getOnlyRefundItems() [org.isf.accounting.manager.BillBrowserManager.getOnlyRefundItems(int billID)];
    - Add method getRefundBills() [org.isf.accounting.manager.BillBrowserManager.getRefundBills(int billID)];
    - Add method getRefundItems() [org.isf.accounting.manager.BillBrowserManager.getRefundItems(int billID)];
    - Update all bill's method (methods used to retrieve bills);
    - Add method getGroupItems() [org.isf.accounting.service.IoOperations.getGroupItems(int billID)];
    - Add method getOnlyRefundItems() [org.isf.accounting.manager.IoOperations.getOnlyRefundItems(int billID)];
    - Add method getRefundBills() [org.isf.accounting.manager.IoOperations.getRefundBills(int billID)];
    - Add props 'refundedQty' and its getter and setter in BillItems model.
    - Update reports: 'Brief Rapport(seulement BadDebts factures)', "All incomes by price code" ("OH004" and "OH004-1"), "OH004-2", "OH004-3", 'All bills', "Bills grouped by reduction plan", "Bills Payment By User Report", "Patient Bill Receipt" (PDF and TXT), "All Incomes By Product and Service per year".
    - Create "Bills Refund By User Report" [rpt/BillsRefundReportUserAllInDate.jrxml ];

    - Create new parameter for Bills Refund Report [mysql/db/migrations/refunds_update/create_bills_refund_report_parameter.sql ];
    - Add database migrations scripts [mysql/db/create_all.sql ].

3. Update BillBrowser to show refunds summary only if the bill's items refund is allowed.
    - Update BillBrowser UI [src/org/isf/accounting/gui/BillBrowser.java ].

## 16/06/2022

4. Update Patient Bill report (PatientBillExtended) to add refunds infos
    - Add subreport [rpt/PatientBillRefunds.jrxml ];
    - Update fr and en translation bundles [angal.report.patientbillextended.refunds, angal.report.patientbillpayments.paid, angal.report.patientbillrefunds.title, angal.report.patientbillrefunds.totalrefunds];
    - Redesign PatientBillExtended report [rpt/PatientBillExtended.jrxml ];
    - Redesign subreport [rpt/PatientBillPayments.jrxml ].

## 19/09/2022 - 22/09/2022

5. Add pagination in bills browser

- Add lot of methods in BillBrowser UI [];
- Update BillBrowser Manager and Service, add methods countBills(String status, GregorianCalendar dateFrom, GregorianCalendar dateTo, User userGarant, BillItems billItem, Patient patient), getBills(String status, GregorianCalendar dateFrom, GregorianCalendar dateTo, User userGarant, BillItems billItem, Patient patient, int limit, int offset),  getStatsByPeriod(GregorianCalendar dateFrom, GregorianCalendar dateTo, String user).






    