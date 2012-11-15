package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.tariffservices.service.EWalletException_Exception;
import zw.co.esolutions.ewallet.tariffservices.service.Tariff;
import zw.co.esolutions.ewallet.tariffservices.service.TariffStatus;
import zw.co.esolutions.ewallet.tariffservices.service.TariffTable;
import zw.co.esolutions.ewallet.tariffservices.service.TariffType;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.MoneyUtil;
import zw.co.esolutions.ewallet.util.NumberUtil;

public class EditTariffBean extends PageCodeBase{

	//Instances
	private Tariff tariff ;
	private List<SelectItem> tariffTableMenu;
	private String tariffTableItem;
	private List<TariffTable> tariffTableList;
	private boolean showFixedField;
	private boolean showScaledFields;
	private double upperLimit;
	private double lowerLimit;
	private double value;
	private String tariffId;
	
	public EditTariffBean() {
		super();
		/*if(this.getTariff().getTariffTable() == null) {
			this.setShowFixedField(false);
			this.setShowScaledFields(false);
		}*/
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>> Tapinda.");
		if(this.getTariffId() == null) {
			this.setTariffId((String)super.getRequestScope().get("tariffId"));
			this.setTariffTableItem((String)super.getRequestScope().get("tariffTableId"));
			TariffTable table = super.getTariffService().findTariffTableById(this.getTariffTableItem());
			if(table.getTariffType().equals(TariffType.FIXED_AMOUNT) || 
					table.getTariffType().equals(TariffType.FIXED_PERCENTAGE)) {
				this.setShowScaledFields(true);
			}
		}
		
	}
	
	private void cleanBean() {
		tariff  = null;
		tariffTableMenu =null;
		tariffTableItem = null;
		tariffTableList = null;
		showFixedField = false;
		showScaledFields = false;
		upperLimit = 0;
		lowerLimit = 0;
		value = 0;
		tariffId = null;
	}
	@SuppressWarnings("unchecked")
	public String editTariffAction() {
		String report = this.checkAttributes();
		String tariffCompliance = null;
		TariffTable oldTable = null;
		if(report != null) {
			//Report Error.
			super.setErrorMessage(report);
			return "failure";
		}
		try {
			
			// Find Table
			oldTable = super.getTariffService().findTariffTableById(this.getTariffTableItem());
			
			// Check Tariff and Tariff Table Compliance
			tariffCompliance = this.checkTariffAndTableCompliance(oldTable);
			if(tariffCompliance != null) {
				super.setErrorMessage(tariffCompliance);
				return "failure";
			}
			
			//Setting Dates and Enums
			this.getTariff().setTariffTable(oldTable);
			this.changeTariffValueDoubleToLong();
			this.getTariff().setOldTariffId(this.getTariffId());
			System.out.println(">>>>>>>>>>>>>>>>>>>>. Value = "+getTariff().getValue());
			Tariff tf = super.getTariffService().editCommission(this.getTariff(), super.getJaasUserName());
			this.setTariff(tf);
		   if(tf == null || tf.getId() == null) {
				report = "Commission exists.";
				super.setErrorMessage(report);
				return "failure";
			}
		   
			this.setTariffTableItem(this.getTariff().getTariffTable().getId());
			this.setTariffId(this.getTariff().getId());
			this.roundOffTariffValuesToCurrency();
		} catch (EWalletException_Exception e) {
			if(TariffStatus.AWAITING_APPROVAL.toString().equalsIgnoreCase(e.getMessage())) {
				report = "This Commission already exists, awaiting approval.";
			} else {
				e.printStackTrace();
				report = PageCodeBase.ERROR_MESSAGE;
			}
		} catch (Exception e) {
			e.printStackTrace();
			report = PageCodeBase.ERROR_MESSAGE;
				
		} 
		
		//Report
		if(report != null) {
			//Report Error.
			super.setErrorMessage(report);
			return "failure";
		}
		report = "Commission Successfully Edited.";
		super.setInformationMessage(report);
		super.getRequestScope().put("tariffTableId", this.getTariff().getTariffTable().getId());
		super.getRequestScope().put("editTariff", report);
		super.getRequestScope().put("tariffId", this.getTariff().getId());
		super.gotoPage("/admin/viewCommissionTable.jspx");
		this.cleanBean();
		return "success";
	}
	
	
	@SuppressWarnings("unchecked")
	public String cancelTariffCreation() {
		super.getRequestScope().put("tariffTableId", this.getTariffTableItem());
		super.gotoPage("/admin/viewCommissionTable.jspx");
		this.cleanBean();
		return "success";
	}
	
	public void handleTableValueChange(ValueChangeEvent event) {
		String tableId = null;
		TariffTable table = null;
		tableId = (String)event.getNewValue();
		if(tableId != null) {
			if(!tableId.equalsIgnoreCase("none") || !tableId.equalsIgnoreCase("nothing")) {
				table = super.getTariffService().findTariffTableById(tableId);
				if(table != null) {
					if(table.getTariffType().equals(TariffType.SCALED)) {
						this.setShowFixedField(false);
					} else {
						this.setShowScaledFields(true);
					}
				}
			}
		}
	}

	/*
	 * Method for missing fields
	 */
	private String checkAttributes(){
		StringBuffer buffer = new StringBuffer("The following values are required: ");
		int length = 0;
		if(this.getTariffTableItem().equalsIgnoreCase("none")) {
			buffer.append("Tariff Table, ");
		}   if(this.getLowerLimit() < 0) {
			buffer.append("Lower Limit, ");
		} if(this.getUpperLimit() < 0) {
			buffer.append("Upper Limit, ");
		} if(this.getValue() < 0) {
			buffer.append("Tariff Value, ");
		} 
		length = buffer.toString().length();
				
		if(length > 40) {
			buffer.replace(length-2, length, " ");
			return buffer.toString();
		}
		return null;
	}
	
	private String checkTariffAndTableCompliance(TariffTable table){
		StringBuffer buffer = new StringBuffer("The following values are required: ");
		int length = 0;
		if(this.getTariffTableItem().equalsIgnoreCase("none")) {
			buffer.append("Commission Table, ");
		}   else if(this.getTariffTableItem().equalsIgnoreCase("nothing")){
			buffer.append("No Commission Tables.");
		} else {
			if(table == null) {
				buffer.append("There are no Commission Tables.");
			} else {
				if(table.getTariffType().equals(TariffType.FIXED_AMOUNT) || 
						table.getTariffType().equals(TariffType.FIXED_PERCENTAGE)) {
							if(this.getValue() < 0) {
								return "Commission Value cannot be less 0 for this type of Tariff";
							}
				} else if(table.getTariffType().equals(TariffType.SCALED)) {
					if(this.getUpperLimit() < 0 && this.getLowerLimit() < 0) {
						return "Lower Limit, Upper Limit values required. ";
					}
					if(this.getUpperLimit() < this.getLowerLimit()) {
						return "Invalid values. Upper Limit cannot be greater than Lower Limit Values.";
					}
				}
			}
		}
		length = buffer.toString().length();
				
		if(length > 40) {
			buffer.replace(length-2, length, " ");
			return buffer.toString();
		}
		return null;
	}
	
	private void changeTariffValueDoubleToLong() {
		this.getTariff().setUpperLimit(MoneyUtil.convertToCents(this.getUpperLimit()));
		this.getTariff().setLowerLimit(MoneyUtil.convertToCents(this.getLowerLimit()));
		this.getTariff().setValue(MoneyUtil.convertToCents(this.getValue()));
	}
	
	private void roundOffTariffValuesToCurrency() {
		this.setUpperLimit(NumberUtil.roundDouble(this.getUpperLimit(), 2));
		this.setLowerLimit(NumberUtil.roundDouble(this.getLowerLimit(), 2));
		this.setValue(NumberUtil.roundDouble(this.getValue(), 2));
	}
	
	public Tariff getTariff() {
		if(this.tariff == null && this.getTariffId() != null) {
			try {
				this.tariff = super.getTariffService().findTariffById(this.getTariffId());
				System.out.println(">>>>>>>>>>>>>>>>>> Tariff In If = "+this.tariff);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} if(this.tariff == null) {
			tariff = new Tariff();
		}
		System.out.println(">>>>>>>>>>>>>>>>>> Tariff = "+this.tariff.getUpperLimit());
		return tariff;
	}

	public void setTariff(Tariff tariff) {
		this.tariff = tariff;
	}

	public List<SelectItem> getTariffTableMenu() {
		if(this.tariffTableMenu == null) {
			this.tariffTableMenu = new ArrayList<SelectItem>();
			if(this.getTariffTableList() == null) {
				this.tariffTableMenu.add(new SelectItem("nothing", " No Tables "));
			} else {
				this.tariffTableMenu.add(new SelectItem("none", "--select--"));
				for(TariffTable table : this.getTariffTableList()) {
					this.tariffTableMenu.add(new SelectItem(table.getId(), "["+table.getTransactionType().toString()+" : "+
							table.getAgentType().toString()+" - "+table.getTariffType().toString()+"] From: "+DateUtil.convertToShortUploadDateFormatNumbersOnly(
							DateUtil.convertToDate(table.getEffectiveDate()))));
				}
			}
		}
		return tariffTableMenu;
	}

	public void setTariffTableMenu(List<SelectItem> tariffTableMenu) {
		this.tariffTableMenu = tariffTableMenu;
	}

	public String getTariffTableItem() {
		try {
			if(this.tariffTableItem == null) {
				this.tariffTableItem = (String)super.getRequestScope().get("tariffTableId");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return tariffTableItem;
	}

	public void setTariffTableItem(String tariffTableItem) {
		this.tariffTableItem = tariffTableItem;
	}

	public List<TariffTable> getTariffTableList() {
		if(this.tariffTableList == null) {
			this.tariffTableList = new ArrayList<TariffTable>();
			this.tariffTableList.add(super.getTariffService().findTariffTableById(this.getTariffTableItem()));
		}
		return tariffTableList;
	}

	public void setTariffTableList(List<TariffTable> tariffTableList) {
		this.tariffTableList = tariffTableList;
	}

	public boolean isShowFixedField() {
		return showFixedField;
	}

	public void setShowFixedField(boolean showFixedField) {
		this.showFixedField = showFixedField;
	}

	public boolean isShowScaledFields() {
		if(this.getTariff() != null && this.getTariff().getTariffTable() != null) {
			TariffTable table = this.getTariff().getTariffTable();
			if(table.getTariffType().equals(TariffType.FIXED_AMOUNT) || 
					table.getTariffType().equals(TariffType.FIXED_PERCENTAGE)) {
				this.showScaledFields = true;
			} else {
				this.showScaledFields = false;
			}
		}
		return showScaledFields;
	}

	public void setShowScaledFields(boolean showScaledFields) {
		this.showScaledFields = showScaledFields;
	}

	public double getUpperLimit() {
		if(this.upperLimit == 0 && this.getTariff().getId() != null) {
			this.upperLimit = MoneyUtil.convertToDollars(this.getTariff().getUpperLimit());
		}
		return upperLimit;
	}

	public void setUpperLimit(double upperLimit) {
		this.upperLimit = upperLimit;
	}

	public double getLowerLimit() {
	    if(this.lowerLimit == 0 && this.getTariff().getId() != null) {
	    	this.lowerLimit = MoneyUtil.convertToDollars(this.getTariff().getLowerLimit());
	    }
		return lowerLimit;
	}

	public void setLowerLimit(double lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public double getValue() {
		if(this.value == 0 && this.getTariff().getId() != null) {
			this.value = MoneyUtil.convertToDollars(this.getTariff().getValue());
		}
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public void setTariffId(String tariffId) {
		this.tariffId = tariffId;
	}

	public String getTariffId() {
		if(this.tariffId == null) {
			try {
				this.tariffId =(String)super.getRequestScope().get("tariffId");
				
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return tariffId;
	}

}
