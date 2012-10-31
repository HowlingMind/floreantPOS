package com.floreantpos.bo.ui.explorer;

import com.floreantpos.main.Application;
import com.floreantpos.model.AttendenceHistory;
import com.floreantpos.model.User;
import com.floreantpos.model.dao.AttendenceHistoryDAO;
import com.floreantpos.swing.MessageDialog;
import com.floreantpos.swing.TransparentPanel;
import com.floreantpos.ui.PosTableRenderer;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.ConfirmDeleteDialog;
import com.floreantpos.ui.forms.AttendenceHistoryForm;
import org.hibernate.exception.ConstraintViolationException;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AttendenceHistoryExplorer extends TransparentPanel {
	
	private JTable table;
	private AttendenceHistoryTableModel tableModel;
	
	public AttendenceHistoryExplorer() {
		List<AttendenceHistory> times = AttendenceHistoryDAO.getInstance().findAll();
		
		tableModel = new AttendenceHistoryTableModel(times);
		table = new JTable(tableModel);
		table.setDefaultRenderer(Object.class, new PosTableRenderer());
		//Add sorting, dag nabit!
		table.setRowSorter(new TableRowSorter(tableModel));
		
		setLayout(new BorderLayout(5,5));
		add(new JScrollPane(table));
		
		/*JButton addButton = new JButton(com.floreantpos.POSConstants.ADD);
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					UserForm editor = new UserForm();
					
					BeanEditorDialog dialog = new BeanEditorDialog(editor, Application.getInstance().getBackOfficeWindow(), true);
					dialog.open();
					if (dialog.isCanceled())
						return;
					User user = (User) editor.getBean();
					tableModel.addItem(user);
				} catch (Exception x) {
					x.printStackTrace();
					MessageDialog.showError(com.floreantpos.POSConstants.ERROR_MESSAGE, x);
				}
			}
			
		});*/
		
		JButton editButton = new JButton(com.floreantpos.POSConstants.EDIT);
		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int index = table.convertRowIndexToModel(table.getSelectedRow());
					if (index < 0)
						return;

					AttendenceHistory time = (AttendenceHistory) tableModel.getRowData(index);
					AttendenceHistoryForm editor = new AttendenceHistoryForm();
					//editor.setEditMode(true);

					editor.setAttendenceHistory(time);
					BeanEditorDialog dialog = new BeanEditorDialog(editor, Application.getInstance().getBackOfficeWindow(), true);
					dialog.open();
					if (dialog.isCanceled())
						return;

					tableModel.updateItem(index);
				} catch (Throwable x) {
					MessageDialog.showError(com.floreantpos.POSConstants.ERROR_MESSAGE, x);
				}
			}
			
		});
		JButton deleteButton = new JButton(com.floreantpos.POSConstants.DELETE);
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = table.convertRowIndexToModel(table.getSelectedRow());
				if (index < 0)
					return;
				
				User user = (User) tableModel.getRowData(index);
				if(user == null) {
					return;
				}
				
				try {
					if (ConfirmDeleteDialog.showMessage(AttendenceHistoryExplorer.this, com.floreantpos.POSConstants.CONFIRM_DELETE, com.floreantpos.POSConstants.DELETE) == ConfirmDeleteDialog.YES) {
						//TODO: removed for compilation
						//UserDAO.getInstance().delete(user);
						tableModel.deleteItem(index);
					}
				} catch(ConstraintViolationException x) {
					String message = com.floreantpos.POSConstants.USER + " " + user.getFirstName() + " " + user.getLastName() + " (" + user.getNewUserType() + ") " + com.floreantpos.POSConstants.ERROR_MESSAGE;
					MessageDialog.showError(message, x);
				} catch (Exception x) {
					MessageDialog.showError(com.floreantpos.POSConstants.ERROR_MESSAGE, x);
				}
			}
			
		});

		TransparentPanel panel = new TransparentPanel();
		//panel.add(addButton);
		panel.add(editButton);
		panel.add(deleteButton);
		add(panel, BorderLayout.SOUTH);
	}
	
	class AttendenceHistoryTableModel extends ListTableModel {
		
		AttendenceHistoryTableModel(List list){
			super(new String[] {AttendenceHistory.PROP_ID, AttendenceHistory.PROP_CLOCK_IN_TIME, AttendenceHistory.PROP_CLOCK_OUT_TIME,
					  AttendenceHistory.PROP_CLOCK_IN_HOUR, AttendenceHistory.PROP_CLOCK_OUT_HOUR, AttendenceHistory.PROP_CLOCKED_OUT,
					  AttendenceHistory.PROP_USER, AttendenceHistory.PROP_SHIFT,AttendenceHistory.PROP_TERMINAL}, list);
		}
		

		public Object getValueAt(int rowIndex, int columnIndex) {
			AttendenceHistory attendenceHistory = (AttendenceHistory) rows.get(rowIndex);
			
			switch(columnIndex) {
				case 0:
					return String.valueOf(attendenceHistory.getId());
					
				case 1:
					return attendenceHistory.getClockInTime();
					
				case 2:
					return attendenceHistory.getClockOutTime();
					
				case 3:
					return attendenceHistory.getClockInHour();
					
				case 4: 
					return attendenceHistory.getClockOutHour();
				
				case 5:
					return attendenceHistory.isClockedOut();

				case 6:
					return attendenceHistory.getUser();

				case 7:
					return attendenceHistory.getShift();

				case 8:
					return attendenceHistory.getTerminal();
			}
			return null;
		}
	}
}
