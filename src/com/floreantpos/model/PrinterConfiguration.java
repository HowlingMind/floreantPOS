package com.floreantpos.model;

import com.floreantpos.dal.PosSessionFactory;
import com.floreantpos.model.base.BasePrinterConfiguration;
import org.hibernate.Session;


public class PrinterConfiguration extends BasePrinterConfiguration {
	private static final long serialVersionUID = 1L;

	private static PrinterConfiguration printerConfiguration;

	/*[CONSTRUCTOR MARKER BEGIN]*/
	public PrinterConfiguration() {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public PrinterConfiguration(java.lang.Integer id) {
		super(id);
	}

/*[CONSTRUCTOR MARKER END]*/

	public final static Integer ID = Integer.valueOf(1);

	//Lazy loaded copy of the current print settings
	public static PrinterConfiguration printerConfiguration() {
		//Null? go ahead and load from the DB
		if (printerConfiguration == null) {
			Session currentSession = PosSessionFactory.currentSession();

			printerConfiguration = (PrinterConfiguration) currentSession.get(PrinterConfiguration.class, 1);
			if (printerConfiguration == null) {
				//still null? don't have one in the DB yet!
				currentSession.beginTransaction();
				printerConfiguration = new PrinterConfiguration(1);
				currentSession.save("PrinterConfiguration", printerConfiguration);
				currentSession.getTransaction().commit();
			}
		}
		return printerConfiguration;
	}

	@Override
	public String getReceiptPrinterName() {
		if (super.getReceiptPrinterName() == null) {
			return "PosPrinter";
		}
		return super.getReceiptPrinterName();
	}

	@Override
	public String getKitchenPrinterName() {
		if (super.getKitchenPrinterName() == null) {
			return "KitchenPrinter";
		}
		return super.getKitchenPrinterName();
	}
}