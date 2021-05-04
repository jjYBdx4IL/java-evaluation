package tests.fieldaccess;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2015 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class InnerClassTest {

    public class InnerAnimal {
        @SuppressWarnings("unused")
		private int privateField = 0;
        int defaultField = 0;
        protected int protectedField = 0;
        public int publicField = 0;
    }

    public class InnerDog extends InnerAnimal {

        public void test() {
            // privateField++;
            super.privateField++;
            defaultField++;
            protectedField++;
            publicField++;
        }
    }

    public class InnerUnrelated {

        public void test() {
            InnerClassTest.InnerAnimal animal = new InnerClassTest.InnerAnimal();
            animal.privateField++;
            animal.defaultField++;
            animal.protectedField++;
            animal.publicField++;
        }
    }
}
