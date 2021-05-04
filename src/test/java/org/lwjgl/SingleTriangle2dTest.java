package org.lwjgl;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import org.junit.Test;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author jjYBdx4IL
 */
public class SingleTriangle2dTest extends LwjglTestBase {

    @Test
    public void test() throws Exception {
        run();
    }

    @Override
    public void loopInit() {
        int vbo = glGenBuffers();
        int ibo = glGenBuffers();
        float[] vertices = { -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f };
        int[] indices = { 0, 1, 2 };
        // http://www.songho.ca/opengl/gl_vbo.html
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, (FloatBuffer) BufferUtils.createFloatBuffer(vertices.length).put(vertices).flip(),
                GL_STATIC_DRAW);
        glEnableClientState(GL_VERTEX_ARRAY);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,
                (IntBuffer) BufferUtils.createIntBuffer(indices.length).put(indices).flip(), GL_STATIC_DRAW);
        glVertexPointer(2, GL_FLOAT, 0, 0L);
    }

    @Override
    public void loopIteration() {
        glViewport(0, 0, getWidth(), getHeight());
        glMatrixMode(GL_PROJECTION);
        float aspect = (float) getWidth() / getHeight();
        glLoadIdentity();
        glOrtho(-aspect, aspect, -1, 1, -1, 1);
        glDrawElements(GL_TRIANGLES, 3, GL_UNSIGNED_INT, 0L);

        glfwSwapBuffers(getWindow()); // swap the color buffers
    }
}
