package org.isf.visits.model;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.isf.utils.jobjects.DateAdapter;
import org.isf.utils.time.TimeTools;

public class Visit extends GregorianCalendar  {

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	private int visitID;
	private int patID;
	private String note;
	private boolean sms;
	private GregorianCalendar visitDate;
	//fields for history	
		private String create_by ; 
		private String modify_by ; 
		private GregorianCalendar create_date;
		private GregorianCalendar modify_date;
		//
	public Visit() {
		visitDate=new GregorianCalendar();
	}

	public Visit(int year, int month, int dayOfMonth, int hourOfDay,
			int minute, int second) {
		visitDate=new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, second);
	}

	public Visit(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
		visitDate=new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute);
	}

	public Visit(int year, int month, int dayOfMonth) {
		visitDate=new GregorianCalendar(year, month, dayOfMonth);
	}

	public Visit(Locale locale) {
		visitDate=new GregorianCalendar(locale);
	}

	public Visit(TimeZone zone, Locale locale) {
		visitDate=new GregorianCalendar(zone, locale);
	}

	public Visit(TimeZone zone) {
		visitDate=new GregorianCalendar(zone);
	}

	public int getVisitID() {
		return visitID;
	}

	public void setVisitID(int visitID) {
		this.visitID = visitID;
	}

	public int getPatID() {
		return patID;
	}

	public void setPatID(int patID) {
		this.patID = patID;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	public boolean isSms() {
		return sms;
	}

	public void setSms(boolean sms) {
		this.sms = sms;
	}

	@XmlJavaTypeAdapter(DateAdapter.class)
	public GregorianCalendar getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(GregorianCalendar visitDate) {
		this.visitDate = visitDate;
	}

	public String toString() {

		return formatDateTime(visitDate);
	}

	public String formatDateTime(GregorianCalendar time) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy - HH:mm:ss"); //$NON-NLS-1$
		return format.format(time.getTime());
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
