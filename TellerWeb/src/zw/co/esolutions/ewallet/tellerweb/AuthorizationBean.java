package zw.co.esolutions.ewallet.tellerweb;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.security.auth.Subject;

import pagecode.PageCodeBase;
import zw.co.esolutions.ewallet.audit.AuditEvents;
import zw.co.esolutions.ewallet.audittrailservices.service.AuditTrailServiceSOAPProxy;
import zw.co.esolutions.ewallet.profileservices.service.Profile;
import zw.co.esolutions.ewallet.tellerweb.model.TellerCode;
import zw.co.esolutions.ewallet.util.DateUtil;

public class AuthorizationBean extends PageCodeBase{

	private String code;
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public EntityManager getEntityManager() {
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("TellerWeb");
		
		return emf.createEntityManager();
		
	}

	public String authorize(){
		try {
	//		Map<String, String> keysMap = (Map<String, String>)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("authorizationCodes");
			
			Subject user = (Subject)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("ProvisionalUser");

			String profileId = (String)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("profileId");

			TellerCode tellerCode = this.getTodayTellerCodeByProfileIdAndCode(profileId, code);
			
//			if(keysMap==null||user==null){
//				this.redirect("login/login.jspx");
//				return "failure";
//			}
			
			if (tellerCode == null || tellerCode.getId() == null) {
				super.setErrorMessage("Invalid authorization code");
				return "failure";
			}
			
			String supervisorId = tellerCode.getSupervisorId();
			if(supervisorId==null){
				super.setErrorMessage("Invalid authorization code");
				return "failure";
			}
			
			//Add user to session
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("CurrentUser",user);
			//Remove temporary attributes from session
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("profileId");
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("ProvisionalUser");
			
			//Log Teller Authorization
			Profile supervisor = super.getProfileService().findProfileById(supervisorId);
			AuditTrailServiceSOAPProxy auditService = new AuditTrailServiceSOAPProxy();
			auditService.logActivityWithNarrative(getJaasUserName().toUpperCase(), AuditEvents.TELLER_AUTHORIZATION, "TELLER: "+super.getJaasUserName().toUpperCase()+" AUTHORIZED BY: "+supervisor.getUserName());
			
			//Move to teller home
			this.redirect("teller/tellerHome.jspx");
		} catch (Exception e) {
			super.setErrorMessage(PageCodeBase.ERROR_MESSAGE);
			e.printStackTrace();
		}
		
		return "authorize";
	}
	
	public String cancel(){
		try {
			//Remove temporary attributes from session
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("authorizationCodes");
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("ProvisionalUser");
			
			this.redirect("login/login.jspx");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "cancel";
	}
	
	private void redirect(String path){
		try {
			if(path.startsWith("/")){
				path = path.replaceFirst("/", "");
			}
			FacesContext.getCurrentInstance().getExternalContext().redirect("/TellerWeb/"+path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<TellerCode> getTellerCodesByProfileIdAndDate(String profileId, Date date) {
		
		EntityManager em = getEntityManager();

		date = DateUtil.convertToDateWithNoTime(date);

		List<TellerCode> tellerCodeList = null;
		
		try {
			
			em.getTransaction().begin();
			
			Query query = em.createQuery("SELECT t from TellerCode t WHERE t.profileId =: profileId AND t.dateCreated =: date");
			query.setParameter("profileId", profileId);
			query.setParameter("date", date);
			
			tellerCodeList = query.getResultList(); 
			
			em.getTransaction().commit();
			
		} catch(NoResultException n) {
			return null;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			em.close();
		}
		
		return tellerCodeList;
	}
	
	public TellerCode getTodayTellerCodeByProfileIdAndCode(String profileId, String code) {
		try {
			List<TellerCode> tellerCodeList = this.getTellerCodesByProfileIdAndDate(profileId, new Date());
			
			if (tellerCodeList == null || tellerCodeList.isEmpty()) {
				return null;
			}
			
			for (TellerCode tellerCode: tellerCodeList) {
				if (tellerCode.getCode().equals(code)) {
					return tellerCode;
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return null;
	}
}
