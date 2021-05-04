package com.google.web.bindery.autobean;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 *
 * @author jjYBdx4IL
 */
interface MyFactory extends AutoBeanFactory {

    AutoBean<Address> address();

    AutoBean<Person> person();
}
