package com.paypal.sdk;

import java.io.File;

import com.github.jjYBdx4IL.utils.AbstractConfig;

public class PayPalLoginAPIConfig extends AbstractConfig {
	
	public String account = "";
	public String endpoint = "api.sandbox.paypal.com";
	public String appId = "";
	public String secret = "";
	public String tokenServiceUrl = "https://api.sandbox.paypal.com/v1/identity/openidconnect/tokenservice";
	public String userInfoUrl = "https://api.sandbox.paypal.com/v1/identity/openidconnect/userinfo/?schema=openid";
	
	public PayPalLoginAPIConfig() {
		super("PayPalSDK", true);
	}

	@Override
	protected void postprocess() {
		// check if user has edited the default config file
		if (account == null || account.isEmpty()) {
			File file = getConfigFile();
			throw new RuntimeException("please update " + file.getAbsolutePath());
		}
	}
	
}
