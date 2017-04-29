package tests.dom4j;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ValidationTest {

    public static final String ENCODING = "UTF-8";
    public static final String VALID_XML_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<configs-list>\n"
            + "</configs-list>";
    public static final String VALID_XML_2 = "<configs-list>\n"
            + "</configs-list>";
    public static final String FATAL_XML_1 = "";
    public static final String FATAL_XML_2 = "<root><asd</root>";

    @Test
    public void testValidationErrorHandler() throws DocumentException, UnsupportedEncodingException {
        ValidationErrorHandler errorHandler = new ValidationErrorHandler();
        SAXReader reader = new SAXReader();
        reader.setValidation(false);
        reader.setErrorHandler(errorHandler);

        reader.read(new ByteArrayInputStream(VALID_XML_1.getBytes(ENCODING)));
        assertEquals(0, errorHandler.getStatus());

        errorHandler.reset();
        reader.read(new ByteArrayInputStream(VALID_XML_2.getBytes(ENCODING)));
        assertEquals(0, errorHandler.getStatus());

        errorHandler.reset();
        try {
            reader.read(new ByteArrayInputStream(FATAL_XML_1.getBytes(ENCODING)));
            fail();
        } catch (DocumentException ex) {
        }
        assertEquals(ValidationErrorHandler.FATAL, errorHandler.getStatus());

        errorHandler.reset();
        try {
            reader.read(new ByteArrayInputStream(FATAL_XML_2.getBytes(ENCODING)));
            fail();
        } catch (DocumentException ex) {
        }
        assertEquals(ValidationErrorHandler.FATAL, errorHandler.getStatus());
    }

    @Test
    public void testValidation() throws DocumentException, UnsupportedEncodingException {
        ValidationErrorHandler errorHandler = new ValidationErrorHandler();
        SAXReader reader = new SAXReader();
        reader.setValidation(false);

        reader.read(new ByteArrayInputStream(VALID_XML_1.getBytes(ENCODING)));

        reader.read(new ByteArrayInputStream(VALID_XML_2.getBytes(ENCODING)));

        errorHandler.reset();
        try {
            reader.read(new ByteArrayInputStream(FATAL_XML_1.getBytes(ENCODING)));
            fail();
        } catch (DocumentException ex) {
        }

        errorHandler.reset();
        try {
            reader.read(new ByteArrayInputStream(FATAL_XML_2.getBytes(ENCODING)));
            fail();
        } catch (DocumentException ex) {
        }
    }
}
