package tests.enums;

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
public enum EnumWithMultipleConstructors {

    ONE, TWO(2);

    private final String value;

    private EnumWithMultipleConstructors(int i) {
        value = Integer.toString(i);
    }
    private EnumWithMultipleConstructors() {
        value = super.toString();
    }

    @Override
    public String toString() {
        return value;
    }
}
