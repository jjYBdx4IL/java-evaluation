package org.junit;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import java.util.Map;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

class TestWatcher2 extends TestWatcher {
    private Map<String, Throwable> protocol;
    
    TestWatcher2(Map<String, Throwable> protocol) {
        this.protocol = protocol;
    }

    @Override
    protected void succeeded(Description description) {
        protocol.put(description.getMethodName()+"-succeeded", null);
    }

    @Override
    protected void failed(Throwable e, Description description) {
        protocol.put(description.getMethodName()+"-failed", e);
    }

    @Override
    protected void starting(Description description) {
        protocol.put(description.getMethodName()+"-starting", null);
    }

    @Override
    protected void finished(Description description) {
        protocol.put(description.getMethodName()+"-finished", null);
    }
}