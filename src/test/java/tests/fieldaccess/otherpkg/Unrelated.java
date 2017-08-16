package tests.fieldaccess.otherpkg;

import tests.fieldaccess.Animal;

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
public class Unrelated {

    public void test() {
        Animal animal = new Animal();
        // animal.privateField++; <-- does not compile
        // animal.defaultField++; <-- does not compile
        // animal.protectedField++;  <-- does not compile
        animal.publicField++;
    }
}
