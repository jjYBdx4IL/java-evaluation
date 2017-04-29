/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package com.thoughtworks.xstream;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class AdvancedAddressConverter implements Converter {

	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		Address address = (Address) value;
		writer.addAttribute("area", address.getArea());
		writer.addAttribute("city", address.getCity());
		writer.addAttribute("country", address.getCountry());
		writer.addAttribute("state", address.getState());
		writer.addAttribute("pincode", Integer.toString(address.getPincode()));
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		Address city = new Address();
		city.setArea(reader.getAttribute("area"));
		city.setCity(reader.getAttribute("city"));
		city.setCountry(reader.getAttribute("country"));
		city.setState(reader.getAttribute("state"));
		city.setPincode(Integer.valueOf(reader.getAttribute("pincode")));
		return city;
	}

	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class clazz) {
		return clazz.equals(Address.class);
	}
}