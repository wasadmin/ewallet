package zw.co.faces.ussdtestweb.util;
import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodBinding;
import javax.faces.el.MethodNotFoundException;

@SuppressWarnings("unchecked")
public class StaticMethodBinding extends MethodBinding implements StateHolder {
	
	Object result;

	public StaticMethodBinding(){
	}

	public StaticMethodBinding(Object arg){
		this.result = arg;
	}

	public Object invoke(FacesContext arg0, Object[] arg1) throws EvaluationException, MethodNotFoundException {
		if(result != null){
			return result.toString();
		}
		return null;
	}

	public Class getType(FacesContext arg0) throws MethodNotFoundException {
		return String.class;
	}
	
	public String getExpressionString() {
		if(result != null){
			return result.toString();
		}
		return null;
	}

	public void restoreState(FacesContext context, Object _state) {
		Object _values[] = (Object[]) _state;		
		this.result= (java.lang.String) _values[0];
	}

	public Object saveState(FacesContext arg0) {
		Object _values[] = new Object[1];
		_values[0] = result;		
		return _values;
	}
	
	public boolean isTransient() {
		return false;
	}

	public void setTransient(boolean arg0) {
	}
}
