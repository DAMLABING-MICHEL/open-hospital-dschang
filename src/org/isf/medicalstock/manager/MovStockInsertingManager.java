package org.isf.medicalstock.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JOptionPane;

import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstock.service.IoOperations;
import org.isf.parameters.manager.Param;
import org.isf.medicals.model.Medical;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;

import com.toedter.calendar.JDateChooser;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;

public class MovStockInsertingManager {

	private IoOperations ioOperations;
	private org.isf.medicals.service.IoOperations ioOperationsMedicals;
	private static final String DATE_FORMAT_HH_MM = "HH:mm:ss";
	
	public MovStockInsertingManager() {
		ioOperations = new IoOperations();
		ioOperationsMedicals = new org.isf.medicals.service.IoOperations();
	}

	// Replaced by getMedical in MedicalBrowsingManager
	/*
	 * Gets the current quantity for the specified {@link Medical}. In case of
	 * error a message error is shown and a <code>0</code> value is returned.
	 * 
	 * @param medical the medical to check.
	 * 
	 * @return the current quantity of medical.
	 * 
	 * public int getCurrentQuantity(Medical medical){ try { return
	 * ioOperations.getCurrentQuantity(medical); } catch (OHException e) {
	 * JOptionPane.showMessageDialog(null, e.getMessage()); return 0; } }
	 */

	private boolean isAutomaticLot() {
		return Param.bool("AUTOMATICLOT");
	}
	
	private boolean isAutomaticLotDischarge() {
		return Param.bool("AUTOMATICLOTDISCHARGE");
	}

	/**
	 * Stores the specified movement. In case of error a message error is shown
	 * and a <code>null</code> value is returned.
	 * 
	 * @param movement - the movement to store.
	 * @return <code>true</code> if the movement has been stored,
	 *         <code>false</code> otherwise.
	 */
	public boolean newMovement(Movement movement) {

		try {

			if (movement.getQuantity() == 0) {
				JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.medicalstock.thequantitymustnotbe"));
				return false;
			}

			if (movement.getMedical() == null) {
				JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.medicalstock.chooseamedical"));
				return false;
			}

			if (movement.getType() == null) {
				JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.medicalstock.chooseatype"));
				return false;
			}
			
			if (movement.getOrigin() == null) {
				JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.medicalstock.chooseasupplier"));
				return false;
			}

			if (movement.getLot() != null) {

				if (movement.getLot().getCode().equalsIgnoreCase("")) {
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.medicalstock.insertavalidlotidentifier"));
					return false;
				}

				if (movement.getLot().getCode().length() >= 50) {
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.medicalstock.changethelotidbecauseitstoolong"));
					return false;
				}

				if (movement.getLot().getDueDate() == null) {
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.medicalstock.insertavalidduedate"));
					return false;
				}

				if (movement.getLot().getPreparationDate() == null) {
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.medicalstock.insertavalidpreparationdate"));
					return false;
				}

				if (movement.getLot().getPreparationDate().compareTo(movement.getLot().getDueDate()) > 0) {
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.medicalstock.preparationdatecannotbelaterthanduedate"));
					return false;
				}

				if ((movement.getType().getType().equalsIgnoreCase("-")) && (movement.getQuantity() > movement.getLot().getQuantity())) {
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.medicalstock.movementquantityisgreaterthanthequantityof"));
					return false;
				}

				// checks if the lot is already used by other medicals
				List<Integer> medicalIds = ioOperations.getMedicalsFromLot(movement.getLot().getCode());
				/*if (!(medicalIds.size() == 0 || (medicalIds.size() == 1 && medicalIds.get(0).intValue() == movement.getMedical().getCode().intValue()))) {
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.medicalstock.thislotreferstoanothermedical"));
					return false;
				}*/
				
				if (!(medicalIds.size() == 0 || (medicalIds.size() == 1 && medicalIds.get(0).intValue() == movement.getMedical().getCode().intValue()))) {
					GregorianCalendar thisDate = TimeTools.getServerDateTime();
					thisDate.setTime((new JDateChooser(new Date())).getDate());
					movement.getLot().setCode(movement.getLot().getCode() + formatDateTime(thisDate) );
				}
			}

			// we check movement quantity in outgoing stock case
			if (!movement.getType().getType().equals("+")) {

				Medical medical = ioOperationsMedicals.getMedical(movement.getMedical().getCode());
				double totalQuantity = medical.getTotalQuantity() - movement.getQuantity();

				// we check if the outgoing movement has a quantity greater than
				// the stocked one
				if (totalQuantity < 0) {
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.medicalstock.thetotalquantitycannotbelessthan"));
					return false;
				}

				// we check if we are near to critical quantity
				if (totalQuantity < medical.getMinqty()) {
					int reply = JOptionPane.showConfirmDialog(null, MessageBundle.getMessage("angal.medicalstock.youaregoingtorununderthecritical"), MessageBundle
							.getMessage("angal.medicalstock.select"), JOptionPane.YES_NO_OPTION);
					boolean abort = reply == JOptionPane.NO_OPTION;

					if (abort)
						return false;
				}
			}
			boolean result;
			//if (isAutomaticLot() && movement.getType().getType().equals("-")) {
			if (isAutomaticLotDischarge() && movement.getType().getType().equals("-")) {
				result = ioOperations.newAutomaticDischargingMovement(movement);
			} else
				result = ioOperations.newMovement(movement);
			return result;
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}

	}

	/**
	 * Retrieves all the {@link Lot} associated to the specified {@link Medical}
	 * . In case of error a message error is shown and a <code>null</code> value
	 * is returned.
	 * 
	 * @param medical
	 *            the medical.
	 * @return the retrieved lots.
	 */
	public ArrayList<Lot> getLotByMedical(Medical medical) {
		if (medical == null) {
			return new ArrayList<Lot>();
		}
		try {
			return ioOperations.getLotsByMedical(medical);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	public ArrayList<Lot> getLotByMedical(int medicalID) {
		if (medicalID == 0) {
			return new ArrayList<Lot>();
		}
		try {
			return ioOperations.getLotsByMedical(medicalID);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Checks if the provided quantity is under the medical limits. In case of
	 * error a message error is shown and a <code>false</code> value is
	 * returned.
	 * 
	 * @param medicalSelected
	 *            the selected medical.
	 * @param specifiedQuantity
	 *            the quantity provided by the user.
	 * @return <code>true</code> if is under the limit, false otherwise.
	 */
	public boolean alertCriticalQuantity(Medical medicalSelected, int specifiedQuantity) {
		try {
			Medical medical = ioOperationsMedicals.getMedical(medicalSelected.getCode());
			double totalQuantity = medical.getTotalQuantity();
			double residual = totalQuantity - specifiedQuantity;
			return residual < medical.getMinqty();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	public boolean alertCriticalQuantity(int medicalSelectedID, int specifiedQuantity) {
		try {
			Medical medical = ioOperationsMedicals.getMedical(medicalSelectedID);
			double totalQuantity = medical.getTotalQuantity();
			double residual = totalQuantity - specifiedQuantity;
			return residual < medical.getMinqty();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * returns the date of the last movement
	 * 
	 * @return
	 */
	public GregorianCalendar getLastMovementDate() {
		try {
			return ioOperations.getLastMovementDate();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	public Double getLastMovementPriceForAMedical(int medicaliD) throws OHException {
		try {
			return ioOperations.getLastMovementPriceForAMedical(medicaliD);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return -1.0;
		}
	}

	/**
	 * check if the reference number is already used
	 * 
	 * @return <code>true</code> if is already used, <code>false</code>
	 *         otherwise.
	 */
	public boolean refNoExists(String refNo) {
		try {
			return ioOperations.refNoExists(refNo);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return true;
		}
	}

	/**
	 * insert a list of {@link Movement}s and related {@link MedicalCost}s
	 * 
	 * @param movements
	 *            - the list of {@link Movement}s
	 * @param costs
	 *            - the related list of {@link MedicalCost}s
	 * @return the list <code>size</code> if all {@link Movement}s have been
	 *         inserted, otherwise the <code>index</code> of the problematic
	 *         {@link Movement} in the list
	 */
	public int newMultipleChargingMovements(ArrayList<Movement> movements) {
		DbQueryLogger dbQuery = new DbQueryLogger();

		int i = 0;
		try {
			boolean ok = true;
			int size = movements.size();
			for (i = 0; i < size; i++) {
				Movement mov = movements.get(i);
				ok = prepareChargingMovement(dbQuery, mov);
				System.out.println("prepareChargingMovement: " + ok);
				if(!ok){
					break;
				}
			}
			if (ok) {
				// i = size - 1
				dbQuery.commit();
				i++;
			}
			
			dbQuery.releaseConnection();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return i;
	}
	
	public boolean lotExists(String lotCode) throws OHException{
		return ioOperations.lotExists(lotCode);
	}
	public int newMultipleChargingMovements2(ArrayList<Integer> movementsIDs) {
		DbQueryLogger dbQuery = new DbQueryLogger();
		
		int i = 0;
		try {
			boolean ok = true;
			int size = movementsIDs.size();
			for (i = 0; i < size; i++) {
				int movementID = movementsIDs.get(i);
				Movement mov = ioOperations.getMovementById(movementID);
				ok = prepareChargingMovement(dbQuery, mov);
			}
			if (ok) {
				// i = size - 1
				dbQuery.commit();
				i++;
			}
			dbQuery.releaseConnection();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return i;
	}

	/**
	 * Prepare the insert of the specified {@link Movement} (no commit)
	 * 
	 * @param dbQuery
	 *            - the session with the DB
	 * @param movement
	 *            - the movement to store.
	 * @return <code>true</code> if the movement has been stored,
	 *         <code>false</code> otherwise.
	 */
	private boolean prepareChargingMovement(DbQueryLogger dbQuery, Movement movement) {
		try {

			if (movement.getQuantity() == 0) {
				JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.medicalstock.thequantitymustnotbe"));
				return false;
			}

			if (movement.getMedical() == null) {
				JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.medicalstock.chooseamedical"));
				return false;
			}

			if (movement.getType() == null) {
				JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.medicalstock.chooseatype"));
				return false;
			}

			if (movement.getLot() != null) {

				/*if (movement.getLot().getCode().equalsIgnoreCase("")) {
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.medicalstock.insertavalidlotidentifier"));
					return false;
				}*/

				if (movement.getLot().getCode().length() >= 50) {
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.medicalstock.changethelotidbecauseitstoolong"));
					return false;
				}

				if (movement.getLot().getDueDate() == null) {
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.medicalstock.insertavalidduedate"));
					return false;
				}

				if (movement.getLot().getPreparationDate() == null) {
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.medicalstock.insertavalidpreparationdate"));
					return false;
				}

				if (movement.getLot().getPreparationDate().compareTo(movement.getLot().getDueDate()) > 0) {
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.medicalstock.preparationdatecannotbelaterthanduedate"));
					return false;
				}

				if ((movement.getType().getType().equalsIgnoreCase("-")) && (movement.getQuantity() > movement.getLot().getQuantity())) {
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.medicalstock.movementquantityisgreaterthanthequantityof"));
					return false;
				}

				// checks if the lot is already used by other medicals
				List<Integer> medicalIds = ioOperations.getMedicalsFromLot(dbQuery, movement.getLot().getCode());
				/*if (!(medicalIds.size() == 0 || (medicalIds.size() == 1 && medicalIds.get(0).intValue() == movement.getMedical().getCode().intValue()))) {
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.medicalstock.thislotreferstoanothermedical"));
					return false;
				}*/
				
				if (!(medicalIds.size() == 0 || (medicalIds.size() == 1 && medicalIds.get(0).intValue() == movement.getMedical().getCode().intValue()))) {
					GregorianCalendar thisDate = TimeTools.getServerDateTime();
					thisDate.setTime((new JDateChooser(new Date())).getDate());
					movement.getLot().setCode(movement.getLot().getCode() + formatDateTime(thisDate) );
				}
			}

			return ioOperations.prepareChargingMovement(dbQuery, movement);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	private String formatDateTime(GregorianCalendar time) {
		if (time == null)
			return MessageBundle.getMessage("angal.medicalstock.nodate");
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_HH_MM);
		return sdf.format(time.getTime());
	}
	
	public int newMultipleDischargingMovements(ArrayList<Movement> movements) {
		DbQueryLogger dbQuery = new DbQueryLogger();
		int i = 0;
		try {
			boolean ok = true;
			int size = movements.size();
			for (i = 0; i < size; i++) {
				Movement mov = movements.get(i);
				ok = prepareDishargingMovement(dbQuery, mov);
			}
			if (ok) {
				// i = size - 1
				dbQuery.commit();
				i++;
			}
			dbQuery.releaseConnection();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage()+" error");
		}
		return i;
	}
	
	public int newMultipleDischargingMovements2(ArrayList<Integer> movementsIDs) {
		DbQueryLogger dbQuery = new DbQueryLogger();
		
		int i = 0;
		try {
			boolean ok = true;
			int size = movementsIDs.size();
			for (i = 0; i < size; i++) {
				int movementID = movementsIDs.get(i);
				Movement mov = ioOperations.getMovementById(movementID);
				ok = prepareDishargingMovement(dbQuery, mov);
			}
			if (ok) {
				// i = size - 1
				dbQuery.commit();
				i++;
			}
			dbQuery.releaseConnection();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return i;
	}

	private boolean prepareDishargingMovement(DbQueryLogger dbQuery, Movement movement) {
		try {
			boolean result;
			//if (isAutomaticLot()) {
			if (isAutomaticLotDischarge()) {
				result = ioOperations.newAutomaticDischargingMovement(dbQuery, movement);
			} else
				result = ioOperations.prepareDischargingwMovement(dbQuery, movement);			
			return result;
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	public boolean prepareDishargingMovementInventory(Movement movement) {
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			boolean result;
				result = ioOperations.prepareDischargingwMovement(dbQuery, movement);
			return result;
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	/**
	 * Updates the incoming quantity for the specified medical.
	 * @param dbQuery the {@link DbQueryLogger} to use.
	 * @param medicalCode the medical code.
	 * @param incrementQuantity the quantity to add.
	 * @return <code>true</code> if the quantity has been updated, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the update.
	 */
	public boolean updateMedicalIncomingQuantity(int medicalCode, double incrementQuantity) throws OHException
	{
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			return ioOperations.updateMedicalIncomingQuantity(dbQuery,medicalCode,incrementQuantity);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	/**
	 * Updates the outcoming quantity for the specified medicinal.
	 * @param dbQuery the {@link DbQueryLogger} to use.
	 * @param medicalCode the medical code.
	 * @param incrementQuantity the quantity to add to the current outcoming quantity.
	 * @return <code>true</code> if the outcoming quantity has been updated <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the update.
	 */
	public boolean updateMedicalOutcomingQuantity(int medicalCode, double incrementQuantity) throws OHException
	{
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			return ioOperations.updateMedicalOutcomingQuantity(dbQuery,medicalCode,incrementQuantity);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
}