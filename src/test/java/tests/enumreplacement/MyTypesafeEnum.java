package tests.enumreplacement;

import java.util.Objects;

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
public class MyTypesafeEnum {

    public static final MyTypesafeEnum i = new MyTypesafeEnum();

    private final String value;

    public MyTypesafeEnum() {
        value = null;
    }

    private MyTypesafeEnum(String value) {
        this.value = value;
    }

    public MyTypesafeEnum ONE() {
        return new MyTypesafeEnum("1");
    }

    public MyTypesafeEnum TWO() {
        return new MyTypesafeEnum("2");
    }

    public MyTypesafeEnum THREE() {
        return new MyTypesafeEnum("3");
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MyTypesafeEnum other = (MyTypesafeEnum) obj;
        if (this.value == null && other.value == null) {
            return true;
        }
        if (this.value == null && other.value != null) {
            return false;
        }
        if (!this.value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.value);
        return hash;
    }
}
