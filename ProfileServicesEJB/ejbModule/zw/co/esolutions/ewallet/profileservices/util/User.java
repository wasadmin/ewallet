package zw.co.esolutions.ewallet.profileservices.util;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import zw.co.esolutions.ewallet.profileservices.model.Profile;
import zw.co.esolutions.ewallet.util.SystemConstants;

public class User implements DirContext{

	private String type;
	private Attributes myAttrs;
	private String cn;
	private String sn;
	private String uid;
	private String givenName;
	private String userPassword;
	private String ou;
	private String base;
	private String dn;
	
	public User(Profile profile) {
		Properties config = SystemConstants.configParams;

		base = (String) config.get("SYSTEM_LDAP_SEARCHBASE_DN");
		ou = base;
		type = profile.getUserName();
		dn= "cn="+profile.getUserName()+ "," + base;
		uid = profile.getUserName();
		givenName = profile.getFirstNames();
		userPassword = profile.getUserPassword();
		// dn="cn="+cn+","+ou;
		sn = profile.getLastName();
		myAttrs = new BasicAttributes(true);
		Attribute oc = new BasicAttribute("objectclass");
		oc.add("inetOrgPerson");
		oc.add("organizationalPerson");
		oc.add("person");
		oc.add("top");

		Attribute ouSet = new BasicAttribute("ou");
		// ouSet.add("People");
		ouSet.add(ou);

		cn = profile.getUserName();

		myAttrs.put(oc);
		myAttrs.put(ouSet);
		myAttrs.put("uid", uid);
		myAttrs.put("cn", cn);
		myAttrs.put("sn", sn);
		myAttrs.put("givenName", givenName);
		myAttrs.put("userPassword", userPassword);
		// myAttrs.put("dn",dn);

		System.out.println(" cn ::" + cn);
		System.out.println("uid  :" + uid);
		System.out.println("sn  :" + sn);
		System.out.println("givenName  :" + givenName);
		System.out.println("dn  :" + dn);
		System.out.println("ou  :" + ou);
		System.out.println("base  :" + base);

	}
	
	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}



	public Attributes getMyAttrs() {
		return myAttrs;
	}



	public void setMyAttrs(Attributes myAttrs) {
		this.myAttrs = myAttrs;
	}



	public String getCn() {
		return cn;
	}



	public void setCn(String cn) {
		this.cn = cn;
	}



	public String getSn() {
		return sn;
	}



	public void setSn(String sn) {
		this.sn = sn;
	}



	public String getUid() {
		return uid;
	}



	public void setUid(String uid) {
		this.uid = uid;
	}



	public String getGivenName() {
		return givenName;
	}



	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}



	public String getUserPassword() {
		return userPassword;
	}



	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}



	public String getOu() {
		return ou;
	}



	public void setOu(String ou) {
		this.ou = ou;
	}



	public String getBase() {
		return base;
	}



	public void setBase(String base) {
		this.base = base;
	}



	public String getDn() {
		return dn;
	}



	public void setDn(String dn) {
		this.dn = dn;
	}



	@Override
	public void bind(Name name, Object obj, Attributes attributes)
			throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bind(String s, Object obj, Attributes attributes)
			throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DirContext createSubcontext(Name name, Attributes attributes)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DirContext createSubcontext(String s, Attributes attributes)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Attributes getAttributes(Name name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Attributes getAttributes(String s) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Attributes getAttributes(Name name, String[] as)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Attributes getAttributes(String s, String[] as)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DirContext getSchema(Name name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DirContext getSchema(String s) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DirContext getSchemaClassDefinition(Name name)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DirContext getSchemaClassDefinition(String s) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void modifyAttributes(Name name, ModificationItem[] modificationItems)
			throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modifyAttributes(String s, ModificationItem[] modificationItems)
			throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modifyAttributes(Name name, int i, Attributes attributes)
			throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modifyAttributes(String s, int i, Attributes attributes)
			throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rebind(Name name, Object obj, Attributes attributes)
			throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rebind(String s, Object obj, Attributes attributes)
			throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NamingEnumeration<SearchResult> search(Name name,
			Attributes attributes) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamingEnumeration<SearchResult> search(String name,
			Attributes attributes) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamingEnumeration<SearchResult> search(Name name,
			Attributes attributes, String[] as) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamingEnumeration<SearchResult> search(Name name, String filter,
			SearchControls searchControls) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamingEnumeration<SearchResult> search(String name,
			Attributes attributes, String[] as) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamingEnumeration<SearchResult> search(String name, String filter,
			SearchControls searchControls) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamingEnumeration<SearchResult> search(Name name, String filter,
			Object[] objs, SearchControls searchControls)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamingEnumeration<SearchResult> search(String name, String filter,
			Object[] objs, SearchControls searchControls)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object addToEnvironment(String s, Object o) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void bind(Name n, Object o) throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bind(String s, Object o) throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Name composeName(Name n, Name pfx) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String composeName(String s, String pfx) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Context createSubcontext(Name n) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Context createSubcontext(String s) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void destroySubcontext(Name n) throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroySubcontext(String s) throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Hashtable<?, ?> getEnvironment() throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNameInNamespace() throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NameParser getNameParser(Name n) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NameParser getNameParser(String s) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamingEnumeration<NameClassPair> list(Name n) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamingEnumeration<NameClassPair> list(String s)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamingEnumeration<Binding> listBindings(Name n)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamingEnumeration<Binding> listBindings(String s)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object lookup(Name n) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object lookup(String s) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object lookupLink(Name n) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object lookupLink(String s) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void rebind(Name n, Object o) throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rebind(String s, Object o) throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object removeFromEnvironment(String s) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void rename(Name old, Name new1) throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rename(String old, String new1) throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unbind(Name n) throws NamingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unbind(String s) throws NamingException {
		// TODO Auto-generated method stub
		
	}

}
