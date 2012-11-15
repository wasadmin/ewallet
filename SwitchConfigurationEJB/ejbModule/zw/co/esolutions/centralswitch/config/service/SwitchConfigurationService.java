package zw.co.esolutions.centralswitch.config.service;
import java.util.List;

import javax.ejb.Local;
import javax.jws.WebParam;
import javax.jws.WebService;

import zw.co.esolutions.centralswitch.config.model.ConfigInfo;

@Local
@WebService(name = "SwitchConfigurationService")
public interface SwitchConfigurationService {

	 List<ConfigInfo> getConfigInfoByUSSDCode(@WebParam(name = "ussdCode") String ussdCode) throws Exception;

	 List<ConfigInfo> getConfigInfoBySMSCode(@WebParam(name = "smsCode") String smsCode) throws Exception;

	ConfigInfo createConfigInfo(@WebParam(name = "bankConfigInfo") ConfigInfo configInfo) throws Exception;

	ConfigInfo editConfigInfo(@WebParam(name = "bankConfigInfo") ConfigInfo configInfo) throws Exception;

	ConfigInfo approveConfigInfo(@WebParam(name = "bankConfigInfo") ConfigInfo configInfo) throws Exception;

	ConfigInfo deleteConfigInfo(@WebParam(name = "bankConfigInfo") ConfigInfo configInfo) throws Exception;
	
	ConfigInfo findConfigInfoByOwnerId(@WebParam(name = "ownerId") String ownerId) throws Exception;
}
