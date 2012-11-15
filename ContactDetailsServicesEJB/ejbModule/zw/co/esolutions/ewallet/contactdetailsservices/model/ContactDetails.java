package zw.co.esolutions.ewallet.contactdetailsservices.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

import zw.co.esolutions.ewallet.audit.Auditable;
import zw.co.esolutions.ewallet.util.MapUtil;

@Entity
public class ContactDetails implements Serializable,Auditable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	
	
	@Id @Column(length=30) private String id;
	@Column(length=40) private String contactName;
	@Column(length=30) private String telephone;
	@Column(length=200) private String postalAddress;
	@Column(length=40) private String email;
	@Column(length=30) private String fax;
	@Column(length=40) private String street;
	@Column(length=30) private String suburb;
	@Column(length=30) private String city;
	//@Column(length=100) private String postalAddress;
	@Column(length=30) private String country;
	@Column(length=30) private String ownerId;
	@Column(length=30) private String ownerType;
	@Column private Date dateCreated;
	@Version @Column private long version;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getSuburb() {
		return suburb;
	}
	public void setSuburb(String suburb) {
		this.suburb = suburb;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	public String getOwnerType() {
		return ownerType;
	}
	public void setOwnerType(String ownerType) {
		this.ownerType = ownerType;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setVersion(long version) {
		this.version = version;
	}
	public long getVersion() {
		return version;
	}
	
	public void setFieldsToUpperCase() {
		contactName = (contactName != null)? this.contactName.toUpperCase() : null;
    	city = (city != null)? this.city.toUpperCase() : null;
    	street = (street != null)? this.street.toUpperCase() : null;
    	suburb = (suburb != null)? this.suburb.toUpperCase() : null;
    	country = (country != null)? this.country.toUpperCase() : null;
    	postalAddress=(postalAddress != null) ? this.postalAddress.toUpperCase(): null;
    
	}
	@Override
	public Map<String, String> getAuditableAttributesMap() {
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put("contactName", getContactName());
		attributesMap.put("telephone", getTelephone());
		attributesMap.put("email", getEmail());
		attributesMap.put("fax", getFax());
		attributesMap.put("street", getStreet());
		attributesMap.put("suburb", getSuburb());
		attributesMap.put("city", getCity());
		attributesMap.put("country", getCountry());
		attributesMap.put("postalAddress", getPostalAddress());
		return attributesMap;
	}
	@Override
	public String getAuditableAttributesString() {
		return MapUtil.convertAttributesMapToString(getAuditableAttributesMap());
	}
	@Override
	public String getEntityName() {
		return getOwnerType();
	}
	@Override
	public String getInstanceName() {
		return getContactName();
	}
	public String getPostalAddress() {
		return postalAddress;
	}
	public void setPostalAddress(String postalAddress) {
		this.postalAddress = postalAddress;
	}
	
	
}
