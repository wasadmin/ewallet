package zw.co.esolutions.mcommerce.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

import org.apache.tools.ant.filters.StringInputStream;

import zw.co.esolutions.config.bank.BankInfo;
import zw.co.esolutions.config.merchant.MerchantInfo;


public class ISOMarshaller {

	public static String marshal(ISOMsg msg, MetaData meta) throws ISOMarshallerException {
		StringWriter stringWriter;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance("zw.co.esolutions.mcommerce.xml");
			Marshaller m = jaxbContext.createMarshaller();
			Messages messages = new Messages();
			if (msg != null)
				messages.setIsoMsg(msg);
			if (meta != null)
				messages.setMetadata(meta);
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			stringWriter = new StringWriter();
			m.marshal(messages, stringWriter);
		} catch (PropertyException e) {
			throw new ISOMarshallerException("Error setting marshaller properties", e);
		} catch (JAXBException e) {
			throw new ISOMarshallerException(e);
		}
		return stringWriter.toString();
	}
	
	public static Messages unmarshal(String xml) throws ISOMarshallerException {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance("zw.co.esolutions.mcommerce.xml");
			// create an Unmarshaller
			Unmarshaller u = jaxbContext.createUnmarshaller();
			// unmarshal the XML document into Java object
			Messages messages = (Messages) u.unmarshal(new StringInputStream(xml));
			return messages;
		} catch (JAXBException e) {
			throw new ISOMarshallerException(e);
		}
	}

	public static ISOMsg unmarshalISO(String xml) throws ISOMarshallerException {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance("zw.co.esolutions.mcommerce.xml");
			// create an Unmarshaller
			Unmarshaller u = jaxbContext.createUnmarshaller();
			// unmarshal the XML document into Java object
			Messages messages = (Messages) u.unmarshal(new StringInputStream(xml));
			return messages.getIsoMsg();
		} catch (JAXBException e) {
			throw new ISOMarshallerException(e);
		}
	}

	public static MetaData unmarshalMeta(String xml) throws ISOMarshallerException {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance("zw.co.esolutions.mcommerce.xml");
			// create an Unmarshaller
			Unmarshaller u = jaxbContext.createUnmarshaller();
			// unmarshal the XML document into Java object
			Messages messages = (Messages) u.unmarshal(new StringInputStream(xml));
			return messages.getMetadata();
		} catch (JAXBException e) {
			throw new ISOMarshallerException(e);
		}
	}


	public static MerchantInfo unmarshalMerchantInfo(String filePath) {
//		System.out.println("File Path : " + filePath);
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(MerchantInfo.class);
			// create an Unmarshaller
			Unmarshaller u = jaxbContext.createUnmarshaller();
			// unmarshal the XML document into Java object
			MerchantInfo merchantInfo = (MerchantInfo) u.unmarshal(new File(filePath));
			return merchantInfo;
		
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static BankInfo unmarshalBankInfo(String filePath) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(BankInfo.class);
			// create an Unmarshaller
			Unmarshaller u = jaxbContext.createUnmarshaller();
			// unmarshal the XML document into Java object
			BankInfo bankInfo = (BankInfo) u.unmarshal(new File(filePath));
			return bankInfo;
		
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
