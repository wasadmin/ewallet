package zw.co.esolutions.ewallet.profileservices.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import zw.co.esolutions.ewallet.enums.AccessRightStatus;

@Entity
@NamedQueries ({
	@NamedQuery(name="getAccessRight", query="SELECT a FROM AccessRight a"),
	@NamedQuery(name="getAccessRightByActionName", query="SELECT a FROM AccessRight a WHERE a.actionName LIKE: actionName"),
	@NamedQuery(name="getAccessRightByActualActionName", query="SELECT a FROM AccessRight a WHERE a.actionName = :actionName")
})

public class AccessRight {
	
	@Id @Column(length=30) private String id;
	@Column(length=50, unique = true) private String actionName;
	@Enumerated(EnumType.STRING) private AccessRightStatus status;
	@OneToMany(mappedBy="accessRight") private List<RoleAccessRight> roleAccessRights;
	@Column private Date dateCreated;
	@Version @Column private long version;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public AccessRightStatus getStatus() {
		return status;
	}
	public void setStatus(AccessRightStatus status) {
		this.status = status;
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
	public void setActionName(String actionName) {
		this.actionName = actionName.toUpperCase();
	}
	public String getActionName() {
		return actionName;
	}
	public void setRoleAccessRights(List<RoleAccessRight> profileAccessRights) {
		this.roleAccessRights = profileAccessRights;
	}
	public List<RoleAccessRight> getRoleleAccessRights() {
		return roleAccessRights;
	}
	
	public void setFieldsToUpperCase() {
		this.setActionName(this.actionName.toUpperCase());
	}
}
