package zw.co.esolutions.ewallet.adminweb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.tariffservices.service.Tariff;
import zw.co.esolutions.ewallet.tariffservices.service.TariffTable;
import zw.co.esolutions.ewallet.tariffservices.service.TariffType;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.MoneyUtil;
import zw.co.esolutions.ewallet.util.NumberUtil;

public class CreateTariffBean extends PageCodeBase{

	//Instances
	private Tariff tariff = new Tariff();
	private List<SelectItem> tariffTableMenu;
	private String tariffTableItem;
	private List<TariffTable> tariffTableList;
	private boolean showFixedField;
	private boolean showScaledFields;
	private boolean disableTableMenu;
	private double upperLimit;
	private double lowerLimit;
	private double value;
	private TariffTable table;
	private String fromPageName;
	
	//For request scope
	private String createTariffParam;
	
	public CreateTariffBean() {
		super();
		if(this.getTariff().getTariffTable() == null) {
			this.setShowFixedField(false);
			this.setShowScaledFields(false);
		}
		if(super.getRequestScope().get("createTariff") != null) {
			this.setDisableTableMenu(true);
			this.setCreateTariffParam((String)super.getRequestScope().get("createTariff"));
			this.setTariffTableItem((String)super.getRequestScope().get("tariffTableId"));
			this.setTable(super.getTariffService().findTariffTableById(this.getTariffTableItem()));
			if(this.getTable().getTariffType().equals(TariffType.FIXED_AMOUNT) || 
					this.getTable().getTariffType().equals(TariffType.FIXED_PERCENTAGE)) {
				this.setShowScaledFields(true);
			}
		}
		if(this.getFromPageName() == null) {
			this.setFromPageName((String)super.getRequestScope().get("createTariffTable"));
		}
		
	}
	
	private void cleanBean() {
		tariff = new Tariff();
		tariffTableMenu = null;
		tariffTableItem = null;
		tariffTableList = null;
		showFixedField = false;
		showScaledFields = false;
		disableTableMenu = false;
		upperLimit = 0;
		lowerLimit = 0;
		value = 0;
		table = null;
		fromPageName = null;
		createTariffParam = null;
	}
	
	@SuppressWarnings("unchecked")
	public String createTariffAction() {
		String report = this.checkAttributes();
		String tariffCompliance = null;
		TariffTable table = null;
		@SuppressWarnings("unused")
		List<Tariff> tariffList = null;
		Tariff tf =null;
		if(report != null) {
			//Report Error.
			super.setErrorMessage(report);
			return "failure";
		}
		try {
			
			// Find Table
			table = super.getTariffService().findTariffTableById(this.getTariffTableItem());
			tariffList = super.getTariffService().getTariffsByTariffTableId(table.getId());
			// Check Tariff and Tariff Table Compliance
			tariffCompliance = this.checkTariffAndTableCompliance(table);
			if(tariffCompliance != null) {
				super.setErrorMessage(tariffCompliance);
				return "failure";
			}
		
			System.out.println(">>>>>>>>>> Table = "+table);
			//Setting Dates and Enums
			this.getTariff().setTariffTable(table);
			this.changeTariffValueDoubleToLong();
			tf = (super.getTariffService().createCommission(this.getTariff(), super.getJaasUserName()));
			this.setTariff(tf);
			if(tf == null || tf.getId() == null) {
				report = "This Commission already exists. You can edit it only.";
				super.setErrorMessage(report);
				return "failure";
			}
			System.out.println(">>>>>>>>>>>>> Table Id = "+this.getTariff().getTariffTable().getId());
			this.setTariffTableItem(this.getTariff().getTariffTable().getId());
			this.roundOffTariffValuesToCurrency();
			this.setTariff(new Tariff());
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
		report = "Commission Successfully Added to Table.";
		super.setInformationMessage(report);
		if(this.getCreateTariffParam() != null) {
			System.out.println(">>>>>>>>>>>> Table ID = "+this.getTariffTableItem());
			super.getRequestScope().put("tariffTableId", this.getTariffTableItem());
			super.getRequestScope().put("createTariff", report);
			if("createTariffTable".equalsIgnoreCase(this.getFromPageName())) {
				super.getRequestScope().put("createTariffTable", "createTariffTable");
			}
			super.gotoPage("/admin/viewCommissionTable.jspx");
			this.cleanBean();
			return "create";
		} 
		this.cleanBean();
		return "success";
	}
	
	@SuppressWarnings("unchecked")
	public String cancelTariffCreation() {
		if(this.getCreateTariffParam() != null) {
			this.getRequestScope().put("tariffTableId", this.getTariffTableItem());
			this.cleanBean();
			return "cancel";
		} else {
			this.setTariff(new Tariff());
		}
		super.gotoPage("/admin/adminHome.jspx");
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
			buffer.append("Tariff Table, ");
		}   else if(this.getTariffTableItem().equalsIgnoreCase("nothing")){
			buffer.append("No Tariff Tables.To continue with this operation, Tariff Tables must be created.");
		} else {
			if(table == null) {
				buffer.append("There are no Tariff Tables.");
			} else {
				if(table.getTariffType().equals(TariffType.FIXED_AMOUNT) || 
						table.getTariffType().equals(TariffType.FIXED_PERCENTAGE)) {
							if(this.getValue() < 0) {
								return "Tariff Value cannot be less 0 for this type of Tariff";
							}
				} else if(table.getTariffType().equals(TariffType.SCALED)) {
					if(this.getUpperLimit() < 0 && this.getLowerLimit() < 0) {
						return "Lower Limit, Upper Limit values required. ";
					}
					if(this.getUpperLimit() < this.getLowerLimit()) {
						return "Invalid values. Lower Limit cannot be greater than Upper Limit Values.";
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
		if(this.tariff.getId() == null) {
				this.setShowFixedField(false);
				this.setShowScaledFields(false);
			
		}
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
					this.tariffTableMenu.add(new SelectItem(table.getId(), "["+table.getTransactionType().toString()+" - "+table.getTariffType().toString()+"] From: "+DateUtil.convertToShortUploadDateFormatNumbersOnly(
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
		if(this.tariffTableItem == null) {
			try {
				this.tariffTableItem = (String)super.getRequestScope().get("tariffTableId");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return tariffTableItem;
	}

	public void setTariffTableItem(String tariffTableItem) {
		this.tariffTableItem = tariffTableItem;
	}

	public List<TariffTable> getTariffTableList() {
		if(this.tariffTableList == null || tariffTableList.isEmpty()) {
			if(this.getCreateTariffParam() == null) {
				this.tariffTableList = super.getTariffService().getEffectiveTariffTablesForBank(this.getBankId());
			} else {
				this.tariffTableList = new ArrayList<TariffTable>();
				tariffTableList.add(super.getTariffService().findTariffTableById(this.getTariffTableItem()));
			}
			
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
		return showScaledFields;
	}

	public void setShowScaledFields(boolean showScaledFields) {
		this.showScaledFields = showScaledFields;
	}

	public double getUpperLimit() {
		return upperLimit;
	}

	public void setUpperLimit(double upperLimit) {
		this.upperLimit = upperLimit;
	}

	public double getLowerLimit() {
		return lowerLimit;
	}

	public void setLowerLimit(double lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public void setTable(TariffTable table) {
		this.table = table;
	}

	public TariffTable getTable() {
		if((this.table == null || this.table.getId() == null) && this.getTariffTableItem() != null) {
			try {
				this.table = super.getTariffService().findTariffTableById(this.getTariffTableItem());
				if(this.table.getTariffType().equals(TariffType.FIXED_AMOUNT) || 
						this.table.getTariffType().equals(TariffType.FIXED_PERCENTAGE)) {
					this.setShowScaledFields(true);
				}
				this.setDisableTableMenu(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return table;
	}

	public void setCreateTariffParam(String createTariffParam) {
		this.createTariffParam = createTariffParam;
	}

	public String getCreateTariffParam() {
		if(this.createTariffParam == null) {
			try {
				this.createTariffParam = (String)super.getRequestScope().get("createTariff");
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return createTariffParam;
	}

	public boolean isDisableTableMenu() {
		return disableTableMenu;
	}

	public void setDisableTableMenu(boolean disableTableMenu) {
		this.disableTableMenu = disableTableMenu;
	}
	
	private String getBankId() {
		String bankId = null;
		try {
			if(tariffTableItem != null) {
				bankId = super.getTariffService().findTariffTableById(tariffTableItem).getBankId();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return bankId;
	}

	public void setFromPageName(String fromPageName) {
		this.fromPageName = fromPageName;
	}

	public String getFromPageName() {
		if(this.fromPageName == null) {
			this.fromPageName = (String)super.getRequestScope().get("createTariffTable");
		}
		return fromPageName;
	}

}
