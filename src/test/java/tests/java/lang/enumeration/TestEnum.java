/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.java.lang.enumeration;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public enum TestEnum {

	ONE("1"),
	TWO("2");
	
	private final String value;
	
	TestEnum(String _value) {
		this.value = _value;
	}
	
    @Override
    public String toString () {
        return value;
    }

}
