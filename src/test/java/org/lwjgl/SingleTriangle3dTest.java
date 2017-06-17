package org.lwjgl;

import com.github.jjYBdx4IL.utils.env.Surefire;
import static org.junit.Assume.assumeTrue;
import org.junit.Test;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 *
 * @author jjYBdx4IL
 */
public class SingleTriangle3dTest extends LwjglTestBase {

    @Test
    public void test() throws Exception {
        assumeTrue(Surefire.isSingleTestExecution());

        run();
    }

    @Override
    public void loopInit() {
        int vbo = glGenBuffers();
        int ibo = glGenBuffers();
        float[] vertices = { -0.5f, -0.5f, 0.0f, 0.5f, -0.5f, 0.0f, 0.5f, 0.5f, 0.0f };
        int[] indices = { 0, 1, 2 };
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, (FloatBuffer) BufferUtils.createFloatBuffer(vertices.length).put(vertices).flip(),
                GL_STATIC_DRAW);
        glEnableClientState(GL_VERTEX_ARRAY);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,
                (IntBuffer) BufferUtils.createIntBuffer(indices.length).put(indices).flip(), GL_STATIC_DRAW);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
    }

    @Override
    public void loopIteration() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glMatrixMode(GL_MODELVIEW);
        glEnable(GL_DEPTH_TEST);
        glDrawElements(GL_TRIANGLES, 3, GL_UNSIGNED_INT, 0L);

        glfwSwapBuffers(getWindow()); // swap the color buffers
    }
}
