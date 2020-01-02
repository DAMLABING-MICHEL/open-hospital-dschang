package org.isf.accounting.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.EventListenerList;

import org.isf.accounting.gui.PatientBillEdit.BillItemEditListener;
import org.isf.accounting.model.BillItems;
import org.isf.generaldata.MessageBundle;
import org.isf.patient.gui.SelectPatient.SelectionListener;
import org.isf.patient.model.Patient;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import javax.swing.SwingConstants;

public class BillItemEdit extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField txtQty;
	private JTextField txtPrice;

	private EventListenerList billItemEditListeners = new EventListenerList();
	private BillItems billItem;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			BillItemEdit dialog = new BillItemEdit(null, null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addListener(BillItemEditListener listener) {
		billItemEditListeners.add(BillItemEditListener.class, listener);
	}

	private void fireBillItemEdited() {
		AWTEvent event = new AWTEvent(billItem, AWTEvent.RESERVED_ID_MAX + 1) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = billItemEditListeners.getListeners(BillItemEditListener.class);
		for (int i = 0; i < listeners.length; i++)
			((BillItemEditListener) listeners[i]).billItemEdited(event);
	}

	/**
	 * Create the dialog.
	 */
	public BillItemEdit(JDialog owner, BillItems item) {
		super(owner, true);
		if (item == null) {
			item = new BillItems();
		}
		this.billItem = item;
		setResizable(false);
		setTitle(MessageBundle.getMessage("angal.patientbill.billitem.edition"));
		setBounds(100, 100, 450, 279);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.CENTER);
			panel.setLayout(null);
			{
				JLabel lblType = new JLabel(MessageBundle.getMessage("angal.patientbill.billitem.edition.type"));
				lblType.setFont(new Font("Tahoma", Font.BOLD, 13));
				lblType.setBounds(12, 27, 56, 16);
				panel.add(lblType);
			}

			JLabel lblDescription = new JLabel(MessageBundle.getMessage("angal.agetype.description"));
			lblDescription.setFont(new Font("Tahoma", Font.BOLD, 13));
			lblDescription.setBounds(12, 56, 87, 16);
			panel.add(lblDescription);

			JLabel lblQty = new JLabel(MessageBundle.getMessage("angal.medicalstock.multiplecharging.quantity"));
			lblQty.setFont(new Font("Tahoma", Font.BOLD, 13));
			lblQty.setBounds(12, 85, 87, 16);
			panel.add(lblQty);

			JLabel lblPrice = new JLabel(MessageBundle.getMessage("angal.patientbill.billitem.edition.unitprice"));
			lblPrice.setFont(new Font("Tahoma", Font.BOLD, 13));
			lblPrice.setBounds(12, 114, 87, 16);
			panel.add(lblPrice);

			JLabel lblTypeValue = new JLabel(item.getItemGroup());
			lblTypeValue.setBounds(129, 27, 281, 16);
			panel.add(lblTypeValue);

			JLabel lblDescValue = new JLabel(item.getItemDescription());
			lblDescValue.setBounds(129, 56, 281, 16);
			panel.add(lblDescValue);

			txtQty = new JTextField();
			txtQty.setText(String.valueOf(this.billItem.getItemQuantity()));
			txtQty.setHorizontalAlignment(SwingConstants.RIGHT);
			txtQty.setBounds(129, 82, 281, 22);
			panel.add(txtQty);
			txtQty.setColumns(10);

			txtPrice = new JTextField();
			txtPrice.setText(String.valueOf(this.billItem.getItemAmount()));
			txtPrice.setHorizontalAlignment(SwingConstants.RIGHT);
			txtPrice.setColumns(10);
			txtPrice.setBounds(129, 111, 281, 22);
			panel.add(txtPrice);
		}
		{
			JPanel panel = new JPanel();
			FlowLayout flowLayout = (FlowLayout) panel.getLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			contentPanel.add(panel, BorderLayout.NORTH);
			{
				JLabel lblBillItemEdition = new JLabel(
						MessageBundle.getMessage("angal.patientbill.billitem.edition.title"));
				panel.add(lblBillItemEdition);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton(MessageBundle.getMessage("angal.common.ok"));
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						String strPrice=txtPrice.getText();
						String strQty=txtQty.getText();
						BillItemEdit.this.billItem.setItemAmount(Double.parseDouble(strPrice));
						BillItemEdit.this.billItem.setItemQuantity(Double.parseDouble(strQty));
						
						fireBillItemEdited();
						
						BillItemEdit.this.setVisible(false);
						
					}
				});
			}
			{
				JButton cancelButton = new JButton(MessageBundle.getMessage("angal.common.cancel"));
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				cancelButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						BillItemEdit.this.setVisible(false);
					}
				});
			}
		}
		setLocationRelativeTo(null);
		
//		if(item.getId()>0){
//			txtPrice.setEnabled(false);
//			txtQty.setEnabled(false);
//		}
	}
}
