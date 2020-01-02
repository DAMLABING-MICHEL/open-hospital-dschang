package org.isf.reduction.manager;

import java.util.ArrayList;
import java.util.Iterator;

import org.isf.priceslist.model.Price;
import org.isf.reduction.model.ExamsReduction;
import org.isf.reduction.model.MedicalsReduction;
import org.isf.reduction.model.OperationReduction;
import org.isf.reduction.model.OtherReduction;
import org.isf.reduction.model.ReductionPlan;
import org.isf.reduction.service.IoOperations;
import org.isf.utils.exception.OHException;

public class ReductionPlanManager {

	private IoOperations ioOperations = new IoOperations();

	public ArrayList<ReductionPlan> getReductionPlans()
			throws OHException {
		return ioOperations.getReductionPlans();
	}

	public ReductionPlan getReductionPlan(int rpID)
			throws OHException {
		return ioOperations.getReductionPlan(rpID);
	}
	public ReductionPlan getReductionPlan(String  description)
			throws OHException {
		return ioOperations.getReductionPlan(description);
	}
	public int newReductionPlan(ReductionPlan reductionPlan)
			throws OHException {
		return ioOperations.newReductionPlan(reductionPlan);
	}

	public boolean updateReductionPlan(
			ReductionPlan reductionPlan) throws OHException {
		return ioOperations.updateReductionPlan(reductionPlan);
	}

	public boolean deleteReductionPlan(
			ReductionPlan reductionPlan, boolean forceDelete)
			throws OHException {
		return ioOperations.deleteReductionPlan(reductionPlan,
				forceDelete);
	}

	public boolean canDelete(ReductionPlan reductionPlan) {
		try {
			return ioOperations.canDelete(reductionPlan.getId());
		} catch (Exception e) {
			return false;
		}

	}

	public ArrayList<MedicalsReduction> getMedicalsReductions(int rpID)
			throws OHException {
		return ioOperations.getMedicalsReductions(rpID);

	}

	public ArrayList<ExamsReduction> getExamsReductions(int rpID)
			throws OHException {
		return ioOperations.getExamsReductions(rpID);

	}

	public ArrayList<OperationReduction> getOperationsReductions(int rpID)
			throws OHException {
		return ioOperations.getOperationsReductions(rpID);

	}

	public ArrayList<OtherReduction> getOtherReductions(int rpID)
			throws OHException {
		return ioOperations.getOtherReductions(rpID);

	}

	public Double getPriceWithRate(Double price, Double rate) {
		Double val = price - ((price * rate) / 100);

		return val;
	}

	public Price getExamPrice(Price price, int rpID) {
		ReductionPlan patReductionPlan;
		try {
			patReductionPlan = this.getReductionPlan(rpID);

			ArrayList<ExamsReduction> reductionList = this
					.getExamsReductions(rpID);

			boolean foundedInException = false;
			Double newPrice = price.getPrice();

			for (Iterator<ExamsReduction> i = reductionList.iterator(); i.hasNext();) {
				ExamsReduction examsReduction = (ExamsReduction) i.next();

				if (price.getItem().equals(examsReduction.getExaCode())) {
					newPrice = getPriceWithRate(price.getPrice(),
							examsReduction.getReductionRate());
					foundedInException = true;
					break;
				}
			}

			if (!foundedInException) {
				/**** test if @patreductionPlan is null ****/
				if(patReductionPlan!=null)
					newPrice = getPriceWithRate(price.getPrice(), patReductionPlan.getExamRate());
			}
			price.setPrice(newPrice);
			return price;

		} catch (OHException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Price getOperationPrice(Price price, int rpID) {
		ReductionPlan patreductionPlan;
		try {
			patreductionPlan = this.getReductionPlan(rpID);
			ArrayList<OperationReduction> reductionList = this.getOperationsReductions(rpID);

			boolean foundedInException = false;
			Double newPrice = price.getPrice();

			for (Iterator<OperationReduction> i = reductionList.iterator(); i.hasNext();) {
				OperationReduction reduction = (OperationReduction) i
						.next();

				if (price.getItem().equals(
						reduction.getOpeCode())) {
					newPrice = getPriceWithRate(price.getPrice(),
							reduction.getReductionRate());
					foundedInException = true;
					break;
				}
			}

			if (!foundedInException) {
				/**** test if @patreductionPlan is null ****/
				if(patreductionPlan!=null)
					newPrice = getPriceWithRate(price.getPrice(), patreductionPlan.getOperationRate());
			}

			price.setPrice(newPrice);


			return price;

		} catch (OHException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Price getOtherPrice(Price price, int rpID) {
		ReductionPlan patreductionPlan;
		try {
			patreductionPlan = this.getReductionPlan(rpID);
			
			ArrayList<OtherReduction> reductionList = this.getOtherReductions(rpID);

			boolean foundedInException = false;
			Double newPrice = price.getPrice();

			for (Iterator<OtherReduction> i = reductionList.iterator(); i.hasNext();) {
				OtherReduction reduction = (OtherReduction) i
						.next();

				if (price.getItem().equals(""+
						reduction.getOthID())) {
					newPrice = getPriceWithRate(price.getPrice(),
							reduction.getReductionRate());
					foundedInException = true;
					break;
				}
			}

			if (!foundedInException) {
				newPrice = getPriceWithRate(price.getPrice(),
						patreductionPlan.getOtherRate());
			}

			price.setPrice(newPrice);
			
			return price;
			
		} catch (OHException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Price getMedicalPrice(Price price, int rpID) {
		ReductionPlan patReductionPlan;
		try {
			patReductionPlan = this.getReductionPlan(rpID);
			
			if(patReductionPlan==null){
				return price;
			}
			
			ArrayList<MedicalsReduction> reductionList = this
					.getMedicalsReductions(rpID);
			
			boolean foundedInException = false;
			Double newPrice = price.getPrice();
			
			for (Iterator<MedicalsReduction> i = reductionList.iterator(); i.hasNext();) {
				MedicalsReduction reduction = (MedicalsReduction) i.next();
				
				if (price.getItem().equalsIgnoreCase("" + reduction.getMedID())) {
					newPrice = getPriceWithRate(price.getPrice(),
							reduction.getReductionRate());
					foundedInException = true;
					break;
				}
			}
			
			if (!foundedInException) {
				newPrice = getPriceWithRate(price.getPrice(),
						patReductionPlan.getMedicalRate());
			}
			
			price.setPrice(newPrice);
			
			return price;
			
		} catch (OHException e) {
			e.printStackTrace();
			return null;
		}
	}

}
