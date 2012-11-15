package zw.co.esolutions.ewallet.bankservices.service;

import java.util.Date;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import zw.co.esolutions.ewallet.bankservices.model.AccountBalance;
import zw.co.esolutions.ewallet.bankservices.model.Bank;
import zw.co.esolutions.ewallet.bankservices.model.BankAccount;
import zw.co.esolutions.ewallet.bankservices.model.BankBranch;
import zw.co.esolutions.ewallet.bankservices.model.ChargePostingInfo;
import zw.co.esolutions.ewallet.bankservices.model.EWalletPostingResponse;
import zw.co.esolutions.ewallet.bankservices.model.Transaction;
import zw.co.esolutions.ewallet.bankservices.model.TransactionPostingInfo;
import zw.co.esolutions.ewallet.bankservices.model.TransactionSummary;
import zw.co.esolutions.ewallet.bankservices.model.TransactionSummaryItem;
import zw.co.esolutions.ewallet.bankservices.model.TransactionUniversalPojo;
import zw.co.esolutions.ewallet.enums.BankAccountLevel;
import zw.co.esolutions.ewallet.enums.BankAccountStatus;
import zw.co.esolutions.ewallet.enums.BankAccountType;
import zw.co.esolutions.ewallet.enums.BankBranchStatus;
import zw.co.esolutions.ewallet.enums.BankStatus;
import zw.co.esolutions.ewallet.enums.OwnerType;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.serviceexception.EWalletException;

@WebService(name = "BankService")
public interface BankService {

	Bank createBank(@WebParam(name = "bank") Bank bank, @WebParam(name = "userName") String userName) throws Exception;

	String deleteBank(@WebParam(name = "bank") Bank bank, @WebParam(name = "userName") String userName) throws Exception;

	Bank approveBank(@WebParam(name = "bank") Bank bank, @WebParam(name = "userName") String userName) throws Exception;

	Bank editBank(@WebParam(name = "bank") Bank bank, @WebParam(name = "userName") String userName) throws Exception;

	Bank findBankById(@WebParam(name = "id") String id);

	Bank getBankByCode(@WebParam(name = "code") String code);

	List<Bank> getBankByName(@WebParam(name = "name") String name);

	List<Bank> getBank();

	List<BankBranch> getBankBranch();

	List<BankAccount> getBankAccount();

	BankBranch createBankBranch(@WebParam(name = "bankBranch") BankBranch bankBranch, @WebParam(name = "userName") String userName) throws Exception;

	String deleteBankBranch(@WebParam(name = "bankBranch") BankBranch bankBranch, @WebParam(name = "userName") String userName) throws Exception;

	BankBranch approveBankBranch(@WebParam(name = "bankBranch") BankBranch bankBranch, @WebParam(name = "userName") String userName) throws Exception;

	BankBranch rejectBankBranch(@WebParam(name = "bankBranch") BankBranch bankBranch, @WebParam(name = "userName") String userName) throws Exception;

	BankBranch editBankBranch(@WebParam(name = "bankBranch") BankBranch bankBranch, @WebParam(name = "userName") String userName) throws Exception;

	BankBranch findBankBranchById(@WebParam(name = "id") String id);

	BankBranch getBankBranchByCode(@WebParam(name = "code") String code);

	List<BankBranch> getBankBranchByName(@WebParam(name = "name") String name);

	List<BankBranch> getBankBranchByStatus(@WebParam(name = "status") BankBranchStatus status);

	List<BankBranch> getBankBranchByBank(@WebParam(name = "bankId") String id);

	BankAccount createBankAccount(@WebParam(name = "bankAccount") BankAccount bankAccount, @WebParam(name = "userName") String userName) throws Exception;

	BankAccount deleteBankAccount(@WebParam(name = "bankAccount") BankAccount bankAccount, @WebParam(name = "userName") String userName) throws Exception;

	BankAccount approveBankAccount(@WebParam(name = "bankAccount") BankAccount bankAccount, @WebParam(name = "userName") String userName) throws Exception;

	BankAccount rejectBankAccount(@WebParam(name = "bankAccount") BankAccount bankAccount, @WebParam(name = "userName") String userName) throws Exception;

	BankAccount editBankAccount(@WebParam(name = "bankAccount") BankAccount bankAccount, @WebParam(name = "userName") String userName) throws Exception;

	BankAccount findBankAccountById(@WebParam(name = "id") String id);

	List<BankAccount> getBankAccountByAccountHolderIdAndOwnerType(@WebParam(name = "accountHolderId") String accountHolderId, @WebParam(name = "ownerType") OwnerType ownerType);

	BankAccount getBankAccountByAccountNumberAndOwnerType(@WebParam(name = "accountNumber") String accountNumber, @WebParam(name = "ownerType") OwnerType ownerType);

	List<BankAccount> getBankAccountsByAccountNumber(@WebParam(name = "bankAccountNumber") String accountNumber);

	List<BankAccount> getBankAccountByStatus(@WebParam(name = "status") BankAccountStatus status);

	List<BankAccount> getBankAccountByStatusAndOwnerType(@WebParam(name = "status") BankAccountStatus status, @WebParam(name = "ownerType") OwnerType ownerType);

	List<BankAccount> getBankAccountsAwaitingApproval();

	List<BankAccount> getBankAccountByType(@WebParam(name = "type") BankAccountType type);

	List<BankAccount> getBankAccountByLevel(@WebParam(name = "level") String level);

	List<BankAccount> getBankAccountByBranch(@WebParam(name = "branchId") String branchId);

	List<BankAccount> getBankAccountByBank(@WebParam(name = "bankId") String bankId);

	BankAccount getBankAccountByAccountHolderAndTypeAndOwnerType(@WebParam(name = "accountHolderId") String accountHolderId, @WebParam(name = "type") BankAccountType type, @WebParam(name = "ownerType") OwnerType ownerType, @WebParam(name = "accountNumber") String acccountNumber) throws Exception;

	List<BankAccount> getBankAccountByLevelAndStatus(BankAccountLevel level, BankAccountStatus status);

	BankAccount getPrimaryAccountByAccountHolderIdAndOwnerType(@WebParam(name = "isPrimaryAccount") boolean primaryAccount, @WebParam(name = "accountHolderId") String accountHolderId, @WebParam(name = "ownerType") OwnerType ownerType);

	List<Bank> getBankByStatus(@WebParam(name = "status") BankStatus status);

	List<BankAccount> deRegisterBankAccountsByOwnerId(@WebParam(name = "ownerId") String ownerId, @WebParam(name = "userName") String userName) throws Exception;

	List<BankAccount> getBankAccountsByAccountHolderIdAndOwnerTypeAndStatus(@WebParam(name = "accountHolderId") String accountHolderId, @WebParam(name = "ownerType") OwnerType ownerType, @WebParam(name = "status") BankAccountStatus status);

	List<BankAccount> getBankAccountsByAccountHolderIdAndOwnerTypeAndStatusAndBankAccountType(@WebParam(name = "accountHolderId") String accountHolderId, @WebParam(name = "ownerType") OwnerType ownerType, @WebParam(name = "status") BankAccountStatus status, @WebParam(name = "bankAAccountType") BankAccountType bankAccountType);

	List<BankAccount> getBankAccountsByMinAttributes(@WebParam(name = "bankId") String bankId, @WebParam(name = "branchId") String branchId, @WebParam(name = "accType") BankAccountType accType, @WebParam(name = "accountNumber") String accountNumber, @WebParam(name = "accountName") String accountName, @WebParam(name = "status") BankAccountStatus status, @WebParam(name = "startIndex") int startIndex, @WebParam(name = "maxResults") int maxValue);

	BankAccount getBankAccountsByAccountHolderIdAndOwnerTypeAndBankAccountType(@WebParam(name = "accountHolderId") String accountHolderId, @WebParam(name = "ownerType") OwnerType ownerType, @WebParam(name = "bankAccountType") BankAccountType bankAccountType);

	BankAccount deRegisterBankAccount(@WebParam(name = "bankAccount") BankAccount bankAccount, @WebParam(name = "userName") String userName) throws Exception;

	BankAccount createCustomerBankAccount(@WebParam(name = "bankAccount") BankAccount bankAccount, @WebParam(name = "source") String source, @WebParam(name = "userName") String userName) throws Exception;

	BankAccount getUniqueBankAccountByAccountNumberAndBankId(@WebParam(name = "accountNumber") String accountNumber, @WebParam(name = "bankId") String bankId);

	BankAccount getUniqueBankAccountByAccountNumber(@WebParam(name = "accountNumber") String accountNumber);

	AccountBalance createAccountBalance(@WebParam(name = "accountBalance") AccountBalance accountBalance) throws EWalletException;

	String deleteAccountBalance(@WebParam(name = "accountBalanceId") String accountBalanceId) throws EWalletException;

	AccountBalance updateAccountBalance(@WebParam(name = "accountBalance") AccountBalance accountBalance) throws EWalletException;

	AccountBalance findAccountBalanceById(@WebParam(name = "accountBalanceId") String accoutntBalanceId);

	List<AccountBalance> findAllAccountBalances();

	Transaction createTransaction(@WebParam(name = "transaction") Transaction transaction) throws EWalletException;

	String deleteTransaction(@WebParam(name = "transactionId") String transactionId) throws EWalletException;

	Transaction updateTransaction(@WebParam(name = "transaction") Transaction transaction) throws EWalletException;

	Transaction findTransactionById(@WebParam(name = "transactionId") String transactionId);

	List<Transaction> findAllTransactions();

	AccountBalance getAccountBalanceByAccountAndDate(@WebParam(name = "accountId") String accountId, @WebParam(name = "transactionDate") Date date) throws Exception;

	List<AccountBalance> getAccountBalanceByAccount(@WebParam(name = "accountId") String accountId) throws Exception;

	List<Transaction> getTransactionByProcessTransactionMessageId(@WebParam(name = "messageId") String messageId);

	List<Transaction> getTransactionsByAllAttributes(@WebParam(name = "transactionUniPojo") TransactionUniversalPojo universal);

	List<AccountBalance> getAccountBalanceByTxnRef(@WebParam(name = "transactionRef") String txnRef);

	AccountBalance getOpeningBalance(@WebParam(name = "accountId") String accountId, @WebParam(name = "openingDate") Date date) throws Exception;

	AccountBalance getClosingBalance(@WebParam(name = "accountId") String accountId, @WebParam(name = "closingDate") Date date) throws Exception;

	List<AccountBalance> getAccountBalanceByAccountIdAndTxnRef(@WebParam(name = "accountId") String accountId, @WebParam(name = "transactionRef") String txnRef);

	EWalletPostingResponse reverseEWalletEntries(@WebParam(name = "transactionPostingInfos")TransactionPostingInfo [] transactionPostingInfos, @WebParam(name = "chargePostingInfos") ChargePostingInfo [] chargePostingInfos) throws Exception;
	
	EWalletPostingResponse postEWalletEntries(@WebParam(name = "transactionPostingInfos")TransactionPostingInfo [] transactionPostingInfos, @WebParam(name = "chargePostingInfos") ChargePostingInfo [] chargePostingInfos) throws Exception;

	TransactionSummaryItem getTransactionSummaryItem(@WebParam(name = "accountId")String accountId, @WebParam(name = "txnDate")Date txnDate, @WebParam(name = "txnType")TransactionType txnType) throws Exception;

	TransactionSummary getTransactionSummary(@WebParam(name = "accountId")String accountId, @WebParam(name = "txnDate")Date txnDate) throws Exception;
}
