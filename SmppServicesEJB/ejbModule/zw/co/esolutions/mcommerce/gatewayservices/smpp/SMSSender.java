package zw.co.esolutions.mcommerce.gatewayservices.smpp;
import javax.ejb.Remote;

@Remote
public interface SMSSender {

	public void sendMessage(String originator,String destination,String messageText) throws Exception;
}
