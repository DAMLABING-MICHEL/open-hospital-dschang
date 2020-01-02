package org.isf.mortuary.gui;

import org.isf.generaldata.MessageBundle;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medicalstockward.model.MedicalWard;

import org.isf.menu.gui.MainMenu;
import org.isf.mortuary.gui.MortuaryEdit.MortuaryListener;
import org.isf.mortuary.manager.DeathReasonBrowserManager;
import org.isf.mortuary.manager.MortuaryBrowserManager;
import org.isf.mortuary.model.Death;
import org.isf.mortuary.model.DeathReason;
import org.isf.parameters.manager.Param;
import org.isf.patient.gui.SelectPatient;
import org.isf.patient.gui.SelectPatient.SelectionListener;
import org.isf.patient.model.Patient;
import org.isf.pricesothers.gui.PricesOthersBrowser;
import org.isf.stat.manager.GenericReportFromDateToDate;
import org.isf.utils.exception.OHException;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JPanel;
import javax.swing.JButton;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.toedter.calendar.JDateChooser;

import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.UIManager;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;


public class MortuaryBrowser extends ModalJFrame implements MortuaryListener, SelectionListener{
	private static Logger logger = LoggerFactory.getLogger(MortuaryBrowser.class);
	private JLabel labelTotal;
	private JLabel labelTotalFilters;
	private JTextField selectedPatient;
	private JTable 	tableDeces;
	private JButton btnDeleteDeces;
	private JButton btnNewDeces;
	private JButton btnEditDeces;
	private JButton btnCertificatDeces;
	private JButton btnReport; 
	private JButton btnClose;
	private JPanel panelPatient;
	private JPanel panelProvenance;
	private JPanel panelDates;
	private JRadioButton rdbtnEntree;
	private JRadioButton rdbtnSortie;
	private JLabel lblFrom;
	private JDateChooser jCalendardateFrom;
	private JLabel lblTo;
	private JDateChooser jCalendardateTo;
	private JComboBox comboBoxPavillon;
	private JComboBox comboBoxAutre;
	private JButton btnChoosePatient;
	private JPanel panelMotif;
	private JButton btnApply;
	private JComboBox comboBoxMotif;
	private JButton btnRemovePatient;
	private JTextField findDeath;
	private MortuaryBrowserModel model;
	private int selectedrow;
	private Death death;
	private JPanel panelStatistics;
	private MortuaryBrowserManager manager = new MortuaryBrowserManager();
	private ArrayList<Death> deathList;
	private JPanel panelFilters;
	private JPanel panelData;
	private JPanel panelButtons;
	private GroupLayout mainLayout;
	private DateFormat dateFormat;
	private Patient patientSelected;
	private ArrayList<MedicalWard> medWardList = new ArrayList<MedicalWard>();	
	private ArrayList<DeathReason> listMotifs = new ArrayList<DeathReason>();
	private DeathReason selectedMotif; 
	private GregorianCalendar dateTo = TimeTools.getServerDateTime();
	private GregorianCalendar dateFrom = TimeTools.getServerDateTime();
	private  ButtonGroup btnSexGrp;
	private  ButtonGroup btnIoGrp;
	String searchQuery = "";
	private ArrayList<Death>deathSearch;
	JButton next = new JButton(">");
	JButton previous = new JButton("<");
	JComboBox pagesCombo = new JComboBox();
    JLabel under = new JLabel("/ 0 Page");
	private static int PAGE_SIZE = 50;
	private int START_INDEX = 0;
	private int TOTAL_ROWS;
	private int TOTAL_ROWS_ALL;
	private String motif = "";
	private JButton btnEtatSjrs; 
	private final int BUNDLE = 0;
	private final int FILENAME = 1;
	private JComboBox jRptComboBox = null;
	private JPanel jMonthPanel = null;
	private JLabel jRptLabel = null;
	private String[][] reportMatrix = {
			{"angal.stat.corpsesliftedperperiod", 			"CorpsesLiftedPerPeriodOfTime"},
			{"angal.stat.corpsestobeliftedperperiod", 		"CorpsesToBeLiftedPerPeriodOfTime"}
		};

	private String[] columsNames = {MessageBundle.getMessage("angal.mortuarybrower.id"),
			MessageBundle.getMessage("angal.mortuarybrowser.patient"), 
			MessageBundle.getMessage("angal.mortuarybrower.sex"),
			MessageBundle.getMessage("angal.mortuarybrower.declarer"), 
			MessageBundle.getMessage("angal.mortuarybrower.origin"), 
			MessageBundle.getMessage("angal.mortuaryedit.entrydate"), 
			MessageBundle.getMessage("angal.mortuaryedit.leavingdate"),
			MessageBundle.getMessage("angal.mortuarybrowser.motif")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
	private int[] columsWidth = {50, 150, 35, 150, 150, 100, 100, 150};
	
	public JTable getTableDeces() {
		if (tableDeces == null) {
			model = new MortuaryBrowserModel();
			tableDeces = new JTable(model);
			tableDeces.setBorder(new LineBorder(new Color(1, 1, 1)));
		}
		for (int i=0;i<columsWidth.length; i++){
			tableDeces.getColumnModel().getColumn(i).setMinWidth(columsWidth[i]);			
			tableDeces.getColumnModel().getColumn(i).setCellRenderer(new StringCenterTableCellRenderer());
		}	
		return tableDeces;
	}

	private void initialize() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screensize = kit.getScreenSize();
		final int pfrmBase = 10;
        final int pfrmWidth = 6;
        final int pfrmHeight = 5;
        this.setBounds((screensize.width - screensize.width * pfrmWidth / pfrmBase ) / 2, (screensize.height - screensize.height * pfrmHeight / pfrmBase)/2, 
                screensize.width * pfrmWidth / pfrmBase, screensize.height * pfrmHeight / pfrmBase);
        
        
		this.setTitle(MessageBundle.getMessage("angal.mortuarybrowser.title"));
		
		//this.setContentPane(getJContainPanel());
		//pack();	
	}
	public MortuaryBrowser(){
		initialize(); 
		setMinimumSize(new Dimension(1100,650));
		 frame.setLocationRelativeTo(null);  
		 MortuaryBrowserManager manager0 = new MortuaryBrowserManager();
		 TOTAL_ROWS_ALL = manager0.getMortuaryTotalRows();
		 TOTAL_ROWS = TOTAL_ROWS_ALL;
		 initialiseCombo(pagesCombo, TOTAL_ROWS);
		 model = new MortuaryBrowserModel(START_INDEX, PAGE_SIZE);
		 tableDeces = new JTable(model);
		 previous.setEnabled(false);
		 if(PAGE_SIZE > TOTAL_ROWS) next.setEnabled(false);
		 tableDeces.setAutoCreateRowSorter(true);
		 
		 JPanel navigation = new JPanel(new FlowLayout(FlowLayout.CENTER));
	        next.addActionListener( new ActionListener(){
	            public void actionPerformed(ActionEvent ae) {
	            	if(!previous.isEnabled()) previous.setEnabled(true);
	            	
	            	int motif = selectedMotif !=null ? selectedMotif.getId() : 0;
					String pavillon = comboBoxPavillon.getSelectedItem().toString();
					int pat = patientSelected== null? 0 : patientSelected.getCode();
					boolean isEntree = rdbtnEntree.isSelected();
					boolean isSortie = rdbtnSortie.isSelected();
					
	            	START_INDEX += PAGE_SIZE;
	            	model = new MortuaryBrowserModel(motif, pavillon,
	    					pat, dateFrom, dateTo, isEntree, isSortie, START_INDEX, PAGE_SIZE);
					model.fireTableDataChanged();
	    			if((START_INDEX + PAGE_SIZE) > TOTAL_ROWS){
	            		next.setEnabled(false); 
	    			}
	    			pagesCombo.setSelectedItem(START_INDEX/PAGE_SIZE + 1);
	    			tableDeces.updateUI();
	            }
	        });
	        
	        previous.addActionListener( new ActionListener(){
	            public void actionPerformed(ActionEvent ae) {
	            	if(!next.isEnabled()) next.setEnabled(true);
	            	int motif = selectedMotif !=null ? selectedMotif.getId() : 0;
					String pavillon = comboBoxPavillon.getSelectedItem().toString();
					int pat = patientSelected== null? 0 : patientSelected.getCode();
					boolean isEntree = rdbtnEntree.isSelected();
					boolean isSortie = rdbtnSortie.isSelected();
					
	        		START_INDEX -= PAGE_SIZE;
	        		model = new MortuaryBrowserModel(motif, pavillon,
	    					pat, dateFrom, dateTo, isEntree, isSortie, START_INDEX, PAGE_SIZE);
					model.fireTableDataChanged();
	    			if(START_INDEX < PAGE_SIZE)	previous.setEnabled(false); 
	    			pagesCombo.setSelectedItem(START_INDEX/PAGE_SIZE + 1);
	    			tableDeces.updateUI();
	            }
	        });
	        pagesCombo.setEditable(true);
			pagesCombo.addActionListener(new ActionListener() {
			 	public void actionPerformed(ActionEvent arg0) {
			 		if(!next.isEnabled()) next.setEnabled(true);
	            	int motif = selectedMotif !=null ? selectedMotif.getId() : 0;
					String pavillon = comboBoxPavillon.getSelectedItem().toString();
					int pat = patientSelected== null? 0 : patientSelected.getCode();
					boolean isEntree = rdbtnEntree.isSelected();
					boolean isSortie = rdbtnSortie.isSelected();
			 		if(pagesCombo.getItemCount() != 0){
			 			int page_number = (Integer) pagesCombo.getSelectedItem();	
				 		START_INDEX = (page_number-1) * PAGE_SIZE;
				 		model = new MortuaryBrowserModel(motif, pavillon,
		    					pat, dateFrom, dateTo, isEntree, isSortie, START_INDEX, PAGE_SIZE);
		        		
						model.fireTableDataChanged();
		    			if((START_INDEX + PAGE_SIZE) > TOTAL_ROWS){
		            		next.setEnabled(false); 
		    			}else{
		    				next.setEnabled(true);
		    			}
		    			if(page_number == 1){
		            		previous.setEnabled(false); 
		    			}else{
		    				previous.setEnabled(true); 
		    			}
		    			pagesCombo.setSelectedItem(START_INDEX/PAGE_SIZE + 1);
			 		}
			 		
			 		tableDeces.updateUI();
			 	}
			 }); 
	        previous.setPreferredSize(new Dimension(40, 25));
	        next.setPreferredSize(new Dimension(40, 25));
	        pagesCombo.setPreferredSize(new Dimension(60, 25));
	        under.setPreferredSize(new Dimension(55, 25));
	        
	        navigation.add(previous); 
	        navigation.add(pagesCombo);
	        navigation.add(under);
	        navigation.add(next);
	        
		 
		 panelFilters = new JPanel();
		
		 panelData = new JPanel();		
		 panelButtons = new JPanel();
		
		JPanel panelFind = new JPanel();
		ImageIcon imageIcon = new ImageIcon("rsc/icons/zoom_r_button.png");
		JLabel label = new JLabel(imageIcon);
		panelFind.setLayout(new FlowLayout());
		panelFind.add(getFindDeath());
		panelFind.add(label);

			
			
		 
		 panelData.setLayout(new BorderLayout());
		 panelData.add(panelFind, java.awt.BorderLayout.NORTH);
		 panelData.add(new JScrollPane(getTableDeces()), java.awt.BorderLayout.CENTER);
	
		
		btnNewDeces = new JButton(MessageBundle.getMessage("angal.mortuarybrowser.new"));
		btnNewDeces.setPreferredSize(new Dimension(100, 25));
		btnNewDeces.setMnemonic(KeyEvent.VK_N);
		btnNewDeces.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				death = new Death();
				MortuaryEdit newrecord = new MortuaryEdit(MortuaryBrowser.this, death, true);
				newrecord.addDeathListener(MortuaryBrowser.this);
				newrecord.setVisible(true);
			}
		});
		
		FlowLayout fl_panelButtons = new FlowLayout(FlowLayout.CENTER, 15, 5);
		panelButtons.setLayout(fl_panelButtons);
		//panelButtons.add(getPanelStatistics());
		panelButtons.add(navigation);
		panelButtons.add(btnNewDeces);
		panelButtons.add(getJEditButton());
		panelButtons.add(getJDeteleButton());
		panelButtons.add(getBtnCertificatDeces());
		panelButtons.add(getJbtnEtatSjrs());
		panelButtons.add(getBtnReport());
		panelButtons.add(getJbtnClose());
		panelPatient = new JPanel();
		panelPatient.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), MessageBundle.getMessage("angal.mortuarybrowser.patient"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		panelProvenance = new JPanel();
		panelProvenance.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), MessageBundle.getMessage("angal.mortuarybrowser.origin"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		panelDates = new JPanel();
		panelDates.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), MessageBundle.getMessage("angal.mortuarybrowser.io"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		btnIoGrp = new ButtonGroup();
		rdbtnEntree = new JRadioButton(MessageBundle.getMessage("angal.mortuarybrowser.enter"));
		
		rdbtnSortie = new JRadioButton(MessageBundle.getMessage("angal.mortuarybrowser.leaving"));
		
		btnIoGrp.add(rdbtnEntree);
		rdbtnEntree.setSelected(true);
		
		btnIoGrp.add(rdbtnSortie);
		
		lblFrom = new JLabel(MessageBundle.getMessage("angal.mortuarybrowser.from"));			
		lblTo = new JLabel(MessageBundle.getMessage("angal.mortuarybrowser.to"));
		GroupLayout gl_panelDates = new GroupLayout(panelDates);
		gl_panelDates.setHorizontalGroup(
			gl_panelDates.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelDates.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelDates.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelDates.createSequentialGroup()
							.addComponent(rdbtnEntree, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
							.addGap(10)
							.addComponent(rdbtnSortie, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panelDates.createSequentialGroup()
							.addGroup(gl_panelDates.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lblTo, GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
								.addComponent(lblFrom, GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE))
							.addGap(10)
							.addGroup(gl_panelDates.createParallelGroup(Alignment.TRAILING)
								.addComponent(getJCalendardateTo(), GroupLayout.PREFERRED_SIZE, 175, GroupLayout.PREFERRED_SIZE)
								.addComponent(getJCalendardateFrom(), GroupLayout.PREFERRED_SIZE, 175, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap(10, Short.MAX_VALUE))
		);
		gl_panelDates.setVerticalGroup(
			gl_panelDates.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelDates.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelDates.createParallelGroup(Alignment.BASELINE)
						.addComponent(rdbtnEntree, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addComponent(rdbtnSortie, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addGap(15)
					.addGroup(gl_panelDates.createParallelGroup(Alignment.TRAILING)
						.addComponent(getJCalendardateFrom(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblFrom, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addGap(15)
					.addGroup(gl_panelDates.createParallelGroup(Alignment.LEADING)
						.addComponent(getJCalendardateTo(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblTo, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		panelDates.setLayout(gl_panelDates);
		
		//comboBoxPavillon = new JComboBox();
		
		comboBoxAutre = new JComboBox();
		GroupLayout gl_panelProvenance = new GroupLayout(panelProvenance);
		gl_panelProvenance.setHorizontalGroup(
			gl_panelProvenance.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelProvenance.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelProvenance.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(getComboBoxOrigin(), Alignment.LEADING, 0, 210, Short.MAX_VALUE))
					.addContainerGap(17, Short.MAX_VALUE))
		);
		gl_panelProvenance.setVerticalGroup(
			gl_panelProvenance.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelProvenance.createSequentialGroup()
					.addContainerGap()
					.addComponent(getComboBoxOrigin(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(10, Short.MAX_VALUE))
		);
		panelProvenance.setLayout(gl_panelProvenance);
		
		
		selectedPatient = new JTextField();
		selectedPatient.setColumns(10);
		
		btnRemovePatient = new JButton();
		btnRemovePatient.setIcon(new ImageIcon("rsc/icons/remove_patient_button.png"));
		
		btnRemovePatient.addMouseListener(new MouseAdapter() {
	        @Override
	        public void mouseClicked(MouseEvent e) {
		        	patientSelected = null;		    
	
		        	selectedPatient.setText("");
					//deathInserted(null);
				}
			});
		
		GroupLayout gl_panelPatient = new GroupLayout(panelPatient);
		gl_panelPatient.setHorizontalGroup(
			gl_panelPatient.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_panelPatient.createSequentialGroup()
					.addContainerGap()
					.addComponent(selectedPatient, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(getJButtonPickPatient(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(btnRemovePatient, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
		);
		gl_panelPatient.setVerticalGroup(
			gl_panelPatient.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panelPatient.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelPatient.createParallelGroup(Alignment.LEADING, false)
						.addComponent(btnRemovePatient, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
						.addComponent(selectedPatient, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
						.addComponent(getJButtonPickPatient(), Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE))
					.addGap(38))
		);
		panelPatient.setLayout(gl_panelPatient);
		
		panelMotif = new JPanel();
		panelMotif.setBorder(new TitledBorder(null, MessageBundle.getMessage("angal.mortuarybrowser.motif"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
	
		
		GroupLayout gl_panelFilters = new GroupLayout(panelFilters);
		gl_panelFilters.setHorizontalGroup(
			gl_panelFilters.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelFilters.createSequentialGroup()
					.addGroup(gl_panelFilters.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelFilters.createSequentialGroup()
							.addGap(12)
							.addGroup(gl_panelFilters.createParallelGroup(Alignment.LEADING, false)
								.addComponent(panelPatient, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(panelProvenance, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
								.addComponent(panelDates, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
						.addGroup(gl_panelFilters.createSequentialGroup()
							.addContainerGap()
							.addComponent(panelMotif, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
						.addGroup(gl_panelFilters.createSequentialGroup()
							.addGap(12)
							.addComponent(getApplyButton(), GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panelFilters.createSequentialGroup()
							.addGap(12)
							.addComponent(getPanelStatistics(), GroupLayout.PREFERRED_SIZE, 128, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_panelFilters.setVerticalGroup(
			gl_panelFilters.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelFilters.createSequentialGroup()
					.addContainerGap()
					.addComponent(panelPatient, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
					.addGap(15)
					.addComponent(panelProvenance, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panelDates, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
					.addGap(15)
					.addComponent(panelMotif, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
					.addGap(15)
					.addComponent(getApplyButton(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
					.addGap(15)
					.addComponent(getPanelStatistics(), GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		
		GroupLayout gl_panelMotif = new GroupLayout(panelMotif);
		gl_panelMotif.setHorizontalGroup(
			gl_panelMotif.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelMotif.createSequentialGroup()
					.addContainerGap()
					.addComponent(getComboMotif(), 0, 210, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panelMotif.setVerticalGroup(
			gl_panelMotif.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelMotif.createSequentialGroup()
					.addContainerGap()
					.addComponent(getComboMotif(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panelMotif.setLayout(gl_panelMotif);
		panelFilters.setLayout(gl_panelFilters);
		getContentPane().setLayout(new BorderLayout());
		
		getContentPane().add(panelData, BorderLayout.CENTER);
		getContentPane().add(panelFilters, BorderLayout.WEST);
		getContentPane().add(panelButtons, BorderLayout.SOUTH);
		
		setVisible(true);
	}
	
public class MortuaryBrowserModel extends DefaultTableModel {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MortuaryBrowserManager manager = new MortuaryBrowserManager();
	
		public MortuaryBrowserModel() {	
				try {
					deathList = manager.getDeaths();
					deathSearch = deathList;
				} catch (OHException e) {
			}			
		}
		public MortuaryBrowserModel(int motif, String pavillon,
				int pat, GregorianCalendar dateFrom,
				GregorianCalendar dateTo, boolean isEntree, boolean isSortie, int start_index, int page_size) {
			deathList = manager.getDeaths(motif, pavillon,
					pat, dateFrom, dateTo, isEntree, isSortie, start_index, page_size);
			deathSearch = deathList;

		}
		public MortuaryBrowserModel(int start_index, int page_size) {			
			MortuaryBrowserManager manager = new MortuaryBrowserManager();
			deathList = manager.getDeaths(start_index, page_size);
			deathSearch = deathList;
		}		
		public int getRowCount() {
			if (deathSearch == null)
				return 0;
			return deathSearch.size();
		}		
		public String getColumnName(int c) {
			return columsNames[c];
		}
		public int getColumnCount() {
			return columsNames.length;
		}		
		//{ "ID", "PATIENT", "SEXE","DECLARANT","PROVENANCE","DATE ENTREE", "DATE SORTIE", "MOTIF"};
		public Object getValueAt(int r, int c) {
			dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			if (c == -1) {
				return deathSearch.get(r);
			}else if (c == 0) {
				return deathSearch.get(r).getId();
			}  else if (c == 1) {
				return deathSearch.get(r).getPatientName();
			} else if (c == 2) {								
				return deathSearch.get(r).getPatientSex();
			} else if (c == 3) {
				return deathSearch.get(r).getNomDeclarant();
			} else if (c == 4) {
				return deathSearch.get(r).getProvenance();
			} else if (c == 5) {
					java.util.Date dateEntree = deathSearch.get(r).getDateEntree().getTime();				 
					String retour1 = dateFormat.format(dateEntree);		
				return retour1;				
			} else if (c == 6) {
					String retour = "";
				if (deathSearch.get(r).getDateSortieProvisoire() != null) {
					java.util.Date dateSortie = deathSearch.get(r).getDateSortieProvisoire().getTime();
					retour = dateFormat.format(dateSortie);
				}
							
				return retour;
			}
			else if (c == 7) {	
				if(deathSearch.get(r).getMotif() !=null)
				return deathSearch.get(r).getMotif().getDescription();
			}
			return null;
		}
		
		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			//return super.isCellEditable(arg0, arg1);
			return false;
		}
		
		
	}

	/**
	 * This method initializes jEditButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJEditButton() {		
		if (btnEditDeces == null) {
			btnEditDeces = new JButton();
			btnEditDeces = new JButton(MessageBundle.getMessage("angal.mortuarybrowser.edit"));
			btnEditDeces.setPreferredSize(new Dimension(85, 25));
			btnEditDeces.setMnemonic(KeyEvent.VK_E);
			btnEditDeces.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (tableDeces.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.common.pleaseselectarow"),
								MessageBundle.getMessage("angal.hospital"), JOptionPane.PLAIN_MESSAGE);
						return;
					} else {
						selectedrow = tableDeces.getSelectedRow();
						death = (Death) (((MortuaryBrowserModel) model).getValueAt(tableDeces.getSelectedRow(), -1));
						MortuaryEdit editrecord = new MortuaryEdit(MortuaryBrowser.this, death, false);
						editrecord.addDeathListener(MortuaryBrowser.this);
						editrecord.setVisible(true);

					}
				}
			});
		}
		return btnEditDeces;
	}
	
private JButton getJbtnEtatSjrs() {
	if (btnEtatSjrs == null) {
		btnEtatSjrs = new JButton(MessageBundle.getMessage("angal.newbill.mortuaryFees"));
		btnEtatSjrs.setPreferredSize(new Dimension(110, 25));
		btnEtatSjrs.setMnemonic(KeyEvent.VK_R);
		btnEtatSjrs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				if (tableDeces.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.common.pleaseselectarow"),
							MessageBundle.getMessage("angal.hospital"), JOptionPane.PLAIN_MESSAGE);
					return;
				} 
					int rowSelected = tableDeces.getSelectedRow();
					Death death = (Death)tableDeces.getValueAt(rowSelected, -1);
					manager.generateEtatSejours(death, "rapportEtatSejoursMorgue", true, true);
			}
		});
	}
	return btnEtatSjrs;
	}
	
	
	/**
	 * This method initializes jDeteleButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJDeteleButton() {
		if (btnDeleteDeces == null) {
			btnDeleteDeces = new JButton();
			btnDeleteDeces.setPreferredSize(new Dimension(80, 25));
			btnDeleteDeces.setText(MessageBundle.getMessage("angal.common.delete"));
			btnDeleteDeces.setMnemonic(KeyEvent.VK_D);
			btnDeleteDeces.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (tableDeces.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.common.pleaseselectarow"),
								MessageBundle.getMessage("angal.hospital"), JOptionPane.PLAIN_MESSAGE);
						return;
					} else {
						MortuaryBrowserManager manager = new MortuaryBrowserManager();
						Death d = (Death) (((MortuaryBrowserModel) model).getValueAt(tableDeces.getSelectedRow(), -1));
						int n = JOptionPane
								.showConfirmDialog(null,
										MessageBundle.getMessage("angal.mortuarybrowser.delete") + " \""
												+ d.getPatientName() + "\" ?",
										MessageBundle.getMessage("angal.hospital"), JOptionPane.YES_NO_OPTION);
						
						if ((n == JOptionPane.YES_OPTION) && (manager.deleteDeath(d.getId()))) {
							deathList.remove(tableDeces.getSelectedRow());
							model.fireTableDataChanged();
							tableDeces.updateUI();
						}
					}
				}
				
			});
		}
		return btnDeleteDeces;
	}
	
	
	/**
	 * This method initializes jCloseButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJbtnClose() {
		if (btnClose == null) {
			btnClose = new JButton();
			btnClose.setPreferredSize(new Dimension(75, 25));
			btnClose.setText(MessageBundle.getMessage("angal.common.close"));
			btnClose.setMnemonic(KeyEvent.VK_C);
			btnClose.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dispose();
				}
			});
		}
		return btnClose;
	}
	private JLabel getLabelTotal() {
		if( labelTotal == null) {
			labelTotal = new JLabel(MessageBundle.getMessage("angal.mortuarybrowser.total")+" "+TOTAL_ROWS_ALL);
		}
		return labelTotal;
	}

	private JButton getJButtonPickPatient() {
		if (btnChoosePatient == null) {
			btnChoosePatient = new JButton();
			btnChoosePatient.setMnemonic(KeyEvent.VK_P);
			btnChoosePatient.setIcon(new ImageIcon("rsc/icons/pick_patient_button.png")); //$NON-NLS-1$

			btnChoosePatient.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					SelectPatient sp = new SelectPatient(MortuaryBrowser.this, patientSelected);
					sp.addSelectionListener((SelectionListener) MortuaryBrowser.this);					
					sp.pack();
					sp.setVisible(true);
				}
			});

		}
		return btnChoosePatient;
	}
	
		public void patientSelected(Patient patient) {
			patientSelected = patient;
			selectedPatient.setText(patientSelected!=null?patientSelected.getName()+" ":"");
		}
		public Patient getPatientParent() {
			return patientSelected;
		}
		
		/**
		 * This method initializes applyButton, which is the button that perform
		 * the filtering and calls the methods to refresh the Table
		 * 
		 * @return applyButton (JButton)
		 */
		private JButton getApplyButton() {
			if (btnApply == null) {
				btnApply = new JButton(
						MessageBundle.getMessage("angal.patvac.search"));
				btnApply.setMnemonic(KeyEvent.VK_S);
				btnApply.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
						dateFrom.setTime(jCalendardateFrom.getDate());
						dateTo.setTime(jCalendardateTo.getDate());
						}catch(NullPointerException ex) {
							dateFrom = null;
							dateTo = null;
						}
						int motif = selectedMotif !=null ? selectedMotif.getId() : 0;
						String pavillon = comboBoxPavillon.getSelectedItem().toString();
						int pat = patientSelected== null? 0 : patientSelected.getCode();
						boolean isEntree = rdbtnEntree.isSelected();
						boolean isSortie = rdbtnSortie.isSelected();
						
						/*
						char sex;
						if (sexSelect.equals(MessageBundle
								.getMessage("angal.patvac.female"))) {
							sex = 'F';
						} else {
							if (sexSelect.equals(MessageBundle
									.getMessage("angal.patvac.male"))) {
								sex = 'M';
							} else {
								sex = 'A';
							}
						}
						*/
						
						MortuaryBrowserManager manager0 = new MortuaryBrowserManager();
						TOTAL_ROWS = manager0.getMortuaryTotalRows(motif,
									pavillon, pat, dateFrom, dateTo, isEntree, isSortie);
						initialiseCombo(pagesCombo, TOTAL_ROWS);
						model = new MortuaryBrowserModel(motif,
									pavillon, pat, dateFrom, dateTo, isEntree, isSortie, START_INDEX, PAGE_SIZE );
						previous.setEnabled(false);
						if(PAGE_SIZE > TOTAL_ROWS) next.setEnabled(false);
						tableDeces.setAutoCreateRowSorter(true);
						
						panelStatistics.remove(labelTotalFilters);
						labelTotalFilters = new JLabel(MessageBundle.getMessage("angal.mortuarybrowser.totalFilter")+" "+TOTAL_ROWS);
						panelStatistics.add(labelTotalFilters);

					}
				});
			}
			
			return btnApply;
		}
		
		private JComboBox getComboMotif() {
			
			if(comboBoxMotif == null) {
				comboBoxMotif = new JComboBox();
				comboBoxMotif.setPreferredSize(new Dimension(31, 25));
			
			DeathReasonBrowserManager motifDeces = new DeathReasonBrowserManager();
			comboBoxMotif.addItem(MessageBundle.getMessage("angal.mortuary.all")); 
			listMotifs = motifDeces.getDeathReasons();
			for(DeathReason motif: listMotifs) {
				comboBoxMotif.addItem(motif);
			}
			comboBoxMotif.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						if(e.getItem() != MessageBundle.getMessage("angal.mortuary.all")) {
							Object item = e.getItem();
							DeathReason motif = (DeathReason) item;
							selectedMotif = motif;	
						}
						else selectedMotif = null;
					}
				}
			});
			
		 } 
			
			return comboBoxMotif;
		}
		
		private JComboBox getComboBoxOrigin() {
			org.isf.ward.manager.WardBrowserManager wbm = new org.isf.ward.manager.WardBrowserManager();
			if (comboBoxPavillon == null) {
				comboBoxPavillon = new JComboBox();
				comboBoxPavillon.setPreferredSize(new Dimension(31, 25));
				String wardCode = MainMenu.getUserWard();
				comboBoxPavillon.addItem(MessageBundle.getMessage("angal.mortuary.all")); 
				ArrayList<Ward> wardList = wbm.getWards();
				boolean trouve = false;
				for (Ward ward : wardList) {
					if (ward.getCode().equals(wardCode)) {
						comboBoxPavillon.addItem(ward);
						trouve = true;
						MovWardBrowserManager manager = new MovWardBrowserManager();
						medWardList = manager.getMedicalsWard(wardCode);					
						break;
					}
				}
				for (org.isf.ward.model.Ward elem : wardList) {
					comboBoxPavillon.addItem(elem);
				}
				comboBoxPavillon.setEnabled(true);
			 
				comboBoxPavillon.addItem(MessageBundle.getMessage("angal.mortuary.othersource"));
				comboBoxPavillon.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.SELECTED) {
						}
					}
				});
				
			}
		
			
			return comboBoxPavillon;
		}
		
		private JDateChooser getJCalendardateFrom() {
			if (jCalendardateFrom == null) {
				dateFrom = TimeTools.getServerDateTime();
				jCalendardateFrom = new JDateChooser();
				jCalendardateFrom.setPreferredSize(new Dimension(50, 25));
				jCalendardateFrom.setLocale(new Locale(Param.string("LANGUAGE")));
				jCalendardateFrom.setDateFormatString("dd/MM/yy HH:mm"); //$NON-NLS-1$
				
			}
			jCalendardateFrom.addPropertyChangeListener("date", new PropertyChangeListener() { //$NON-NLS-1$
				
				public void propertyChange(PropertyChangeEvent evt) {
					dateFrom = TimeTools.getServerDateTime();
					jCalendardateFrom.setDate((Date) evt.getNewValue());
					dateFrom.setTime((Date) evt.getNewValue());				
					
				}
			});
			
		
			
			if(jCalendardateFrom.getDate() == null) dateFrom = null;
			
			return jCalendardateFrom;
		}
		private JDateChooser getJCalendardateTo() {
			if (jCalendardateTo == null) {
				dateTo = new GregorianCalendar();
				jCalendardateTo = new JDateChooser(); // Calendar
				jCalendardateTo.setPreferredSize(new Dimension(50, 25));
				jCalendardateTo.setLocale(new Locale(Param.string("LANGUAGE")));
				jCalendardateTo.setDateFormatString("dd/MM/yy HH:mm"); //$NON-NLS-1$
				
			}
			jCalendardateTo.addPropertyChangeListener("date", new PropertyChangeListener() { //$NON-NLS-1$
				
				public void propertyChange(PropertyChangeEvent evt) {
					dateTo = new GregorianCalendar();
					jCalendardateTo.setDate((Date) evt.getNewValue());						
					dateTo.setTime((Date) evt.getNewValue());
				}
			});
			if(jCalendardateTo.getDate() == null) dateTo = null;
			
			return jCalendardateTo;
		}
		public JTextField getFindDeath() {
			if(findDeath == null) {
				findDeath = new JTextField();
				findDeath.setToolTipText(MessageBundle.getMessage("angal.mortuaryedit.find"));
				findDeath.setColumns(20);
			}
			findDeath.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void insertUpdate(DocumentEvent e) {
					filterDeath();
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					filterDeath();
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					filterDeath();
				}
				
			});
			
			return findDeath;
		}
		private void filterDeath() {
			//String s = searchTextField.getText() + lastKey;
			String s = findDeath.getText();
			s.trim();
			deathSearch = new ArrayList<Death>();

			for (Death death : deathList) {
				if (!s.equals("")) {
					String name = death.getSearchString();
					if (name.contains(s.toLowerCase()))
						// if (name.indexOf(s.toLowerCase())>-1)
						deathSearch.add(death);
				} else {
					deathSearch.add(death);
				}
			}
			;
			if (tableDeces.getRowCount() == 0) {
				death = null;
			}
			if (tableDeces.getRowCount() == 1) {
				death = (Death) tableDeces.getValueAt(0, -1);
			}
			tableDeces.updateUI();
			findDeath.requestFocus();
		}
		public void initialiseCombo(JComboBox pagesCombo, int total_rows){
			int j = 0;
			pagesCombo.removeAllItems();
			for(int i=0; i< total_rows/PAGE_SIZE; i++){
				j = i+1;
				pagesCombo.addItem(j);
			}
			if(j * PAGE_SIZE < total_rows){
				pagesCombo.addItem(j+1);
				under.setText("/" + (total_rows/PAGE_SIZE + 1 + " Pages"));
			}else{
				under.setText("/" + total_rows/PAGE_SIZE + " Pages");
			}
			
		}
		
		private JButton getBtnCertificatDeces() {
			if (btnCertificatDeces == null) {
				btnCertificatDeces = new JButton(MessageBundle.getMessage("angal.mortuarybrowser.certificate"));
				btnCertificatDeces.setPreferredSize(new Dimension(115, 25));
				btnCertificatDeces.setMnemonic(KeyEvent.VK_R);
				btnCertificatDeces.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						
						if (tableDeces.getSelectedRow() < 0) {
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.common.pleaseselectarow"),
									MessageBundle.getMessage("angal.hospital"), JOptionPane.PLAIN_MESSAGE);
							return;
						} 
							int rowSelected = tableDeces.getSelectedRow();
							Death death = (Death)tableDeces.getValueAt(rowSelected, -1);
							manager.generateDeathCertificate(death.getId(), Param.string("CERTIFICATEOFDEATH"), true, true);
					}
				});
			}
			return btnCertificatDeces;
		}
		
		private JPanel getJParameterSelectionPanel() {

			if (jMonthPanel == null) {

				jMonthPanel = new JPanel(new BorderLayout());				
				//jRptLabel = new JLabel();
				//jRptLabel.setText(MessageBundle.getMessage("angal.stat.report") + "  ");				
				
				jRptComboBox = new JComboBox();
				jRptComboBox.setPreferredSize(new Dimension(220, 25));
				jRptComboBox.addItem("");
				for (int i=0;i<reportMatrix.length;i++) {
					jRptComboBox.addItem(MessageBundle.getMessage(reportMatrix[i][BUNDLE]));	
				}
				
				//jMonthPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
				JPanel panelReport = new JPanel(new FlowLayout());
				
				JButton btnReport = new JButton(MessageBundle.getMessage("angal.mortuarybrowser.reportok"));
				btnReport.setMnemonic(KeyEvent.VK_N);
				btnReport.setPreferredSize(new Dimension(35, 25));
				btnReport.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent event) { 
						dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
						
						String datefrom = "";
						String dateto = "";	
						try {
							int rptIndex = jRptComboBox.getSelectedIndex();
							if(rptIndex != 0) {
								java.util.Date d1 = jCalendardateFrom.getDate();
								datefrom = dateFormat.format(d1);
								java.util.Date d2 = jCalendardateTo.getDate();				 
								dateto = dateFormat.format(d2);
								new GenericReportFromDateToDate(datefrom, dateto, reportMatrix[rptIndex -1][FILENAME], false);
							 }
							}catch(NullPointerException e1) {
							JOptionPane.showMessageDialog(				
									null,
									MessageBundle.getMessage("angal.pricesothers.pleasechooseperiod")); //$NON-NLS-1$
						}											
						}
					});
				panelReport.add(btnReport);
				JPanel pan = new JPanel(new GridBagLayout());
				//pan.add(jRptLabel);
				pan.add(jRptComboBox);
				pan.add(panelReport);
				
				jMonthPanel.add(pan, BorderLayout.CENTER);				
			}
			return jMonthPanel;
		}
		
	private JPanel getPanelStatistics() {
		if(panelStatistics == null) {
			labelTotalFilters = new JLabel();
			panelStatistics = new JPanel(new GridLayout(2, 0, 0, 10));
			panelStatistics.add(labelTotalFilters);
			panelStatistics.add(getLabelTotal());
		}
		return panelStatistics;
	}
		
	private JButton getBtnReport(){
		if(btnReport == null) {
		btnReport = new JButton(MessageBundle.getMessage("angal.mortuarybrowser.report"));
		btnReport.setPreferredSize(new Dimension(75, 25));
		btnReport.setMnemonic(KeyEvent.VK_N);
		btnReport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event){
				JDialog reportDialog = new JDialog(MortuaryBrowser.this);
				reportDialog.setBounds(100,100,300,100);
				reportDialog.setResizable(false);
				reportDialog.setTitle(MessageBundle.getMessage("angal.mortuarybrowser.titlereport"));
				reportDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				reportDialog.setLocationRelativeTo(null);
				reportDialog.setContentPane(getJParameterSelectionPanel());
				reportDialog.setVisible(true);				
			}
		});
		}
		return btnReport;
	}
	
	@Override
	public void mortuaryUpdated(AWTEvent e) {
		tableDeces.setModel(new MortuaryBrowserModel());
		tableDeces.updateUI();
	}
	@Override
	public void mortuaryInserted(AWTEvent e) {
		tableDeces.setModel(new MortuaryBrowserModel());
		tableDeces.updateUI();
	}
	
}

class StringCenterTableCellRenderer extends DefaultTableCellRenderer {  
	   
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	java.util.Date now = TimeTools.getServerDateTime().getTime();			 
	String retour = dateFormat.format(now);


	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
			boolean hasFocus, int row, int column) {  
	   
		Component cell=super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
		cell.setForeground(Color.BLACK);
		if (((String)table.getValueAt(row, 6)).equals(retour)) { //$NON-NLS-1$
			cell.setForeground(Color.RED);
		}
		return cell;
   }
}
