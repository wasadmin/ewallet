package zw.co.faces.ussdtestweb.util;
import java.util.Stack;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.el.VariableResolver;

@SuppressWarnings("unchecked")
public class CustomComponentVariableResolver extends VariableResolver {

	VariableResolver oldResolver = null;
	
	public CustomComponentVariableResolver(VariableResolver old) {
		oldResolver = old;
	}

	public Object resolveVariable(FacesContext context, String var) {
		if(var.equals("component")) {
			UIComponent component = null;
			
			UIViewRoot view = FacesContext.getCurrentInstance().getViewRoot();
			Stack stack = (Stack) view.getAttributes().get("com.ibm.faces.COMPOSITE_STACK");
			if(stack != null) {
				component = (UIComponent) stack.peek();
			}

			if(component != null) {
				return component;
			}
		}
		return oldResolver.resolveVariable(context, var);
	}

}
