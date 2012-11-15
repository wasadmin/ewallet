package zw.co.esolutions.ewallet.referralservices.service;
import java.util.Date;
import java.util.List;
import javax.jws.WebService;

import zw.co.esolutions.ewallet.enums.ReferralStatus;
import zw.co.esolutions.ewallet.referralservices.model.Referral;
import zw.co.esolutions.ewallet.referralservices.model.ReferralConfig;
import zw.co.esolutions.ewallet.referralservices.model.ReferralState;

@WebService(name="ReferralService")
public interface ReferralService {

	Referral createReferral(Referral referral) throws Exception;

	String deleteReferral(Referral referral, String userName) throws Exception;

	Referral updateReferral(Referral referral, String userName) throws Exception;

	Referral findReferralById(String id);

	List<Referral> getReferral();

	List<Referral> getReferralByReferrerMobileId(String referrerMobileId);

	List<Referral> getReferralByReferredMobile(String referredMobile);

	List<Referral> getReferralByCode(int code);

	List<Referral> getReferralByStatus(ReferralStatus status);

	List<Referral> getReferralByDateCreated(Date dateCreated);

	Referral getReferralByReferredMobileAndCode(String referredMobile, int code)
			throws Exception;

	List<Referral> getReferralByReferredMobileAndStatus(String referredMobile,
			ReferralStatus status) throws Exception;

	ReferralState createReferralState(ReferralState referralState)
			throws Exception;

	String deleteReferralState(ReferralState referralState) throws Exception;

	ReferralState updateReferralState(ReferralState referralState)
			throws Exception;

	ReferralState findReferralStateById(String id);

	List<ReferralState> getReferralState();

	List<ReferralState> getReferralStateByReferral(String referral_id);

	List<ReferralState> getReferralStateByStatus(ReferralStatus status);

	List<ReferralState> getReferralStateByDateCreated(Date dateCreated);

	String promoteReferralState(Referral referral, ReferralStatus status)
			throws Exception;

	ReferralConfig createReferralConfig(ReferralConfig referralConfig, String userName)
			throws Exception;

	String deleteReferralConfig(ReferralConfig referralConfig, String userName) throws Exception;

	ReferralConfig updateReferralConfig(ReferralConfig referralConfig, String userName)
			throws Exception;

	ReferralConfig findReferralConfigById(String id);

	List<ReferralConfig> getReferralConfig();

	List<ReferralConfig> getReferralConfigByDateFrom(Date dateFrom);

	List<ReferralConfig> getReferralConfigByDateTo(Date dateTo);

	ReferralConfig getActiveReferralConfig();

	List<ReferralConfig> getReferralConfigBetweenDates(Date dateFrom,
			Date dateTo);

}
