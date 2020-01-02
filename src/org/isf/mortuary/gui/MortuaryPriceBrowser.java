package org.isf.mortuary.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.isf.generaldata.MessageBundle;
import org.isf.mortuary.gui.MortuaryPriceEdit.MortuaryPriceListener;

import org.isf.mortuary.model.PlagePrixMorgue;
import org.isf.pricesothers.manager.PricesOthersManager;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.mortuary.manager.MortuaryPriceManager;

public class MortuaryPriceBrowser extends ModalJFrame implements MortuaryPriceListener{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private ArrayList<PlagePrixMorgue> pPlage;
		private String[] pColums = { MessageBundle.getMessage("angal.distype.codem"),MessageBundle.getMessage("angal.mortuary.mindays"), MessageBundle.getMessage("angal.mortuary.maxdays"), MessageBundle.getMessage("angal.distype.descriptionm")};
		private int[] pColumwidth = {80, 100, 100, 100, 200 };
		private JPanel jContainPanel = null;
		private JPanel jButtonPanel = null;
		private JButton jNewButton = null;
		private JButton jEditButton = null;
		private JButton jCloseButton = null;
		private JButton jDeteleButton = null;
		private JTable jTable = null;
		private MortuaryPriceBrowserModel model;
		private int selectedrow;
		private PlagePrixMorgue plage = null;
		private final JFrame myFrame;	
		private MortuaryPriceManager manager = new MortuaryPriceManager();
		PricesOthersManager pOtherManager = new PricesOthersManager();
		
		/**
		 * This method initializes 
		 * 
		 */
		public MortuaryPriceBrowser() {
			super();
			myFrame=this;
			
			initialize();
			setVisible(true);
		}
		
		
		private void initialize() {
			Toolkit kit = Toolkit.getDefaultToolkit();
			Dimension screensize = kit.getScreenSize();
			final int pfrmBase = 10;
	        final int pfrmWidth = 5;
	        final int pfrmHeight =4;
	        this.setBounds((screensize.width - screensize.width * pfrmWidth / pfrmBase ) / 2, (screensize.height - screensize.height * pfrmHeight / pfrmBase)/2, 
	                screensize.width * pfrmWidth / pfrmBase, screensize.height * pfrmHeight / pfrmBase);
			this.setTitle(MessageBundle.getMessage("angal.plageprix.plageprixtitle"));
			this.setContentPane(getJContainPanel());
			//pack();	
		}
		
		
		private JPanel getJContainPanel() {
			if (jContainPanel == null) {
				jContainPanel = new JPanel();
				jContainPanel.setLayout(new BorderLayout());
				jContainPanel.add(getJButtonPanel(), java.awt.BorderLayout.SOUTH);
				jContainPanel.add(new JScrollPane(getJTable()),
						java.awt.BorderLayout.CENTER);
				validate();
			}
			return jContainPanel;
		}
		
		private JPanel getJButtonPanel() {
			if (jButtonPanel == null) {
				jButtonPanel = new JPanel();
				jButtonPanel.add(getJNewButton(), null);
				jButtonPanel.add(getJEditButton(), null);
				jButtonPanel.add(getJDeteleButton(), null);
				jButtonPanel.add(getJCloseButton(), null);
			}
			return jButtonPanel;
		}
		
		
		private JButton getJNewButton() {
			if (jNewButton == null) {
				jNewButton = new JButton();
				jNewButton.setText(MessageBundle.getMessage("angal.common.new"));
				jNewButton.setMnemonic(KeyEvent.VK_N);
				jNewButton.addActionListener(new ActionListener() {
					
					public void actionPerformed(ActionEvent event) {
						plage = new PlagePrixMorgue(0, 0, 0, 0, "", "");
						MortuaryPriceEdit newrecord = new MortuaryPriceEdit(myFrame, plage, true);
						newrecord.addMortuaryPriceListener(MortuaryPriceBrowser.this);
						newrecord.setVisible(true);
					}
				});
			}
			return jNewButton;
		}
		
		/**
		 * This method initializes jEditButton	
		 * 	
		 * @return javax.swing.JButton	
		 */
		private JButton getJEditButton() {
			if (jEditButton == null) {
				jEditButton = new JButton();
				jEditButton.setText(MessageBundle.getMessage("angal.common.edit"));
				jEditButton.setMnemonic(KeyEvent.VK_E);
				jEditButton.addActionListener(new ActionListener() {
					
					public void actionPerformed(ActionEvent event) {
						if (jTable.getSelectedRow() < 0) {
							JOptionPane.showMessageDialog(null,
									MessageBundle.getMessage("angal.common.pleaseselectarow"), MessageBundle.getMessage("angal.hospital"),
									JOptionPane.PLAIN_MESSAGE);
							return;
						} else {
							selectedrow = jTable.getSelectedRow();
							plage = (PlagePrixMorgue) (((MortuaryPriceBrowserModel) model).getValueAt(selectedrow, -1));
							MortuaryPriceEdit newrecord = new MortuaryPriceEdit(myFrame, plage, false);
							newrecord.addMortuaryPriceListener(MortuaryPriceBrowser.this);
							newrecord.setVisible(true);
						}
					}
				});
			}
			return jEditButton;
		}
		
		/**
		 * This method initializes jCloseButton	
		 * 	
		 * @return javax.swing.JButton	
		 */
		private JButton getJCloseButton() {
			if (jCloseButton == null) {
				jCloseButton = new JButton();
				jCloseButton.setText(MessageBundle.getMessage("angal.common.close"));
				jCloseButton.setMnemonic(KeyEvent.VK_C);
				jCloseButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
			}
			return jCloseButton;
		}
		
		/**
		 * This method initializes jDeteleButton	
		 * 	
		 * @return javax.swing.JButton	
		 */
		private JButton getJDeteleButton() {
			
			if (jDeteleButton == null) {
				jDeteleButton = new JButton();
				jDeteleButton.setText(MessageBundle.getMessage("angal.common.delete"));
				jDeteleButton.setMnemonic(KeyEvent.VK_D);
				jDeteleButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						
						if (jTable.getSelectedRow() < 0) {
							JOptionPane.showMessageDialog(null,
									MessageBundle.getMessage("angal.common.pleaseselectarow"), MessageBundle.getMessage("angal.hospital"),
									JOptionPane.PLAIN_MESSAGE);
							return;
						} else {
							PlagePrixMorgue dis = 
									(PlagePrixMorgue) (((MortuaryPriceBrowserModel) model).getValueAt(jTable.getSelectedRow(), -1));
							int n = JOptionPane.showConfirmDialog(null,
									MessageBundle.getMessage("angal.plageprix.deleteplageprix") + " \" "+dis.getDescription() + "\" ?",
									MessageBundle.getMessage("angal.hospital"), JOptionPane.YES_NO_OPTION);
							
							if ((n == JOptionPane.YES_OPTION) && (manager.deletePrice(dis))) {								
								pPlage.remove(jTable.getSelectedRow());
								model.fireTableDataChanged();
								jTable.updateUI();
								pOtherManager.deleteOther(dis.getCode());
								
							}
						}
					}
					
				});
			}
			return jDeteleButton;
		}
		
		public JTable getJTable() {
			if (jTable == null) {
				model = new MortuaryPriceBrowserModel();
				jTable = new JTable(model);
				jTable.getColumnModel().getColumn(0).setMinWidth(pColumwidth[0]);
				jTable.getColumnModel().getColumn(1).setMinWidth(pColumwidth[1]);
				jTable.getColumnModel().getColumn(2).setMinWidth(pColumwidth[2]);
				jTable.getColumnModel().getColumn(3).setMinWidth(pColumwidth[3]);

				
			}return jTable;
		}		
		
		class MortuaryPriceBrowserModel extends DefaultTableModel {
			
			private static final long serialVersionUID = 1L;

			public MortuaryPriceBrowserModel() {
				MortuaryPriceManager manager = new MortuaryPriceManager();
				pPlage = manager.getPrices();
			}
			
			public int getRowCount() {
				if (pPlage == null)
					return 0;
				return pPlage.size();
			}
			
			public String getColumnName(int c) {
				return pColums[c];
			}

			public int getColumnCount() {
				return pColums.length;
			}

			public Object getValueAt(int r, int c) {
				if (c == -1) {
					return pPlage.get(r);
				}else if (c == 0) {
					return pPlage.get(r).getCode();
				}else if (c == 1) {
					return pPlage.get(r).getNbJourmin();
				}
				else if (c == 2) {
					return pPlage.get(r).getNbJourMax();
				}
				else if (c == 3) {
					return pPlage.get(r).getDescription();
				}
				return null;
			}
			
			@Override
			public boolean isCellEditable(int arg0, int arg1) {
				return false;
			}
		}

		@Override
		public void mortuaryPriceUpdated(AWTEvent e) {
			/*pPlage.set(selectedrow, plage);
			((MortuaryPriceBrowserModel) jTable.getModel()).fireTableDataChanged();
			jTable.updateUI();
			if ((jTable.getRowCount() > 0) && selectedrow > -1)
				jTable.setRowSelectionInterval(selectedrow, selectedrow);
			repaint();*/
			jTable.setModel(new MortuaryPriceBrowserModel());
		}

		@Override
		public void mortuaryPriceInserted(AWTEvent e) {
			 jTable.setModel(new MortuaryPriceBrowserModel());
	}


	}



