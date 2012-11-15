package zw.co.esolutions.ewallet.process.timers;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.ewallet.agentservice.service.Agent;
import zw.co.esolutions.ewallet.agentservice.service.AgentServiceSOAPProxy;
import zw.co.esolutions.ewallet.agentservice.service.ProfileStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankAccount;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountStatus;
import zw.co.esolutions.ewallet.bankservices.service.BankAccountType;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.bankservices.service.OwnerType;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetailsServiceSOAPProxy;
import zw.co.esolutions.ewallet.customerservices.service.Customer;
import zw.co.esolutions.ewallet.customerservices.service.CustomerServiceSOAPProxy;
import zw.co.esolutions.ewallet.reports.util.GenerateReportUtil;
import zw.co.esolutions.ewallet.util.DateUtil;
import zw.co.esolutions.ewallet.util.EWalletConstants;
import zw.co.esolutions.ewallet.util.EmailSender;
import zw.co.esolutions.ewallet.util.SystemConstants;

/**
 * Session Bean implementation class AgentStatementProcessorImpl
 */
@Stateless
public class AgentStatementProcessorImpl implements AgentStatementProcessor{

	@Resource
	private TimerService timerService;
	
	private static Logger LOG;
	private final static String AGENT_STATEMENT_TIMER = "Agent_Statement_Timer";
	private static Properties sysConfig = SystemConstants.configParams;
	
    public AgentStatementProcessorImpl() {
        // TODO Auto-generated constructor stub
    }
    
    static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/ewallet.log.properties");
			LOG = Logger.getLogger(AgentStatementProcessorImpl.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + AgentStatementProcessorImpl.class);
		}
	}
	
    @Timeout
    public void processAgentStatements(Timer timer){
    	LOG.debug("Runnning Timer Service Method ");
    	
		try{
//			LOG.debug("Scheduling new Timer for next run ");
//	    	this.scheduleTimer(this.getNextDay(hr,min));
	    	this.processAgentStatments();
		}catch(Exception e){
			
			LOG.debug("The procession failed :"+e.getMessage());
			e.printStackTrace();
		}
    	
    }
    
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void scheduleTimer(Date date) {
		LOG.debug(" processes >>>>>>>>>>>>>>>>>>>>> Schedule for next date.");
		Calendar calendar = Calendar.getInstance();
		
		try {			
			calendar.setTime(date);
			cancelTimer(AGENT_STATEMENT_TIMER);
		LOG.debug("In  Agent Sweeping processes >>>>>>>>>>>>>>>>>>>>> Done cancelling Timer."+date.toString());
			timerService.createTimer(calendar.getTime(), AGENT_STATEMENT_TIMER);
			long time = 1000*60*60*24;
//			long time = 1000*60*60*3;
//			timerService.createTimer(time,  AGENT_STATEMENT_TIMER);
			timerService.createTimer(this.getRunTime(date), time, AGENT_STATEMENT_TIMER);
			LOG.debug(" >>>>>>>>>>>>>>>>>>>>> Done Scheduling new timer.");
		} catch (Exception e) {
			// e.printStackTrace();
		}

	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void cancelTimer(String description) throws Exception{
		LOG.debug(" In Cancel Timer Method >>>>>>>>>>>>> Description = "+description);
		for (Object obj : timerService.getTimers( )) {
	        Timer timer = (Timer)obj;
	        LOG.debug(" In cancelTimer Method >>>>>>>>>>>>> Timer found = "+timer);
	        String scheduled = (String)timer.getInfo();
	        LOG.debug(" In cancelTimer Method >>>>>>>>>>>>> Timer found description : "+scheduled);
	        if (scheduled.equals(description)) {
	            timer.cancel();
	            LOG.debug(" In cancelTimer Method >>>>>>>>>>>>> Timer cancelled : "+scheduled);
	        }
	    }
	}
	
	
	private Date getRunTime(Date date) {
		
		String hour = null, minute= null;
		// schedule timer for next day 
		if (sysConfig != null) {
			
			hour = sysConfig.getProperty(SystemConstants.AGENT_STATEMENT_HOUR);
			LOG.debug("Getting Hour Value for Timer  >>>>>>>>>>>>>>>>>>>>> Hour = "+hour);
			minute = sysConfig.getProperty(SystemConstants.AGENT_STATEMENT_MINUTE);
			LOG.debug("Gettinh Min Value for Timer  >>>>>>>>>>>>>>>>>>>>> Minute = "+minute);
		}
		
		int hr = Integer.parseInt(hour);
		int min = Integer.parseInt(minute);
		
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, hr);
		calendar.set(Calendar.MINUTE, min);
		
//		calendar.setTime(new Date());
//		calendar.add(Calendar.MINUTE,3);
			
		return calendar.getTime();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void processAgentStatments(){
		
		AgentServiceSOAPProxy agentService = new AgentServiceSOAPProxy();
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		ContactDetailsServiceSOAPProxy contactDetailsService = new ContactDetailsServiceSOAPProxy();
		CustomerServiceSOAPProxy customerService = new CustomerServiceSOAPProxy();
		
		List<BankAccount> agentAccounts = new ArrayList<BankAccount>();
		List<Agent> agentList = agentService.getAgentByStatus(ProfileStatus.ACTIVE.name());
		String agentEwalletStatement = "";
		String commissionSuspenceStatement = "";
		BankAccount agentEwallet , commissionSuspense ;
		List<BankAccount> temp;
		String recipient;
		String [] fileNames =  {agentEwalletStatement,commissionSuspenceStatement};
		ContactDetails contacts =  null;
		Customer customer = null;
		File agentEwalletFile = null;
		File commissionSuspenseFile = null;
		
		for(Agent agent:agentList){
			try{
				customer = customerService.findCustomerById(agent.getCustomerId());
				contacts = contactDetailsService.findContactDetailsById(customer.getContactDetailsId());
				recipient = contacts.getEmail();
				temp = bankService.getBankAccountsByAccountHolderIdAndOwnerTypeAndStatusAndBankAccountType(agent.getCustomerId(),OwnerType.AGENT,
						BankAccountStatus.ACTIVE, BankAccountType.AGENT_EWALLET);
				agentEwallet = temp.get(0);
				
				temp = bankService.getBankAccountsByAccountHolderIdAndOwnerTypeAndStatusAndBankAccountType(agent.getCustomerId(),OwnerType.AGENT,
						BankAccountStatus.ACTIVE, BankAccountType.AGENT_COMMISSION_SUSPENSE);
				commissionSuspense = temp.get(0);
				
				agentAccounts.add(agentEwallet);
				agentAccounts.add(commissionSuspense);
				for(BankAccount account : agentAccounts){
//				BankAccount account = bankService.getBankAccountByAccountNumberAndOwnerType("4131847300840",OwnerType.BANK);
					this.generateReport(account);
				}
				agentEwalletStatement = (String)sysConfig.getProperty("PROCESS_MODULE_PATH")+GenerateReportUtil.PDF_FOLDER+agentEwallet.getAccountName()+"_"+
				agentEwallet.getType()+"_"+"Statement.pdf";
				
				commissionSuspenceStatement = (String)sysConfig.getProperty("PROCESS_MODULE_PATH")+GenerateReportUtil.PDF_FOLDER+commissionSuspense.getAccountName()+"_"+
				commissionSuspense.getType()+"_"+"Statement.pdf";
	
//				String [] docNames = {account.getAccountName()+"_"+account.getType()+"_"+"Statement.pdf"};
				String [] docNames = {agentEwallet.getAccountName()+"_"+agentEwallet.getType()+"_"+"Statement.pdf",
						commissionSuspense.getAccountName()+"_"+commissionSuspense.getType()+"_"+"Statement.pdf"};
				
				this.sendMail(docNames, recipient);
//				this.sendMail(docNames, "zviko.kanyume@gmail.com");
				
				agentEwalletFile = new File(agentEwalletStatement);
				agentEwalletFile.delete();
				commissionSuspenseFile = new File(commissionSuspenceStatement);
				commissionSuspenseFile.delete();
				
			}catch(Exception e){
				LOG.warn("There is an error with the agent details "+e.getMessage());
				e.printStackTrace();
			}
		}
		
		
		
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void generateReport(BankAccount account){
		
		JasperPrint jasperPrint = null;
		
		String query = "SELECT t.transactiondate as valueDate,t.type as transactionType ,t.processtxnreference as messageId,t.amount, " +
				"t.accountId, t.narrative, COALESCE ((SELECT SUM(tr.amount) FROM BANKIF.TRANSACTION as tr WHERE tr.TRANSACTIONDATE < '" +DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay((new Date())))+
				"' AND tr.accountid='"+ account.getId() +"') ,0) as sumbefore  FROM BANKIF.TRANSACTION as t WHERE t.accountid='"+ account.getId() + 
				"' and t.transactiondate >= '"+ DateUtil.convertDateToTimestamp(DateUtil.getBeginningOfDay((new Date()))) +
				"' and t.transactiondate <='"+DateUtil.convertDateToTimestamp(DateUtil.getEndOfDay(new Date())) +"'"+" ORDER BY t.transactiondate ASC ";
		
		
//		 WHERE tr.TRANSACTIONDATE < '2012-03-04 00:00:00.0' AND tr.accountid='Z5A31309864043569421') ,0) as sumbefore  FROM BANKIF.TRANSACTION as t WHERE t.accountid='Z5A31309864043569421' " +
//				"and t.transactiondate >= '2012-03-04 00:00:00.0' and t.transactiondate <='2012-05-04 23:59:59.999'ORDER BY t.transactiondate ASC ";
		LOG.debug(query);
		
		String fileName = "account_statement_report";
		Map<String, String> parameters = new HashMap<String, String>();
		
		parameters.put("sourceFile", fileName+".xml");
		parameters.put("bankName", account.getBranch().getBank().getName()+"\n \n"+account.getBranch().getName());
//		parameters.put("bankName", "ZB BANK");
		
        String fileId = GenerateReportUtil.getJasperUserId("SYSTEM");
		
        parameters.put("fileId", fileId);
		
		parameters.put("fromDate", DateUtil.convertDateToLongString(DateUtil.getBeginningOfDay(new Date())));
		parameters.put("toDate", DateUtil.convertDateToLongString(DateUtil.getEndOfDay(new Date())));
		parameters.put("asAtDate", DateUtil.convertDateToLongString(new Date()));
		
		parameters.put("reportTitle", account.getAccountName() +" : "+account.getAccountNumber()+" ["+account.getType().toString()+"] " +
		"Account Statement");
		parameters.put("pageHeader", "Account Statement for: "+DateUtil.convertToDateWithTime(new Date()));
				
//		jasperPrint = GenerateReportUtil.generatePrintViaDatasource(query, parameters, new HashMap<String, String>(), (String)application.getRealPath("/")+"/");
		try{
			
			if(!checkSize(query)){
				//No data to display use viaCollectionDataSource
				parameters.put(EWalletConstants.JASPER_COLLECTION_DATASOURSE, EWalletConstants.JASPER_COLLECTION_DATASOURSE);
				parameters.put("noDate","noData");
				parameters.put("accountId",account.getId());
				
				jasperPrint = GenerateReportUtil.generatePrintViaCollectionDatasource(query, parameters, (String)sysConfig.getProperty("PROCESS_MODULE_PATH"));
			}else{
				jasperPrint = GenerateReportUtil.generatePrintViaDatasource(query, parameters, new HashMap<String, String>(), (String)sysConfig.getProperty("PROCESS_MODULE_PATH"));
			}
			
			String reportName = (String)sysConfig.getProperty("PROCESS_MODULE_PATH")+GenerateReportUtil.PDF_FOLDER+account.getAccountName()+"_"+
			account.getType()+"_"+"Statement.pdf";
			JasperExportManager.exportReportToPdfFile(jasperPrint,reportName);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void sendMail(String[]fileNames,String recipient){
		EmailSender es = new EmailSender();
		String from = "ewallet@zb.co.zw";
		String subject = "Ewallet Statemnt "+DateUtil.convertDateToSimpleDayWithNoTime(new Date());
		String[] recipients = {recipient};
		String message = "Find attached a copy of your Agent Ewallet Account Statement and a copy of your Commission Suspence Account";
//		es.postMailWithAttachments(recipients, from, subject, message, fileNames);
		es.sendCommonsMail(recipients, from, subject, message, fileNames);
	}

	@Override
	public boolean checkTimer(String description) {
		LOG.debug("***************** Validating timer config *******************");
		try{
			LOG.debug("Retrieving agent timer status");
			String status = sysConfig.getProperty("AGENT_STATEMENT_STATUS");
			LOG.debug("Checking config value "+status);
			if("true".equals(status)){
				LOG.debug("Status is "+timerService.getTimers().isEmpty()+"*********");
				if(timerService.getTimers().isEmpty()){
//					if(true){
						LOG.debug("Scheduling new timer ");
			        	this.scheduleTimer(new Date());
					}else{
						for (Object obj : timerService.getTimers( )) {
					        Timer timer = (Timer)obj;
					        String scheduled = (String)timer.getInfo();
					        if (scheduled.equals(description)) {
					        	LOG.debug("Timer already scheduled return ");
					            return true;
					        }else{
					        	LOG.debug("Scheduling new timer ");
					        	this.scheduleTimer(new Date());
					        	return true;
					        }
					    }
					}
//				}
			}else{
				LOG.debug("Canceling the TIMER");
				cancelTimer(AGENT_STATEMENT_TIMER);
			}
		}catch (Exception e) {
			LOG.debug("Exception occured"+e.getMessage());
			return false;
		}	
		return false;
	}
	
	private boolean checkSize(String query){
			
		try{
			Connection connection = GenerateReportUtil.establishConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
//			System.out.println(resultSet.getInt("totalCustomers")+"********************");
			
			if(resultSet.next()){
				return true;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
