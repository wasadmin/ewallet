package zw.co.esolutions.ewallet.tariffservices.service;
import java.util.Date;
import java.util.List;
import javax.jws.WebParam;
import javax.jws.WebService;

import zw.co.esolutions.ewallet.enums.AgentType;
import zw.co.esolutions.ewallet.enums.CustomerClass;
import zw.co.esolutions.ewallet.enums.TariffStatus;
import zw.co.esolutions.ewallet.enums.TariffType;
import zw.co.esolutions.ewallet.enums.TariffValueType;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.serviceexception.EWalletException;
import zw.co.esolutions.ewallet.tariffservices.model.Tariff;
import zw.co.esolutions.ewallet.tariffservices.model.TariffTable;

@WebService(name = "TariffService")
public interface TariffService {

	Tariff createCommission(@WebParam(name = "tariff")Tariff tariff, 
			@WebParam(name = "userName")String userName) throws EWalletException;

	String deleteTariff(@WebParam(name = "tariffId")String tariffId, 
			@WebParam(name = "userName")String userName) throws EWalletException;

	Tariff editCommission(@WebParam(name = "tariff")Tariff tariff, 
			@WebParam(name = "userName")String userName) throws EWalletException;

	Tariff findTariffById(@WebParam(name = "tariffId")String tariffId);
	
	List<Tariff> getTariffByTariffTableAndBankId(@WebParam(name = "tariffTableId") String tariffTableId, 
			@WebParam(name = "bankId")String bankId);
	
	List<Tariff> getTariffByTariffTableIdValueTypeAndBankId(@WebParam(name = "tariffTableId")String tariffTableId, 
			@WebParam(name = "valueType")TariffValueType valueType, @WebParam(name = "bankId")String bankId);

	List<Tariff> getAllTariffs();

	TariffTable createCommissionTable(@WebParam(name = "tariffTable")TariffTable tariffTable, 
			@WebParam(name = "userName")String userName) throws EWalletException;

	String deleteTariffTable(@WebParam(name = "tariffTableId")String tariffTableId, 
			@WebParam(name = "userName")String userName) throws EWalletException;

	TariffTable findTariffTableById(@WebParam(name = "tariffTableId")String tariffId);
	
	List<TariffTable> getTariffTableByTransactionTypeAndBankId(@WebParam(name = "transactionType")TransactionType transactionType, 
			@WebParam(name = "bankId")String bankId);
	
	List<TariffTable> getTariffTableByTariffTypeAndBankId(@WebParam(name = "tariffType")TariffType tariffType, 
			@WebParam(name = "bankId")String bankId);
	
	List<TariffTable> getTariffTableByTariffTypeTransactionAgentTypeAndBankId(@WebParam(name = "tariffType")TariffType tariffType,
			@WebParam(name = "transactionType")TransactionType transactionType, @WebParam(name = "agentType")AgentType agentType, 
			@WebParam(name = "bankId")String bankId);
	
	TariffTable getTariffTableByTariffTypeEffectiveDateAndBankId(@WebParam(name = "tariffType")TariffType tariffType,
			@WebParam(name = "effectiveDate") Date effectiveDate, @WebParam(name = "agentType")AgentType agentType, 
			@WebParam(name = "bankId")String bankId);
	
	List<TariffTable> getTariffTableByTransactionTypeEffectiveDateAgentTypeAndBankId(@WebParam(name = "transactionType")TransactionType  transactionType,
			@WebParam(name = "effectiveDate") Date effectiveDate, @WebParam(name = "agentType")AgentType agentType, 
			@WebParam(name = "bankId")String bankId);
	
	TariffTable getTariffTableByTariffTypeTransactionTypeEffectiveDateAndBankId(@WebParam(name = "tariffType")TariffType tariffType,
			@WebParam(name = "transactionType")TransactionType transactionType,
			@WebParam(name = "effectiveDate") Date effectiveDate, 
			@WebParam(name = "agentType")AgentType agentType, @WebParam(name = "bankid")String bankId);
	
	TariffTable getTariffTableByTariffTypeTransactionTypeEffectiveDateEndDateAndBankId(@WebParam(name = "tariffType")TariffType tariffType,
			@WebParam(name = "transactionType")TransactionType transactionType,
			@WebParam(name = "effectiveDate") Date effectiveDate,
			@WebParam(name = "endDate") Date endDate,
			@WebParam(name = "agentType")AgentType agentType, 
			@WebParam(name = "bankId")String bankId);
	
	

	List<TariffTable> getAllTariffTables();

	List<TariffTable> getTariffTableByDateRangeAndBankId(@WebParam(name = "fromDate")Date fromDate, @WebParam(name = "toDate")Date toDate, 
			@WebParam(name = "bankId")String bankId);

	List<TariffTable> getEffectiveTariffTablesForBank(@WebParam(name = "bankId")String bankId);

	long calculateTariffCharge(@WebParam(name = "customerClass")CustomerClass customerClass,
			@WebParam(name = "txnType")TransactionType txnType, @WebParam(name = "agentType")AgentType agentType, @WebParam(name = "amount")long amount,
			@WebParam(name = "bankId")String bankId) throws EWalletException;

	Tariff retrieveAppropriateTariff(@WebParam(name = "customerClass")CustomerClass customerClass,
			@WebParam(name = "txnType")TransactionType txnType, @WebParam(name = "agentType")AgentType agentType, @WebParam(name = "amount")long amount, 
			@WebParam(name = "bankId")String bankId) throws EWalletException;

	List<Tariff> getAllTariffsByBankId(@WebParam(name = "bankId")String bankId);

	List<TariffTable> getAllTariffTablesByBankId(@WebParam(name = "bankId")String bankId);

	List<Tariff> getTariffsByTariffTableId(@WebParam(name = "tableId")String tableId);

	List<TariffTable> getTariffTableByCustomerClassAndBankId(@WebParam(name = "customerClass")
			CustomerClass customerClass, @WebParam(name = "bankId")String bankId);

	List<TariffTable> getTariffTableByTariffStatusAndBankId(@WebParam(name = "status")
			TariffStatus status, @WebParam(name = "bankId")String bankId);

	TariffTable editCommissionTable(@WebParam(name = "tariffTable")TariffTable tariffTable, @WebParam(name = "userName")String userName)
			throws EWalletException;

	TariffTable approveCommission(@WebParam(name = "tariffTable")TariffTable tariffTable, @WebParam(name = "userName")String userName)
			throws EWalletException;

	TariffTable disapproveCommission(@WebParam(name = "tariffTable")TariffTable tariffTable, @WebParam(name = "userName")String userName)
			throws EWalletException;

}
