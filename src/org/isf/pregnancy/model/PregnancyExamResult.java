package org.isf.pregnancy.model;

/**
 * @author Martin Reinstadler
 * this class represents the database table PREGNANCYEXAMRESULT
 * it has a reference to a {@link PregnancyVisit}, another reference
 * to a {@link PregnancyExam} and a outcome as string
 */
public class PregnancyExamResult {
	
	/**
	 * @uml.property  name="visitid"
	 */
	private int visitid;
	/**
	 * @uml.property  name="pregresid"
	 */
	private int pregresid;
	/**
	 * @uml.property  name="examCode"
	 */
	private String examCode;
	/**
	 * @uml.property  name="outcome"
	 */
	private String outcome;
	
	/**
	 * Initializes a new PregnancyExamResult
	 */
	public PregnancyExamResult(){
		visitid = 0;
		examCode = "";
		outcome = "";
		pregresid = 0;
	}
	/**
	 * 
	 * @param visit a reference to a {@link PregnancyVisit}
	 * @param examcode a reference to a {@link PregnancyExam}
	 * @param res outcome as string
	 */
	public PregnancyExamResult(int visit, String examcode, String res){
		this.visitid = visit;
		this.examCode =  examcode;
		this.outcome = res;
	}
	/**
	 * @return  the reference to a  {@link PregnancyVisit}  
	 * @uml.property  name="visitid"
	 */
	public int getVisitid() {
		return visitid;
	}
	/**
	 * @param visitid  the reference to a  {@link PregnancyVisit}  
	 * @uml.property  name="visitid"
	 */
	public void setVisitid(int visitid) {
		this.visitid = visitid;
	}
	/**
	 * @return  the reference to a  {@link PregnancyExam}  
	 * @uml.property  name="examCode"
	 */
	public String getExamCode() {
		return examCode;
	}
	/**
	 * @param examid  the reference to a  {@link PregnancyExam}  
	 * @uml.property  name="examCode"
	 */
	public void setExamCode(String examid) {
		this.examCode = examid;
	}
	/**
	 * @return  the result as String
	 * @uml.property  name="outcome"
	 */
	public String getOutcome() {
		return outcome;
	}
	/**
	 * @param result  the result as String
	 * @uml.property  name="outcome"
	 */
	public void setOutcome(String result) {
		this.outcome = result;
	}
	/**
	 * 
	 * @return the primary key of the PREGNANCYEXAMRESULT table
	 */
	public int getPregnancyExamResultId() {
		return pregresid;
	}
	/**
	 * 
	 * @param resultId the primary key of the PREGNANCYEXAMRESULT table
	 */
	public void setPregnancyExamResultId(int resultId) {
		this.pregresid = resultId;
	}
	

}
