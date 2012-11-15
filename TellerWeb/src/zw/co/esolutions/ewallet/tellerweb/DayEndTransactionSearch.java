package zw.co.esolutions.ewallet.tellerweb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.process.DayEnd;
import zw.co.esolutions.ewallet.process.DayEndStatus;
import zw.co.esolutions.ewallet.process.ProcessServiceSOAPProxy;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.profileservices.service.ProfileServiceSOAPProxy;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.JsfUtil;

public class DayEndTransactionSearch extends PageCodeBase{
	private List<DayEnd> list;
	private List<SelectItem> criteriaList;
	private List<SelectItem> statusList;
	private List<SelectItem> tellerMenu;
	private String selectedCriteria;
	private String selectedStatus;
	private String selectedTeller;
	private String selectedBranch;
	private String bankName;
	private List<SelectItem> branchMenu;
	private List<Bank> bankList;
	private List<BankBranch> bankBranchList;
	private BankBranch bankBranch;
	private boolean renderBranchInfo;
	private boolean renderTellerInfo;
	private boolean renderParameters;
	private Date fromDate;
	private Date toDate;
	private Date defaultDate;
	
	
private static Logger LOG ;
	
	static{
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(DayEndTransactionSearch.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + DayEndTransactionSearch.class);
		}
	}
	
	
	
	
	
	public boolean isRenderParameters() {
		return renderParameters;
	}






	public void setRenderParameters(boolean renderParameters) {
		this.renderParameters = renderParameters;
	}






	public Date getDefaultDate() {
		if(defaultDate == null){
			defaultDate=new Date();
		}
		return defaultDate;
	}






	public void setDefaultDate(Date defaultDate) {
		this.defaultDate = defaultDate;
	}






	public Date getFromDate() {
		fromDate = (fromDate == null)? getDefaultDate() : fromDate;
	
		return fromDate;
	}



	


	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}



	public Date getToDate() {
		toDate = (toDate == null)? getDefaultDate() : toDate;
		
		
		return toDate;
	}



	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}



	public boolean isRenderBranchInfo() {
		return renderBranchInfo;
	}



	public void setRenderBranchInfo(boolean renderBranchInfo) {
		this.renderBranchInfo = renderBranchInfo;
	}



	public boolean isRenderTellerInfo() {
		return renderTellerInfo;
	}



	public void setRenderTellerInfo(boolean renderTellerInfo) {
		this.renderTellerInfo = renderTellerInfo;
	}



	public DayEndTransactionSearch() {
		super();
		if(this.bankName == null) {
			try {
				List<Profile> profileList = null;
			ProfileServiceSOAPProxy profileService= new ProfileServiceSOAPProxy();
			BankServiceSOAPProxy bankService= new BankServiceSOAPProxy();
			Profile profile=profileService.getProfileByUserName(getJaasUserName());
			String branchId=profile.getBranchId();
			boolean isSupervisor=profileService.canDo(getJaasUserName(), "DAYENDAPPROVAL");
			LOG.debug("is SUpervisor   " +isSupervisor);
			if(isSupervisor){
				LOG.debug("Branch ID "+branchId+"Profile list sizelist size     "+profileService.getProfileByBranchIdAndAccessRight(branchId, "WITHDRAWCASH").size());
			profileList=profileService.getProfileByBranchIdAndAccessRight(branchId, "WITHDRAWCASH");
			}else{
				profileList=new ArrayList<Profile>();
				Profile profile1=profileService.getProfileByUserName(getJaasUserName());
				profileList.add(profile1);
			}
			this.tellerMenu=getTellerMenu(profileList);
			LOG.debug("Done getting teller menu");
			
			LOG.debug("Branch id   "+branchId);
			BankBranch bankBranch =bankService.findBankBranchById(branchId);
			
			LOG.debug("Bank Name  "+bankBranch.getBank().getName());
				this.bankName = bankBranch.getBank().getName();
				LOG.debug("Bank Name ::::"+bankName);
				LOG.debug("----------------------------------Done sending bank name-----------------------");
			} catch (Exception e) {
				// TODO: handle exception
				LOG.debug("Exception with bank name retrival   "+e.getMessage());
				e.printStackTrace();
			}
		}
	}

	
	
	public void processCriteriaChange(ValueChangeEvent e){
		LOG.debug("Processing value change teller branch"+(String)e.getNewValue());
		String newValue=(String) e.getNewValue();
		if(newValue.equalsIgnoreCase("Teller")){
			renderBranchInfo=false;
			renderTellerInfo=true;
			renderParameters=true;
		}else if(newValue.equalsIgnoreCase("Branch")){
			renderBranchInfo=true;
			renderTellerInfo=false;
			renderParameters=true;
		}else{
			//
		}
		LOG.debug("Value      "+newValue);
		LOG.debug(" rendered Value branch   "+renderBranchInfo);
		LOG.debug(" rendered Value teller  "+renderTellerInfo);
		LOG.debug(" rendered Value parameters  "+renderParameters);
	}
	
	private List<SelectItem> getTellerMenu(List<Profile> profileList) {
		LOG.debug("getting teller menu  "+profileList.size());
		this.tellerMenu= new ArrayList<SelectItem>();
		for(Profile profile: profileList){
			LOG.debug("Profile   "+profile.getUserName() +"  profile id    "+profile.getId());
			SelectItem item = new SelectItem(profile.getId(),profile.getFirstNames() +" "+profile.getLastName());
			this.tellerMenu.add(item);
		}
		return this.tellerMenu;
	}





	public List<SelectItem> getTellerMenu() {
		
		return tellerMenu;
	}





	public void setTellerMenu(List<SelectItem> tellerMenu) {
		this.tellerMenu = tellerMenu;
	}





	public List<SelectItem> getBranchMenu() {
		if(this.branchMenu == null) {
			this.branchMenu = new ArrayList<SelectItem>();
			if(this.getBankList() == null || this.getBankList().isEmpty()) {
				branchMenu.add(new SelectItem("nothing", "No Branches"));
			} else {
//				branchMenu.add(new SelectItem("none", "<--select-->"));
//				branchMenu.add(new SelectItem("all", "All"));
//				for(BankBranch bk : this.getBankBranchList()) {
//					branchMenu.add(new SelectItem(bk.getId(),bk.getName()));
//				}
				if(this.getBankBranch() != null) {
					branchMenu.add(new SelectItem(this.getBankBranch().getId(),
							this.getBankBranch().getName()));
				}
			}
			
		}
		return branchMenu;
	}





	public List<Bank> getBankList() {
		if(this.bankList == null || this.bankList.isEmpty()) {
			try {
				//this.bankList = super.getBankService().getBank();
				LOG.debug("Bank Name ::::::"+bankName);
				this.bankList = super.getBankService().getBankByName(this.bankName);
				LOG.debug("Bank List   "+bankList);
			} catch (Exception e) {
				
			}
		}
		return bankList;
	}





	public void setBankList(List<Bank> bankList) {
		this.bankList = bankList;
	}





	public List<BankBranch> getBankBranchList() {
		return bankBranchList;
	}





	public void setBankBranchList(List<BankBranch> bankBranchList) {
		this.bankBranchList = bankBranchList;
	}





	public BankBranch getBankBranch() {
		if(this.bankBranch == null) {
			try {
				ProfileServiceSOAPProxy profileProxy= new ProfileServiceSOAPProxy();
				Profile p = super.getProfileService().getProfileByUserName(super.getJaasUserName());
				bankBranch = super.getBankService().findBankBranchById(p.getBranchId());
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return bankBranch;
	}





	public void setBankBranch(BankBranch bankBranch) {
		this.bankBranch = bankBranch;
	}





	public void setBranchMenu(List<SelectItem> branchMenu) {
		this.branchMenu = branchMenu;
	}





	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public List<DayEnd> getList() {
		return list;
	}

	public void setList(List<DayEnd> list) {
		this.list = list;
	}
	
	
/*
 * tellerId
 * tellerId and date Range
 * status
 * branch and daterange
 * branch, daterange and status
 * teller null dayEndSummary
 */
	/*
	 * Criteria: Branch- 1. Branch and date range and status
	 *         : Teller  2. Teller and date Range and status
	 * 
	 */
	
	public List<SelectItem> getCriteriaList() {
		//ProfileServiceSOAPProxy proxy = new ProfileServiceSOAPProxy();
		
		if (criteriaList == null) {
			criteriaList = new ArrayList<SelectItem>();
			criteriaList.add(new SelectItem("--Selecr--", "-Select-"));
			criteriaList.add(new SelectItem("Teller", "Teller"));
			if(this.checkProfileAccessRight("DAYENDAPPROVAL")){
			criteriaList.add(new SelectItem("Branch", "Branch"));
			}
		}
		return criteriaList;
	}

	private boolean checkProfileAccessRight(String accessRightName) {
		ProfileServiceSOAPProxy proxy = new ProfileServiceSOAPProxy();
		
		return proxy.canDo(getJaasUserName(), accessRightName);
	}
	
	public void setCriteriaList(List<SelectItem> criteriaList) {
		this.criteriaList = criteriaList;
	}

	public String getSelectedCriteria() {
		return selectedCriteria;
	}

	public void setSelectedCriteria(String selectedCriteria) {
		this.selectedCriteria = selectedCriteria;
	}

	public String getSelectedStatus() {
		
		return selectedStatus;
	}

	public void setSelectedStatus(String selectedStatus) {
		this.selectedStatus = selectedStatus;
	}

	public String getSelectedTeller() {
		return selectedTeller;
	}

	public void setSelectedTeller(String selectedTeller) {
		this.selectedTeller = selectedTeller;
	}

	public String getSelectedBranch() {
		
		return selectedBranch;
	}

	public void setSelectedBranch(String selectedBranch) {
		this.selectedBranch = selectedBranch;
	}

	public String search(){
		ProcessServiceSOAPProxy proxy= new ProcessServiceSOAPProxy();
		List<DayEnd>results=new ArrayList<DayEnd>();
		try {
			if(renderTellerInfo){
				LOG.debug("Teller based search");
				LOG.debug(fromDate+"fromDate ::::::toDate:::"+toDate+" status"+getSelectedStatus()+ " branch "+getSelectedBranch());
				results=	proxy.getDayEndsByDayEndStatusAndDateRangeAndTeller(DateUtil.convertToXMLGregorianCalendar(fromDate),DateUtil.convertToXMLGregorianCalendar(toDate), DayEndStatus.valueOf(getSelectedStatus()), getSelectedTeller());
			LOG.debug("A    List result     "+list);
			}else if(renderBranchInfo){
				LOG.debug("branch based search");
				LOG.debug(fromDate+"fromDate ::::::toDate:::"+toDate+" status"+getSelectedStatus()+ " branch "+getSelectedBranch());
				results=	proxy.getDayEndsByDayEndStatusAndDateRangeAndBranch(DateUtil.convertToXMLGregorianCalendar(fromDate), DateUtil.convertToXMLGregorianCalendar(toDate), DayEndStatus.valueOf(getSelectedStatus()), getSelectedBranch());
		LOG.debug("B   List result     "+list);
			}
			setList(results);
			if(results != null &&results.size()>0){
				super.setInformationMessage("Search Results: "+list.size()+" entries found");
			}else{
				super.setInformationMessage("No Results found");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.debug("Exception msg");
		}
		return "";
	}

	public List<SelectItem> getStatusList() {
		if(statusList == null){
			this.statusList=JsfUtil.getSelectItemsAsList(DayEndStatus.values(), false);
		}
		return statusList;
	}

	public void setStatusList(List<SelectItem> statusList) {
		this.statusList = statusList;
	}

	public String viewDayEnd(){
		String search="Search";
		LOG.debug("Day end id value   "+super.getRequestParam().get("dayEndId"));
		super.getRequestScope().put("dayEndId", super.getRequestParam().get("dayEndId"));
		super.getRequestScope().put("search",search);
		
		super.gotoPage("/teller/dayEndSummary.jspx");
		return "viewDayEndSummary";
	}
}
