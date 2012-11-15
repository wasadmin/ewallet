package zw.co.esolutions.ewallet.limitservices.service;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;

public class LimitServiceSOAPProxy{

    protected Descriptor _descriptor;

    public class Descriptor {
        private zw.co.esolutions.ewallet.limitservices.service.LimitService_Service _service = null;
        private zw.co.esolutions.ewallet.limitservices.service.LimitService _proxy = null;
        private Dispatch<Source> _dispatch = null;

        public Descriptor() {
            _service = new zw.co.esolutions.ewallet.limitservices.service.LimitService_Service();
            initCommon();
        }

        public Descriptor(URL wsdlLocation, QName serviceName) {
            _service = new zw.co.esolutions.ewallet.limitservices.service.LimitService_Service(wsdlLocation, serviceName);
            initCommon();
        }

        private void initCommon() {
            _proxy = _service.getLimitServiceSOAP();
        }

        public zw.co.esolutions.ewallet.limitservices.service.LimitService getProxy() {
            return _proxy;
        }

        public Dispatch<Source> getDispatch() {
            if(_dispatch == null ) {
                QName portQName = new QName("http://service.limitservices.ewallet.esolutions.co.zw/", "LimitServiceSOAP");
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

    public LimitServiceSOAPProxy() {
        _descriptor = new Descriptor();
    }

    public LimitServiceSOAPProxy(URL wsdlLocation, QName serviceName) {
        _descriptor = new Descriptor(wsdlLocation, serviceName);
    }

    public Descriptor _getDescriptor() {
        return _descriptor;
    }

    public Limit createLimit(Limit limit, String userName) throws EWalletException_Exception {
        return _getDescriptor().getProxy().createLimit(limit,userName);
    }

    public String deleteLimit(String limitId, String userName) throws EWalletException_Exception {
        return _getDescriptor().getProxy().deleteLimit(limitId,userName);
    }

    public Limit findLimitById(String limitId) {
        return _getDescriptor().getProxy().findLimitById(limitId);
    }

    public List<Limit> getLimitByTypeAndStatusAndBankId(TransactionType type, LimitStatus status, String bankId) {
        return _getDescriptor().getProxy().getLimitByTypeAndStatusAndBankId(type,status,bankId);
    }

    public List<Limit> getLimitByValueTypeAndStatusAndBankId(LimitValueType valueType, LimitStatus status, String bankId) {
        return _getDescriptor().getProxy().getLimitByValueTypeAndStatusAndBankId(valueType,status,bankId);
    }

    public List<Limit> getLimitByAccountClassTypeValueTypeStatusPeriodTypeAndBankId(BankAccountClass accountClass, TransactionType type, LimitValueType valueType, LimitStatus status, LimitPeriodType periodtype, String bankId) {
        return _getDescriptor().getProxy().getLimitByAccountClassTypeValueTypeStatusPeriodTypeAndBankId(accountClass,type,valueType,status,periodtype,bankId);
    }

    public List<Limit> getLimitByValueTypeEffectiveDateStatusAndBankId(LimitValueType valueType, XMLGregorianCalendar effectiveDate, LimitStatus status, String bankId) {
        return _getDescriptor().getProxy().getLimitByValueTypeEffectiveDateStatusAndBankId(valueType,effectiveDate,status,bankId);
    }

    public List<Limit> getLimitByTypeEffectiveDateStatusAndBankId(TransactionType type, XMLGregorianCalendar effectiveDate, LimitStatus status, String bankId) {
        return _getDescriptor().getProxy().getLimitByTypeEffectiveDateStatusAndBankId(type,effectiveDate,status,bankId);
    }

    public List<Limit> getLimitByTypeValueTypeEffectiveDateStatusAndBankId(TransactionType type, LimitValueType valueType, XMLGregorianCalendar effectiveDate, LimitStatus status, String bankId) {
        return _getDescriptor().getProxy().getLimitByTypeValueTypeEffectiveDateStatusAndBankId(type,valueType,effectiveDate,status,bankId);
    }

    public List<Limit> getLimitByAccountClassTypeValueTypeEffectiveDateEndDateStatusAndBankId(BankAccountClass accountClass, TransactionType type, LimitValueType valueType, XMLGregorianCalendar effectiveDate, XMLGregorianCalendar endDate, LimitStatus status, String bankId) {
        return _getDescriptor().getProxy().getLimitByAccountClassTypeValueTypeEffectiveDateEndDateStatusAndBankId(accountClass,type,valueType,effectiveDate,endDate,status,bankId);
    }

    public Limit getActiveLimitByAccountClassTypeValueTypeEffectiveDateEndDateStatusPeriodTypeAndBankId(BankAccountClass accountClass, TransactionType type, LimitValueType valueType, XMLGregorianCalendar effectiveDate, XMLGregorianCalendar endDate, LimitPeriodType periodType, String bankId) {
        return _getDescriptor().getProxy().getActiveLimitByAccountClassTypeValueTypeEffectiveDateEndDateStatusPeriodTypeAndBankId(accountClass,type,valueType,effectiveDate,endDate,periodType,bankId);
    }

    public List<Limit> getAllLimits() {
        return _getDescriptor().getProxy().getAllLimits();
    }

    public Limit getValidLimitOnDateByBankId(TransactionType type, BankAccountClass accountClass, XMLGregorianCalendar onDate, LimitPeriodType periodType, String bankId) {
        return _getDescriptor().getProxy().getValidLimitOnDateByBankId(type,accountClass,onDate,periodType,bankId);
    }

    public List<Limit> getLimitByTypeAndBankId(TransactionType limitType, String bankId) {
        return _getDescriptor().getProxy().getLimitByTypeAndBankId(limitType,bankId);
    }

    public List<Limit> getLimitByAccountClassAndBankId(BankAccountClass accountClass, String bankId) {
        return _getDescriptor().getProxy().getLimitByAccountClassAndBankId(accountClass,bankId);
    }

    public List<Limit> getEffectiveLimitsByBankId(XMLGregorianCalendar limitDate, String bankId) {
        return _getDescriptor().getProxy().getEffectiveLimitsByBankId(limitDate,bankId);
    }

    public List<Limit> getLimitByValueTypeAndBankId(LimitValueType valueType, String bankId) {
        return _getDescriptor().getProxy().getLimitByValueTypeAndBankId(valueType,bankId);
    }

    public List<Limit> getAllLimitsByBankId(String bankId) {
        return _getDescriptor().getProxy().getAllLimitsByBankId(bankId);
    }

    public List<Limit> getLimitByStatusAndBankId(LimitStatus status, String bankId) {
        return _getDescriptor().getProxy().getLimitByStatusAndBankId(status,bankId);
    }

    public Limit editLimit(Limit limit, String userName) throws EWalletException_Exception {
        return _getDescriptor().getProxy().editLimit(limit,userName);
    }

    public Limit approveLimit(Limit limit, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().approveLimit(limit,userName);
    }

    public Limit disapproveLimit(Limit limit, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().disapproveLimit(limit,userName);
    }

    public Limit activateLimit(Limit limit, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().activateLimit(limit,userName);
    }

}