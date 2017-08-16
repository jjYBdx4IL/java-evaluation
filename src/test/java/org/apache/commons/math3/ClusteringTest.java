package org.apache.commons.math3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.log4j.Logger;
import static org.junit.Assert.*;
import org.junit.Test;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ClusteringTest {

    private final static Logger log = Logger.getLogger(ClusteringTest.class.getName());
    
    class Point implements Clusterable {

        final double[] values;

        Point(double x, double y) {
            this.values = new double[]{x, y};
        }

        @Override
        public double[] getPoint() {
            return Arrays.copyOf(values, values.length);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Point [");
            builder.append("values=");
            builder.append(Arrays.toString(values));
            builder.append("]");
            return builder.toString();
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 61 * hash + Arrays.hashCode(this.values);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Point other = (Point) obj;
            if (!Arrays.equals(this.values, other.values)) {
                return false;
            }
            return true;
        }

    }

    @Test
    public void testKMeansPlusPlusClusterer() {
        KMeansPlusPlusClusterer<Point> clusterer = new KMeansPlusPlusClusterer<>(2);
        List<Point> pts = new ArrayList<>();
        pts.add(new Point(0., 0.));
        pts.add(new Point(0., 1.));
        pts.add(new Point(10., 2.));
        List<CentroidCluster<Point>> res = clusterer.cluster(pts);
        assertEquals(2, res.size());
        log.info(res.get(0).getCenter());
        log.info(res.get(1).getCenter());
    }

}
