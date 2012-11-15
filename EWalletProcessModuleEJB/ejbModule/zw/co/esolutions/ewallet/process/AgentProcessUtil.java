package zw.co.esolutions.ewallet.process;
import javax.ejb.Local;
import zw.co.esolutions.ewallet.msg.RequestInfo;
import zw.co.esolutions.ewallet.process.model.ProcessTransaction;
import zw.co.esolutions.ewallet.sms.ResponseCode;

@Local
public interface AgentProcessUtil {

	ResponseCode postCustomerDepositByAgentTxns(ProcessTransaction txn)
			throws Exception;

	ResponseCode postCustomerWidthdrawalByAgentTxns(ProcessTransaction txn)
			throws Exception;

	ResponseCode postTrasnferEmoneyToSubAgentTxns(ProcessTransaction txn)
			throws Exception;

	ResponseCode postCustomerNonHolderWidthdrawalTxns(ProcessTransaction txn)
			throws Exception;

	ResponseCode postAgentWidthdrawTxns(ProcessTransaction txn)
			throws Exception;

	ResponseCode postAgentCommissionTxns(ProcessTransaction txn)
			throws Exception;

	ResponseCode postAgentRegistersCustomerTxns(ProcessTransaction txn)
			throws Exception;

	ResponseCode postAgentRegistersSubAgentTxns(ProcessTransaction txn)
			throws Exception;

	ProcessTransaction populateProcessTransaction(RequestInfo requestInfo);

	ProcessTransaction initializeTransferDetails(RequestInfo info)
			throws Exception;

}
