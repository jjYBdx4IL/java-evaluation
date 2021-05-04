package org.smali;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.dexbacked.DexBackedClassDef;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class BakSmaliTest {

    @Ignore // needs classes.dex file in test resources
    @Test
    public void testListClassesInDexFile() throws IOException {
        DexBackedDexFile dexFile = DexFileFactory.loadDexFile(
                getClass().getResource("classes.dex").getPath(), 17);
        assertNotNull(dexFile);

        Set<? extends DexBackedClassDef> classes = dexFile.getClasses();
        assertEquals(126, classes.size());

        Pattern p = Pattern.compile("^L(org/apache/commons/io)/.+;$", Pattern.DOTALL);

        for (DexBackedClassDef classDef : classes) {
            assertTrue(p.matcher(classDef.toString()).find());
        }
    }
}
