package org.isf.mortuary.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.util.EventListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.EventListenerList;

import org.isf.generaldata.MessageBundle;
import org.isf.mortuary.manager.MortuaryPriceManager;

import org.isf.mortuary.model.PlagePrixMorgue;
import org.isf.pricesothers.manager.PricesOthersManager;
import org.isf.pricesothers.model.PricesOthers;
import org.isf.utils.jobjects.VoLimitedTextField;
public class MortuaryPriceEdit extends JDialog {
	


		private static final long serialVersionUID = 1L;
		
		private EventListenerList mortuaryPriceListeners = new EventListenerList();

	    public interface MortuaryPriceListener extends EventListener {
	        public void mortuaryPriceUpdated(AWTEvent e);
	        public void mortuaryPriceInserted(AWTEvent e);
	    }

	    public void addMortuaryPriceListener(MortuaryPriceListener l) {
	    	mortuaryPriceListeners.add(MortuaryPriceListener.class, l);
	    }

	    public void removeMortuaryPriceListener(MortuaryPriceListener listener) {
	    	mortuaryPriceListeners.remove(MortuaryPriceListener.class, listener);
	    }

	    private void fireMortuaryPriceInserted() {
	        AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {
	        	private static final long serialVersionUID = 1L;
			};

	        EventListener[] listeners = mortuaryPriceListeners.getListeners(MortuaryPriceListener.class);
	        for (int i = 0; i < listeners.length; i++)
	            ((MortuaryPriceListener)listeners[i]).mortuaryPriceUpdated(event);
	    }
	    private void fireMortuaryPriceUpdated() {
	        AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {
				private static final long serialVersionUID = 1L;
			};

	        EventListener[] listeners = mortuaryPriceListeners.getListeners(MortuaryPriceListener.class);
	        for (int i = 0; i < listeners.length; i++)
	            ((MortuaryPriceListener)listeners[i]).mortuaryPriceInserted(event);
	    }
	    
		private JPanel jContentPane = null;
		private JPanel dataPanel = null;
		private JPanel buttonPanel = null;
		private JButton cancelButton = null;
		private JButton okButton = null;
		private JTextField descriptionTextField = null;
		private String lastdescription;
		private int lastMinDays;
		private int lastMaxDays;
		private PlagePrixMorgue plage = null;
		private boolean insert;
		private JPanel jDataPanel = null;
		private JPanel jDescriptionLabelPanel = null;
		private JLabel jDescripitonLabel = null;
		private JLabel jMinDaysLabel = null;
		private JPanel jMinDaysLabelPanel = null;
		private JPanel jMaxDaysLabelPanel = null;
		private JLabel jMaxDaysLabel = null;
		private PricesOthers pOther;
		private JTextField codeTextField;
		private JPanel jPanelCode;
		private JLabel jLabelCode;
		
		private int id = 0;
		
		private JTextField minDaysTextField = null;
		private JTextField maxDaysTextField = null;
		private JTextField priceTextField = null;
		
		
		/**
	     * 
		 * This is the default constructor; we pass the arraylist and the selectedrow
	     * because we need to update them
		 */
		public MortuaryPriceEdit(JFrame owner, PlagePrixMorgue old, boolean inserting) {
			super(owner,true);
			insert = inserting;
			plage = old;
			lastdescription= plage.getDescription();
			lastMinDays = plage.getNbJourmin() ;
			lastMaxDays = plage.getNbJourMax();
			id = plage.getId();
			initialize();
		}


		/**
		 * This method initializes this
		 * 
		 * @return void
		 */
		private void initialize() {
			
			this.setBounds(300,300,350,400);
			this.setContentPane(getJContentPane());
			if (insert) {
				this.setTitle(MessageBundle.getMessage("angal.pricerange.pricerangenewtitle"));
			} else {
				this.setTitle(MessageBundle.getMessage("angal.pricerange.pricerangeedittitle"));
			}
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			this.setLocationRelativeTo(null);
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
				jContentPane.add(getDataPanel(), java.awt.BorderLayout.NORTH);  // Generated
				jContentPane.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);  // Generated
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
				//dataPanel.setLayout(new BoxLayout(getDataPanel(), BoxLayout.Y_AXIS));  // Generated
				dataPanel.add(getJDataPanel(), null);
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
				cancelButton.setMnemonic(KeyEvent.VK_C);
				cancelButton.setText(MessageBundle.getMessage("angal.common.cancel"));  // Generated
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
				okButton.setText(MessageBundle.getMessage("angal.common.ok"));  // Generated
				okButton.setMnemonic(KeyEvent.VK_O);
				okButton.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						MortuaryPriceManager manager = new MortuaryPriceManager();
						if (codeTextField.getText().equals("")) {  //$NON-NLS-1$
							JOptionPane.showMessageDialog(				
									null,
									MessageBundle.getMessage("angal.pricesothers.pleaseinsertacode"));  //$NON-NLS-1$
							return;
						}	
						if (descriptionTextField.getText().equals("")){
							JOptionPane.showMessageDialog(				
			                        null,
			                        MessageBundle.getMessage("angal.distype.pleaseinsertavaliddescription"),
			                        MessageBundle.getMessage("angal.hospital"),
			                        JOptionPane.PLAIN_MESSAGE);
							return;	
						}
						if(!checkMinDays()) {
							JOptionPane.showMessageDialog(				
			                        null,
			                        MessageBundle.getMessage("angal.distype.pleaseinsertavalidmindays"),
			                        MessageBundle.getMessage("angal.hospital"),
			                        JOptionPane.PLAIN_MESSAGE);
							return;	
						}
						if(!checkMaxDays()) {
							JOptionPane.showMessageDialog(				
			                        null,
			                        MessageBundle.getMessage("angal.distype.pleaseinsertavalidmaxdays"),
			                        MessageBundle.getMessage("angal.hospital"),
			                        JOptionPane.PLAIN_MESSAGE);
							return;	
						}
						
						int min = Integer.parseInt(minDaysTextField.getText());
						int max = Integer.parseInt(maxDaysTextField.getText());
						if(max <= min) {
							JOptionPane.showMessageDialog(				
			                        null,
			                        MessageBundle.getMessage("angal.distype.pleaseinsertavalidmaxdays"),
			                        MessageBundle.getMessage("angal.hospital"),
			                        JOptionPane.PLAIN_MESSAGE);
							return;	
						}
						if(!manager.isPriceRangeCoherent(id, min, max)) {
							JOptionPane.showMessageDialog(				
			                        null,
			                        MessageBundle.getMessage("angal.distype.pleaseinsertacoherentvalue"),
			                        MessageBundle.getMessage("angal.hospital"),
			                        JOptionPane.PLAIN_MESSAGE);
							return;	
						}
						pOther = new PricesOthers();
						pOther.setCode(codeTextField.getText());
						pOther.setDescription(descriptionTextField.getText()+" "+MessageBundle.getMessage("angal.mortuarybrowser.from")+" "+min+" "+MessageBundle.getMessage("angal.mortuarybrowser.to")+" "+max+" "+MessageBundle.getMessage("angal.mortuarybrowser.days"));
						pOther.setOpdInclude(false);
						pOther.setIpdInclude(true);
						pOther.setDaily(true);
						pOther.setDischarge(false);
						pOther.setUndefined(false);
						pOther.setAccount("");
						
						plage.setCode(codeTextField.getText());
						plage.setDescription(descriptionTextField.getText());
						plage.setNbJourmin(min);
						plage.setNbJourMax(max);
						boolean result = false;
						boolean result1 = false;
						PricesOthersManager pOtherManager = new PricesOthersManager();
						if (insert) {  // inserting
							result = manager.newPrice(plage);
							result1 = pOtherManager.newOther(pOther);
							if (result) {
								fireMortuaryPriceInserted();
	                        }
							if (!result || !result1) JOptionPane.showMessageDialog(null,  MessageBundle.getMessage("angal.distype.thedatacouldnotbesaved"));
		                    else  dispose();
	                    }
	                    else {  // updating
	    						result = manager.updatePrice(plage);
	    						result1 = pOtherManager.updateOtherByCode(pOther)? true : pOtherManager.newOther(pOther);
	    						
							if (result) {
								fireMortuaryPriceUpdated();
	                        }
							if (!result) JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.distype.thedatacouldnotbesaved"));
	                        else  dispose();
	    					}
	                    	
						}
						
	                
				});
			}
			return okButton;
		}

		/**
		 * This method initializes descriptionTextField	
		 * 	
		 * @return javax.swing.JTextField	
		 */
		private JTextField getDescriptionTextField() {
			if (descriptionTextField == null) {
				descriptionTextField = new JTextField(20);
				if (!insert) {
					descriptionTextField.setText(plage.getDescription());
					lastdescription=plage.getDescription();
				} 
			}
			return descriptionTextField;
		}
		private JTextField getMinDaysTextField() {
			if (minDaysTextField == null) {
				minDaysTextField = new JTextField(20);
				if (!insert) {
					minDaysTextField.setText(plage.getNbJourmin()+"");
					lastMinDays= plage.getNbJourmin();
				} 
			}
			return minDaysTextField;
		}
		private JTextField getJTextFieldCode() {
			if (codeTextField == null) {
				codeTextField = new VoLimitedTextField(10);
				codeTextField.setText(insert? "" : plage.getCode()); //$NON-NLS-1$
			}
			if(!insert) codeTextField.setEditable(false);
			
			return codeTextField;
		}

		private JLabel getJLabelCode() {
			if (jLabelCode == null) {
				jLabelCode = new JLabel();
				jLabelCode.setText("Code");
				jLabelCode.setSize(50, 10);
			}
			return jLabelCode;
		}

		private JPanel getJPanelCode() {
			if (jPanelCode == null) {
				jPanelCode = new JPanel();
				jPanelCode.setLayout(new GridLayout(2, 2));
				jPanelCode.add(getJLabelCode());
				jPanelCode.add(getJTextFieldCode());
			}
			return jPanelCode;
		}
		private JTextField getMaxDaysTextField() {
			if (maxDaysTextField == null) {
				maxDaysTextField = new JTextField(20);
				if (!insert) {
					maxDaysTextField.setText(plage.getNbJourMax()+"");
					lastMaxDays= plage.getNbJourMax();
				} 
			}
			return maxDaysTextField;
		}
		
		
		

		/**
		 * This method initializes jDataPanel	
		 * 	
		 * @return javax.swing.JPanel	
		 */
		private JPanel getJDataPanel() {
			if (jDataPanel == null) {
				jDataPanel = new JPanel();
				jDataPanel.setLayout(new BoxLayout(getJDataPanel(),BoxLayout.Y_AXIS));				
				jDataPanel.add(getJPanelCode(), null);
				jDataPanel.add(getJDescriptionLabelPanel(), null);
				jDataPanel.add(getDescriptionTextField(), null);
				jDataPanel.add(getJMinDaysLabelPanel(), null);
				jDataPanel.add(getMinDaysTextField(), null);
				jDataPanel.add(getJMaxDaysLabelPanel(), null);
				jDataPanel.add(getMaxDaysTextField(), null);
			}
			return jDataPanel;
		}

		/**
		 * This method initializes jCodeLabel	
		 * 	
		 * @return javax.swing.JLabel	
		 */
		private JLabel getJMinDaysLabel() {
			if (jMinDaysLabel == null) {
				jMinDaysLabel = new JLabel();
				jMinDaysLabel.setText(MessageBundle.getMessage("angal.mortuary.mindays"));
			}
			return jMinDaysLabel;
		}
		
		private JLabel getJMaxDaysLabel() {
			if (jMaxDaysLabel == null) {
				jMaxDaysLabel = new JLabel();
				jMaxDaysLabel.setText(MessageBundle.getMessage("angal.mortuary.maxdays"));
			}
			return jMaxDaysLabel;
		}
		

		/**
		 * This method initializes jMinDaysLabelPanel	
		 * 	
		 * @return javax.swing.JPanel	
		 */
		private JPanel getJMinDaysLabelPanel() {
			if (jMinDaysLabelPanel == null) {
				jMinDaysLabelPanel = new JPanel();
				jMinDaysLabelPanel.add(getJMinDaysLabel(), BorderLayout.CENTER);
			}
			return jMinDaysLabelPanel;
		}
		private JPanel getJMaxDaysLabelPanel() {
			if (jMaxDaysLabelPanel == null) {
				jMaxDaysLabelPanel = new JPanel();
				jMaxDaysLabelPanel.add(getJMaxDaysLabel(), BorderLayout.CENTER);
			}
			return jMaxDaysLabelPanel;
		}		

		/**
		 * This method initializes jDescriptionLabelPanel	
		 * 	
		 * @return javax.swing.JPanel	
		 */
		private JPanel getJDescriptionLabelPanel() {
			if (jDescriptionLabelPanel == null) {
				jDescripitonLabel = new JLabel();
				jDescripitonLabel.setText(MessageBundle.getMessage("angal.distype.description"));
				jDescriptionLabelPanel = new JPanel();
				jDescriptionLabelPanel.add(jDescripitonLabel, null);
			}
			return jDescriptionLabelPanel;
		}
		
	private boolean checkMinDays() {
		int m = 0;
			try {
				m = Integer.parseInt(minDaysTextField.getText());						    
			} catch (NumberFormatException e1) {
			return false;						
			}
			if(m <= 0) {return false;}
			return true;							
		}
	private boolean checkMaxDays() {
		int m = 0;
		try {
			m = Integer.parseInt(maxDaysTextField.getText());						    
		} catch (NumberFormatException e1) {
		return false;						
		}
		if(m <= 0) {return false;}
		return true;							
	}

}



