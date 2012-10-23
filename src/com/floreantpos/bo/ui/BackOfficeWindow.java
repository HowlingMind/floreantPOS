/*
 * BackOfficeWindow.java
 *
 * Created on August 16, 2006, 12:43 PM
 */

package com.floreantpos.bo.ui;

import com.floreantpos.bo.actions.*;
import com.floreantpos.config.ApplicationConfig;
import com.floreantpos.main.Application;
import com.floreantpos.model.User;
import com.floreantpos.model.UserPermission;
import com.floreantpos.model.UserType;
import com.floreantpos.ui.report.actions.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Set;

/**
 *
 * @author  MShahriar
 */
public class BackOfficeWindow extends javax.swing.JFrame {

	/** Creates new form BackOfficeWindow */
	public BackOfficeWindow() {
		initComponents();

		createMenus();

		tabbedPane.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					JPopupMenu menu = new JPopupMenu();
					menu.add(new AbstractAction(com.floreantpos.POSConstants.CLOSE) {

						public void actionPerformed(ActionEvent e) {
							int index = tabbedPane.getSelectedIndex();
							tabbedPane.remove(index);
						}

					});
					menu.show(tabbedPane, e.getX(), e.getY());
				}
			}
		});

		positionWindow();
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ApplicationConfig.getPreferences().putInt("bwwidth", BackOfficeWindow.this.getWidth());
				ApplicationConfig.getPreferences().putInt("bwheight", BackOfficeWindow.this.getHeight());
				ApplicationConfig.getPreferences().putInt("bwx", BackOfficeWindow.this.getX());
				ApplicationConfig.getPreferences().putInt("bwy", BackOfficeWindow.this.getY());
				Application.getInstance().setBackOfficeWindow(null);
				dispose();
			}
		});

		setTitle(Application.getTitle() + "- " + com.floreantpos.POSConstants.BACK_OFFICE);
	}

	private void positionWindow() {
		int width = ApplicationConfig.getPreferences().getInt("bwwidth", 900);
		int height = ApplicationConfig.getPreferences().getInt("bwheight", 650);
		setSize(width, height);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width - width) >> 1;
		int y = (screenSize.height - height) >> 1;
		
		x = ApplicationConfig.getPreferences().getInt("bwx", x);
		y = ApplicationConfig.getPreferences().getInt("bwy", y);
		
		setLocation(x, y);
	}

	private void createMenus() {
		User user = Application.getCurrentUser();
		UserType newUserType = user.getNewUserType();

		Set<UserPermission> permissions = null;

		if (newUserType != null) {
			permissions = newUserType.getPermissions();
		}

		JMenuBar menuBar = new JMenuBar();

		if (newUserType == null) {
			createAdminMenu(menuBar);
			createExplorerMenu(menuBar);
			createReportMenu(menuBar);
		}
		else {
			if(permissions != null && permissions.contains(UserPermission.PERFORM_ADMINISTRATIVE_TASK)) {
				createAdminMenu(menuBar);
			}
			if(permissions != null && permissions.contains(UserPermission.VIEW_EXPLORERS)) {
				createExplorerMenu(menuBar);
			}
			if(permissions != null && permissions.contains(UserPermission.VIEW_REPORTS)) {
				createReportMenu(menuBar);
			}
		}

		setJMenuBar(menuBar);
	}

	private void createReportMenu(JMenuBar menuBar) {
		JMenu reportMenu = new JMenu(com.floreantpos.POSConstants.REPORTS);
		reportMenu.add(new SalesReportAction());
		reportMenu.add(new OpenTicketSummaryReportAction());
		reportMenu.add(new HourlyLaborReportAction());
		reportMenu.add(new PayrollReportAction());
		reportMenu.add(new KeyStatisticsSalesReportAction());
		reportMenu.add(new SalesAnalysisReportAction());
		reportMenu.add(new CreditCardReportAction());
		reportMenu.add(new MenuUsageReportAction());
		reportMenu.add(new ServerProductivityReportAction());
		reportMenu.add(new JournalReportAction());
		reportMenu.add(new SalesBalanceReportAction());
		reportMenu.add(new SalesExceptionReportAction());
		reportMenu.add(new SalesDetailReportAction());
		menuBar.add(reportMenu);
	}

	private void createExplorerMenu(JMenuBar menuBar) {
		JMenu explorerMenu = new JMenu(com.floreantpos.POSConstants.EXPLORERS);
		explorerMenu.add(new CategoryExplorerAction());
		explorerMenu.add(new GroupExplorerAction());
		explorerMenu.add(new ItemExplorerAction());
		explorerMenu.add(new ModifierGroupExplorerAction());
		explorerMenu.add(new ModifierExplorerAction());
		explorerMenu.add(new ShiftExplorerAction());
		explorerMenu.add(new CouponExplorerAction());
		explorerMenu.add(new CookingInstructionExplorerAction());
		menuBar.add(explorerMenu);
	}

	private void createAdminMenu(JMenuBar menuBar) {
		JMenu adminMenu = new JMenu(com.floreantpos.POSConstants.ADMIN);
		adminMenu.add(new ConfigureRestaurantAction());
		adminMenu.add(new UserExplorerAction());
		adminMenu.add(new UserTypeExplorerAction());
		adminMenu.add(new ViewGratuitiesAction());
		adminMenu.add(new DrawerPullReportExplorerAction());
		adminMenu.add(new AttendanceHistoryExplorerAction());
		menuBar.add(adminMenu);
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	private void initComponents() {
		jPanel1 = new javax.swing.JPanel();
		tabbedPane = new javax.swing.JTabbedPane();

		getContentPane().setLayout(new java.awt.BorderLayout(5, 0));

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		jPanel1.setLayout(new java.awt.BorderLayout(5, 0));

		jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
		jPanel1.add(tabbedPane, java.awt.BorderLayout.CENTER);

		getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

		pack();
	}// </editor-fold>//GEN-END:initComponents

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new BackOfficeWindow().setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel jPanel1;
	private javax.swing.JTabbedPane tabbedPane;

	// End of variables declaration//GEN-END:variables

	public javax.swing.JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

}
