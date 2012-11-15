package zw.co.esolutions.ewallet.util;

import java.util.Arrays;
import java.util.List;

import javax.faces.model.SelectItem;

import zw.co.esolutions.ewallet.enums.OwnerType;

public class JsfUtil {

	public static SelectItem[] getSelectItemsAsArray(List<?> entities, boolean selectOne) {
        int size = selectOne ? entities.size() + 1 : entities.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem("", "--Select--");
            i++;
        }
        for (Object x : entities) {
            items[i++] = new SelectItem(x, x.toString());
        }
        return items;
    }
	
	public static List<SelectItem> getSelectItemsAsList(List<?> entities, boolean selectOne) {
        int size = selectOne ? entities.size() + 1 : entities.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem("", "--Select--");
            i++;
        }
        for (Object x : entities) {
            items[i++] = new SelectItem(x, x.toString());
        }
        return (List<SelectItem>)Arrays.asList(items);
    }
	
	public static SelectItem[] getSelectItemsAsArray(Object[] entities, boolean selectOne) {
        int size = selectOne ? entities.length + 1 : entities.length;
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem("", "--Select--");
            i++;
        }
        for (Object x : entities) {
            items[i++] = new SelectItem(x, x.toString());
        }
        return items;
    }
	
	public static List<SelectItem> getSelectItemsAsList(Object[] entities, boolean selectOne) {
        int size = selectOne ? entities.length + 1 : entities.length;
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem("", "--Select--");
            i++;
        }
        for (Object x : entities) {
            items[i++] = new SelectItem(x, x.toString());
        }
        return (List<SelectItem>)Arrays.asList(items);
    }
	
	public static List<SelectItem> getCustomerSelectItemsAsList(Object[] entities, boolean selectOne) {
        int size = selectOne ? entities.length + 1 : entities.length;
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem("", "--All--");
            i++;
        }
        for (Object x : entities) {
            items[i++] = new SelectItem(x, x.toString());
        }
        return (List<SelectItem>)Arrays.asList(items);
    }
	
	
}
