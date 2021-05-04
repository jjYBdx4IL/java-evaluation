package com.thoughtworks.xstream;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import com.thoughtworks.xstream.annotations.XStreamConverter;

/**
 * Derived from: http://www.tutorialspoint.com/xstream/xstream_first_application.htm
 * @author Github jjYBdx4IL Projects
 *
 */
public class Student {

	@XStreamConverter(StudentRollNoConverter.class)
	private Integer rollNo;

	private String firstName;
	private String lastName;
	private String className;

	private Address address;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Integer getRollNo() {
		return rollNo;
	}

	public void setRollNo(Integer rollNo) {
		this.rollNo = rollNo;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

        @Override
	public String toString(){

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("Student [ ");
		stringBuilder.append("\nfirstName: ");
		stringBuilder.append(firstName);
		stringBuilder.append("\nlastName: ");
		stringBuilder.append(lastName);
		stringBuilder.append("\nrollNo: ");
		stringBuilder.append(rollNo);
		stringBuilder.append("\nclassName: ");
		stringBuilder.append(className);
		stringBuilder.append("\naddress: ");
		stringBuilder.append(address);
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
		Student other = (Student) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (rollNo == null) {
			if (other.rollNo != null)
				return false;
		} else if (!rollNo.equals(other.rollNo))
			return false;
		return true;
	}
}