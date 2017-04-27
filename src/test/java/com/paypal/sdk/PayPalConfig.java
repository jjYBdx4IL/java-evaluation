package com.paypal.sdk;

import java.io.File;
import java.util.Properties;

import com.github.jjYBdx4IL.utils.AbstractConfig;

public class PayPalConfig extends AbstractConfig {

    /**
     * Account credentials (Add one or more API accounts here)
     *
     * Beware! the first account specified seems to have special importance! (default for ops where you don't explicitly
     * state an id?)
     */
    public String acct1UserName = "";
    public String acct1Password = "";
    public String acct1Signature = "";
    public String acct1AppId = "";
    public String acct1Subject = "";
    public String emailAddress = "";

    public String ecRedirectUrl = "https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=";

    public PayPalConfig() {
        super("PayPalSDK", true);
    }

    @Override
    protected void postprocess() {
        // check if user has edited the default config file
        if (acct1UserName == null || acct1UserName.isEmpty()) {
            File file = getConfigFile();
            throw new RuntimeException("please update " + file.getAbsolutePath());
        }
    }

    public Properties getSDKProps() {
        Properties p = new Properties();
        p.setProperty("acct1.UserName", acct1UserName);
        p.setProperty("acct1.Password", acct1Password);
        p.setProperty("acct1.Signature", acct1Signature);
        p.setProperty("acct1.AppId", acct1AppId);
        p.setProperty("acct1.Subject", acct1Subject);
        p.setProperty("sandbox.EmailAddress", emailAddress);

        // Connection Information
        p.setProperty("http.ConnectionTimeOut", "5000");
        p.setProperty("http.Retry", "2");
        p.setProperty("http.ReadTimeOut", "30000");
        p.setProperty("http.MaxConnection", "100");
        p.setProperty("http.IPAddress", "127.0.0.1");

        // HTTP Proxy configuration
        // If you are using proxy set http.UseProxy to true and replace the following values with your proxy parameters
        p.setProperty("http.UseProxy", "false");
        p.setProperty("http.ProxyPort", "8080");
        p.setProperty("http.ProxyHost", "127.0.0.1");
        p.setProperty("http.ProxyUserName", null);
        p.setProperty("http.ProxyPassword", null);

        // Set this property to true if you are using the PayPal SDK within a Google App Engine java app
        p.setProperty("http.GoogleAppEngine", "false");

        // Service Configuration
        p.setProperty("service.RedirectURL", "https://www.sandbox.paypal.com/webscr&cmd=");
        p.setProperty("service.DevCentralURL", "https://developer.paypal.com");
        p.setProperty("service.IPNEndpoint", "https://ipnpb.sandbox.paypal.com/cgi-bin/webscr");

        // Multiple end-points configuration, while using multiple SDKs in combination, like Merchant and Permissions etc..
        // configure the end-points as shown below one for each service used, The existing service.EndPoint property is still
        // supported for backward compatibility (using a single SDK)
        // ------------------------------SANDBOX------------------------------ #
        // Merchant Service (3 Token)
        p.setProperty("service.EndPoint.PayPalAPI", "https://api-3t.sandbox.paypal.com/2.0");
        p.setProperty("service.EndPoint.PayPalAPIAA", "https://api-3t.sandbox.paypal.com/2.0");
        // Merchant Service (Certificate)
        // service.EndPoint.PayPalAPI=https://api.sandbox.paypal.com/2.0");
        // service.EndPoint.PayPalAPIAA=https://api.sandbox.paypal.com/2.0");
        // Permissions Platform Service
        p.setProperty("service.EndPoint.Permissions", "https://svcs.sandbox.paypal.com/");
        // AdaptivePayments Platform Service
        p.setProperty("service.EndPoint.AdaptivePayments", "https://svcs.sandbox.paypal.com/");
        // AdaptiveAccounts Platform Service
        p.setProperty("service.EndPoint.AdaptiveAccounts", "https://svcs.sandbox.paypal.com/");
        // Invoicing Platform Service
        p.setProperty("service.EndPoint.Invoice", "https://svcs.sandbox.paypal.com/");

        // ------------------------------PRODUCTION------------------------------ #
        // Merchant Service (3 Token)
        // service.EndPoint.PayPalAPI=https://api-3t.paypal.com/2.0
        // service.EndPoint.PayPalAPIAA=https://api-3t.paypal.com/2.0
        // Merchant Service (Certificate)
        // service.EndPoint.PayPalAPI=https://api.paypal.com/2.0
        // service.EndPoint.PayPalAPIAA=https://api.paypal.com/2.0
        // Permissions Platform Service
        // service.EndPoint.Permissions=https://svcs.paypal.com/
        return p;
    }

}
