package jna;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Surefire;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.unix.X11.Display;
import com.sun.jna.platform.unix.X11.KeySym;
import com.sun.jna.platform.unix.X11.Window;
import com.sun.jna.platform.unix.X11.XErrorEvent;
import com.sun.jna.platform.unix.X11.XEvent;
import com.sun.jna.platform.unix.X11.XKeyEvent;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testgroup.RequiresIsolatedVM;

import java.awt.GraphicsEnvironment;

@Category(RequiresIsolatedVM.class)
public class XGrabKeyTest {

    private static final Logger LOG = LoggerFactory.getLogger(XGrabKeyTest.class);

    @Test
    public void test() throws InterruptedException {
        assumeTrue(Surefire.isSingleTestExecution());
        assumeTrue(SystemUtils.IS_OS_UNIX);
        assumeFalse(GraphicsEnvironment.isHeadless());

        Display display = X11.INSTANCE.XOpenDisplay(null);
        assertNotNull(display);
        X11.INSTANCE.XSetErrorHandler(new X11.XErrorHandler() {

            @Override
            public int apply(Display display, XErrorEvent errorEvent) {
                LOG.warn("" + errorEvent);
                byte[] buf = new byte[4096];
                X11.INSTANCE.XGetErrorText(display, errorEvent.error_code, buf, buf.length);
                LOG.info("err msg: " + new String(buf));
                return 0;
            }
        });
        Window window = X11.INSTANCE.XDefaultRootWindow(display);
        KeySym sym = X11.INSTANCE.XStringToKeysym("F11");
        LOG.info("sym = " + sym);
        byte code = X11.INSTANCE.XKeysymToKeycode(display, sym);
        LOG.info("code = " + code);
        // ALT-F11, you need to add all combinations of extra keys that might be
        // active, like NUM LOCK, in order to work properly. Using the
        // X11.AnyModifier mask is not an option
        // because we'd have to send unnecessarily intercepted keypress onward
        // to the currently focused window - and that's not officially supported
        // by X11, though it could be done, although it's not clear how well
        // such a workaround would work.
        X11.INSTANCE.XGrabKey(display, code, X11.Mod1Mask, window, 1, X11.GrabModeSync, X11.GrabModeAsync);
        XEvent event = new XEvent();

        LOG.info("registration done.");
        while (true) {
            while (X11.INSTANCE.XPending(display) > 0) {
                X11.INSTANCE.XNextEvent(display, event);
                // LOG.info("" + event);
                XKeyEvent xkey = (XKeyEvent) event.readField("xkey");
                if (event.type == X11.KeyPress) {
                    LOG.info(String.format("key %d down, state: %d", xkey.keycode, xkey.state));
                }
                if (event.type == X11.KeyRelease) {
                    LOG.info(String.format("key %d up, state: %d", xkey.keycode, xkey.state));
                }
            }
            Thread.sleep(100);
        }
    }
}
