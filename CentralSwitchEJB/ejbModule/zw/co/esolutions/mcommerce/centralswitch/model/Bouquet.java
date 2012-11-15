package zw.co.esolutions.mcommerce.centralswitch.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

@Entity
@NamedQueries({
	@NamedQuery(name="getBouquetByName", query = "SELECT b FROM Bouquet b WHERE b.name = :name"),
	@NamedQuery(name="getBouquetByCode", query = "SELECT b FROM Bouquet b WHERE b.code = :code"),
	@NamedQuery(name="getBouquetByNameAndType", query = "SELECT b FROM Bouquet b WHERE b.name = :name AND b.type = :type"),
	@NamedQuery(name="getBouquetByCodeAndType", query = "SELECT b FROM Bouquet b WHERE b.code = :code AND b.type = :type")

})
public class Bouquet {

	@Id 
	@Column(length = 30)
	private String id;
	
	@Column(length = 50)
	private String name;
	
	@Column(length = 20)
	private String code;
	
	@Column(length = 20)
	private String type;
	
	@Version private long version;

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public long getVersion() {
		return version;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
}
