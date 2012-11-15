package zw.co.esolutions.ewallet.profileservices.service;

import java.util.Date;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import zw.co.esolutions.ewallet.enums.AccessRightStatus;
import zw.co.esolutions.ewallet.enums.ProfileStatus;
import zw.co.esolutions.ewallet.enums.RoleStatus;
import zw.co.esolutions.ewallet.profileservices.model.AccessRight;
import zw.co.esolutions.ewallet.profileservices.model.Bulletin;
import zw.co.esolutions.ewallet.profileservices.model.Profile;
import zw.co.esolutions.ewallet.profileservices.model.Role;
import zw.co.esolutions.ewallet.profileservices.model.RoleAccessRight;

@WebService(name="ProfileService")
public interface ProfileService {
	
	AccessRight getAccessRightByActionName(String actionName);

	List<AccessRight> getAccessRight();

	List<Profile> getProfileByLastName(String lastName);

	List<Profile> getProfileByStatus(ProfileStatus status) throws Exception;
	
	AccessRight createAccessRight(@WebParam(name="accessRight") AccessRight accessRight, 
			@WebParam(name="userName") String userName) throws Exception;

	String deleteAccessRight(@WebParam(name="accessRight") AccessRight accessRight, 
			@WebParam(name="userName") String userName) throws Exception;

	AccessRight editAccessRight(@WebParam(name="accessRight") AccessRight accessRight, 
			@WebParam(name="userName") String userName) throws Exception;

	AccessRight findAccessRightById(@WebParam(name="id") String id);

	RoleAccessRight updateRoleAccessRight(@WebParam(name="roleAccessRight")RoleAccessRight roleAccessRight,@WebParam(name="userName")String username) throws Exception;
	
	RoleAccessRight findRoleAccessRightById(@WebParam(name="id")String id);
	
	RoleAccessRight getRoleAccessRightByRoleActionNameAndStatus(@WebParam(name="roleId")String roleId,@WebParam(name="actionName")String actionName,@WebParam(name="status")AccessRightStatus status);
	
	List<RoleAccessRight> getRoleAccessRightByRole(@WebParam(name="roleId")String roleId);
	
	Profile createProfile(@WebParam(name="profile") Profile profile, 
			@WebParam(name="userName") String userName) throws Exception;

	String deleteProfile(@WebParam(name="profile") Profile profile, 
			@WebParam(name="userName") String userName) throws Exception;

	Profile editProfile(@WebParam(name="profile") Profile profile, 
			@WebParam(name="userName") String userName) throws Exception;
	
	Profile approveProfile(@WebParam(name="profile") Profile profile, 
			@WebParam(name="userName") String userName) throws Exception;

	Profile rejectProfile(@WebParam(name="profile") Profile profile, 
			@WebParam(name="userName") String userName) throws Exception;
	
	String changeProfilePassword(@WebParam(name="profile") Profile profile,@WebParam(name="oldPassword")String oldPassword,@WebParam(name="newPassword")String newPassword,
			@WebParam(name="userName") String userName) throws Exception;
	
	String resetProfilePassword(@WebParam(name="profile") Profile profile, 
			@WebParam(name="userName") String userName) throws Exception;
	
	Profile findProfileById(@WebParam(name="id") String id);
	
	Profile getProfileByUserName(@WebParam(name="userName") String userName);
	
	Profile getProfileByUserNameAndStatus(@WebParam(name="userName") String userName,@WebParam(name="status") ProfileStatus status);
	
	List<Profile> getProfileByUserRole(@WebParam(name="roleName") String roleName); 
	
	List<Profile> getAllProfiles();
	
	List<Profile> getProfileByBranchId(@WebParam(name="branchId") String branchId);
	
	List<Profile> getProfileByBranchIdAndAccessRight(@WebParam(name="branchId") String branchId,@WebParam(name="accessRightName") String accessRightName);
	
	Role createRole(@WebParam(name="role")Role role,@WebParam(name="userName")String userName) throws Exception;
	Role editRole(@WebParam(name="role")Role role,@WebParam(name="userName")String userName) throws Exception;
	Role approveRole(@WebParam(name="role")Role role,@WebParam(name="userName")String userName) throws Exception;
	Role rejectRole(@WebParam(name="role")Role role,@WebParam(name="userName")String userName) throws Exception;
	Role deleteRole(@WebParam(name="role")Role role,@WebParam(name="userName")String userName) throws Exception;
	Role findRoleByRoleId(@WebParam(name="roleId")String roleId);
	Role getRoleByRoleName(@WebParam(name="roleName")String roleName);
	List<Role> getRoleByStatus(@WebParam(name="roleStatus")RoleStatus status);
	List<Role> getActiveRoles();
	
	boolean canDo(@WebParam(name="username")String userName,@WebParam(name="accessRightName")String accessRightName);
	
	String authenticateUser(@WebParam(name="username")String username,@WebParam(name="password")String password);
	
	Profile updateProfile(@WebParam(name="profile") Profile profile, @WebParam(name="userName") String userName) throws Exception;
	
	List<Profile> getAllLoggedOnUsers();
	
	Profile getProfileByIP(@WebParam(name="ip") String ip);
	
	Profile resetProfileIP(@WebParam(name="profile") Profile profile , @WebParam(name="userName")String userName);
	
	boolean validateHost(@WebParam(name="profile") Profile profile,@WebParam(name="ip")String ip);
	
	Bulletin createBulletin(@WebParam(name="bulletin") Bulletin bulletin, @WebParam(name="userName") String userName) throws Exception;
	
	Bulletin findBulletinById(@WebParam(name="id") String id);
	
	Bulletin approveBulletin(@WebParam(name="bulletin") Bulletin bulletin,@WebParam(name="userName") String userName) throws Exception;
	
	Bulletin disapproveBulletin(@WebParam(name="bulletin") Bulletin bulletin,@WebParam(name="userName") String userName) throws Exception;
	
	Bulletin deactivateBulletin(@WebParam(name="bulletin") Bulletin bulletin,@WebParam(name="userName") String userName) throws Exception;
	
	List<Bulletin> getBulletinByStatus(@WebParam(name="status") String status);
	
	Bulletin getCurrentBulletin();
	
	List<Bulletin> getBulletinByApprover(@WebParam(name="approverId") String approverId);
	
	List<Bulletin>getBulletinByInitiator(@WebParam(name="initiatorId") String initiatorId);
	
	List<Bulletin> getBulletinByAllFields(@WebParam(name="approverId") String approverId,@WebParam(name="initiatorId") String initiatorId,
			@WebParam(name="status") String status,@WebParam(name="dateCreated")Date dateCreated ,@WebParam(name="expirationDate")Date expirationDate);
}
