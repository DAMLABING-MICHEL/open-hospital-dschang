package org.isf.reduction.gui;

import java.awt.AWTEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.isf.generaldata.MessageBundle;
import org.isf.reduction.gui.ReductionPlanEdit.ReductionPlanListener;
import org.isf.reduction.manager.ReductionPlanManager;
import org.isf.reduction.model.ReductionPlan;
import org.isf.utils.exception.OHException;
import org.isf.utils.jobjects.ModalJFrame;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;

public class ReductionPlanBrowser extends ModalJFrame implements
		ReductionPlanListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	private JScrollPane scrollPane;
	private JButton jNewButton;
	private JButton jEditButton;
	private JButton jDeteleButton;
	private JButton jCloseButton;
	private String[] pbiColumn = new String[] {
			MessageBundle.getMessage("angal.common.code"),
			MessageBundle.getMessage("angal.common.description"),
			MessageBundle.getMessage("angal.reduction.medicalrate"),
			MessageBundle.getMessage("angal.reduction.examrate"),
			MessageBundle.getMessage("angal.reduction.operate"),
			MessageBundle.getMessage("angal.reduction.otherrate") };
	private int[] pColumwidth = { 80, 200, 80, 80, 80, 80 };
	
	private ArrayList<ReductionPlan> reductionPlanList;
	private int selectedrow;
	private ReductionPlan reductionPlan;
	ReductionPlanManager manager = new ReductionPlanManager();
	private JPanel buttonPanel;


	/**
	 * Create the frame.
	 */
	public ReductionPlanBrowser() {

		initialize();
	}

	private void initialize() {
		setTitle("Patient billing informations browser");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 620, 300);
		setContentPane(getMainContentPane());
		setLocationRelativeTo(null);
	}

	private JPanel getMainContentPane() {
		try {
			if (contentPane == null) {
				contentPane = new JPanel();
			}
			// contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			contentPane.setLayout(new BorderLayout());

			scrollPane = new JScrollPane();
			contentPane.add(scrollPane, BorderLayout.CENTER);

			table = new JTable();
			table.setModel(new ReductionPlanModel());

			for (int i=0;i<pbiColumn.length; i++){
				table.getColumnModel().getColumn(i).setMinWidth(pColumwidth[i]);
				table.getColumnModel().getColumn(i).setMaxWidth(pColumwidth[i]);
			}
			
			scrollPane.setViewportView(table);
			contentPane.add(getButtonPane(), BorderLayout.SOUTH);
			return contentPane;
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}

	}

	private JPanel getButtonPane() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getJNewButton());
			buttonPanel.add(getJEditButton());
			buttonPanel.add(getJDeteleButton());
			buttonPanel.add(getJCloseButton());
		}
		return buttonPanel;
	}

	private JButton getJNewButton() {
		if (jNewButton == null) {
			jNewButton = new JButton();
			jNewButton.setText(MessageBundle.getMessage("angal.common.new"));
			jNewButton.setMnemonic(KeyEvent.VK_N);
			jNewButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					reductionPlan=new ReductionPlan();
					ReductionPlanEdit editPane=new ReductionPlanEdit(reductionPlan, true);
					editPane.addReductionPlanListener(ReductionPlanBrowser.this);
					editPane.setVisible(true);
				}
			});
		}
		return jNewButton;
	}

	private JButton getJCloseButton() {
		if (jCloseButton == null) {
			jCloseButton = new JButton();
			jCloseButton
					.setText(MessageBundle.getMessage("angal.common.close"));
			jCloseButton.setMnemonic(KeyEvent.VK_C);
			jCloseButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dispose();
				}
			});
		}
		return jCloseButton;
	}

	private JButton getJDeteleButton() {
		if (jDeteleButton == null) {
			jDeteleButton = new JButton();
			jDeteleButton.setText(MessageBundle
					.getMessage("angal.common.delete"));
			jDeteleButton.setMnemonic(KeyEvent.VK_D);
			jDeteleButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					try {
						if (table.getSelectedRow() < 0) {
							JOptionPane
									.showMessageDialog(
											ReductionPlanBrowser.this,
											MessageBundle
													.getMessage("angal.common.pleaseselectarow"),
											MessageBundle
													.getMessage("angal.hospital"),
											JOptionPane.PLAIN_MESSAGE);
							return;
						} else {
							selectedrow = table.getSelectedRow();
							reductionPlan = (ReductionPlan) (((ReductionPlanModel) table
									.getModel()).getValueAt(selectedrow, -1));
							int n = JOptionPane.showConfirmDialog(
									null,
									MessageBundle
											.getMessage("angal.reduction.deletereductionplan")
											+ " \" "
											+ reductionPlan
													.getDescription() + "\" ?",
									MessageBundle.getMessage("angal.hospital"),
									JOptionPane.YES_NO_OPTION);
							if ((n == JOptionPane.YES_OPTION)) {

								boolean canDelete = manager
										.canDelete(reductionPlan);
								boolean forceDelete = false;
								if (!canDelete) {
									n = JOptionPane
											.showConfirmDialog(
													null,
													MessageBundle
															.getMessage("angal.reduction.deletereductionplanused")
															+ " \" "
															+ reductionPlan
																	.getDescription()
															+ "\" ?",
													MessageBundle
															.getMessage("angal.hospital"),
													JOptionPane.YES_NO_OPTION);
									if (n == JOptionPane.YES_OPTION) {
										canDelete = true;
										forceDelete = true;
									}
								}
								if (canDelete
										&& manager
												.deleteReductionPlan(
														reductionPlan,
														forceDelete)) {
									reductionPlanList.remove(table.getSelectedRow());
								}
								((ReductionPlanModel) table.getModel()).fireTableDataChanged();
								table.updateUI();
							}
						}
					} catch (OHException e) {
						JOptionPane.showMessageDialog(null, e.getMessage());
					}
				}
			});
		}
		return jDeteleButton;
	}

	private JButton getJEditButton() {
		if (jEditButton == null) {
			jEditButton = new JButton();
			jEditButton.setText(MessageBundle.getMessage("angal.common.edit"));
			jEditButton.setMnemonic(KeyEvent.VK_E);
			jEditButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					try {
						if (table.getSelectedRow() < 0) {
							JOptionPane
									.showMessageDialog(
											ReductionPlanBrowser.this,
											MessageBundle
													.getMessage("angal.common.pleaseselectarow"),
											MessageBundle
													.getMessage("angal.hospital"),
											JOptionPane.PLAIN_MESSAGE);
							return;
						} else {
							selectedrow = table.getSelectedRow();
							reductionPlan = (ReductionPlan) (((ReductionPlanModel) table
									.getModel()).getValueAt(selectedrow, -1));
							ReductionPlanEdit editPane=new ReductionPlanEdit(reductionPlan, false);
							editPane.addReductionPlanListener(ReductionPlanBrowser.this);
							editPane.setVisible(true);
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, e.getMessage());
					}
				}
			});
		}
		return jEditButton;
	}

	private class ReductionPlanModel extends DefaultTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ReductionPlanModel() throws OHException {
			ReductionPlanManager manager = new ReductionPlanManager();
			reductionPlanList = manager.getReductionPlans();
		}

		public int getRowCount() {
			if (reductionPlanList == null)
				return 0;
			return reductionPlanList.size();
		}

		public String getColumnName(int c) {
			return pbiColumn[c];
		}

		public int getColumnCount() {
			return pbiColumn.length;
		}

		// { "CODE", "DESCRIPTION","MEDICALRATE","EXAMRATE","OPERATIONRATE",
		// "OTHERRATE"};
		public Object getValueAt(int r, int c) {
			if (c == 0) {
				return reductionPlanList.get(r).getId();
			} else if (c == -1) {
				return reductionPlanList.get(r);
			} else if (c == 1) {
				return reductionPlanList.get(r).getDescription();
			} else if (c == 2) {
				return reductionPlanList.get(r).getMedicalRate();
			} else if (c == 3) {
				return reductionPlanList.get(r).getExamRate();
			} else if (c == 4) {
				return reductionPlanList.get(r).getOperationRate();
			} else if (c == 5) {
				return reductionPlanList.get(r).getOtherRate();
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			// return super.isCellEditable(arg0, arg1);
			return false;
		}
	}

	@Override
	public void pbiInserted(AWTEvent aEvent) {
		try {
			reductionPlanList = manager.getReductionPlans();
			((ReductionPlanModel) table.getModel()).fireTableDataChanged();
			table.updateUI();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(ReductionPlanBrowser.this,
					e.getMessage());
		}

	}
}
