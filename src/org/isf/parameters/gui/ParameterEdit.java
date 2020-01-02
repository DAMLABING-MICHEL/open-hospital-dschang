
/*------------------------------------------
 * ExamEdit - add/edit an exam
 * -----------------------------------------
 * modification history
 * 03/11/2006 - ross - Enlarged Destription from 50 to 100 
 *                   - removed toupper for the description
 * 			         - version is now 1.0 
 *------------------------------------------*/

package org.isf.parameters.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.*;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JComboBox;
import org.isf.exa.manager.ExamBrowsingManager;
import org.isf.exa.model.*;
import org.isf.exatype.model.ExamType;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.utils.jobjects.VoLimitedTextField;
import org.isf.generaldata.MessageBundle;
import org.isf.medicalinventory.gui.InventoryEdit.InventoryListener;
import org.isf.parameters.manager.ParametersManager;
import org.isf.parameters.model.Parameter;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;
import javax.swing.event.EventListenerList;

public class ParameterEdit extends ModalJFrame {

	private static final long serialVersionUID = 1L;
	// listeners/////////////////////////////////////
	private static EventListenerList parametersListeners = new EventListenerList();

	public interface ParameterListener extends EventListener {
		public void ParameterUpdated(AWTEvent e);

		public void ParameterInserted(AWTEvent e);		

		public void ParameterDeleted(AWTEvent e);
	}

	public static void addParameterListener(ParameterListener l) {
		parametersListeners.add(ParameterListener.class, l);
	}

	public static void removeParameterListener(ParameterListener listener) {
		parametersListeners.remove(ParameterListener.class, listener);
	}

	private void fireParameterInserted() {
		AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = parametersListeners.getListeners(ParameterListener.class);
		for (int i = 0; i < listeners.length; i++)
			((ParameterListener) listeners[i]).ParameterInserted(event);
	}

	private void fireParameterUpdated() {
		AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = parametersListeners.getListeners(ParameterListener.class);
		for (int i = 0; i < listeners.length; i++)
			((ParameterListener) listeners[i]).ParameterUpdated(event);
	}

	

	private void fireParameterDeleted() {
		AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = parametersListeners.getListeners(ParameterListener.class);
		for (int i = 0; i < listeners.length; i++)
			((ParameterListener) listeners[i]).ParameterDeleted(event);
	}

	/////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////

	private static final String VERSION=MessageBundle.getMessage("angal.versione");  
    
	private JPanel jContentPane = null;
	private JPanel dataPanel = null;
	private JPanel buttonPanel = null;
	private JButton cancelButton = null;
	private JButton okButton = null;
	private JLabel descLabel = null;
	private JLabel codeLabel= null;
	private JLabel procLabel = null;
	private JLabel defLabel = null;
	private JLabel typeLabel = null;
	private Parameter parameter = null;
	private boolean insert = false;
	private JLabel codesLabel;
	private JTextField codeTextField;
	private JLabel descriptionLabel;
	//private JTextField textField_1;
	private JTextPane descriptionTextField;
	private JLabel typeeLabel;
	private JRadioButton alphanumRadio;
	private JRadioButton booleanRadio;
	private JLabel valueLabel;
	private JComboBox valueComboBox;
	private JLabel defaultValueLabel;
	private JComboBox defaultValueComboBox;
	private JPanel titlePanel;
	private JLabel titleLabel;
	private JComboBox scopeComboBox;
	private JLabel scopeLabel;
	private String[] patternExamples = { "",MessageBundle.getMessage("angal.parameteredit.yes"),
			MessageBundle.getMessage("angal.parameteredit.no")};
	/**
     * 
	 * This is the default constructor; we pass the arraylist and the selectedrow
     * because we need to update them
	 */
	public ParameterEdit(JFrame owner, Parameter old, boolean inserting) {
		//super();
		insert = inserting;
		parameter = old;		//medical will be used for every operation
		initialize();
	}
	/**
	 * @wbp.parser.constructor
	 */
	public ParameterEdit(Parameter old, boolean inserting) {
		//super();
		insert = inserting;
		parameter = old;		//medical will be used for every operation
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screensize = kit.getScreenSize();
        final int pfrmBase = 20;
        final int pfrmWidth = 7;
        final int pfrmHeight = 8;
        this.setBounds((screensize.width - screensize.width * pfrmWidth / pfrmBase ) / 2, (screensize.height - screensize.height * pfrmHeight / pfrmBase)/2, 
                515, 343);
		this.setContentPane(getJContentPane());
		if (insert) {
			titleLabel.setText(MessageBundle.getMessage("angal.parameteredit.titleadd")); 
		} else {
			titleLabel.setText(MessageBundle.getMessage("angal.parameteredit.titleedit")); 
		}
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		///
		if(!insert && parameter!=null){
			String val = parameter.getValue()!=null?parameter.getValue():parameter.getDefault_value();
			if(val !=null && !val.equals("@true") && !val.equals("@false")){
				alphanumRadio.setSelected(true);
				
				booleanRadio.setEnabled(false);
				
				valueComboBox.setEditable(true);
				defaultValueComboBox.setEditable(true);											
				valueComboBox.removeAllItems();
				defaultValueComboBox.removeAllItems();				
				valueComboBox.setSelectedItem(parameter.getValue());
				defaultValueComboBox.setSelectedItem(parameter.getDefault_value());
				getButtonSubComponent(valueComboBox).setVisible(false);
				getButtonSubComponent(defaultValueComboBox).setVisible(false);
			}	
						
			
			String valdefault = parameter.getDefault_value();
			String value = parameter.getValue();
			if((val !=null && val.equals("@true")) || (val !=null && val.equals("@false"))){
				booleanRadio.setSelected(true);	
				
				alphanumRadio.setEnabled(false);
				
				if(value !=null && value.equals("@true"))
					valueComboBox.setSelectedIndex(1);
				if(value !=null && value.equals("@false"))
					valueComboBox.setSelectedIndex(2);
				
				if(valdefault !=null && valdefault.equals("@true"))
					defaultValueComboBox.setSelectedIndex(1);
				if(valdefault !=null && valdefault.equals("@false"))
					defaultValueComboBox.setSelectedIndex(2);								
			}
		}
		///
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getTitlePanel(), BorderLayout.NORTH);
			jContentPane.add(getDataPanel(), BorderLayout.CENTER);  // Generated
			jContentPane.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes dataPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getDataPanel() {
		if (dataPanel == null) {
			dataPanel = new JPanel();
			GridBagLayout gbl_dataPanel = new GridBagLayout();
			gbl_dataPanel.columnWidths = new int[]{49, 0, 0, 0, 0, 0, 42, 0};
			gbl_dataPanel.rowHeights = new int[]{20, 19, 0, 0, 0, 0, 0, 0};
			gbl_dataPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, Double.MIN_VALUE};
			gbl_dataPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
			dataPanel.setLayout(gbl_dataPanel);
//			GridBagConstraints gbc_scopeLabel = new GridBagConstraints();
//			gbc_scopeLabel.anchor = GridBagConstraints.WEST;
//			gbc_scopeLabel.insets = new Insets(0, 0, 5, 5);
//			gbc_scopeLabel.gridx = 2;
//			gbc_scopeLabel.gridy = 1;
//			dataPanel.add(getScopeLabel(), gbc_scopeLabel);
//			GridBagConstraints gbc_scopeComboBox = new GridBagConstraints();
//			gbc_scopeComboBox.gridwidth = 3;
//			gbc_scopeComboBox.insets = new Insets(0, 0, 5, 5);
//			gbc_scopeComboBox.fill = GridBagConstraints.HORIZONTAL;
//			gbc_scopeComboBox.gridx = 3;
//			gbc_scopeComboBox.gridy = 1;
//			dataPanel.add(getScopeComboBox(), gbc_scopeComboBox);
			GridBagConstraints gbc_codesLabel = new GridBagConstraints();
			gbc_codesLabel.anchor = GridBagConstraints.WEST;
			gbc_codesLabel.insets = new Insets(0, 0, 5, 5);
			gbc_codesLabel.gridx = 2;
			gbc_codesLabel.gridy = 2;
			dataPanel.add(getCodesLabel(), gbc_codesLabel);
			GridBagConstraints gbc_codeTextField = new GridBagConstraints();
			gbc_codeTextField.gridwidth = 3;
			gbc_codeTextField.insets = new Insets(0, 0, 5, 5);
			gbc_codeTextField.fill = GridBagConstraints.HORIZONTAL;
			gbc_codeTextField.gridx = 3;
			gbc_codeTextField.gridy = 2;
			dataPanel.add(getCodeTextField(), gbc_codeTextField);
			GridBagConstraints gbc_descriptionLabel = new GridBagConstraints();
			gbc_descriptionLabel.anchor = GridBagConstraints.WEST;
			gbc_descriptionLabel.insets = new Insets(0, 0, 5, 5);
			gbc_descriptionLabel.gridx = 2;
			gbc_descriptionLabel.gridy = 3;
			dataPanel.add(getDescriptionLabel(), gbc_descriptionLabel);
			GridBagConstraints gbc_descriptionTextField = new GridBagConstraints();
			gbc_descriptionTextField.gridwidth = 3;
			gbc_descriptionTextField.insets = new Insets(0, 0, 5, 5);
			gbc_descriptionTextField.fill = GridBagConstraints.HORIZONTAL;
			gbc_descriptionTextField.gridx = 3;
			gbc_descriptionTextField.gridy = 3;
			dataPanel.add(getDescriptionTextField(), gbc_descriptionTextField);
			GridBagConstraints gbc_typeeLabel = new GridBagConstraints();
			gbc_typeeLabel.anchor = GridBagConstraints.WEST;
			gbc_typeeLabel.insets = new Insets(0, 0, 5, 5);
			gbc_typeeLabel.gridx = 2;
			gbc_typeeLabel.gridy = 4;
			dataPanel.add(getTypeeLabel(), gbc_typeeLabel);
			GridBagConstraints gbc_booleanRadio = new GridBagConstraints();
			gbc_booleanRadio.insets = new Insets(0, 0, 5, 5);
			gbc_booleanRadio.gridx = 3;
			gbc_booleanRadio.gridy = 4;
			dataPanel.add(getBooleanRadio(), gbc_booleanRadio);
			GridBagConstraints gbc_alphanumRadio = new GridBagConstraints();
			gbc_alphanumRadio.insets = new Insets(0, 0, 5, 5);
			gbc_alphanumRadio.gridx = 4;
			gbc_alphanumRadio.gridy = 4;
			dataPanel.add(getAlphanumRadio(), gbc_alphanumRadio);
			GridBagConstraints gbc_valueLabel = new GridBagConstraints();
			gbc_valueLabel.anchor = GridBagConstraints.WEST;
			gbc_valueLabel.insets = new Insets(0, 0, 5, 5);
			gbc_valueLabel.gridx = 2;
			gbc_valueLabel.gridy = 5;
			dataPanel.add(getValueLabel(), gbc_valueLabel);
			GridBagConstraints gbc_valueComboBox = new GridBagConstraints();
			gbc_valueComboBox.gridwidth = 3;
			gbc_valueComboBox.insets = new Insets(0, 0, 5, 5);
			gbc_valueComboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_valueComboBox.gridx = 3;
			gbc_valueComboBox.gridy = 5;
			dataPanel.add(getValueComboBox(), gbc_valueComboBox);
			GridBagConstraints gbc_defaultValueLabel = new GridBagConstraints();
			gbc_defaultValueLabel.anchor = GridBagConstraints.WEST;
			gbc_defaultValueLabel.insets = new Insets(0, 0, 0, 5);
			gbc_defaultValueLabel.gridx = 2;
			gbc_defaultValueLabel.gridy = 6;
			dataPanel.add(getDefaultValueLabel(), gbc_defaultValueLabel);
			GridBagConstraints gbc_defaultValueComboBox = new GridBagConstraints();
			gbc_defaultValueComboBox.gridwidth = 3;
			gbc_defaultValueComboBox.insets = new Insets(0, 0, 0, 5);
			gbc_defaultValueComboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_defaultValueComboBox.gridx = 3;
			gbc_defaultValueComboBox.gridy = 6;
			dataPanel.add(getDefaultValueComboBox(), gbc_defaultValueComboBox);
			
			ButtonGroup group = new ButtonGroup();
			group.add(alphanumRadio);
			group.add(booleanRadio);
		}
		return dataPanel;
	}

	/**
	 * This method initializes buttonPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setBorder(new MatteBorder(1, 0, 0, 0, (Color) new Color(0, 0, 0)));
			buttonPanel.add(getOkButton(), null);  // Generated
			buttonPanel.add(getCancelButton(), null);  // Generated
		}
		return buttonPanel;
	}

	/**
	 * This method initializes cancelButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText(MessageBundle.getMessage("angal.common.cancel"));  // Generated
            cancelButton.setMnemonic(KeyEvent.VK_C);
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				dispose();
				}
			});
		}
		return cancelButton;
	}

	/**
	 * This method initializes okButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText(MessageBundle.getMessage("angal.parameters.save"));  // Generated
            okButton.setMnemonic(KeyEvent.VK_E);
			okButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if((       codeTextField.getText().trim().equals(""))
							||(defaultValueComboBox.getSelectedItem().toString().trim().equals(""))
							
						){
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.parameter.pleaseinsertrequiredfields"));
					}
					else{	
						ParametersManager manager = new ParametersManager();
						boolean result = false;
						if (insert) {
							Parameter parameter = new Parameter();						
							parameter.setCode(codeTextField.getText().trim().toString());
							parameter.setDescription(descriptionTextField.getText().trim().toString());
							if(alphanumRadio.isSelected()){
								parameter.setValue(valueComboBox.getSelectedItem()!=null?valueComboBox.getSelectedItem().toString():"");
								parameter.setDefault_value(defaultValueComboBox.getSelectedItem()!=null?defaultValueComboBox.getSelectedItem().toString():"");
							}
							if(booleanRadio.isSelected()){
								int index = valueComboBox.getSelectedIndex();
								parameter.setValue(index == 1?"@true":index == 2?"@false":"");
								index = defaultValueComboBox.getSelectedIndex();
								parameter.setDefault_value(index == 1?"@true":index == 2?"@false":"");
							}
							if (manager.isKeyPresent(parameter.getCode())) {
								JOptionPane.showMessageDialog(ParameterEdit.this, parameter.getCode()+", "+MessageBundle.getMessage("angal.parameters.codealreadyused"));
								return;
							}
							result = manager.newParameter(parameter);
						} else {
							parameter.setCode(codeTextField.getText().trim().toString());
							parameter.setDescription(descriptionTextField.getText().trim().toString());
							if(alphanumRadio.isSelected()){
								parameter.setValue(valueComboBox.getSelectedItem()!=null?valueComboBox.getSelectedItem().toString():"");
								parameter.setDefault_value(defaultValueComboBox.getSelectedItem()!=null?defaultValueComboBox.getSelectedItem().toString():"");
							}
							if(booleanRadio.isSelected()){
								int index = valueComboBox.getSelectedIndex();
								parameter.setValue(index == 1?"@true":index == 2?"@false":"");
								index = defaultValueComboBox.getSelectedIndex();
								parameter.setDefault_value(index == 1?"@true":index == 2?"@false":"");
							}
							result = manager.updateParameter(parameter);
						}
						if (!result){
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.exa.thedatacouldnotbesaved"));
						}
						else {
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.parameter.wellsaved"));							
							fireParameterInserted();
							dispose();
						}
					}
				}
			});
		}
		return okButton;
	}
	private JLabel getCodesLabel() {
		if (codesLabel == null) {
			codesLabel = new JLabel(MessageBundle.getMessage("angal.parameteredit.code")+"*");
			codesLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		}
		return codesLabel;
	}
	private JTextField getCodeTextField() {
		if (codeTextField == null) {
			codeTextField = new JTextField();
			codeTextField.setPreferredSize(new Dimension(0, 27));
			codeTextField.setMinimumSize(new Dimension(0, 27));
			codeTextField.setSize(new Dimension(0, 27));
			codeTextField.setFont(new Font("Tahoma", Font.PLAIN, 12));
			codeTextField.setColumns(10);
			if (parameter != null) {
				codeTextField.setText(parameter.getCode());
				codeTextField.setEnabled(false);
			}
		}
		return codeTextField;
	}
	private JLabel getDescriptionLabel() {
		if (descriptionLabel == null) {
			descriptionLabel = new JLabel(MessageBundle.getMessage("angal.parameteredit.description"));
			descriptionLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
			
		}
		return descriptionLabel;
	}
	private JTextPane getDescriptionTextField() {
		if (descriptionTextField == null) {
			descriptionTextField = new JTextPane();
			descriptionTextField.setBorder(new LineBorder(new Color(0, 0, 0)));
			descriptionTextField.setPreferredSize(new Dimension(0, 54));
			descriptionTextField.setMinimumSize(new Dimension(0, 54));
			descriptionTextField.setSize(new Dimension(0, 27));
			descriptionTextField.setFont(new Font("Tahoma", Font.PLAIN, 12));			
			if (parameter != null) {
				descriptionTextField.setText(parameter.getDescription());
			}
		}
		return descriptionTextField;
	}
	private JLabel getTypeeLabel() {
		if (typeeLabel == null) {
			typeeLabel = new JLabel(MessageBundle.getMessage("angal.parameteredit.type")+"*");
			typeeLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		}
		return typeeLabel;
	}
	
	private JRadioButton getAlphanumRadio() {
		if (alphanumRadio == null) {
			alphanumRadio = new JRadioButton(MessageBundle.getMessage("angal.parameteredit.alphanum"));
			if(insert)
				alphanumRadio.setSelected(false);	
			else if(parameter!=null){
//				String val = parameter.getValue();
//				if(val !=null && !val.equals("@true") && !val.equals("@false")){
//					alphanumRadio.setSelected(true);
//					valueComboBox.setEditable(true);
//					defaultValueComboBox.setEditable(true);							
//					getButtonSubComponent(valueComboBox).setVisible(false);
//					getButtonSubComponent(defaultValueComboBox).setVisible(false);
//					valueComboBox.removeAllItems();
//					defaultValueComboBox.removeAllItems();
//				}					
			}
			alphanumRadio.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (alphanumRadio.isSelected()) {
						valueComboBox.setEditable(true);
						defaultValueComboBox.setEditable(true);							
						getButtonSubComponent(valueComboBox).setVisible(false);
						getButtonSubComponent(defaultValueComboBox).setVisible(false);
						valueComboBox.removeAllItems();
						defaultValueComboBox.removeAllItems();
					}
				}
			});
		}
		return alphanumRadio;
	}
	private JRadioButton getBooleanRadio() {
		if (booleanRadio == null) {
			booleanRadio = new JRadioButton(MessageBundle.getMessage("angal.parameteredit.boolean"));
			if(insert)
				booleanRadio.setSelected(true);	
			else if(parameter!=null){
//				String val = parameter.getValue();
//				System.out.println("the value "+val);
//				if((val !=null && val.equals("@true")) || (val !=null && val.equals("@false"))){
//					booleanRadio.setSelected(true);	
//				}else{
//					System.out.println("the else "+val);
//				}					
			}
			booleanRadio.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (booleanRadio.isSelected()) {
						valueComboBox.setEditable(false);
						defaultValueComboBox.setEditable(false);
						
						getButtonSubComponent(valueComboBox).setVisible(true);
						getButtonSubComponent(defaultValueComboBox).setVisible(true);
						
						valueComboBox.removeAllItems();
						defaultValueComboBox.removeAllItems();
						valueComboBox.addItem("");
						valueComboBox.addItem(MessageBundle.getMessage("angal.parameteredit.yes"));
						valueComboBox.addItem(MessageBundle.getMessage("angal.parameteredit.no"));
						defaultValueComboBox.addItem("");
						defaultValueComboBox.addItem(MessageBundle.getMessage("angal.parameteredit.yes"));
						defaultValueComboBox.addItem(MessageBundle.getMessage("angal.parameteredit.no"));
					}
				}
			});
		}
		return booleanRadio;  
	}
	private JLabel getValueLabel() {
		if (valueLabel == null) {
			valueLabel = new JLabel(MessageBundle.getMessage("angal.parameteredit.value"));
			valueLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		}
		return valueLabel;
	}
	private JComboBox getValueComboBox() {
		if (valueComboBox == null) {			
			valueComboBox = new JComboBox(patternExamples);
			valueComboBox.setPreferredSize(new Dimension(0, 27));
			valueComboBox.setMinimumSize(new Dimension(0, 27));
			valueComboBox.setSize(new Dimension(0, 27));
			valueComboBox.setFont(new Font("Tahoma", Font.PLAIN, 12));
			valueComboBox.setEditable(false);
			if (parameter != null) {
				
				String val = parameter.getValue();
				System.out.println("ValueComboBox "+val);
				if(val !=null && val.equals("@true"))
					valueComboBox.setSelectedIndex(1);
				if(val !=null && val.equals("@false"))
					valueComboBox.setSelectedIndex(2);
			}
		}
		return valueComboBox;
	}
	private JLabel getDefaultValueLabel() {
		if (defaultValueLabel == null) {
			defaultValueLabel = new JLabel(MessageBundle.getMessage("angal.parameteredit.defaultvalue")+"*");
			defaultValueLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		}
		return defaultValueLabel;
	}
	private JComboBox getDefaultValueComboBox() {
		if (defaultValueComboBox == null) {			
			defaultValueComboBox = new JComboBox(patternExamples);
			defaultValueComboBox.setPreferredSize(new Dimension(0, 27));
			defaultValueComboBox.setMinimumSize(new Dimension(0, 27));
			defaultValueComboBox.setSize(new Dimension(0, 27));
			defaultValueComboBox.setFont(new Font("Tahoma", Font.PLAIN, 12));
			defaultValueComboBox.setEditable(false);
			if (parameter != null) {
				String val = parameter.getValue();
				if(val !=null && val.equals("@true"))
					defaultValueComboBox.setSelectedIndex(1);
				if(val !=null && val.equals("@false"))
					defaultValueComboBox.setSelectedIndex(2);
			}
		}
		return defaultValueComboBox;
	}
	private JPanel getTitlePanel() {
		if (titlePanel == null) {
			titlePanel = new JPanel();
			titlePanel.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
			titlePanel.setPreferredSize(new Dimension(10, 35));
			titlePanel.setMinimumSize(new Dimension(10, 35));
			titlePanel.add(getTitleLabel());
		}
		return titlePanel;
	}
	private JLabel getTitleLabel() {
		if (titleLabel == null) {
			titleLabel = new JLabel(MessageBundle.getMessage("angal.parameteredit.titleadd"));
			titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
			titleLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		}
		return titleLabel;
	}
//	private JComboBox getScopeComboBox() {
//		if (scopeComboBox == null) {
//			scopeComboBox = new JComboBox();
//			scopeComboBox.addItem("");
//			scopeComboBox.addItem(MessageBundle.getMessage("angal.parameteredit.general"));
//			scopeComboBox.addItem(MessageBundle.getMessage("angal.parameteredit.local"));
//			
//			
//			scopeComboBox.setPreferredSize(new Dimension(28, 27));
//			scopeComboBox.setMinimumSize(new Dimension(28, 27));
//			scopeComboBox.setFont(new Font("Tahoma", Font.PLAIN, 12));
//			if (parameter != null) {
//				String scope = parameter.getScope();
//				if(scope!=null && scope.equals("1"))
//					scopeComboBox.setSelectedItem(MessageBundle.getMessage("angal.parameteredit.general"));
//					//scopeComboBox.getSelectedIndex();
//				if(scope!=null && scope.equals("2"))
//					scopeComboBox.setSelectedItem(MessageBundle.getMessage("angal.parameteredit.local"));
//			}
//		}
//		return scopeComboBox;
//	}
//	private JLabel getScopeLabel() {
//		if (scopeLabel == null) {
//			scopeLabel = new JLabel(MessageBundle.getMessage("angal.parameteredit.scope")+"*");
//		}
//		return scopeLabel;
//	}
	
	private  JButton getButtonSubComponent(Container container) {
	      if (container instanceof JButton) {
	         return (JButton) container;
	      } else {
	         Component[] components = container.getComponents();
	         for (Component component : components) {
	            if (component instanceof Container) {
	               return getButtonSubComponent((Container)component);
	            }
	         }
	      }
	      return null;
	   }
}  //  @jve:decl-index=0:visual-constraint="82,7"
