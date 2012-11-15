package zw.co.esolutions.ewallet.profileservices.service;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;

public class ProfileServiceSOAPProxy{

    protected Descriptor _descriptor;

    public class Descriptor {
        private zw.co.esolutions.ewallet.profileservices.service.ProfileService_Service _service = null;
        private zw.co.esolutions.ewallet.profileservices.service.ProfileService _proxy = null;
        private Dispatch<Source> _dispatch = null;

        public Descriptor() {
            init();
        }

        public Descriptor(URL wsdlLocation, QName serviceName) {
            _service = new zw.co.esolutions.ewallet.profileservices.service.ProfileService_Service(wsdlLocation, serviceName);
            initCommon();
        }

        public void init() {
            _service = null;
            _proxy = null;
            _dispatch = null;
            _service = new zw.co.esolutions.ewallet.profileservices.service.ProfileService_Service();
            initCommon();
        }

        private void initCommon() {
            _proxy = _service.getProfileServiceSOAP();
        }

        public zw.co.esolutions.ewallet.profileservices.service.ProfileService getProxy() {
            return _proxy;
        }

        public Dispatch<Source> getDispatch() {
            if (_dispatch == null ) {
                QName portQName = new QName("http://service.profileservices.ewallet.esolutions.co.zw/", "ProfileServiceSOAP");
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

    public ProfileServiceSOAPProxy() {
        _descriptor = new Descriptor();
        _descriptor.setMTOMEnabled(true);
    }

    public ProfileServiceSOAPProxy(URL wsdlLocation, QName serviceName) {
        _descriptor = new Descriptor(wsdlLocation, serviceName);
        _descriptor.setMTOMEnabled(true);
    }

    public Descriptor _getDescriptor() {
        return _descriptor;
    }

    public AccessRight getAccessRightByActionName(String arg0) {
        return _getDescriptor().getProxy().getAccessRightByActionName(arg0);
    }

    public List<AccessRight> getAccessRight() {
        return _getDescriptor().getProxy().getAccessRight();
    }

    public List<Profile> getProfileByLastName(String arg0) {
        return _getDescriptor().getProxy().getProfileByLastName(arg0);
    }

    public List<Profile> getProfileByStatus(ProfileStatus arg0) throws Exception_Exception {
        return _getDescriptor().getProxy().getProfileByStatus(arg0);
    }

    public AccessRight createAccessRight(AccessRight accessRight, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().createAccessRight(accessRight,userName);
    }

    public String deleteAccessRight(AccessRight accessRight, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().deleteAccessRight(accessRight,userName);
    }

    public AccessRight editAccessRight(AccessRight accessRight, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().editAccessRight(accessRight,userName);
    }

    public AccessRight findAccessRightById(String id) {
        return _getDescriptor().getProxy().findAccessRightById(id);
    }

    public RoleAccessRight updateRoleAccessRight(RoleAccessRight roleAccessRight, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().updateRoleAccessRight(roleAccessRight,userName);
    }

    public RoleAccessRight findRoleAccessRightById(String id) {
        return _getDescriptor().getProxy().findRoleAccessRightById(id);
    }

    public RoleAccessRight getRoleAccessRightByRoleActionNameAndStatus(String roleId, String actionName, AccessRightStatus status) {
        return _getDescriptor().getProxy().getRoleAccessRightByRoleActionNameAndStatus(roleId,actionName,status);
    }

    public List<RoleAccessRight> getRoleAccessRightByRole(String roleId) {
        return _getDescriptor().getProxy().getRoleAccessRightByRole(roleId);
    }

    public Profile createProfile(Profile profile, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().createProfile(profile,userName);
    }

    public String deleteProfile(Profile profile, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().deleteProfile(profile,userName);
    }

    public Profile editProfile(Profile profile, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().editProfile(profile,userName);
    }

    public Profile approveProfile(Profile profile, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().approveProfile(profile,userName);
    }

    public Profile rejectProfile(Profile profile, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().rejectProfile(profile,userName);
    }

    public String changeProfilePassword(Profile profile, String oldPassword, String newPassword, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().changeProfilePassword(profile,oldPassword,newPassword,userName);
    }

    public String resetProfilePassword(Profile profile, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().resetProfilePassword(profile,userName);
    }

    public Profile findProfileById(String id) {
        return _getDescriptor().getProxy().findProfileById(id);
    }

    public Profile getProfileByUserName(String userName) {
        return _getDescriptor().getProxy().getProfileByUserName(userName);
    }

    public Profile getProfileByUserNameAndStatus(String userName, ProfileStatus status) {
        return _getDescriptor().getProxy().getProfileByUserNameAndStatus(userName,status);
    }

    public List<Profile> getProfileByUserRole(String roleName) {
        return _getDescriptor().getProxy().getProfileByUserRole(roleName);
    }

    public List<Profile> getAllProfiles() {
        return _getDescriptor().getProxy().getAllProfiles();
    }

    public List<Profile> getProfileByBranchId(String branchId) {
        return _getDescriptor().getProxy().getProfileByBranchId(branchId);
    }

    public List<Profile> getProfileByBranchIdAndAccessRight(String branchId, String accessRightName) {
        return _getDescriptor().getProxy().getProfileByBranchIdAndAccessRight(branchId,accessRightName);
    }

    public Role createRole(Role role, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().createRole(role,userName);
    }

    public Role editRole(Role role, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().editRole(role,userName);
    }

    public Role approveRole(Role role, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().approveRole(role,userName);
    }

    public Role rejectRole(Role role, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().rejectRole(role,userName);
    }

    public Role deleteRole(Role role, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().deleteRole(role,userName);
    }

    public Role findRoleByRoleId(String roleId) {
        return _getDescriptor().getProxy().findRoleByRoleId(roleId);
    }

    public Role getRoleByRoleName(String roleName) {
        return _getDescriptor().getProxy().getRoleByRoleName(roleName);
    }

    public List<Role> getRoleByStatus(RoleStatus roleStatus) {
        return _getDescriptor().getProxy().getRoleByStatus(roleStatus);
    }

    public List<Role> getActiveRoles() {
        return _getDescriptor().getProxy().getActiveRoles();
    }

    public boolean canDo(String username, String accessRightName) {
        return _getDescriptor().getProxy().canDo(username,accessRightName);
    }

    public String authenticateUser(String username, String password) {
        return _getDescriptor().getProxy().authenticateUser(username,password);
    }

    public Profile updateProfile(Profile profile, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().updateProfile(profile,userName);
    }

    public List<Profile> getAllLoggedOnUsers() {
        return _getDescriptor().getProxy().getAllLoggedOnUsers();
    }

    public Profile getProfileByIP(String ip) {
        return _getDescriptor().getProxy().getProfileByIP(ip);
    }

    public Profile resetProfileIP(Profile profile, String userName) {
        return _getDescriptor().getProxy().resetProfileIP(profile,userName);
    }

    public boolean validateHost(Profile profile, String ip) {
        return _getDescriptor().getProxy().validateHost(profile,ip);
    }

    public Bulletin createBulletin(Bulletin bulletin, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().createBulletin(bulletin,userName);
    }

    public Bulletin findBulletinById(String id) {
        return _getDescriptor().getProxy().findBulletinById(id);
    }

    public Bulletin approveBulletin(Bulletin bulletin, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().approveBulletin(bulletin,userName);
    }

    public Bulletin disapproveBulletin(Bulletin bulletin, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().disapproveBulletin(bulletin,userName);
    }

    public Bulletin deactivateBulletin(Bulletin bulletin, String userName) throws Exception_Exception {
        return _getDescriptor().getProxy().deactivateBulletin(bulletin,userName);
    }

    public List<Bulletin> getBulletinByStatus(String status) {
        return _getDescriptor().getProxy().getBulletinByStatus(status);
    }

    public Bulletin getCurrentBulletin() {
        return _getDescriptor().getProxy().getCurrentBulletin();
    }

    public List<Bulletin> getBulletinByApprover(String approverId) {
        return _getDescriptor().getProxy().getBulletinByApprover(approverId);
    }

    public List<Bulletin> getBulletinByInitiator(String initiatorId) {
        return _getDescriptor().getProxy().getBulletinByInitiator(initiatorId);
    }

    public List<Bulletin> getBulletinByAllFields(String approverId, String initiatorId, String status, XMLGregorianCalendar dateCreated, XMLGregorianCalendar expirationDate) {
        return _getDescriptor().getProxy().getBulletinByAllFields(approverId,initiatorId,status,dateCreated,expirationDate);
    }

}