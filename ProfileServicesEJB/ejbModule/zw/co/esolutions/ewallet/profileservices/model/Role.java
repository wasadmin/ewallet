package zw.co.esolutions.ewallet.profileservices.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.*;

import zw.co.esolutions.ewallet.audit.Auditable;
import zw.co.esolutions.ewallet.enums.RoleStatus;
import zw.co.esolutions.ewallet.util.MapUtil;

/**
 * Entity implementation class for Entity: Role
 *
 */
@Entity
@NamedQueries({
	@NamedQuery(name="getAllRoles",query="SELECT r FROM Role r ORDER BY r.roleName ASC"),
	@NamedQuery(name="getActiveRoles",query="SELECT r FROM Role r WHERE r.status =:status ORDER BY r.roleName ASC"),
	@NamedQuery(name="getRoleByRoleName",query="SELECT r FROM Role r WHERE r.roleName =:roleName"),
	@NamedQuery(name="getRoleByStatus",query="SELECT r FROM Role r WHERE r.status =:status ORDER BY r.roleName ASC")
})
public class Role implements Serializable,Auditable {

	@Id
	@Column(length=30)
	private String id;
	@Column(length=30)
	private String roleName;
	@Enumerated(EnumType.STRING)
	private RoleStatus status;
	@OneToMany(mappedBy="role")
	private List<RoleAccessRight> roleAccessRights;
	@OneToMany(mappedBy="role")
	private List<Profile> profiles;
	@Column Date dateCreated;
	@Version @Column private int version;
	
	private static final long serialVersionUID = 1L;

	public Role() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public List<RoleAccessRight> getRoleAccessRights() {
		return roleAccessRights;
	}

	public void setRoleAccessRights(List<RoleAccessRight> roleAccessRights) {
		this.roleAccessRights = roleAccessRights;
	}

	public List<Profile> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<Profile> profiles) {
		this.profiles = profiles;
	}
	
	public RoleStatus getStatus() {
		return status;
	}

	public void setStatus(RoleStatus status) {
		this.status = status;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public Map<String, String> getAuditableAttributesMap() {
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put("roleName", getRoleName());
		attributesMap.put("status", getStatus().toString());
		return attributesMap;
	}

	@Override
	public String getAuditableAttributesString() {
		return MapUtil.convertAttributesMapToString(getAuditableAttributesMap());
	}

	@Override
	public String getEntityName() {
		return "ROLE";
	}

	@Override
	public String getInstanceName() {
		return getRoleName();
	}

	
}
