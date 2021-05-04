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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerWithStringList extends Customer {

    List<String> someList;

    /**
     * @return the someList
     */
    public List<String> getSomeList() {
        return someList;
    }

    /**
     * @param someList the someList to set
     */
    public void setSomeList(List<String> someList) {
        this.someList = someList;
    }

}