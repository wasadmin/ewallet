package zw.co.esolutions.ewallet.adminweb.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;

public class BranchNameConverter implements Converter{

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		String branchName = "";
		try {
			BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
			BankBranch branch = bankService.findBankBranchById((String)value);
			if(branch!=null){
				branchName = branch.getName();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return branchName;
	}

}
