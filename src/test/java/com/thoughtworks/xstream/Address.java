package com.thoughtworks.xstream;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

/**
 * Derived from: http://www.tutorialspoint.com/xstream/xstream_first_application.htm
 * @author Github jjYBdx4IL Projects
 *
 */
public class Address {

	private String area;
	private String city;
	private String state;
	private String country;

	private int pincode;

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public int getPincode() {
		return pincode;
	}

	public void setPincode(int pincode) {
		this.pincode = pincode;
	}

	public String toString(){

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("\nAddress [ ");
		stringBuilder.append("\narea: ");
		stringBuilder.append(area);
		stringBuilder.append("\ncity: ");
		stringBuilder.append(city);
		stringBuilder.append("\nstate: ");
		stringBuilder.append(state);
		stringBuilder.append("\ncountry: ");
		stringBuilder.append(country);
		stringBuilder.append("\npincode: ");	
		stringBuilder.append(pincode);
		stringBuilder.append(" ]");

		return stringBuilder.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Address other = (Address) obj;
		if (area == null) {
			if (other.area != null)
				return false;
		} else if (!area.equals(other.area))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (pincode != other.pincode)
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}
	
}