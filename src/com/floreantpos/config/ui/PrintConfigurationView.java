/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PrintConfiguration.java
 *
 * Created on Apr 5, 2010, 4:31:47 PM
 */

package com.floreantpos.config.ui;

import com.floreantpos.config.ApplicationConfig;
import com.floreantpos.config.PrintConfig;
import com.floreantpos.dal.PosSessionFactory;
import com.floreantpos.model.PrinterConfiguration;
import com.floreantpos.print.PrinterType;
import org.hibernate.Session;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author mshahriar
 */
public class PrintConfigurationView extends ConfigurationView {

	private PrinterConfiguration printerConfiguration;
	private Session currentSession;

	/** Creates new form PrintConfiguration */
	public PrintConfigurationView() {
		currentSession = PosSessionFactory.currentSession();
		initComponents();

		printerConfiguration = (PrinterConfiguration) currentSession.get(PrinterConfiguration.class,
				  1);
		if (printerConfiguration == null)
		{
			currentSession.beginTransaction();
			//don't have one in the DB yet!
			printerConfiguration = new PrinterConfiguration(1);
			currentSession.save("PrinterConfiguration",printerConfiguration);
			currentSession.getTransaction().commit();
		}
	}

	@Override
	public String getName() {
		return com.floreantpos.POSConstants.PRINT_CONFIGURATION;
	}

	@Override
	public void initialize() throws Exception {
		PrinterType[] values = PrinterType.values();
		cbReceiptPrinterType.setModel(new DefaultComboBoxModel(values));
		cbKitchenPrinterType.setModel(new DefaultComboBoxModel(values));

		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
		cbReceiptPrinterName.setModel(new DefaultComboBoxModel(printServices));
		cbKitchenPrinterName.setModel(new DefaultComboBoxModel(printServices));
		
		PrintServiceComboRenderer comboRenderer = new PrintServiceComboRenderer();
		cbReceiptPrinterName.setRenderer(comboRenderer);
		cbKitchenPrinterName.setRenderer(comboRenderer);
		
		cbReceiptPrinterType.setSelectedItem(PrinterType.fromString(ApplicationConfig.getString(PrintConfig.P_RECEIPT_PRINTER_TYPE, PrinterType.OS_PRINTER.getName())));
		cbKitchenPrinterType.setSelectedItem(PrinterType.fromString(ApplicationConfig.getString(PrintConfig.P_KITCHEN_PRINTER_TYPE, PrinterType.OS_PRINTER.getName())));
		
		tfReceiptPrinterName.setText(ApplicationConfig.getString(PrintConfig.P_JAVAPOS_PRINTER_FOR_RECEIPT, "POSPrinter"));
		tfReceiptCashDrawerName.setText(ApplicationConfig.getString(PrintConfig.P_CASH_DRAWER_NAME, "CashDrawer"));
		tfKitchenPrinterName.setText(ApplicationConfig.getString(PrintConfig.P_JAVAPOS_PRINTER_FOR_KITCHEN, "KitchenPrinter"));
		
		setSelectedPrinter(cbReceiptPrinterName, PrintConfig.P_OS_PRINTER_FOR_RECEIPT);
		setSelectedPrinter(cbKitchenPrinterName, PrintConfig.P_OS_PRINTER_FOR_KITCHEN);
		
		/*chkPrintReceiptWhenTicketCreated.setSelected(ApplicationConfig.getBoolean(PrintConfig.P_PRINT_RECEIPT_WHEN_SETTELED, true));
		chkPrintReceiptWhenTicketPaid.setSelected(ApplicationConfig.getBoolean(PrintConfig.P_PRINT_RECEIPT_WHEN_PAID, false));
		chkPrintKitchenWhenTicketCreated.setSelected(ApplicationConfig.getBoolean(PrintConfig.P_PRINT_KITCHEN_WHEN_SETTELED, false));
		chkPrintKitchenWhenTicketPaid.setSelected(ApplicationConfig.getBoolean(PrintConfig.P_PRINT_KITCHEN_WHEN_PAID, false));*/

		chkPrintReceiptWhenTicketCreated.setSelected(printerConfiguration.isPrintReceiptWhenTicketCreated());
		chkPrintReceiptWhenTicketPaid.setSelected(printerConfiguration.isPrintReceiptWhenTicketPaid());
		chkPrintKitchenWhenTicketPaid.setSelected(printerConfiguration.isPrintKitchenWhenTicketPaid());
		chkPrintKitchenWhenTicketCreated.setSelected(printerConfiguration.isPrintKitchenWhenTicketCreated());
		chkPreviewInsteadOfPrint.setSelected(printerConfiguration.isPrintPreviewInsteadOfPrint());
		chkPrintTwoKitchenTickets.setSelected(printerConfiguration.isPrintTwoKitchenTickets());

		setInitialized(true);
	}

	private void setSelectedPrinter(JComboBox whichPrinter, String propertyName) {
		PrintService osDefaultPrinter = PrintServiceLookup.lookupDefaultPrintService();
		String receiptPrinterName = ApplicationConfig.getString(propertyName, osDefaultPrinter.getName());
		
		int printerCount = whichPrinter.getItemCount();
		for(int i = 0; i < printerCount; i++) {
			PrintService printService = (PrintService) whichPrinter.getItemAt(i);
			if(printService.getName().equals(receiptPrinterName)) {
				whichPrinter.setSelectedIndex(i);
				return;
			}
		}
	}

	@Override
	public boolean save() throws Exception {
		currentSession = PosSessionFactory.currentSession();
		currentSession.beginTransaction();
		ApplicationConfig.put(PrintConfig.P_RECEIPT_PRINTER_TYPE, cbReceiptPrinterType.getSelectedItem().toString());
		ApplicationConfig.put(PrintConfig.P_KITCHEN_PRINTER_TYPE, cbKitchenPrinterType.getSelectedItem().toString());
		
		PrintService printService = (PrintService) cbReceiptPrinterName.getSelectedItem();
		//ApplicationConfig.put(PrintConfig.P_OS_PRINTER_FOR_RECEIPT, printService == null ? null : printService.getName());
		printerConfiguration.setReceiptPrinterName(printService == null ? null : printService.getName());
		printService = (PrintService) cbKitchenPrinterName.getSelectedItem();
		printerConfiguration.setKitchenPrinterName(printService == null ? null : printService.getName());

		ApplicationConfig.put(PrintConfig.P_JAVAPOS_PRINTER_FOR_RECEIPT, tfReceiptPrinterName.getText());
		ApplicationConfig.put(PrintConfig.P_CASH_DRAWER_NAME, tfReceiptCashDrawerName.getText());
		ApplicationConfig.put(PrintConfig.P_JAVAPOS_PRINTER_FOR_KITCHEN, tfKitchenPrinterName.getText());

		//ApplicationConfig.put(PrintConfig.P_PRINT_KITCHEN_WHEN_PAID, chkPrintKitchenWhenTicketPaid.isSelected());
		//ApplicationConfig.put(PrintConfig.P_PRINT_KITCHEN_WHEN_SETTELED, chkPrintKitchenWhenTicketCreated.isSelected());
		//ApplicationConfig.put(PrintConfig.P_PRINT_RECEIPT_WHEN_PAID, chkPrintReceiptWhenTicketPaid.isSelected());
		//ApplicationConfig.put(PrintConfig.P_PRINT_RECEIPT_WHEN_SETTELED, chkPrintReceiptWhenTicketCreated.isSelected());

		printerConfiguration.setPrintKitchenWhenTicketPaid(chkPrintKitchenWhenTicketPaid.isSelected());
		printerConfiguration.setPrintKitchenWhenTicketCreated(chkPrintKitchenWhenTicketCreated.isSelected());
		printerConfiguration.setPrintReceiptWhenTicketPaid(chkPrintReceiptWhenTicketPaid.isSelected());
		printerConfiguration.setPrintReceiptWhenTicketCreated(chkPrintReceiptWhenTicketCreated.isSelected());
		printerConfiguration.setPrintPreviewInsteadOfPrint(chkPreviewInsteadOfPrint.isSelected());
		printerConfiguration.setPrintTwoKitchenTickets(chkPrintTwoKitchenTickets.isSelected());

		//Persist to the database
		currentSession.saveOrUpdate(printerConfiguration);
		currentSession.getTransaction().commit();
		return true;
	}

	private void setReceiptPrinterType(PrinterType printerType) {
		switch (printerType) {
			case OS_PRINTER:
				lblReceiptPrinterName.setEnabled(true);
				lblSelectReceiptPrinter.setEnabled(true);
				cbReceiptPrinterName.setEnabled(true);
				lblReceiptPrinterName.setEnabled(false);
				tfReceiptPrinterName.setEnabled(false);
				lblReceiptCashDrawerName.setEnabled(false);
				tfReceiptCashDrawerName.setEnabled(false);
				break;
				
			case JAVAPOS:
				lblReceiptPrinterName.setEnabled(false);
				lblSelectReceiptPrinter.setEnabled(false);
				cbReceiptPrinterName.setEnabled(false);
				lblReceiptPrinterName.setEnabled(true);
				tfReceiptPrinterName.setEnabled(true);
				lblReceiptCashDrawerName.setEnabled(true);
				tfReceiptCashDrawerName.setEnabled(true);
				break;
		}
	}
	
	private void setKitchenPrinterType(PrinterType printerType) {
		switch (printerType) {
			case OS_PRINTER:
				lblKitchenPrinterName.setEnabled(true);
				lblSelectKitchenPrinter.setEnabled(true);
				cbKitchenPrinterName.setEnabled(true);
				lblKitchenPrinterName.setEnabled(false);
				tfKitchenPrinterName.setEnabled(false);
				break;
				
			case JAVAPOS:
				lblKitchenPrinterName.setEnabled(false);
				lblSelectKitchenPrinter.setEnabled(false);
				cbKitchenPrinterName.setEnabled(false);
				lblKitchenPrinterName.setEnabled(true);
				tfKitchenPrinterName.setEnabled(true);
				break;
		}
	}

	private void receiptPrinterSelectionChanged(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_receiptPrinterSelectionChanged
		setReceiptPrinterType((PrinterType) cbReceiptPrinterType.getSelectedItem());
	}//GEN-LAST:event_receiptPrinterSelectionChanged
	
	private void kitchenPrinterTypeSelectionChanged(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kitchenPrinterTypeSelectionChanged
		setKitchenPrinterType((PrinterType) cbKitchenPrinterType.getSelectedItem());
	}//GEN-LAST:event_kitchenPrinterTypeSelectionChanged
	
	

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chkPrintReceiptWhenTicketCreated = new javax.swing.JCheckBox();
        chkPrintReceiptWhenTicketPaid = new javax.swing.JCheckBox();
        chkPrintKitchenWhenTicketCreated = new javax.swing.JCheckBox();
        chkPrintKitchenWhenTicketPaid = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        lblReceiptCashDrawerName = new javax.swing.JLabel();
        tfReceiptCashDrawerName = new javax.swing.JTextField();
        lblReceiptPrinterName = new javax.swing.JLabel();
        tfReceiptPrinterName = new javax.swing.JTextField();
        cbReceiptPrinterName = new javax.swing.JComboBox();
        cbReceiptPrinterType = new javax.swing.JComboBox();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        lblSelectReceiptPrinter = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblKitchenPrinterName = new javax.swing.JLabel();
        tfKitchenPrinterName = new javax.swing.JTextField();
        cbKitchenPrinterName = new javax.swing.JComboBox();
        cbKitchenPrinterType = new javax.swing.JComboBox();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        lblSelectKitchenPrinter = new javax.swing.JLabel();
        chkPrintTwoKitchenTickets = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        chkPreviewInsteadOfPrint = new javax.swing.JCheckBox();

        chkPrintReceiptWhenTicketCreated.setText("Print receipt when ticket created");

        chkPrintReceiptWhenTicketPaid.setText("Print receipt when ticket paid");

        chkPrintKitchenWhenTicketCreated.setText("Print to kitchen when ticket created");

        chkPrintKitchenWhenTicketPaid.setText("Print to kitchen when ticket paid");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Receipt Printer", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));

        lblReceiptCashDrawerName.setText("CashDrawer:");

        tfReceiptCashDrawerName.setText("CashDrawer");

        lblReceiptPrinterName.setText("Printer name:");

        tfReceiptPrinterName.setText("PosPrinter");

        cbReceiptPrinterType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                receiptPrinterSelectionChanged(evt);
            }
        });

        jLabel1.setText("Printer Type:");

        lblSelectReceiptPrinter.setText("Select Printer:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(lblReceiptPrinterName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfReceiptPrinterName))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbReceiptPrinterType, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSelectReceiptPrinter)
                    .addComponent(lblReceiptCashDrawerName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfReceiptCashDrawerName, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                    .addComponent(cbReceiptPrinterName, 0, 178, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, lblReceiptPrinterName});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cbReceiptPrinterType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSelectReceiptPrinter)
                    .addComponent(cbReceiptPrinterName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblReceiptPrinterName)
                    .addComponent(tfReceiptPrinterName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblReceiptCashDrawerName)
                    .addComponent(tfReceiptCashDrawerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Kitchen Printer", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));

        lblKitchenPrinterName.setText("Printer name:");

        tfKitchenPrinterName.setText("KitchenPrinter");

        cbKitchenPrinterType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kitchenPrinterTypeSelectionChanged(evt);
            }
        });

        jLabel2.setText("Printer Type:");

        lblSelectKitchenPrinter.setText("Select Printer:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(lblKitchenPrinterName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfKitchenPrinterName))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbKitchenPrinterType, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(lblSelectKitchenPrinter)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbKitchenPrinterName, 0, 178, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel2, lblKitchenPrinterName});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cbKitchenPrinterType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSelectKitchenPrinter)
                    .addComponent(cbKitchenPrinterName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblKitchenPrinterName)
                    .addComponent(tfKitchenPrinterName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        chkPrintTwoKitchenTickets.setText("Print two kitchen tickets");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Changes require restart of all POS instances");

        chkPreviewInsteadOfPrint.setText("Print preview instead printing");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(102, 102, 102)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkPrintReceiptWhenTicketCreated)
                            .addComponent(chkPrintReceiptWhenTicketPaid)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkPrintKitchenWhenTicketCreated)
                                    .addComponent(chkPrintKitchenWhenTicketPaid))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkPreviewInsteadOfPrint)
                                    .addComponent(chkPrintTwoKitchenTickets)))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkPrintReceiptWhenTicketCreated)
                    .addComponent(chkPreviewInsteadOfPrint))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkPrintReceiptWhenTicketPaid)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkPrintKitchenWhenTicketCreated)
                    .addComponent(chkPrintTwoKitchenTickets))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkPrintKitchenWhenTicketPaid)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbKitchenPrinterName;
    private javax.swing.JComboBox cbKitchenPrinterType;
    private javax.swing.JComboBox cbReceiptPrinterName;
    private javax.swing.JComboBox cbReceiptPrinterType;
    private javax.swing.JCheckBox chkPreviewInsteadOfPrint;
    private javax.swing.JCheckBox chkPrintKitchenWhenTicketCreated;
    private javax.swing.JCheckBox chkPrintKitchenWhenTicketPaid;
    private javax.swing.JCheckBox chkPrintReceiptWhenTicketCreated;
    private javax.swing.JCheckBox chkPrintReceiptWhenTicketPaid;
    private javax.swing.JCheckBox chkPrintTwoKitchenTickets;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblKitchenPrinterName;
    private javax.swing.JLabel lblReceiptCashDrawerName;
    private javax.swing.JLabel lblReceiptPrinterName;
    private javax.swing.JLabel lblSelectKitchenPrinter;
    private javax.swing.JLabel lblSelectReceiptPrinter;
    private javax.swing.JTextField tfKitchenPrinterName;
    private javax.swing.JTextField tfReceiptCashDrawerName;
    private javax.swing.JTextField tfReceiptPrinterName;
    // End of variables declaration//GEN-END:variables
    
    private class PrintServiceComboRenderer extends DefaultListCellRenderer {
    	@Override
    	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    		JLabel listCellRendererComponent = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    		listCellRendererComponent.setText(((PrintService) value).getName());
    		
    		return listCellRendererComponent;
    	}
    }

}
