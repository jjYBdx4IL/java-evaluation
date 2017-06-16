package org.lwjgl;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Locale;

import javax.swing.Timer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * from
 * https://github.com/LWJGL/lwjgl3-demos/blob/master/src/org/lwjgl/demo/opengl/SimpleDrawElements.java
 *
 * @author jjYBdx4IL
 */
public abstract class LwjglTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(LwjglTestBase.class);

    // We need to strongly reference callback instances.

    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback keyCallback;
    private GLFWWindowSizeCallback wsCallback;
    private Callback debugProc;

    // The window handle
    private long window;
    private int width, height;

    // statistics
    private volatile int frameCount = 0;
    private long startTimeMs = 0;
    private static final int LOG_FPS_IVAL_MS = 10000;

    public void run() {
        try {
            init();
            loop();

            // Release window and window callbacks
            glfwDestroyWindow(window);
            keyCallback.free();
            wsCallback.free();
            if (debugProc != null) {
                debugProc.free();
            }
        } finally {
            // Terminate GLFW and release the GLFWerrorfun
            glfwTerminate();
            errorCallback.free();
        }
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are
                                  // already the default
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 1);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden
                                                  // after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be
                                                   // resizable

        int WIDTH = 300;
        int HEIGHT = 300;

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Setup a key callback. It will be called every time a key is pressed,
        // repeated or released.
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                    glfwSetWindowShouldClose(window, true); // We will detect
                                                            // this in our
                                                            // rendering loop
                }
            }
        });
        glfwSetWindowSizeCallback(window, wsCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int w, int h) {
                LOG.info("window resized");
                if (w > 0 && h > 0) {
                    width = w;
                    height = h;
                }
            }
        });

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the ContextCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
        debugProc = GLUtil.setupDebugMessageCallback();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        startTimeMs = System.currentTimeMillis();
        final Timer fpsLogtimer = new Timer(LOG_FPS_IVAL_MS, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                LOG.info(String.format(Locale.ROOT, "%.2f fps",
                        1000f * frameCount / (System.currentTimeMillis() - startTimeMs)));
            }
        });
        fpsLogtimer.start();
    }

    public abstract void loopInit();

    public abstract void loopIteration();

    private void loop() {
        loopInit();
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            frameCount++;
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the
                                                                // framebuffer
            glViewport(0, 0, getWidth(), getHeight());

            loopIteration();

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getWindow() {
        return window;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public float getAspect() {
        return (float) getWidth() / getHeight();
    }
}
