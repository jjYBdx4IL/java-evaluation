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

import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Derived from: http://www.tutorialspoint.com/xstream/xstream_first_application.htm
 *
 * @author Github jjYBdx4IL Projects
 *
 */
public class AdvancedConverterTest extends XStreamTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(AdvancedConverterTest.class);

    @Test
    public void test() {

        AdvancedConverterTest tester = new AdvancedConverterTest();
        XStream xstream = new XStream(new StaxDriver());

        Student student = tester.getStudentDetails();

        XStream.setupDefaultSecurity(xstream);
        xstream.allowTypesByWildcard(new String[] {
            getClass().getPackage().getName() + ".**"
        });
        
        xstream.autodetectAnnotations(true);
        xstream.registerConverter(new AdvancedAddressConverter());

        //Object to XML Conversion
        String xml = xstream.toXML(student);
        LOG.info(formatXml(xml));

        assertTrue(xml.contains("area=\"H.No. 16/3, Preet Vihar.\""));

        //XML to Object Conversion
        Student student1 = (Student) xstream.fromXML(xml);
        LOG.info(student1.toString());

        assertEquals(student, student1);
    }

}
