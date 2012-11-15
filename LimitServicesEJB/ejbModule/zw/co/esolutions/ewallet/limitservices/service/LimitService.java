package zw.co.esolutions.ewallet.limitservices.service;
import java.util.Date;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import zw.co.esolutions.ewallet.enums.BankAccountClass;
import zw.co.esolutions.ewallet.enums.LimitPeriodType;
import zw.co.esolutions.ewallet.enums.LimitStatus;
import zw.co.esolutions.ewallet.enums.TransactionType;
import zw.co.esolutions.ewallet.enums.LimitValueType;
import zw.co.esolutions.ewallet.limitservices.model.Limit;
import zw.co.esolutions.ewallet.serviceexception.EWalletException;

@WebService(name = "LimitService")
public interface LimitService {

	Limit createLimit(@WebParam(name = "limit")Limit limit, 
			@WebParam(name = "userName")String userName) throws EWalletException;

	String deleteLimit(@WebParam(name = "limitId")String limitId, 
			@WebParam(name = "userName")String userName) throws EWalletException;

	Limit findLimitById(@WebParam(name = "limitId")String limitId);
	
	List<Limit> getLimitByTypeAndStatusAndBankId(@WebParam(name = "type")TransactionType type, @WebParam(name = "status") LimitStatus status, 
			@WebParam(name = "bankId")String bankId);
	
	List<Limit> getLimitByValueTypeAndStatusAndBankId(@WebParam(name = "valueType")LimitValueType valueType
			, @WebParam(name = "status") LimitStatus status, @WebParam(name = "bankId")String bankId);
	
	List<Limit> getLimitByAccountClassTypeValueTypeStatusPeriodTypeAndBankId(@WebParam(name = "accountClass")BankAccountClass accountClass, @WebParam(name = "type")TransactionType type,
			@WebParam(name = "valueType")LimitValueType valueType, @WebParam(name = "status") LimitStatus status, 
			@WebParam(name = "periodtype")LimitPeriodType periodType, @WebParam(name = "bankId")String bankId);
	
	List<Limit> getLimitByValueTypeEffectiveDateStatusAndBankId(@WebParam(name = "valueType")LimitValueType valueType,
			@WebParam(name = "effectiveDate")Date effectiveDate
			, @WebParam(name = "status") LimitStatus status, @WebParam(name = "bankId")String bankId);
	
	List<Limit> getLimitByTypeEffectiveDateStatusAndBankId(@WebParam(name = "type")TransactionType Type, 
			@WebParam(name = "effectiveDate") Date effectiveDate
			, @WebParam(name = "status") LimitStatus status, @WebParam(name = "bankId")String bankId);
	
	List<Limit> getLimitByTypeValueTypeEffectiveDateStatusAndBankId(@WebParam(name = "type") TransactionType type,
			@WebParam(name = "valueType")LimitValueType valueType,
			@WebParam(name = "effectiveDate")Date effectiveDate,
			@WebParam(name = "status") LimitStatus status, @WebParam(name = "bankId")String bankId);
	
	List<Limit> getLimitByAccountClassTypeValueTypeEffectiveDateEndDateStatusAndBankId(@WebParam(name = "accountClass")BankAccountClass accountClass,
			@WebParam(name = "type") TransactionType type,
			@WebParam(name = "valueType")LimitValueType valueType,
			@WebParam(name = "effectiveDate")Date effectiveDate,
			@WebParam(name = "endDate")Date endDate, @WebParam(name = "status") LimitStatus status,
			@WebParam(name = "bankId")String bankId);

	
	Limit getActiveLimitByAccountClassTypeValueTypeEffectiveDateEndDateStatusPeriodTypeAndBankId(@WebParam(name = "accountClass")BankAccountClass accountClass, 
			@WebParam(name = "type") TransactionType type,
			@WebParam(name = "valueType")LimitValueType valueType,
			@WebParam(name = "effectiveDate")Date effectiveDate,
			@WebParam(name = "endDate") Date endDate,
			@WebParam(name = "periodType")LimitPeriodType periodType,
			@WebParam(name = "bankId")String bankId);
	
	List<Limit> getAllLimits();

	Limit getValidLimitOnDateByBankId(@WebParam(name = "type")TransactionType type, @WebParam(name = "accountClass")BankAccountClass accountClass,
			@WebParam(name = "onDate")Date onDate, @WebParam(name = "periodType")LimitPeriodType periodType, @WebParam(name = "bankId")String bankId);

	List<Limit> getLimitByTypeAndBankId(@WebParam(name = "limitType")TransactionType type, @WebParam(name = "bankId")String bankId);

	List<Limit> getLimitByAccountClassAndBankId(@WebParam(name = "accountClass")BankAccountClass accountClass, @WebParam(name = "bankId")String bankId);

	List<Limit> getEffectiveLimitsByBankId(@WebParam(name = "limitDate")Date limitDate, @WebParam(name = "bankId")String bankId);

	List<Limit> getLimitByValueTypeAndBankId(@WebParam(name = "valueType")LimitValueType valueType, @WebParam(name = "bankId")String bankId);

	List<Limit> getAllLimitsByBankId(@WebParam(name = "bankId")String bankId);
	
	List<Limit> getLimitByStatusAndBankId(@WebParam(name = "status") LimitStatus status, 
			@WebParam(name = "bankId")String bankId);

	Limit editLimit(@WebParam(name = "limit")Limit limit, @WebParam(name = "userName")String userName) throws EWalletException;

	Limit approveLimit(@WebParam(name = "limit")Limit limit, @WebParam(name = "userName")String userName) throws Exception;

	Limit disapproveLimit(@WebParam(name = "limit")Limit limit, @WebParam(name = "userName")String userName) throws Exception;

	Limit activateLimit(@WebParam(name = "limit")Limit limit, @WebParam(name = "userName")String userName) throws Exception;

}
