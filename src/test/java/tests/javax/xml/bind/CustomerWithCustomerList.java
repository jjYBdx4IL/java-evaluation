package tests.javax.xml.bind;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerWithCustomerList extends Customer {

    @XmlElement
    private List<Customer> customer;

    /**
     * @return the customer
     */
    public List<Customer> getRefs() {
        return customer;
    }

    /**
     * @param customer the customer to set
     */
    public void setRefs(List<Customer> customer) {
        this.customer = customer;
    }

}