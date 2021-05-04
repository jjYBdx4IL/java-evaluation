package org.dom4j;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import org.dom4j.io.SAXReader;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jjYBdx4IL.utils.xml.XMLUtils;

/**
 *
 * @author jjYBdx4IL
 */
public class VisitorTest {

    private static final Logger LOG = LoggerFactory.getLogger(VisitorTest.class);

    @Test
    public void test() throws Exception {
        Document dom = new SAXReader().read(new ByteArrayInputStream("<a>\n<b> </b>\n<b/></a>".getBytes("UTF-8")));

        LOG.info(dom.asXML());
        assertEquals("<a>\n<b> </b>\n<b/></a>", XMLUtils.stripXMLHeader(dom.asXML()));

        // remove whitespace if there are xml nodes having the same parent
        Visitor visitor = new VisitorSupport() {
            
            @Override
            public void visit(Text node) {
                LOG.info("Text>>" + node.toString());
            }
            @Override
            public void visit(CDATA node) {
                LOG.info("CDATA>>" + node.toString());
            }
            @Override
            public void visit(Attribute node) {
                LOG.info("Attribute>>" + node.toString());
            }
            @Override
            public void visit(Document node) {
                LOG.info("Document>>" + node.toString());
            }
            @Override
            public void visit(Element element) {
                LOG.info("Element>>"+element.toString());
            }
        };

        dom.accept(visitor);

        LOG.info(dom.asXML());
        assertEquals("<a>\n<b> </b>\n<b/></a>", XMLUtils.stripXMLHeader(dom.asXML()));
    }
}
