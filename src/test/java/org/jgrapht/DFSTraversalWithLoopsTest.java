/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package org.jgrapht;

import java.util.Iterator;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.DepthFirstIterator;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class DFSTraversalWithLoopsTest {

    public static DirectedGraph<String, DefaultEdge> createExampleDirectedGraph() {
        DirectedGraph<String, DefaultEdge> dg =
            new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

        String a = "A";
        String b = "B";
        String c = "C";

        dg.addVertex(a);
        dg.addVertex(b);
        dg.addVertex(c);

        dg.addEdge(a, b);
        dg.addEdge(b, c);
        dg.addEdge(c, a);

        return dg;
    }

    @Test
    public void test() {
        DirectedGraph<String, DefaultEdge> dg = createExampleDirectedGraph();

        Iterator<String> dfs = new DepthFirstIterator<String, DefaultEdge>(dg);
        String actual = "";
        while (dfs.hasNext()) {
            actual += dfs.next();
        }

        assertEquals("ABC", actual);

    }

}
