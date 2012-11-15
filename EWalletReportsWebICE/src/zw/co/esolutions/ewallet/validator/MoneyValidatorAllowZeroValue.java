package zw.co.esolutions.ewallet.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public class MoneyValidatorAllowZeroValue implements Validator {
	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		double amt = 0.00;
		if (value != null) {
			amt = Double.parseDouble(value.toString());
		}
		if (amt < 0.00) {
			FacesMessage msg = new FacesMessage();
			msg.setDetail("Negative Amount");
			msg.setSummary("Negative Amount");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}
	}
}
