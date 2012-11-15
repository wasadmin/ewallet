package zw.co.esolutions.mobile.banking.services;
import javax.jws.WebParam;
import javax.jws.WebService;

import zw.co.esolutions.mobile.banking.msg.BankRequest;
import zw.co.esolutions.mobile.banking.msg.BankResponse;
import zw.co.esolutions.mobile.banking.msg.MerchantRequest;
import zw.co.esolutions.mobile.banking.msg.MerchantResponse;
import zw.co.esolutions.mobile.banking.msg.Pareq;
import zw.co.esolutions.mobile.banking.msg.Pares;
import zw.co.esolutions.mobile.banking.msg.TransactionRequest;
import zw.co.esolutions.mobile.banking.msg.TransactionResponse;
import zw.co.esolutions.mobile.banking.msg.Vereq;
import zw.co.esolutions.mobile.banking.msg.Veres;

@WebService(name = "MobileBankingService")
public interface MobileBankingService {
	
	Veres verifyEnrolment(@WebParam(name = "vereq") Vereq vereq);
	Pares verifyPassword(@WebParam(name = "pareq") Pareq pareq);
	TransactionResponse submitTransaction(@WebParam(name = "transactionRequest")TransactionRequest transactionRequest);
	MerchantResponse getMerchants(@WebParam(name = "merchantRequest")MerchantRequest merchantRequest);
	BankResponse getBanks(@WebParam(name = "bankRequest")BankRequest bankRequest);

}
