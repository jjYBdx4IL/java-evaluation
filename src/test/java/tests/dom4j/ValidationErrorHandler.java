package tests.dom4j;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ValidationErrorHandler implements ErrorHandler {

    private int status = 0;
    public static final int WARNING = 1;
    public static final int ERROR = 2;
    public static final int FATAL = 4;

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        status |= WARNING;
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        status |= ERROR;
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        status |= FATAL;
    }

    public int getStatus() {
        return status;
    }

    public void reset() {
        status = 0;
    }
}
