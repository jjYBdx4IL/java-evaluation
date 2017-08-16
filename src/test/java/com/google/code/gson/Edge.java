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
public class Edge {

    private String from;
    private String to;
    private String type;

    /**
     * @return the from
     */
    public String getFrom() {
        return from;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * @return the to
     */
    public String getTo() {
        return to;
    }

    /**
     * @param to the to to set
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Edge [");
        builder.append("from=");
        builder.append(from);
        builder.append(", to=");
        builder.append(to);
        builder.append(", type=");
        builder.append(type);
        builder.append("]");
        return builder.toString();
    }

    
}
