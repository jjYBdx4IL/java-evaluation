package org.jsoup;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import com.github.jjYBdx4IL.test.HttpStatusCodeTestBase;

import java.io.IOException;

import org.jsoup.Jsoup;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class JsoupErrorHandlingTest extends HttpStatusCodeTestBase {

    @Test
    public void testAssertExists() throws Exception {
        Jsoup.connect(getServerUrl() + URI_200_OK).get();
        try {
            Jsoup.connect(getServerUrl() + URI_500_ERROR).get();
            fail();
        } catch (IOException ex) {
        }
        try {
            Jsoup.connect(getServerUrl() + URI_404_NOT_FOUND).get();
            fail();
        } catch (IOException ex) {
        }
        try {
            Jsoup.connect(getServerUrl() + URI_200_OK_IMG).get();
            fail();
        } catch (IOException ex) {
        }
    }
}