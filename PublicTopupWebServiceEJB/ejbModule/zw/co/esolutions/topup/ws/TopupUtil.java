package zw.co.esolutions.topup.ws;

import javax.ejb.Local;

import zw.co.esolutions.topup.ws.model.TransactionInfo;
import zw.co.esolutions.topup.ws.util.WSRequest;

@Local
public interface TopupUtil {
	TransactionInfo findTransactionInfoByRequest(WSRequest request);
	TransactionInfo createTransactionInfo(WSRequest request);
	TransactionInfo updateTransactionInfo(TransactionInfo info);
}
