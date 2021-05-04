package tests.fieldaccess;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class Dog extends Animal {

    public void test() {
        // privateField++;       <-- not compiling
        // super.privateField++; <-- not compiling
        defaultField++;
        protectedField++;
        publicField++;
    }
}
