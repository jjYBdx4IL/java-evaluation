package org.mapdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.concurrent.ConcurrentMap;

import org.junit.Test;

import com.github.jjYBdx4IL.utils.env.Maven;

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

    private final static File MAVEN_TEST_DIR = Maven.getTempTestDir(MapDBTest.class);

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
