package org.isf.medicals.model;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.medicalstock.model.Lot;

public class MedicalLot {

	private Medical medical;
	private Lot lot;
	private List<MedicalLot> children=new ArrayList<MedicalLot>();
	private boolean isOpen=false;
	private boolean isParent;
	
	public Medical getMedical() {
		return medical;
	}
	public void setMedical(Medical medical) {
		this.medical = medical;
	}
	public Lot getLot() {
		return lot;
	}
	public void setLot(Lot lot) {
		this.lot = lot;
	}
	
	public boolean isParent(){
		return isParent;
	}
	public void setParent(boolean parent){
		this.isParent=parent;
	}
	
	public boolean hasChildren(){
		boolean hasChildren= this.children!=null && this.children.size()>1;
		if(!hasChildren){
			List<MedicalLot> toReturn=new ArrayList<MedicalLot>();
			toReturn.addAll(children);
			double lotQty=0;
			for (Iterator<MedicalLot> iterator = children.iterator(); iterator.hasNext();) {
				MedicalLot medicalLot = (MedicalLot) iterator.next();
				lotQty+=medicalLot.getLot().getQuantity();
			}
			if(lotQty<this.getMedical().getTotalQuantity()){
				hasChildren=true && this.children.size()>0;
			}
		}
		return hasChildren;
		
	}
	
	public void addChild(MedicalLot child){
		boolean found=false;
		for (Iterator<MedicalLot> iterator = children.iterator(); iterator.hasNext();) {
			MedicalLot medicalLot = (MedicalLot) iterator.next();
			Lot lot=medicalLot.getLot();
			Lot childLot=child.getLot();
			if(lot!=null && childLot!=null){
				if(lot.getCode().equals(childLot.getCode())){
					found=true;
					break;
				}
			}
		}
		
		if(!found){
			children.add(child);
		}
	}
	
	public List<MedicalLot> getChildren(){
		List<MedicalLot> toReturn=new ArrayList<MedicalLot>();
		toReturn.addAll(children);
		double lotQty=0;
		for (Iterator<MedicalLot> iterator = children.iterator(); iterator.hasNext();) {
			MedicalLot medicalLot = (MedicalLot) iterator.next();
			lotQty+=medicalLot.getLot().getQuantity();
		}
		if(lotQty<this.getMedical().getTotalQuantity()){
			MedicalLot inventory=new MedicalLot();
			Lot lot=new Lot();
			lot.setCode(MessageBundle.getMessage("angal.common.unknown"));
			lot.setDueDate(new GregorianCalendar());
			lot.setQuantity(this.getMedical().getTotalQuantity()-lotQty);
			inventory.setMedical(this.getMedical());
			inventory.setLot(lot);
			toReturn.add(0, inventory);
		}
		return toReturn;
	}
	
	public boolean isOpen() {
		return isOpen;
	}
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
	
	
	
	
	
}
