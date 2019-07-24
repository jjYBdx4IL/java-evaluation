/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.io;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ObjectInputStreamTest {

    // this describes a bug filed under Review ID: JI-9028653 (16.1.2016)
    @Test
    public void testHeaderErrorPreventsClosePropagationToUnderlyingStream() {
        final AtomicBoolean closed = new AtomicBoolean(false);

        InputStream bais = new ByteArrayInputStream(new byte[1024]) {

            @Override
            public void close() throws IOException {
                closed.set(true);
                super.close();
            }
        };

        // note: the ois constructor blocks until it gets the object stream
        // header - so in real life scenarios you'd want to flush the oos right
        // away after opening it

        try (ObjectInputStream ois = new ObjectInputStream(bais)) {
            ois.readObject();
        } catch (ClassNotFoundException | IOException ex) {
        }

        assertFalse(closed.get());
    }
}
