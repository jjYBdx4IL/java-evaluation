package tests.java.io;

import com.fasterxml.sort.ExampleDTO;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectOutputStreamTest {

    private static final Logger LOG = LoggerFactory.getLogger(ObjectOutputStreamTest.class);

    public static final long DURATION_MS = 1 * 1000L;

    @Test
    public void testPerformance() throws IOException, ClassNotFoundException {
        long n = 0;
        byte[] data;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            long end = System.currentTimeMillis() + DURATION_MS;
            do {
                oos.writeObject(ExampleDTO.genRandom());
                n++;
            } while (System.currentTimeMillis() < end);
            LOG.info(String.format("%d objects encoded per second, %d bytes per object",
                n * 1000l / DURATION_MS, baos.size() / n));

            oos.flush();
            data = baos.toByteArray();
            long start = System.currentTimeMillis();
            try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
                ObjectInputStream ois = new ObjectInputStream(bais)) {
                while (ois.readObject() != null) {
                }
            } catch (EOFException ex) {
            }
            long duration = System.currentTimeMillis() - start;
            LOG.info(String.format("%d objects decoded per second",
                n * 1000l / duration));
        }
    }

}
