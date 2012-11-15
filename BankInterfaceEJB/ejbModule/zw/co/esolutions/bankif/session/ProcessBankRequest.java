package zw.co.esolutions.bankif.session;
import javax.ejb.Local;

import zw.co.esolutions.bankif.model.TransactionInfo;
import zw.co.esolutions.bankif.util.Response;
import zw.co.esolutions.ewallet.msg.BankRequest;
import zw.co.esolutions.ewallet.msg.BankResponse;

@Local
public interface ProcessBankRequest {
	Response processBankRequest(BankRequest bankRequest) throws Exception;

	BankResponse processEWalletToEWalletBankRequest(BankRequest bankRequest) throws Exception;
	
	TransactionInfo findTransactionInfoByProcessingCode(String processingCode) throws Exception;
}
