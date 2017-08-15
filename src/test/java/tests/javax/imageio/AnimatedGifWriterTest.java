package tests.javax.imageio;

import com.github.jjYBdx4IL.test.FileUtil;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import org.junit.Before;
import org.junit.Test;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
/**
 * http://elliot.kroo.net/software/java/GifSequenceWriter/GifSequenceWriter.java
 *
 * @author Github jjYBdx4IL Projects
 */
public class AnimatedGifWriterTest {

    private final static File tempDir = FileUtil.createMavenTestDir(AnimatedGifWriterTest.class);

    @Before
    public void before() {
        FileUtil.provideCleanDirectory(tempDir);
    }

    @Test
    public void test() throws IOException {
        int bufferedImageType = BufferedImage.TYPE_BYTE_GRAY;
        int timeBetweenFramesMS = 40;
        boolean loopContinuously = true;
        ImageOutputStream ios = new FileImageOutputStream(new File(tempDir, "animated.gif"));

        ImageWriter writer = ImageIO.getImageWritersBySuffix("gif").next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        IIOMetadata imageMetaData = writer.getDefaultImageMetadata(
                ImageTypeSpecifier.createFromBufferedImageType(bufferedImageType), param);

        String metaFormatName = imageMetaData.getNativeMetadataFormatName();

        IIOMetadataNode root = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);

        IIOMetadataNode graphicsControlExtensionNode = getNode(
                root,
                "GraphicControlExtension");

        graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
        graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute(
                "transparentColorFlag",
                "FALSE");
        graphicsControlExtensionNode.setAttribute(
                "delayTime",
                Integer.toString(timeBetweenFramesMS / 10));
        graphicsControlExtensionNode.setAttribute(
                "transparentColorIndex",
                "0");

        IIOMetadataNode commentsNode = getNode(root, "CommentExtensions");
        commentsNode.setAttribute("CommentExtension", "Created by MAH");

        IIOMetadataNode appEntensionsNode = getNode(
                root,
                "ApplicationExtensions");

        IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");

        child.setAttribute("applicationID", "NETSCAPE");
        child.setAttribute("authenticationCode", "2.0");

        int loop = loopContinuously ? 0 : 1;

        child.setUserObject(new byte[]{0x1, (byte) (loop & 0xFF), (byte) ((loop >> 8) & 0xFF)});
        appEntensionsNode.appendChild(child);

        imageMetaData.setFromTree(metaFormatName, root);

        writer.setOutput(ios);

        writer.prepareWriteSequence(null);

        final int w = 100;
        final int h = 100;
        final int frames = 100;
        final int lastIndex = frames - 1;
        for (int i = 0; i <= lastIndex; i++) {
            BufferedImage img = new BufferedImage(w, h, bufferedImageType);
            Graphics2D g = (Graphics2D) img.getGraphics();
            int componentValue = (i * 255 / lastIndex);
            g.setColor(new Color(0xff000000 | componentValue | (componentValue << 8) | (componentValue << 16)));
            g.fillRect(0, 0, w, h);
            writer.writeToSequence(
                    new IIOImage(
                            img,
                            null,
                            imageMetaData),
                    param);
        }

        writer.endWriteSequence();
        ios.close();

    }

    /**
     * Returns an existing child node, or creates and returns a new child node (if the requested node does not
     * exist).
     *
     * @param rootNode the <tt>IIOMetadataNode</tt> to search for the child node.
     * @param nodeName the name of the child node.
     *
     * @return the child node, if found or a new node created with the given name.
     */
    private static IIOMetadataNode getNode(
            IIOMetadataNode rootNode,
            String nodeName) {
        int nNodes = rootNode.getLength();
        for (int i = 0; i < nNodes; i++) {
            if (rootNode.item(i).getNodeName().compareToIgnoreCase(nodeName)
                    == 0) {
                return ((IIOMetadataNode) rootNode.item(i));
            }
        }
        IIOMetadataNode node = new IIOMetadataNode(nodeName);
        rootNode.appendChild(node);
        return (node);
    }
}
