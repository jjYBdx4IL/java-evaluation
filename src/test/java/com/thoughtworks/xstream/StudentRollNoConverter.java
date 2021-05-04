package com.thoughtworks.xstream;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import com.thoughtworks.xstream.converters.basic.IntConverter;

/**
 * allow "." indicate "no value"
 * 
 * @author Github jjYBdx4IL Projects
 *
 */
public class StudentRollNoConverter extends IntConverter {

	@Override
	public Object fromString(String value) {

		Object result = null;

		if (value != null && !(".".equals(value)))
			result = super.fromString(value);

		return result;
	}
	
}
