package com.floreantpos.jreports;

import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemModifier;
import com.floreantpos.model.TicketItemModifierGroup;

import java.util.ArrayList;
import java.util.List;


public class KitchenTicketDataSource extends AbstractReportDataSource {

	public KitchenTicketDataSource() {
		super(new String[] {"itemNo", "itemName", "itemQty"});
	}
	
	public KitchenTicketDataSource(Ticket ticket) {
		super(new String[] {"itemNo", "itemName", "itemQty"});
		
		setTicket(ticket);
	}

	public void setTicket(Ticket ticket) {
		ArrayList<Row> rows = new ArrayList<Row>();
		
		List<TicketItem> ticketItems = ticket.getTicketItems();
		if (ticketItems != null) {
			for (TicketItem ticketItem : ticketItems) {
				if(ticketItem.isShouldPrintToKitchen() && !ticketItem.isPrintedToKitchen()) {
					Row row1 = new Row(ticketItem.getItemCount(), ticketItem.getName(), ticketItem.getId());
					rows.add(row1);
				}
				//ticketItem.setPrintedToKitchen(true);
				
				List<TicketItemModifierGroup> modifierGroups = ticketItem.getTicketItemModifierGroups();
				if (modifierGroups != null) {
					for (TicketItemModifierGroup modifierGroup : modifierGroups) {
						List<TicketItemModifier> modifiers = modifierGroup.getTicketItemModifiers();
						if (modifiers != null) {
							for (TicketItemModifier modifier : modifiers) {
								if(!modifier.isShouldPrintToKitchen() || modifier.isPrintedToKitchen()) {
									continue;
								}
								modifier.setPrintedToKitchen(true);
								
								String modifierGroupName = modifierGroup.getName();
								if (modifierGroupName == null) {
									modifierGroupName = "";
								}
								String name = modifier.getName();
								String tabSpacing = "    ";
								if ((modifierGroupName.equalsIgnoreCase("Toppings")) || (modifierGroupName.equalsIgnoreCase("Condiments"))) {
									tabSpacing += "  ";
								}
								if (modifier.getModifierType() == TicketItemModifier.EXTRA_MODIFIER) {
									name = tabSpacing + "++ Extra " + name;
								} else 	if (modifier.getModifierType() == TicketItemModifier.NO_MODIFIER) {
									//Make sure that "NO" gets added to the no modifier
									name = tabSpacing + "- NO " + name;
								}else if (modifierGroupName.equalsIgnoreCase("Special Instructions")) {
									name = tabSpacing + "%% " + name + "%%";
								}else
								{
									name = tabSpacing + "+ " + name;
								}
								Row row = new Row();
								row.setItemCount(modifier.getItemCount());
								row.setItemName(name);
								row.setItemNo(modifier.getId());
								rows.add(row);
							}
						}
					}
				}
				//Add a 'blank' row to help separate the dishes
				if(ticketItem.isShouldPrintToKitchen() && !ticketItem.isPrintedToKitchen()) {
					Row blankRow = new Row(0,"-------------------------------------", 0);
					rows.add(blankRow);
				}
				ticketItem.setPrintedToKitchen(true);
			}
		}
		//Remove the last blank row, because it looks silly
		rows.remove(rows.size()-1);
		setRows(rows);
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Row item = (Row) rows.get(rowIndex);
		
		switch(columnIndex) {
		case 0:
			return String.valueOf(item.getItemNo());
			
		case 1:
			return item.getItemName();
			
		case 2:
			return String.valueOf(item.getItemCount());
		}
		
		return null;
	}

	private class Row {
		private int itemCount;
		private String itemName;
		private int itemNo;
		
		public Row() {
			super();
		}

		public Row(int itemCount, String itemName, int itemNo) {
			super();
			this.itemCount = itemCount;
			this.itemName = itemName;
			this.itemNo = itemNo;
		}

		public int getItemCount() {
			return itemCount;
		}

		public void setItemCount(int itemCount) {
			this.itemCount = itemCount;
		}

		public String getItemName() {
			return itemName;
		}

		public void setItemName(String itemName) {
			this.itemName = itemName;
		}

		public int getItemNo() {
			return itemNo;
		}

		public void setItemNo(int itemNo) {
			this.itemNo = itemNo;
		}
		
		
	}
}
