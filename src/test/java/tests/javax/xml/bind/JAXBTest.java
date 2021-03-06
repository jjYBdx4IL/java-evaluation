package tests.javax.xml.bind;
/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2017 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.junit.Test;

import com.github.jjYBdx4IL.utils.xml.XMLUtils;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class JAXBTest {

    @Test
    public void testObject2XML() throws JAXBException {
        Customer customer = new Customer();
        customer.setId(101);
        customer.setName("some name");
        customer.setAge(55);
        customer.setName2("aBc");

        JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        // output pretty printed
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        jaxbMarshaller.marshal(customer, baos);

        assertTrue(XMLUtils.compareStrippedXml("<customer id=\"101\">\n"
                + "    <name>some name</name>\n"
                + "    <age>55</age>\n"
                + "    <name2>ABC</name2>\n"
                + "</customer>", baos.toString()));
    }

    @Test
    public void testXML2Object() throws JAXBException {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<customer id=\"101\">\n"
                + "    <age>55</age>\n"
                + "    <name>some name</name>\n"
                + "    <name2>aBc</name2>\n"
                + "</customer>\n";

        JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Customer customer = (Customer) jaxbUnmarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes()));

        assertEquals(101, customer.getId());
        assertEquals("some name", customer.getName());
        assertEquals(55, customer.getAge());
        assertEquals("ABC", customer.getName2());

    }

    @Test
    public void testObjectWithStringList2XML() throws JAXBException {
        CustomerWithStringList customer = new CustomerWithStringList();
        customer.setId(101);
        customer.setName("some name");
        customer.setAge(55);
        customer.setSomeList(new ArrayList<String>());
        customer.getSomeList().add("testStr1");
        customer.getSomeList().add("testStr2");

        JAXBContext jaxbContext = JAXBContext.newInstance(CustomerWithStringList.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        // output pretty printed
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        jaxbMarshaller.marshal(customer, baos);

        assertTrue(XMLUtils.compareStrippedXml("<customerWithStringList id=\"101\">\n"
                + "    <name>some name</name>\n"
                + "    <age>55</age>\n"
                + "    <someList>testStr1</someList>\n"
                + "    <someList>testStr2</someList>\n"
                + "</customerWithStringList>", baos.toString()));
    }

    @Test
    public void testObjectWithObjectList2XML() throws JAXBException {
        CustomerWithCustomerList customer = new CustomerWithCustomerList();
        customer.setId(101);
        customer.setName("some name");
        customer.setAge(55);
        customer.setRefs(new ArrayList<Customer>());

        Customer customer1 = new Customer();
        customer1.setId(102);
        customer1.setName("cust1name");
        customer1.setAge(56);
        customer.getRefs().add(customer1);

        JAXBContext jaxbContext = JAXBContext.newInstance(CustomerWithCustomerList.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        // output pretty printed
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        jaxbMarshaller.marshal(customer, baos);

        assertTrue(XMLUtils.compareStrippedXml("<customerWithCustomerList id=\"101\">\n"
                + "    <name>some name</name>\n"
                + "    <age>55</age>\n"
                + "    <customer id=\"102\">\n"
                + "        <name>cust1name</name>\n"
                + "        <age>56</age>\n"
                + "    </customer>\n"
                + "</customerWithCustomerList>", baos.toString()));
    }

    /**
     * JAXB ignores XML entries for which there is no class mapping.
     * 
     * @throws Exception
     */
    @Test
    public void testParseUnknownProperty() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<customer id=\"101\">\n"
                + "    <agebbb>55</agebbb>\n"
                + "</customer>\n";

        JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Customer customer = (Customer) jaxbUnmarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes()));

        assertEquals(101, customer.getId());
        assertNull(customer.getName());
        assertEquals(0, customer.getAge());
    }

    @Test(expected = UnmarshalException.class)
    public void testFailOnUnknownProperty() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<customer id=\"101\">\n"
                + "    <agebbb>55</agebbb>\n"
                + "</customer>\n";

        JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        
        jaxbUnmarshaller.setEventHandler(new ValidationEventHandler () {

            @Override
            public boolean handleEvent(ValidationEvent event) {
                return false;
            }

        });
        
        jaxbUnmarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes()));
    }
}
