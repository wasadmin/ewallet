package zw.co.esolutions.bankif.session;
import java.util.List;

import javax.ejb.Local;

import zw.co.esolutions.bankif.model.BankResponseCode;
import zw.co.esolutions.bankif.util.BankResponseHandlerResponse;
import zw.co.esolutions.bankif.util.Response;
import zw.co.esolutions.ewallet.msg.BankResponse;

@Local
public interface ProcessBankResponse {

	BankResponseHandlerResponse handleBankResponse(BankResponse bankResponse);

	BankResponse processTransactionAdvice(BankResponse bankResponse);

	BankResponse processReversalResponse(BankResponse bankResponse);

	BankResponseCode findBankResponseCode(String respCode);

	List<Response> processTransactionCharge(String bankReference) throws Exception;
	
	BankResponseHandlerResponse processTransactionChargeResponse(BankResponse bankResponse);
	
	BankResponseHandlerResponse processAgentCommissionResponse(BankResponse bankResponse); 
}
