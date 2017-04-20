package org.commonmark;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class CommonMarkVisitor extends AbstractVisitor {

    private static final Logger LOG = LoggerFactory.getLogger(CommonMarkVisitor.class);
    
    @Override
    public void visit(Text text) {
        LOG.info(text.getClass().getName());
        Node parent = text.getParent();
        if (parent != null && parent instanceof Heading) {
            Heading heading = (Heading) parent;
            if (heading.getLevel() == 1) {
                LOG.info("Blog title: " + text.getLiteral());
            }
            if (heading.getLevel() == 2) {
                LOG.info("RSS entry: " + text.getLiteral());
            }
        }
        
        visitChildren(text);
    }
}
