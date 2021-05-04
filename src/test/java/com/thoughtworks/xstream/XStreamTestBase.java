package com.thoughtworks.xstream;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

/**
 * Derived from: http://www.tutorialspoint.com/xstream/xstream_first_application.htm
 * @author Github jjYBdx4IL Projects
 *
 */
public class XStreamTestBase {
	
	protected Student getStudentDetails() {

		Student student = new Student();

		student.setFirstName("Mahesh");
		student.setLastName("Parashar");
		student.setRollNo(1);
		student.setClassName("1st");

		Address address = new Address();

		address.setArea("H.No. 16/3, Preet Vihar.");
		address.setCity("Delhi");
		address.setState("Delhi");
		address.setCountry("India");
		address.setPincode(110012);

		student.setAddress(address);

		return student;
	}

	public static String formatXml(String xml) {

		try{
			Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();

			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			Source xmlSource = new SAXSource(new InputSource(new ByteArrayInputStream(xml.getBytes())));
			StreamResult res =  new StreamResult(new ByteArrayOutputStream());            

			serializer.transform(xmlSource, res);

			return new String(((ByteArrayOutputStream)res.getOutputStream()).toByteArray());

		}catch(Exception e){
			return xml;
		}
	}
}