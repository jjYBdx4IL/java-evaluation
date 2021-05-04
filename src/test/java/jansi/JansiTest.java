package jansi;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;

import org.fusesource.jansi.AnsiConsole;
import org.junit.Test;

/**
 * Using jansi lib for colorful console output. Might require an additional
 * binary lib on Windows.
 * 
 * @see https://github.com/fusesource/jansi
 * @author jjYBdx4IL
 */
public class JansiTest {

    @Test
    public void test() {
        AnsiConsole.systemInstall();
        System.out.println(ansi().eraseScreen().fg(RED).a("Hello").fg(GREEN).a(" World").reset());
        AnsiConsole.systemUninstall();
    }
}
