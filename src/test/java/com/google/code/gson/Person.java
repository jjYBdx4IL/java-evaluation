/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package com.google.code.gson;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class Person {

    private String name;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Person [");
        builder.append("name=");
        builder.append(name);
        builder.append("]");
        return builder.toString();
    }

}
