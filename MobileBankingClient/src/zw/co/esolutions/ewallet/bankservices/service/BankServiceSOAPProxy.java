package zw.co.esolutions.ewallet.bankservices.service;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;

public class BankServiceSOAPProxy{

    protected Descriptor _descriptor;

    public class Descriptor {
        private zw.co.esolutions.ewallet.bankservices.service.BankService_Service _service = null;
        private zw.co.esolutions.ewallet.bankservices.service.BankService _proxy = null;
        private Dispatch<Source> _dispatch = null;

        public Descriptor() {
            _service = new zw.co.esolutions.ewallet.bankservices.service.BankService_Service();
            initCommon();
        }

        public Descriptor(URL wsdlLocation, QName serviceName) {
            _service = new zw.co.esolutions.ewallet.bankservices.service.BankService_Service(wsdlLocation, serviceName);
            initCommon();
        }

        private void initCommon() {
            _proxy = _service.getBankServiceSOAP();
        }

        public zw.co.esolutions.ewallet.bankservices.service.BankService getProxy() {
            return _proxy;
        }

        public Dispatch<Source> getDispatch() {
            if(_dispatch == null ) {
                QName portQName = new QName("http://service.bankservices.ewallet.esolutions.co.zw/", "BankServiceSOAP");
                _dispatch = _service.createDispatch(portQName, Source.class, Service.Mode.MESSAGE);

                String proxyEndpointUrl = getEndpoint();
                BindingProvider bp = (BindingProvider) _dispatch;
                String dispatchEndpointUrl = (String) bp.getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
                if(!dispatchEndpointUrl.equals(proxyEndpointUrl))
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

            if(_dispatch != null ) {
            bp = (BindingProvider) _dispatch;
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);
            }
        }
    }

    public BankServiceSOAPProxy() {
        _descriptor = new Descriptor();
    }

    public BankServiceSOAPProxy(URL wsdlLocation, QName serviceName) {
        _descriptor = new Descriptor(wsdlLocation, serviceName);
    }

    public Descriptor _getDescriptor() {
        return _descriptor;
    }

    public Bank createBank(Bank bank, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().createBank(bank,userName);
    }

    public String deleteBank(Bank bank, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().deleteBank(bank,userName);
    }

    public Bank approveBank(Bank bank, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().approveBank(bank,userName);
    }

    public Bank editBank(Bank bank, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().editBank(bank,userName);
    }

    public Bank findBankById(String id) {
        return _getDescriptor().getProxy().findBankById(id);
    }

    public Bank getBankByCode(String code) {
        return _getDescriptor().getProxy().getBankByCode(code);
    }

    public List<Bank> getBankByName(String name) {
        return _getDescriptor().getProxy().getBankByName(name);
    }

    public List<Bank> getBank() {
        return _getDescriptor().getProxy().getBank();
    }

    public List<BankBranch> getBankBranch() {
        return _getDescriptor().getProxy().getBankBranch();
    }

    public List<BankAccount> getBankAccount() {
        return _getDescriptor().getProxy().getBankAccount();
    }

    public BankBranch createBankBranch(BankBranch bankBranch, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().createBankBranch(bankBranch,userName);
    }

    public String deleteBankBranch(BankBranch bankBranch, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().deleteBankBranch(bankBranch,userName);
    }

    public BankBranch approveBankBranch(BankBranch bankBranch, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().approveBankBranch(bankBranch,userName);
    }

    public BankBranch rejectBankBranch(BankBranch bankBranch, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().rejectBankBranch(bankBranch,userName);
    }

    public BankBranch editBankBranch(BankBranch bankBranch, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().editBankBranch(bankBranch,userName);
    }

    public BankBranch findBankBranchById(String id) {
        return _getDescriptor().getProxy().findBankBranchById(id);
    }

    public BankBranch getBankBranchByCode(String code) {
        return _getDescriptor().getProxy().getBankBranchByCode(code);
    }

    public List<BankBranch> getBankBranchByName(String name) {
        return _getDescriptor().getProxy().getBankBranchByName(name);
    }

    public List<BankBranch> getBankBranchByStatus(BankBranchStatus status) {
        return _getDescriptor().getProxy().getBankBranchByStatus(status);
    }

    public List<BankBranch> getBankBranchByBank(String bankId) {
        return _getDescriptor().getProxy().getBankBranchByBank(bankId);
    }

    public BankAccount createBankAccount(BankAccount bankAccount, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().createBankAccount(bankAccount,userName);
    }

    public BankAccount deleteBankAccount(BankAccount bankAccount, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().deleteBankAccount(bankAccount,userName);
    }

    public BankAccount approveBankAccount(BankAccount bankAccount, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().approveBankAccount(bankAccount,userName);
    }

    public BankAccount rejectBankAccount(BankAccount bankAccount, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().rejectBankAccount(bankAccount,userName);
    }

    public BankAccount editBankAccount(BankAccount bankAccount, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().editBankAccount(bankAccount,userName);
    }

    public BankAccount findBankAccountById(String id) {
        return _getDescriptor().getProxy().findBankAccountById(id);
    }

    public List<BankAccount> getBankAccountByAccountHolderIdAndOwnerType(String accountHolderId, OwnerType ownerType) {
        return _getDescriptor().getProxy().getBankAccountByAccountHolderIdAndOwnerType(accountHolderId,ownerType);
    }

    public BankAccount getBankAccountByAccountNumberAndOwnerType(String accountNumber, OwnerType ownerType) {
        return _getDescriptor().getProxy().getBankAccountByAccountNumberAndOwnerType(accountNumber,ownerType);
    }

    public List<BankAccount> getBankAccountsByAccountNumber(String bankAccountNumber) {
        return _getDescriptor().getProxy().getBankAccountsByAccountNumber(bankAccountNumber);
    }

    public List<BankAccount> getBankAccountByStatus(BankAccountStatus status) {
        return _getDescriptor().getProxy().getBankAccountByStatus(status);
    }

    public List<BankAccount> getBankAccountByStatusAndOwnerType(BankAccountStatus status, OwnerType ownerType) {
        return _getDescriptor().getProxy().getBankAccountByStatusAndOwnerType(status,ownerType);
    }

    public List<BankAccount> getBankAccountsAwaitingApproval() {
        return _getDescriptor().getProxy().getBankAccountsAwaitingApproval();
    }

    public List<BankAccount> getBankAccountByType(BankAccountType type) {
        return _getDescriptor().getProxy().getBankAccountByType(type);
    }

    public List<BankAccount> getBankAccountByLevel(String level) {
        return _getDescriptor().getProxy().getBankAccountByLevel(level);
    }

    public List<BankAccount> getBankAccountByBranch(String branchId) {
        return _getDescriptor().getProxy().getBankAccountByBranch(branchId);
    }

    public List<BankAccount> getBankAccountByBank(String bankId) {
        return _getDescriptor().getProxy().getBankAccountByBank(bankId);
    }

    public BankAccount getBankAccountByAccountHolderAndTypeAndOwnerType(String accountHolderId, BankAccountType type, OwnerType ownerType, String accountNumber) throws Exception_Exception {
        return _getDescriptor().getProxy().getBankAccountByAccountHolderAndTypeAndOwnerType(accountHolderId,type,ownerType,accountNumber);
    }

    public List<BankAccount> getBankAccountByLevelAndStatus(BankAccountLevel arg0, BankAccountStatus arg1) {
        return _getDescriptor().getProxy().getBankAccountByLevelAndStatus(arg0,arg1);
    }

    public BankAccount getPrimaryAccountByAccountHolderIdAndOwnerType(boolean isPrimaryAccount, String accountHolderId, OwnerType ownerType) {
        return _getDescriptor().getProxy().getPrimaryAccountByAccountHolderIdAndOwnerType(isPrimaryAccount,accountHolderId,ownerType);
    }

    public List<Bank> getBankByStatus(BankStatus status) {
        return _getDescriptor().getProxy().getBankByStatus(status);
    }

    public List<BankAccount> deRegisterBankAccountsByOwnerId(String ownerId, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().deRegisterBankAccountsByOwnerId(ownerId,userName);
    }

    public List<BankAccount> getBankAccountsByAccountHolderIdAndOwnerTypeAndStatus(String accountHolderId, OwnerType ownerType, BankAccountStatus status) {
        return _getDescriptor().getProxy().getBankAccountsByAccountHolderIdAndOwnerTypeAndStatus(accountHolderId,ownerType,status);
    }

    public List<BankAccount> getBankAccountsByAccountHolderIdAndOwnerTypeAndStatusAndBankAccountType(String accountHolderId, OwnerType ownerType, BankAccountStatus status, BankAccountType bankAAccountType) {
        return _getDescriptor().getProxy().getBankAccountsByAccountHolderIdAndOwnerTypeAndStatusAndBankAccountType(accountHolderId,ownerType,status,bankAAccountType);
    }

    public List<BankAccount> getBankAccountsByMinAttributes(String bankId, String branchId, BankAccountType accType, String accountNumber, String accountName, BankAccountStatus status, int startIndex, int maxResults) {
        return _getDescriptor().getProxy().getBankAccountsByMinAttributes(bankId,branchId,accType,accountNumber,accountName,status,startIndex,maxResults);
    }

    public BankAccount getBankAccountsByAccountHolderIdAndOwnerTypeAndBankAccountType(String accountHolderId, OwnerType ownerType, BankAccountType bankAccountType) {
        return _getDescriptor().getProxy().getBankAccountsByAccountHolderIdAndOwnerTypeAndBankAccountType(accountHolderId,ownerType,bankAccountType);
    }

    public BankAccount deRegisterBankAccount(BankAccount bankAccount, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().deRegisterBankAccount(bankAccount,userName);
    }

    public BankAccount createCustomerBankAccount(BankAccount bankAccount, String source, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().createCustomerBankAccount(bankAccount,source,userName);
    }

    public BankAccount getUniqueBankAccountByAccountNumberAndBankId(String accountNumber, String bankId) {
        return _getDescriptor().getProxy().getUniqueBankAccountByAccountNumberAndBankId(accountNumber,bankId);
    }

    public BankAccount getUniqueBankAccountByAccountNumber(String accountNumber) {
        return _getDescriptor().getProxy().getUniqueBankAccountByAccountNumber(accountNumber);
    }

    public AccountBalance createAccountBalance(AccountBalance accountBalance) throws EWalletException_Exception {
        return _getDescriptor().getProxy().createAccountBalance(accountBalance);
    }

    public String deleteAccountBalance(String accountBalanceId) throws EWalletException_Exception {
        return _getDescriptor().getProxy().deleteAccountBalance(accountBalanceId);
    }

    public AccountBalance updateAccountBalance(AccountBalance accountBalance) throws EWalletException_Exception {
        return _getDescriptor().getProxy().updateAccountBalance(accountBalance);
    }

    public AccountBalance findAccountBalanceById(String accountBalanceId) {
        return _getDescriptor().getProxy().findAccountBalanceById(accountBalanceId);
    }

    public List<AccountBalance> findAllAccountBalances() {
        return _getDescriptor().getProxy().findAllAccountBalances();
    }

    public Transaction createTransaction(Transaction transaction) throws EWalletException_Exception {
        return _getDescriptor().getProxy().createTransaction(transaction);
    }

    public String deleteTransaction(String transactionId) throws EWalletException_Exception {
        return _getDescriptor().getProxy().deleteTransaction(transactionId);
    }

    public Transaction updateTransaction(Transaction transaction) throws EWalletException_Exception {
        return _getDescriptor().getProxy().updateTransaction(transaction);
    }

    public Transaction findTransactionById(String transactionId) {
        return _getDescriptor().getProxy().findTransactionById(transactionId);
    }

    public List<Transaction> findAllTransactions() {
        return _getDescriptor().getProxy().findAllTransactions();
    }

    public AccountBalance getAccountBalanceByAccountAndDate(String accountId, XMLGregorianCalendar transactionDate) throws Exception_Exception {
        return _getDescriptor().getProxy().getAccountBalanceByAccountAndDate(accountId,transactionDate);
    }

    public List<AccountBalance> getAccountBalanceByAccount(String accountId) throws Exception_Exception {
        return _getDescriptor().getProxy().getAccountBalanceByAccount(accountId);
    }

    public List<Transaction> getTransactionByProcessTransactionMessageId(String messageId) {
        return _getDescriptor().getProxy().getTransactionByProcessTransactionMessageId(messageId);
    }

    public List<Transaction> getTransactionsByAllAttributes(TransactionUniversalPojo transactionUniPojo) {
        return _getDescriptor().getProxy().getTransactionsByAllAttributes(transactionUniPojo);
    }

    public List<AccountBalance> getAccountBalanceByTxnRef(String transactionRef) {
        return _getDescriptor().getProxy().getAccountBalanceByTxnRef(transactionRef);
    }

    public AccountBalance getOpeningBalance(String accountId, XMLGregorianCalendar openingDate) throws Exception_Exception {
        return _getDescriptor().getProxy().getOpeningBalance(accountId,openingDate);
    }

    public AccountBalance getClosingBalance(String accountId, XMLGregorianCalendar closingDate) throws Exception_Exception {
        return _getDescriptor().getProxy().getClosingBalance(accountId,closingDate);
    }

    public List<AccountBalance> getAccountBalanceByAccountIdAndTxnRef(String accountId, String transactionRef) {
        return _getDescriptor().getProxy().getAccountBalanceByAccountIdAndTxnRef(accountId,transactionRef);
    }

    public EWalletPostingResponse reverseEWalletEntries(List<TransactionPostingInfo> transactionPostingInfos, List<ChargePostingInfo> chargePostingInfos) throws Exception_Exception {
        return _getDescriptor().getProxy().reverseEWalletEntries(transactionPostingInfos,chargePostingInfos);
    }

    public EWalletPostingResponse postEWalletEntries(List<TransactionPostingInfo> transactionPostingInfos, List<ChargePostingInfo> chargePostingInfos) throws Exception_Exception {
        return _getDescriptor().getProxy().postEWalletEntries(transactionPostingInfos,chargePostingInfos);
    }

    public TransactionSummaryItem getTransactionSummaryItem(String accountId, XMLGregorianCalendar txnDate, TransactionType txnType) throws Exception_Exception {
        return _getDescriptor().getProxy().getTransactionSummaryItem(accountId,txnDate,txnType);
    }

    public TransactionSummary getTransactionSummary(String accountId, XMLGregorianCalendar txnDate) throws Exception_Exception {
        return _getDescriptor().getProxy().getTransactionSummary(accountId,txnDate);
    }

}