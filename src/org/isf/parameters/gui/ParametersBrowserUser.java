package org.isf.parameters.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.isf.generaldata.MessageBundle;
import org.isf.menu.gui.MainMenu;
import org.isf.parameters.gui.ParameterEditUser.ParameterListener;
import org.isf.parameters.manager.Param;
import org.isf.parameters.manager.ParametersManager;
import org.isf.parameters.model.Parameter;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.utils.time.TimeTools;

import com.toedter.calendar.JDateChooser;

public class ParametersBrowserUser extends ModalJFrame implements ParameterListener{
	
	private JDateChooser jCalendarTo;
	private GregorianCalendar dateFrom = TimeTools.getServerDateTime();
	private GregorianCalendar dateTo = TimeTools.getServerDateTime();
	private JLabel jLabelTo;
	private JLabel jLabelFrom;
	private JPanel panelHeader;
	private JPanel panelFooter;
	private JPanel panelContent;
	private JButton closeButton;
	private JButton newButton;
	private JButton updateButton;
	private JButton printButton;
	private JButton deleteButton;
	private JButton viewButton;
	private JScrollPane scrollPaneInventory;
	private JTable jTableParameter;
	private ArrayList<Parameter> parametersList;
	private String[] pColums = { MessageBundle.getMessage("angal.parameters.code"),
			                     
			                     MessageBundle.getMessage("angal.parameters.value")
			                    
			                   };
	private int[] pColumwidth = { 200, 200};
	
	private final String FILE_PROPERTIES = Param.string("GENERALDATA_PROPERTIES");

	private JComboBox typeComboBox;
	private JLabel typeLabel;
	private JLabel titlesLabel;
	
	public ParametersBrowserUser() {		
		initComponents();
		//modal exclude
		if(!Param.bool("WITHMODALWINDOW")){
			setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		}
	}
	
	private void initComponents() {
		//setSize(new Dimension(650, 550));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(850, 550));
		setLocationRelativeTo(null); // center
		setTitle(MessageBundle.getMessage("angal.parameters.managementtitle"));
		
		panelHeader = getPanelHeader();
		getContentPane().add(panelHeader, BorderLayout.NORTH);
		
		panelContent = getPanelContent();
		getContentPane().add(panelContent, BorderLayout.CENTER);
		
		panelFooter = getPanelFooter();
		getContentPane().add(panelFooter, BorderLayout.SOUTH);
		
				
		addWindowListener(new WindowAdapter(){	
			public void windowClosing(WindowEvent e) {
				//to free memory
				if(parametersList!=null){
					parametersList.clear();
				}
				dispose();
			}			
		});
		//pack();
	}
	
	private JPanel getPanelHeader(){
		if(panelHeader==null){
			panelHeader = new JPanel();
			panelHeader.setBorder(new EmptyBorder(5, 0, 0, 5));
			GridBagLayout gbl_panelHeader = new GridBagLayout();
			gbl_panelHeader.columnWidths = new int[]{21, 97, 66, 99, 95, 136, 0};
			gbl_panelHeader.rowHeights = new int[]{32, 0};
			gbl_panelHeader.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
			gbl_panelHeader.rowWeights = new double[]{0.0, Double.MIN_VALUE};
			panelHeader.setLayout(gbl_panelHeader);
			GridBagConstraints gbc_jLabelFrom = new GridBagConstraints();
			gbc_jLabelFrom.fill = GridBagConstraints.HORIZONTAL;
			gbc_jLabelFrom.insets = new Insets(0, 0, 0, 5);
			gbc_jLabelFrom.gridx = 0;
			gbc_jLabelFrom.gridy = 0;
			panelHeader.add(getJLabelFrom(), gbc_jLabelFrom);
			GridBagConstraints gbc_titlesLabel = new GridBagConstraints();
			gbc_titlesLabel.anchor = GridBagConstraints.WEST;
			gbc_titlesLabel.insets = new Insets(0, 0, 0, 5);
			gbc_titlesLabel.gridx = 1;
			gbc_titlesLabel.gridy = 0;
			panelHeader.add(getTitlesLabel(), gbc_titlesLabel);
			GridBagConstraints gbc_jLabelTo = new GridBagConstraints();
			gbc_jLabelTo.fill = GridBagConstraints.HORIZONTAL;
			gbc_jLabelTo.insets = new Insets(0, 0, 0, 5);
			gbc_jLabelTo.gridx = 2;
			gbc_jLabelTo.gridy = 0;
			panelHeader.add(getJLabelTo(), gbc_jLabelTo);
			GridBagConstraints gbc_jCalendarTo = new GridBagConstraints();
			gbc_jCalendarTo.fill = GridBagConstraints.HORIZONTAL;
			gbc_jCalendarTo.insets = new Insets(0, 0, 0, 5);
			gbc_jCalendarTo.gridx = 3;
			gbc_jCalendarTo.gridy = 0;
			panelHeader.add(getJCalendarTo(), gbc_jCalendarTo);
			GridBagConstraints gbc_typeLabel = new GridBagConstraints();
			gbc_typeLabel.fill = GridBagConstraints.HORIZONTAL;
			gbc_typeLabel.insets = new Insets(0, 0, 0, 5);
			gbc_typeLabel.gridx = 4;
			gbc_typeLabel.gridy = 0;
			if(MainMenu.checkUserGrants("parameters_general")){
				panelHeader.add(getTypeLabel(), gbc_typeLabel);
			}
			GridBagConstraints gbc_typeComboBox = new GridBagConstraints();
			gbc_typeComboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_typeComboBox.gridx = 5;
			gbc_typeComboBox.gridy = 0;
			
//			if(MainMenu.checkUserGrants("parameters_general")){
//				panelHeader.add(getComboBox(), gbc_typeComboBox);
//			}
			
		}
		return panelHeader;
	}
	private JPanel getPanelContent(){
		if(panelContent==null){
			panelContent = new JPanel();
			GridBagLayout gbl_panelContent = new GridBagLayout();
			gbl_panelContent.columnWidths = new int[]{452, 0};
			gbl_panelContent.rowHeights = new int[]{402, 0};
			gbl_panelContent.columnWeights = new double[]{1.0, Double.MIN_VALUE};
			gbl_panelContent.rowWeights = new double[]{1.0, Double.MIN_VALUE};
			panelContent.setLayout(gbl_panelContent);
			GridBagConstraints gbc_scrollPaneInventory = new GridBagConstraints();
			gbc_scrollPaneInventory.fill = GridBagConstraints.BOTH;
			gbc_scrollPaneInventory.gridx = 0;
			gbc_scrollPaneInventory.gridy = 0;
			panelContent.add(getScrollPaneInventory(), gbc_scrollPaneInventory);
		}
		return panelContent;
	}
	private JPanel getPanelFooter(){
		if(panelFooter==null){
			panelFooter = new JPanel();
//			if(MainMenu.checkUserGrants("parameters_add")){
//				panelFooter.add(getNewButton());
//			}			
			if(MainMenu.checkUserGrants("parameters_update")){
				panelFooter.add(getUpdateButton());
			}
//			if(MainMenu.checkUserGrants("parameters_delete")){
//				panelFooter.add(getDeleteButton());
//			}
			
			panelFooter.add(getCloseButton());
		}
		return panelFooter;
	}

	private JDateChooser getJCalendarTo() {
		if (jCalendarTo == null) {
			dateTo.set(GregorianCalendar.HOUR_OF_DAY, 23);
			dateTo.set(GregorianCalendar.MINUTE, 59);
			dateTo.set(GregorianCalendar.SECOND, 59);
			jCalendarTo = new JDateChooser(dateTo.getTime()); // Calendar
			jCalendarTo.setVisible(false);
			jCalendarTo.setLocale(new Locale(Param.string("LANGUAGE")));
			jCalendarTo.setDateFormatString("dd/MM/yy"); //$NON-NLS-1$
			jCalendarTo.addPropertyChangeListener("date", new PropertyChangeListener() { //$NON-NLS-1$
				
				public void propertyChange(PropertyChangeEvent evt) {
					jCalendarTo.setDate((Date) evt.getNewValue());
					dateTo.setTime((Date) evt.getNewValue());
					dateTo.set(GregorianCalendar.HOUR_OF_DAY, 23);
					dateTo.set(GregorianCalendar.MINUTE, 59);
					dateTo.set(GregorianCalendar.SECOND, 59);
					if(parametersList!=null) parametersList.clear();
					jTableParameter.setModel(new ParametersBrowsingModel());
				}
			});
		}
		return jCalendarTo;
	}
	private JLabel getJLabelTo() {
		if (jLabelTo == null) {
			jLabelTo = new JLabel();
			jLabelTo.setVisible(false);
			jLabelTo.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelTo.setText(MessageBundle.getMessage("angal.billbrowser.to")); //$NON-NLS-1$
		}
		return jLabelTo;
	}
	private JLabel getJLabelFrom() {
		if (jLabelFrom == null) {
			jLabelFrom = new JLabel();
			jLabelFrom.setVisible(false);
			jLabelFrom.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelFrom.setText(MessageBundle.getMessage("angal.billbrowser.from")); //$NON-NLS-1$
		}
		return jLabelFrom;
	}
	
//	private JButton getNewButton() {
//		newButton = new JButton(MessageBundle.getMessage("angal.parameter.new"));
//		newButton.setMnemonic(KeyEvent.VK_N);
//		newButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				ParameterEditUser parameterEdit = new ParameterEditUser(null,true);
//				parameterEdit.addParameterListener(ParametersBrowserUser.this);
//				if(Param.bool("WITHMODALWINDOW")){
//					parameterEdit.showAsModal(ParametersBrowserUser.this);
//				}else{
//					parameterEdit.show(ParametersBrowserUser.this);
//				}
//				
//			}
//		});
//		return newButton;
//	}
	
	private JButton getUpdateButton() {
		updateButton = new JButton(MessageBundle.getMessage("angal.parameter.update"));
		updateButton.setMnemonic(KeyEvent.VK_M);
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Parameter parameter = new Parameter();
				int selecedRow = jTableParameter.getSelectedRow();
				if(selecedRow >-1 ){
					parameter = parametersList.get(selecedRow);					
					ParameterEditUser parameterEdit = new ParameterEditUser(parameter,false);					
					parameterEdit.addParameterListener(ParametersBrowserUser.this);
					if(Param.bool("WITHMODALWINDOW")){
						parameterEdit.showAsModal(ParametersBrowserUser.this);
					}else{
						parameterEdit.show(ParametersBrowserUser.this);
					}
				}
				else{
					JOptionPane.showMessageDialog(ParametersBrowserUser.this,
							MessageBundle.getMessage("angal.parameters.pleaseselectarow"), 
							MessageBundle.getMessage("angal.parameters.managementtitle"), 
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
		});
		return updateButton;
	}
	
//	private JButton getDeleteButton() {
//		deleteButton = new JButton(MessageBundle.getMessage("angal.parameter.delete"));
//		deleteButton.setMnemonic(KeyEvent.VK_D);
//		deleteButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				
//					int indexSelected = jTableParameter.getSelectedRow();
//					if(indexSelected > -1 ){
//						int response = JOptionPane.showConfirmDialog(ParametersBrowserUser.this,
//								MessageBundle.getMessage("angal.parameter.deleteconfirm"), 
//								MessageBundle.getMessage("angal.parameter.title"), 
//								JOptionPane.OK_CANCEL_OPTION); 
//						if(response==JOptionPane.OK_OPTION){
//							Parameter parameter = parametersList.get(indexSelected);
//							ParametersManager manager = new ParametersManager();
//							if(manager.deleteParameter(parameter.getId())){
//								JOptionPane.showMessageDialog(ParametersBrowserUser.this,
//										MessageBundle.getMessage("angal.parameter.deletedsucces"), 
//										MessageBundle.getMessage("angal.parameter.title"), 
//										JOptionPane.INFORMATION_MESSAGE);
//								if(parametersList!=null) parametersList.clear();
//								jTableParameter.setModel(new ParametersBrowsingModel());
//							}
//							else{
//								JOptionPane.showMessageDialog(ParametersBrowserUser.this,
//										MessageBundle.getMessage("angal.parameter.deletederror"), 
//										MessageBundle.getMessage("angal.parameter.title"), 
//										JOptionPane.INFORMATION_MESSAGE);
//							}
//						}
//					}else{
//						JOptionPane.showMessageDialog(ParametersBrowserUser.this,
//								MessageBundle.getMessage("angal.inventory.pleaseselectarow"), 
//								MessageBundle.getMessage("angal.inventoryoperation.title"), 
//								JOptionPane.INFORMATION_MESSAGE);
//						return;
//					}
//			}
//		});
//		return deleteButton;
//	}
	
	private JButton getCloseButton() {
		closeButton = new JButton(MessageBundle.getMessage("angal.inventory.close"));
		closeButton.setMnemonic(KeyEvent.VK_C);
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		return closeButton;
	}

	private JScrollPane getScrollPaneInventory() {
		if (scrollPaneInventory == null) {
			scrollPaneInventory = new JScrollPane();
			scrollPaneInventory.setViewportView(getJTableParameter());
		}
		return scrollPaneInventory;
	}
	
	private JTable getJTableParameter() {
		if (jTableParameter == null) {
			jTableParameter = new JTable();
			jTableParameter.setFillsViewportHeight(true);
			jTableParameter.setModel(new ParametersBrowsingModel());
		}
		return jTableParameter;
	}
	
	
	class ParametersBrowsingModel extends DefaultTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ParametersBrowsingModel() {
			parametersList = new ArrayList<Parameter>();
			Parameter param = null;
			try {
				Properties p = new Properties();
				FileInputStream in = new FileInputStream("rsc" + File.separator + FILE_PROPERTIES);
				p.load(in);
				for(Entry<Object, Object> e : p.entrySet()) {
		            param = new Parameter();
		            param.setCode(e.getKey().toString());
		            param.setValue(e.getValue().toString());
		            parametersList.add(param);
		        }
				in.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public ParametersBrowsingModel(String type) {
			ParametersManager manager = new ParametersManager();
			parametersList = manager.getParameters(type);	
		}
		


		public Class<?> getColumnClass(int c) {
			if (c == 0) {
				return String.class;
			} else if (c == 1) {
				return String.class;
			} else if (c == 2) {
				return String.class;
			} else if (c == 3) {
				return String.class;
			}
			else if (c == 4) {
				return String.class;
			}
			return null;
		}

		public int getRowCount() {
			if (parametersList == null)
				return 0;
			return parametersList.size();
		}

		public String getColumnName(int c) {
			return pColums[c];
		}

		public int getColumnCount() {
			return pColums.length;
		}

		public Object getValueAt(int r, int c) {
			Parameter medInvt = parametersList.get(r);
			
			if (c == -1) {
				return medInvt;
			} else if (c == 0) {
				return medInvt.getCode();
			} else if (c == 1) {
				return medInvt.getValue();
			} 			
			return "";
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}

	}
	public String formatDateTime(GregorianCalendar time) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");  //$NON-NLS-1$
		return format.format(time.getTime());
	}

	
//	private JComboBox getComboBox() {
//		if (typeComboBox == null) {
//			typeComboBox = new JComboBox();
//			typeComboBox.addItem("");
//			typeComboBox.addItem(MessageBundle.getMessage("angal.parameters.type.general"));
//			typeComboBox.addItem(MessageBundle.getMessage("angal.parameters.type.local"));
//			typeComboBox.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					if(parametersList!=null) parametersList.clear();
//					int index = typeComboBox.getSelectedIndex();					
//					if(index > 0)
//						jTableParameter.setModel(new ParametersBrowsingModel(index+""));	
//					else
//						jTableParameter.setModel(new ParametersBrowsingModel());
//				}
//			});			
//		}
//		return typeComboBox;
//	}
	
	private JLabel getTypeLabel() {
		if (typeLabel == null) {
			typeLabel = new JLabel(MessageBundle.getMessage("angal.parameters.type"));
			typeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return typeLabel;
	}

	@Override
	public void ParameterUpdated(AWTEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ParameterInserted(AWTEvent e) {
		if(parametersList!=null) parametersList.clear();
		jTableParameter.setModel(new ParametersBrowsingModel());		
	}

	@Override
	public void ParameterDeleted(AWTEvent e) {
		// TODO Auto-generated method stub
		
	}
	private JLabel getTitlesLabel() {
		if (titlesLabel == null) {
			titlesLabel = new JLabel(MessageBundle.getMessage("angal.parameters.list"));
			titlesLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		}
		return titlesLabel;
	}
}
