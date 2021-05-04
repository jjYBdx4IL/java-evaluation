package com.fasterxml.jackson;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import javax.xml.bind.annotation.XmlElement;

public class MyPojoWithXmlAnnotations {

    private String product;
    private Long cost;
    private String sometestattr;

    @XmlElement(name="PRODUCTDESC")
    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    /**
     * @return the sometestattr
     */
    @XmlElement(nillable=false)
    public String getSometestattr() {
        return sometestattr;
    }

    /**
     * @param sometestattr the sometestattr to set
     */
    public void setSometestattr(String sometestattr) {
        this.sometestattr = sometestattr;
    }
}