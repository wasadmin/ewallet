package zw.co.esolutions.ewallet.download.session;
import java.util.List;

import javax.ejb.Local;

import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.contactdetailsservices.service.ContactDetails;
import zw.co.esolutions.ewallet.merchantservices.service.Merchant;
import zw.co.esolutions.ewallet.msg.DownloadResponse;
import zw.co.esolutions.ewallet.msg.SynchronisationResponse;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessage;
import zw.co.esolutions.mcommerce.msg.BankRegistrationMessage.MessageCommand.Enum;

@Local
public interface DownloadProcessUtil {

	SynchronisationResponse processRegMessage(
			DownloadResponse res, Enum action)
			throws Exception;

	
	List<Bank> getAllBanks();

	List<Merchant> getAllMerchants();

	ContactDetails findContactDetailsById(String contactDetailsId);

}
