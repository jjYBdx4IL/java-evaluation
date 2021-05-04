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
public class Unrelated {

    public void test() {
        Animal animal = new Animal();
        // animal.privateField++; <-- does not compile
        animal.defaultField++;
        animal.protectedField++;
        animal.publicField++;
    }
}
