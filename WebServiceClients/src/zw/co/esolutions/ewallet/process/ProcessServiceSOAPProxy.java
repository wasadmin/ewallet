package zw.co.esolutions.ewallet.process;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;

public class ProcessServiceSOAPProxy{

    protected Descriptor _descriptor;

    public class Descriptor {
        private zw.co.esolutions.ewallet.process.ProcessService_Service _service = null;
        private zw.co.esolutions.ewallet.process.ProcessService _proxy = null;
        private Dispatch<Source> _dispatch = null;

        public Descriptor() {
            init();
        }

        public Descriptor(URL wsdlLocation, QName serviceName) {
            _service = new zw.co.esolutions.ewallet.process.ProcessService_Service(wsdlLocation, serviceName);
            initCommon();
        }

        public void init() {
            _service = null;
            _proxy = null;
            _dispatch = null;
            _service = new zw.co.esolutions.ewallet.process.ProcessService_Service();
            initCommon();
        }

        private void initCommon() {
            _proxy = _service.getProcessServiceSOAP();
        }

        public zw.co.esolutions.ewallet.process.ProcessService getProxy() {
            return _proxy;
        }

        public Dispatch<Source> getDispatch() {
            if (_dispatch == null ) {
                QName portQName = new QName("http://process.ewallet.esolutions.co.zw/", "ProcessServiceSOAP");
                _dispatch = _service.createDispatch(portQName, Source.class, Service.Mode.MESSAGE);

                String proxyEndpointUrl = getEndpoint();
                BindingProvider bp = (BindingProvider) _dispatch;
                String dispatchEndpointUrl = (String) bp.getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
                if (!dispatchEndpointUrl.equals(proxyEndpointUrl))
                    bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, proxyEndpointUrl);
            }
            return _dispatch;
        }

        public String getEndpoint() {
            BindingProvider bp = (BindingProvider) _proxy;
            return (String) bp.getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
        }

        public void setEndpoint(String endpointUrl) {
            BindingProvider bp = (BindingProvider) _proxy;
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);

            if (_dispatch != null ) {
                bp = (BindingProvider) _dispatch;
                bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);
            }
        }

        public void setMTOMEnabled(boolean enable) {
            SOAPBinding binding = (SOAPBinding) ((BindingProvider) _proxy).getBinding();
            binding.setMTOMEnabled(enable);
        }
    }

    public ProcessServiceSOAPProxy() {
        _descriptor = new Descriptor();
        _descriptor.setMTOMEnabled(false);
    }

    public ProcessServiceSOAPProxy(URL wsdlLocation, QName serviceName) {
        _descriptor = new Descriptor(wsdlLocation, serviceName);
        _descriptor.setMTOMEnabled(false);
    }

    public Descriptor _getDescriptor() {
        return _descriptor;
    }

    public ProcessResponse depositCash(DepositInfo depositInfo) {
        return _getDescriptor().getProxy().depositCash(depositInfo);
    }

    public ProcessTransaction getProcessTransaction(NonHolderWithdrawalInfo info) throws Exception_Exception {
        return _getDescriptor().getProxy().getProcessTransaction(info);
    }

    public ProcessResponse confirmNonHolderWithdrawal(String txnRefId, String profileId) {
        return _getDescriptor().getProxy().confirmNonHolderWithdrawal(txnRefId,profileId);
    }

    public List<ProcessTransaction> getProcessTransactionsWithinDateRange(XMLGregorianCalendar fromDate, XMLGregorianCalendar toDate, TransactionStatus status) {
        return _getDescriptor().getProxy().getProcessTransactionsWithinDateRange(fromDate,toDate,status);
    }

    public List<ProcessTransaction> getProcessTransactionsWithinDateRangeByMsgType(XMLGregorianCalendar fromDate, XMLGregorianCalendar toDate, TransactionType msgType, TransactionStatus status) {
        return _getDescriptor().getProxy().getProcessTransactionsWithinDateRangeByMsgType(fromDate,toDate,msgType,status);
    }

    public List<ProcessTransaction> getProcessTransactionsWithinDateRangeByBankId(XMLGregorianCalendar fromDate, XMLGregorianCalendar toDate, String bankId, TransactionStatus status) {
        return _getDescriptor().getProxy().getProcessTransactionsWithinDateRangeByBankId(fromDate,toDate,bankId,status);
    }

    public List<ProcessTransaction> getProcessTransactionsWithinDateRangeByMsgTypeByBankId(XMLGregorianCalendar fromDate, XMLGregorianCalendar toDate, TransactionType msgType, String bankId, TransactionStatus status) {
        return _getDescriptor().getProxy().getProcessTransactionsWithinDateRangeByMsgTypeByBankId(fromDate,toDate,msgType,bankId,status);
    }

    public List<ProcessTransaction> getProcessTransactionsWithinDateRangeByBankIdAndBranchId(XMLGregorianCalendar fromDate, XMLGregorianCalendar toDate, String bankId, String branchId, TransactionStatus status) {
        return _getDescriptor().getProxy().getProcessTransactionsWithinDateRangeByBankIdAndBranchId(fromDate,toDate,bankId,branchId,status);
    }

    public List<ProcessTransaction> getProcessTransactionsWithinDateRangeByMsgTypeByBankIdAndBranchId(XMLGregorianCalendar fromDate, XMLGregorianCalendar toDate, TransactionType msgType, String bankId, String branchId, TransactionStatus status) {
        return _getDescriptor().getProxy().getProcessTransactionsWithinDateRangeByMsgTypeByBankIdAndBranchId(fromDate,toDate,msgType,bankId,branchId,status);
    }

    public DayEnd runTellerDayEnd(DayEnd dayEnd, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().runTellerDayEnd(dayEnd,userName);
    }

    public List<ProcessTransaction> getTellerDayEnd(String bankName, String profileId, XMLGregorianCalendar txnDate, TransactionType msgType) {
        return _getDescriptor().getProxy().getTellerDayEnd(bankName,profileId,txnDate,msgType);
    }

    public long getTellerDayEndTotal(String bankName, String profileId, XMLGregorianCalendar txnDate, TransactionType msgType) {
        return _getDescriptor().getProxy().getTellerDayEndTotal(bankName,profileId,txnDate,msgType);
    }

    public String validateEwalletHolderWithdrawal(String sourceMobileId, long amount, String bankId, TransactionLocationType locationType) throws Exception_Exception {
        return _getDescriptor().getProxy().validateEwalletHolderWithdrawal(sourceMobileId,amount,bankId,locationType);
    }

    public ProcessResponse processEwalletWithdrawal(RequestInfo requestInfo) {
        return _getDescriptor().getProxy().processEwalletWithdrawal(requestInfo);
    }

    public ProcessTransaction getProcessTransactionByMessageId(String messageId) throws Exception_Exception {
        return _getDescriptor().getProxy().getProcessTransactionByMessageId(messageId);
    }

    public List<ProcessTransaction> getProcessTransactionsByTellerID(String tellerId) {
        return _getDescriptor().getProxy().getProcessTransactionsByTellerID(tellerId);
    }

    public List<ProcessTransaction> getProcessTransactionsByTellerIdAndDateRange(XMLGregorianCalendar fromDate, XMLGregorianCalendar toDate, String tellerId) {
        return _getDescriptor().getProxy().getProcessTransactionsByTellerIdAndDateRange(fromDate,toDate,tellerId);
    }

    public List<ProcessTransaction> getProcessTransactionsByTransactionStatus(TransactionStatus status) {
        return _getDescriptor().getProxy().getProcessTransactionsByTransactionStatus(status);
    }

    public List<ProcessTransaction> getProcessTransactionByTellerIdAndDateRangeAndStatus(XMLGregorianCalendar fromDate, XMLGregorianCalendar toDate, String tellerId, TransactionStatus status) {
        return _getDescriptor().getProxy().getProcessTransactionByTellerIdAndDateRangeAndStatus(fromDate,toDate,tellerId,status);
    }

    public List<ProcessTransaction> getPocessTransactionsByDateRangeAndStatus(XMLGregorianCalendar fromDate, XMLGregorianCalendar toDate, TransactionStatus status) {
        return _getDescriptor().getProxy().getPocessTransactionsByDateRangeAndStatus(fromDate,toDate,status);
    }

    public List<ProcessTransaction> getProcessTransactionsByBranch(String branch) {
        return _getDescriptor().getProxy().getProcessTransactionsByBranch(branch);
    }

    public List<ProcessTransaction> getProcessTransactionsByBranchAndDateRangeAndStatus(XMLGregorianCalendar fromDate, XMLGregorianCalendar toDate, String branch, TransactionStatus status) {
        return _getDescriptor().getProxy().getProcessTransactionsByBranchAndDateRangeAndStatus(fromDate,toDate,branch,status);
    }

    public List<ProcessTransaction> getProcessTransactionsByTellerIdAndDayEndSummary(String tellerId, String dayEndSummaryId) {
        return _getDescriptor().getProxy().getProcessTransactionsByTellerIdAndDayEndSummary(tellerId,dayEndSummaryId);
    }

    public List<DayEnd> getDayEndByTellerIdAndDateCreatedAndStatus(String tellerId, XMLGregorianCalendar dateCreated, TransactionStatus status) {
        return _getDescriptor().getProxy().getDayEndByTellerIdAndDateCreatedAndStatus(tellerId,dateCreated,status);
    }

    public DayEnd createDayEnd(DayEnd dayEnd, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().createDayEnd(dayEnd,userName);
    }

    public DayEnd editDayEnd(DayEnd dayEnd, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().editDayEnd(dayEnd,userName);
    }

    public DayEndSummary createDayEndSummary(DayEndSummary summary, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().createDayEndSummary(summary,userName);
    }

    public DayEnd findDayEndById(String dayEndId, String userName) {
        return _getDescriptor().getProxy().findDayEndById(dayEndId,userName);
    }

    public DayEndSummary editDayEndSummary(DayEndSummary summary, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().editDayEndSummary(summary,userName);
    }

    public DayEndSummary findDayEndSummary(String id, String userName) {
        return _getDescriptor().getProxy().findDayEndSummary(id,userName);
    }

    public DayEnd approveDayEnd(DayEnd dayEnd, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().approveDayEnd(dayEnd,userName);
    }

    public DayEnd disapproveDayEnd(DayEnd dayEnd, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().disapproveDayEnd(dayEnd,userName);
    }

    public List<DayEnd> getDayEndByDayEndStatus(DayEndStatus dayEndStatus, String userName) {
        return _getDescriptor().getProxy().getDayEndByDayEndStatus(dayEndStatus,userName);
    }

    public List<DayEnd> getDayEndsByDayEndStatusAndDateRangeAndBranch(XMLGregorianCalendar fromDate, XMLGregorianCalendar toDate, DayEndStatus dayEndStatus, String branchId) throws Exception_Exception {
        return _getDescriptor().getProxy().getDayEndsByDayEndStatusAndDateRangeAndBranch(fromDate,toDate,dayEndStatus,branchId);
    }

    public List<DayEnd> getDayEndsByDayEndStatusAndDateRangeAndTeller(XMLGregorianCalendar fromDate, XMLGregorianCalendar toDate, DayEndStatus dayEndStatus, String tellerid) throws Exception_Exception {
        return _getDescriptor().getProxy().getDayEndsByDayEndStatusAndDateRangeAndTeller(fromDate,toDate,dayEndStatus,tellerid);
    }

    public List<DayEndSummary> getDayEndSummaryByDayEndId(String dayEndId, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().getDayEndSummaryByDayEndId(dayEndId,userName);
    }

    public boolean isTodaysDayEndRun(String tellerId, XMLGregorianCalendar dayEndDate) {
        return _getDescriptor().getProxy().isTodaysDayEndRun(tellerId,dayEndDate);
    }

    public DayEndResponse isPreviousDayEndRun(String profileId, XMLGregorianCalendar dayEndDate) {
        return _getDescriptor().getProxy().isPreviousDayEndRun(profileId,dayEndDate);
    }

    public List<DayEnd> getDayEndByDayEndStatusAndBranch(DayEndStatus arg0, String arg1) {
        return _getDescriptor().getProxy().getDayEndByDayEndStatusAndBranch(arg0,arg1);
    }

    public DayEndResponse checkIfThereAreTrxnTomark(String userName, XMLGregorianCalendar dayEndDate) throws Exception_Exception {
        return _getDescriptor().getProxy().checkIfThereAreTrxnTomark(userName,dayEndDate);
    }

    public List<DayEndSummary> getDayEndSummariesByDayEnd(String dayEndId) throws Exception_Exception {
        return _getDescriptor().getProxy().getDayEndSummariesByDayEnd(dayEndId);
    }

    public ProcessTransaction processEquationPosting(DayEnd dayEnd, ProcessTransaction processTxn, String userName) {
        return _getDescriptor().getProxy().processEquationPosting(dayEnd,processTxn,userName);
    }

    public DayEnd processDayEndBookEntries(DayEnd dayEnd, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().processDayEndBookEntries(dayEnd,userName);
    }

    public ProcessTransaction createProcessTransaction(ProcessTransaction processTxn, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().createProcessTransaction(processTxn,userName);
    }

    public ProcessTransaction updateProcessTxn(ProcessTransaction processTxn, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().updateProcessTxn(processTxn,userName);
    }

    public List<ProcessTransaction> getProcessTransactionsByDayEndId(DayEnd arg0) {
        return _getDescriptor().getProxy().getProcessTransactionsByDayEndId(arg0);
    }

    public void deleteProcessTransactionsByDayEnd(DayEnd dayEnd, String arg1) throws Exception_Exception {
        _getDescriptor().getProxy().deleteProcessTransactionsByDayEnd(dayEnd,arg1);
    }

    public List<ProcessTransaction> getProcessTransactionsByApplicableParameters(XMLGregorianCalendar fromDate, XMLGregorianCalendar toDate, TransactionStatus status, TransactionType txnType, TxnFamily txnFamily, String tellerId, String bankId, String branchId) {
        return _getDescriptor().getProxy().getProcessTransactionsByApplicableParameters(fromDate,toDate,status,txnType,txnFamily,tellerId,bankId,branchId);
    }

    public long getTotalAmountByApplicableParameters(XMLGregorianCalendar fromDate, XMLGregorianCalendar toDate, TransactionStatus status, TransactionType txnType, TxnFamily txnFamily, String tellerId, String bankId, String branchId) {
        return _getDescriptor().getProxy().getTotalAmountByApplicableParameters(fromDate,toDate,status,txnType,txnFamily,tellerId,bankId,branchId);
    }

    public String validateDeposit(String sourceMobileId, long amount, String bankId, TransactionLocationType locationType) throws Exception_Exception {
        return _getDescriptor().getProxy().validateDeposit(sourceMobileId,amount,bankId,locationType);
    }

    public long getDailyAmountByCustomerIdAndTxnType(String customerId, TransactionType txnType) {
        return _getDescriptor().getProxy().getDailyAmountByCustomerIdAndTxnType(customerId,txnType);
    }

    public ProcessTransaction createStartOfDayTransaction(ProcessTransaction processTransaction, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().createStartOfDayTransaction(processTransaction,username);
    }

    public long getTellerNetCashPosition(String profileId, XMLGregorianCalendar date) throws Exception_Exception {
        return _getDescriptor().getProxy().getTellerNetCashPosition(profileId,date);
    }

    public ProcessTransaction findProcessTransactionById(String transactionId) throws Exception_Exception {
        return _getDescriptor().getProxy().findProcessTransactionById(transactionId);
    }

    public List<ProcessTransaction> getStartOfDayTransactionByTransactionTypeAndBranchAndStatus(TransactionType transactionType, String branchId, TransactionStatus txnStatus) throws Exception_Exception {
        return _getDescriptor().getProxy().getStartOfDayTransactionByTransactionTypeAndBranchAndStatus(transactionType,branchId,txnStatus);
    }

    public ProcessTransaction approveTellerDailyFloat(String transactionId, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().approveTellerDailyFloat(transactionId,username);
    }

    public ProcessTransaction disapproveTellerDailyFloat(String transactionId, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().disapproveTellerDailyFloat(transactionId,username);
    }

    public List<ProcessTransaction> getStartOfDayTransactionByTransactionTypeAndTellerAndDateRange(TransactionType transactionType, String tellerId, XMLGregorianCalendar startDate, XMLGregorianCalendar endDate, TransactionStatus txnStatus) throws Exception_Exception {
        return _getDescriptor().getProxy().getStartOfDayTransactionByTransactionTypeAndTellerAndDateRange(transactionType,tellerId,startDate,endDate,txnStatus);
    }

    public List<ProcessTransaction> getStartDayTxnsByTellerAndDateRangeAndStatus(String tellerId, XMLGregorianCalendar fromDate, XMLGregorianCalendar toDate) throws Exception_Exception {
        return _getDescriptor().getProxy().getStartDayTxnsByTellerAndDateRangeAndStatus(tellerId,fromDate,toDate);
    }

    public String canTellerMakeTransact(String profileId, XMLGregorianCalendar toDay, TransactionType trxType) {
        return _getDescriptor().getProxy().canTellerMakeTransact(profileId,toDay,trxType);
    }

    public List<ProcessTransaction> getStartOfDayTxnByProfileIdAndDayEndSummaryAndStatus(String arg0, TransactionStatus arg1) {
        return _getDescriptor().getProxy().getStartOfDayTxnByProfileIdAndDayEndSummaryAndStatus(arg0,arg1);
    }

    public boolean checkIfAnyFloatsPendingApproval(String profileId) {
        return _getDescriptor().getProxy().checkIfAnyFloatsPendingApproval(profileId);
    }

    public boolean checkTellerDayEndsPendingApproval(String profileId) {
        return _getDescriptor().getProxy().checkTellerDayEndsPendingApproval(profileId);
    }

    public List<ProcessTransaction> getProcessTransactionsByAllAttributes(UniversalProcessSearch universalProcessPojo) {
        return _getDescriptor().getProxy().getProcessTransactionsByAllAttributes(universalProcessPojo);
    }

    public String reverseTransaction(ManualPojo manualPojo) throws Exception_Exception {
        return _getDescriptor().getProxy().reverseTransaction(manualPojo);
    }

    public String completeTransaction(ManualPojo manualPojo) throws Exception_Exception {
        return _getDescriptor().getProxy().completeTransaction(manualPojo);
    }

    public ManualReturn manualResolve(ManualPojo manualPojo) throws Exception_Exception {
        return _getDescriptor().getProxy().manualResolve(manualPojo);
    }

    public String confirmManualResolve(ManualPojo manualPojo) throws Exception_Exception {
        return _getDescriptor().getProxy().confirmManualResolve(manualPojo);
    }

    public List<TransactionCharge> getTransactionChargeByProcessTransactionId(String processTxnId) {
        return _getDescriptor().getProxy().getTransactionChargeByProcessTransactionId(processTxnId);
    }

    public String scheduleTimer(String dayOfMonth, String hour, String minutes, boolean isForThisMonth, XMLGregorianCalendar runDate) {
        return _getDescriptor().getProxy().scheduleTimer(dayOfMonth,hour,minutes,isForThisMonth,runDate);
    }

    public DayEndApprovalResponse processDayEndApproval(DayEnd dayEnd, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().processDayEndApproval(dayEnd,userName);
    }

    public ProcessTransaction approveMarkedTransaction(String arg0, TransactionStatus arg1, String arg2, String arg3) throws Exception_Exception {
        return _getDescriptor().getProxy().approveMarkedTransaction(arg0,arg1,arg2,arg3);
    }

    public DayEnd updateDayEnd(DayEnd dayEnd, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().updateDayEnd(dayEnd,userName);
    }

    public String scheduleCollectionTimer(XMLGregorianCalendar runDate) {
        return _getDescriptor().getProxy().scheduleCollectionTimer(runDate);
    }

    public String cancellCollectionTimer() {
        return _getDescriptor().getProxy().cancellCollectionTimer();
    }

    public String saveCollectionConfigurationData(String email, String timeToRun, String expPeriod) {
        return _getDescriptor().getProxy().saveCollectionConfigurationData(email,timeToRun,expPeriod);
    }

    public String saveTxnReversalConfigData(String runningTime, String expPeriod) {
        return _getDescriptor().getProxy().saveTxnReversalConfigData(runningTime,expPeriod);
    }

    public String scheduleTxnReversalTimer(XMLGregorianCalendar runDate) {
        return _getDescriptor().getProxy().scheduleTxnReversalTimer(runDate);
    }

    public String cancellTxnReversalTimer() {
        return _getDescriptor().getProxy().cancellTxnReversalTimer();
    }

    public String validateAgentDeposit(String sourceMobileId, long amount, String bankId, TransactionLocationType locationType) throws Exception_Exception {
        return _getDescriptor().getProxy().validateAgentDeposit(sourceMobileId,amount,bankId,locationType);
    }

    public ProcessResponse depositAgentCash(DepositInfo depositInfo) {
        return _getDescriptor().getProxy().depositAgentCash(depositInfo);
    }

    public String cancellCommissionTimer() {
        return _getDescriptor().getProxy().cancellCommissionTimer();
    }

    public String saveCommissionConfigData(String email, String timeToRun, String expPeriod) {
        return _getDescriptor().getProxy().saveCommissionConfigData(email,timeToRun,expPeriod);
    }

    public String scheduleCommissionTimer(XMLGregorianCalendar runDate) {
        return _getDescriptor().getProxy().scheduleCommissionTimer(runDate);
    }

    public ProcessResponse processAlertRegistration(String arg0, String arg1) {
        return _getDescriptor().getProxy().processAlertRegistration(arg0,arg1);
    }

}