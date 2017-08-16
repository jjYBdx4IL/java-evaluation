package org.apache.pdfbox;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.pdfbox.cos.COSName;
//import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDestinationNameTreeNode;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureTreeRoot;
//import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
//import org.apache.pdfbox.pdmodel.interactive.action.type.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class CreatePDFTest {

    private static final Logger log = Logger.getLogger(CreatePDFTest.class.getName());

    // this is pdfbox v1
    
//    @Test
//    public void helloWord() throws IOException, COSVisitorException {
//        // Create a document and add a page to it
//        PDDocument document = new PDDocument();
//        PDPage page = new PDPage();
//        document.addPage(page);
//
//        // Create a new font object selecting one of the PDF base fonts
//        PDFont font = PDType1Font.TIMES_ROMAN;
//        float fontSize = 12f;
//        log.info("font boundingbox = " + font.getFontBoundingBox());
//
//        // Start a new content stream which will "hold" the to be created content
//        PDPageContentStream contentStream = new PDPageContentStream(document, page);
//
//        // Define a text content stream using the selected font, moving the cursor and drawing the text "Hello World"
//        String s = "Hello World";
//        float y = 700f;
//        float margin = 100f;
//        contentStream.beginText();
//        contentStream.setFont(font, fontSize);
//        contentStream.moveTextPositionByAmount(margin, y);
//        for (int i = 0; i < 100; i++) {
//            contentStream.drawString(s);
//        }
//        contentStream.endText();
//
//        s = "Text with line drawn at text y position";
//        y = 650f;
//        contentStream.beginText();
//        contentStream.setFont(font, fontSize);
//        contentStream.moveTextPositionByAmount(margin, y);
//        contentStream.drawString(s);
//        contentStream.endText();
//        contentStream.drawLine(margin, y, margin + font.getStringWidth(s) * fontSize / 1000f, y);
//
//        s = "Text with lines drawn at upper and lower bounding box positions";
//        y = 600f;
//        contentStream.beginText();
//        contentStream.setFont(font, fontSize);
//        contentStream.moveTextPositionByAmount(margin, y);
//        contentStream.drawString(s);
//        contentStream.endText();
//        float yLineOffset = font.getFontBoundingBox().getUpperRightY() * fontSize / 1000f;
//        contentStream.drawLine(margin, y + yLineOffset,
//                margin + font.getStringWidth(s) * fontSize / 1000f, y + yLineOffset);
//        yLineOffset = font.getFontBoundingBox().getLowerLeftY() * fontSize / 1000f;
//        contentStream.drawLine(margin, y + yLineOffset,
//                margin + font.getStringWidth(s) * fontSize / 1000f, y + yLineOffset);
//
//        // full example: http://svn.apache.org/repos/asf/pdfbox/trunk/examples/src/main/java/org/apache/pdfbox/examples/pdmodel/Annotation.java
//        String tgt = "http://www.google.de";
//        s = "Hyperlink pointing to " + tgt + ".";
//        y = 550f;
//        contentStream.beginText();
//        contentStream.setFont(font, fontSize);
//        contentStream.moveTextPositionByAmount(margin, y);
//        PDAnnotationLink txtLink = new PDAnnotationLink();
//        PDActionURI action = new PDActionURI();
//        action.setURI(tgt);
//        txtLink.setAction(action);
//        PDRectangle position = new PDRectangle();
//        position.setLowerLeftX(margin);
//        position.setLowerLeftY(y + font.getFontBoundingBox().getLowerLeftY() * fontSize / 1000f);
//        position.setUpperRightX(margin + font.getStringWidth(s) * fontSize / 1000f);
//        position.setUpperRightY(y + font.getFontBoundingBox().getUpperRightY() * fontSize / 1000f);
//        txtLink.setRectangle(position);
//        page.getAnnotations().add(txtLink);
//        contentStream.drawString(s);
//        contentStream.endText();
//
//        // create document contents
//        PDOutlineItem item1 = new PDOutlineItem();
//        item1.setTitle("title1");
//        item1.setDestination(page);
//        PDOutlineItem item11 = new PDOutlineItem();
//        item11.setTitle("title11");
//        PDOutlineItem item2 = new PDOutlineItem();
//        item1.appendChild(item11);
//        item2.setTitle("title2");
//        PDDocumentOutline outline = new PDDocumentOutline();
//        outline.appendChild(item1);
//        outline.appendChild(item2);
//        document.getDocumentCatalog().setDocumentOutline(outline);
//
//        // add a named anchor to the second page (not yet working)
//        PDStructureTreeRoot root = document.getDocumentCatalog().getStructureTreeRoot();
//        PDStructureElement anchor = new PDStructureElement(tgt, root);
//        anchor.setActualText("actualText");
//        
//        final String namedAnchorString = "w1";
//        s = "Hyperlink pointing to next page.";
//        y = 500f;
//        contentStream.beginText();
//        
//        contentStream.setFont(font, fontSize);
//        contentStream.moveTextPositionByAmount(margin, y);
//        txtLink = new PDAnnotationLink();
//
//
//        PDNamedDestination namedDestination = new PDNamedDestination(COSName.getPDFName(namedAnchorString));
//        PDDocumentNameDictionary dict = new PDDocumentNameDictionary(document.getDocumentCatalog());
//        document.getDocumentCatalog().setNames(dict);
//        PDDestinationNameTreeNode dests = new PDDestinationNameTreeNode();
//        dict.setDests(dests);
//        
//        Map<String, ? extends COSObjectable> names = new HashMap<>();
//        //names.put("w1", namedDestination);
//        dests.setNames(names);
//
////        assertNotNull(document.getDocumentCatalog().getNames());
////        assertNotNull(document.getDocumentCatalog().getNames().getDests());
////        assertNotNull(document.getDocumentCatalog().getNames().getDests().getNames());
//        //document.getDocumentCatalog().getNames().getDests().getNames().put("w1", namedDestination);
//        txtLink.setDestination(namedDestination);
//        item2.setDestination(namedDestination);
//        position = new PDRectangle();
//        position.setLowerLeftX(margin);
//        position.setLowerLeftY(y + font.getFontBoundingBox().getLowerLeftY() * fontSize / 1000f);
//        position.setUpperRightX(margin + font.getStringWidth(s) * fontSize / 1000f);
//        position.setUpperRightY(y + font.getFontBoundingBox().getUpperRightY() * fontSize / 1000f);
//        txtLink.setRectangle(position);
//        page.getAnnotations().add(txtLink);
//        contentStream.drawString(s);
//        contentStream.endText();
//
//        // Make sure that the content stream is closed:
//        contentStream.close();
//
//        // second page
//        page = new PDPage();
//        document.addPage(page);
//        item2.setDestination(page);
//        contentStream = new PDPageContentStream(document, page);
//
//
//
//
//        y = 700f;
//        contentStream.beginText();
//        contentStream.setFont(font, fontSize);
//        contentStream.moveTextPositionByAmount(margin, y);
//        contentStream.beginMarkedContentSequence(COSName.getPDFName(namedAnchorString));
//        contentStream.drawString("test");
//        contentStream.moveTextPositionByAmount(font.getStringWidth("test")*fontSize/1000f,
//                -font.getFontBoundingBox().getHeight()*fontSize/1000f);
//        contentStream.drawString("test");
//        contentStream.moveTextPositionByAmount(font.getStringWidth("test")*fontSize/1000f,
//                -font.getFontBoundingBox().getHeight()*fontSize/1000f);
//        contentStream.drawString("test");
//        contentStream.endMarkedContentSequence();
//        contentStream.endText();
//        
//        
//        
//
//        // Make sure that the content stream is closed:
//        contentStream.close();
//
//        // Save the results and ensure that the document is properly closed:
//        document.save(new File("target", CreatePDFTest.class.getName() + ".pdf"));
//        document.close();
//    }
}
