package tests.dom4j;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
@SuppressWarnings("unchecked")
public class ParserTest {

    public static final String ENCODING = "UTF-8";
    public static final String XML_INPUT_1 = "<configs-list>\n"
            + "  <data>\n"
            + "    <configs-list-item>\n"
            + "      <resourceURI>http://localhost:3129/nexus/service/local/configs/3</resourceURI>\n"
            + "      <name>security-configuration.xml</name>\n"
            + "    </configs-list-item>\n"
            + "    <configs-list-item>\n"
            + "      <resourceURI>http://localhost:3129/nexus/service/local/configs/2</resourceURI>\n"
            + "      <name>logback.properties</name>\n"
            + "    </configs-list-item>\n"
            + "    <configs-list-item>\n"
            + "      <resourceURI>http://localhost:3129/nexus/service/local/configs/10</resourceURI>\n"
            + "      <name>capabilities.xml</name>\n"
            + "    </configs-list-item>\n"
            + "    <configs-list-item>\n"
            + "      <resourceURI>http://localhost:3129/nexus/service/local/configs/1</resourceURI>\n"
            + "      <name>security.xml</name>\n"
            + "    </configs-list-item>\n"
            + "    <configs-list-item>\n"
            + "      <resourceURI>http://localhost:3129/nexus/service/local/configs/7</resourceURI>\n"
            + "      <name>logback-events.xml</name>\n"
            + "    </configs-list-item>\n"
            + "    <configs-list-item>\n"
            + "      <resourceURI>http://localhost:3129/nexus/service/local/configs/6</resourceURI>\n"
            + "      <name>logback-nexus.xml</name>\n"
            + "    </configs-list-item>\n"
            + "    <configs-list-item>\n"
            + "      <resourceURI>http://localhost:3129/nexus/service/local/configs/5</resourceURI>\n"
            + "      <name>logback-healthcheck.xml</name>\n"
            + "    </configs-list-item>\n"
            + "    <configs-list-item>\n"
            + "      <resourceURI>http://localhost:3129/nexus/service/local/configs/4</resourceURI>\n"
            + "      <name>logback.xml</name>\n"
            + "    </configs-list-item>\n"
            + "    <configs-list-item>\n"
            + "      <resourceURI>http://localhost:3129/nexus/service/local/configs/9</resourceURI>\n"
            + "      <name>nexus.xml</name>\n"
            + "    </configs-list-item>\n"
            + "    <configs-list-item>\n"
            + "      <resourceURI>http://localhost:3129/nexus/service/local/configs/8</resourceURI>\n"
            + "      <name>lvo-plugin.xml</name>\n"
            + "    </configs-list-item>\n"
            + "  </data>\n"
            + "</configs-list>";

    @Test
    public void test1() throws DocumentException, UnsupportedEncodingException {
        Document dom = new SAXReader().read(new ByteArrayInputStream(XML_INPUT_1.getBytes(ENCODING)));

        List<Element> list = (List<Element>) dom.getRootElement().elements("data");
        assertEquals(1, list.size());

        list = (List<Element>) dom.getRootElement().elements("configs-list-item");
        assertEquals(0, list.size());

        list = (List<Element>) dom.getRootElement().elements("data/configs-list-item");
        assertEquals(0, list.size());
    }

    @Test
    public void test2() throws DocumentException, UnsupportedEncodingException {
        Document dom = new SAXReader().read(new ByteArrayInputStream(XML_INPUT_1.getBytes(ENCODING)));

        List<Element> list = (List<Element>) dom.getRootElement().elements("data");
        Element e = list.get(0);

        list = (List<Element>) e.elements("configs-list-item");
        assertEquals(10, list.size());
    }
}
