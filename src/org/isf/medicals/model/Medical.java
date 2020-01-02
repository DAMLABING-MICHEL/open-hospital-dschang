/**
 * @(#) Farmaco.java
 * 11-dec-2005
 * 14-jan-2006
 */

package org.isf.medicals.model;

import java.util.GregorianCalendar;

import org.isf.medtype.model.MedicalType;

/**
 * Pure Model Medical DSR (Drugs Surgery Rest): represents a medical
 * 
 * @author bob
 * 		   modified by alex:
 * 			- product code
 * 			- pieces per packet
 */
public class Medical implements Comparable<Medical> {
	/**
	 * Code of the medical
	 */
	private Integer code;

	/**
	 * Code of the product
	 */
	
	private String prod_code;

	/**
	 * Type of the medical
	 */
	private MedicalType type;

	/**
	 * Description of the medical
	 */
	private String description;

	/**
	 * initial quantity
	 */
	private double initialqty;
	
	/**
	 * pieces per packet
	 */
	private Integer pcsperpck;

	/**
	 * input quantity
	 */
	private double inqty;

	/**
	 * out quantity
	 */
	private double outqty;
	/**
	 * min quantity
	 */
	private double minqty;
	
	private double lastprice;

	private double mediumprice;
	
	private String conditioning;
	
	private String shape;

	private String dosing;
	
	public double getLastprice() {
		return lastprice;
	}

	public void setLastprice(double lastprice) {
		this.lastprice = lastprice;
	}

	/**
	 * Lock control
	 */

	private Integer lock;

	//fields for history	
		private String create_by ; 
		private String modify_by ; 
		private GregorianCalendar create_date;
		private GregorianCalendar modify_date;
		//
	
	
	/**
	 * Constructor
	 */
	public Medical() {
		super();
		this.code = 0;
		this.type = null;
		this.prod_code = "";
		this.description = " ";
		this.initialqty = (double)0;
		this.pcsperpck = 0;
		this.minqty = (double)0;
		this.inqty = (double)0;
		this.outqty = (double)0;
		this.lock = 0;
	}
	
	/**
	 * Constructor
	 */
	public Medical(Integer code, MedicalType type, String prod_code, String description,
			double initialqty, Integer pcsperpck, double minqty, double inqty, double outqty, Integer lock) {
		super();
		this.code = code;
		this.type = type;
		this.prod_code = prod_code;
		this.description = description;
		this.initialqty = initialqty;
		this.pcsperpck = pcsperpck;
		this.minqty=minqty;
		this.inqty = inqty;
		this.outqty = outqty;
		this.lock = lock;
	}
	
	public Medical(Integer code, MedicalType type, String prod_code, String description,
			double initialqty, Integer pcsperpck, double minqty, double inqty, double outqty, Integer lock, String conditioning, String shape, String dosing) {
		super();
		this.code = code;
		this.type = type;
		this.prod_code = prod_code;
		this.description = description;
		this.initialqty = initialqty;
		this.pcsperpck = pcsperpck;
		this.minqty=minqty;
		this.inqty = inqty;
		this.outqty = outqty;
		this.lock = lock;
		this.conditioning = conditioning;
		this.shape = shape;
		this.dosing = dosing;
	}
	public Medical(Integer code, MedicalType type, String prod_code, String description,
			double initialqty, Integer pcsperpck, double minqty, double inqty, double outqty, Integer lock, double lastprice) {
		super();
		this.code = code;
		this.type = type;
		this.prod_code = prod_code;
		this.description = description;
		this.initialqty = initialqty;
		this.pcsperpck = pcsperpck;
		this.minqty=minqty;
		this.inqty = inqty;
		this.outqty = outqty;
		this.lock = lock;
		this.lastprice = lastprice;
	}
	
	public Medical(Integer code, MedicalType type, String prod_code, String description,
			double initialqty, Integer pcsperpck, double minqty, double inqty, double outqty, Integer lock, double lastprice, String conditioning, String shape, String dosing) {
		super();
		this.code = code;
		this.type = type;
		this.prod_code = prod_code;
		this.description = description;
		this.initialqty = initialqty;
		this.pcsperpck = pcsperpck;
		this.minqty=minqty;
		this.inqty = inqty;
		this.outqty = outqty;
		this.lock = lock;
		this.lastprice = lastprice;
		this.conditioning = conditioning;
		this.shape = shape;
		this.dosing = dosing;
	}
	
	public Medical(Integer code, MedicalType type, String prod_code, String description,
			double initialqty, Integer pcsperpck, double minqty, double inqty, double outqty, Integer lock, double lastprice, double mediumprice) {
		super();
		this.code = code;
		this.type = type;
		this.prod_code = prod_code;
		this.description = description;
		this.initialqty = initialqty;
		this.pcsperpck = pcsperpck;
		this.minqty=minqty;
		this.inqty = inqty;
		this.outqty = outqty;
		this.lock = lock;
		this.lastprice = lastprice;
		this.mediumprice = mediumprice;
		
	}
	/*
	public Medical(Integer code, String prod_code, MedicalType type, String description, double initialqty,
			Integer pcsperpck, double inqty, double outqty, double minqty, double lastprice, double mediumprice,
			String conditioning, String shape, String dosing, Integer lock) {
		super();
		this.code = code;
		this.prod_code = prod_code;
		this.type = type;
		this.description = description;
		this.initialqty = initialqty;
		this.pcsperpck = pcsperpck;
		this.inqty = inqty;
		this.outqty = outqty;
		this.minqty = minqty;
		this.lastprice = lastprice;
		this.mediumprice = mediumprice;
		this.conditioning = conditioning;
		this.shape = shape;
		this.dosing = dosing;
		this.lock = lock;
	}
*/
	public double getTotalQuantity()
	{
		return initialqty + inqty - outqty;
	}
	
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public double getMediumprice() {
		return mediumprice;
	}

	public void setMediumprice(double mediumprice) {
		this.mediumprice = mediumprice;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getInitialqty() {
		return initialqty;
	}

	public void setInitialqty(double initialqty) {
		this.initialqty = initialqty;
	}

	public double getInqty() {
		return inqty;
	}

	public void setInqty(double inqty) {
		this.inqty = inqty;
	}
	public double getMinqty() {
		return minqty;
	}

	public void setMinqty(double minqty) {
		this.minqty = minqty;
	}

	public Integer getLock() {
		return lock;
	}

	public void setLock(Integer lock) {
		this.lock = lock;
	}

	public double getOutqty() {
		return outqty;
	}

	public void setOutqty(double outqty) {
		this.outqty = outqty;
	}

	public MedicalType getType() {
		return type;
	}

	public void setType(MedicalType type) {
		this.type = type;
	}

	public String getProd_code() {
		return prod_code;
	}

	public void setProd_code(String prod_code) {
		this.prod_code = prod_code;
	}

	public Integer getPcsperpck() {
		return pcsperpck;
	}

	public void setPcsperpck(Integer pcsperpck) {
		this.pcsperpck = pcsperpck;
	}

	public boolean equals(Object anObject) {
		return (anObject == null) || !(anObject instanceof Medical) ? false
				: (getCode().equals(((Medical) anObject).getCode())
						&& getDescription().equalsIgnoreCase(
								((Medical) anObject).getDescription())
						&& getType().equals(((Medical) anObject).getType())
						&& getProd_code().equals(((Medical) anObject).getProd_code())
						&& getInitialqty()==(((Medical) anObject).getInitialqty()) 
						&& getInqty()==(((Medical) anObject).getInqty())
						&& getOutqty()==(((Medical) anObject).getOutqty())
						&& (getLock()
						.equals(((Medical) anObject).getLock())));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((prod_code == null) ? 0 : prod_code.hashCode());
		result = prime * result + Integer.parseInt(String.valueOf(Math.round(initialqty)));
		result = prime * result + Integer.parseInt(String.valueOf(Math.round(inqty)));
		result = prime * result + Integer.parseInt(String.valueOf(Math.round(outqty)));
		result = prime * result + ((lock == null) ? 0 : lock.hashCode());
		return result;
	}
	
	public String toString() {
		return getDescription();
	}

	public int compareTo(Medical o) {
		return this.description.compareTo(o.getDescription());
	}
	public String getSearchString() {
		StringBuffer sbNameCode = new StringBuffer();
		sbNameCode.append(getCode());
		sbNameCode.append(getProd_code().toLowerCase());
		sbNameCode.append(getDescription().toLowerCase());
		return sbNameCode.toString();
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

	public String getConditioning() {
		return conditioning;
	}

	public void setConditioning(String conditioning) {
		this.conditioning = conditioning;
	}

	public String getShape() {
		return shape;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}

	public String getDosing() {
		return dosing;
	}

	public void setDosing(String dosing) {
		this.dosing = dosing;
	}
	
}
