package zw.co.esolutions.ewallet.referralservices.service;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;

public class ReferralServiceSOAPProxy{

    protected Descriptor _descriptor;

    public class Descriptor {
        private zw.co.esolutions.ewallet.referralservices.service.ReferralService_Service _service = null;
        private zw.co.esolutions.ewallet.referralservices.service.ReferralService _proxy = null;
        private Dispatch<Source> _dispatch = null;

        public Descriptor() {
            _service = new zw.co.esolutions.ewallet.referralservices.service.ReferralService_Service();
            initCommon();
        }

        public Descriptor(URL wsdlLocation, QName serviceName) {
            _service = new zw.co.esolutions.ewallet.referralservices.service.ReferralService_Service(wsdlLocation, serviceName);
            initCommon();
        }

        private void initCommon() {
            _proxy = _service.getReferralServiceSOAP();
        }

        public zw.co.esolutions.ewallet.referralservices.service.ReferralService getProxy() {
            return _proxy;
        }

        public Dispatch<Source> getDispatch() {
            if(_dispatch == null ) {
                QName portQName = new QName("http://service.referralservices.ewallet.esolutions.co.zw/", "ReferralServiceSOAP");
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

    public ReferralServiceSOAPProxy() {
        _descriptor = new Descriptor();
    }

    public ReferralServiceSOAPProxy(URL wsdlLocation, QName serviceName) {
        _descriptor = new Descriptor(wsdlLocation, serviceName);
    }

    public Descriptor _getDescriptor() {
        return _descriptor;
    }

    public Referral createReferral(Referral arg0) throws Exception_Exception {
        return _getDescriptor().getProxy().createReferral(arg0);
    }

    public String deleteReferral(Referral arg0, String arg1) throws Exception_Exception {
        return _getDescriptor().getProxy().deleteReferral(arg0,arg1);
    }

    public Referral updateReferral(Referral arg0, String arg1) throws Exception_Exception {
        return _getDescriptor().getProxy().updateReferral(arg0,arg1);
    }

    public Referral findReferralById(String arg0) {
        return _getDescriptor().getProxy().findReferralById(arg0);
    }

    public List<Referral> getReferral() {
        return _getDescriptor().getProxy().getReferral();
    }

    public List<Referral> getReferralByReferrerMobileId(String arg0) {
        return _getDescriptor().getProxy().getReferralByReferrerMobileId(arg0);
    }

    public List<Referral> getReferralByReferredMobile(String arg0) {
        return _getDescriptor().getProxy().getReferralByReferredMobile(arg0);
    }

    public List<Referral> getReferralByCode(int arg0) {
        return _getDescriptor().getProxy().getReferralByCode(arg0);
    }

    public List<Referral> getReferralByStatus(ReferralStatus arg0) {
        return _getDescriptor().getProxy().getReferralByStatus(arg0);
    }

    public List<Referral> getReferralByDateCreated(XMLGregorianCalendar arg0) {
        return _getDescriptor().getProxy().getReferralByDateCreated(arg0);
    }

    public Referral getReferralByReferredMobileAndCode(String arg0, int arg1) throws Exception_Exception {
        return _getDescriptor().getProxy().getReferralByReferredMobileAndCode(arg0,arg1);
    }

    public List<Referral> getReferralByReferredMobileAndStatus(String arg0, ReferralStatus arg1) throws Exception_Exception {
        return _getDescriptor().getProxy().getReferralByReferredMobileAndStatus(arg0,arg1);
    }

    public ReferralState createReferralState(ReferralState arg0) throws Exception_Exception {
        return _getDescriptor().getProxy().createReferralState(arg0);
    }

    public String deleteReferralState(ReferralState arg0) throws Exception_Exception {
        return _getDescriptor().getProxy().deleteReferralState(arg0);
    }

    public ReferralState updateReferralState(ReferralState arg0) throws Exception_Exception {
        return _getDescriptor().getProxy().updateReferralState(arg0);
    }

    public ReferralState findReferralStateById(String arg0) {
        return _getDescriptor().getProxy().findReferralStateById(arg0);
    }

    public List<ReferralState> getReferralState() {
        return _getDescriptor().getProxy().getReferralState();
    }

    public List<ReferralState> getReferralStateByReferral(String arg0) {
        return _getDescriptor().getProxy().getReferralStateByReferral(arg0);
    }

    public List<ReferralState> getReferralStateByStatus(ReferralStatus arg0) {
        return _getDescriptor().getProxy().getReferralStateByStatus(arg0);
    }

    public List<ReferralState> getReferralStateByDateCreated(XMLGregorianCalendar arg0) {
        return _getDescriptor().getProxy().getReferralStateByDateCreated(arg0);
    }

    public String promoteReferralState(Referral arg0, ReferralStatus arg1) throws Exception_Exception {
        return _getDescriptor().getProxy().promoteReferralState(arg0,arg1);
    }

    public ReferralConfig createReferralConfig(ReferralConfig arg0, String arg1) throws Exception_Exception {
        return _getDescriptor().getProxy().createReferralConfig(arg0,arg1);
    }

    public String deleteReferralConfig(ReferralConfig arg0, String arg1) throws Exception_Exception {
        return _getDescriptor().getProxy().deleteReferralConfig(arg0,arg1);
    }

    public ReferralConfig updateReferralConfig(ReferralConfig arg0, String arg1) throws Exception_Exception {
        return _getDescriptor().getProxy().updateReferralConfig(arg0,arg1);
    }

    public ReferralConfig findReferralConfigById(String arg0) {
        return _getDescriptor().getProxy().findReferralConfigById(arg0);
    }

    public List<ReferralConfig> getReferralConfig() {
        return _getDescriptor().getProxy().getReferralConfig();
    }

    public List<ReferralConfig> getReferralConfigByDateFrom(XMLGregorianCalendar arg0) {
        return _getDescriptor().getProxy().getReferralConfigByDateFrom(arg0);
    }

    public List<ReferralConfig> getReferralConfigByDateTo(XMLGregorianCalendar arg0) {
        return _getDescriptor().getProxy().getReferralConfigByDateTo(arg0);
    }

    public ReferralConfig getActiveReferralConfig() {
        return _getDescriptor().getProxy().getActiveReferralConfig();
    }

    public List<ReferralConfig> getReferralConfigBetweenDates(XMLGregorianCalendar arg0, XMLGregorianCalendar arg1) {
        return _getDescriptor().getProxy().getReferralConfigBetweenDates(arg0,arg1);
    }

}