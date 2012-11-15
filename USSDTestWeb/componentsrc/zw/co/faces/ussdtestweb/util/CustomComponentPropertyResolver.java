package zw.co.faces.ussdtestweb.util;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.PropertyResolver;
import javax.faces.el.ValueBinding;

@SuppressWarnings("unchecked")
public class CustomComponentPropertyResolver extends PropertyResolver {

	PropertyResolver oldPropertyResolver = null;
	
	public CustomComponentPropertyResolver(PropertyResolver old) {
		super();
		oldPropertyResolver = old;
	}

	public Object getValue(Object base, Object property)
			throws EvaluationException, PropertyNotFoundException {
		if(base instanceof UIComponent) {
			if(property.toString().equals("clientId")) {
				return ((UIComponent)base).getClientId(FacesContext.getCurrentInstance());
			}
			return ((UIComponent)base).getAttributes().get(property);
		}
		return oldPropertyResolver.getValue(base, property);
	}

	public Object getValue(Object base, int index) throws EvaluationException,
			PropertyNotFoundException {
		return oldPropertyResolver.getValue(base, index);
	}

	public void setValue(Object base, Object property, Object value)
			throws EvaluationException, PropertyNotFoundException {
		if(base instanceof UIComponent) {
			ValueBinding vb = ((UIComponent)base).getValueBinding(property.toString());
			if(vb != null) {
				vb.setValue(FacesContext.getCurrentInstance(), value);
				return;
			}
			((UIComponent)base).getAttributes().put(property.toString(), value);
			return;
		}
		oldPropertyResolver.setValue(base, property, value);
	}

	public void setValue(Object base, int index, Object value)
			throws EvaluationException, PropertyNotFoundException {
		oldPropertyResolver.setValue(base, index, value);
	}

	public boolean isReadOnly(Object base, Object property)
			throws EvaluationException, PropertyNotFoundException {
		if(base instanceof UIComponent) {
			return oldPropertyResolver.isReadOnly(((UIComponent)base).getAttributes(), property);
		}
		return oldPropertyResolver.isReadOnly(base, property);
	}

	public boolean isReadOnly(Object base, int index)
			throws EvaluationException, PropertyNotFoundException {
		return oldPropertyResolver.isReadOnly(base, index);
	}

	public Class getType(Object base, Object property) throws EvaluationException,
			PropertyNotFoundException {
		if(base instanceof UIComponent) {
			return oldPropertyResolver.getType(((UIComponent)base).getAttributes(), property);
		}
		return oldPropertyResolver.getType(base, property);
	}

	public Class getType(Object base, int index) throws EvaluationException,
			PropertyNotFoundException {
		return oldPropertyResolver.getType(base, index);
	}

}
