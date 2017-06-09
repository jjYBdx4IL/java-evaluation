package org.mapdb;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import com.github.jjYBdx4IL.test.FileUtil;

import java.io.File;
import java.util.concurrent.ConcurrentMap;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * My conclusion: don't use MapDB. I can't even make to unchecked cast warnings go away
 * without suppressing them. There are also serious, open issues in the issue tracker.
 * Generally speaking, implementing a database is a highly nontrivial task. Use mature
 * embedded databases like h2 instead.
 * On top of that, it is implemented in Kotlin, so forget about debugging in Java.
 *
 * @author Github jjYBdx4IL Projects
 */
@SuppressWarnings("unchecked")
public class MapDBTest {

    private final static File MAVEN_TEST_DIR = FileUtil.createMavenTestDir(MapDBTest.class);

    @Test
    public void testPersistence() {
        File dbFile = new File(MAVEN_TEST_DIR, "testPersistence");
        
        DB db = DBMaker
                .fileDB(dbFile)
                .transactionEnable()
                .closeOnJvmShutdown()
                .make();
		final ConcurrentMap<String, String> map = (ConcurrentMap<String, String>) db.hashMap("map").createOrOpen();
        
        assertNull(map.get("1"));
        map.put("1", "one");
        db.commit();
        
        db.close();

        db = DBMaker
                .fileDB(dbFile)
                .transactionEnable()
                .closeOnJvmShutdown()
                .make();
        ConcurrentMap<String, String> map1 = (ConcurrentMap<String, String>) db.hashMap("map").createOrOpen();
        assertEquals(map1.get("1"), "one");

        ConcurrentMap<String, String> map2 = (ConcurrentMap<String, String>) db.hashMap("map2").createOrOpen();
        assertNull(map2.get("1"));
    }

}
