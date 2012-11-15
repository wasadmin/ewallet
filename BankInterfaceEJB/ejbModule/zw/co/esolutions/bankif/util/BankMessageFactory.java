package zw.co.esolutions.bankif.util;

import zw.co.esolutions.bankif.model.BankRequestMessage;
import zw.co.esolutions.bankif.model.CommissionMessage;
import zw.co.esolutions.ewallet.msg.BankResponse;

public interface BankMessageFactory {
	String createBankMessage(zw.co.esolutions.bankif.model.BankRequestMessage bankRequestMessage);
	BankResponse parseBankResponse(String input);
	String createReversalMessage(BankRequestMessage existingRequest);
	String createChargesMessage(CommissionMessage info);
	String createChargesReversal(CommissionMessage info);
	String createMainTransactionReversalMessage(BankRequestMessage bankRequestMessage);
}
