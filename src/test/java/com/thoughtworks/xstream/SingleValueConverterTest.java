package com.thoughtworks.xstream;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Derived from: http://www.tutorialspoint.com/xstream/xstream_first_application.htm
 *
 * @author Github jjYBdx4IL Projects
 *
 */
public class SingleValueConverterTest extends XStreamTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(SingleValueConverterTest.class);

    @Test
    public void test() {

        SingleValueConverterTest tester = new SingleValueConverterTest();
        XStream xstream = new XStream(new StaxDriver());

        Student student = tester.getStudentDetails();

        xstream.autodetectAnnotations(true);
        xstream.registerConverter(new AddressConverter());

        //Object to XML Conversion
        String xml = xstream.toXML(student);
        LOG.info(formatXml(xml));

        assertTrue(xml.contains("<address>H.No. 16/3, Preet Vihar.;Delhi;Delhi;India;110012</address>"));

        //XML to Object Conversion
        Student student1 = (Student) xstream.fromXML(xml);
        LOG.info(student1.toString());

        assertEquals(student, student1);
    }

}
