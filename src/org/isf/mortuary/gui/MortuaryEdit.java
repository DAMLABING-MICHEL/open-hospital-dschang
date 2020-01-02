package org.isf.mortuary.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EventListener;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.isf.generaldata.MessageBundle;
import org.isf.lab.gui.LabNew.CheckBox;
import org.isf.medicals.gui.MedicalEdit.MedicalEditListener;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.menu.gui.MainMenu;
import org.isf.mortuary.manager.DeathReasonBrowserManager;
import org.isf.mortuary.manager.MortuaryBrowserManager;
import org.isf.mortuary.model.DeathReason;
import org.isf.mortuary.model.Death;
import org.isf.parameters.manager.Param;
import org.isf.patient.gui.SelectPatient;
import org.isf.patient.gui.PatientInsert.PatientListener;
import org.isf.patient.gui.SelectPatient.SelectionListener;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;

import javax.swing.ComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.GroupLayout.Alignment;

import java.awt.AWTEvent;
import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;
import javax.swing.event.EventListenerList;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import com.toedter.calendar.JDateChooser;

public class MortuaryEdit extends JDialog implements SelectionListener{

	private static EventListenerList MortuaryEditListeners = new EventListenerList();	 
	public interface MortuaryListener extends EventListener {
	        public void mortuaryUpdated(AWTEvent e);
	        public void mortuaryInserted(AWTEvent e);
	    }

	    public void addDeathListener(MortuaryListener l) {
	    	MortuaryEditListeners.add(MortuaryListener.class, l);
	    }
	    public void removeDeathListener(MortuaryListener listener) {
	    	MortuaryEditListeners.remove(MortuaryListener.class, listener);
	    }

	    private void fireDeathInserted() {
	        AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {
	        	
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;};
				
	        EventListener[] listeners = MortuaryEditListeners.getListeners(MortuaryListener.class);
	        for (int i = 0; i < listeners.length; i++)
	            ((MortuaryListener)listeners[i]).mortuaryInserted(event);
	    }
	    
	    private void fireDeathUpdated() {
	        AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;};

	        EventListener[] listeners = MortuaryEditListeners.getListeners(MortuaryListener.class);
	        for (int i = 0; i < listeners.length; i++)
	            ((MortuaryListener)listeners[i]).mortuaryUpdated(event);
	    }	
	
	/**
	 * 
	 */	
	
	private static final long serialVersionUID = 1L;
	private EventListenerList mortuaryListeners = new EventListenerList();
	private JTextField patientChoosed;
	private JTextField deathPlace;	
	private JTextField nameDeclarant;
	private JTextField telDeclarant;	
	private JTextField nidDeclarant;
	private JTextField nameFamille;
	private JTextField telFamille;
	private JTextField nidFamille;
	private JTextField casier;
	private JButton btnChoosePatient;
	private JComboBox comboBoxOrigin;
	private JComboBox comboMotif;
	private JDateChooser jCalendarDeathDate;
	private JDateChooser jCalendarEntryDate;
	private JDateChooser jCalendarLeavingDate;
	private JCheckBox chckbxLeaving;
	private JButton btnSave;
	private JButton btnQuit;
	private Patient patientSelected = null;
	private Death death = null;
	private boolean insert = false;
	private Death deathSelected;
	private DeathReason selectedMotif = new DeathReason(0, "", "");
	private MortuaryBrowserManager manager;
	private GregorianCalendar dateDeath = TimeTools.getServerDateTime();
	private GregorianCalendar dateEntry = TimeTools.getServerDateTime();
	private GregorianCalendar dateLeaving = TimeTools.getServerDateTime();
	private ArrayList<MedicalWard> medWardList = new ArrayList<MedicalWard>();	
	private ArrayList<DeathReason> listMotifs = new ArrayList<DeathReason>();
	private PatientBrowserManager pat = new PatientBrowserManager();
	
	private String oldProvenance;
	private GregorianCalendar oldDateDeces;
	private GregorianCalendar oldDateEntree;
	private GregorianCalendar oldDateSortie;
	private GregorianCalendar oldDateSortieProvisoire;
	private int oldIdMotif;
	private String oldNomDeclarant;
	private String oldTelDeclarant;
	private String oldNidDeclarant;
	private String oldNomFamille;
	private String oldTelFamille;
	private String oldNidFamille;
	private String oldPatientName;
	private String oldCasier;
	private boolean oldBodyLift;
	
	public JTextField getCasier() {
		if(casier == null) {
			casier = new JTextField();
			casier.setPreferredSize(new Dimension(21, 60));
			casier.setColumns(10);
		}
		if(!insert) {
			casier.setText(deathSelected.getCasier());
		}
		return casier;
	}
	public JTextField getPatientChoosed() {
		if(patientChoosed == null) {
			patientChoosed = new JTextField();
			patientChoosed.setPreferredSize(new Dimension(6, 25));
			patientChoosed.setColumns(10);
			patientChoosed.setEditable(false);
		}
		if(!insert) {
			patientChoosed.setText(deathSelected.getPatientName());
			patientSelected = pat.getPatient(deathSelected.getIdPatient());

		}
		return patientChoosed;
	}
	public JTextField getNameDeclarant() {
		if(nameDeclarant == null) {
			nameDeclarant = new JTextField();
			nameDeclarant.setColumns(10);
		}
		if(!insert)nameDeclarant.setText(deathSelected.getNomDeclarant());
		return nameDeclarant;
	}	
	public JTextField getDeathPlace() {
		if(deathPlace == null) {
			deathPlace = new JTextField();
			deathPlace.setPreferredSize(new Dimension(6, 25));
			deathPlace.setColumns(10);
		}
		if(!insert)deathPlace.setText(deathSelected.getLieu());
		return deathPlace;
	}
	public JTextField getTelDeclarant() {
		if(telDeclarant == null) {
			telDeclarant = new JTextField();
			telDeclarant.setColumns(10);
		}
		if(!insert)telDeclarant.setText(deathSelected.getTelDeclarant());
		return telDeclarant;
	}
	public JTextField getNidDeclarant() {
		if(nidDeclarant == null) {
			nidDeclarant = new JTextField();
			nidDeclarant.setColumns(10);
		}
		if(!insert)nidDeclarant.setText(deathSelected.getNidDeclarant());
		return nidDeclarant;
	}
	public JTextField getNameFamille() {
		if(nameFamille == null) {
			nameFamille = new JTextField();
			nameFamille.setColumns(10);
		}
		if(!insert)nameFamille.setText(deathSelected.getNomFamille());
		return nameFamille;
	}
	public JTextField getNidFamille() {		
		if(nidFamille == null) {
			nidFamille = new JTextField();
			nidFamille.setColumns(10);
		}
		if(!insert)nidFamille.setText(deathSelected.getNidFamille());
		return nidFamille;
	}
	public JTextField getTelFamille() {
		if(telFamille == null) {
			telFamille = new JTextField();
			telFamille.setColumns(10);
		}
		if(!insert)telFamille.setText(deathSelected.getTelFamille());
		return telFamille;
	}
	public 	JCheckBox getChckbxLeaving() {
		if(chckbxLeaving == null) {
			chckbxLeaving = new JCheckBox(MessageBundle.getMessage("angal.mortuaryedit.alreadylift"));
		}
		chckbxLeaving.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(chckbxLeaving.isSelected() ) {
					GregorianCalendar leavingDateTmp = new GregorianCalendar();
					if(jCalendarLeavingDate.getDate()!= null) {						
						leavingDateTmp = new GregorianCalendar();
						leavingDateTmp.setTime(jCalendarLeavingDate.getDate());
					} else {
						JOptionPane.showMessageDialog(MortuaryEdit.this, MessageBundle.getMessage("angal.mortuaryedit.providedateleaving"));
						chckbxLeaving.setSelected(false);
						return;
					}
					SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yy HH:mm");
					String message = MessageBundle.getMessage("angal.mortuaryedit.confirmdateleaving1") + " "+ fmt.format(deathSelected.getDateSortieProvisoire().getTime())
							+ " " + MessageBundle.getMessage("angal.mortuaryedit.confirmdateleaving2");
					int res = JOptionPane.showConfirmDialog(MortuaryEdit.this, message, MessageBundle.getMessage("angal.therapy.warning"), JOptionPane.YES_NO_OPTION);
					if(res == JOptionPane.NO_OPTION) {						
						chckbxLeaving.setSelected(false);
					} else {
						GregorianCalendar now = new GregorianCalendar();
						if(deathSelected.getDateSortieProvisoire().after(now)) {
							JOptionPane.showMessageDialog(MortuaryEdit.this, 
									MessageBundle.getMessage("angal.mortuaryedit.liftingtooearly"), 
									MessageBundle.getMessage("angal.therapy.warning"),
									JOptionPane.ERROR_MESSAGE);
							chckbxLeaving.setSelected(false);
							return;
						}
					}
				}
			}
		});
		if(!insert && deathSelected.getDateSortie() != null) chckbxLeaving.setSelected(true);
		return chckbxLeaving;
	}	
	
	public MortuaryEdit(JFrame owner, Death oldDeath, boolean inserting) {
		super(owner, true);
		deathSelected = oldDeath;
		insert = inserting;
		
		if(!insert) {
		oldProvenance = deathSelected.getProvenance();
		oldDateDeces = deathSelected.getDateDeces();
		oldDateEntree = deathSelected.getDateEntree();
		oldDateSortieProvisoire = deathSelected.getDateSortieProvisoire();
		oldIdMotif = deathSelected.getIdMotif();
		oldNomDeclarant = deathSelected.getNomDeclarant();
		oldTelDeclarant = deathSelected.getTelDeclarant();
		oldNidDeclarant = deathSelected.getNidDeclarant();
		oldNomFamille = deathSelected.getNomFamille();
		oldTelFamille = deathSelected.getTelFamille();
		oldNidFamille = deathSelected.getNidFamille();
		oldPatientName = deathSelected.getPatientName();
		oldCasier = deathSelected.getCasier();
		oldBodyLift = deathSelected.getDateSortie() != null;
		}
		setMinimumSize(new Dimension(750, 450));
		if(insert)setTitle(MessageBundle.getMessage("angal.mortuarybrowser.new"));
		else setTitle(MessageBundle.getMessage("angal.mortuarybrowser.editdeath") + " "+deathSelected.getPatientName());
		
		//pack();
		setLocationRelativeTo(null);
		setResizable(false);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, MessageBundle.getMessage("angal.mortuaryedit.death"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, MessageBundle.getMessage("angal.mortuaryedit.declarer"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel panel_2 = new JPanel();
		
		JPanel panel_0 = new JPanel();
		panel_0.setBorder(new TitledBorder(null, MessageBundle.getMessage("angal.mortuaryedit.lift"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_0.add(getChckbxLeaving());
		
		JPanel panel_3 = new JPanel();
		JPanel panel_casier = new JPanel();
		panel_casier.setBorder(new TitledBorder(null, MessageBundle.getMessage("angal.mortuaryedit.casiertitle"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		panel_3.setBorder(new TitledBorder(null, MessageBundle.getMessage("angal.mortuaryedit.family"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel panel_4 = new JPanel(new BorderLayout());
		panel_4.add(panel_1, BorderLayout.NORTH);
		panel_4.add(panel_3, BorderLayout.SOUTH);
	
		JPanel panel_5 = new JPanel(new BorderLayout());
		panel_5.add(panel, BorderLayout.NORTH);
		panel_5.add(panel_0, BorderLayout.SOUTH);
		
		JPanel panel_6 = new JPanel(new BorderLayout());
		panel_6.add(panel_4, BorderLayout.NORTH);
		panel_6.add(panel_casier, BorderLayout.SOUTH);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(panel_5, BorderLayout.WEST);
		getContentPane().add(panel_6, BorderLayout.EAST);
		getContentPane().add(panel_2, BorderLayout.SOUTH);
		
		JLabel label = new JLabel(MessageBundle.getMessage("angal.mortuaryedit.fullname"));
		
		JLabel label_1 = new JLabel(MessageBundle.getMessage("angal.mortuaryedit.tel"));
		
		JLabel label_2 = new JLabel(MessageBundle.getMessage("angal.mortuaryedit.nid"));
		GroupLayout gl_panel_3 = new GroupLayout(panel_3);
		gl_panel_3.setHorizontalGroup(
			gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
						.addComponent(label, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_2, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE))
					.addGap(5)
					.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
						.addComponent(getNidFamille(), GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
						.addComponent(getTelFamille(), GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
						.addComponent(getNameFamille(), GroupLayout.DEFAULT_SIZE, 200,GroupLayout.PREFERRED_SIZE)))
		);
		gl_panel_3.setVerticalGroup(
			gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
						.addGap(10)
					.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_3.createSequentialGroup()
							.addComponent(label, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(15))
						.addGroup(gl_panel_3.createSequentialGroup()
							.addComponent(getNameFamille(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
							.addGap(15)))
					.addGroup(gl_panel_3.createParallelGroup(Alignment.BASELINE)
						.addComponent(getTelFamille(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addGap(15)
					.addGroup(gl_panel_3.createParallelGroup(Alignment.BASELINE)
						.addComponent(label_2, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addComponent(getNidFamille(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		panel_3.setLayout(gl_panel_3);
		
		JLabel lblNomComplet = new JLabel(MessageBundle.getMessage("angal.mortuaryedit.fullname"));
		
		JLabel lblTlphone = new JLabel(MessageBundle.getMessage("angal.mortuaryedit.tel"));
		
		JLabel lblNid = new JLabel(MessageBundle.getMessage("angal.mortuaryedit.nid"));
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(lblTlphone, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNid, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNomComplet, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE))
					.addGap(10)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
						.addGroup(Alignment.LEADING, gl_panel_1.createSequentialGroup()
							.addComponent(getNidDeclarant(), GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
							.addGap(10))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(getTelDeclarant(), GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
							.addGap(10))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(getNameDeclarant(), GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
							.addGap(10))))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblNomComplet, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGap(15)
							.addComponent(getNameDeclarant(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)))
					.addGap(15)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(getTelDeclarant(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblTlphone, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addGap(15)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(getNidDeclarant(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNid, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		panel_1.setLayout(gl_panel_1);
		
		JLabel lblNewLabel = new JLabel(MessageBundle.getMessage("angal.mortuarybrowser.patient"));
		

		JLabel lblNewLabel_1 = new JLabel(MessageBundle.getMessage("angal.mortuarybrowser.origin"));
		
		JLabel lblNewLabel_2 = new JLabel(MessageBundle.getMessage("angal.mortuaryedit.deathdate"));
		
		JLabel lblNewLabel_3 = new JLabel(MessageBundle.getMessage("angal.mortuaryedit.deathplace"));
		
		JLabel lblNewLabel_4 = new JLabel(MessageBundle.getMessage("angal.mortuaryedit.entrydate"));
		
		JLabel lblNewLabel_5 = new JLabel(MessageBundle.getMessage("angal.mortuaryedit.leavingdate"));
		
		JLabel lblNewLabel_6 = new JLabel(MessageBundle.getMessage("angal.mortuarybrowser.motif"));
		JLabel lblCasier = new JLabel(MessageBundle.getMessage("angal.mortuaryedit.casier"));
		
		
		
		GroupLayout gl_panel_casier = new GroupLayout(panel_casier);
		gl_panel_casier.setHorizontalGroup(
				gl_panel_casier.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_casier.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_casier.createParallelGroup(Alignment.LEADING)
						.addComponent(lblCasier, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE))
					.addGap(10)
					.addGroup(gl_panel_casier.createParallelGroup(Alignment.TRAILING)
						.addGroup(Alignment.LEADING, gl_panel_casier.createSequentialGroup()
							.addComponent(getCasier(), GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE))
							.addGap(10)))
		);
		gl_panel_casier.setVerticalGroup(
				gl_panel_casier.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_casier.createSequentialGroup()
					.addGroup(gl_panel_casier.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_casier.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblCasier, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel_casier.createSequentialGroup()
							.addComponent(getCasier(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))))
		);
		
		panel_casier.setLayout(gl_panel_casier);
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel_3, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_4, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_5, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_2, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_6, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE))
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(getComboMotif(), GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(getPatientChoosed(), GroupLayout.PREFERRED_SIZE, 170, GroupLayout.PREFERRED_SIZE)
							.addGap(5)
							.addComponent(getJButtonPickPatient(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
						.addComponent(getComboBoxOrigin(), GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
						.addComponent(getJCalendarLeavingDate(), Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
						.addComponent(getJCalendarDeathDate(), Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
						.addComponent(getDeathPlace(), GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
						.addComponent(getJCalendarEntryDate(), GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(getPatientChoosed(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addComponent(getJButtonPickPatient(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addGap(15)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(getComboBoxOrigin(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addGap(15)
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addComponent(getJCalendarDeathDate(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_2, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addGap(15)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(getDeathPlace(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_3, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addGap(15)
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addComponent(getJCalendarEntryDate(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_4, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(15)
							.addComponent(lblNewLabel_5, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(15)
							.addComponent(getJCalendarLeavingDate(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)))
					.addGap(15)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(getComboMotif(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_6, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		panel.setLayout(gl_panel);
		
		FlowLayout fl_panelButtons = new FlowLayout(FlowLayout.CENTER, 10, 5);
		
		JPanel p_actions = new JPanel(); 
		p_actions.setLayout(fl_panelButtons);
		p_actions.add(getBtnSave());
		p_actions.add(getJbtnQuit());
		
		panel_2.setLayout(new BorderLayout());
		//panel_2.add(getChckbxLeaving(), BorderLayout.WEST);
		panel_2.add(p_actions, BorderLayout.SOUTH);		
		
		//getContentPane().setLayout(groupLayout);
	}
	
	private JComboBox getComboBoxOrigin() {
		org.isf.ward.manager.WardBrowserManager wbm = new org.isf.ward.manager.WardBrowserManager();
		if (comboBoxOrigin == null) {
			comboBoxOrigin = new JComboBox();
			comboBoxOrigin.setPreferredSize(new Dimension(31, 25));
			String wardCode = MainMenu.getUserWard();
			// wardBox.addItem(MessageBundle.getMessage("angal.medicalstock.all")); 
			ArrayList<Ward> wardList = wbm.getWards();
			boolean trouve = false;
			for (Ward ward : wardList) {
				if (ward.getCode().equals(wardCode)) {
					comboBoxOrigin.addItem(ward);
					trouve = true;
					MovWardBrowserManager manager = new MovWardBrowserManager();
					medWardList = manager.getMedicalsWard(wardCode);					
					break;
				}
			}
			if (!trouve) {
				comboBoxOrigin.addItem("");
			}
			for (org.isf.ward.model.Ward elem : wardList) {
				comboBoxOrigin.addItem(elem);
			}
			comboBoxOrigin.setEnabled(true);
			comboBoxOrigin.addItem(MessageBundle.getMessage("angal.mortuary.othersource"));
			comboBoxOrigin.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
				}
			});
			
		}
	 if(!insert) {;
         int size = comboBoxOrigin.getItemCount();
         for(int i=0;i<size;i++) {
             if(comboBoxOrigin.getItemAt(i).toString().equals(deathSelected.getProvenance())) {comboBoxOrigin.getModel().setSelectedItem(comboBoxOrigin.getItemAt(i));
            	 break;	 
             };
		 
         }
	 }
		
		return comboBoxOrigin;
	}
	private JComboBox getComboMotif() {
		
		if(comboMotif == null) {
			comboMotif = new JComboBox();
			comboMotif.setPreferredSize(new Dimension(31, 25));
		
		DeathReasonBrowserManager motifDeces = new DeathReasonBrowserManager();
		comboMotif.addItem("");
		listMotifs = motifDeces.getDeathReasons();
		for(DeathReason motif: listMotifs) {
			comboMotif.addItem(motif);
		}
		comboMotif.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if(!e.getItem().toString().equals("")) {
						
						Object item = e.getItem();
						try {
							DeathReason motif = (DeathReason) item;
							selectedMotif = motif;
						}catch(ClassCastException e1) { 
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.mortuaryedit.error.deathReason"));
							return ;
						}
					}
				}
			}
		});
		
	 } if(!insert) {
		 DeathReasonBrowserManager deathr = new DeathReasonBrowserManager();
         int size =comboMotif.getItemCount();
         for(int i=0;i<size;i++) {
             if(comboMotif.getItemAt(i).toString().equals(deathr.getDeathReason(deathSelected.getIdMotif()).toString())) {
            	 comboMotif.getModel().setSelectedItem(comboMotif.getItemAt(i));
            	 break;
             }
		 
         }
	 }
		
		return comboMotif;
	}
	
	private JDateChooser getJCalendarDeathDate() {
		if (jCalendarDeathDate == null) {
			
			jCalendarDeathDate = new JDateChooser(); // Calendar
			jCalendarDeathDate.setPreferredSize(new Dimension(96, 25));
			jCalendarDeathDate.setLocale(new Locale(Param.string("LANGUAGE")));
			jCalendarDeathDate.setDateFormatString("dd/MM/yy HH:mm"); //$NON-NLS-1$
			
			jCalendarDeathDate.addPropertyChangeListener("date", new PropertyChangeListener() { //$NON-NLS-1$
				public void propertyChange(PropertyChangeEvent evt) {
					jCalendarDeathDate.setDate((Date) evt.getNewValue());
					dateDeath.setTime((Date) evt.getNewValue());				
				}
			});			
		}
		if(!insert) {
			jCalendarDeathDate.setCalendar(deathSelected.getDateDeces());
			Date sDateDeces = deathSelected.getDateDeces().getTime();

			if (sDateDeces != null) {
				dateDeath = (GregorianCalendar) Calendar.getInstance();
				dateDeath.setTimeInMillis(sDateDeces.getTime());
			}
		}		
		return jCalendarDeathDate;
	}

	private JDateChooser getJCalendarEntryDate() {
		if (jCalendarEntryDate == null) {
			jCalendarEntryDate = new JDateChooser(); // Calendar
			jCalendarEntryDate.setPreferredSize(new Dimension(96, 25));
			jCalendarEntryDate.setLocale(new Locale(Param.string("LANGUAGE")));
			jCalendarEntryDate.setDateFormatString("dd/MM/yy HH:mm"); //$NON-NLS-1$
			
			jCalendarEntryDate.addPropertyChangeListener("date", new PropertyChangeListener() { //$NON-NLS-1$			
				public void propertyChange(PropertyChangeEvent evt) {
					jCalendarEntryDate.setDate((Date) evt.getNewValue());
					dateEntry.setTime((Date) evt.getNewValue());				
				}
			});
			
		}
		if(!insert) {
			jCalendarEntryDate.setDate(deathSelected.getDateEntree().getTime());
			Date sDateEntree = deathSelected.getDateEntree().getTime();

			if (sDateEntree != null) {
				dateEntry = (GregorianCalendar) Calendar.getInstance();
				dateEntry.setTimeInMillis(sDateEntree.getTime());
			}
		}
		
		
		return jCalendarEntryDate;
	}
	
	private JDateChooser getJCalendarLeavingDate() {
		if (jCalendarLeavingDate == null) {			
			jCalendarLeavingDate = new JDateChooser(); // Calendar
			jCalendarLeavingDate.setPreferredSize(new Dimension(96, 25));
			jCalendarLeavingDate.setLocale(new Locale(Param.string("LANGUAGE")));
			jCalendarLeavingDate.setDateFormatString("dd/MM/yy HH:mm"); //$NON-NLS-1$
			
			jCalendarLeavingDate.addPropertyChangeListener("date", new PropertyChangeListener() { //$NON-NLS-1$			
				public void propertyChange(PropertyChangeEvent evt) {
					jCalendarLeavingDate.setDate((Date) evt.getNewValue());
					dateLeaving.setTime((Date) evt.getNewValue());						
				}
			});
			
		}
		
		if(!insert) {
			if(deathSelected.getDateSortieProvisoire() != null) {
			jCalendarLeavingDate.setDate(deathSelected.getDateSortieProvisoire().getTime());	
			
			Date sDateSortie = deathSelected.getDateSortieProvisoire().getTime();

			if (sDateSortie != null) {
				dateLeaving = (GregorianCalendar) Calendar.getInstance();
				dateLeaving.setTimeInMillis(sDateSortie.getTime());
			}
		}
	}
		
		return jCalendarLeavingDate;
	}
	private JButton getJButtonPickPatient() {
		if (btnChoosePatient == null) {
			btnChoosePatient = new JButton();		
			btnChoosePatient.setMnemonic(KeyEvent.VK_P);
			btnChoosePatient.setIcon(new ImageIcon("rsc/icons/pick_patient_button.png")); //$NON-NLS-1

			btnChoosePatient.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					SelectPatient sp = new SelectPatient(MortuaryEdit.this, patientSelected);
					sp.addSelectionListener((SelectionListener) MortuaryEdit.this);					
					sp.pack();
					sp.setVisible(true);
				}
			});

		}
		if(!insert) {
			btnChoosePatient.setEnabled(false);
		}
		return btnChoosePatient;
	}
	
		public void patientSelected(Patient patient) {
			patientSelected = patient;
			patientChoosed.setText(patientSelected!=null?patientSelected.getName()+" ":"");
		}
		public Patient getPatientParent() {
			return patientSelected;
		}
		
		private JButton getBtnSave() {
			if (btnSave == null) {
				btnSave = new JButton(MessageBundle.getMessage("angal.mortuaryedit.save"));
				btnSave.setMnemonic(KeyEvent.VK_O);
				btnSave.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						manager = new MortuaryBrowserManager();
						if(patientChoosed.getText().equalsIgnoreCase("")){
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.mortuaryedit.error.patient"));
							return ;
						}
						
						if(comboBoxOrigin.getSelectedItem().toString().equalsIgnoreCase("")){
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.mortuaryedit.error.origin"));
							return ;
						}
						if(jCalendarDeathDate.getDate() == null){
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.mortuaryedit.error.deathDate"));
							return ;
						}
						if(jCalendarEntryDate.getDate() == null){
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.mortuaryedit.error.entryDate"));
							return ;
						}
						if(jCalendarLeavingDate.getDate() == null){
							dateLeaving = null;
							
							if(chckbxLeaving.isSelected()) {
								JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.mortuaryedit.providedateleaving"), MessageBundle.getMessage("angal.therapy.warning"), JOptionPane.ERROR_MESSAGE);
								return ;
							}
							
						}
						if(deathPlace.getText().equalsIgnoreCase("")){
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.mortuaryedit.error.deathplace"));
							return ;
						}

						if(comboMotif.getSelectedItem().toString().equalsIgnoreCase("")){
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.mortuaryedit.error.deathreason"));
							return ;
						}
						if(nameDeclarant.getText().equalsIgnoreCase("")){
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.mortuaryedit.error.declarername"));
							return ;
						}
						if( dateLeaving != null) {
							if(dateLeaving.before(TimeTools.getServerDateTime().getTime())){
								JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.mortuaryedit.error.baddeathleaving"));
								return ;
							}
						}
						if(dateDeath.after(TimeTools.getServerDateTime().getTime())){
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.mortuaryedit.error.baddeathdate"));
							return ;
						}
						if(dateEntry.before(dateDeath)){
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.mortuaryedit.error.badentry"));
							return ;
						}
						if( dateLeaving != null) {
							if(dateLeaving.before(dateEntry)){
								JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.mortuaryedit.error.badentry1"));
								return ;
							}
						}
						
						if(dateEntry.after(TimeTools.getServerDateTime().getTime())){
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.mortuaryedit.error.baddeathentry"));
							return ;
						}
						if(telDeclarant.getText().equalsIgnoreCase("")){
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.mortuaryedit.error.decalarertel"));
							return ;
						}
							death = new Death();
							
							
							death.setId(deathSelected.getId());
							death.setMotif(selectedMotif);
							death.setPatient(patientSelected);
							death.setProvenance(comboBoxOrigin.getSelectedItem().toString());
							death.setDateDeces(dateDeath);
							death.setDateEntree(dateEntry);
							death.setLieu(deathPlace.getText());
							if(chckbxLeaving.isSelected()) {
								death.setDateSortie(dateLeaving);
							}
							death.setDateSortieProvisoire(dateLeaving);
							death.setNidDeclarant(nidDeclarant.getText());
							death.setNomDeclarant(nameDeclarant.getText());
							death.setTelDeclarant(telDeclarant.getText());
							death.setNidFamille(nidFamille.getText());
							death.setNomFamille(nameFamille.getText());
							death.setTelFamille(telFamille.getText());
							death.setCasier(casier.getText());
							
							try {		
								
								boolean result = false;
								if (insert) { // inserting
									if(manager.patientIsDied(patientSelected.getCode())) {
										JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.mortuaryedit.error.patientalreadydied"));
										return ;
									}
									result = manager.newDeath(death);
									if (result) {
										JOptionPane.showMessageDialog(null,
												MessageBundle.getMessage("angal.mortuaryedit.success.insert"));
										fireDeathInserted();
										dispose();
									}
								} else {// updating
								
									if(controlChanges(death)) {
										//JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.mortuaryedit.error.nothingmodified"));
										dispose();
										return;
									}
									result = manager.updateDeath(death);
									
									if (result) {
										
										JOptionPane.showMessageDialog(null,
												MessageBundle.getMessage("angal.mortuaryedit.success.update"));
										fireDeathUpdated();
										dispose();
									}else JOptionPane.showMessageDialog(null,
											MessageBundle.getMessage("angal.mortuaryedit.error.updated.failed"));
								}
								if (!result)
									JOptionPane.showMessageDialog(null,
											MessageBundle.getMessage("angal.mortuaryedit.error.failed"));
									//dispose();
							} catch (OHException ex) {
								JOptionPane.showMessageDialog(null,
										ex.getMessage());
						}
					}
				});
			}
			return btnSave;
		}
	
		/**
		 * This method initializes jCloseButton	
		 * 	
		 * @return javax.swing.JButton	
		 */
		private JButton getJbtnQuit() {
			if (btnQuit == null) {
				btnQuit = new JButton();
				btnQuit.setText(MessageBundle.getMessage("angal.common.close"));
				btnQuit.setMnemonic(KeyEvent.VK_C);
				btnQuit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
			}
			return btnQuit;
		}

	private boolean controlChanges(Death d) {
		 SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yy HH:mm");
		 String decesold = fmt.format(oldDateDeces.getTime());		 
		 String deces = fmt.format(d.getDateDeces().getTime());		 
		 String entreeold = fmt.format(oldDateEntree.getTime());		 
		 String entree = fmt.format(d.getDateEntree().getTime());	
		 String sortieold = "";
		 String sortie = "";
		 if(oldDateSortieProvisoire != null) {
			 sortieold = fmt.format(oldDateSortieProvisoire.getTime());		 
		 }	
		 if(d.getDateSortieProvisoire() != null) {	 
		 	 sortie = fmt.format(d.getDateSortieProvisoire().getTime());
		}
		 return (
				decesold.equals(deces) &&
				entreeold.equals(entree) &&
				sortieold.equals(sortie) &&				 
				oldNomDeclarant.equalsIgnoreCase(nameDeclarant.getText())&&
				oldTelDeclarant.equalsIgnoreCase(telDeclarant.getText()) &&
				oldNidDeclarant.equalsIgnoreCase(nidDeclarant.getText()) &&
				oldNomFamille.equalsIgnoreCase(nameFamille.getText()) &&
				oldTelFamille.equalsIgnoreCase(telFamille.getText()) &&
				oldCasier.equalsIgnoreCase(casier.getText())&&
				oldIdMotif == d.getMotif().getId()&&
				oldProvenance.equalsIgnoreCase(comboBoxOrigin.getSelectedItem().toString()) &&
				oldNidFamille.equalsIgnoreCase(nidFamille.getText())&&
				oldBodyLift == chckbxLeaving.isSelected()
				);				
	}
}
