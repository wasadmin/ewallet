package zw.co.esolutions.ewallet.tariffservices.service;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;

public class TariffServiceSOAPProxy{

    protected Descriptor _descriptor;

    public class Descriptor {
        private zw.co.esolutions.ewallet.tariffservices.service.TariffService_Service _service = null;
        private zw.co.esolutions.ewallet.tariffservices.service.TariffService _proxy = null;
        private Dispatch<Source> _dispatch = null;

        public Descriptor() {
            _service = new zw.co.esolutions.ewallet.tariffservices.service.TariffService_Service();
            initCommon();
        }

        public Descriptor(URL wsdlLocation, QName serviceName) {
            _service = new zw.co.esolutions.ewallet.tariffservices.service.TariffService_Service(wsdlLocation, serviceName);
            initCommon();
        }

        private void initCommon() {
            _proxy = _service.getTariffServiceSOAP();
        }

        public zw.co.esolutions.ewallet.tariffservices.service.TariffService getProxy() {
            return _proxy;
        }

        public Dispatch<Source> getDispatch() {
            if(_dispatch == null ) {
                QName portQName = new QName("http://service.tariffservices.ewallet.esolutions.co.zw/", "TariffServiceSOAP");
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

    public TariffServiceSOAPProxy() {
        _descriptor = new Descriptor();
    }

    public TariffServiceSOAPProxy(URL wsdlLocation, QName serviceName) {
        _descriptor = new Descriptor(wsdlLocation, serviceName);
    }

    public Descriptor _getDescriptor() {
        return _descriptor;
    }

    public Tariff createCommission(Tariff tariff, String userName) throws EWalletException_Exception {
        return _getDescriptor().getProxy().createCommission(tariff,userName);
    }

    public String deleteTariff(String tariffId, String userName) throws EWalletException_Exception {
        return _getDescriptor().getProxy().deleteTariff(tariffId,userName);
    }

    public Tariff editCommission(Tariff tariff, String userName) throws EWalletException_Exception {
        return _getDescriptor().getProxy().editCommission(tariff,userName);
    }

    public Tariff findTariffById(String tariffId) {
        return _getDescriptor().getProxy().findTariffById(tariffId);
    }

    public List<Tariff> getTariffByTariffTableAndBankId(String tariffTableId, String bankId) {
        return _getDescriptor().getProxy().getTariffByTariffTableAndBankId(tariffTableId,bankId);
    }

    public List<Tariff> getTariffByTariffTableIdValueTypeAndBankId(String tariffTableId, TariffValueType valueType, String bankId) {
        return _getDescriptor().getProxy().getTariffByTariffTableIdValueTypeAndBankId(tariffTableId,valueType,bankId);
    }

    public List<Tariff> getAllTariffs() {
        return _getDescriptor().getProxy().getAllTariffs();
    }

    public TariffTable createCommissionTable(TariffTable tariffTable, String userName) throws EWalletException_Exception {
        return _getDescriptor().getProxy().createCommissionTable(tariffTable,userName);
    }

    public String deleteTariffTable(String tariffTableId, String userName) throws EWalletException_Exception {
        return _getDescriptor().getProxy().deleteTariffTable(tariffTableId,userName);
    }

    public TariffTable findTariffTableById(String tariffTableId) {
        return _getDescriptor().getProxy().findTariffTableById(tariffTableId);
    }

    public List<TariffTable> getTariffTableByTransactionTypeAndBankId(TransactionType transactionType, String bankId) {
        return _getDescriptor().getProxy().getTariffTableByTransactionTypeAndBankId(transactionType,bankId);
    }

    public List<TariffTable> getTariffTableByTariffTypeAndBankId(TariffType tariffType, String bankId) {
        return _getDescriptor().getProxy().getTariffTableByTariffTypeAndBankId(tariffType,bankId);
    }

    public List<TariffTable> getTariffTableByTariffTypeTransactionAgentTypeAndBankId(TariffType tariffType, TransactionType transactionType, AgentType agentType, String bankId) {
        return _getDescriptor().getProxy().getTariffTableByTariffTypeTransactionAgentTypeAndBankId(tariffType,transactionType,agentType,bankId);
    }

    public TariffTable getTariffTableByTariffTypeEffectiveDateAndBankId(TariffType tariffType, XMLGregorianCalendar effectiveDate, AgentType agentType, String bankId) {
        return _getDescriptor().getProxy().getTariffTableByTariffTypeEffectiveDateAndBankId(tariffType,effectiveDate,agentType,bankId);
    }

    public List<TariffTable> getTariffTableByTransactionTypeEffectiveDateAgentTypeAndBankId(TransactionType transactionType, XMLGregorianCalendar effectiveDate, AgentType agentType, String bankId) {
        return _getDescriptor().getProxy().getTariffTableByTransactionTypeEffectiveDateAgentTypeAndBankId(transactionType,effectiveDate,agentType,bankId);
    }

    public TariffTable getTariffTableByTariffTypeTransactionTypeEffectiveDateAndBankId(TariffType tariffType, TransactionType transactionType, XMLGregorianCalendar effectiveDate, AgentType agentType, String bankid) {
        return _getDescriptor().getProxy().getTariffTableByTariffTypeTransactionTypeEffectiveDateAndBankId(tariffType,transactionType,effectiveDate,agentType,bankid);
    }

    public TariffTable getTariffTableByTariffTypeTransactionTypeEffectiveDateEndDateAndBankId(TariffType tariffType, TransactionType transactionType, XMLGregorianCalendar effectiveDate, XMLGregorianCalendar endDate, AgentType agentType, String bankId) {
        return _getDescriptor().getProxy().getTariffTableByTariffTypeTransactionTypeEffectiveDateEndDateAndBankId(tariffType,transactionType,effectiveDate,endDate,agentType,bankId);
    }

    public List<TariffTable> getAllTariffTables() {
        return _getDescriptor().getProxy().getAllTariffTables();
    }

    public List<TariffTable> getTariffTableByDateRangeAndBankId(XMLGregorianCalendar fromDate, XMLGregorianCalendar toDate, String bankId) {
        return _getDescriptor().getProxy().getTariffTableByDateRangeAndBankId(fromDate,toDate,bankId);
    }

    public List<TariffTable> getEffectiveTariffTablesForBank(String bankId) {
        return _getDescriptor().getProxy().getEffectiveTariffTablesForBank(bankId);
    }

    public long calculateTariffCharge(CustomerClass customerClass, TransactionType txnType, AgentType agentType, long amount, String bankId) throws EWalletException_Exception {
        return _getDescriptor().getProxy().calculateTariffCharge(customerClass,txnType,agentType,amount,bankId);
    }

    public Tariff retrieveAppropriateTariff(CustomerClass customerClass, TransactionType txnType, AgentType agentType, long amount, String bankId) throws EWalletException_Exception {
        return _getDescriptor().getProxy().retrieveAppropriateTariff(customerClass,txnType,agentType,amount,bankId);
    }

    public List<Tariff> getAllTariffsByBankId(String bankId) {
        return _getDescriptor().getProxy().getAllTariffsByBankId(bankId);
    }

    public List<TariffTable> getAllTariffTablesByBankId(String bankId) {
        return _getDescriptor().getProxy().getAllTariffTablesByBankId(bankId);
    }

    public List<Tariff> getTariffsByTariffTableId(String tableId) {
        return _getDescriptor().getProxy().getTariffsByTariffTableId(tableId);
    }

    public List<TariffTable> getTariffTableByCustomerClassAndBankId(CustomerClass customerClass, String bankId) {
        return _getDescriptor().getProxy().getTariffTableByCustomerClassAndBankId(customerClass,bankId);
    }

    public List<TariffTable> getTariffTableByTariffStatusAndBankId(TariffStatus status, String bankId) {
        return _getDescriptor().getProxy().getTariffTableByTariffStatusAndBankId(status,bankId);
    }

    public TariffTable editCommissionTable(TariffTable tariffTable, String userName) throws EWalletException_Exception {
        return _getDescriptor().getProxy().editCommissionTable(tariffTable,userName);
    }

    public TariffTable approveCommission(TariffTable tariffTable, String userName) throws EWalletException_Exception {
        return _getDescriptor().getProxy().approveCommission(tariffTable,userName);
    }

    public TariffTable disapproveCommission(TariffTable tariffTable, String userName) throws EWalletException_Exception {
        return _getDescriptor().getProxy().disapproveCommission(tariffTable,userName);
    }

}