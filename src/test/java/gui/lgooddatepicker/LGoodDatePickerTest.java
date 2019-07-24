package gui.lgooddatepicker;

import static org.junit.Assert.assertEquals;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;
import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.github.lgooddatepicker.components.TimePickerSettings.TimeArea;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import testgroup.RequiresIsolatedVM;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.DayOfWeek;
import java.time.LocalTime;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * https://github.com/LGoodDatePicker/LGoodDatePicker/blob/master/Project/src/main/java/com/github/lgooddatepicker/demo/
 *
 */
@Category(RequiresIsolatedVM.class)
public class LGoodDatePickerTest {

    private static final File TEMP_DIR = Maven.getTempTestDir(LGoodDatePickerTest.class);

    static class Demo extends JFrame {
        private static final long serialVersionUID = 1L;
        private DateTimePicker dateTimePicker1;
        private DatePicker datePicker1;

        Demo() {
            super("Demo");
            setLayout(new FlowLayout());
            setSize(new Dimension(640, 480));
            setLocationRelativeTo(null);

            datePicker1 = new DatePicker();
            add(datePicker1);

            // Create a time picker, and add it to the form.
            TimePicker timePicker1 = new TimePicker();
            add(timePicker1);

            dateTimePicker1 = new DateTimePicker();
            add(dateTimePicker1);

            // Create a date picker with some custom settings.
            DatePickerSettings dateSettings = new DatePickerSettings();
            dateSettings.setFirstDayOfWeek(DayOfWeek.MONDAY);
            DatePicker datePicker2 = new DatePicker(dateSettings);
            add(datePicker2);

            // Create a time picker with some custom settings.
            TimePickerSettings timeSettings = new TimePickerSettings();
            timeSettings.setColor(TimeArea.TimePickerTextValidTime, Color.blue);
            timeSettings.initialTime = LocalTime.now();
            TimePicker timePicker2 = new TimePicker(timeSettings);
            add(timePicker2);
        }
    }

    @Test
    public void test() throws InvocationTargetException, InterruptedException, IOException {
        final Demo demo = new Demo();
        demo.pack();
        demo.setVisible(true);
        
        SwingUtilities.invokeAndWait(new Runnable() {
            
            @Override
            public void run() {
                demo.datePicker1.openPopup();
            }
        });
        
        assertEquals(2, Window.getWindows().length);

        AWTUtils.writeToPng(Window.getWindows()[0], new File(TEMP_DIR, "test0.png"));
        // @insert:image:test0.png@
        AWTUtils.writeToPng(Window.getWindows()[1], new File(TEMP_DIR, "test1.png"));
        // @insert:image:test1.png@
        
        AWTUtils.showFrameAndWaitForCloseByUserTest(demo);
    }
}
