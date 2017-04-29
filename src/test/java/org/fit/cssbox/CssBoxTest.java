/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package org.fit.cssbox;

import com.github.jjYBdx4IL.test.AdHocHttpServer;
import com.github.jjYBdx4IL.test.Screenshot;
import com.github.jjYBdx4IL.utils.awt.AWTUtils;
import com.github.jjYBdx4IL.utils.env.Surefire;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.fit.cssbox.css.CSSNorm;
import org.fit.cssbox.css.DOMAnalyzer;
import org.fit.cssbox.io.DOMSource;
import org.fit.cssbox.io.DefaultDOMSource;
import org.fit.cssbox.io.DefaultDocumentSource;
import org.fit.cssbox.io.DocumentSource;
import org.fit.cssbox.io.StreamDocumentSource;
import org.fit.cssbox.layout.BrowserCanvas;
import org.junit.After;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * http://cssbox.sourceforge.net/manual/
 *
 * @author Github jjYBdx4IL Projects
 */
public class CssBoxTest {

	private static final Logger LOG = LoggerFactory.getLogger(CssBoxTest.class);
    private static AdHocHttpServer server = null;

    public static File getLocalExampleHomepageRoot() throws URISyntaxException {
        return new File(CssBoxTest.class.getResource("homepage_root").toURI());
    }

    @After
    public void after() throws Exception {
        if (server != null) {
            server.close();
            server = null;
        }
    }

    @Test
    public void testTransparentBackground() throws MalformedURLException, IOException, SAXException, InterruptedException, Exception {
        server = new AdHocHttpServer(getLocalExampleHomepageRoot());

        URL url = server.computeServerURL("/cssbox_homepage.html");
        DocumentSource src = new DefaultDocumentSource(url);
        //Parse the input document (replace this with your own parser if desired)
        DOMSource parser = new DefaultDOMSource(src);
        Document doc = parser.parse(); //doc represents the obtained DOM

        DOMAnalyzer da = new DOMAnalyzer(doc, url);
        da.attributesToStyles(); //convert the HTML presentation attributes to inline styles
        da.addStyleSheet(null, CSSNorm.stdStyleSheet(), DOMAnalyzer.Origin.AGENT); //use the standard style sheet
        da.addStyleSheet(null, CSSNorm.userStyleSheet(), DOMAnalyzer.Origin.AGENT); //use the additional style sheet
        da.getStyleSheets(); //load the author style sheets

        BrowserCanvas browser
                = new BrowserCanvas(da.getRoot(),
                        da,
                        url);
        browser.setImage(new BufferedImage(1000, 600, BufferedImage.TYPE_INT_ARGB));
        browser.createLayout(new java.awt.Dimension(1000, 600));

        ImageIO.write(browser.getImage(), "png",
                new File(Screenshot.getMavenScreenshotOutputDir(getClass()), "test.png"));

        // no auto-resize when explicitly setting an image
        assertEquals(1000, browser.getImage().getWidth());
        assertEquals(600, browser.getImage().getHeight());
    }

    @Test
    public void testAutoSize() throws MalformedURLException, IOException, SAXException, InterruptedException, Exception {
        URL url = CssBoxTest.class.getResource("TransparentBackgroundTest.html");
        DocumentSource src = new DefaultDocumentSource(url);
        //Parse the input document (replace this with your own parser if desired)
        DOMSource parser = new DefaultDOMSource(src);
        Document doc = parser.parse(); //doc represents the obtained DOM

        DOMAnalyzer da = new DOMAnalyzer(doc, url);
        da.attributesToStyles(); //convert the HTML presentation attributes to inline styles
        da.addStyleSheet(null, CSSNorm.stdStyleSheet(), DOMAnalyzer.Origin.AGENT); //use the standard style sheet
        da.addStyleSheet(null, CSSNorm.userStyleSheet(), DOMAnalyzer.Origin.AGENT); //use the additional style sheet
        da.getStyleSheets(); //load the author style sheets

        BrowserCanvas browser
                = new BrowserCanvas(da.getRoot(),
                        da,
                        new java.awt.Dimension(1024, 1),
                        url);
        ImageIO.write(browser.getImage(), "png",
                new File(Screenshot.getMavenScreenshotOutputDir(getClass()), "testAutoSize.png"));

        assertEquals(1024, browser.getImage().getWidth());
        assertTrue(browser.getImage().getHeight() > 8);
    }

    @Test
    public void testPreview() throws MalformedURLException, IOException, SAXException, InterruptedException, Exception {
        assumeTrue(Surefire.isSingleTestExecution());
        
        JFrame frame = new JFrame("HTML Editor With Preview Provided by CSSBox");
        frame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        Container container = frame.getContentPane();
        
        final JPanel browserPanel = new JPanel();
        JTextArea textArea = new JTextArea();
        
        textArea.setText("<b>example</b>");
        textArea.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				anything();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				anything();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				anything();
			}
			
			private void anything() {
				browserPanel.removeAll();
				try {
					browserPanel.add(getBrowser(textArea.getText()));
				} catch (IOException | SAXException e1) {
					LOG.error("", e1);
				}
			}
		});
        
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 1.0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.BOTH;
        
        container.add(textArea, c);
        
        c.gridy++;
        
        browserPanel.add(getBrowser(textArea.getText()));
        container.add(browserPanel, c);
        frame.pack();
        frame.setPreferredSize(new Dimension(frame.getWidth(), frame.getHeight()*2));

        AWTUtils.showFrameAndWaitForCloseByUser(frame);
    }
    
    public static BrowserCanvas getBrowser(String html) throws UnsupportedEncodingException, IOException, SAXException {
        Document doc = null;
        URL url = new URL("http://fake.url");
        try (InputStream is = new ByteArrayInputStream(html.getBytes("UTF-8"))) {
            DocumentSource src = new StreamDocumentSource(is, url, "text/html");
            DOMSource parser = new DefaultDOMSource(src);
            doc = parser.parse(); //doc represents the obtained DOM
        }

        DOMAnalyzer da = new DOMAnalyzer(doc, url);
        da.attributesToStyles(); //convert the HTML presentation attributes to inline styles
        da.addStyleSheet(null, CSSNorm.stdStyleSheet(), DOMAnalyzer.Origin.AGENT); //use the standard style sheet
        da.addStyleSheet(null, CSSNorm.userStyleSheet(), DOMAnalyzer.Origin.AGENT); //use the additional style sheet
        da.getStyleSheets(); //load the author style sheets

    	BrowserCanvas browser = new BrowserCanvas(da.getRoot(),
                        da,
                        new java.awt.Dimension(1024, 1),
                        url);
    	return browser;
    }
}
