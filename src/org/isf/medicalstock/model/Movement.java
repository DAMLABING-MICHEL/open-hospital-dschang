package org.isf.medicalstock.model;

import java.util.GregorianCalendar;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.isf.medicals.model.Medical;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.supplier.model.Supplier;
import org.isf.utils.jobjects.DateAdapter;
import org.isf.ward.model.Ward;
import org.isf.generaldata.MessageBundle;

public class Movement {

	private int code;
	private Medical medical;
	private MovementType type;
	private Ward ward;
	private Lot lot;
	private GregorianCalendar date;
	private double quantity;
	private Supplier supplier;
	private String refNo;
	private Ward wardFrom;
	private double stockQtyInAfter;
	private double stockQtyOutAfter;
	//fields for history	
	private String create_by ; 
	private String modify_by ; 
	private GregorianCalendar create_date;
	private GregorianCalendar modify_date;
	
	public Movement(){
		super();
	}
	public Movement(Medical aMedical,MovementType aType, Ward aWard, Lot aLot,GregorianCalendar aDate,double aQuantity,Supplier aSupplier, String aRefNo, Ward wardFrom){
		medical = aMedical;
		type = aType;
		ward = aWard;
		lot = aLot;
		date = aDate;
		quantity = aQuantity;
		supplier = aSupplier;
		refNo=aRefNo;
		this.wardFrom = wardFrom;
	}
	public Movement(Medical aMedical,MovementType aType, Ward aWard, Lot aLot,GregorianCalendar aDate,double aQuantity,Supplier aSupplier, String aRefNo, Ward wardFrom, double aStockQtyInAfter,  double aStockQtyOutAfter){
		medical = aMedical;
		type = aType;
		ward = aWard;
		lot = aLot;
		date = aDate;
		quantity = aQuantity;
		supplier = aSupplier;
		refNo=aRefNo;
		this.wardFrom = wardFrom;
		this.stockQtyOutAfter = aStockQtyOutAfter;
		this.stockQtyInAfter = aStockQtyInAfter;
	}
	public Movement(Medical aMedical,MovementType aType,Ward aWard,Lot aLot,GregorianCalendar aDate,double aQuantity,Supplier aSupplier, String aRefNo){
		medical = aMedical;
		type = aType;
		ward = aWard;
		lot = aLot;
		date = aDate;
		quantity = aQuantity;
		supplier = aSupplier;
		refNo=aRefNo;
	}
	public Movement(Medical aMedical,MovementType aType,Ward aWard,Lot aLot,GregorianCalendar aDate,double aQuantity,Supplier aSupplier, String aRefNo, double aStockQtyInAfter, double aStockQtyOutAfter){
		medical = aMedical;
		type = aType;
		ward = aWard;
		lot = aLot;
		date = aDate;
		quantity = aQuantity;
		supplier = aSupplier;
		refNo=aRefNo;
		stockQtyInAfter = aStockQtyInAfter;
		stockQtyOutAfter = aStockQtyOutAfter;
	}
	
	public int getCode(){
		return code;
	}
	public double getStockQtyInAfter() {
		return stockQtyInAfter;
	}

	public void setStockQtyInAfter(double stockQtyInAfter) {
		this.stockQtyInAfter = stockQtyInAfter;
	}
	
	public double getStockQtyOutAfter() {
		return stockQtyOutAfter;
	}

	public void setStockQtyOutAfter(double stockQtyOutAfter) {
		this.stockQtyOutAfter = stockQtyOutAfter;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public Medical getMedical(){
		return medical;
	}
	public MovementType getType(){
		return type;
	}
	public Ward getWard(){
		return ward;
	}
	public Lot getLot(){
		return lot;
	}
	
	@XmlJavaTypeAdapter(DateAdapter.class)
	public GregorianCalendar getDate(){
		return date;
	}
	public double getQuantity(){
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public Supplier getOrigin(){
		return supplier;
	}
	public void setWard(Ward ward) {
		this.ward = ward;
	}
	public void setDate(GregorianCalendar date) {
		this.date = date;
	}
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}
	public void setCode(int aCode){
		code=aCode;
	}
	public void setMedical(Medical aMedical){
		medical=aMedical;
	}
	public void setType(MovementType aType){
		type=aType;
	}
	public void setLot(Lot aLot){
		lot=aLot;
	}
	public String getRefNo() {
		return refNo;
	}
	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}
	
	public Ward getWardFrom() {
		return wardFrom;
	}


	public void setWardFrom(Ward wardFrom) {
		this.wardFrom = wardFrom;
	}
	
	public String toString(){
		return MessageBundle.getMessage("angal.medicalstock.medical")+":"+medical.toString()+MessageBundle.getMessage("angal.medicalstock.type")+":"+type.toString()+MessageBundle.getMessage("angal.medicalstock.quantity")+":"+quantity;
	}

	public String getCreate_by() {
		return create_by;
	}

	public void setCreate_by(String create_by) {
		this.create_by = create_by;
	}

	public String getModify_by() {
		return modify_by;
	}

	public void setModify_by(String modify_by) {
		this.modify_by = modify_by;
	}

	public GregorianCalendar getCreate_date() {
		return create_date;
	}

	public void setCreate_date(GregorianCalendar create_date) {
		this.create_date = create_date;
	}

	public GregorianCalendar getModify_date() {
		return modify_date;
	}

	public void setModify_date(GregorianCalendar modify_date) {
		this.modify_date = modify_date;
	}
	
}
