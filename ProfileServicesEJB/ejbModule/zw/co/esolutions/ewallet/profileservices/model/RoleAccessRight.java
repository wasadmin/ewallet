package zw.co.esolutions.ewallet.profileservices.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

import zw.co.esolutions.ewallet.audit.Auditable;
import zw.co.esolutions.ewallet.util.MapUtil;

@Entity
@NamedQueries ({
	@NamedQuery(name="getRoleAccessRightByRole", query="SELECT p FROM RoleAccessRight p WHERE p.role.id =: roleId ORDER BY p.accessRight.actionName ASC"),
	@NamedQuery(name = "getRoleAccessRightByRoleAndActionName", query = "SELECT p FROM RoleAccessRight p WHERE p.role.id =: roleId AND p.accessRight.actionName =: actionName"),
	@NamedQuery(name="getRoleAccessRightByRoleActionNameAndStatus", query="SELECT p FROM RoleAccessRight p WHERE p.role.id =: roleId " +
			"AND p.accessRight.actionName = :actionName AND  p.accessRight.status = :status " +
			"AND p.canDo = :canDo")
})
public class RoleAccessRight implements Auditable{
	
	@Id @Column(length=30) private String id;
	@Column private boolean canDo;
	@ManyToOne @Column private Role role;
	@ManyToOne @Column private AccessRight accessRight;
	@Column private Date dateCreated;
	@Version @Column private long version;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isCanDo() {
		return canDo;
	}
	public void setCanDo(boolean canDo) {
		this.canDo = canDo;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public long getVersion() {
		return version;
	}
	public void setVersion(long version) {
		this.version = version;
	}
	
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public void setAccessRight(AccessRight accessRight) {
		this.accessRight = accessRight;
	}
	public AccessRight getAccessRight() {
		return accessRight;
	}
	@Override
	public Map<String, String> getAuditableAttributesMap() {
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put("canDo", canDo+"");
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
		return role.getRoleName();
	}
	
	
}
