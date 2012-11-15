package zw.co.esolutions.ussd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;

import noNamespace.MethodCallDocument;
import noNamespace.MethodResponseDocument;
import noNamespace.MemberDocument.Member;
import noNamespace.MethodCallDocument.MethodCall;
import noNamespace.MethodResponseDocument.MethodResponse;
import noNamespace.StructDocument.Struct;
import noNamespace.ValueDocument.Value;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			File file = new File("/home/prince/request.xml");
			
			Logger logger = Logger.getLogger(Test.class);
			PropertyConfigurator.configure("/home/prince/ussd.log.properties");

			//BasicConfigurator.configure();


			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			 String line = null;
	         StringBuffer buffer = new StringBuffer();
	         while ((line = reader.readLine()) != null) {
	             buffer.append(line);
	         }
			MethodCallDocument methodCallDocument = MethodCallDocument.Factory.parse(buffer.toString());
			MethodCall mc = methodCallDocument.getMethodCall();
			//System.out.println(" ********************* method Name " + mc.getMethodName());
			Member[] members = mc.getParams().getParam().getValue().getStruct().getMemberArray();
			for (Member member : members) {
				logger.info("************ key " + member.getName()  + " value " + member.getValue().getString());
			}
			
			
		 
			MethodResponseDocument mrd = MethodResponseDocument.Factory.newInstance();
			MethodResponse methodResponse = mrd.addNewMethodResponse();
			Struct struc = methodResponse.addNewParams().addNewParam().addNewValue().addNewStruct();
			Member transactionID = struc.addNewMember();
			transactionID.setName("TransactionId");
			Value value = transactionID.addNewValue();
			value.setString("122345");
			
			Member transactionTime = struc.addNewMember();
			transactionTime.setName("TransactionTime");
			Value value2 = transactionTime.addNewValue();
			value2.setDateTimeIso8601("20090526T15:44:34+0000");
			SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ssZ");
			String date = value2.getDateTimeIso8601().replaceAll("\\+0([0-9]){1}\\:00", "+0$100");
			logger.debug("********** date "+ISO8601DATEFORMAT.parse(date));

			logger.info("**************** xml response " + mrd.xmlText());
		}catch(Exception e){
			e.printStackTrace();
		}

	}

}
