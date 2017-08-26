/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package fr.opensagres.xdocreport.sfntly;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.FontFactory;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.table.Table;
import com.google.typography.font.sfntly.table.core.NameTable;
import com.google.typography.font.sfntly.table.core.NameTable.NameEntry;
import com.google.typography.font.sfntly.table.core.NameTable.NameId;

import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * https://github.com/googlei18n/sfntly/blob/master/java/test/com/google/typography/font/sfntly/testutils/TestFontUtils.java
 *
 * @author Github jjYBdx4IL Projects
 */
public class TTFRenameTest {

	private static final Logger LOG = LoggerFactory.getLogger(TTFRenameTest.class);
	static final String testFontPath = "/net/sf/jasperreports/fonts/dejavu/DejaVuSansMono.ttf";

	@Test
	public void testLoadFontFromResource() throws IOException, FontFormatException {
		final java.awt.Font unchangedFont;
		try (InputStream is = getClass().getResourceAsStream(testFontPath)) {
			unchangedFont = java.awt.Font.createFont(java.awt.Font.PLAIN, is);
		}
		assertEquals("DejaVu Sans Mono", unchangedFont.getFontName());

		FontFactory fontFactory = FontFactory.getInstance();
		final Font[] fonts;
		try (InputStream is = getClass().getResourceAsStream(testFontPath)) {
			fonts = fontFactory.loadFonts(is);
		}
		assertEquals(1, fonts.length);
		LOG.info(fonts[0].toString());
		for (Integer key : fonts[0].tableMap().keySet()) {
			Table t = fonts[0].getTable(key);
			assertNotNull(t);
			LOG.info("" + t);
			LOG.info(Tag.stringValue(t.header().tag()));
		}

		NameTable name = (NameTable) fonts[0].getTable(Tag.name);
		for (NameEntry entry : name) {
			LOG.info(entry.toString());
			if (NameId.valueOf(entry.nameId()).equals(NameId.FontFamilyName)) {
				LOG.info("font family name is: " + entry.name());
			}
		}

		// see
		// https://github.com/googlei18n/sfntly/blob/master/java/test/com/google/typography/font/sfntly/NameEditingTests.java
		// for more
		// ...
	}
}