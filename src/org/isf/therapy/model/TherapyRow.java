package org.isf.therapy.model;

import java.util.GregorianCalendar;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.isf.medicals.model.Medical;
import org.isf.utils.jobjects.DateAdapter;

//import com.sun.org.apache.bcel.internal.generic.InstructionComparator;

/**
 * 
 * @author Mwithi
 * 
 * Bean to collect data from DB table THERAPIES
 *
 */
public class TherapyRow  implements Comparable<TherapyRow>{
	
	private int therapyID;
	private int patID;
	private GregorianCalendar startDate;
	private GregorianCalendar endDate;
	private Medical medical;
	private Double qty;
	private int unitID;
	private int freqInDay;
	private int freqInPeriod;
	private String note;
	private boolean notify;
	private boolean sms;
	private Double qtyBougth;
	
	private GregorianCalendar prescriptionDate;
		
	
	/**
	 * @param therapyID
	 * @param patID
	 * @param startDate
	 * @param endDate
	 * @param medical
	 * @param qty
	 * @param unitID
	 * @param freqInDay
	 * @param freqInPeriod
	 * @param note
	 * @param notify
	 * @param sms
	 */
	public TherapyRow() {
		super();
		this.therapyID = 0;
		this.patID = 0;
		this.startDate = null;
		this.endDate = null;
		this.medical = null;
		this.qty = (double) 0;
		this.unitID = 0;
		this.freqInDay = 0;
		this.freqInPeriod = 0;
		this.note = "";
		this.notify = false;
		this.sms = false;
	}
	
	
	/**
	 * @param therapyID
	 * @param patID
	 * @param startDate
	 * @param endDate
	 * @param medical
	 * @param qty
	 * @param unitID
	 * @param freqInDay
	 * @param freqInPeriod
	 * @param note
	 * @param notify
	 * @param sms
	 */
	public TherapyRow(int therapyID, int patID, 
			GregorianCalendar startDate, GregorianCalendar endDate,
			Medical medical, Double qty, int unitID, int freqInDay,
			int freqInPeriod, String note, boolean notify, boolean sms, Double qtyBougth) {
		super();
		this.therapyID = therapyID;
		this.patID = patID;
		this.startDate = startDate;
		this.endDate = endDate;
		this.medical = medical;
		this.qty = qty;
		this.unitID = unitID;
		this.freqInDay = freqInDay;
		this.freqInPeriod = freqInPeriod;
		this.note = note;
		this.notify = notify;
		this.sms = sms;
		this.qtyBougth=qtyBougth;
	}
	
	public int getTherapyID() {
		return therapyID;
	}

	public void setTherapyID(int therapyID) {
		this.therapyID = therapyID;
	}

	public int getPatID() {
		return patID;
	}

	public void setPatID(int patID) {
		this.patID = patID;
	}

	@XmlJavaTypeAdapter(DateAdapter.class)
	public GregorianCalendar getStartDate() {
		return startDate;
	}

	public void setStartDate(GregorianCalendar startDate) {
		this.startDate = startDate;
	}

	@XmlJavaTypeAdapter(DateAdapter.class)
	public GregorianCalendar getEndDate() {
		return endDate;
	}

	public void setEndDate(GregorianCalendar endDate) {
		this.endDate = endDate;
	}

	public Medical getMedical() {
		return medical;
	}

	public void setMedical(Medical medical) {
		this.medical = medical;
	}

	public Double getQty() {
		return qty;
	}

	public void setQty(Double qty) {
		this.qty = qty;
	}

	public int getUnitID() {
		return unitID;
	}

	public void setUnitID(int unitID) {
		this.unitID = unitID;
	}

	public int getFreqInDay() {
		return freqInDay;
	}

	public void setFreqInDay(int freqInDay) {
		this.freqInDay = freqInDay;
	}

	public int getFreqInPeriod() {
		return freqInPeriod;
	}

	public void setFreqInPeriod(int freqInPeriod) {
		this.freqInPeriod = freqInPeriod;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public boolean isNotify() {
		return notify;
	}

	public void setNotify(boolean notify) {
		this.notify = notify;
	}

	public boolean isSms() {
		return sms;
	}

	public void setSms(boolean sms) {
		this.sms = sms;
	}
	
	public Double getQtyBougth() {
		return qtyBougth;
	}


	public void setQtyBougth(Double qtyBougth) {
		this.qtyBougth = qtyBougth;
	}


	public String toString() {
		String string = medical.toString() + " - " + this.unitID + " " + String.valueOf(this.qty) + "/" + this.freqInDay + "/" + this.freqInPeriod; 
		return string;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + freqInDay;
		result = prime * result + freqInPeriod;
		result = prime * result + ((medical == null) ? 0 : medical.hashCode());
		result = prime * result + ((note == null) ? 0 : note.hashCode());
		result = prime * result + (notify ? 1231 : 1237);
		result = prime * result + patID;
		result = prime * result + ((qty == null) ? 0 : qty.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + therapyID;
		result = prime * result + unitID;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TherapyRow other = (TherapyRow) obj;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (freqInDay != other.freqInDay)
			return false;
		if (freqInPeriod != other.freqInPeriod)
			return false;
		if (medical == null) {
			if (other.medical != null)
				return false;
		} else if (!medical.equals(other.medical))
			return false;
		if (note == null) {
			if (other.note != null)
				return false;
		} else if (!note.equals(other.note))
			return false;
		if (notify != other.notify)
			return false;
		if (patID != other.patID)
			return false;
		if (qty == null) {
			if (other.qty != null)
				return false;
		} else if (!qty.equals(other.qty))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (therapyID != other.therapyID)
			return false;
		if (unitID != other.unitID)
			return false;
		return true;
	}

	@Override
	public int compareTo(TherapyRow therapyTow) {
		int sdateCpt= therapyTow.getStartDate().compareTo(this.startDate);
		if(sdateCpt!=0) return sdateCpt;
		return this.therapyID<therapyTow.getTherapyID()?-1:
			this.therapyID==therapyTow.getTherapyID()?0:1;
	}


	public GregorianCalendar getPrescriptionDate() {
		return prescriptionDate;
	}


	public void setPrescriptionDate(GregorianCalendar prescriptionDate) {
		this.prescriptionDate = prescriptionDate;
	}
	
}
