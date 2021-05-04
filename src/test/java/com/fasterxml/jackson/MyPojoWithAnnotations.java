package com.fasterxml.jackson;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import com.fasterxml.jackson.annotation.JsonProperty;

public class MyPojoWithAnnotations {

    private String product;
    private Long cost;

    @JsonProperty(value="PRODUCTDESC")
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
}