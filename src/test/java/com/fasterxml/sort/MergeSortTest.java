package com.fasterxml.sort;

import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.env.Surefire;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;

public class MergeSortTest {

    private static final Logger LOG = LoggerFactory.getLogger(MergeSortTest.class);
    public static final File tempDir = Maven.getTempTestDir(MergeSortTest.class);
    public static final long TARGET_SIZE = 100 * 1024 * 1024L;
    Random r = new Random(0);

    @Test
    public void test() throws IOException, ClassNotFoundException {
        assumeTrue(Surefire.isSingleTestExecution());
        
        File one = createFile("one");

        Sorter<ExampleDTO> sorter = new Sorter<ExampleDTO>(
            new SortConfig().withMaxMemoryUsage(1024 * 1024),
            new ExampleDTOReader.Factory(),
            new ExampleDTOWriter.Factory(),
            new Comparator<ExampleDTO>() {

                @Override
                public int compare(ExampleDTO o1, ExampleDTO o2) {
                    return o1.getCreated().compareTo(o2.getCreated());
                }
            }
        );

        File output = new File(tempDir, "output");

        try (
            FileInputStream fis = new FileInputStream(one);
            ExampleDTOReader dr = new ExampleDTOReader(fis);
            FileOutputStream fos = new FileOutputStream(output);
            ExampleDTOWriter dw = new ExampleDTOWriter(fos)) {
            sorter.sort(dr, dw);
        }
        // assertEquals("sorted count", STRING_COUNT, counter.getCount());
        sorter.close();

        long count = 0;
        long start = System.currentTimeMillis();
        try (
            FileInputStream fis = new FileInputStream(output);
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (ois.readObject() != null) {
                count++;
            }
        } catch (EOFException ex) {}
        long duration = System.currentTimeMillis() - start;
        LOG.info(String.format("%d objects read and decoded per second", count * 1000l / duration));
    }

    private File createFile(String fileName) throws IOException {
        File file = new File(tempDir, fileName);
        long objectsWritten = 0;
        long start = System.currentTimeMillis();
        int nPerLoop = 1000;
        try (
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);) {
            while (file.length() < TARGET_SIZE) {
                for (int i = 0; i < nPerLoop; i++) {
                    oos.writeObject(ExampleDTO.genRandom());
                }
                objectsWritten += nPerLoop;
            }
        }
        long duration = System.currentTimeMillis() - start;
        LOG.info(String.format("%d objects read and decoded per second", objectsWritten * 1000l / duration));
        LOG.info(String.format("%,d objects written to %s", objectsWritten, file));
        return file;
    }
}
