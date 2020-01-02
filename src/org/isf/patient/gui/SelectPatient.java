package org.isf.patient.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.isf.accounting.gui.BillBrowser;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.gui.MainMenu;
import org.isf.parameters.manager.Param;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.utils.jobjects.VoLimitedTextField;

public class SelectPatient extends JDialog
		implements PatientInsert.PatientListener, PatientInsertExtended.PatientListener {

	// LISTENER INTERFACE
	// --------------------------------------------------------
	private EventListenerList selectionListener = new EventListenerList();

	
	public interface SelectionListener extends EventListener {
		public void patientSelected(Patient patient);
	}

	public void addSelectionListener(SelectionListener l) {
		selectionListener.add(SelectionListener.class, l);
	}
	
	/*** custom listeners **/
	List<PatientInsertExtended> patientListeners = new ArrayList<PatientInsertExtended>();
	List<BillBrowser> billBrowserListeners = new ArrayList<BillBrowser>();
	public void addSelectionListener(PatientInsertExtended l) {
		patientListeners.add(l);
	}
	public void addSelectionListener(BillBrowser l) {
		billBrowserListeners.add(l);
	}
	/*** custom **/

	private void fireSelectedPatient(Patient patient) {
		new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = selectionListener.getListeners(SelectionListener.class);
		for (int i = 0; i < listeners.length; i++)
			((SelectionListener) listeners[i]).patientSelected(patient);
		
		/** custom 1 **/
		for (int i = 0; i < patientListeners.size(); i++)
			(patientListeners.get(i)).patientSelected(patient);
		
		/** custom 2 **/
		for (int i = 0; i < billBrowserListeners.size(); i++)
			(billBrowserListeners.get(i)).patientSelected(patient);
	}

	// ---------------------------------------------------------------------------
	private static final long serialVersionUID = 1L;
	private JPanel jPanelButtons;
	private JPanel jPanelTop;
	private JPanel jPanelCenter;
	private JTable jTablePatient;
	private JScrollPane jScrollPaneTablePatient;
	private JButton jButtonCancel;
	private JButton jButtonSelect;
	private JLabel jLabelSearch;
	private JTextField jTextFieldSearchPatient;
	private JButton jSearchButton;
	private JPanel jPanelDataPatient;
	private Patient patient;
	private PatientSummary ps;
	private String[] patColums = { MessageBundle.getMessage("angal.patient.code"),
			MessageBundle.getMessage("angal.patient.name") };
	private int[] patColumsWidth = { 100, 250 };
	private boolean[] patColumsResizable = { false, true };

	PatientBrowserManager patManager = new PatientBrowserManager();
	//ArrayList<Patient> patArray = new ArrayList<Patient>();
	ArrayList<Patient> patSearch = new ArrayList<Patient>();
	private String lastKey = "";

	private JButton buttonNew;
	/**
	 * @wbp.parser.constructor
	 */
	public SelectPatient(JFrame owner, Patient pat) {
		super(owner, true);
		if (!Param.bool("ENHANCEDSEARCH")) {
			//patArray = patManager.getPatientWithHeightAndWeight(null);
			//patSearch = patManager.getPatientWithHeightAndWeight(null);
		}
		if (pat == null) {
			patient = null;
		} else
			patient = pat;
		ps = new PatientSummary(patient);
		initComponents();
		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				// to free memory
				//patArray.clear();
				patSearch.clear();
				dispose();
			}
		});
		setLocationRelativeTo(null);
	}

	public SelectPatient(JDialog owner, Patient pat) {
		
		super(owner, true);
		if (!Param.bool("ENHANCEDSEARCH")) {
			patSearch = patManager.getPatientWithHeightAndWeight(null);
			
		}
		if (pat == null) {
			patient = null;
		} else{
			patient = pat;
		}
		ps = new PatientSummary(patient);
		initComponents();
		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				// to free memory
				//patArray.clear();
				patSearch.clear();
				dispose();
			}
		});
		setLocationRelativeTo(null);
	}

	public SelectPatient(JDialog owner, String search) {
		super(owner, true);
		if (!Param.bool("ENHANCEDSEARCH")) {
			//patArray = patManager.getPatientWithHeightAndWeight(null);
			patSearch = patManager.getPatientWithHeightAndWeight(null);
		}
		ps = new PatientSummary(patient);
		initComponents();
		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				// to free memory
				//patArray.clear();
				patSearch.clear();
				dispose();
			}
		});
		setLocationRelativeTo(null);
		jTextFieldSearchPatient.setText(search);
		if (Param.bool("ENHANCEDSEARCH")) {
			jSearchButton.doClick();
		}
	}
	
	public SelectPatient(JFrame owner, boolean abbleAddPatient) {
		super(owner, true);
		if (!Param.bool("ENHANCEDSEARCH")) {
			//patArray = patManager.getPatientHeadWithHeightAndWeight();
			patSearch = patManager.getPatientHeadWithHeightAndWeight();
		}
		ps = new PatientSummary(patient);
		initComponents();
		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				// to free memory
				//patArray.clear();
				patSearch.clear();
				dispose();
			}
		});
		setLocationRelativeTo(null);
		buttonNew.setVisible(abbleAddPatient);
	}
	public SelectPatient(JFrame owner, boolean abbleAddPatient, boolean full) {
		super(owner, true);
		if (!Param.bool("ENHANCEDSEARCH")) {
			if(!full)
				//patArray = patManager.getPatientHeadWithHeightAndWeight();
				patSearch = patManager.getPatientHeadWithHeightAndWeight();
			else
				//patArray = patManager.getPatient();
				patSearch = patManager.getPatient();
		}
		ps = new PatientSummary(patient);
		initComponents();
		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				// to free memory
				//patArray.clear();
				patSearch.clear();
				dispose();
			}
		});
		setLocationRelativeTo(null);
		buttonNew.setVisible(abbleAddPatient);
	}
	public SelectPatient(JDialog owner, boolean abbleAddPatient) {
		super(owner, true);
		if (!Param.bool("ENHANCEDSEARCH")) {
			//patArray = patManager.getPatientHeadWithHeightAndWeight();
			//MARCO
			patSearch = patManager.getPatientHeadWithHeightAndWeight();
		}
		ps = new PatientSummary(patient);
		initComponents();
		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				// to free memory
				//patArray.clear();
				patSearch.clear();
				dispose();
			}
		});
		setLocationRelativeTo(null);
		buttonNew.setVisible(abbleAddPatient);
	}

	private void initComponents() {
		getContentPane().add(getJPanelTop(), BorderLayout.NORTH);
		getContentPane().add(getJPanelCenter(), BorderLayout.CENTER);
		getContentPane().add(getJPanelButtons(), BorderLayout.SOUTH);
		setTitle(MessageBundle.getMessage("angal.patient.patientselection"));
		pack();
	}

	private JPanel getJPanelDataPatient() {
		if (jPanelDataPatient == null) {
			jPanelDataPatient = ps.getPatientCompleteSummary();
			jPanelDataPatient.setAlignmentY(Box.TOP_ALIGNMENT);
		}
		return jPanelDataPatient;
	}

	private JTextField getJTextFieldSearchPatient() {
		if (jTextFieldSearchPatient == null) {
			jTextFieldSearchPatient = new VoLimitedTextField(100, 20);
			jTextFieldSearchPatient.setText("");
			jTextFieldSearchPatient.selectAll();
			
			if (Param.bool("ENHANCEDSEARCH")) {
				jTextFieldSearchPatient.addKeyListener(new KeyListener() {
					
					public void keyPressed(KeyEvent e) {
						
						int key = e.getKeyCode();
						if (key == KeyEvent.VK_ENTER) {
							
							jSearchButton.doClick();
						}
					}

					public void keyReleased(KeyEvent e) {
					}

					public void keyTyped(KeyEvent e) {
					}
				});
			} else {
				jTextFieldSearchPatient.addKeyListener(new KeyListener() {
					
					public void keyTyped(KeyEvent e) {
						lastKey = "";
						String s = "" + e.getKeyChar();
						if (Character.isLetterOrDigit(e.getKeyChar())) {
							lastKey = s;
						}
						if(jTextFieldSearchPatient.getText().length() > 5){
							filterPatient();
						}
					}

					public void keyPressed(KeyEvent e) {
					    int key = e.getKeyCode();
						if (key == KeyEvent.VK_ENTER) {
							lastKey = "";
							filterPatient();
						}
					}
					public void keyReleased(KeyEvent e) {
					}
				});
			}
		}
		return jTextFieldSearchPatient;
	}

	
	private void filterPatient() {
		
		patSearch = new ArrayList<Patient>();
		patSearch = patManager.getPatientWithHeightAndWeight2(jTextFieldSearchPatient.getText());
		
		if (jTablePatient.getRowCount() == 0) {
			patient = null;
			updatePatientSummary();
		}
		if (jTablePatient.getRowCount() == 1) {

			patient = (Patient) jTablePatient.getValueAt(0, -1);
			updatePatientSummary();
		}
		jTablePatient.updateUI();
		jTextFieldSearchPatient.requestFocus();
	}
	
	private JLabel getJLabelSearch() {
		if (jLabelSearch == null) {
			jLabelSearch = new JLabel();
			jLabelSearch.setText(MessageBundle.getMessage("angal.patient.searchpatient"));
		}
		return jLabelSearch;
	}

	private JButton getJButtonSelect() {
		if (jButtonSelect == null) {
			jButtonSelect = new JButton();
			jButtonSelect.setMnemonic(KeyEvent.VK_S);
			jButtonSelect.setText(MessageBundle.getMessage("angal.patient.select"));
			jButtonSelect.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {

					if (patient != null) {
						// to free memory
						//patArray.clear();
						patSearch.clear();
						fireSelectedPatient(patient);
						dispose();
					} else
						return;
				}
			});
		}
		return jButtonSelect;
	}

	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton();
			jButtonCancel.setMnemonic(KeyEvent.VK_C);
			jButtonCancel.setText(MessageBundle.getMessage("angal.common.cancel"));
			jButtonCancel.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					// to free memory
					//patArray.clear();
					patSearch.clear();
					dispose();
				}
			});
		}
		return jButtonCancel;
	}

	private JScrollPane getJScrollPaneTablePatient() {
		if (jScrollPaneTablePatient == null) {
			jScrollPaneTablePatient = new JScrollPane();
			jScrollPaneTablePatient.setViewportView(getJTablePatient());
			jScrollPaneTablePatient.setAlignmentY(Box.TOP_ALIGNMENT);
		}
		return jScrollPaneTablePatient;
	}

	private JTable getJTablePatient() {
		if (jTablePatient == null) {
			jTablePatient = new JTable();
			jTablePatient.setModel(new SelectPatientModel());
			jTablePatient.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			for (int i = 0; i < patColums.length; i++) {
				jTablePatient.getColumnModel().getColumn(i).setMinWidth(patColumsWidth[i]);
				if (!patColumsResizable[i])
					jTablePatient.getColumnModel().getColumn(i).setMaxWidth(patColumsWidth[i]);
			}
			jTablePatient.setAutoCreateColumnsFromModel(false);
			jTablePatient.getColumnModel().getColumn(0).setCellRenderer(new CenterTableCellRenderer());

			ListSelectionModel listSelectionModel = jTablePatient.getSelectionModel();
			listSelectionModel.addListSelectionListener(new ListSelectionListener() {

				public void valueChanged(ListSelectionEvent e) {
					if (!e.getValueIsAdjusting()) {

						int index = jTablePatient.getSelectedRow();
						patient = (Patient) jTablePatient.getValueAt(index, -1);
						updatePatientSummary();

					}
				}
			});

			jTablePatient.addMouseListener(new MouseListener() {

				public void mouseReleased(MouseEvent e) {
				}

				public void mousePressed(MouseEvent e) {
				}

				public void mouseExited(MouseEvent e) {
				}

				public void mouseEntered(MouseEvent e) {
				}

				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2 && !e.isConsumed()) {
						e.consume();
						jButtonSelect.doClick();
					}
				}
			});

			jTablePatient.addKeyListener(new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e) {
					// TODO Auto-generated method stub
					System.out.println("Jtable key typed");
					int key = e.getKeyCode();
					if (key == KeyEvent.VK_ENTER) {
						jButtonSelect.doClick();
					}
				}

				@Override
				public void keyReleased(KeyEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void keyPressed(KeyEvent e) {
					// TODO Auto-generated method stub

					int key = e.getKeyCode();
					if (key == KeyEvent.VK_ENTER) {
						jButtonSelect.doClick();
					}
					System.out.println("Jtable key pressed");
				}
			});
		}
		return jTablePatient;
	}

	private JButton getButtonNew() {
		//JButton buttonNew = new JButton(MessageBundle.getMessage("angal.admission.newpatient"));
		buttonNew = new JButton(MessageBundle.getMessage("angal.admission.newpatient"));
		buttonNew.setMnemonic(KeyEvent.VK_N);
		buttonNew.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {

				if (Param.bool("PATIENTEXTENDED")) {
					PatientInsertExtended newrecord = new PatientInsertExtended(SelectPatient.this, new Patient(),
							true);
					newrecord.addPatientListener(SelectPatient.this);
					newrecord.setVisible(true);
				} else {
					PatientInsert newrecord = new PatientInsert(SelectPatient.this, new Patient(), true);
					newrecord.addPatientListener(SelectPatient.this);
					newrecord.setVisible(true);
				}

			}
		});
		return buttonNew;
	}

	private void updatePatientSummary() {
		jPanelCenter.remove(jPanelDataPatient);
		ps = new PatientSummary(patient);
		jPanelDataPatient = ps.getPatientCompleteSummary();
		jPanelDataPatient.setAlignmentY(Box.TOP_ALIGNMENT);

		jPanelCenter.add(jPanelDataPatient);
		jPanelCenter.validate();
		jPanelCenter.repaint();
	}

	private JPanel getJPanelCenter() {
		if (jPanelCenter == null) {
			jPanelCenter = new JPanel();
			jPanelCenter.setLayout(new BoxLayout(jPanelCenter, BoxLayout.X_AXIS));
			jPanelCenter.add(getJScrollPaneTablePatient());
			jPanelCenter.add(getJPanelDataPatient());

			if (patient != null) {
				for (int i = 0; i < patSearch.size(); i++) {
					if (patSearch.get(i).getCode().equals(patient.getCode())) {
						jTablePatient.addRowSelectionInterval(i, i);
						int j = 0;
						if (i > 10)
							j = i - 10; // to center the selected row
						jTablePatient.scrollRectToVisible(jTablePatient.getCellRect(j, i, true));
						break;
					}
				}
			}
		}
		return jPanelCenter;
	}

	private JPanel getJPanelTop() {
		if (jPanelTop == null) {
			jPanelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
			jPanelTop.add(getJLabelSearch());
			jPanelTop.add(getJTextFieldSearchPatient());
			if (MainMenu.checkUserGrants("btnadmnew"))
				jPanelTop.add(getButtonNew());
			if (Param.bool("ENHANCEDSEARCH"))
				jPanelTop.add(getJSearchButton());
		}
		return jPanelTop;
	}

	private JButton getJSearchButton() {
		if (jSearchButton == null) {
			jSearchButton = new JButton();
			jSearchButton.setIcon(new ImageIcon("rsc/icons/zoom_r_button.png"));
			jSearchButton.setPreferredSize(new Dimension(20, 20));
			jSearchButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					//patArray = patManager.getPatientWithHeightAndWeight(jTextFieldSearchPatient.getText());
					patSearch = patManager.getPatientWithHeightAndWeight(jTextFieldSearchPatient.getText());
					
					filterPatient();
				}
			});
		}
		return jSearchButton;
	}

	private JPanel getJPanelButtons() {
		if (jPanelButtons == null) {
			jPanelButtons = new JPanel();
			jPanelButtons.add(getJButtonSelect());
			jPanelButtons.add(getJButtonCancel());
		}
		return jPanelButtons;
	}

	class SelectPatientModel extends DefaultTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SelectPatientModel() {
			patSearch = patManager.getPatientWithHeightAndWeight2(null);
		}

		public int getRowCount() {
			if (patSearch == null)
				return 0;
			return patSearch.size();
		}

		public String getColumnName(int c) {
			return patColums[c];
		}

		public int getColumnCount() {
			return patColums.length;
		}

		public Object getValueAt(int r, int c) {
			Patient patient = patSearch.get(r);
			if (c == -1) {
				return patient;
			} else if (c == 0) {
				return patient.getCode();
			} else if (c == 1) {
				return patient.getName();
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}
	}

	class CenterTableCellRenderer extends DefaultTableCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			cell.setForeground(Color.BLACK);
			setHorizontalAlignment(CENTER);
			return cell;
		}
	}

	@Override
	public void patientUpdated(AWTEvent e) {
		// System.out.println(e);
	}

	@Override
	public void patientInserted(AWTEvent e) {
		patient = (Patient) e.getSource();
		jButtonSelect.doClick();
		// patSearch.add(patient);
		// jTablePatient.updateUI();
	}

	public void setButtonNew(JButton buttonNew) {
		this.buttonNew = buttonNew;
	}
	public Patient getPatient() {
		return patient;
	}
}
