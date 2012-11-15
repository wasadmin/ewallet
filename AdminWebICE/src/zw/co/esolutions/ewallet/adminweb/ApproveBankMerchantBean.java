package zw.co.esolutions.ewallet.adminweb;

import java.util.List;

import javax.faces.event.ActionEvent;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.merchantservices.service.BankMerchant;
import zw.co.esolutions.ewallet.merchantservices.service.BankMerchantStatus;
import zw.co.esolutions.ewallet.merchantservices.service.Merchant;
import zw.co.esolutions.ewallet.merchantservices.service.MerchantServiceSOAPProxy;
import zw.co.esolutions.ewallet.merchantservices.service.MerchantStatus;
import zw.co.esolutions.ewallet.msg.MessageAction;
import zw.co.esolutions.ewallet.msg.MessageSync;

public class ApproveBankMerchantBean extends PageCodeBase{
	
	private MerchantServiceSOAPProxy merchantService = new MerchantServiceSOAPProxy();
	private List<BankMerchant> bankMerchantList;
	private String bankMerchantId;
	
	public List<BankMerchant> getBankMerchantList() {
		if(bankMerchantList==null){
			try{
				bankMerchantList = merchantService.getBankMerchantByStatus(BankMerchantStatus.AWAITING_APPROVAL);
			}catch(Exception e){}
		}
		return bankMerchantList;
	}
	public void setBankMerchantList(List<BankMerchant> bankMerchantList) {
		this.bankMerchantList = bankMerchantList;
	}
	
	public void doApproveAction(ActionEvent event){
		bankMerchantId = (String)event.getComponent().getAttributes().get("bankMerchantId");
	}
	
//	public String approve(){
//		BankMerchant bankMerchant = null;
//		try{
//			bankMerchant = merchantService.findBankMerchantById(bankMerchantId);
//			bankMerchant.setStatus(BankMerchantStatus.ACTIVE);
//			bankMerchant.setEnabled(true);
//			merchantService.editBankMerchant(bankMerchant, super.getJaasUserName());
//			
//			Merchant merchant = merchantService.findMerchantById(bankMerchant.getId());
//			merchant.setStatus(MerchantStatus.ACTIVE);
//			merchantService.editMerchant(merchant, super.getJaasUserName());
//			
//	//		MessageSync.populateAndSync(bankMerchant, MessageAction.UPDATE);
//	//		MessageSync.populateAndSync(merchant, MessageAction.UPDATE);
//			
//			super.setInformationMessage(bankMerchant.getMerchant().getName()+" approved successfully.");
//		}catch(Exception e){
//			e.printStackTrace();
//			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
//		}
//		return null;
//	}
//	
	public String viewBankMerchant() {
		gotoPage("/admin/viewBankMerchant.jspx");
		return "viewBankMerchant";
	}
}
