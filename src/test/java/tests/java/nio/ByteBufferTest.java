package tests.java.nio;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import static org.junit.Assert.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ByteBufferTest {

    private static final Logger LOG = LoggerFactory.getLogger(ByteBufferTest.class);

    @Test
    public void test1() {
        ByteBuffer buf = ByteBuffer.allocate(48);
        byte[] ba = new byte[48];

        buf.put("test".getBytes());

        try {
            buf.get(ba, 0, ba.length);
            fail();
        } catch (BufferUnderflowException ex) {
        }

        buf.get(ba, 0, 2);
        assertNotEquals("te", new String(ba, 0, 2));

        buf.flip();

        buf.get(ba, 0, 2);
        assertEquals("te", new String(ba, 0, 2));
    }

    @Test
    public void test2() {
        ByteBuffer buf = ByteBuffer.allocate(48);
        byte[] ba = new byte[48];
        for (int i = 0; i < ba.length; i++) {
            ba[i] = 12;
        }

        buf.put(ba);

        try {
            buf.put(ba, 0, 1);
            fail();
        } catch (BufferOverflowException ex) {}
    }

    @Test
    public void test3() {
        ByteBuffer buf = ByteBuffer.allocate(48);
        byte[] ba = new byte[48];

        buf.put("test".getBytes());
        buf.flip();
        buf.get(ba, 0, 4);
        assertEquals("test", new String(ba,0,4));
        buf.flip();
        buf.put("TEST".getBytes());
        buf.flip();
        buf.get(ba, 0, 4);
        assertEquals("TEST", new String(ba,0,4));
    }

    @Test
    public void testFlip() {
        ByteBuffer buf = ByteBuffer.allocate(4);
        byte[] ba = new byte[48];

        LOG.info(buf.toString());
        LOG.info("buf.put(\"test\")");
        buf.put("test".getBytes());
        LOG.info(buf.toString());
        LOG.info("buf.flip()");
        buf.flip();
        LOG.info(buf.toString());
        buf.get(ba, 0, 1);
        assertEquals("t", new String(ba,0,1));
        LOG.info("buf.get(byte[], 0, 1) == \"t\"");
        LOG.info(buf.toString());
        LOG.info("buf.flip()");
        buf.flip();
        LOG.info(buf.toString());
        LOG.info("buf.put(\"T\")");
        buf.put("T".getBytes());
        LOG.info(buf.toString());
        LOG.info("buf.flip()");
        buf.flip();

        try {
            LOG.info(buf.toString());
            buf.get(ba, 0, 4);
            fail();
        } catch (BufferUnderflowException ex) {
            LOG.info("buf.get(byte[], 0, 4) -> "+ex.toString());
        }


        try {
            LOG.info(buf.toString());
            buf.get(ba, 0, 4);
            fail();
        } catch (BufferUnderflowException ex) {
            LOG.info("buf.get(byte[], 0, 4) -> "+ex.toString());
        }

        LOG.info(buf.toString());
        buf.get(ba, 0, 1);
        assertEquals("T", new String(ba,0,1));
        LOG.info("buf.get(byte[], 0, 1) == \"T\"");
    }

    @Test
    public void testCompact() {
        ByteBuffer buf = ByteBuffer.allocate(4);
        byte[] ba = new byte[48];

        LOG.info(buf.toString());
        LOG.info("buf.put(\"test\")");
        buf.put("test".getBytes());
        LOG.info(buf.toString());
        LOG.info("buf.flip()");
        buf.flip();
        LOG.info(buf.toString());
        buf.get(ba, 0, 1);
        assertEquals("t", new String(ba,0,1));
        LOG.info("buf.get(byte[], 0, 1) == \"t\"");
        LOG.info(buf.toString());
        LOG.info("buf.compact()");
        buf.compact();
        LOG.info(buf.toString());
        LOG.info("buf.put(\"T\")");
        buf.put("T".getBytes());
        LOG.info(buf.toString());
        LOG.info("buf.flip()");
        buf.flip();

        LOG.info(buf.toString());
        buf.get(ba, 0, 4);
        assertEquals("estT", new String(ba,0,4));
        LOG.info("buf.get(byte[], 0, 4) == \"estT\"");
        LOG.info(buf.toString());
    }

    @Test
    public void testClear() {
        ByteBuffer buf = ByteBuffer.allocate(4);
        byte[] ba = new byte[48];

        LOG.info(buf.toString());
        LOG.info("buf.put(\"test\")");
        buf.put("test".getBytes());
        LOG.info(buf.toString());
        LOG.info("buf.flip()");
        buf.flip();
        LOG.info(buf.toString());
        buf.get(ba, 0, 1);
        assertEquals("t", new String(ba,0,1));
        LOG.info("buf.get(byte[], 0, 1) == \"t\"");
        LOG.info(buf.toString());
        LOG.info("buf.clear()");
        buf.clear();
        LOG.info(buf.toString());
        LOG.info("buf.put(\"T\")");
        buf.put("T".getBytes());
        LOG.info(buf.toString());
        LOG.info("buf.flip()");
        buf.flip();

        LOG.info(buf.toString());
        buf.get(ba, 0, 1);
        assertEquals("T", new String(ba,0,1));
        LOG.info("buf.get(byte[], 0, 1) == \"T\"");
        LOG.info(buf.toString());
    }

    @Test
    public void testGetVsPut() {
        ByteBuffer a = ByteBuffer.allocate(4);
        ByteBuffer b = ByteBuffer.allocate(4);
        byte[] ba = new byte[48];

        LOG.info("a = "+a);
        LOG.info("b = "+b);
        LOG.info("a.put(\"test\")");
        LOG.info("a.get(ba,0,4)");
        a.put("test".getBytes());
        b.get(ba,0,4);
        LOG.info("a = "+a);
        LOG.info("b = "+b);

        LOG.info("a.compact()");
        LOG.info("b.compact()");
        a.compact();
        b.compact();
        LOG.info("a = "+a);
        LOG.info("b = "+b);

        LOG.info("a.clear()");
        LOG.info("b.clear()");
        a.clear();
        b.clear();
        LOG.info("a = "+a);
        LOG.info("b = "+b);
    }


    /**
     * Flipping always discards elements, ie. flipping from read mode to write mode discards any unread
     * elements! To use ByteBuffers as "FIFOs", ie for partial reads, use "compact()" to switch back to writing
     * to it.
     */
    @Test
    public void testAsyncFIFO() {
        ByteBuffer bb = ByteBuffer.allocate(10);
        bb.put("0123".getBytes());

        byte[] ba2 = new byte[2];
        bb.flip();
        bb.get(ba2);
        assertEquals("01", new String(ba2));

        bb.compact();
        bb.put("4".getBytes());

        ba2 = new byte[2];
        bb.flip();
        bb.get(ba2);
        assertEquals("23", new String(ba2));

        bb.compact();
        bb.put("567".getBytes());

        byte[] ba3 = new byte[3];
        bb.flip();
        bb.get(ba3);
        assertEquals("456", new String(ba3));

        bb.compact();
        bb.put("".getBytes()); // or not calling it at all

        byte[] ba1 = new byte[1];
        bb.flip();
        bb.get(ba1);
        assertEquals("7", new String(ba1));
    }

    /**
     * A demonstration on how to force filling the ByteBuffer until compaction is really necessary.
     * We don't check for the actual compaction condition here.
     */
    @Test
    public void testAsyncFIFOForceWithoutCompaction() {
        int readpos = 0;

        ByteBuffer bb = ByteBuffer.allocate(10);
        bb.put("0123".getBytes());

        byte[] ba2 = new byte[2];
        bb.limit(bb.position());
        bb.position(readpos);
        bb.get(ba2);
        assertEquals("01", new String(ba2));

        //bb.compact();
        readpos = bb.position();
        bb.position(bb.limit());
        bb.limit(bb.capacity());
        bb.put("4".getBytes());

        ba2 = new byte[2];
        bb.limit(bb.position());
        bb.position(readpos);
        bb.get(ba2);
        assertEquals("23", new String(ba2));

        //bb.compact();
        readpos = bb.position();
        bb.position(bb.limit());
        bb.limit(bb.capacity());
        bb.put("567".getBytes());

        byte[] ba3 = new byte[3];
        bb.limit(bb.position());
        bb.position(readpos);
        bb.get(ba3);
        assertEquals("456", new String(ba3));

        //bb.compact();
        readpos = bb.position();
        bb.position(bb.limit());
        bb.limit(bb.capacity());
        bb.put("".getBytes()); // or not calling it at all

        byte[] ba1 = new byte[1];
        bb.limit(bb.position());
        bb.position(readpos);
        bb.get(ba1);
        assertEquals("7", new String(ba1));
    }
}
