/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package com.googlecode.fannj;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class FannJTest {

    public static final int SAMPLES_PER_TRAIN_RUN = 1000;
    public static final int INPUT_NEURONS = 3;

//    @Ignore
//    @Test
//    public void test() throws IOException {
//        File temp = null;
//        try (InputStream is = new FileInputStream("/work/mark/ibmktdepth.0.1457339754536.log.gz")) {
//            try (GZIPInputStream gzis = new GZIPInputStream(is)) {
//                do {
//                    if (temp != null) {
//                        temp.delete();
//                    }
//                    temp = File.createTempFile("fannj_", ".tmp");
//                } while(run(temp, gzis));
//            }
//        } finally {
//            if (temp != null) {
//                temp.delete();
//            }
//        }
//    }

    @Test
    public void testrun() throws IOException {
        //System.getProperties().list(System.out);

        File temp = File.createTempFile("fannj_", ".tmp");
        temp.deleteOnExit();
        IOUtils.copy(
                this.getClass().getResourceAsStream("xor.data"),
                new FileOutputStream(temp));

        List<Layer> layers = new ArrayList<>();
        layers.add(Layer.create(2));
        layers.add(Layer.create(3, ActivationFunction.FANN_SIGMOID_SYMMETRIC));
        layers.add(Layer.create(1, ActivationFunction.FANN_SIGMOID_SYMMETRIC));
        Fann fann = new Fann(layers);
        Trainer trainer = new Trainer(fann);
        trainer.setTrainingAlgorithm(TrainingAlgorithm.FANN_TRAIN_QUICKPROP);
        float desiredError = .001f;
        float mse = trainer.train(temp.getPath(), 500000, 1000,
                desiredError);
        assertTrue("" + mse, mse <= desiredError);
    }
}


