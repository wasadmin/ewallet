package zw.co.datacentre.xml.bet;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

import org.apache.tools.ant.filters.StringInputStream;

public class ISO8583Marshaller {
	

	public static String marshal(ISOMsg msg, MetaData meta) throws Exception {
		StringWriter sw;
		try {
			JAXBContext jc = JAXBContext.newInstance("zw.co.datacentre.xml.bet");
			Marshaller m = jc.createMarshaller();
			Messages messages = new Messages();
			if (msg != null)
				messages.setIsoMsg(msg);
			if (meta != null)
				messages.setMetadata(meta);
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			sw = new StringWriter();
			m.marshal(messages, sw);
		} catch (PropertyException e) {
			throw new Exception("Error setting marshaller properties", e);
		} catch (JAXBException e) {
			throw new Exception(e);
		}
		return sw.toString();
	}

	public static ISOMsg unmarshalISO(String xml) throws Exception {
		try {
			JAXBContext jc = JAXBContext.newInstance("zw.co.datacentre.xml.bet");
			// create an Unmarshaller
			Unmarshaller u = jc.createUnmarshaller();
			// unmarshal the XML document into Java object
			Messages messages = (Messages) u.unmarshal(new StringInputStream(xml));
			return messages.getIsoMsg();
		} catch (JAXBException e) {
			throw new Exception(e);
		}
	}

	public static MetaData unmarshalMeta(String xml) throws Exception {
		try {
			JAXBContext jc = JAXBContext.newInstance("zw.co.datacentre.xml.bet");
			// create an Unmarshaller
			Unmarshaller u = jc.createUnmarshaller();
			// unmarshal the XML document into Java object
			Messages messages = (Messages) u.unmarshal(new StringInputStream(xml));
			return messages.getMetadata();
		} catch (JAXBException e) {
			throw new Exception(e);
		}
	}

}
