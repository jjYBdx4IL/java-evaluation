package com.opencsv;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class CSVReaderTest {

    private final static Logger log = Logger.getLogger(CSVReaderTest.class.getName());

    private static final String TEST_INPUT_1 = ""
            + "Datum,Beschreibung,Beschreibung2,Wert\n"
            + "28.02.2015,Habenzinsen (Nr. T2099662539),\"\",\"2,41\"\n"
            + "28.02.2015,Kapitalertragsteuer (Nr. T2099640232),\"\",\"-0,6\"\n";
    private static final String TEST_INPUT_2 = ""
            + "Datum;Beschreibung;Beschreibung2;Wert\n"
            + "28.02.2015;Habenzinsen (Nr. T2099662539);\"\";2,41\n"
            + "28.02.2015;Kapitalertragsteuer (Nr. T2099640232);\"\";-0,6\n";

    @Test
    public void testCSVReader() throws IOException {
        CSVReader reader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(TEST_INPUT_1.getBytes())));
        String[] nextLine;
        List<String[]> lines = new ArrayList<>();
        while ((nextLine = reader.readNext()) != null) {
            log.info(Arrays.toString(nextLine));
            lines.add(nextLine);
        }
        reader.close();

        assertEquals(3, lines.size());
        assertEquals(4, lines.get(0).length);
        assertEquals(4, lines.get(1).length);
        assertEquals(4, lines.get(2).length);

        assertEquals("Datum", lines.get(0)[0]);
        assertEquals("Beschreibung", lines.get(0)[1]);
        assertEquals("Beschreibung2", lines.get(0)[2]);
        assertEquals("Wert", lines.get(0)[3]);

        assertEquals("28.02.2015", lines.get(1)[0]);
        assertEquals("Habenzinsen (Nr. T2099662539)", lines.get(1)[1]);
        assertEquals("", lines.get(1)[2]);
        assertEquals("2,41", lines.get(1)[3]);

        assertEquals("28.02.2015", lines.get(2)[0]);
        assertEquals("Kapitalertragsteuer (Nr. T2099640232)", lines.get(2)[1]);
        assertEquals("", lines.get(2)[2]);
        assertEquals("-0,6", lines.get(2)[3]);
    }

    @Test
    public void testCSVReaderSkipLines() throws IOException {
        CSVReader reader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(TEST_INPUT_2.getBytes())), ';', '"', 1);
        String[] nextLine;
        List<String[]> lines = new ArrayList<>();
        while ((nextLine = reader.readNext()) != null) {
            log.info(Arrays.toString(nextLine));
            lines.add(nextLine);
        }
        reader.close();

        assertEquals(2, lines.size());
        assertEquals(4, lines.get(0).length);
        assertEquals(4, lines.get(1).length);

        assertEquals("28.02.2015", lines.get(0)[0]);
        assertEquals("Habenzinsen (Nr. T2099662539)", lines.get(0)[1]);
        assertEquals("", lines.get(0)[2]);
        assertEquals("2,41", lines.get(0)[3]);

        assertEquals("28.02.2015", lines.get(1)[0]);
        assertEquals("Kapitalertragsteuer (Nr. T2099640232)", lines.get(1)[1]);
        assertEquals("", lines.get(1)[2]);
        assertEquals("-0,6", lines.get(1)[3]);
    }
}
