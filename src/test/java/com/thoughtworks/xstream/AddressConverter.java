/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package com.thoughtworks.xstream;

import com.thoughtworks.xstream.converters.SingleValueConverter;

public class AddressConverter implements SingleValueConverter  {

	public static final String separator = ";";
	
	public Object fromString(String addressString) {
		String[] parts = addressString.split(separator);
		String area = parts[0];
		String city = parts[1];
		String state = parts[2];
		String country = parts[3];
		int pincode = Integer.valueOf(parts[4]);
		Address address = new Address();
		address.setArea(area);
		address.setCity(city);
		address.setCountry(country);
		address.setState(state);
		address.setPincode(pincode);
		return address;
	}

	public String toString(Object addressObject) {
		Address address = (Address) addressObject;
		StringBuilder sb = new StringBuilder();
		sb.append(address.getArea());
		sb.append(separator);
		sb.append(address.getCity());
		sb.append(separator);
		sb.append(address.getState());
		sb.append(separator);
		sb.append(address.getCountry());
		sb.append(separator);
		sb.append(address.getPincode());
		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class type) {
		return type.equals(Address.class);
	}
}