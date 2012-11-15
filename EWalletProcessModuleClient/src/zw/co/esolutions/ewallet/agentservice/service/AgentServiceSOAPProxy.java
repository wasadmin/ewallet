package zw.co.esolutions.ewallet.agentservice.service;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import java.util.List;

public class AgentServiceSOAPProxy{

    protected Descriptor _descriptor;

    public class Descriptor {
        private zw.co.esolutions.ewallet.agentservice.service.AgentService_Service _service = null;
        private zw.co.esolutions.ewallet.agentservice.service.AgentService _proxy = null;
        private Dispatch<Source> _dispatch = null;

        public Descriptor() {
            _service = new zw.co.esolutions.ewallet.agentservice.service.AgentService_Service();
            initCommon();
        }

        public Descriptor(URL wsdlLocation, QName serviceName) {
            _service = new zw.co.esolutions.ewallet.agentservice.service.AgentService_Service(wsdlLocation, serviceName);
            initCommon();
        }

        private void initCommon() {
            _proxy = _service.getAgentServiceSOAP();
        }

        public zw.co.esolutions.ewallet.agentservice.service.AgentService getProxy() {
            return _proxy;
        }

        public Dispatch<Source> getDispatch() {
            if(_dispatch == null ) {
                QName portQName = new QName("http://service.agentservice.ewallet.esolutions.co.zw/", "AgentServiceSOAP");
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

    public AgentServiceSOAPProxy() {
        _descriptor = new Descriptor();
    }

    public AgentServiceSOAPProxy(URL wsdlLocation, QName serviceName) {
        _descriptor = new Descriptor(wsdlLocation, serviceName);
    }

    public Descriptor _getDescriptor() {
        return _descriptor;
    }

    public Agent updateAgent(Agent agent, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().updateAgent(agent,username);
    }

    public String deleteAgent(Agent agent, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().deleteAgent(agent,username);
    }

    public Agent findAgentById(String id) {
        return _getDescriptor().getProxy().findAgentById(id);
    }

    public Agent createAgent(Agent agent, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().createAgent(agent,username);
    }

    public List<Agent> getAgentByName(String firstname, String lastName) {
        return _getDescriptor().getProxy().getAgentByName(firstname,lastName);
    }

    public Agent getAgentByAgentNumber(String agentNumber) {
        return _getDescriptor().getProxy().getAgentByAgentNumber(agentNumber);
    }

    public List<Agent> getAgentByStatus(String status) {
        return _getDescriptor().getProxy().getAgentByStatus(status);
    }

    public List<Agent> getAgentByLevel(String agentLevel) {
        return _getDescriptor().getProxy().getAgentByLevel(agentLevel);
    }

    public Agent getAgentByNationalId(String nationalId) {
        return _getDescriptor().getProxy().getAgentByNationalId(nationalId);
    }

    public List<Agent> getAllAgents() {
        return _getDescriptor().getProxy().getAllAgents();
    }

    public List<Agent> getAgentByAgentType(String agentType) {
        return _getDescriptor().getProxy().getAgentByAgentType(agentType);
    }

    public List<Agent> getAgentByBankId(String bankId) {
        return _getDescriptor().getProxy().getAgentByBankId(bankId);
    }

    public List<Agent> getAgentByLastName(String lastName) {
        return _getDescriptor().getProxy().getAgentByLastName(lastName);
    }

    public List<Agent> getSubAgentBySuperAgentId(String superAgentId) {
        return _getDescriptor().getProxy().getSubAgentBySuperAgentId(superAgentId);
    }

    public Agent getSubAgentByNationalId(String superAgentId, String nationalId) {
        return _getDescriptor().getProxy().getSubAgentByNationalId(superAgentId,nationalId);
    }

    public List<Agent> getSubAgentByStatus(String superAgentId, String status) {
        return _getDescriptor().getProxy().getSubAgentByStatus(superAgentId,status);
    }

    public Agent getSubAgentByAgentNumber(String superAgentId, String agentNumber) {
        return _getDescriptor().getProxy().getSubAgentByAgentNumber(superAgentId,agentNumber);
    }

    public Agent getAgentByMobileNumber(String mobileNumber) {
        return _getDescriptor().getProxy().getAgentByMobileNumber(mobileNumber);
    }

    public Agent getAgentByProfileId(String profileId) {
        return _getDescriptor().getProxy().getAgentByProfileId(profileId);
    }

    public Agent approveAgent(Agent agent, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().approveAgent(agent,username);
    }

    public Agent rejectAgent(Agent agent, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().rejectAgent(agent,username);
    }

    public List<Agent> getAllSubAgents(String superAgentId) {
        return _getDescriptor().getProxy().getAllSubAgents(superAgentId);
    }

    public Agent deregisterAgent(String agentId, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().deregisterAgent(agentId,username);
    }

    public Agent approveDeregistration(String agentId, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().approveDeregistration(agentId,username);
    }

    public Agent cancelDeregistration(String agentId, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().cancelDeregistration(agentId,username);
    }

    public AgentClass createAgentClass(AgentClass agentClass, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().createAgentClass(agentClass,username);
    }

    public AgentClass updateAgentClass(AgentClass agentClass, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().updateAgentClass(agentClass,username);
    }

    public AgentClass deleteAgentClass(String agentClassId, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().deleteAgentClass(agentClassId,username);
    }

    public AgentClass findAgentClassById(String agentClassId) {
        return _getDescriptor().getProxy().findAgentClassById(agentClassId);
    }

    public List<AgentClass> getAllAgentClasses() {
        return _getDescriptor().getProxy().getAllAgentClasses();
    }

    public List<Agent> getAgentsByAgentClass(String agentClassId) {
        return _getDescriptor().getProxy().getAgentsByAgentClass(agentClassId);
    }

    public AgentClass getAgentClassByName(String className) {
        return _getDescriptor().getProxy().getAgentClassByName(className);
    }

    public List<AgentClass> getAgentClassByStatus(String status) {
        return _getDescriptor().getProxy().getAgentClassByStatus(status);
    }

    public AgentClass approveAgentClass(AgentClass agentClass, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().approveAgentClass(agentClass,username);
    }

    public AgentClass rejectAgentClass(AgentClass agentClass, String username) throws Exception_Exception {
        return _getDescriptor().getProxy().rejectAgentClass(agentClass,username);
    }

    public Agent getAgentByCustomerId(String customerId) throws Exception_Exception {
        return _getDescriptor().getProxy().getAgentByCustomerId(customerId);
    }

    public String generateAgentNumber() throws Exception_Exception {
        return _getDescriptor().getProxy().generateAgentNumber();
    }

}