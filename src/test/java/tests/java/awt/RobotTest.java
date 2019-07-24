package tests.java.awt;

import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Surefire;

import org.junit.Test;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class RobotTest {

    @Test
    public void testKeyCombos() throws AWTException {
        assumeTrue(Surefire.isSingleTestExecution());
        
        Robot bot = new Robot();
        bot.keyPress(KeyEvent.VK_CONTROL);
        bot.keyPress(KeyEvent.VK_O);
        bot.keyRelease(KeyEvent.VK_O);
        bot.keyRelease(KeyEvent.VK_CONTROL);
    }
}
