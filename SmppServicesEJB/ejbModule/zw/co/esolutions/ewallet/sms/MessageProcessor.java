package zw.co.esolutions.ewallet.sms;
import javax.ejb.Local;
import org.smslib.InboundMessage;

@Local
public interface MessageProcessor {

	void processMessage(InboundMessage msg);

	void pushMessage(String message);

}
