package org.isf.accounting.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.event.EventListenerList;
import javax.swing.table.AbstractTableModel;

import org.isf.accounting.gui.PatientBillEdit.BillItemEditListener;
import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItemListItem;
import org.isf.accounting.model.BillItemPayments;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.generaldata.MessageBundle;
import org.isf.patient.gui.SelectPatient.SelectionListener;
import org.isf.patient.model.Patient;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import javax.swing.SwingConstants;

abstract class BillItemPaymentDialog {
	
	public static void initComponent(JDialog dialog, JTable jTableItemPayment, JPanel jPanelButtonsItemPaymentActions) {
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
		contentPanel.setSize(600, 350);
		contentPanel.add(jTableItemPayment.getTableHeader(), BorderLayout.PAGE_START);
		JScrollPane contentScrollPane = new JScrollPane();
		contentScrollPane.setSize(600, 310);
		contentScrollPane.setViewportView(jTableItemPayment);
		jPanelButtonsItemPaymentActions.setSize(600, 40);
		contentPanel.add(contentScrollPane);
		contentPanel.add(jPanelButtonsItemPaymentActions);
		dialog.setContentPane(contentPanel);
		dialog.setModal(true);
		dialog.setTitle("angal.accounting.edititemtopay");
		dialog.setLocationRelativeTo(null);
		dialog.setSize(650, 400);
		dialog.setLocationRelativeTo(null);
		dialog.setModal(true);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
	}
}

class BillItemListRenderer extends JCheckBox implements ListCellRenderer {
  public Component getListCellRendererComponent(JList list, Object value,
      int index, boolean isSelected, boolean hasFocus) {
    setEnabled(list.isEnabled());
    setSelected(((BillItemListItem) value).isSelected());
    setFont(list.getFont());
    setBackground(list.getBackground());
    setForeground(list.getForeground());
    setText(value.toString());
    return this;
  }
}

class BillItemPaymentTableModel extends AbstractTableModel
{
    private final List<BillItemListItem> data;
    private ArrayList<BillPayments> payItems;
    private ArrayList<BillItems> billItems;
    private Bill bill;
    private BillBrowserManager billManager = new BillBrowserManager();
    private Double paymentTotal = 0.0;
     
    private final String[] columnNames = new String[] {
            "Id", "Item Description", "To Pay", "Paid Amount", "Pay Amount", "Pay"
    };
    private JDialog owner;
    private final Class[] columnClass = new Class[] {
        Integer.class, String.class, Double.class, Double.class, Double.class, Boolean.class
    };
 
    public BillItemPaymentTableModel(
    		ArrayList<BillItemListItem> data, JDialog owner, double paymentTotal
    ){
        this.data = data;
        this.owner = owner;
        BillItemListItem.setPayAmount(data, paymentTotal);
        this.paymentTotal = paymentTotal;
    }
     
    @Override
    public String getColumnName(int column)
    {
        return columnNames[column];
    }
 
    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        return columnClass[columnIndex];
    }
 
    @Override
    public int getColumnCount()
    {
        return columnNames.length;
    }
 
    @Override
    public int getRowCount()
    {
        return data.size();
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
    	if(columnIndex == 4 || columnIndex == 5) {
    		return true;
    	} else return false;
    }
 
    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        BillItemListItem row = data.get(rowIndex);
        if(0 == columnIndex) {
            return row.getId();
        }
        else if(1 == columnIndex) {
            return row.getItemDescription();
        }
        else if(2 == columnIndex) {
            return row.getToPay();
        }
        else if(3 == columnIndex) {
            return row.getPaidAmount();
        }
        else if(4 == columnIndex) {
            return row.getPayAmount();
        }
        else if(5 == columnIndex) {
            return row.isSelected();
        }
        return null;
    }
    
    
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    	BillItemListItem row = data.get(rowIndex);
	    if(4 == columnIndex) {
	    	if(false) {//(Double) aValue != 3500
	    		JOptionPane.showMessageDialog(owner,
					MessageBundle.getMessage("angal.newbill.payementinthefuturenotallowed"), //$NON-NLS-1$
					MessageBundle.getMessage("angal.newbill.invaliddate"), //$NON-NLS-1$
					JOptionPane.PLAIN_MESSAGE);
	    	} else {
	    		row.setPayAmount((Double) aValue);
	    		row.setToPay((row.getItemAmount()*row.getItemQuantity()) - row.getPaidAmount());
	    	}
            
        }
        else if(5 == columnIndex) {
            row.setSelected((boolean) aValue);
        }
    }
}
