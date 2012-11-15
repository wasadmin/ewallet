package zw.co.esolutions.ewallet.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import zw.co.esolutions.ewallet.bankservices.service.Bank;
import zw.co.esolutions.ewallet.bankservices.service.BankBranch;
import zw.co.esolutions.ewallet.bankservices.service.BankServiceSOAPProxy;
import zw.co.esolutions.ewallet.process.TransactionType;

public class TransactionLocationDetailsConverter implements Converter {
	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
	
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		String res = null; 
		BankServiceSOAPProxy bankService = new BankServiceSOAPProxy();
		try {
			if(value == null || "".equalsIgnoreCase((String)value)) {
				return "";
			}
			BankBranch bb = bankService.findBankBranchById((String)value);
			if(bb != null) {
				res = bb.getBank().getName()+" : "+bb.getName();
			} else {
				Bank bk = bankService.findBankById((String)value);
				if(bk != null) {
					res = bk.getName();
				} else {
					res = (String)value;
					loop:for(TransactionType type : getApplicableTxnTypes()) {
						if(res.contains(type.toString())) {
							res = res.replace(type.toString(), "SMS Initiated");
							res = res.replace("Txns", "Txn");
							break loop;
						}
					}
					
				}
			}
			
		} catch (NullPointerException e) {
			
			return "";
		}  catch (Exception e) {
			
			res = "";
		}
		if(res == null) {
			res = "";
		}
		return res;
	}
	
   private static TransactionType[] getApplicableTxnTypes() {
		
		return new TransactionType[] {TransactionType.DEPOSIT, TransactionType.WITHDRAWAL, TransactionType.WITHDRAWAL_NONHOLDER, 
				TransactionType.BANKACCOUNT_TO_BANKACCOUNT_TRANSFER, TransactionType.BANKACCOUNT_TO_EWALLET_TRANSFER, TransactionType.BANKACCOUNT_TO_NONHOLDER_TRANSFER, 
				TransactionType.EWALLET_TO_BANKACCOUNT_TRANSFER, TransactionType.EWALLET_TO_EWALLET_TRANSFER, TransactionType.EWALLET_TO_NON_HOLDER_TRANSFER, 
				TransactionType.BILLPAY, TransactionType.EWALLET_BILLPAY, TransactionType.TOPUP, TransactionType.EWALLET_TOPUP, 
				TransactionType.TARIFF, TransactionType.AGENT_CUSTOMER_DEPOSIT, TransactionType.AGENT_CUSTOMER_WITHDRAWAL, TransactionType.COMMISSION_TRANSFER, 
				TransactionType.DAYEND_OVERPOST, TransactionType.DAYEND_PAYOUTS, TransactionType.DAYEND_RECEIPTS, TransactionType.DAYEND_UNDERPOST};
		
	}

}


