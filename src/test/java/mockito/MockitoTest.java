package mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class MockitoTest {

    @Mock
    ExampleInterface ei;

    // bring life to @Mock annotations
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test(expected = WantedButNotInvoked.class)
    public void test() {
        verify(ei).doSomething("");
    }
    
    @Test
    public void testVerifyInvocation() {
        assertFalse(ei.doSomething("123"));
        // verify that ei.doSomething("123") has been invoked
        verify(ei).doSomething("123");
    }
    
    @Test
    public void testSetReturnValue() {
        assertFalse(ei.doSomething("123"));
        when(ei.doSomething("123")).thenReturn(true);
        assertTrue(ei.doSomething("123"));
    }
    
}
