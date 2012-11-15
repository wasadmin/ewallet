package zw.co.esolutions.ewallet.profileservices.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.ewallet.audit.AuditEvents;
import zw.co.esolutions.ewallet.audittrailservices.service.AuditTrailServiceSOAPProxy;
import zw.co.esolutions.ewallet.enums.AccessRightStatus;
import zw.co.esolutions.ewallet.enums.BulletinStatus;
import zw.co.esolutions.ewallet.enums.ProfileStatus;
import zw.co.esolutions.ewallet.enums.RoleStatus;
import zw.co.esolutions.ewallet.profileservices.model.AccessRight;
import zw.co.esolutions.ewallet.profileservices.model.Bulletin;
import zw.co.esolutions.ewallet.profileservices.model.Profile;
import zw.co.esolutions.ewallet.profileservices.model.Role;
import zw.co.esolutions.ewallet.profileservices.model.RoleAccessRight;
import zw.co.esolutions.ewallet.profileservices.util.LdapUtil;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.GenerateKey;
import zw.co.esolutions.ewallet.util.PasswordUtil;
import zw.co.esolutions.ewallet.util.SystemConstants;


/**
 * Session Bean implementation class ProfileServiceImpl
 */
@Stateless
@WebService(endpointInterface="zw.co.esolutions.ewallet.profileservices.service.ProfileService", serviceName="ProfileService", portName="ProfileServiceSOAP")
public class ProfileServiceImpl implements ProfileService {
	@PersistenceContext private EntityManager em;
    /**
     * Default constructor. 
     */
    public ProfileServiceImpl() {
        
    }
    
    

	private static Logger LOG ;
	
	static{
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(ProfileServiceImpl.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + ProfileServiceImpl.class);
		}
	}
	
    
    
    public AccessRight createAccessRight(AccessRight accessRight, String userName) throws Exception {
		
    	if(accessRight.getId() == null) {
    		accessRight.setId(GenerateKey.generateEntityId());
    	}
    	if(accessRight.getDateCreated() == null) {
    		accessRight.setDateCreated(new Date());
    	}
    	accessRight.setFieldsToUpperCase();
    	LOG.debug("Access Right : " + accessRight.getActionName());
		try {
			AccessRight a = this.getAccessRightByActionName(accessRight.getActionName());
			if (a != null) {
				throw new EntityExistsException();
			}
			em.persist(accessRight);
		} catch(Exception e) {
			e.printStackTrace();
		}
		accessRight = this.refreshRoles(accessRight);
		return accessRight;
	}
    
    private AccessRight refreshRoles(AccessRight accessRight) throws Exception{
    	
    	try {
			List<Role> roleList = this.getAllRoles();
			if(roleList!=null){
				for(Role role:roleList){
					RoleAccessRight rar = new RoleAccessRight();
					rar.setId(GenerateKey.generateEntityId());
					rar.setAccessRight(accessRight);
					rar.setRole(role);
					rar.setCanDo(false);
					em.persist(rar);
				}
			}
			accessRight= em.merge(accessRight);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error occured during refreshing role access rights");
		}
    	return accessRight;
    }
    
	public String deleteAccessRight(AccessRight accessRight, String userName) throws Exception {		
		try {
			accessRight.setStatus(AccessRightStatus.DELETED);
			em.merge(accessRight);
		} finally {
			
		}
		return "";
	}
           
    
	public AccessRight editAccessRight(AccessRight accessRight, String userName)
			throws Exception {
		return this.updateAccessRight(accessRight);
	}

	private AccessRight updateAccessRight(AccessRight accessRight) throws Exception {
		accessRight.setFieldsToUpperCase(); 
		try {
			accessRight = em.merge(accessRight);
		} finally {

		}
		return accessRight;
	}

	public AccessRight findAccessRightById(String id) {
		AccessRight accessRight = null;
		try {
			accessRight = (AccessRight) em.find(AccessRight.class, id);
		} finally {

		}
		return accessRight;
	}
	
	public AccessRight getAccessRightByActionName(String actionName) {
		AccessRight accessRight = null;
		try {
			Query query = em.createNamedQuery("getAccessRightByActionName");
			query.setParameter("actionName", "%" + actionName.toUpperCase() + "%");
			accessRight = (AccessRight) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			
		}
		return accessRight;
	}

	@SuppressWarnings("unchecked")
	public List<AccessRight> getAccessRight() {
		List<AccessRight> results = null;
		try {
			Query query = em.createNamedQuery("getAccessRight");
			results = (List<AccessRight>) query.getResultList();
		} catch(NoResultException e) {
			return null;
		} catch(Exception ex) {
			
		}
		return results;
	}
	
	@SuppressWarnings("unchecked")
	public List<Profile> getProfileByLastName(String lastName) {
		List<Profile> results = null;
		try {
			Query query = em.createNamedQuery("getProfileByLastName");
			query.setParameter("lastName", "%" + lastName.toUpperCase() + "%");
			query.setParameter("status", ProfileStatus.DELETED);			
			results = (List<Profile>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			
		}
		return results;
	}
	
	@SuppressWarnings("unchecked")
	public List<Profile> getProfileByStatus(ProfileStatus status) throws Exception {
		List<Profile> results = null;
		try {
			Query query = em.createNamedQuery("getProfileByStatus");
			query.setParameter("status", status);
			results = (List<Profile>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}
	
		
	
	public Profile createProfile(Profile profile, String userName) throws Exception {
	LOG.debug(">>>>>>>>>>>>creating profile>>>>>>>>>"+profile.getId());
		if(profile.getId() == null) {
			profile.setId(GenerateKey.generateEntityId());
		}
		LOG.debug(" adding creation date");
		if(profile.getDateCreated() == null) {
			profile.setDateCreated(new Date());
		}
				
		if(profile.getStatus() == null) {
			profile.setStatus(ProfileStatus.AWAITING_APPROVAL);
		}
		LOG.debug(" adding status");
		profile.setFieldsToUpperCase();
		profile.setChangePassword(true);
		LOG.debug(" retrieving profile from database");
		Profile p = this.getProfileByUserName(profile.getUserName());
		LOG.debug(" done retriving profilem ::::::::::::"+p);
		if (p != null) {
			throw new EntityExistsException("ERROR: Profile with username " + profile.getUserName() + " already exists.");
		}
		
				
		try {
			//TODO : Add code to add profile to ldap directory
			LOG.debug("persisiting profile");
			em.persist(profile);
			LOG.debug(" done persiting profile");
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.CREATE_PROFILE, profile.getId(), profile.getEntityName(), null, profile.getAuditableAttributesString(), profile.getInstanceName());
			LOG.debug(" done with audittrail and returning");
		} catch(Exception e){
			LOG.debug(">>>>>>>>>>>>>>>>>> error in creation >>>>>>>>>>>>>"+e.getMessage());
			e.printStackTrace();
			throw new Exception("Error occured during creation of profile");
		}
		return profile;
	}
	
	
	public String deleteProfile(Profile profile, String userName) throws Exception {
		
		try {
			String oldProfile = this.findProfileById(profile.getId()).getAuditableAttributesString();
			profile.setStatus(ProfileStatus.DELETED);
			em.merge(profile);
			
			//LDAP
			LdapUtil.resetUserPassword(profile.getUserName(), PasswordUtil.getPassword(8));
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.DELETE_PROFILE, profile.getId(), profile.getEntityName(), oldProfile, profile.getAuditableAttributesString(), profile.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error occured during deletion of profile");
		}
		return "";
	}
	
	
	public Profile approveProfile(Profile profile, String userName)
			throws Exception {
		try {
			String oldProfile = this.findProfileById(profile.getId()).getAuditableAttributesString();
			String password ="";
			
			
				try {
					//Generate Profile Password
					password = PasswordUtil.getPassword(8);
					profile.setUserPassword(password);
					//Add user to directory
					LdapUtil.creatLDAPEntry(profile);
				} catch (Exception e) {
					LOG.debug("User Already In Directory");
				}
								
			
			profile.setStatus(ProfileStatus.ACTIVE);
			profile.setUserPassword("");
			profile = this.updateProfile(profile);
			profile.setUserPassword(password);
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.APPROVE_PROFILE, profile.getId(), profile.getEntityName(), oldProfile, profile.getAuditableAttributesString(), profile.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return profile;
	}

	public Profile rejectProfile(Profile profile, String userName)
			throws Exception {
		try {
			String oldProfile = this.findProfileById(profile.getId()).getAuditableAttributesString();
			profile.setStatus(ProfileStatus.DISAPPROVED);
			profile = this.updateProfile(profile);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.REJECT_PROFILE, profile.getId(), profile.getEntityName(), oldProfile, profile.getAuditableAttributesString(), profile.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error occured during rejection of profile");
		}
		return profile;
	}
	
	
	public String changeProfilePassword(Profile profile,String olPassword,String newPassword, String userName)
			throws Exception {
		String result = null;
		try {
			
			/*
			 * 
			 */
			String authResult=checkUserPassword(userName, olPassword);
			LOG.debug("....>>>>>>>>>>>>>>>>>>>>>>>>>authResult>>>>>>>>>>>>>>"+authResult);
			if(SystemConstants.AUTH_STATUS_AUTHENTICATED.equalsIgnoreCase(authResult)){
				LOG.debug("in if");
			String oldProfile = this.findProfileById(profile.getId()).getAuditableAttributesString();
			//profile = this.updateProfile(profile);
			result = LdapUtil.changeUserPassword(profile.getUserName(), olPassword, newPassword);
			if(result.equals(SystemConstants.CHANGE_PASSWORD_SUCCESS)){
				profile.setChangePassword(false);
				this.updateProfile(profile);
			}
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.CHANGE_PROFILE_PASSWORD, profile.getId(), profile.getEntityName(), oldProfile, profile.getAuditableAttributesString(), profile.getInstanceName());
			}else{
				LOG.debug("In else");
				/*
				 * one of many things might have happened\
				 * 1. SystemConstants.AUTH_STATUS_NETWORK_PROBLEM
				 */
				
				if(SystemConstants.AUTH_STATUS_INVALID_CREDENTIALS.equalsIgnoreCase(authResult)){
					return SystemConstants.INVALID_OLD_PASSWORD;
				}
				return authResult;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error occured during changing of profile");
		}
		return result;
	}
	
	
	public String resetProfilePassword(Profile profile, String userName)
			throws Exception {	
		String result = null;
		try {
			String oldProfile = this.findProfileById(profile.getId()).getAuditableAttributesString();
			result = LdapUtil.resetUserPassword(profile.getUserName(), profile.getUserPassword());
			
			
			if(result.equals(SystemConstants.RESET_PASSWORD_SUCCESS)){
				profile.setChangePassword(true);
				this.updateProfile(profile);
			}
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.RESET_PROFILE_PASSWORD, profile.getId(), profile.getEntityName(), oldProfile, profile.getAuditableAttributesString(), profile.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error occured during reset of profile password");
		}
		return result;
	}
	
	
	
	public Profile editProfile(Profile profile, String userName)
			throws Exception {
		try {
			String oldProfile = this.findProfileById(profile.getId()).getAuditableAttributesString();
			profile.setStatus(ProfileStatus.AWAITING_EDIT_APPROVAL);
			profile = this.updateProfile(profile);
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.EDIT_PROFILE, profile.getId(), profile.getEntityName(), oldProfile, profile.getAuditableAttributesString(), profile.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error occured during modification of profile");
		}
		return profile;
	}

		
	private Profile updateProfile(Profile profile) throws Exception {
		profile.setFieldsToUpperCase();
		try {
			profile = em.merge(profile);
		}catch (Exception e) {
			throw e;
		}
		return profile;
	}

	public Profile findProfileById(String id) {
		Profile profile = null;
		try {
			profile = (Profile) em.find(Profile.class, id);
		} finally {
	
		}
		return profile;
	}

	@SuppressWarnings("unchecked")
	public List<Profile> getProfileByUserRole(String roleName) {
		List<Profile> profiles = null;
		try {
			Query query = em.createNamedQuery("getProfileByUserRole");
			query.setParameter("roleName","%"+roleName.toUpperCase()+"%");
			query.setParameter("status", ProfileStatus.DELETED);			
			profiles = (List<Profile>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			
		}
		return profiles;
		
	}

	public Profile getProfileByUserName(String userName) {
		Profile profile = null;
		
		if (userName == null) {
			return profile;
		}
		
		try {
			//LOG.debug(">>>>>>>>>>>>>>>"+userName);
			Query query = em.createNamedQuery("getProfileByUserName");
			query.setParameter("userName", userName.toUpperCase());
			query.setParameter("status", ProfileStatus.DELETED);
			profile = (Profile) query.getSingleResult();
		} catch (NoResultException e) {
			//e.printStackTrace();
			LOG.debug("no result exception");
		} catch (Exception e) {
			e.printStackTrace();
		}
		//LOG.debug("returning>>>>>>>>>>>>>>>>>>>>>.."+profile);
		return profile;
		
	}
	
	public Profile getProfileByUserNameAndStatus(String userName,ProfileStatus status) {
		Profile profile = null;
		try {
			Query query = em.createNamedQuery("getProfileByUserNameAndStatus");
			query.setParameter("userName", userName.toUpperCase());
			query.setParameter("status", status);
			profile = (Profile) query.getSingleResult();
		} catch (NoResultException e) {
			System.err.println("getProfileByUserNameAndStatus returned no result." );
		} catch (Exception e) {
			e.printStackTrace();
		}
		return profile;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Profile> getAllProfiles() {
		List<Profile> profiles = null;
		try {
			Query query = em.createNamedQuery("getAllProfiles");
			query.setParameter("status", ProfileStatus.DELETED);			
			profiles = (List<Profile>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			
		}
		return profiles;
		
	}

	@SuppressWarnings("unchecked")
	public List<Profile> getProfileByBranchId(String branchId) {
		List<Profile> profiles = null;
		try {
			Query query = em.createNamedQuery("getProfileByBranchId");
			query.setParameter("branchId", branchId);
			query.setParameter("status", ProfileStatus.DELETED);
			profiles = (List<Profile>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			
		}
		return profiles;
		
	}


	@Override
	public Role approveRole(Role role, String userName) throws Exception{
		try {
			String oldRole = this.findRoleByRoleId(role.getId()).getAuditableAttributesString();
			role.setStatus(RoleStatus.ACTIVE);
			role = em.merge(role);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.APPROVE_ROLE, role.getId(), role.getEntityName(), oldRole, role.getAuditableAttributesString(), role.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("An error occured during approval of role");
		}
		return role;
	}


	@Override
	public Role createRole(Role role, String userName) throws Exception{
		role.setDateCreated(new Date());
		role.setRoleName(role.getRoleName().toUpperCase());
		role.setStatus(RoleStatus.AWAITING_APPROVAL);
		if(role.getId()==null){
			role.setId(GenerateKey.generateEntityId());			
		}
		//Make sure the role is unique
		Role r = this.getRoleByRoleName(role.getRoleName());
		if(r!=null){
			throw new EntityExistsException();		
		}
		
		try {
			
			em.persist(role);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error occured during creation of role");
		}
		//Create role access rights
		role = this.createRoleAccessRights(role);
		return role;
	}
	
	private Role createRoleAccessRights(Role role) throws Exception{
		
		try {
			List<AccessRight> accessRightsList = this.getAccessRight();
			if(accessRightsList!=null){
				for(AccessRight ar:accessRightsList){
					RoleAccessRight rar = new RoleAccessRight();
					rar.setId(GenerateKey.generateEntityId());
					rar.setAccessRight(ar);
					rar.setRole(role);
					rar.setCanDo(false);
					em.persist(rar);
				}
			}
			role = em.merge(role);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error occured during creation of role access rights");
		}
		
		return role;
	}

	public RoleAccessRight updateRoleAccessRight(RoleAccessRight roleAccessRight,String username) throws Exception{
		try {
			roleAccessRight = em.merge(roleAccessRight);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return roleAccessRight;
	}

	@Override
	public Role deleteRole(Role role, String userName) throws Exception{
		if(role.getProfiles()!=null || !role.getProfiles().isEmpty()){
			throw new Exception("Role cannot be deleted because it has profiles");
		}
		try {
			String oldRole = this.findRoleByRoleId(role.getId()).getAuditableAttributesString();
			role.setStatus(RoleStatus.DELETED);
			role = em.merge(role);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.DELETE_ROLE, role.getId(), role.getEntityName(), oldRole, role.getAuditableAttributesString(), role.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("An error occured during deletion of role");
		}
		return role;
	}


	@Override
	public Role editRole(Role role, String userName) throws Exception{
		try {
			String oldRole = this.findRoleByRoleId(role.getId()).getAuditableAttributesString();
			role = em.merge(role);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.EDIT_ROLE, role.getId(), role.getEntityName(), oldRole, role.getAuditableAttributesString(), role.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("An error occured during modification of role");
		}
		return role;
	}


	@Override
	public Role findRoleByRoleId(String roleId) {
		Role role = null;
		try{
			role = em.find(Role.class, roleId);
		}catch(Exception e){
			e.printStackTrace();
		}
		return role;
	}


	@Override
	@SuppressWarnings("unchecked")
	public List<Role> getActiveRoles(){
		List<Role> roleList = null;
		try {
			Query query = em.createNamedQuery("getActiveRoles");
			query.setParameter("status", RoleStatus.ACTIVE);
			roleList = (List<Role>)query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return roleList;
	}


	@Override
	public Role getRoleByRoleName(String roleName) {
		Role role = null;
		try{
			Query query = em.createNamedQuery("getRoleByRoleName");
			query.setParameter("roleName", roleName);
			role = (Role)query.getSingleResult();
		}catch (NoResultException e) {
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return role;
	}


	@Override
	@SuppressWarnings("unchecked")
	public List<Role> getRoleByStatus(RoleStatus status) {
		List<Role> roleList = null;
		try {
			Query query = em.createNamedQuery("getRoleByStatus");
			query.setParameter("status", status);
			roleList = (List<Role>)query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return roleList;
	}

	@SuppressWarnings("unchecked")
	private List<Role> getAllRoles(){
		List<Role> roleList = null;
		try {
			Query query = em.createNamedQuery("getAllRoles");
			roleList = (List<Role>)query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return roleList;
	}

	@Override
	public Role rejectRole(Role role, String userName) throws Exception{
		
		try {
			String oldRole = this.findRoleByRoleId(role.getId()).getAuditableAttributesString();
			role.setStatus(RoleStatus.DISAPPROVED);
			role = em.merge(role);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.REJECT_ROLE, role.getId(), role.getEntityName(), oldRole, role.getAuditableAttributesString(), role.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("An error occured during rejection of role");
		}
		return role;
	}
	
	@SuppressWarnings("unchecked")
	public List<RoleAccessRight> getRoleAccessRightByRole(String roleId){
		List<RoleAccessRight> rarList = null;
		try{
			Query query = em.createNamedQuery("getRoleAccessRightByRole");
			query.setParameter("roleId", roleId);
			rarList = (List<RoleAccessRight>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rarList;
	}
	
	public RoleAccessRight getRoleAccessRightByRoleActionNameAndStatus(String roleId,String actionName,AccessRightStatus status){
		RoleAccessRight rar = null;
		try{
			Query query = em.createNamedQuery("getRoleAccessRightByRoleActionNameAndStatus");
			query.setParameter("roleId", roleId);
			query.setParameter("actionName", actionName);
			query.setParameter("status", status);
			query.setParameter("canDo", true);
			rar = (RoleAccessRight)query.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rar;
	}
	
	public RoleAccessRight findRoleAccessRightById(String id){
		RoleAccessRight rar = null;
		try {
			rar = em.find(RoleAccessRight.class, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rar;
	}
	
	private RoleAccessRight getRoleAccessRightByRoleAndActionName(String roleId,String actionName){
		RoleAccessRight rar = null;
		try{
			Query query = em.createNamedQuery("getRoleAccessRightByRoleAndActionName");
			query.setParameter("roleId", roleId);
			query.setParameter("actionName", actionName);
			rar = (RoleAccessRight)query.getSingleResult();
		}catch(NoResultException nre){
			//LOG.debug(nre.getMessage());
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			
		}
		return rar;
	}


	@Override
	public boolean canDo(String userName, String accessRightName) {
		Profile profile = this.getProfileByUserName(userName);
		if(profile==null){
			return false;
		}
		RoleAccessRight rar = this.getRoleAccessRightByRoleAndActionName(profile.getRole().getId(), accessRightName.toUpperCase());
		if(rar == null) {
			return false;
		} else if (rar.isCanDo()){
			return true;
		}
		return false;
	}


	@Override
	public String authenticateUser(String username, String password) {
		Profile profile = this.getProfileByUserNameAndStatus(username, ProfileStatus.ACTIVE);
		if(profile==null){
			return SystemConstants.AUTH_STATUS_INVALID_CREDENTIALS;
		}
		if(profile.getPasswordExpiryDate()!= null){
			if(new Date().after(profile.getPasswordExpiryDate())){
				profile.setStatus(ProfileStatus.DISABLED);
				try{
					this.updateProfile(profile);
				}catch(Exception e){
					
				}
				return SystemConstants.AUTH_STATUS_PROFILE_EXPIRED;
			}			
		}
		String result = LdapUtil.validateUser(username, password);
		
		
		if(result.equals(SystemConstants.AUTH_STATUS_AUTHENTICATED)){
			
			if(profile.isChangePassword()){
				return SystemConstants.AUTH_STATUS_CHANGE_PASSWORD;
			}
			if(LdapUtil.shouldChangePassword(profile.getUserName())){
				return SystemConstants.AUTH_STATUS_CHANGE_PASSWORD;
			}
			profile.setLastLoginDate(new Date());
			profile.setLoggedIn(true);
			try {
				this.updateProfile(profile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}


	public String checkUserPassword(String userName, String password){
		String result = LdapUtil.validateUser(userName, password);
		return result;
	}
	
	
	
	public List<Profile> getProfileByBranchIdAndAccessRight(String branchId,
			String accessRightName) {
		
		List<Profile> branchProfiles = this.getProfileByBranchId(branchId);
		List<Profile> profileList = new ArrayList<Profile>();
		
		if(branchProfiles!=null){
			for(Profile profile:branchProfiles){
				if(this.canDo(profile.getUserName(), accessRightName)){
					profileList.add(profile);
				}
			}
		}
		return profileList;
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Profile> getAllLoggedOnUsers() {
		List<Profile> profiles = null;
		try {
			Query query = em.createNamedQuery("getAllLoggedOnProfiles");
			query.setParameter("loggedOn",1);
			profiles = (List<Profile>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			
		}
		return profiles;
	}

	@Override
	public Profile getProfileByIP(String ip) {
		Profile profiles = null;
		try {
			Query query = em.createNamedQuery("getProfileByIP");
			query.setParameter("ipAddress",ip);
			profiles = (Profile) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			
		}
		return profiles;
	}
	
	public Profile resetProfileIP(Profile profile, String userName) {

		try{
			String oldProfile = findProfileById(profile.getId()).getAuditableAttributesString();
			profile.setIpAddress("");
			profile = this.updateProfile(profile);
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.RESET_IP_ACTIVITY, profile.getId(), profile.getEntityName(), oldProfile, profile.getAuditableAttributesString(), profile.getInstanceName());
		}catch (Exception e) {
			
		}
		return profile;
		
	}


	public boolean validateHost(Profile profile , String ip) {
		if(profile.getIpAddress()== null || "".equalsIgnoreCase(profile.getIpAddress())){
//			try{
//				profile.setIpAddress(ip);
//				updateProfile(profile);
//			}catch (Exception e) {
//				
//			}
			return false;
		}else{
			if(!ip.equalsIgnoreCase(profile.getIpAddress())){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public Profile updateProfile(Profile profile, String userName) 	throws Exception {
		try {
			String oldProfile = this.findProfileById(profile.getId()).getAuditableAttributesString();
			profile = this.updateProfile(profile);
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.EDIT_PROFILE, profile.getId(), profile.getEntityName(), oldProfile, profile.getAuditableAttributesString(), profile.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error occured during modification of profile");
		}
		return profile;
	}

	@Override
	public Bulletin createBulletin(Bulletin bulletin, String userName)
			throws Exception {
		LOG.debug("Bulletin is "+bulletin+"In create bulletin");
		if(bulletin.getId() == null) {
			LOG.debug("Bulletin is "+bulletin+"In create bulletin");
			bulletin.setId(GenerateKey.generateEntityId());
    	}

    	if(bulletin.getDateCreated() == null) {
    		LOG.debug("Bulletin is "+bulletin+"In create bulletin");
    		bulletin.setDateCreated(new Date());
    	}
    	bulletin.setStatus(BulletinStatus.AWAITING_APPROVAL.name());
    	bulletin.setFieldsToUpperCase();
    	LOG.debug("Bulletin  : " + bulletin.getInitiatorId());
		try {
			Bulletin b = this.getCurrentBulletin();
			if (b!= null) {
				b.setStatus(BulletinStatus.EXPIRED.toString());
				this.updateBulletin(b);
			}
			em.persist(bulletin);
			
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.CREATE_BULLETIN, bulletin.getId(), bulletin.getEntityName(),null, bulletin.getAuditableAttributesString(), bulletin.getInstanceName());
		} catch(Exception e) {
			e.printStackTrace();
		}
	
		return bulletin;
	}

	@Override
	public Bulletin findBulletinById(String id) {
		Bulletin result = null;
		try {
			result = (Bulletin)em.find(Bulletin.class, id);
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			
		}
		return result;
	}

	@Override
	public Bulletin approveBulletin(Bulletin bulletin, String userName) throws Exception{
		try {
			String oldBulletin = this.findBulletinById(bulletin.getId()).getAuditableAttributesString();
			bulletin.setStatus(BulletinStatus.ACTIVE.toString());
			this.updateBulletin(bulletin);
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.APPROVE_BULLETIN, bulletin.getId(), bulletin.getEntityName(), oldBulletin, bulletin.getAuditableAttributesString(), bulletin.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return bulletin;
	}

	@Override
	public Bulletin disapproveBulletin(Bulletin bulletin, String userName) throws Exception{
		try {
			String oldBulletin = this.findBulletinById(bulletin.getId()).getAuditableAttributesString();
			bulletin.setStatus(BulletinStatus.DISAPPROVED.toString());
			this.updateBulletin(bulletin);
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.DISAPPROVE_BULLETIN, bulletin.getId(), bulletin.getEntityName(), oldBulletin, bulletin.getAuditableAttributesString(), bulletin.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return bulletin;
	}

	@Override
	public Bulletin deactivateBulletin(Bulletin bulletin, String userName) throws Exception{
		try {
			String oldBulletin = this.findBulletinById(bulletin.getId()).getAuditableAttributesString();
			bulletin.setStatus(BulletinStatus.INACTIVE.toString());
			this.updateBulletin(bulletin);
			//Audit Trail
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivity(userName, AuditEvents.DEACTIVATE_BULLETIN, bulletin.getId(), bulletin.getEntityName(), oldBulletin, bulletin.getAuditableAttributesString(), bulletin.getInstanceName());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return bulletin;
	}

	@Override
	public List<Bulletin> getBulletinByStatus(String status) {
		List<Bulletin> results = null;
		try {
			Query query = em.createNamedQuery("getBulletinByStatus");
			query.setParameter("status",status);
			results = (List<Bulletin>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			
		}
		return results;
	}

	@Override
	public Bulletin getCurrentBulletin() {
		Bulletin results = null;
		try {
			Query query = em.createNamedQuery("getCurrentBulletin");
			query.setParameter("status",BulletinStatus.ACTIVE);
			query.setParameter("expirationDate",new Date());
			results = (Bulletin) query.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			
		}
		return results;
	}

	@Override
	public List<Bulletin> getBulletinByApprover(String approverId) {
		List<Bulletin> results = null;
		try {
			Query query = em.createNamedQuery("getBulletinByInitiator");
			query.setParameter("approverId",approverId);			
			results = (List<Bulletin>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			
		}
		return results;
	}

	@Override
	public List<Bulletin> getBulletinByInitiator(String initiatorId) {
		
		List<Bulletin> results = null;
		try {
			Query query = em.createNamedQuery("getBulletinByInitiator");
			query.setParameter("initiatorId",initiatorId);			
			results = (List<Bulletin>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			
		}
		return results;
	}

	@Override
	public List<Bulletin> getBulletinByAllFields(String approverId,
			String initiatorId,String status,Date dateCreated ,Date expirationDate) {
		List<Bulletin> bulletin = null;
		
		if (approverId == null) {
			approverId = "";
		}
		
		if(initiatorId == null){
			initiatorId = "";
		}
		
		if(status == null){
			status = "";
		}
		
		if(expirationDate == null){
			expirationDate = DateUtil.getEndOfDay(new Date());
		}
		
		if(dateCreated == null){
			dateCreated = DateUtil.getBeginningOfDay(new Date());
		}
		try {
			Query query = em.createNamedQuery("getBulletinByAllFields");
			query.setParameter("approverId", approverId);
			query.setParameter("approverId", initiatorId);
			query.setParameter("status", status);
			query.setParameter("dateCreated", dateCreated);
			query.setParameter("expirationDate", expirationDate);
			
			bulletin = (List<Bulletin>)query.getResultList();
		} catch (NoResultException e) {
			//e.printStackTrace();
			LOG.debug("no result exception");
		} catch (Exception e) {
			e.printStackTrace();
		}
		//LOG.debug("returning>>>>>>>>>>>>>>>>>>>>>.."+profile);
		return bulletin;
	}
	
	private Bulletin updateBulletin(Bulletin bulletin) throws Exception{
		
		bulletin.setFieldsToUpperCase();
		try {
			bulletin = em.merge(bulletin);
		}catch (Exception e) {
			throw e;
		}
		return bulletin;
	}
}
