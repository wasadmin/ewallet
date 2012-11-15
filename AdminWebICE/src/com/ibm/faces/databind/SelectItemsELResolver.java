package com.ibm.faces.databind;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;

public class SelectItemsELResolver extends ELResolver {
	
	public Object getValue(ELContext elContext, Object base, Object property) {
		if(base == null && property == null) {
			return null;
		}
		
		if(base == null) {
			if(property.equals("selectitems")) {
				elContext.setPropertyResolved(true);
				SelectItemsWrapper wrapperObj = new SelectItemsWrapper();
				return wrapperObj;
			}
		}
		else {
			if(base instanceof SelectItemsWrapper) {
				elContext.setPropertyResolved(true);
				SelectItemsWrapper wrapper = (SelectItemsWrapper) base;
				String segment = (String) property;
				if(!"toArray".equals(segment)) {
					wrapper.addExpressionSegment(segment);
					return wrapper;
				} else {
					return wrapper.toArray();
				}
			}
		}
		
		return null;
	}
	
	public void setValue(ELContext elContext, Object base, Object property, Object value) {
	}

	public Class<?> getType(ELContext elContext, Object base, Object property) {
		if(base instanceof SelectItemsWrapper) {
			elContext.setPropertyResolved(true);
			return SelectItemsWrapper.class;
		}
		return null;
	}

	public boolean isReadOnly(ELContext elContext, Object base, Object property) {
		if(base instanceof SelectItemsWrapper) {
			elContext.setPropertyResolved(true);
			return true;
		}
		return false;
	}

	public Class<?> getCommonPropertyType(ELContext elContext, Object base) {
		return null;
	}
	
	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext elContext, Object base) {
		return null;
	}

}