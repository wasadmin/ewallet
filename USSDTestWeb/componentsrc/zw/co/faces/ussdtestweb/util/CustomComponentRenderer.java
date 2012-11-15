package zw.co.faces.ussdtestweb.util;
import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

@SuppressWarnings("unchecked")
public class CustomComponentRenderer extends Renderer {

    public boolean getRendersChildren() {
        return true;
    }

	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		writer.startElement("span", component);
		writer.writeAttribute("id", component.getClientId(context), "id");
	}

	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		writer.endElement("span");
	}

	public void encodeChildren(FacesContext context, UIComponent component)
			throws IOException {
		if (!component.isRendered()) {
			return;
		}
        Iterator kids = component.getChildren().iterator();
        while (kids.hasNext()) {
            encodeRecursive(context, (UIComponent) kids.next());
        }
    }

	public void encodeRecursive(FacesContext context, UIComponent component)
			throws IOException {
		if (!component.isRendered()) {
			return;
		}

		component.encodeBegin(context);
		if (component.getRendersChildren()) {
			component.encodeChildren(context);
		} else {
			Iterator i = component.getChildren().iterator();
			while (i.hasNext()) {
				UIComponent child = (UIComponent) i.next();
				encodeRecursive(context, child);
			}
		}
		component.encodeEnd(context);
	}
}
