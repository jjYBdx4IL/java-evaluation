package tests.java.lang;

import org.junit.Test;

//@meta:keywords:memory,ram,free ram,free memory,memory usage@
public class RuntimeTest {

    @Test
    public void test() {
        dumpMemInfo();
    }

    static void dumpMemInfo() {
        Runtime runtime = Runtime.getRuntime();

        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        System.out.println(String.format("free memory:       %,11d kb", freeMemory / 1024));
        System.out.println(String.format("allocated memory:  %,11d kb", allocatedMemory / 1024));
        System.out.println(String.format("max memory:        %,11d kb", maxMemory / 1024));
        System.out.println(String.format("total free memory: %,11d kb",
            (freeMemory + (maxMemory - allocatedMemory)) / 1024));
    }

}
