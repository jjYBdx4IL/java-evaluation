package tests.javax.xml.bind;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Customer {

    @XmlElement
    String name;
    @XmlElement
    int age;
    @XmlAttribute
    int id;
    @XmlElement
    @XmlJavaTypeAdapter(UpperCaseStringConverterJaxbAdapter.class)
    String name2;

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Customer [name=" + name + ", age=" + age + ", id=" + id + ", name2=" + name2 + "]";
    }

  
}