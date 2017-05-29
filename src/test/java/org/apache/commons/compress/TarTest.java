package org.apache.commons.compress;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.io.*;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Test;

public class TarTest {
    
    // http://commons.apache.org/compress/examples.html
    @Test
    public void test1() throws ArchiveException, IOException {
        final String tarFileName = "target/test.tar";
        
        final String file1Content = "file 1 content";
        final String file1Path = "./file1.txt";
        final String file2Content = "file 2 content";
        final String file2Path = "./file2.txt";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final TarArchiveOutputStream taos = (TarArchiveOutputStream) new ArchiveStreamFactory().createArchiveOutputStream("tar", baos);

        TarArchiveEntry tae = new TarArchiveEntry(file1Path);
        tae.setSize(file1Content.getBytes().length);
        taos.putArchiveEntry(tae);
        taos.write(file1Content.getBytes());
        taos.closeArchiveEntry();

        tae = new TarArchiveEntry(file2Path);
        tae.setSize(file2Content.getBytes().length);
        taos.putArchiveEntry(tae);
        taos.write(file2Content.getBytes());
        taos.closeArchiveEntry();

        taos.close();

        FileWriter fw = new FileWriter(tarFileName);
        IOUtils.write(baos.toByteArray(), fw);
        
        // read in the generated tar file
        final TarArchiveInputStream tais = (TarArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream("tar", new BufferedInputStream(new FileInputStream(tarFileName)));
        
        while((tae = tais.getNextTarEntry()) != null) {
            System.out.println(tae.getModTime()+"\t"+tae.getSize()+"\t"+tae.getName());
        }
        
        tais.close();
    }
}
