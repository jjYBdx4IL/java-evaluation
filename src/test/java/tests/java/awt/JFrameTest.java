package tests.java.awt;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import static org.junit.Assert.*;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.junit.Ignore;
import org.junit.Test;

public class JFrameTest {

	private AtomicBoolean windowClosed = new AtomicBoolean(false);

	@Ignore
	@Test
	public void testJFrame() throws InterruptedException {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFrame jf = createTestJFrame();
				jf.setVisible(true);
			}
		});
		assertFalse(SwingUtilities.isEventDispatchThread());
		synchronized (windowClosed) {
			while (!windowClosed.get()) {
				windowClosed.wait();
			}
		}
	}

	protected JFrame createTestJFrame() {
		JFrame jf = new JFrame("Test JFrame");
		jf.addWindowStateListener(new WindowStateListener() {

			@Override
			public void windowStateChanged(WindowEvent arg0) {
				System.out.println(arg0);
			}
		});
		jf.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent arg0) {
				System.out.println(arg0);
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
				System.out.println(arg0);
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
				System.out.println(arg0);
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				System.out.println(arg0);
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				System.out.println(arg0);
				synchronized (windowClosed) {
					windowClosed.set(true);
					windowClosed.notifyAll();
				}
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				System.out.println("XXX" + arg0);
			}

			@Override
			public void windowActivated(WindowEvent arg0) {
				System.out.println(arg0);
			}
		});
		jf.pack();
		jf.setSize(800, 600);
		jf.setLocationRelativeTo(null);
		return jf;
	}

	public void waitForSwing() {
		if (!SwingUtilities.isEventDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
					}
				});
			} catch (InterruptedException e) {
			} catch (InvocationTargetException e) {
			}
		}
	}
}
