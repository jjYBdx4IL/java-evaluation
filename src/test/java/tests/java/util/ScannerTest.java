package tests.java.util;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.util.NoSuchElementException;
import java.util.Scanner;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ScannerTest {

    @Test
    public void test() {
        Scanner scanner = new Scanner("17-MAR-11   15.52.25.000000000");

        Scanner dayScanner = new Scanner(scanner.next());
        Scanner timeScanner = new Scanner(scanner.next());

        dayScanner.useDelimiter("-");
        assertEquals(17, dayScanner.nextInt());
        assertEquals("MAR", dayScanner.next());
        assertEquals(11, dayScanner.nextInt());

        timeScanner.useDelimiter("\\.");
        assertEquals(15, timeScanner.nextInt());
        assertEquals(52, timeScanner.nextInt());
        assertEquals(25, timeScanner.nextInt());
        assertEquals(0, timeScanner.nextInt());
        
        dayScanner.close();
        timeScanner.close();
        scanner.close();
    }

    @Test
    public void testOctals() {
        Scanner scanner = new Scanner("    17    021   ");

        assertEquals(17, scanner.nextInt());
        assertEquals(17, scanner.nextInt(8));
        
        scanner.close();
    }

    @Test
    public void testLine() {
        Scanner scanner = new Scanner("1\n2\n");

        // line 1: "1\n"
        assertTrue(scanner.hasNextLine());
        scanner.nextInt();
        assertEquals("", scanner.nextLine());

        // line 1: "2\n"
        assertTrue(scanner.hasNextLine());
        scanner.nextInt();
        assertEquals("", scanner.nextLine());

        assertFalse(scanner.hasNextLine());
        try {
            scanner.nextLine();
        } catch (NoSuchElementException ex) {
        }
        
        scanner.close();
    }
    
    @Test
    public void testWithoutNextLine() {
        Scanner scanner = new Scanner("1\n2\n");

        assertEquals(1, scanner.nextInt());
        assertEquals(2, scanner.nextInt());
        
        scanner.close();
    }
    
    @Test
    public void testPartialScan() {
        Scanner scanner = new Scanner("1 2\n3 4\n");

        // line 1: "1 2\n"
        assertTrue(scanner.hasNextLine());
        assertEquals(1, scanner.nextInt());
        assertTrue(scanner.hasNextLine());
        assertEquals(" 2", scanner.nextLine());

        // line 1: "3 4\n"
        assertTrue(scanner.hasNextLine());
        assertEquals(3, scanner.nextInt());
        assertEquals(" 4", scanner.nextLine());

        assertFalse(scanner.hasNextLine());
        try {
            scanner.nextLine();
        } catch (NoSuchElementException ex) {
        }
        
        scanner.close();
    }
    
    @Test
    public void testNextLine() {
        Scanner scanner = new Scanner("1 2\n3 4\n");

        // line 1: "1 2\n"
        assertEquals("1 2", scanner.nextLine());
        assertEquals(3, scanner.nextInt());
        
        scanner.close();
    }
    
    @SuppressWarnings("resource")
    @Test
    public void testHasNextLine() {
        assertTrue(new Scanner("1 2\n").hasNextLine());
        assertTrue(new Scanner("1 2").hasNextLine());
        Scanner scanner = new Scanner("1");
        scanner.nextInt();
        assertFalse(scanner.hasNextLine());
    }
}
