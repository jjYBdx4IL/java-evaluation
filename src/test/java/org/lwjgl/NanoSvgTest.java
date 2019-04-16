package org.lwjgl;

import static java.lang.Math.max;
import static org.junit.Assert.assertNotNull;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.nanovg.NanoSVG.nsvgCreateRasterizer;
import static org.lwjgl.nanovg.NanoSVG.nsvgParse;
import static org.lwjgl.nanovg.NanoSVG.nsvgRasterize;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.stb.STBImageResize.STBIR_COLORSPACE_SRGB;
import static org.lwjgl.stb.STBImageResize.STBIR_EDGE_CLAMP;
import static org.lwjgl.stb.STBImageResize.STBIR_FILTER_MITCHELL;
import static org.lwjgl.stb.STBImageResize.STBIR_FLAG_ALPHA_PREMULTIPLIED;
import static org.lwjgl.stb.STBImageResize.stbir_resize_uint8_generic;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memRealloc;

import org.junit.Test;
import org.lwjgl.nanovg.NSVGImage;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Locale;

/**
 * lots of stuff taken from:
 * 
 * https://github.com/LWJGL/lwjgl3/blob/3.1.6/modules/samples/src/test/java/org/lwjgl/demo/nanovg/SVGDemo.java
 *
 * @author jjYBdx4IL
 */
//@meta:keywords:svg,nanosvg,animation,rasterization@
public class NanoSvgTest extends LwjglTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(NanoSvgTest.class);
    
    private URL svgResource;
    private ByteBuffer image;
    int w,h;
    int texID;

    @Test
    public void test() throws Exception {
        run();
    }

    @Override
    public void loopInit() {
        svgResource = getClass().getResource("/org/apache/batik/apps/svgbrowser/resources/init.svg");
        assertNotNull(svgResource);
        load();
        texID = createTexture();
        glEnable(GL_TEXTURE_2D);
    }
    
    @Override
    public void loopExit() {
        glDisable(GL_TEXTURE_2D);
        glDeleteTextures(texID);
    }

    @Override
    public void loopIteration() {
        glViewport(0, 0, getWidth(), getHeight());
        glMatrixMode(GL_PROJECTION);
        float aspect = (float) getWidth() / getHeight();
        glLoadIdentity();
        glOrtho(-aspect, aspect, -1, 1, -1, 1);
        
        float zoom = (float) Math.sin(getElapsedTime()/100f);
        
        glBegin(GL_QUADS);
        {
            glTexCoord2f(0.0f, 0.0f);
            glVertex2f(-.8f*zoom, -.8f*zoom);

            glTexCoord2f(1.0f, 0.0f);
            glVertex2f(.8f*zoom, -.8f*zoom);

            glTexCoord2f(1.0f, 1.0f);
            glVertex2f(0.8f*zoom, .8f*zoom);

            glTexCoord2f(0.0f, 1.0f);
            glVertex2f(-.8f*zoom, .8f*zoom);
        }
        glEnd();
        
        glfwSwapBuffers(getWindow()); // swap the color buffers
    }
    
    private void load() {
        ByteBuffer svgData = memAlloc(128 * 1024);
        try {
            try (ReadableByteChannel rbc = Channels.newChannel(svgResource.openStream())) {
                int c;
                while ((c = rbc.read(svgData)) != -1) {
                    if (c == 0) {
                        ByteBuffer newData = memRealloc(svgData, (svgData.capacity() * 3) >> 1);
                        if (newData == null) {
                            throw new OutOfMemoryError();
                        }
                        svgData = newData;
                    }
                }
            }

            svgData.put((byte)0);
            svgData.flip();

            image = rasterize(svgData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            memFree(svgData);
        }        
    }
    
    private ByteBuffer rasterize(ByteBuffer svgData) {
        ByteBuffer img;
        NSVGImage svg;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            svg = nsvgParse(svgData, stack.ASCII("px"), 96.0f);
            if (svg == null) {
                throw new IllegalStateException("Failed to parse SVG.");
            }
        }

        long rast = nsvgCreateRasterizer();
        if (rast == NULL) {
            throw new IllegalStateException("Failed to create SVG rasterizer.");
        }

        w = (int)svg.width();
        h = (int)svg.height();

        img = memAlloc(w * h * 4);

        LOG.info(String.format(Locale.ROOT, "Rasterizing SVG image %d x %d...", w, h));
        long t = System.nanoTime();
        nsvgRasterize(rast, svg, 0, 0, 1, img, w, h, w * 4);
        t = System.nanoTime() - t;
        LOG.info(String.format(Locale.ROOT, "%.2fms", t * 1e-6));
        
        return img;
    }
    
    private int createTexture() {
        LOG.info("Creating texture...");
        long t = System.nanoTime();

        int texID = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, texID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

//        premultiplyAlpha();

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);

        ByteBuffer input_pixels = image;
        int        input_w      = w;
        int        input_h      = h;
        int        mipmapLevel  = 0;
        while (1 < input_w || 1 < input_h) {
            int output_w = max(1, input_w >> 1);
            int output_h = max(1, input_h >> 1);

            ByteBuffer output_pixels = memAlloc(output_w * output_h * 4);
            stbir_resize_uint8_generic(
                input_pixels, input_w, input_h, input_w * 4,
                output_pixels, output_w, output_h, output_w * 4,
                4, 3, STBIR_FLAG_ALPHA_PREMULTIPLIED,
                STBIR_EDGE_CLAMP,
                STBIR_FILTER_MITCHELL,
                STBIR_COLORSPACE_SRGB
            );

            memFree(input_pixels);

            glTexImage2D(GL_TEXTURE_2D, ++mipmapLevel, GL_RGBA, output_w, output_h, 0, GL_RGBA, GL_UNSIGNED_BYTE, output_pixels);

            input_pixels = output_pixels;
            input_w = output_w;
            input_h = output_h;
        }
        memFree(input_pixels);

        t = System.nanoTime() - t;
        LOG.info(String.format(Locale.ROOT, "%.2fms\n", t * 1e-6));

        return texID;
    }    
}
