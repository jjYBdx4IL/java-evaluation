package com.paypal.sdk;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2017 jjYBdx4IL
 * %%
 * #L%
 */

import com.github.jjYBdx4IL.junit.runners.RetryRunner;
import com.github.jjYBdx4IL.junit.runners.RetryRunnerConfig;
import com.github.jjYBdx4IL.test.AdHocHttpServer;
import com.github.jjYBdx4IL.test.selenium.SeleniumTestBase;
import com.github.jjYBdx4IL.test.selenium.WebElementNotFoundException;
import com.github.jjYBdx4IL.utils.env.Surefire;
import com.github.jjYBdx4IL.utils.net.URLUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
@RunWith(RetryRunner.class)
@RetryRunnerConfig(delayMillis = 300000l)
public class LoginAPITest extends SeleniumTestBase {

    private static AdHocHttpServer server = null;
    private static final Logger LOG = LoggerFactory.getLogger(LoginAPITest.class);
    
    private final PayPalLoginAPIConfig loginApiConfig;
    private final PayPalTestAccountsConfig testAccountsConfig;

    public LoginAPITest() {
    	loginApiConfig = new PayPalLoginAPIConfig();
    	testAccountsConfig = new PayPalTestAccountsConfig();
    }
    
    @Before
    public void before2() throws Exception {
        assumeTrue(Surefire.isSingleTestExecution()); // don't run tests with unstable external dependencies in CI
        server = new AdHocHttpServer();
        
        loginApiConfig.read();
    }

    @After
    public void after2() throws Exception {
        if (server != null) {
            server.close();
        }
    }

    @AfterClass
    public static void afterClass() throws Exception {
        if (server != null) {
            server.close();
        }
    }

    @Test
    public void testLogin() throws MalformedURLException, WebElementNotFoundException, InterruptedException, UnsupportedEncodingException, IOException {

        String redirectUri = "http://localhost/paypal-login-return";

        URL buttonPage = server.addStaticContent("/buttonPage", new AdHocHttpServer.StaticResponse(
                "<html><head></head><body>\n"
                + "<span id=\"myContainer\"></span>\n"
                + "<script src=\"https://www.paypalobjects.com/js/external/api.js\"></script>\n"
                + "<script>\n"
                + "paypal.use( [\"login\"], function(login) {\n"
                + "  login.render ({\n"
                + "    \"appid\": \"" + loginApiConfig.appId + "\",\n"
                + "    \"authend\": \"sandbox\",\n"
                + "    \"scopes\": \"openid profile email address phone https://uri.paypal.com/services/paypalattributes\",\n"
                + "    \"containerid\": \"myContainer\",\n"
                + "    \"locale\": \"US\",\n"
                + "    \"returnurl\": \"" + redirectUri + "\"\n"
                + "  });\n"
                + "});\n"
                + "</script></body></html>\n"));
        getDriver().get(buttonPage.toExternalForm());
        WebElement myContainer = waitForElement("xpath://span[@id='myContainer']");
        //log.info("generated button code: " + getDriver().getPageSource());
        takeScreenshot();
        myContainer.click();

        switchToOtherWindow();

        WebElement emailField = waitForElement("xpath://input[@id='email']");
        WebElement passwordField = waitForElement("xpath://input[@id='password']");
        WebElement submitButton = waitForElement("xpath://input[@type='submit']");
        setInputFieldValue(emailField, testAccountsConfig.deBuyerEmail);
        setInputFieldValue(passwordField, testAccountsConfig.deBuyerPassword);
        takeScreenshot();
        LOG.info("current url: " + getDriver().getCurrentUrl());
        submitButton.click();
        Thread.sleep(5000L);

        submitButton = findElement("xpath://input[@type='submit']");
        if (submitButton != null) {
            takeScreenshot();
            submitButton.click();
        }

        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return driver.getCurrentUrl().toLowerCase().contains("://localhost");
            }
        });

        takeScreenshot();
        String returnUrl = getDriver().getCurrentUrl();
        LOG.info("return url: " + returnUrl);

        
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("client_id", loginApiConfig.appId));
        nameValuePairs.add(new BasicNameValuePair("client_secret", loginApiConfig.secret));
        nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
        nameValuePairs.add(new BasicNameValuePair("code", URLUtils.getQueryParams(returnUrl).get("code").get(0)));
        nameValuePairs.add(new BasicNameValuePair("redirect_uri", redirectUri));

        CloseableHttpClient httpclient = HttpClients.custom()
                .build();
        String accessToken = null;
        try {
            // turn authorization code into access token
            HttpPost httpPost = new HttpPost(loginApiConfig.tokenServiceUrl);

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpPost.setHeader("Accept", ContentType.APPLICATION_JSON.getMimeType());
            LOG.info("Executing request " + httpPost.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                LOG.info("response status: " + response.getStatusLine());
                String content = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
                LOG.info("response content: " + content);
                accessToken = new JSONObject(content).getString("access_token");
                assertEquals(200, response.getStatusLine().getStatusCode());
                EntityUtils.consume(response.getEntity());
            } finally {
                response.close();
            }

            assertNotNull(accessToken);

            // get user information
            HttpGet httpGet = new HttpGet(loginApiConfig.userInfoUrl);

            httpGet.setHeader("Accept", ContentType.APPLICATION_JSON.getMimeType());
            httpGet.setHeader("Authorization", "Bearer " + accessToken);
            LOG.info("Executing request " + httpGet.getRequestLine());
            response = httpclient.execute(httpGet);
            try {
                LOG.info("response status: " + response.getStatusLine());
                String content = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
                LOG.info("response content: " + content);
                assertEquals(200, response.getStatusLine().getStatusCode());
                // http://stackoverflow.com/questions/17715649/can-i-get-payer-id-with-login-with-paypal
                // payer_id output must be enabled by tech support!
                String payerId = new JSONObject(content).getString("payer_id");
                assertNotNull(payerId);
                EntityUtils.consume(response.getEntity());
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }

}
