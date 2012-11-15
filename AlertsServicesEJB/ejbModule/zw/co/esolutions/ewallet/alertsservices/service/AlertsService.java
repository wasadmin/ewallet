package zw.co.esolutions.ewallet.alertsservices.service;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import zw.co.esolutions.ewallet.alertsservices.model.AlertOption;
import zw.co.esolutions.ewallet.alertsservices.model.TransactionType;
import zw.co.esolutions.ewallet.enums.AlertOptionStatus;
import zw.co.esolutions.ewallet.enums.TransactionTypeStatus;


@WebService(name="AlertsService")
public interface AlertsService {

	void createAlertOptionsForAccount(@WebParam(name="accountId")String accountId,@WebParam(name="mobileProfileId")String mobileProfileId,@WebParam(name="username")String username) throws Exception;
	AlertOption createAlertOption(@WebParam(name="alertOption")AlertOption alertOption,@WebParam(name="username")String username) throws Exception;
	AlertOption editAlertOption(@WebParam(name="alertOption")AlertOption alertOption,@WebParam(name="username")String username) throws Exception;
	AlertOption approveAlertOption(@WebParam(name="alertOption")AlertOption alertOption,@WebParam(name="username")String username) throws Exception;
	AlertOption rejectAlertOption(@WebParam(name="alertOption")AlertOption alertOption,@WebParam(name="username")String username) throws Exception;
	AlertOption deleteAlertOption(@WebParam(name="alertOption")AlertOption alertOption,@WebParam(name="username")String username) throws Exception;
	AlertOption enableAlertOption(@WebParam(name="alertOption")AlertOption alertOption,@WebParam(name="username")String username) throws Exception;
	AlertOption disableAlertOption(@WebParam(name="alertOption")AlertOption alertOption,@WebParam(name="username")String username) throws Exception;
	AlertOption findAlertOptionById(@WebParam(name="alertOptionId")String alertOptionId);
	AlertOption getAlertOptionByBankAccountAndMobileProfileAndTransactionType(@WebParam(name="bankAccountId")String bankAccountId,@WebParam(name="mobileProfileId")String mobileProfileId,@WebParam(name="transactionTypeId")String transactionTypeId);
	List<AlertOption> getAlertOptionByStatus(@WebParam(name="status")AlertOptionStatus status);
	List<AlertOption> getAlertOptionByBankAccountId(@WebParam(name="bankAccountId")String bankAccountId);
	List<AlertOption> getAlertOptionByMobileProfileId(@WebParam(name="mobileProfileId")String mobileProfileId);
	List<AlertOption> getAlertOptionByTransationTypeId(@WebParam(name="transactionTypeId")String transactionTypeId);
	
	TransactionType createTransactionType(@WebParam(name="transactionType")TransactionType transactionType,@WebParam(name="username")String username) throws Exception;
	TransactionType editTransactionType(@WebParam(name="transactionType")TransactionType transactionType,@WebParam(name="username")String username) throws Exception;
	TransactionType approveTransactionType(@WebParam(name="transactionType")TransactionType transactionType,@WebParam(name="username")String username) throws Exception;
	TransactionType rejectTransactionType(@WebParam(name="transactionType")TransactionType transactionType,@WebParam(name="username")String username) throws Exception;
	TransactionType deleteTransactionType(@WebParam(name="transactionType")TransactionType transactionType,@WebParam(name="username")String username) throws Exception;
	TransactionType enableTransactionType(@WebParam(name="transactionType")TransactionType transactionType,@WebParam(name="username")String username) throws Exception;
	TransactionType disableTransactionType(@WebParam(name="transactionType")TransactionType transactionType,@WebParam(name="username")String username) throws Exception;
	TransactionType findTransactionType(@WebParam(name="transactionTypeId")String transactionTypeId);
	TransactionType getTransactionTypeByCode(@WebParam(name="transactionTypeCode")String transactionTypeCode);
	TransactionType getTransactionTypeByAlertOption(@WebParam(name="alertOption")AlertOption alertOption);
	List<TransactionType> getTransactionTypeByStatus(@WebParam(name="status")TransactionTypeStatus status);
	List<TransactionType> getAllTransactionTypes();
}
