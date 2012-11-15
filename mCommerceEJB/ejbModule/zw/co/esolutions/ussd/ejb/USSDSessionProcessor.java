package zw.co.esolutions.ussd.ejb;
import javax.ejb.Local;

@Local
public interface USSDSessionProcessor {
	
	String processUSSDRequest(String xml);

}
