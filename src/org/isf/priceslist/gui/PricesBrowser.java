package org.isf.priceslist.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.isf.exa.manager.ExamBrowsingManager;
import org.isf.exa.model.Exam;
import org.isf.generaldata.MessageBundle;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.operation.manager.OperationBrowserManager;
import org.isf.operation.model.Operation;
import org.isf.priceslist.manager.PriceListManager;
import org.isf.priceslist.model.ItemGroup;
import org.isf.priceslist.model.List;
import org.isf.priceslist.model.Price;
import org.isf.pricesothers.manager.PricesOthersManager;
import org.isf.pricesothers.model.PricesOthers;
import org.isf.serviceprinting.manager.PrintManager;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.utils.jobjects.OhTableModel;
import org.isf.utils.treetable.JTreeTable;
import javax.swing.JTextField;
import javax.swing.JTree;

/**
 * Browsing of table PriceList
 * 
 * @author Alessandro
 * 
 */

public class PricesBrowser extends ModalJFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jPanelNorth;
	private JComboBox jComboBoxLists;
	private JScrollPane jScrollPaneList;
	private JTreeTable jTreeTable;
	private JPanel jPanelButtons;
	private JButton jButtonSave;
	private JButton jButtonCancel;
	private JLabel jLabelDescription;
	private JPanel jPanelSelection;
	private JPanel jPanelConfig;
	private JButton jButtonManage;
	private JButton jPrintTableButton;
	private JPanel jPanelDescription;
	String searchQuery="";
//    static protected String[] cCategories = {"EXA","OPE","MED","OTH"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//    static protected String[] cCategoriesNames = {MessageBundle.getMessage("angal.priceslist.exams"),MessageBundle.getMessage("angal.priceslist.operations"),MessageBundle.getMessage("angal.priceslist.medicals"),MessageBundle.getMessage("angal.priceslist.others")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    private boolean[] columsResizable = {true, false};
	private int[] columWidth = {400,150};
    
	private PriceListManager listManager = new PriceListManager();
	private ArrayList<List> listArray = listManager.getLists();
	private ArrayList<Price> priceArray = listManager.getPrices();
	private List listSelected;
	
	private PriceNode examNodes;
	private ExamBrowsingManager examManager = new ExamBrowsingManager();
    private ArrayList<Exam> examArray = examManager.getExams();
    
    private PriceNode opeNodes;
    private OperationBrowserManager operManager = new OperationBrowserManager();
    private ArrayList<Operation> operArray = operManager.getOperation();
       
    private PriceNode medNodes;
    private MedicalBrowsingManager mediManager = new MedicalBrowsingManager();
    private ArrayList<Medical> mediArray = mediManager.getMedicals();
    
    private PriceNode othNodes;
    private PricesOthersManager othManager = new PricesOthersManager();
    private ArrayList<PricesOthers> othArray = othManager.getOthers();
    private JTextField researchField;
	
	public PricesBrowser() {
		initComponents();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void initComponents() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
		setForeground(Color.black);
		checkLists();
		getContentPane().add(getJPanelNorth(), BorderLayout.NORTH);
		getContentPane().add(getJScrollPaneList(), BorderLayout.CENTER);
		getContentPane().add(getJPanelButtons(), BorderLayout.SOUTH);
		setTitle(MessageBundle.getMessage("angal.priceslist.pricesbrowser"));
		setSize(647, 440);
	}

	private void checkLists() {
		if (listArray.isEmpty()) {
			JOptionPane.showMessageDialog(null, 
						MessageBundle.getMessage("angal.priceslist.pleasecreatealistfirst"),
						MessageBundle.getMessage("angal.priceslist.nolist"),
						JOptionPane.OK_OPTION);
			ListBrowser browseList = new ListBrowser();
			browseList.setVisible(true);
			dispose();
		}
	}
	
	private JButton getPrintTableButton() {
		if (jPrintTableButton == null) {
			jPrintTableButton = new JButton(MessageBundle.getMessage("angal.priceslist.printing"));
			jPrintTableButton.setMnemonic(KeyEvent.VK_P);
			jPrintTableButton.setVisible(true);
			jPrintTableButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					
					new PrintManager("PriceList", listManager.convertPrice(listSelected, priceArray), 0);
				}
			});
		}
		return jPrintTableButton;
	}

	private JButton getJButtonManage() {
		if (jButtonManage == null) {
			jButtonManage = new JButton();
			jButtonManage.setText(MessageBundle.getMessage("angal.priceslist.managelists")); //$NON-NLS-1$
			jButtonManage.setMnemonic(KeyEvent.VK_U);
			//jButtonManage.setEnabled(false);
			jButtonManage.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent event) {
						ListBrowser browseList = new ListBrowser();
						browseList.setVisible(true);
						dispose();						
				}
			});
			
		}
		return jButtonManage;
	}

	private JPanel getJPanelConfig() {
		if (jPanelConfig == null) {
			jPanelConfig = new JPanel();
			jPanelConfig.setLayout(new FlowLayout(FlowLayout.RIGHT));
			jPanelConfig.add(getJButtonManage());
		}
		return jPanelConfig;
	}

	protected void updateDescription() {
		jLabelDescription.setText(getTextDescription());
		
	}

	private JPanel getJPanelSelection() {
		if (jPanelSelection == null) {
			jPanelSelection = new JPanel();
			jPanelSelection.setLayout(new FlowLayout(FlowLayout.LEFT));
			jPanelSelection.add(getJComboBoxLists());
			jPanelSelection.add(getResearchField());
		}
		return jPanelSelection;
	}

	private JLabel getJLabelDescription() {
		if (jLabelDescription == null) {
			jLabelDescription = new JLabel();
			jLabelDescription.setText(getTextDescription());
		}
		return jLabelDescription;
	}

	private String getTextDescription() {
		String desc = listSelected.getDescription().toUpperCase()+
		  " ("+ //$NON-NLS-1$
		  listSelected.getCurrency()+")"; //$NON-NLS-1$
		return desc;
	}

	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton();
			jButtonCancel.setText(MessageBundle.getMessage("angal.common.cancel")); //$NON-NLS-1$
			jButtonCancel.setMnemonic(KeyEvent.VK_C);
			jButtonCancel.addActionListener(new ActionListener() {
	
				public void actionPerformed(ActionEvent event) {
						dispose();
				}
			});
		}
		return jButtonCancel;
	}

	private JButton getJButtonSave() {
		if (jButtonSave == null) {
			jButtonSave = new JButton();
			jButtonSave.setText(MessageBundle.getMessage("angal.common.savem")); //$NON-NLS-1$
			jButtonSave.setMnemonic(KeyEvent.VK_S);
			jButtonSave.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent event) {
					int option = JOptionPane.showConfirmDialog(null, 
							   MessageBundle.getMessage("angal.priceslist.thiswillsavecurrentpricescontinue"),  //$NON-NLS-1$
							   MessageBundle.getMessage("angal.priceslist.savelist"),  //$NON-NLS-1$
							   JOptionPane.OK_CANCEL_OPTION);

					if (option == 0) {
						
						ArrayList<Price> updateList = new ArrayList<Price>();
						updateList = convertTreeToArray();
						boolean updated = listManager.updatePrices(listSelected, updateList);
						
						if (updated) {
							JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.priceslist.listsaved")); //$NON-NLS-1$
							updateFromDB();
							PriceNode root = getTreeContent();
							jTreeTable.setModel(new PriceModel(root));
							jTreeTable.getTree().expandRow(3);
						    jTreeTable.getTree().expandRow(2);
						    jTreeTable.getTree().expandRow(1);
							validate();
							repaint();
							return;
							
						} else JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.priceslist.listcouldnotbesaved")); //$NON-NLS-1$
					
					} else return;
				}

				
			});

		}
		return jButtonSave;
	}

	private ArrayList<Price> convertTreeToArray() {
		
		ArrayList<Price> listPrices = new ArrayList<Price>();
		for (int i=0; i<examNodes.getItems().length; i++) {
			
			PriceNode newPriceNode = (PriceNode)examNodes.getItems()[i];
			listPrices.add(newPriceNode.getPrice());
		}
		for (int i=0; i<opeNodes.getItems().length; i++) {
			
			PriceNode newPriceNode = (PriceNode)opeNodes.getItems()[i];
			listPrices.add(newPriceNode.getPrice());
		}
		for (int i=0; i<medNodes.getItems().length; i++) {
			
			PriceNode newPriceNode = (PriceNode)medNodes.getItems()[i];
			listPrices.add(newPriceNode.getPrice());
		}
		for (int i=0; i<othNodes.getItems().length; i++) {
			
			PriceNode newPriceNode = (PriceNode)othNodes.getItems()[i];
			listPrices.add(newPriceNode.getPrice());
		}
		return listPrices;
	}

	private JPanel getJPanelButtons() {
		if (jPanelButtons == null) {
			jPanelButtons = new JPanel();
			jPanelButtons.add(getJButtonSave());
			jPanelButtons.add(getPrintTableButton());
			jPanelButtons.add(getJButtonCancel());
		}
		return jPanelButtons;
	}

	private JTreeTable getJTreeList() {
		if (jTreeTable == null) {
			
			updateFromDB();
		    PriceNode root = getTreeContent();
		    
		    jTreeTable = new JTreeTable(new PriceModel(root));
		    
		    jTreeTable.getTree().expandRow(4);
		    jTreeTable.getTree().expandRow(3);
		    jTreeTable.getTree().expandRow(2);
		    jTreeTable.getTree().expandRow(1);
		    
		    for (int i=0;i<columWidth.length; i++){
		    	jTreeTable.getColumnModel().getColumn(i).setMinWidth(columWidth[i]);
		    	
		    	if (!columsResizable[i]) jTreeTable.getColumnModel().getColumn(i).setMaxWidth(columWidth[i]);
			}
		    jTreeTable.setAutoCreateColumnsFromModel(false); 
		    
		}
		    
		return jTreeTable;
	}

	private void updateFromDB() {
		
		listArray = listManager.getLists();
		priceArray = listManager.getPrices();
		examArray = examManager.getExams();
	    operArray = operManager.getOperation();
	    mediArray = mediManager.getMedicals();
	    othArray = othManager.getOthers();
	}

	private PriceNode getTreeContent() {
		
		HashMap<String,Price> priceHashTable = new HashMap<String,Price>();
	    for (Price price : priceArray) {
	    	priceHashTable.put(price.getList()+
	    					  price.getGroup()+
	    					  price.getItem(), price);
	    }
	    
	    examNodes = new PriceNode(new Price(0,"","",ItemGroup.EXAM.getLabel(),null)); //$NON-NLS-1$ //$NON-NLS-2$
	    for(Exam exa: examArray){
	    	Price p = priceHashTable.get(listSelected.getId()+ItemGroup.EXAM.getCode()+exa.getCode());
	    	double priceValue = p != null ? p.getPrice() : 0.;
		    examNodes.addItem(new PriceNode(new Price(0, ItemGroup.EXAM.getCode(), exa.getCode(), exa.getDescription(), priceValue)));
	    }
	    
	    opeNodes = new PriceNode(new Price(0,"","",ItemGroup.OPERATION.getLabel(),null)); //$NON-NLS-1$ //$NON-NLS-2$
	    for(Operation ope: operArray){
	    	Price p = priceHashTable.get(listSelected.getId()+ItemGroup.OPERATION.getCode()+ope.getCode());
	    	double priceValue = p != null ? p.getPrice() : 0.;
		    opeNodes.addItem(new PriceNode(new Price(0, ItemGroup.OPERATION.getCode(), ope.getCode(), ope.getDescription(), priceValue)));
	    }
	    
	    medNodes = new PriceNode(new Price(0,"","",ItemGroup.MEDICAL.getLabel(),null)); //$NON-NLS-1$ //$NON-NLS-2$
	    for(Medical med: mediArray){
	    	Price p = priceHashTable.get(listSelected.getId()+ItemGroup.MEDICAL.getCode()+med.getCode().toString());
	    	double priceValue = p != null ? p.getPrice() : 0.;
		    medNodes.addItem(new PriceNode(new Price(0, ItemGroup.MEDICAL.getCode(), med.getCode().toString(), med.getDescription(), priceValue)));
	    }
	    
	    othNodes = new PriceNode(new Price(0,"","",ItemGroup.OTHER.getLabel(),null)); //$NON-NLS-1$ //$NON-NLS-2$
	    for(PricesOthers oth: othArray){
	    	Price p = priceHashTable.get(listSelected.getId()+ItemGroup.OTHER.getCode()+oth.getId());
	    	double priceValue = p != null ? p.getPrice() : 0.;
	    	othNodes.addItem(new PriceNode(new Price(0, ItemGroup.OTHER.getCode(), Integer.toString(oth.getId()), oth.getDescription(), priceValue, !oth.isUndefined())));
	    }
	    
	    PriceNode root = new PriceNode(new Price(0,"","",listSelected.getName(),null)); //$NON-NLS-1$ //$NON-NLS-2$
	    root.addItem(examNodes);
	    root.addItem(opeNodes);
	    root.addItem(medNodes);
	    root.addItem(othNodes);
	    
	    return root;
	}

	private JScrollPane getJScrollPaneList() {
		if (jScrollPaneList == null) {
			jScrollPaneList = new JScrollPane();
			jScrollPaneList.setViewportView(getJTreeList());
		}
		return jScrollPaneList;
	}

	private JComboBox getJComboBoxLists() {
		if (jComboBoxLists == null) {
			jComboBoxLists = new JComboBox();
			for (List elem : listArray) {
				
				jComboBoxLists.addItem(elem);
			}
			jComboBoxLists.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					
					int option = JOptionPane.showConfirmDialog(null, 
					   MessageBundle.getMessage("angal.priceslist.doyoureallywanttochangelist"),  //$NON-NLS-1$
					   MessageBundle.getMessage("angal.priceslist.changelist"),  //$NON-NLS-1$
					   JOptionPane.OK_CANCEL_OPTION);
						
					if (option == 0) {
						listSelected = (List) jComboBoxLists.getSelectedItem();
						
						PriceNode root = getTreeContent();
						jTreeTable.setModel(new PriceModel(root));
						jTreeTable.getTree().expandRow(3);
					    jTreeTable.getTree().expandRow(2);
					    jTreeTable.getTree().expandRow(1);
					    
					    updateDescription();
						validate();
						repaint();
					} else {
						
						jComboBoxLists.setSelectedItem(listSelected);
						return;
					}
				}				
			});	
			listSelected = (List) jComboBoxLists.getSelectedItem();
			jComboBoxLists.setDoubleBuffered(false);
			jComboBoxLists.setBorder(null);
		}
		return jComboBoxLists;
	}

	private JPanel getJPanelNorth() {
		if (jPanelNorth == null) {
			jPanelNorth = new JPanel();
			jPanelNorth.setLayout(new BoxLayout(jPanelNorth, BoxLayout.X_AXIS));
			jPanelNorth.add(getJPanelSelection());
			jPanelNorth.add(getJPanelDescription());
			jPanelNorth.add(getJPanelConfig());
		}
		return jPanelNorth;
	}

	private JPanel getJPanelDescription() {
		if (jPanelDescription == null) {
			jPanelDescription = new JPanel();
			//jPanelDescription.setLayout(new FlowLayout(FlowLayout.CENTER));
			jPanelDescription.add(getJLabelDescription());
		}
		return jPanelDescription;
	}
	private JTextField getResearchField() {
		if (researchField == null) {
			researchField = new JTextField();
			researchField.setToolTipText("Search");
			researchField.setColumns(20);
			Border outer = researchField.getBorder();
			Border search = new MatteBorder(0, 16, 0, 0, new ImageIcon("rsc/icons/zoom_button.png"));
			researchField.setBorder( new CompoundBorder(outer, search) );
			researchField.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void changedUpdate(DocumentEvent arg0) {
					String textl = researchField.getText();
					String text = researchField.getText().toLowerCase();
					PriceNode examNodes1 = new PriceNode(new Price(0,"","",ItemGroup.EXAM.getLabel(),null));
					PriceNode opeNodes1 = new PriceNode(new Price(0,"","",ItemGroup.OPERATION.getLabel(),null));

					PriceNode medNodes1 = new PriceNode(new Price(0,"","",ItemGroup.MEDICAL.getLabel(),null));

					PriceNode othNodes1 = new PriceNode(new Price(0,"","",ItemGroup.OTHER.getLabel(),null));
					//updateFromDB();
				    //PriceNode root = getTreeContent();				    
				    for (int i=0; i<examNodes.getItems().length; i++) {
						PriceNode newPriceNode = (PriceNode)examNodes.getItems()[i];
						String desc = newPriceNode.getPrice().getDesc();
						if(desc.toLowerCase().contains(text) || desc.contains(textl)) {
							examNodes1.addItem(newPriceNode);
						}
						
					}
				    for (int i=0; i<opeNodes.getItems().length; i++) {
						PriceNode newPriceNode = (PriceNode)opeNodes.getItems()[i];
						String desc = newPriceNode.getPrice().getDesc();
						if(desc.toLowerCase().contains(text)  || desc.contains(textl)) {
							opeNodes1.addItem(newPriceNode);
						}
						
					}
				    for (int i=0; i<medNodes.getItems().length; i++) {
						PriceNode newPriceNode = (PriceNode)medNodes.getItems()[i];
						String desc = newPriceNode.getPrice().getDesc();
						if(desc.toLowerCase().contains(text)  || desc.contains(textl)) {
							medNodes1.addItem(newPriceNode);
						}
						
					}
				    
				    for (int i=0; i<othNodes.getItems().length; i++) {
						PriceNode newPriceNode = (PriceNode)othNodes.getItems()[i];
						String desc = newPriceNode.getPrice().getDesc();
						if(desc.toLowerCase().contains(text) || desc.contains(textl)) {
							othNodes1.addItem(newPriceNode);							
						}
								
						
					}
				   PriceNode root1 = new PriceNode(new Price(0,"","",listSelected.getName(),null)); 
				   if(examNodes1.getItems().length > 0 )root1.addItem(examNodes1);
				   if(opeNodes1.getItems().length > 0 )root1.addItem(opeNodes1);
				   if(medNodes1.getItems().length > 0 )root1.addItem(medNodes1);
				   if(othNodes1.getItems().length > 0 )root1.addItem(othNodes1);
				    
					jTreeTable.setModel(new PriceModel(root1));
					jTreeTable.getTree().expandRow(3);
				    jTreeTable.getTree().expandRow(2);
				    jTreeTable.getTree().expandRow(1);
				    
				    //updateDescription();
				    jTreeTable.validate();
					jTreeTable.repaint();
					jTreeTable.updateUI();
					
				}

				@Override
				public void insertUpdate(DocumentEvent arg0) {
					String textl = researchField.getText();
					String text = researchField.getText().toLowerCase();
					PriceNode examNodes1 = new PriceNode(new Price(0,"","",ItemGroup.EXAM.getLabel(),null));
					PriceNode opeNodes1 = new PriceNode(new Price(0,"","",ItemGroup.OPERATION.getLabel(),null));

					PriceNode medNodes1 = new PriceNode(new Price(0,"","",ItemGroup.MEDICAL.getLabel(),null));

					PriceNode othNodes1 = new PriceNode(new Price(0,"","",ItemGroup.OTHER.getLabel(),null));
					//updateFromDB();
				    //PriceNode root = getTreeContent();				    
				    for (int i=0; i<examNodes.getItems().length; i++) {
						PriceNode newPriceNode = (PriceNode)examNodes.getItems()[i];
						String desc = newPriceNode.getPrice().getDesc();
						if(desc.toLowerCase().contains(text) || desc.contains(textl)) {
							examNodes1.addItem(newPriceNode);
						}
						
					}
				    for (int i=0; i<opeNodes.getItems().length; i++) {
						PriceNode newPriceNode = (PriceNode)opeNodes.getItems()[i];
						String desc = newPriceNode.getPrice().getDesc();
						if(desc.toLowerCase().contains(text)  || desc.contains(textl)) {
							opeNodes1.addItem(newPriceNode);
						}
						
					}
				    for (int i=0; i<medNodes.getItems().length; i++) {
						PriceNode newPriceNode = (PriceNode)medNodes.getItems()[i];
						String desc = newPriceNode.getPrice().getDesc();
						if(desc.toLowerCase().contains(text)  || desc.contains(textl)) {
							medNodes1.addItem(newPriceNode);
						}
						
					}
				    
				    for (int i=0; i<othNodes.getItems().length; i++) {
						PriceNode newPriceNode = (PriceNode)othNodes.getItems()[i];
						String desc = newPriceNode.getPrice().getDesc();
						if(desc.toLowerCase().contains(text) || desc.contains(textl)) {
							othNodes1.addItem(newPriceNode);							
						}
								
						
					}
				   PriceNode root1 = new PriceNode(new Price(0,"","",listSelected.getName(),null)); 
				   if(examNodes1.getItems().length > 0 )root1.addItem(examNodes1);
				   if(opeNodes1.getItems().length > 0 )root1.addItem(opeNodes1);
				   if(medNodes1.getItems().length > 0 )root1.addItem(medNodes1);
				   if(othNodes1.getItems().length > 0 )root1.addItem(othNodes1);
				    
					jTreeTable.setModel(new PriceModel(root1));
					jTreeTable.getTree().expandRow(3);
				    jTreeTable.getTree().expandRow(2);
				    jTreeTable.getTree().expandRow(1);
				    
				    //updateDescription();
				    jTreeTable.validate();
					jTreeTable.repaint();
					jTreeTable.updateUI();
					
				}

				@Override
				public void removeUpdate(DocumentEvent arg0) {
					String text = researchField.getText().toLowerCase();
					String textl = researchField.getText();
					PriceNode examNodes1 = new PriceNode(new Price(0,"","",ItemGroup.EXAM.getLabel(),null));
					PriceNode opeNodes1 = new PriceNode(new Price(0,"","",ItemGroup.OPERATION.getLabel(),null));

					PriceNode medNodes1 = new PriceNode(new Price(0,"","",ItemGroup.MEDICAL.getLabel(),null));

					PriceNode othNodes1 = new PriceNode(new Price(0,"","",ItemGroup.OTHER.getLabel(),null));				    
				    for (int i=0; i<examNodes.getItems().length; i++) {
						PriceNode newPriceNode = (PriceNode)examNodes.getItems()[i];
						String desc = newPriceNode.getPrice().getDesc();
						if(desc.toLowerCase().contains(text) || desc.contains(textl)) {
							examNodes1.addItem(newPriceNode);
						}
						
					}
				    for (int i=0; i<opeNodes.getItems().length; i++) {
						PriceNode newPriceNode = (PriceNode)opeNodes.getItems()[i];
						String desc = newPriceNode.getPrice().getDesc();
						if(desc.toLowerCase().contains(text) || desc.contains(textl)) {
							opeNodes1.addItem(newPriceNode);
						}
						
					}
				    for (int i=0; i<medNodes.getItems().length; i++) {
						PriceNode newPriceNode = (PriceNode)medNodes.getItems()[i];
						String desc = newPriceNode.getPrice().getDesc();
						if(desc.toLowerCase().contains(text) || desc.contains(textl)) {
							medNodes1.addItem(newPriceNode);
						}
						
					}
				    for (int i=0; i<othNodes.getItems().length; i++) {
						PriceNode newPriceNode = (PriceNode)othNodes.getItems()[i];
						String desc = newPriceNode.getPrice().getDesc();
						if(desc.toLowerCase().contains(text) || desc.contains(textl)) {
							othNodes1.addItem(newPriceNode);
						}
						
					}
				   PriceNode root1 = new PriceNode(new Price(0,"","",listSelected.getName(),null)); 
				   if(examNodes1.getItems().length > 0 )root1.addItem(examNodes1);
				   if(opeNodes1.getItems().length > 0 )root1.addItem(opeNodes1);
				   if(medNodes1.getItems().length > 0 )root1.addItem(medNodes1);
				   if(othNodes1.getItems().length > 0 )root1.addItem(othNodes1);
				    
					jTreeTable.setModel(new PriceModel(root1));
					jTreeTable.getTree().expandRow(3);
				    jTreeTable.getTree().expandRow(2);
				    jTreeTable.getTree().expandRow(1);
				    
				    //updateDescription();
				    jTreeTable.validate();
					jTreeTable.repaint();
					jTreeTable.updateUI();
					}
				});
		}
		return researchField;
	}
}
