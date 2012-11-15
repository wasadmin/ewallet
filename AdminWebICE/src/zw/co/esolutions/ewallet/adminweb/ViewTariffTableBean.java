package zw.co.esolutions.ewallet.adminweb;

import java.util.List;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.tariffservices.service.Tariff;
import zw.co.esolutions.ewallet.tariffservices.service.TariffStatus;
import zw.co.esolutions.ewallet.tariffservices.service.TariffTable;

public class ViewTariffTableBean extends PageCodeBase{

	private TariffTable tariffTable;
	private List<Tariff> tariffList;
	private String tariffTableId;
	private String selectedTariffId;
	private String msg;
	private boolean approver;
	private boolean renderNew;
	private boolean renderApprove;
	private boolean renderDisapprove;
	private boolean renderActivate;
	private boolean renderDelete;
	private boolean renderRestore;
	private boolean renderEditable;
	private String approverMsg;
	private String fromPageName;
	private String initializeInfor;
	
	
	public ViewTariffTableBean() {
		super();
		if(this.getTariffTableId() == null) {
			this.setTariffTableId((String)super.getRequestScope().get("tariffTableId"));
		}
		if(this.getSelectedTariffId() == null) {
			this.setSelectedTariffId((String)super.getRequestScope().get("tariffId"));
		}
		if(this.getApproverMsg() == null) {
			this.setApproverMsg((String)super.getRequestScope().get("approveTariff"));
			if(this.getApproverMsg() != null) {
				this.setApprover(true);
			}/* else {
				this.setNonApprover(true);
			}*/
		}
		if(this.getFromPageName() == null) {
			this.setFromPageName((String)super.getRequestScope().get("createTariffTable"));
		}
		/*if(!isApprover()) {
			this.checkApprover();
		}*/
		
	}
	
	private void cleanBean() {
		this.tariffTableId = null;
		this.selectedTariffId = null;
		this.approverMsg = null;
		this.fromPageName = null;
		this.tariffTable = null;
		this.tariffList = null;
		approver = false;
		renderNew = false;
		renderApprove = false;
		renderDisapprove = false;
		renderActivate = false;
		renderDelete = false;
		renderRestore = false;
		this.initializeInfor = null;
	}
	
	@SuppressWarnings("unchecked")
	public String editTable() {
		if(this.getTariffTable().getStatus().equals(TariffStatus.DISAPPROVED) || 
				this.getTariffTable().getStatus().equals(TariffStatus.INACTIVE) ||
				this.getTariffTable().getStatus().equals(TariffStatus.DELETED)) {
			super.setInformationMessage("You cannot edit Commission Table with status: "+
					this.getTariffTable().getStatus().toString()+". ");
			return "failure";
		}
		super.getRequestScope().put("tariffTableId", this.getTariffTableId());
		super.gotoPage("/admin/editCommissionTable.jspx");
		this.cleanBean();
		return "success";
	}
	
	@SuppressWarnings("unchecked")
	public String newTariff() {
		if(this.getTariffTable().getStatus().equals(TariffStatus.DISAPPROVED) || 
				this.getTariffTable().getStatus().equals(TariffStatus.INACTIVE) ||
				this.getTariffTable().getStatus().equals(TariffStatus.DELETED)) {
			super.setInformationMessage("You cannot create a Commission for a For Commission Table with status: "+
					this.getTariffTable().getStatus().toString()+". ");
			return "failure";
		}
		super.getRequestScope().put("tariffTableId",super.getRequestParam().get("tariffTableId"));
		super.getRequestScope().put("createTariff", "createTariff");
		if("createTariffTable".equalsIgnoreCase(this.getFromPageName())) {
			super.getRequestScope().put("createTariffTable", "createTariffTable");
		}
		super.gotoPage("/admin/createCommission.jspx");
		this.cleanBean();
		return "success";
	}
	
	public String goBack() {
		if(isApprover()) {
			System.out.println(">>>  Is Approver In Back action, Message = "+this.getApproverMsg());
			if(this.getApproverMsg() != null) {
				super.gotoPage("/admin/approveCommission.jspx");
			} else {
				super.gotoPage("/admin/viewAllCommissionTables.jspx");
			}
		} else if(!isApprover()) {
			if("createTariffTable".equalsIgnoreCase(this.getFromPageName())) {
				super.gotoPage("/admin/createCommissionTable.jspx");
			}  else {
			System.out.println(">>>  Is Not Approver In Back action, Message = "+this.getApproverMsg());
			super.gotoPage("/admin/viewAllCommissionTables.jspx");
			}
		} 
		this.cleanBean();
		return "success";
	}
	
	public String logs(){
		super.gotoPage("/admin/viewLogs.jspx");
		return "logs";
	}
	
	@SuppressWarnings("unchecked")
	public String viewLogs(){
		try {
			super.getRequestScope().put("entityId",
					super.getRequestParam().get("entityId"));
			super.getRequestScope().put("entityName",
					super.getRequestParam().get("entityName"));
			super.getRequestScope().put("previous",
					super.getRequestParam().get("previous"));
			
			super.gotoPage("/admin/viewLogs.jspx");
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "logs";
	}
	
	public String approveTariff() {
		try {
			if(TariffStatus.ACTIVE.equals(this.getTariffTable().getStatus())) {
				super.setErrorMessage("Commission already approved.");
				return "failure";
			}
			this.getTariffTable().setStatus(TariffStatus.ACTIVE);
			
			this.setTariffTable(super.getTariffService().approveCommission(this.getTariffTable(), this.getJaasUserName()));
			if(this.getTariffTable() != null) {
				this.setTariffTableId(this.getTariffTable().getId());
			} else {
				throw new Exception("Failed to approve.");
			}
			//this.syncMsg();
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage("Error occurred. Operation aborted.");
			return "failure";
		}
		
		
		super.setInformationMessage("Commission approved successfully.");
		return "success";
	}
	
	public String disApproveTariff() {
		try {
			if(TariffStatus.DISAPPROVED.equals(this.getTariffTable().getStatus())) {
				super.setErrorMessage("Commission already disapproved.");
				return "failure";
			}
			this.getTariffTable().setStatus(TariffStatus.DISAPPROVED);
			
			this.setTariffTable(super.getTariffService().disapproveCommission(this.getTariffTable(), super.getJaasUserName()));
			if(this.getTariffTable() != null && this.getTariffTable().getId() != null) {
				this.setTariffTableId(this.getTariffTable().getId());
			}
			else {
				throw new Exception("Failed to approve.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage("Error occurred. Operation aborted.");
			return "failure";
		}
		super.setInformationMessage("Commission disapproved.");
		return "success";
	}
	
	public String deleteTariff() {
		try {
			if(TariffStatus.DELETED.equals(this.getTariffTable().getStatus())) {
				super.setErrorMessage("Commission already deleted.");
				return "failure";
			}
			this.getTariffTable().setStatus(TariffStatus.DELETED);
			super.getTariffService().deleteTariffTable(getTariffTable().getId(), super.getJaasUserName());
			
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			return "failure";
		}
		super.setInformationMessage("Commission Table deleted.");
		return "success";
	}
	
	public String activateTariff() {
		this.getTariffTable().setStatus(TariffStatus.ACTIVE);
		try {
			//this.super.getTariffService().updateTariffTable(getTariffTable(), super.getJaasUserName());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.setInformationMessage("Tariffs activated.");
		return "success";
	}
	
	@SuppressWarnings("unchecked")
	public String editTariff() {
		super.getRequestScope().put("tariffId", super.getRequestParam().get("tariffId"));
		this.setTariffTableId(super.getTariffService().findTariffById((String)super.getRequestParam().get("tariffId")).getTariffTable().getId());
		super.getRequestScope().put("tariffTableId",this.getTariffTableId());
		super.gotoPage("/admin/editCommission.jspx");
		this.cleanBean();
		return "success";
	}
	
/*	private void checkApprover() {
		Profile profile = null;
		//RoleAccessRight roleAccessRight = null;
		try {
			profile = super.getProfileService().getProfileByUserName(super.getJaasUserName());
			//roleAccessRight = super.getProfileService().getRoleAccessRightByRoleActionNameAndStatus(profile.getRole().getId(), 
			//		"APPROVECOMMISSION", AccessRightStatus.ENABLED);
			if(super.getProfileService().canDo(profile.getUserName(), "APPROVECOMMISSION")) {
				this.setApprover(true);
			}
			//roleAccessRight = super.getProfileService().getRoleAccessRightByRoleActionNameAndStatus(profile.getRole().getId(), 
			//		"CREATECOMMISSION", AccessRightStatus.ENABLED);
			if(super.getProfileService().canDo(profile.getUserName(), "CREATECOMMISSION")) {
				this.setRenderNew(true);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}*/
	
	
	public TariffTable getTariffTable() {
		try {
			if((tariffTable == null || tariffTable.getId() == null ) && this.getTariffTableId() != null) {
				this.tariffTable = super.getTariffService().findTariffTableById(this.getTariffTableId());
			}
			if(this.tariffTable != null && this.tariffTable.getId() != null) {
				List<Tariff> tariffs = super.getTariffService().getTariffsByTariffTableId(this.tariffTable.getId());
				if(tariffs == null) {
					if(this.isApprover()) {
						this.setRenderNew(false);
					} else {
						this.setRenderNew(true);
					}
				} else {
					if(tariffs.isEmpty()) {
						if(this.isApprover()) {
							this.setRenderNew(false);
						} else {
							this.setRenderNew(true);
						}
					} else {
						if(!(zw.co.esolutions.ewallet.tariffservices.service.TariffType.SCALED.equals(this.tariffTable.getTariffType()) || 
								zw.co.esolutions.ewallet.tariffservices.service.TariffType.SCALED_PERCENTAGE.equals(this.tariffTable.getTariffType()))) {
							if(tariffs.size() < 1) {
								if(this.isApprover()) {
									this.setRenderNew(false);
								} else {
									this.setRenderNew(true);
								}
							} else {
								this.setRenderNew(false);
							}
						} else {
							if(this.isApprover()) {
								this.setRenderNew(false);
							} else {
								this.setRenderNew(true);
							}
						}
					}
				}
			}
			if(this.tariffTable != null && this.isApprover()) {
				System.out.println(">>>>>>>>>>> Is Approver .");
				if(this.tariffTable.getStatus().equals(TariffStatus.ACTIVE)) {
					//this.setRenderActivate(false);
					this.setRenderApprove(false);
					//this.setRenderDelete(false);
					if(this.getApproverMsg() != null) {
						this.setRenderDisapprove(true);
					} else {
						this.setRenderDisapprove(false);
					}
					//this.setRenderRestore(false);
					
				} if(this.tariffTable.getStatus().equals(TariffStatus.DISAPPROVED)) {
					//this.setRenderActivate(false);
					if(this.getApproverMsg() != null) {
						this.setRenderApprove(true);
					} else {
						this.setRenderApprove(false);
					}
					//this.setRenderDelete(true);
					this.setRenderDisapprove(false);
					//this.setRenderRestore(false);
					
				} if(this.tariffTable.getStatus().equals(TariffStatus.INACTIVE)) {
					//this.setRenderActivate(true);
					this.setRenderApprove(false);
					//this.setRenderDelete(false);
					this.setRenderDisapprove(false);
					//this.setRenderRestore(false);
					
				} if (this.tariffTable.getStatus().equals(TariffStatus.AWAITING_APPROVAL)) {
					//this.setRenderActivate(true);
					this.setRenderApprove(true);
					//this.setRenderDelete(true);
					this.setRenderDisapprove(true);
					//this.setRenderRestore(false);
					
				} if(this.tariffTable.getStatus().equals(TariffStatus.DELETED)) {
					//this.setRenderActivate(false);
					this.setRenderApprove(false);
					//this.setRenderDelete(false);
					this.setRenderDisapprove(false);
					//this.setRenderRestore(true);
					
				}if(this.tariffTable.getStatus().equals(TariffStatus.DRAFT)) {
					//this.setRenderActivate(false);
					this.setRenderApprove(false);
					//this.setRenderDelete(false);
					this.setRenderDisapprove(false);
					//this.setRenderRestore(true);
					
				}
				
			} else {
				System.out.println(">>>>>>>>>>>>> Isn't approver.");
				if(this.tariffTable.getStatus().equals(TariffStatus.ACTIVE)) {
				//this.setRenderActivate(false);
				this.setRenderDelete(false);
				//this.setRenderRestore(false);
				
			} if(this.tariffTable.getStatus().equals(TariffStatus.DISAPPROVED)) {
				//this.setRenderActivate(false);
				this.setRenderDelete(true);
				//this.setRenderRestore(false);
				
			} if(this.tariffTable.getStatus().equals(TariffStatus.INACTIVE)) {
				//this.setRenderActivate(true);
				this.setRenderDelete(false);
				//this.setRenderRestore(false);
				
			} if (this.tariffTable.getStatus().equals(TariffStatus.AWAITING_APPROVAL)) {
				//this.setRenderActivate(true);
				this.setRenderDelete(true);
				//this.setRenderRestore(false);
				
			} if(this.tariffTable.getStatus().equals(TariffStatus.DELETED)) {
				//this.setRenderActivate(false);
				this.setRenderDelete(false);
				//this.setRenderRestore(true);
				
			}if(this.tariffTable.getStatus().equals(TariffStatus.DRAFT)) {
				//this.setRenderActivate(false);
				//this.setRenderApprove(false);
				this.setRenderDelete(true);
				//this.setRenderDisapprove(false);
				//this.setRenderRestore(true);
				
			}
			
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
		return tariffTable;
	}

	public void setTariffTable(TariffTable tariffTable) {
		this.tariffTable = tariffTable;
	}

	public List<Tariff> getTariffList() {
		if((this.tariffList == null || this.tariffList.isEmpty()) && this.getTariffTableId() != null) {
			this.tariffList = super.getTariffService().getTariffsByTariffTableId(this.getTariffTableId()); 
		}
		return tariffList;
	}

	public void setTariffList(List<Tariff> tariffList) {
		this.tariffList = tariffList;
	}

	public String getTariffTableId() {
		if(tariffTableId == null){
			try {
				tariffTableId = (String)super.getRequestScope().get("tariffTableId");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(">>>>>>>>>>>>> Table Id in getTariffTableId = "+tariffTableId);
		return tariffTableId;
	}

	public void setTariffTableId(String tariffTableId) {
		this.tariffTableId = tariffTableId;
	}

	public void setMsg(String msg) {
		this.msg = msg;		
	}

	public String getMsg() {
		/*if((this.msg = (String)super.getRequestScope().get("editTariff")) != null) {
			
		} else if((this.msg = (String)super.getRequestScope().get("editTariffTable")) != null) {
			// Sync table Infor;
		} if((this.msg = (String)super.getRequestScope().get("createTariff")) != null) {
			
		}*/
		
		return msg;
	}

	public void setApprover(boolean approver) {
		this.approver = approver;
	}

	public boolean isApprover() {
		Profile profile = null;
		//RoleAccessRight roleAccessRight = null;
		try {
			profile = super.getProfileService().getProfileByUserName(super.getJaasUserName());
			if(super.getProfileService().canDo(profile.getUserName(), "APPROVECOMMISSION")) {
				this.approver = true;
			} else {
				this.approver = false;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return approver;
	}

	public void setApproverMsg(String approverMsg) {
		this.approverMsg = approverMsg;
	}

	public String getApproverMsg() {
		if(this.approverMsg == null) {
			try {
				this.approverMsg = (String)super.getRequestScope().get("approveTariff");
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return approverMsg;
	}

	public void setFromPageName(String fromPageName) {
		this.fromPageName = fromPageName;
	}

	public String getFromPageName() {
		if(this.fromPageName == null) {
			try {
				this.fromPageName = (String)super.getRequestScope().get("createTariffTable");
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return fromPageName;
	}

	public void setSelectedTariffId(String selectedTariffId) {
		this.selectedTariffId = selectedTariffId;
	}

	public String getSelectedTariffId() {
		if(this.selectedTariffId == null) {
			try {
				this.selectedTariffId = (String)super.getRequestScope().get("tariffId");
			} catch (Exception e) {
				
			}
		}
		return selectedTariffId;
	}

	public void setRenderNew(boolean renderNew) {
		this.renderNew = renderNew;
	}

	public boolean isRenderNew() {
		return renderNew;
	}

	public void setRenderApprove(boolean renderApprove) {
		this.renderApprove = renderApprove;
	}

	public boolean isRenderApprove() {
		return renderApprove;
	}

	public void setRenderDisapprove(boolean renderDisapprove) {
		this.renderDisapprove = renderDisapprove;
	}

	public boolean isRenderDisapprove() {
		return renderDisapprove;
	}

	public void setRenderActivate(boolean renderActivate) {
		this.renderActivate = renderActivate;
	}

	public boolean isRenderActivate() {
		return renderActivate;
	}

	public void setRenderRestore(boolean renderRestore) {
		this.renderRestore = renderRestore;
	}

	public boolean isRenderRestore() {
		return renderRestore;
	}

	public void setRenderDelete(boolean renderDelete) {
		this.renderDelete = renderDelete;
	}

	public boolean isRenderDelete() {
		return renderDelete;
	}

	public void setInitializeInfor(String initializeInfor) {
		this.initializeInfor = initializeInfor;
	}

	public String getInitializeInfor() {
		try {
			if(this.getApproverMsg() != null) {
				this.setApprover(true);
			}/* else {
				this.setNonApprover(true);
			}*/
			if(this.getFromPageName() == null) {
				this.setFromPageName((String)super.getRequestScope().get("createTariffTable"));
			}
			//this.checkApprover();
			this.toggleApproverButtons();
		} catch (Exception e) {
			// TODO: handle exception
		}
		this.initializeInfor = "";
		return initializeInfor;
	}
	
	private void toggleApproverButtons() {
		if(this.isApprover()) {
			if(this.getTariffTable() != null && this.getTariffTable().getId() != null) {
				if(TariffStatus.ACTIVE.equals(this.getTariffTable().getStatus())) {
					this.setRenderApprove(false);
					this.setRenderDisapprove(true);
				} else if(TariffStatus.DISAPPROVED.equals(this.getTariffTable().getStatus())) {
					this.setRenderApprove(true);
					this.setRenderDisapprove(false);
				}
			}
		}
	}

	public void setRenderEditable(boolean renderEditable) {
		this.renderEditable = renderEditable;
	}

	public boolean isRenderEditable() {
		if(this.isApprover()) {
			renderEditable = false;
		} else {
			if(TariffStatus.AWAITING_APPROVAL.equals(this.getTariffTable().getStatus()) ||
				TariffStatus.ACTIVE.equals(this.getTariffTable().getStatus()) ||
				TariffStatus.DRAFT.equals(this.getTariffTable().getStatus())) {
				renderEditable = true;
			} else {
				renderEditable = false;
			}
		}
		return renderEditable;
	}

	
}
