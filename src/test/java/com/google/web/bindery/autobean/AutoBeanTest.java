package com.google.web.bindery.autobean;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http://www.gwtproject.org/doc/latest/DevGuideAutoBeans.html
 *
 * @author jjYBdx4IL
 */
public class AutoBeanTest {

    private static final Logger LOG = LoggerFactory.getLogger(AutoBeanTest.class);

    // Instantiate the factory
    //MyFactory factory = GWT.create(MyFactory.class);
    MyFactory factory = AutoBeanFactorySource.create(MyFactory.class);
    // In non-GWT code, use AutoBeanFactorySource.create(MyFactory.class);

    Person makePerson() {
        // Construct the AutoBean
        AutoBean<Person> person = factory.person();

        // Return the Person interface shim
        return person.as();
    }
    
    Address makeAddress() {
        // Construct the AutoBean
        AutoBean<Address> address = factory.address();

        // Return the Person interface shim
        return address.as();
    }

    String serializeToJson(Person person) {
        // Retrieve the AutoBean controller
        AutoBean<Person> bean = AutoBeanUtils.getAutoBean(person);

        return AutoBeanCodex.encode(bean).getPayload();
    }

    Person deserializeFromJson(String json) {
        AutoBean<Person> bean = AutoBeanCodex.decode(factory, Person.class, json);
        return bean.as();
    }

    @Test
    public void test() {
        Person person = makePerson();
        person.setName("123");
        Address address = makeAddress();
        address.setStreet("str");
        person.setAddress(address);
        
        String ser = serializeToJson(person);
        LOG.info(ser);
        Person deSer = deserializeFromJson(ser);
        assertEquals("123", deSer.getName());
        assertEquals("str", deSer.getAddress().getStreet());
    }
}
