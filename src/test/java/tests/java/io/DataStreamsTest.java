/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;
import java.util.Locale;

import static org.junit.Assert.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Based on: https://docs.oracle.com/javase/tutorial/essential/io/datastreams.html
 *
 * @author Github jjYBdx4IL Projects
 */
public class DataStreamsTest {

    private static final Logger LOG = LoggerFactory.getLogger(DataStreamsTest.class);

    static final double[] prices = {19.99, 9.99, 15.99, 3.99, 4.99};
    static final int[] units = {12, 8, 13, 29, 50};
    static final String[] descs = {
        "Java T-shirt",
        "Java Mug",
        "Duke Juggling Dolls",
        "Java Pin",
        "Java Key Chain"
    };

    @Test
    public void test1() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(baos))) {
            for (int i = 0; i < prices.length; i++) {
                out.writeDouble(prices[i]);
                out.writeInt(units[i]);
                out.writeUTF(descs[i]);
            }
        }

        int count = 0;
        try (InputStream bais = new ByteArrayInputStream(baos.toByteArray());
                DataInputStream in = new DataInputStream(new BufferedInputStream(bais))) {
            double price;
            int unit;
            String desc;
            double total = 0.0;
            Exception caught = null;
            try {
                while (true) {
                    price = in.readDouble();
                    unit = in.readInt();
                    desc = in.readUTF();
                    LOG.info(String.format(Locale.ROOT, "You ordered %d" + " units of %s at $%.2f",
                            unit, desc, price));
                    total += unit * price;
                    assertEquals(prices[count], price, 1e-10);
                    assertEquals(units[count], unit);
                    assertEquals(descs[count], desc);
                    count++;
                }
            } catch (Exception e) {
                caught = e;
            }
            assertEquals(prices.length, count);
            assertTrue(caught instanceof EOFException);
        }
    }

    @Test
    public void testWriteStringMaxLen65535() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 65536/10; i++) {
            sb.append("0123456789");
        }

        // length of 65536 is too long
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(baos))) {
            out.writeUTF(sb.toString() + "123456");
            fail();
        } catch (UTFDataFormatException ex) {
        }

        // long of 65535 is still okay
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(baos))) {
            out.writeUTF(sb.toString() + "12345");
        } catch (UTFDataFormatException ex) {
            fail();
        }
    }
}
