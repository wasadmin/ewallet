package zw.co.esolutions.mcommerce.centralswitch.processes;
import java.util.List;

import javax.ejb.Local;

import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.merchantservices.service.Merchant;
import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.ewallet.msg.ResponseInfo;
import zw.co.esolutions.ewallet.msg.SynchronisationResponse;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessage;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessage.MessageCommand.Enum;
import zw.co.esolutions.topup.ws.impl.WsResponse;

@Local
public interface ProcessUtil {

	SynchronisationResponse processRegMessage(BankRegistrationMessage bankRegistrationMessage, Enum action) throws Exception;

	List<Bank> getAllBanks();

	List<Merchant> getAllMerchants();

	WsResponse sendTopupRequest(ResponseInfo responseInfo) throws Exception;
	
	WsResponse submitBillPayRequest(ResponseInfo responseInfo) throws Exception;

	WsResponse sendPhoneValidation(RequestInfo requestInfo) throws Exception;

}
