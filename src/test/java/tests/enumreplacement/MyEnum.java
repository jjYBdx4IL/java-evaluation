package tests.enumreplacement;

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
public class MyEnum {

    public static final MyEnum i = new MyEnum();

    public final int ONE = 1;
    public final int TWO = 2;

    private MyEnum() {
    }

    public int THREE() { return 3; }
}
