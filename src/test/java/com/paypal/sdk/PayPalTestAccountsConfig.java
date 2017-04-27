package com.paypal.sdk;

import java.io.File;

import com.github.jjYBdx4IL.utils.AbstractConfig;

public class PayPalTestAccountsConfig extends AbstractConfig {
	
	public String seller1Email = "";
	public String deBuyerEmail = "";
	public String deBuyerPassword = "";
	public String usBuyerEmail = "";
	public String usBuyerPassword = "";
	
	public PayPalTestAccountsConfig() {
		super("PayPalSDK", true);
	}

	@Override
	protected void postprocess() {
		// check if user has edited the default config file
		if (seller1Email == null || seller1Email.isEmpty()) {
			File file = getConfigFile();
			throw new RuntimeException("please update " + file.getAbsolutePath());
		}
	}
	
}
