package zw.co.esolutions.ussd.services;

import zw.co.esolutions.ussd.util.SystemConstants;

public class USSDServiceFactory {
	
	
	public static USSDService getUSSDServiceInstance(String serviceName){
		
		if(SystemConstants.SERVICE_NAME_AUTHENTICATION.equals(serviceName)){
			return new AuthenticationService();
		}else if(SystemConstants.SERVICE_NAME_SELECT_SERVICE.equals(serviceName)){
			return new SelectService();
		}else if(SystemConstants.SERVICE_NAME_SELECT_ACCOUNT_TYPE.equals(serviceName)){
			return new SelectAccountTypeService();
		}else if(SystemConstants.SERVICE_NAME_TOPUP_ANOTHER_PHONE.equals(serviceName)){
			return new TopupService();
		}else if(SystemConstants.SERVICE_NAME_TOPUP_MY_PHONE.equals(serviceName)){
			return new SelfTopupService();
		}else if(SystemConstants.SERVICE_NAME_TRANSFER.equals(serviceName)){
			return new TransferService();
		}else if(SystemConstants.SERVICE_NAME_BALANCE_ENQUIRY.equals(serviceName)){
			return new BalanceEnquiryService();
		}else if(SystemConstants.SERVICE_NAME_MINI_STATEMENT.equals(serviceName)){
			return new MiniStatementService();
		}else if(SystemConstants.SERVICE_NAME_PINCHANGE.equals(serviceName)){
			return new PinChangeService();
		}else if(SystemConstants.SERVICE_NAME_BILL_PAYMENT.equals(serviceName)){
			return new BillPaymentService();
		}else if(SystemConstants.SERVICE_NAME_AGENT_CUSTOMER_WITHDRAWAL.equals(serviceName)){
			return new AgentCustomerHolderWithdrwalService();
		}else if(SystemConstants.SERVICE_NAME_AGENT_NON_WITH.equals(serviceName)){
			return new AgentCustomerNonHolderWithdrawalService();
		}else if(SystemConstants.SERVICE_NAME_AGENT_SEND_MONEY.equals(serviceName)){
			return new AgentTransferService();
		}else if(SystemConstants.SERVICE_NAME_DEPOSIT_CASH.equals(serviceName)){
			return new AgentCustomerDepositService();
		}else if(SystemConstants.SERVICE_NAME_AGENT_SUMMARY.equals(serviceName)){
			return new AgentSummaryService();
		} else if(SystemConstants.SERVICE_NAME_RTGS_PAYMENT.equals(serviceName)){
			return new RtgsService();
		}else if(SystemConstants.SERVICE_NAME_REGISTER_MERCHANT.equals(serviceName)){
			return new RegisterMerchantService();
		}
		return null;
	}

}
