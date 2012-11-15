package zw.co.esolutions.merchants.session;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import zw.co.esolutions.merchants.util.InterfaceConstants;
import zw.co.esolutions.merchants.util.PaymentRequest;
import zw.co.esolutions.merchants.util.PaymentResponse;

/**
 * Session Bean implementation class MerchantPaymentProcessor
 */
@Stateless
@LocalBean
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class MerchantPaymentProcessor {

	// @Resource(mappedName = "jdbc/ZETDC")
	private DataSource zetdcDataSource;

	// @Resource
	// UserTransaction utx;

//	private Connection jdbcConnection;

	public MerchantPaymentProcessor() {

	}

	private static Logger LOG;

	static {
		try {
			PropertyConfigurator.configure("/opt/eSolutions/conf/merchantsGateway.log.properties");
			LOG = Logger.getLogger(MerchantPaymentProcessor.class);
		} catch (Exception e) {
			System.err.println("Failed to initilise logger for " + MerchantPaymentProcessor.class);
			e.printStackTrace(System.err);
		}
	}

	// @PostConstruct
	// public void initialize() {
	// try {
	// jdbcConnection = zetdcDataSource.getConnection();
	// jdbcConnection.setAutoCommit(false);
	// } catch (SQLException e) {
	// e.printStackTrace(System.err);
	// }
	// }

	
	private Connection getConnection() throws Exception{
		
		Connection jdbcConnection = null;
		try {
			Context context = new InitialContext();
			zetdcDataSource = (DataSource) context.lookup("jdbc/ZETDC");
			LOG.debug("Datasource found : " + zetdcDataSource);
		
			jdbcConnection = zetdcDataSource.getConnection("esolutions", "zetdc2012");
//			jdbcConnection = zetdcDataSource.getConnection();
			LOG.debug("Connection found : " + jdbcConnection);
			
		} catch (NamingException ne) {
			LOG.debug("Unable to lookup datasource. " + ne.getMessage());
		} catch (SQLException sqlex) {
			LOG.debug("Unable to open database connection. " + sqlex.getMessage());
			sqlex.printStackTrace();
			throw new Exception(sqlex);
		}
		
		return jdbcConnection;
	}
	
//	@PreDestroy
//	public void cleanup() {
//		try {
//			if (jdbcConnection != null) {
//				jdbcConnection.close();
//			}
//		} catch (SQLException e) {
//			e.printStackTrace(System.err);
//		}
//	}

	public PaymentResponse postAccountValidation(PaymentRequest validationRequest) {
		Connection con = null;
		
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setPaymentRequest(validationRequest);
		
		try {
			// utx.begin();
			con = getConnection();
			if (this.validateAccount(con, validationRequest.getAccountNumber())) {
				LOG.debug("Queury executed and returned result : continue : as success");
				paymentResponse.setResponseCode(InterfaceConstants.RC_OK);
				paymentResponse.setNarrative("Account Validation successful.");
			} else {
				LOG.debug("Query executed and returned NOTHING : continue : as failure");
				paymentResponse.setResponseCode("91");
				paymentResponse.setNarrative("Account with account number "+validationRequest.getAccountNumber()+" was not found.");
			}
			// utx.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.debug("Query threw SQL Exception : continue : as failure");
			paymentResponse.setResponseCode("91");
			paymentResponse.setNarrative("Merchant system currently unavailabe try again later.");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.debug("Queury threw Exception : continue : as failure");
			paymentResponse.setResponseCode("91");
			paymentResponse.setNarrative("Merchant system currently unavailabe try again later.");
		}finally {
			try {
				if (con != null)
					con.close();
			} catch (Exception e1) {
			}
		}
		return paymentResponse;
	}

	public PaymentResponse postCreditTransaction(PaymentRequest paymentRequest) {
		
		Connection con = null;
		
		String creditQuery = "INSERT INTO CUST_PAYMENTS (Zim_trans_id, Pay_zsw_trans_id, Pay_cust_Acc, Pay_Amount," + " Pay_Date, Pay_Form, Pay_Bank, Pay_Branch, Pay_Name, " +
				" Pay_Proc_ind ) " + " VALUES (zim_trans_id.nextval," + paymentRequest.getMerchantRef() + ", " + paymentRequest.getAccountNumber() + "," + paymentRequest.getAmount() + ", SYSDATE, 2," + paymentRequest.getPaymentBank() + "," + paymentRequest.getPaymentBranch() + ",'" + paymentRequest.getCustomerName() + "', 2)";
		
		LOG.debug("QUERY : " + creditQuery);
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setPaymentRequest(paymentRequest);
		Statement statement = null;
		LOG.debug("Merchant Ref : " + paymentRequest.getMerchantRef());
		int result;
		try {
			// utx.begin();
			con = getConnection();
			if(validateAccount(con,paymentRequest.getAccountNumber())){
				statement = con.createStatement();
				result = statement.executeUpdate(creditQuery);
				if (result > 0) {
					LOG.debug("Query executed and returned result : continue : as success");
					paymentResponse.setResponseCode(InterfaceConstants.RC_OK);
					paymentResponse.setNarrative("Credit Transaction Successful.");
				} else {
					LOG.debug("Query executed and returned NOTHING : continue : as failure");
					paymentResponse.setResponseCode("91");
					paymentResponse.setNarrative("Credit Transaction Failed : Account "+paymentRequest.getAccountNumber()+" does not exist");
				}
			}else {
				LOG.debug("Acount validation failed : continue : as failure");
				paymentResponse.setResponseCode("91");
				paymentResponse.setNarrative("Credit Transaction Failed : Account "+paymentRequest.getAccountNumber()+" does not exist");
			}
			
			// utx.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.debug("Query threw SQL Exception : continue : as failure");
			paymentResponse.setResponseCode("91");
			paymentResponse.setNarrative("Credit Transaction Failed : System currently unavailable");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.debug("SOME OTHER Exception thrown : continue : as failure");
			paymentResponse.setResponseCode("91");
			paymentResponse.setNarrative("Credit Transaction Failed : System currently unavailable");
		}finally {
			try {
				if (con != null)
					con.close();
			} catch (Exception e1) {
			}
		}
		LOG.debug("Done processing a credit request...");
		return paymentResponse;
	}

	public PaymentResponse postReversalTransaction(PaymentRequest paymentRequest) {
		String reversalQuery = "INSERT INTO CUST_PAYMENTS_REVERSALS (Pay_zsw_trans_id, Pay_cust_Acc, Pay_Amount, Pay_Date, Pay_Form, Pay_Bank, Pay_Branch, Pay_Name )" + " VALUES ('" + paymentRequest.getPaymentId() + "', " + paymentRequest.getAccountNumber() + "," + paymentRequest.getAmount() + ", SYSDATE, 2," + +paymentRequest.getPaymentBank() + "," + paymentRequest.getPaymentBranch() + ",'" + paymentRequest.getCustomerName() + "')";
		LOG.debug("QUERY : " + reversalQuery);
		
		Connection con = null;
		
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setPaymentRequest(paymentRequest);
		Statement statement = null;
		int result;
		try {
			// utx.begin();
			con = getConnection();
			statement = con.createStatement();
			result = statement.executeUpdate(reversalQuery);
			if (result > 0) {
				LOG.debug("Queury executed and returned result : continue : as success");
				paymentResponse.setResponseCode(InterfaceConstants.RC_OK);
				paymentResponse.setNarrative("Reversal Transaction successful");
			} else {
				LOG.debug("Queury executed and returned NOTHING : continue : as failure");
				paymentResponse.setResponseCode("91");
				paymentResponse.setNarrative("No account held");
			}
			// utx.commit();
		} catch (SQLException e) {
			LOG.debug("Queury threw SQL Exception : continue : as failure");
			paymentResponse.setResponseCode("91");
			paymentResponse.setNarrative("Reversal Processing error");
		} catch (Exception e) {
			LOG.debug("Queury threw Exception : continue : as failure");
			paymentResponse.setResponseCode("91");
			paymentResponse.setNarrative("Reversal Processing error");
		}finally {
			try {
				if (con != null)
					con.close();
			} catch (Exception e1) {
			}
		}
		return paymentResponse;
	}
	
	private boolean validateAccount(Connection con ,String accountNumber) throws Exception{
		
		String validationQuery = "SELECT * FROM ZSW_CUST_DETAILS@ICS WHERE NIS_RAD = '" + accountNumber + "'";
		LOG.debug("QUERY : " + validationQuery);
		Statement statement = null;
		ResultSet resultSet = null;
			// utx.begin();
		statement = con.createStatement();
		resultSet = statement.executeQuery(validationQuery);
		if (resultSet.next()) {
			LOG.debug("Queury executed and returned result : continue : as success");
			return true;
		} else {
			LOG.debug("Query executed and returned NOTHING : continue : as failure");
			return false;
		}
	}
	
	public static void main(String ...args){
		try{
			
			LOG.debug(new MerchantPaymentProcessor().getConnection());
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
