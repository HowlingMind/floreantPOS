package com.floreantpos.bo.actions;

import com.floreantpos.POSConstants;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.bo.ui.explorer.AttendenceHistoryExplorer;
import com.floreantpos.main.Application;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AttendanceHistoryExplorerAction extends AbstractAction {

	public AttendanceHistoryExplorerAction() {
		super(POSConstants.ATTENDANCE_HISTORY);
	}

	public AttendanceHistoryExplorerAction(String name) {
		super(name);
	}

	public AttendanceHistoryExplorerAction(String name, Icon icon) {
		super(name, icon);
	}

	public void actionPerformed(ActionEvent e) {
		BackOfficeWindow backOfficeWindow = Application.getInstance().getBackOfficeWindow();
		
		AttendenceHistoryExplorer explorer = null;
		JTabbedPane tabbedPane = backOfficeWindow.getTabbedPane();
		int index = tabbedPane.indexOfTab(POSConstants.ATTENDANCE_HISTORY);
		if (index == -1) {
			explorer = new AttendenceHistoryExplorer();
			tabbedPane.addTab(POSConstants.ATTENDANCE_HISTORY, explorer);
		}
		else {
			explorer = (AttendenceHistoryExplorer) tabbedPane.getComponentAt(index);
		}
		tabbedPane.setSelectedComponent(explorer);
	}

}
