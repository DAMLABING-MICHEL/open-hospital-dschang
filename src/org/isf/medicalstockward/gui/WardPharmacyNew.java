package org.isf.medicalstockward.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.text.PlainDocument;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.medicalinventory.gui.InventoryWardEdit;
import org.isf.medicalinventory.gui.InventoryWardEdit.InventoryListener;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.gui.MedicalPicker;
import org.isf.medicalstock.gui.MovStockMultipleCharging;
import org.isf.medicalstock.gui.MovStockMultipleDischarging;
import org.isf.medicalstock.gui.StockMedModel;
import org.isf.medicalstock.gui.MovStockMultipleCharging.JTableModel;
import org.isf.medicalstock.manager.MovStockInsertingManager;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.medstockmovtype.gui.MedicaldsrstockmovTypeBrowser;
import org.isf.medstockmovtype.manager.MedicaldsrstockmovTypeBrowserManager;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.menu.gui.MainMenu;
import org.isf.patient.gui.SelectPatient;
import org.isf.patient.gui.SelectPatient.SelectionListener;
import org.isf.patient.model.Patient;
import org.isf.supplier.model.Supplier;
import org.isf.utils.db.NormalizeString;
import org.isf.utils.jobjects.IntegerDocumentFilter;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.utils.jobjects.TextPrompt;
import org.isf.utils.jobjects.TextPrompt.Show;
import org.isf.utils.time.TimeTools;
import org.isf.ward.gui.WardEdit;
import org.isf.ward.model.Ward;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

//public class WardPharmacyNew extends JDialog implements SelectionListener  
//Jframe{
public class WardPharmacyNew extends ModalJFrame implements SelectionListener,InventoryListener {

	// LISTENER INTERFACE
	// --------------------------------------------------------
	private EventListenerList movementWardListeners = new EventListenerList();

	public interface MovementWardListeners extends EventListener {
		public void movementUpdated(AWTEvent e);

		public void movementInserted(AWTEvent e);
	}

	public void addMovementWardListener(MovementWardListeners l) {
		movementWardListeners.add(MovementWardListeners.class, l);
	}

	public void removeMovementWardListener(MovementWardListeners listener) {
		movementWardListeners.remove(MovementWardListeners.class, listener);
	}

	private void fireMovementWardInserted() {
		AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = movementWardListeners.getListeners(MovementWardListeners.class);
		for (int i = 0; i < listeners.length; i++)
			((MovementWardListeners) listeners[i]).movementInserted(event);
	}

	/*
	 * private void fireMovementWardUpdated() { AWTEvent event = new
	 * AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {
	 * 
	 *//**
		* 
		*//*
		 * private static final long serialVersionUID = 1L;};
		 * 
		 * EventListener[] listeners =
		 * movementWardListeners.getListeners(MovementWardListeners.class); for
		 * (int i = 0; i < listeners.length; i++)
		 * ((MovementWardListeners)listeners[i]).movementUpdated(event); }
		 */
	// ---------------------------------------------------------------------------

	public void patientSelected(Patient patient) {
		patientSelected = patient;
		jTextFieldPatient.setText(patientSelected.getName());
		jTextFieldPatient.setEditable(false);
		jButtonPickPatient.setText(MessageBundle.getMessage("angal.medicalstockwardedit.changepatient")); //$NON-NLS-1$
		jButtonPickPatient.setToolTipText(
				MessageBundle.getMessage("angal.medicalstockwardedit.changethepatientassociatedwiththismovement")); //$NON-NLS-1$
		jButtonTrashPatient.setEnabled(true);
		if (Float.parseFloat("" + patientSelected.getWeight()) == 0) {
			JOptionPane.showMessageDialog(WardPharmacyNew.this,
					MessageBundle.getMessage("angal.medicalstockwardedit.theselectedpatienthasnoweightdefined"));
		}
	}

	private static final long serialVersionUID = 1L;
	private JLabel jLabelPatient;
	private JTextField jTextFieldPatient;
	private JButton jButtonPickPatient;
	private JButton jButtonTrashPatient;
	private JPanel jPanelPatient;
	private JPanel jPanelMedicals;
	private JPanel jPanelButtons;
	private JPanel jPanelNorth;
	private JPanel jPanelUse;
	private JButton jButtonOK;
	private JButton jButtonCancel;
	private JRadioButton jRadioPatient;
	private JTable jTableMedicals;
	private JScrollPane jScrollPaneMedicals;
//	private JPanel jPanelMedicalsButtons;
	private JButton jButtonAddMedical;
	private JButton jButtonRemoveMedical;
//	private static final Dimension PatientDimension = new Dimension(300, 20);

	private Patient patientSelected = null;
	private Ward wardSelected;
	private Object[] medClasses = { Medical.class, Integer.class };
	private String[] medColumnNames = { MessageBundle.getMessage("angal.medicalstockward.medical"),
			MessageBundle.getMessage("angal.medicalstockward.quantity") };
	private Integer[] medWidth = { 300, 200 };
	private boolean[] medResizable = { true, false };

	// Medicals (ALL)
	// MedicalBrowsingManager medManager = new MedicalBrowsingManager();
	// ArrayList<Medical> medArray = medManager.getMedicals();

	// Medicals (in WARD)
	// ArrayList<MedItem> medItems = new ArrayList<MedItem>();
	private ArrayList<Medical> medArray = new ArrayList<Medical>();
	private ArrayList<Double> qtyArray = new ArrayList<Double>();
	private ArrayList<MedicalWard> wardDrugs = null;
	private ArrayList<MedicalWard> medItems = new ArrayList<MedicalWard>();
	private JRadioButton jRadioUse;
	private JTextField jTextFieldUse;
	private JLabel jLabelUse;
	private JPanel panelWard;
	private JRadioButton jRadioWard;
	private JComboBox wardBox;
	private JLabel jLabelSelectWard;
	private JPanel panelEntry;
	private Medical currentMedical = null;

	private JTextField jTextFieldSearch;
	private JTextField jTextFieldDescription;
	private JTextField jTextFieldQty;
	//private HashMap<String, Medical> medicalMap;
	public static HashMap<String, Medical> medicalMap;

	public WardPharmacyNew(JFrame owner, Ward ward, ArrayList<MedicalWard> drugs) {
		//super(owner, true);
		super();
		// initialize();
		medicalMap = new HashMap<String, Medical>();
		wardDrugs = drugs;
		Medical med;
		for (MedicalWard elem : wardDrugs) {
			medArray.add(elem.getMedical());
			qtyArray.add(elem.getQty());
			///////////
			med = elem.getMedical();
			// if(med!=null){
			String key = med.getProd_code().toLowerCase();
			if (key.equals("")) //$NON-NLS-1$
				key = med.getCode().toString().toLowerCase();
			medicalMap.put(key, med);
			// }
			//////////
		}
		wardSelected = ward;
		setPreferredSize(new Dimension(800, 700));
		setMinimumSize(new Dimension(800, 700));
		initComponents();				
	}
	   
    
	private void initComponents() {
		getContentPane().add(getJPanelButtons(), BorderLayout.SOUTH);
		getContentPane().add(getJPanelMedicals(), BorderLayout.CENTER);
		getContentPane().add(getJPanelNorth(), BorderLayout.NORTH);
		//setDefaultCloseOperation(WardPharmacyNew.DISPOSE_ON_CLOSE);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setTitle(MessageBundle.getMessage("angal.medicalstockwardedit.title"));
		pack();
		setLocationRelativeTo(null);
		
		/***************/
		WindowListener exitListener = new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent e) {
		        int confirm = JOptionPane.showOptionDialog(null, MessageBundle.getMessage("angal.common.areyousuretoexit"), 
		        		MessageBundle.getMessage("angal.common.exitconfirmation"), JOptionPane.YES_NO_OPTION, 
		             JOptionPane.QUESTION_MESSAGE, null, null, null);
		        if (confirm == JOptionPane.YES_OPTION) {
		        	InventoryWardEdit.removeInventoryListener(WardPharmacyNew.this);
		            dispose();
		        }else		        
			       return;			    
		    }
		};
		this.addWindowListener(exitListener);
		/***************/
		
		/******* add to inventoryWard listener ***/
		InventoryWardEdit.addInventoryListener(WardPharmacyNew.this);
	}

	private JPanel getJPanelNorth() {
		if (jPanelNorth == null) {
			jPanelNorth = new JPanel();
			jPanelNorth.setLayout(new BoxLayout(jPanelNorth, BoxLayout.Y_AXIS));
			jPanelNorth.add(getJPanelPatient());
			jPanelNorth.add(getJPanelUse());
			jPanelNorth.add(getPanelWard());
			ButtonGroup group = new ButtonGroup();
			group.add(jRadioPatient);
			group.add(jRadioUse);
			group.add(jRadioWard);
			jPanelNorth.add(getPanelEntry());

		}
		return jPanelNorth;
	}

	private JPanel getJPanelUse() {
		if (jPanelUse == null) {
			FlowLayout fl_jPanelUse = new FlowLayout(FlowLayout.LEFT);
			jPanelUse = new JPanel(fl_jPanelUse);
			jPanelUse.add(getJRadioUse());
			jPanelUse.add(getJLabelUse());
			jPanelUse.add(getJTextFieldUse());
		}
		return jPanelUse;
	}

	private JLabel getJLabelUse() {
		if (jLabelUse == null) {
			jLabelUse = new JLabel();
			jLabelUse.setText(MessageBundle.getMessage("angal.medicalstockwardedit.internaluse"));
		}
		return jLabelUse;
	}

	private JTextField getJTextFieldUse() {
		if (jTextFieldUse == null) {
			jTextFieldUse = new JTextField();
			jTextFieldUse.setText(MessageBundle.getMessage("angal.medicalstockwardedit.internaluse").toUpperCase()); //$NON-NLS-1$
			jTextFieldUse.setPreferredSize(new Dimension(300, 30));
			jTextFieldUse.setEnabled(false);
		}
		return jTextFieldUse;
	}

	private JRadioButton getJRadioUse() {
		if (jRadioUse == null) {
			jRadioUse = new JRadioButton();
			jRadioUse.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					jTextFieldPatient.setEnabled(false);
					jButtonPickPatient.setEnabled(false);
					jButtonTrashPatient.setEnabled(false);
					jTextFieldUse.setEnabled(true);
					wardBox.setEnabled(false);
				}
			});
		}
		return jRadioUse;
	}

//	private JButton getJButtonAddMedical() {
//		if (jButtonAddMedical == null) {
//			jButtonAddMedical = new JButton();
//			jButtonAddMedical.setText(MessageBundle.getMessage("angal.medicalstockwardedit.medical")); //$NON-NLS-1$
//			jButtonAddMedical.setMnemonic(KeyEvent.VK_M);
//			jButtonAddMedical.setIcon(new ImageIcon("rsc/icons/plus_button.png")); //$NON-NLS-1$
//			jButtonAddMedical.addActionListener(new ActionListener() {
//
//				public void actionPerformed(ActionEvent e) {
//					ArrayList<Medical> currentMeds = new ArrayList<Medical>();
//					currentMeds.addAll(medArray);
//					ArrayList<Double> currentQties = new ArrayList<Double>();
//					currentQties.addAll(qtyArray);
//
//					// remove already inserted items
//					for (MedicalWard medItem : medItems) {
//						Medical med = medItem.getMedical();
//						int index = currentMeds.indexOf(med);
//						currentMeds.remove(index);
//						currentQties.remove(index);
//					}
//
//					Icon icon = new ImageIcon("rsc/icons/medical_dialog.png"); //$NON-NLS-1$
//					Medical med = (Medical) JOptionPane.showInputDialog(WardPharmacyNew.this,
//							MessageBundle.getMessage("angal.medicalstockwardedit.selectamedical"), //$NON-NLS-1$
//							MessageBundle.getMessage("angal.medicalstockwardedit.medical"), //$NON-NLS-1$
//							JOptionPane.PLAIN_MESSAGE, icon, currentMeds.toArray(), ""); //$NON-NLS-1$
//					if (med != null) {
//						Double startQty = 0.;
//						Double minQty = 0.;
//						Double maxQty = currentQties.get(currentMeds.indexOf(med));
//						Double stepQty = 0.5;
//						JSpinner jSpinnerQty = new JSpinner(new SpinnerNumberModel(startQty, minQty, null, stepQty));
//
//						StringBuilder messageBld = new StringBuilder(med.getDescription()).append("\n");
//						messageBld
//								.append(MessageBundle
//										.getMessage("angal.medicalstockwardedit.insertquantitypiecesormls"))
//								.append("\n");
//						messageBld.append(MessageBundle.getMessage("angal.medicalstockwardedit.instock")).append(": ")
//								.append(maxQty);
//
//						int r = JOptionPane.showConfirmDialog(WardPharmacyNew.this,
//								new Object[] { messageBld.toString(), jSpinnerQty },
//								MessageBundle.getMessage("angal.medicalstockwardedit.quantity"),
//								JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
//
//						if (r == JOptionPane.OK_OPTION) {
//							try {
//								Double qty = (Double) jSpinnerQty.getValue();
//								if (qty > maxQty) {
//									JOptionPane.showMessageDialog(WardPharmacyNew.this,
//											MessageBundle.getMessage(
//													"angal.medicalstockwardedit.invalidquantitypleaseinsertmax") + " " //$NON-NLS-1$
//													+ maxQty,
//											MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantity"), //$NON-NLS-1$
//											JOptionPane.ERROR_MESSAGE);
//									return;
//								}
//								double roundedQty = round(qty, stepQty);
//								if (roundedQty >= stepQty)
//									addItem(med, roundedQty);
//								else
//									JOptionPane.showMessageDialog(WardPharmacyNew.this,
//											MessageBundle.getMessage(
//													"angal.medicalstockwardedit.invalidquantitypleaseinsertatleast") //$NON-NLS-1$
//													+ " " + stepQty,
//											MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantity"), //$NON-NLS-1$
//											JOptionPane.ERROR_MESSAGE);
//							} catch (Exception eee) {
//								JOptionPane.showMessageDialog(WardPharmacyNew.this,
//										MessageBundle
//												.getMessage("angal.medicalstockwardedit.invalidquantitypleasetryagain"), //$NON-NLS-1$
//										MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantity"), //$NON-NLS-1$
//										JOptionPane.ERROR_MESSAGE);
//							}
//						} else
//							return;
//					}
//				}
//			});
//		}
//		return jButtonAddMedical;
//	}

	public double round(double input, double step) {
		return Math.round(input / step) * step;
	}

//	private JButton getJButtonRemoveMedical() {
//		if (jButtonRemoveMedical == null) {
//			jButtonRemoveMedical = new JButton();
//			jButtonRemoveMedical.setText(MessageBundle.getMessage("angal.medicalstockwardedit.removeitem")); //$NON-NLS-1$
//			jButtonRemoveMedical.setIcon(new ImageIcon("rsc/icons/delete_button.png")); //$NON-NLS-1$
//			jButtonRemoveMedical.addActionListener(new ActionListener() {
//
//				public void actionPerformed(ActionEvent e) {
//					if (jTableMedicals.getSelectedRow() < 0) {
//						JOptionPane.showMessageDialog(WardPharmacyNew.this,
//								MessageBundle.getMessage("angal.medicalstockwardedit.pleaseselectanitem"), //$NON-NLS-1$
//								"Error", //$NON-NLS-1$
//								JOptionPane.WARNING_MESSAGE);
//					} else {
//						removeItem(jTableMedicals.getSelectedRow());
//					}
//				}
//			});
//		}
//		return jButtonRemoveMedical;
//	}

	private void addItem(Medical med, Double qty) {
		if (med != null) {
			MedicalWard item = new MedicalWard(med, qty);
			boolean found=false;
			for (Iterator<MedicalWard> iterator = medItems.iterator(); iterator.hasNext();) {
				MedicalWard medWard = (MedicalWard) iterator.next();
				if(medWard.getMedical()==med){
					found=true;
					medWard.setQty(qty);
					break;
				}
				
			}
			if(!found){
				medItems.add(item);
			}
			
			jTableMedicals.updateUI();
		}
	}

	private void removeItem(int row) {
		if (row != -1) {
			medItems.remove(row);
			jTableMedicals.updateUI();
		}

	}

//	private JPanel getJPanelMedicalsButtons() {
//		if (jPanelMedicalsButtons == null) {
//			jPanelMedicalsButtons = new JPanel();
//			jPanelMedicalsButtons.setLayout(new BoxLayout(jPanelMedicalsButtons, BoxLayout.Y_AXIS));
//			jPanelMedicalsButtons.add(getJButtonAddMedical());
//			jPanelMedicalsButtons.add(getJButtonRemoveMedical());
//		}
//		return jPanelMedicalsButtons;
//	}

	private JScrollPane getJScrollPaneMedicals() {
		if (jScrollPaneMedicals == null) {
			jScrollPaneMedicals = new JScrollPane();
			jScrollPaneMedicals.setViewportView(getJTableMedicals());
		}
		return jScrollPaneMedicals;
	}

	private JTable getJTableMedicals() {
		if (jTableMedicals == null) {
			jTableMedicals = new JTable();
			jTableMedicals.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent evt) {
					if (evt.getClickCount() == 2) {
						if (jTableMedicals.getRowCount() > 0) {
							if (jTableMedicals.getSelectedRow() >= 0) {
								currentMedical = ((MedicalTableModel) jTableMedicals.getModel())
										.getMedical(jTableMedicals.getSelectedRow());
								if (currentMedical != null) {
									double qty = ((MedicalTableModel) jTableMedicals.getModel())
											.getValueIntAt(jTableMedicals.getSelectedRow(), 1);
									
									loadMedical(qty);
								}

							}
						}
					}
				}
			});
			jTableMedicals.addKeyListener(new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e) {
				}

				@Override
				public void keyReleased(KeyEvent e) {
				}

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_DELETE) {
						int row = jTableMedicals.getSelectedRow();
						MedicalTableModel model = (MedicalTableModel) jTableMedicals.getModel();
						model.removeItem(row);
						jTableMedicals.updateUI();                     
					}
				}
			});
			
			jTableMedicals.setModel(new MedicalTableModel());
			for (int i = 0; i < medWidth.length; i++) {
				jTableMedicals.getColumnModel().getColumn(i).setMinWidth(medWidth[i]);
				if (!medResizable[i])
					jTableMedicals.getColumnModel().getColumn(i).setMaxWidth(medWidth[i]);
			}
		}
		return jTableMedicals;
	}

	private JButton getJButtonOK() {
		if (jButtonOK == null) {
			jButtonOK = new JButton();
			jButtonOK.setText(MessageBundle.getMessage("angal.common.ok")); //$NON-NLS-1$
			jButtonOK.setMnemonic(KeyEvent.VK_O);
			jButtonOK.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					boolean isPatient = false;//
					String description = "";
					Ward wardTo = null; //
					int age = 0;
					float weight = 0;

					if (jRadioPatient.isSelected()) {
						if (patientSelected == null) {
							JOptionPane.showMessageDialog(null,
									MessageBundle.getMessage("angal.medicalstockwardedit.pleaseselectapatient")); //$NON-NLS-1$
							return;
						}
						description = patientSelected.getName();
						age = patientSelected.getAge();
						weight = Float.parseFloat("" + patientSelected.getWeight());
						isPatient = true;
					}
					// else {
					if (jRadioUse.isSelected()) {
						if (jTextFieldUse.getText().compareTo("") == 0) {
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage(
									"angal.medicalstockwardedit.pleaseinsertadescriptionfortheinternaluse")); //$NON-NLS-1$
							jTextFieldUse.requestFocus();
							return;
						}
						description = jTextFieldUse.getText();
						isPatient = false;
					}
					// manage case of ward
					if (jRadioWard.isSelected()) {
						Object selectedObj=wardBox.getSelectedItem();
						if(selectedObj instanceof Ward){
							wardTo = (Ward) selectedObj;
						}
						else{
							JOptionPane.showMessageDialog(null,
									MessageBundle.getMessage("angal.medicalstock.multipledischarging.pleaseselectaward"));
							return;
						}
//						wardTo = (Ward) wardBox.getSelectedItem();
						isPatient = false;
					}

					if (medItems.size() == 0) {
						JOptionPane.showMessageDialog(null,
								MessageBundle.getMessage("angal.medicalstockwardedit.pleaseselectadrug")); //$NON-NLS-1$
						return;
					}

					MovWardBrowserManager wardManager = new MovWardBrowserManager();
					GregorianCalendar newDate = TimeTools.getServerDateTime();

					// innit of the datas needed to store the movement
					ArrayList<Movement> movements = new ArrayList<Movement>();
					MovStockInsertingManager movManager = new MovStockInsertingManager();
					MovementType typeDischarge = new MedicaldsrstockmovTypeBrowserManager()
							.getMovementType("discharge");
					Lot aLot = new Lot("", newDate, newDate);
					String refNo = "";

					if (medItems.size() == 1) {

						MovementWard oneMovementWard = new MovementWard(wardSelected, newDate, isPatient,
								patientSelected, age, weight, description, medItems.get(0).getMedical(),
								medItems.get(0).getQty(), MessageBundle.getMessage("angal.medicalstockwardedit.pieces"),
								wardTo);

						boolean result = wardManager.newMovementWard(oneMovementWard);
						if (result && wardTo != null) {

							MovementType typeCharge = new MedicaldsrstockmovTypeBrowserManager()
									.getMovementType("charge");
							Movement oneMovement =
									// new
									// Movement(medItems.get(0).getMedical(),typeCharge,wardTo,aLot,newDate,medItems.get(0).getQty().intValue(),
									// new Supplier(), refNo, wardSelected);
									new Movement(medItems.get(0).getMedical(), typeCharge, wardTo, aLot, newDate,
											medItems.get(0).getQty().intValue(), null, refNo, wardSelected);
							movements.add(oneMovement);
							// movManager.newMultipleDischargingMovements(movements);
							movManager.newMultipleChargingMovements(movements);

						}
						if (result) {
							fireMovementWardInserted();
						}
						if (!result)
							JOptionPane.showMessageDialog(null,
									MessageBundle.getMessage("angal.medicalstockwardedit.thedatacouldnotbesaved"));
						else
							dispose();
					} else {
						ArrayList<MovementWard> manyMovementWard = new ArrayList<MovementWard>();
						MovementType typeCharge = new MedicaldsrstockmovTypeBrowserManager().getMovementType("charge");
						for (int i = 0; i < medItems.size(); i++) {
							manyMovementWard.add(new MovementWard(wardSelected, newDate, isPatient, patientSelected,
									age, weight, description, medItems.get(i).getMedical(), medItems.get(i).getQty(),
									MessageBundle.getMessage("angal.medicalstockwardedit.pieces"), wardTo));
							// Movement oneMovement = new
							// Movement(medItems.get(i).getMedical(),typeDischarge,wardSelected,aLot,newDate,medItems.get(i).getQty().intValue(),new
							// Supplier(), refNo, wardTo);
							Movement oneMovement = new Movement(medItems.get(i).getMedical(), typeCharge, wardTo, aLot,
									newDate, medItems.get(i).getQty().intValue(), null, refNo, wardSelected);
							movements.add(oneMovement);
						}

						boolean result = wardManager.newMovementWard(manyMovementWard);
						// movManager.newMultipleDischargingMovements(movements);
						if (wardTo != null) {
							movManager.newMultipleChargingMovements(movements);
						}

						if (result) {
							fireMovementWardInserted();
						}
						if (!result)
							JOptionPane.showMessageDialog(null,
									MessageBundle.getMessage("angal.medicalstockwardedit.thedatacouldnotbesaved"));
						else
							dispose();
					}
				}
			});
		}
		return jButtonOK;
	}

	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton();
			jButtonCancel.setText(MessageBundle.getMessage("angal.common.cancel")); //$NON-NLS-1$
			jButtonCancel.setMnemonic(KeyEvent.VK_C);
			jButtonCancel.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					int confirm = JOptionPane.showOptionDialog(null, MessageBundle.getMessage("angal.common.areyousuretoexit"), 
							MessageBundle.getMessage("angal.common.exitconfirmation"), JOptionPane.YES_NO_OPTION, 
				             JOptionPane.QUESTION_MESSAGE, null, null, null);
				    if (confirm == JOptionPane.YES_OPTION) {
				    	dispose();
				    }else		        
					    return;
					//dispose();
				}
			});
		}
		return jButtonCancel;
	}

	private JPanel getJPanelButtons() {
		if (jPanelButtons == null) {
			jPanelButtons = new JPanel();
			jPanelButtons.add(getJButtonOK());
			jPanelButtons.add(getJButtonCancel());
		}
		return jPanelButtons;
	}

	private JPanel getJPanelMedicals() {
		if (jPanelMedicals == null) {
			jPanelMedicals = new JPanel();
			jPanelMedicals.setLayout(new BorderLayout());
			// jPanelMedicals.setLayout(new BoxLayout(jPanelMedicals,
			// BoxLayout.X_AXIS));
			jPanelMedicals.add(getJScrollPaneMedicals(), BorderLayout.CENTER);
//			jPanelMedicals.add(getJPanelMedicalsButtons());
		}
		return jPanelMedicals;
	}

	private JPanel getJPanelPatient() {
		if (jPanelPatient == null) {
			jPanelPatient = new JPanel(new FlowLayout(FlowLayout.LEFT));
			jPanelPatient.add(getJRadioPatient());
			jPanelPatient.add(getJLabelPatient());
			jPanelPatient.add(getJTextFieldPatient());
			jPanelPatient.add(getJButtonPickPatient());
			jPanelPatient.add(getJButtonTrashPatient());
		}
		return jPanelPatient;
	}

	private JRadioButton getJRadioPatient() {
		if (jRadioPatient == null) {
			jRadioPatient = new JRadioButton();
			// jRadioPatient.setSelected(true);
			jRadioPatient.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					//System.out.println("in jRadioPatient: " + e.getID());
					jTextFieldUse.setEnabled(false);
					jTextFieldPatient.setEnabled(true);
					jButtonPickPatient.setEnabled(true);
					wardBox.setEnabled(false);
					if (patientSelected != null)
						jButtonTrashPatient.setEnabled(true);

				}
			});
		}
		return jRadioPatient;
	}

	private JButton getJButtonTrashPatient() {
		if (jButtonTrashPatient == null) {
			jButtonTrashPatient = new JButton();
			jButtonTrashPatient.setMnemonic(KeyEvent.VK_R);
			jButtonTrashPatient.setPreferredSize(new Dimension(25, 30));
			jButtonTrashPatient.setIcon(new ImageIcon("rsc/icons/remove_patient_button.png")); //$NON-NLS-1$
			jButtonTrashPatient.setToolTipText(MessageBundle
					.getMessage("angal.medicalstockwardedit.tooltip.removepatientassociationwiththismovement")); //$NON-NLS-1$
			jButtonTrashPatient.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					patientSelected = null;
					jTextFieldPatient.setText(""); //$NON-NLS-1$
					jTextFieldPatient.setEditable(true);
					jButtonPickPatient.setText(MessageBundle.getMessage("angal.medicalstockwardedit.pickpatient"));
					jButtonPickPatient.setToolTipText(MessageBundle
							.getMessage("angal.medicalstockwardedit.tooltip.associateapatientwiththismovement")); //$NON-NLS-1$
					jButtonTrashPatient.setEnabled(false);
				}
			});
			jButtonTrashPatient.setEnabled(false);
		}
		return jButtonTrashPatient;
	}

	private JButton getJButtonPickPatient() {
		if (jButtonPickPatient == null) {
			jButtonPickPatient = new JButton();
			jButtonPickPatient.setEnabled(false);
			jButtonPickPatient.setPreferredSize(new Dimension(33, 30));
			jButtonPickPatient.setText(MessageBundle.getMessage("angal.medicalstockwardedit.pickpatient"));
			jButtonPickPatient.setMnemonic(KeyEvent.VK_P);
			jButtonPickPatient.setIcon(new ImageIcon("rsc/icons/pick_patient_button.png")); //$NON-NLS-1$
			jButtonPickPatient.setToolTipText(
					MessageBundle.getMessage("angal.medicalstockwardedit.tooltip.associateapatientwiththismovement"));
			jButtonPickPatient.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					SelectPatient sp = new SelectPatient(WardPharmacyNew.this, patientSelected);
					sp.addSelectionListener(WardPharmacyNew.this);
					sp.pack();
					sp.setVisible(true);
				}
			});
		}
		return jButtonPickPatient;
	}

	private JTextField getJTextFieldPatient() {
		if (jTextFieldPatient == null) {
			jTextFieldPatient = new JTextField();
			jTextFieldPatient.setEnabled(false);
			jTextFieldPatient.setText(""); //$NON-NLS-1$
			jTextFieldPatient.setPreferredSize(new Dimension(300, 30));
			// Font patientFont=new Font(jTextFieldPatient.getFont().getName(),
			// Font.BOLD, jTextFieldPatient.getFont().getSize() + 4);
			// jTextFieldPatient.setFont(patientFont);
		}
		return jTextFieldPatient;
	}

	private JLabel getJLabelPatient() {
		if (jLabelPatient == null) {
			jLabelPatient = new JLabel();
			jLabelPatient.setText(MessageBundle.getMessage("angal.medicalstockwardedit.patient"));
		}
		return jLabelPatient;
	}

	public class MedicalTableModel implements TableModel {

		public MedicalTableModel() {

		}

		public Class<?> getColumnClass(int i) {
			return medClasses[i].getClass();
		}

		public int getColumnCount() {
			return medClasses.length;
		}

		public int getRowCount() {
			if (medItems == null)
				return 0;
			return medItems.size();
		}

		public Medical getMedical(int row) {
			if (medItems.size() > row && row >= 0) {
				return medItems.get(row).getMedical();
			}
			return null;
		}
        public void removeItem(int idItem){
        	medItems.remove(idItem);
        }
		public Double getValueIntAt(int r, int c) {
			if (c == 1) {
				return medItems.get(r).getQty();
			}
			return null;
		}

		public Object getValueAt(int r, int c) {
			if (c == -1) {
				return medItems.get(r);
			}
			if (c == 0) {
				return medItems.get(r).getMedical().getDescription();
			}
			if (c == 1) {
				return medItems.get(r).getQty();
			}
			return null;
		}

		public boolean isCellEditable(int r, int c) {
			if (c == 1)
				return true;
			return false;
		}

		public void setValueAt(Object item, int r, int c) {
			// if (c == 1) billItems.get(r).setItemQuantity((Integer)item);

		}

		public void addTableModelListener(TableModelListener l) {

		}

		public String getColumnName(int columnIndex) {
			return medColumnNames[columnIndex];
		}

		public void removeTableModelListener(TableModelListener l) {
		}

	}

	private JPanel getPanelWard() {
		if (panelWard == null) {
			panelWard = new JPanel();
			panelWard.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
			panelWard.add(getJRadioWard());
			panelWard.add(getJLabelSelectWard());
			panelWard.add(getWardBox());
		}
		return panelWard;
	}

	private JRadioButton getJRadioWard() {
		if (jRadioWard == null) {
			jRadioWard = new JRadioButton(MessageBundle.getMessage("angal.wardpharmacynew.ward"));
			jRadioWard.setSelected(true);
			jRadioWard.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					jTextFieldUse.setEnabled(false);
					jTextFieldPatient.setEnabled(false);
					jButtonPickPatient.setEnabled(false);
					wardBox.setEnabled(true);
				}
			});
			jRadioWard.setMinimumSize(new Dimension(55, 23));
			jRadioWard.setMaximumSize(new Dimension(57, 23));
		}
		return jRadioWard;
	}

	private JComboBox getWardBox() {
		if (wardBox == null) {
			wardBox = new JComboBox();
			wardBox.setPreferredSize(new Dimension(300, 30));
			org.isf.ward.manager.WardBrowserManager wbm = new org.isf.ward.manager.WardBrowserManager();
			ArrayList<Ward> wardList = wbm.getWards();
			wardBox.addItem("");
			for (org.isf.ward.model.Ward elem : wardList) {
				if (!wardSelected.getCode().equals(elem.getCode()))
					wardBox.addItem(elem);
			}
			wardBox.setEnabled(true);
		}
		return wardBox;
	}

	private JLabel getJLabelSelectWard() {
		if (jLabelSelectWard == null) {
			jLabelSelectWard = new JLabel(MessageBundle.getMessage("angal.wardpharmacynew.selectward"));
			jLabelSelectWard.setVisible(false);
			jLabelSelectWard.setAlignmentX(Component.CENTER_ALIGNMENT);
		}
		return jLabelSelectWard;
	}

	// private JPanel getPanelEntry() {
	// if (panelEntry == null) {
	// panelEntry = new JPanel();
	// }
	// return panelEntry;
	// }
	private JPanel getPanelEntry() {
		panelEntry = new JPanel();
		GridBagLayout gbl_panel = new GridBagLayout();

		gbl_panel.columnWidths = new int[] { 150, 150, 100, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };

		gbl_panel.rowHeights = new int[] { 30, 0 };

		gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panelEntry.setLayout(gbl_panel);
		GridBagConstraints gbc_jTextFieldSearch = new GridBagConstraints();
		gbc_jTextFieldSearch.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldSearch.anchor = GridBagConstraints.NORTH;
		gbc_jTextFieldSearch.gridx = 0;
		gbc_jTextFieldSearch.gridy = 0;
		panelEntry.add(getJTextFieldSearch(), gbc_jTextFieldSearch);

		GridBagConstraints gbc_jTextFieldDescription = new GridBagConstraints();
		gbc_jTextFieldDescription.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldDescription.anchor = GridBagConstraints.NORTH;
		gbc_jTextFieldDescription.gridx = 1;
		gbc_jTextFieldDescription.gridy = 0;
		panelEntry.add(getJTextFieldDescription(), gbc_jTextFieldDescription);

		GridBagConstraints gbc_jTextFieldQty = new GridBagConstraints();
		gbc_jTextFieldQty.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldQty.anchor = GridBagConstraints.NORTH;
		gbc_jTextFieldQty.gridx = 2;
		gbc_jTextFieldQty.gridy = 0;
		panelEntry.add(getJTextFieldQty(), gbc_jTextFieldQty);

		return panelEntry;
	}

	private JTextField getJTextFieldSearch() {
		if (jTextFieldSearch == null) {
			jTextFieldSearch = new JTextField();
			jTextFieldSearch.setPreferredSize(new Dimension(300, 30));
			jTextFieldSearch.setHorizontalAlignment(SwingConstants.LEFT);
			jTextFieldSearch.setColumns(12);
			TextPrompt suggestion = new TextPrompt(
					MessageBundle
							.getMessage("angal.medicalstock.multipledischarging.typeacodeoradescriptionandpressenter"), //$NON-NLS-1$
					jTextFieldSearch, Show.FOCUS_LOST);
			{
				suggestion.setFont(new Font("Tahoma", Font.PLAIN, 14)); //$NON-NLS-1$
				suggestion.setForeground(Color.GRAY);
				suggestion.setHorizontalAlignment(JLabel.CENTER);
				suggestion.changeAlpha(0.5f);
				suggestion.changeStyle(Font.BOLD + Font.ITALIC);
			}
			jTextFieldSearch.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					///////////////////////
					ArrayList<Medical> currentMeds = new ArrayList<Medical>();
					currentMeds.addAll(medArray);
					ArrayList<Double> currentQties = new ArrayList<Double>();
					currentQties.addAll(qtyArray);
					HashMap<String, Medical> currentMedicalMap = new HashMap<String, Medical>();//
					currentMedicalMap.putAll(medicalMap);
					// remove already inserted items
					for (MedicalWard medItem : medItems) {
						Medical med = medItem.getMedical();
						int index = currentMeds.indexOf(med);
						currentMeds.remove(index);
						currentQties.remove(index);
						String key = med.getProd_code().toLowerCase();
						if (key.equals("")) //$NON-NLS-1$
							key = med.getCode().toString().toLowerCase();
						currentMedicalMap.remove(key);
					}

					//////////////////////////

					String text = jTextFieldSearch.getText();
					Medical med = null;
					// if (medicalMap.containsKey(text.toLowerCase())) {
					// // Medical found
					// currentMedical = medicalMap.get(text.toLowerCase());
					if (currentMedicalMap.containsKey(text.toLowerCase())) {
						// Medical found
						currentMedical = currentMedicalMap.get(text.toLowerCase());
					} else {
						currentMedical = chooseMedical(text.toLowerCase());
					}

					if (currentMedical != null) {
						jTextFieldSearch.setText(currentMedical.getProd_code().toUpperCase());
						jTextFieldDescription.setText(currentMedical.getDescription());
						jTextFieldQty.requestFocus();
						jTextFieldQty.selectAll();
						jTextFieldSearch.setEnabled(false);
					}
				}
			});

			jTextFieldSearch.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						newMedical();
						return;
					}
				}
				@Override
				public void keyReleased(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_SPACE) {
						String actual = jTextFieldSearch.getText();
						jTextFieldSearch.setText(actual.replaceAll(" ", "_"));
					}
				}
			});
		}
		return jTextFieldSearch;
	}

	private JTextField getJTextFieldDescription() {
		if (jTextFieldDescription == null) {
			jTextFieldDescription = new JTextField();
			jTextFieldDescription.setEnabled(false);
			jTextFieldDescription.setPreferredSize(new Dimension(150, 30));
			jTextFieldDescription.setHorizontalAlignment(SwingConstants.LEFT);
			jTextFieldDescription.setColumns(10);
		}
		return jTextFieldDescription;
	}

	private JTextField getJTextFieldQty() {
		if (jTextFieldQty == null) {
			jTextFieldQty = new JTextField();
			jTextFieldQty.setPreferredSize(new Dimension(100, 30));
			jTextFieldQty.setHorizontalAlignment(SwingConstants.RIGHT);
			jTextFieldQty.setColumns(10);
			

			PlainDocument doc = (PlainDocument) jTextFieldQty.getDocument();
		      doc.setDocumentFilter(new IntegerDocumentFilter());
		      
			TextPrompt suggestion = new TextPrompt(
					MessageBundle.getMessage("angal.medicalstock.multiplecharging.typeqty"), //$NON-NLS-1$
					jTextFieldQty, Show.FOCUS_LOST);
			{
				suggestion.setFont(new Font("Tahoma", Font.PLAIN, 12)); //$NON-NLS-1$
				suggestion.setForeground(Color.GRAY);
				suggestion.setHorizontalAlignment(JLabel.CENTER);
				suggestion.changeAlpha(0.5f);
				suggestion.changeStyle(Font.BOLD + Font.ITALIC);
			}

			jTextFieldQty.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						newMedical();
						return;
					}
					if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_TAB
							|| e.getKeyCode() == KeyEvent.VK_RIGHT) {
						// Move to next field
						double qty = getQty(true);
						if (qty <= 0) {
							jTextFieldQty.requestFocus();
							e.consume();
							return;
						} else {
							int index = medArray.indexOf(currentMedical);
							System.out.println("index en question "+index);
							double maxQty = qtyArray.get(index);
							if (maxQty < qty) {
								
								StringBuilder message = new StringBuilder();
								message.append(
										MessageBundle.getMessage("angal.medicalstock.multipledischarging.thequantityisnotavailable")); //$NON-NLS-1$
								message.append("\n").append(MessageBundle.getMessage("angal.medicalstock.multipledischarging.lyinginstock")) //$NON-NLS-1$ //$NON-NLS-2$
										.append(maxQty);
								JOptionPane.showMessageDialog(WardPharmacyNew.this, message.toString(), MessageBundle.getMessage("angal.hospital"),JOptionPane.PLAIN_MESSAGE);
								
//								JOptionPane.showMessageDialog(WardPharmacyNew.this,
//										MessageBundle.getMessage(
//												"angal.medicalstockward.pleaseselectanoutcomesmovementfirst"),
//										MessageBundle.getMessage("angal.hospital"), JOptionPane.PLAIN_MESSAGE);
								jTextFieldQty.requestFocus();
								jTextFieldQty.selectAll();
							} else {
								addItem(currentMedical, qty);
								newMedical();
							}

						}
					}
				}

				@Override
				public void keyTyped(KeyEvent e) {
					char c = e.getKeyChar();
					if (!(Character.isDigit(c))) {
						e.consume();
					}
				};
			});

			jTextFieldQty.addFocusListener(new FocusListener() {

				@Override
				public void focusLost(FocusEvent e) {

				}

				@Override
				public void focusGained(FocusEvent e) {
					if (e.getComponent() == jTextFieldQty) {
						if (jTextFieldQty.getText().trim().equals("")) {
							jTextFieldQty.setText("1");
						}
						jTextFieldQty.selectAll();
					}

				}
			});

		}
		return jTextFieldQty;
	}

	private void initialize() {
		MedicalBrowsingManager medMan = new MedicalBrowsingManager();
		ArrayList<Medical> medicals = medMan.getMedicals();
		medicalMap = new HashMap<String, Medical>();
		for (Medical med : medicals) {
			String key = med.getProd_code().toLowerCase();
			if (key.equals("")) //$NON-NLS-1$
				key = med.getCode().toString().toLowerCase();
			medicalMap.put(key, med);
		}
		// units = new ArrayList<Integer>();
	}

	protected Medical chooseMedical(String text) {
		ArrayList<Medical> medList = new ArrayList<Medical>();
		///////////////
		HashMap<String, Medical> currentMedicalMap = new HashMap<String, Medical>();//
		currentMedicalMap.putAll(medicalMap);
		// remove already inserted items
		for (MedicalWard medItem : medItems) {
			Medical med = medItem.getMedical();
			String key = med.getProd_code().toLowerCase();
			if (key.equals("")) //$NON-NLS-1$
				key = med.getCode().toString().toLowerCase();
			currentMedicalMap.remove(key);
		}
		///////////////
		// for (Medical aMed : medicalMap.values()) {
		for (Medical aMed : currentMedicalMap.values()) {
			if (NormalizeString.normalizeContains(aMed.getDescription().toLowerCase(), text))
				medList.add(aMed);
		}
		Collections.sort(medList);
		Medical med = null;

		if (!medList.isEmpty()) {

			// JTable medTable = new JTable(new StockMedModel(medList));
			// medTable.getColumnModel().getColumn(0).setMaxWidth(CODE_COLUMN_WIDTH);
			// medTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			// JPanel panel = new JPanel();
			// panel.add(new JScrollPane(medTable));
			//
			// int ok =
			// JOptionPane.showConfirmDialog(MovStockMultipleCharging.this,
			// panel,
			// MessageBundle.getMessage("angal.medicalstock.multiplecharging.chooseamedical"),
			// //$NON-NLS-1$
			// JOptionPane.YES_NO_OPTION);
			//
			// if (ok == JOptionPane.OK_OPTION) {
			// int row = medTable.getSelectedRow();
			// med = medList.get(row);
			// }

			MedicalPicker framas = new MedicalPicker(new StockMedModel(medList));

			framas.setSize(300, 400);

			JDialog dialog = new JDialog();
			dialog.setLocationRelativeTo(null);
			dialog.setSize(600, 350);
			dialog.setLocationRelativeTo(null);
			dialog.setModal(true);

			dialog.setTitle(MessageBundle.getMessage("angal.medicalstock.multiplecharging.selectmedical"));

			Image ico = new javax.swing.ImageIcon("rsc/icons/medical_dialog.png").getImage();

			framas.setParentFrame(dialog);
			dialog.setContentPane(framas);
			dialog.setIconImage(ico);
			dialog.setVisible(true);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

			med = framas.getSelectedMedical();
			return med;
		}
		return null;
	}

	protected int getQty(boolean showMessage) {
		String strQty = jTextFieldQty.getText();
		if (strQty == null || strQty.trim().equals("")) {
			if (showMessage)
				JOptionPane.showMessageDialog(WardPharmacyNew.this,
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantitypleasetryagain"), //$NON-NLS-1$
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantity"), //$NON-NLS-1$
						JOptionPane.ERROR_MESSAGE);
			return -1;
		}

		int qty = 1;
		try {
			qty = Integer.valueOf(strQty);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			if (showMessage)
				JOptionPane.showMessageDialog(WardPharmacyNew.this,
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantitypleasetryagain"), //$NON-NLS-1$
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantity"), //$NON-NLS-1$
						JOptionPane.ERROR_MESSAGE);
			return -1;
		}
		if (qty <= 0) {
			if (showMessage)
				JOptionPane.showMessageDialog(WardPharmacyNew.this,
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantitypleasetryagain"), //$NON-NLS-1$
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantity"), //$NON-NLS-1$
						JOptionPane.ERROR_MESSAGE);
			return -1;
		}
		return qty;
	}

	protected void newMedical() {
		jTextFieldSearch.setText("");
		jTextFieldDescription.setText("");
		jTextFieldQty.setText("");
		jTextFieldSearch.requestFocus();
		currentMedical = null;
		jTextFieldSearch.setEnabled(true);
	}

	private void loadMedical(Double qty) {
		if (currentMedical != null) {
			// isNew = false;
			jTextFieldSearch.setText(currentMedical.getProd_code());
			jTextFieldDescription.setText(currentMedical.getDescription());
			int qtyInt=qty.intValue();
			jTextFieldQty.setText(String.valueOf(qtyInt));
			jTextFieldSearch.setEnabled(false);
			jTextFieldDescription.setEnabled(false);
			jTextFieldQty.requestFocus();
			jTextFieldQty.selectAll();
		}

	}

	@Override
	public void InventoryUpdated(AWTEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void InventoryInserted(AWTEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void InventoryValidated(AWTEvent e) {
		refreshData();
	}

	@Override
	public void InventoryCancelled(AWTEvent e) {
		// TODO Auto-generated method stub		
	}
	
	private void refreshData(){
		MovWardBrowserManager wardManager = new MovWardBrowserManager();
		medicalMap.clear();
    	medicalMap = new HashMap<String, Medical>();
    	wardDrugs.clear();
    	wardDrugs = wardManager.getMedicalsWard(wardSelected.getCode());
		medArray.clear();
		qtyArray.clear();
		Medical med;
		for (MedicalWard elem : wardDrugs) {
			medArray.add(elem.getMedical());
			qtyArray.add(elem.getQty());
			med = elem.getMedical();
			String key = med.getProd_code().toLowerCase();
			if (key.equals("")) 
				key = med.getCode().toString().toLowerCase();
			medicalMap.put(key, med);			
		}
    }
}
