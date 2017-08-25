package org.mozilla.rhino;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mozilla.javascript.*;

public class RhinoTest {

	private static final Logger LOG = LoggerFactory.getLogger(RhinoTest.class);

	@Test
	public void test() {
		Context cx = Context.enter();
		try {
			// Initialize the standard objects (Object, Function, etc.)
			// This must be done before scripts can be executed. Returns
			// a scope object that we use in later calls.
			Scriptable scope = cx.initStandardObjects();

			String s = "Math.cos(Math.PI);";

			// Now evaluate the string we've colected.
			Object result = cx.evaluateString(scope, s, "<cmd>", 1, null);

			// Convert the result to a string and print it.
			LOG.info(Context.toString(result));
		} finally {
			// Exit from the context.
			Context.exit();
		}
	}
}
