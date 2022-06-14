package org.isf.accounting.model;

/**
 * This class is the model for refund bill's items
 * It adds new props to store the quantity and the amount of the item to be refunded. 
 * 
 * @author Silevester D., created at 17/05/2022
 *
 */
public class RefundBillItem {

	private BillItems billItem;
	private int refundedQty;
	private double refundedAmount;
	
	public RefundBillItem() {
		super();
	}
	
	public RefundBillItem(BillItems item) {
		super();
		this.billItem = item;
	}
	
	public RefundBillItem(BillItems item, int refundedQty) {
		super();
		this.billItem = item;
		this.refundedQty = refundedQty;
	}

	public int getRefundedQty() {
		return refundedQty;
	}

	public void setRefundedQty(int refundedQty) {
		this.refundedQty = refundedQty;
	}

	public double getRefundedAmount() {
		return refundedAmount;
	}

	public void setRefundedAmount(double refundedAmount) {
		this.refundedAmount = refundedAmount;
	}
	
	public BillItems getBillItem() {
		return billItem;
	}

	public void setBillItem(BillItems item) {
		this.billItem = item;
	}

}
