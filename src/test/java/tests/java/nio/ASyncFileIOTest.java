/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.nio;

import com.github.jjYBdx4IL.utils.env.Surefire;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ASyncFileIOTest {

    private static final Logger LOG = LoggerFactory.getLogger(ASyncFileIOTest.class);
    public static final File TMPDIR = Surefire.getTempDirForClassRT(ASyncFileIOTest.class);

    class Attachment {

        public Path path;
        public ByteBuffer buffer;
        public AsynchronousFileChannel asyncChannel;
    }

    @Test
    public void testWriteSimple() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        Path path = new File(TMPDIR, "testWriteSimple.dat").toPath();
        try (AsynchronousFileChannel afc = AsynchronousFileChannel.open(path,
                StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            ByteBuffer dataBuffer = getDataBuffer();
            Future<Integer> result = afc.write(dataBuffer, 0);
            int writtenBytes = result.get(1, TimeUnit.MINUTES);
            LOG.info(String.format(Locale.ROOT, "%s bytes written  to  %s", writtenBytes,
                    path.toAbsolutePath()));
        }
    }

    @Test
    public void testWriteExtended() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        Path path = new File(TMPDIR, "testWriteExtended.dat").toPath();
        AsynchronousFileChannel afc = AsynchronousFileChannel.open(
                path, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        WriteHandler handler = new WriteHandler();
        ByteBuffer dataBuffer = getDataBuffer();
        Attachment attach = new Attachment();
        attach.asyncChannel = afc;
        attach.buffer = dataBuffer;
        attach.path = path;

        afc.write(dataBuffer, 0, attach, handler);
    }

    class WriteHandler implements CompletionHandler<Integer, Attachment> {

        @Override
        public void completed(Integer result, Attachment attach) {
            LOG.info(String.format(Locale.ROOT, "%s bytes written  to  %s", result,
                    attach.path.toAbsolutePath()));
            try {
                attach.asyncChannel.close();
            } catch (IOException e) {
                LOG.error("", e);
            }
        }

        @Override
        public void failed(Throwable e, Attachment attach) {
            try {
                attach.asyncChannel.close();
            } catch (IOException e1) {
                LOG.error("", e1);
            }
        }
    }

    private ByteBuffer getDataBuffer() {
        StringBuilder sb = new StringBuilder();
        sb.append("test\n");

        String str = sb.toString();
        Charset cs = Charset.forName("ASCII");
        ByteBuffer bb = ByteBuffer.wrap(str.getBytes(cs));

        return bb;
    }
}
