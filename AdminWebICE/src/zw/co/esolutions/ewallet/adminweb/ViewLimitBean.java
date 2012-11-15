package zw.co.esolutions.ewallet.adminweb;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.limitservices.service.Limit;
import zw.co.esolutions.ewallet.limitservices.service.LimitStatus;
import zw.co.esolutions.ewallet.msg.MessageAction;
import zw.co.esolutions.ewallet.msg.MessageSync;
import zw.co.esolutions.ewallet.profileservices.service.Profile;

public class ViewLimitBean extends PageCodeBase{

	private String approvePage;
	private String createPage;
	private Limit limit;
	private String limitId;
	private String msg;
	private boolean renderApprove;
	private boolean renderDelete;
	private boolean renderRestore;
	private boolean renderActivate;
	private boolean renderDisapprove;
	private boolean approver;
	private boolean diableEdit;
	private String initializeInfor;
	private boolean renderEditable;
	
	public ViewLimitBean() {
		super();
		if(this.getLimitId() == null) {
			this.setLimitId((String)super.getRequestScope().get("limitId"));
		}
		System.out.println(">>>>>>>>>>>>>>>>>>>>>> ID = "+this.getLimitId());
		if(this.getApprovePage() == null) {
			this.setApprovePage((String)super.getRequestScope().get("approveLimit"));
			if(this.getApprovePage() != null) {
				if(this.getApprovePage().equalsIgnoreCase("approveLimit")) {
					this.setApprover(true);
					this.setDiableEdit(true);
					
				}
			} else {
				this.checkApprover();
				
			}
		} else {
			this.checkApprover();
			
		}
		
		
	}

	@SuppressWarnings("unchecked")
	public String edit() {
		super.getRequestScope().put("limitId", this.getLimitId());
		this.cleanBean();
		return "success";
	}
	
	public  String goBack() {
		System.out.println(">>>>>>>>>>>>>>>>>>>>>> ID = "+this.getLimitId());
		if(this.getApprovePage() != null) {
			if(this.getApprovePage().equalsIgnoreCase("approveLimit")) {
				super.gotoPage("/admin/approveLimit.jspx");
			}
		} else {
			super.gotoPage("/admin/viewAllLimits.jspx");
		}
		this.cleanBean();
		return "success";
	}
	
	public String deleteLimit() {
		try {
			super.getLimitService().deleteLimit(this.getLimit().getId(), super.getJaasUserName());
			this.setLimit(super.getLimitService().findLimitById(this.getLimitId()));
			this.getLimit();
			//MessageSync.populateAndSync(this.getLimit(), MessageAction.DELETE);
		} catch (Exception e) {
			super.setInformationMessage("An error has occured. Operation aborted.");
			return "failure";
		}
		super.setInformationMessage("Deletion successful.");
		return "success";
	}
	
	public String restoreLimit() {
		try {
			this.getLimit().setStatus(LimitStatus.ACTIVE);
			super.getLimitService().approveLimit(this.getLimit(), super.getJaasUserName());
			MessageSync.populateAndSync(this.getLimit(), MessageAction.UPDATE);
		} catch (Exception e) {
			super.setInformationMessage("An error has occured. Operation aborted.");
			return "failure";
		}
		super.setInformationMessage("Limit Restored Successfully.");
		return "success";
	}

	public String activateLimit() {
		try {
			this.getLimit().setStatus(LimitStatus.ACTIVE);
			super.getLimitService().activateLimit(this.getLimit(), super.getJaasUserName());
			
		} catch (Exception e) {
			super.setErrorMessage("An error has occured. Operation aborted.");
			return "failure";
		}
		super.setInformationMessage("Limit activated successfully.");
		return "success";
	}
	
	public String disapproveLimit() {
		try {
			if(LimitStatus.DISAPPROVED.equals(this.getLimit().getStatus())) {
				super.setErrorMessage("Limit already disapproved");
				return "failure";
			}
			this.getLimit().setStatus(LimitStatus.DISAPPROVED);
			this.setLimit(super.getLimitService().disapproveLimit(this.getLimit(), super.getJaasUserName()));
			
		} catch (Exception e) {
			super.setErrorMessage("An error has occured. Operation aborted.");
			return "failure";
		}
		super.setInformationMessage("Limit disapproved.");
		return "success";
	}
	
	public String approveLimit() {
		try {
			if(LimitStatus.ACTIVE.equals(this.getLimit().getStatus())) {
				super.setErrorMessage("Limit already approved");
				return "failure";
			}
			this.getLimit().setStatus(LimitStatus.ACTIVE);
			this.setLimit(super.getLimitService().approveLimit(this.getLimit(), super.getJaasUserName()));
			//MessageSync.populateAndSync(this.getLimit(), MessageAction.CREATE);
		} catch (Exception e) {
			e.printStackTrace();
			super.setErrorMessage("An error has occured. Operation aborted.");
			return "failure";
		}
		super.setInformationMessage("Limit Approved.");
		return "success";
	}
	
	public Limit getLimit() {
		if((this.limit== null || this.limit.getId() == null) && this.getLimitId() != null) {
			try {
				this.limit = super.getLimitService().findLimitById(this.getLimitId());
			} catch (Exception e) {
				
			}
		}
		
		if((this.limit != null && this.limit.getId() != null) && this.isApprover()) {
			if(this.limit.getStatus().equals(LimitStatus.AWAITING_APPROVAL)) {
				this.setRenderApprove(true);
				this.setRenderDisapprove(true);
				this.setRenderDelete(false);
				//this.setRenderActivate(false);
				//this.setRenderRestore(false);
			} if(this.limit.getStatus().equals(LimitStatus.ACTIVE)) {
				//One Has to Delete Only
				this.setRenderApprove(false);
				if(this.getApprovePage() != null) {
					if(this.getApprovePage().equalsIgnoreCase("approveLimit")) {
						this.setRenderDisapprove(true);
					}
				} else {
					this.setRenderDisapprove(false);
				}
				this.setRenderDelete(false);
				//this.setRenderActivate(false);
				//this.setRenderRestore(false);
			} if(this.limit.getStatus().equals(LimitStatus.DELETED)) {
				//One Has to Restore Only
				this.setRenderApprove(false);
				this.setRenderDisapprove(false);
				this.setRenderDelete(false);
				//this.setRenderActivate(false);
				//this.setRenderRestore(true);
				
			}if(this.limit.getStatus().equals(LimitStatus.INACTIVE)) {
				this.setRenderApprove(false);
				this.setRenderDisapprove(false);
				this.setRenderDelete(false);
				//this.setRenderActivate(true);
				//this.setRenderRestore(false);
			} if(this.limit.getStatus().equals(LimitStatus.DISAPPROVED)) {
				this.setRenderApprove(true);
				if(this.getApprovePage() != null) {
					if(this.getApprovePage().equalsIgnoreCase("approveLimit")) {
						this.setRenderDisapprove(true);
					}
				} else {
					this.setRenderDisapprove(false);
				}
				this.setRenderDelete(false);
				//this.setRenderActivate(false);
				//this.setRenderRestore(false);
			}
		} else if((this.limit != null && this.limit.getId() != null) && !this.isApprover()) {
			if(this.limit.getStatus().equals(LimitStatus.AWAITING_APPROVAL)) {
				this.setRenderApprove(false);
				this.setRenderDisapprove(false);
				this.setRenderDelete(true);
				//this.setRenderActivate(false);
				//this.setRenderRestore(false);
			} if(this.limit.getStatus().equals(LimitStatus.ACTIVE)) {
				//One Has to Delete Only
				this.setRenderApprove(false);
				this.setRenderDisapprove(false);
				this.setRenderDelete(false);
				//this.setRenderActivate(false);
				//this.setRenderRestore(false);
			} if(this.limit.getStatus().equals(LimitStatus.DELETED)) {
				//One Has to Restore Only
				this.setRenderApprove(false);
				this.setRenderDisapprove(false);
				this.setRenderDelete(false);
				//this.setRenderActivate(false);
				//this.setRenderRestore(true);
				
			}if(this.limit.getStatus().equals(LimitStatus.INACTIVE)) {
				this.setRenderApprove(false);
				this.setRenderDisapprove(false);
				this.setRenderDelete(false);
				//this.setRenderActivate(true);
				//this.setRenderRestore(false);
			} if(this.limit.getStatus().equals(LimitStatus.DISAPPROVED)) {
				this.setRenderApprove(false);
				this.setRenderDisapprove(false);
				this.setRenderDelete(true);
				//this.setRenderActivate(false);
				//this.setRenderRestore(false);
			}
		}
		return limit;
	}

	public void setLimit(Limit limit) {
		this.limit = limit;
	}

	public String getLimitId() {
		if(this.limitId == null) {
			try {
				this.limitId = (String)super.getRequestScope().get("limitId");
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return limitId;
	}

	public void setLimitId(String limitId) {
		this.limitId = limitId;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		if(this.msg == null) {
			if((this.msg = (String)super.getRequestScope().get("editLimit")) != null) {
				//Previously for synching, now not working
			}
		}
		return msg;
	}
	
	private void checkApprover() {
		Profile profile = null;
		try {
			profile = super.getProfileService().getProfileByUserName(super.getJaasUserName());
			if(super.getProfileService().canDo(profile.getUserName(), "APPROVELIMIT")) {
				this.setApprover(true);
				this.setDiableEdit(true);
			} else {
				this.setApprover(false);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public String logs(){
		super.gotoPage("/admin/viewLogs.jspx");
		return "logs";
	}
	
	public boolean isRenderApprove() {
		return renderApprove;
	}

	public void setRenderApprove(boolean renderApprove) {
		this.renderApprove = renderApprove;
	}

	public void setApprovePage(String approvePage) {
		this.approvePage = approvePage;
	}

	public String getApprovePage() {
		if(this.approvePage == null) {
			this.approvePage = (String)super.getRequestScope().get("approveLimit");
		}
		return approvePage;
	}

	public void setRenderDelete(boolean renderDelete) {
		this.renderDelete = renderDelete;
	}

	public boolean isRenderDelete() {
		return renderDelete;
	}

	public void setRenderRestore(boolean renderRestore) {
		this.renderRestore = renderRestore;
	}

	public boolean isRenderRestore() {
		return renderRestore;
	}

	public void setRenderActivate(boolean renderActivate) {
		this.renderActivate = renderActivate;
	}

	public boolean isRenderActivate() {
		return renderActivate;
	}

	public void setRenderDisapprove(boolean renderDisapprove) {
		this.renderDisapprove = renderDisapprove;
	}

	public boolean isRenderDisapprove() {
		return renderDisapprove;
	}

	public void setApprover(boolean approver) {
		this.approver = approver;
	}

	public boolean isApprover() {
		Profile profile = null;
		//RoleAccessRight roleAccessRight = null;
		try {
			profile = super.getProfileService().getProfileByUserName(super.getJaasUserName());
			if(super.getProfileService().canDo(profile.getUserName(), "approveLimit".toUpperCase())) {
				this.approver = true;
			} else {
				this.approver = false;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return approver;
	}

	public void setDiableEdit(boolean diableEdit) {
		this.diableEdit = diableEdit;
	}

	public boolean isDiableEdit() {
		return diableEdit;
	}

	public void setCreatePage(String createPage) {
		this.createPage = createPage;
	}

	public String getCreatePage() {
		return createPage;
	}
	
	private void cleanBean() {
		approvePage = null;
		createPage = null;
		limit = null;
		limitId = null;
		msg = null;
		renderApprove = false;
		renderDelete = false;
		renderRestore = false;
		renderActivate = false;
		renderDisapprove = false;
		approver = false;
		diableEdit = false;
	}

	public void setInitializeInfor(String initializeInfor) {
		this.initializeInfor = initializeInfor;
	}

	public String getInitializeInfor() {
		try {
			if(this.getApprovePage() == null) {
				this.setApprovePage((String)super.getRequestScope().get("approveLimit"));
				if(this.getApprovePage() != null) {
					if(this.getApprovePage().equalsIgnoreCase("approveLimit")) {
						this.setApprover(true);
						this.setDiableEdit(true);
						
					}
				} else {
					this.checkApprover();
					
				}
			} else {
				if(this.getApprovePage() != null) {
					if(this.getApprovePage().equalsIgnoreCase("approveLimit")) {
						this.setApprover(true);
						this.setDiableEdit(true);
						
					}
				}
				this.checkApprover();
				
			}
			this.toggleApproverButtons();
		} catch (Exception e) {
			// TODO: handle exception
		}
		this.initializeInfor = "";
		return initializeInfor;
	}
	
	private void toggleApproverButtons() {
		if(this.isApprover()) {
			if(this.getLimit() != null && this.getLimit().getId() != null) {
				if(LimitStatus.ACTIVE.equals(this.getLimit().getStatus())) {
					this.setRenderApprove(false);
					this.setRenderDisapprove(true);
				} else if(LimitStatus.DISAPPROVED.equals(this.getLimit().getStatus())) {
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
			if(LimitStatus.AWAITING_APPROVAL.equals(this.getLimit().getStatus()) ||
				LimitStatus.ACTIVE.equals(this.getLimit().getStatus())) {
				renderEditable = true;
			} else {
				renderEditable = false;
			}
		}
		return renderEditable;
	}

}
