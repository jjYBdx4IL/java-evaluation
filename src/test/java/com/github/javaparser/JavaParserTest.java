package com.github.javaparser;

import static com.github.jjYBdx4IL.utils.text.StringUtil.f;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.github.javaparser.ParserConfiguration.LanguageLevel;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.javadoc.JavadocBlockTag;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Optional;

/**
 * Java source code parser examples.
 * 
 * @author jjYBdx4IL
 */
public class JavaParserTest {

    private static final Logger LOG = LoggerFactory.getLogger(JavaParserTest.class);

    static {
        StaticJavaParser.getConfiguration().setLanguageLevel(LanguageLevel.CURRENT);
    }
    
    // beware: a comment line between javadoc and class spec makes the parser
    // fail to find the javadoc comment.
    @Test
    public void testJavadoc() {
        CompilationUnit compilationUnit = StaticJavaParser.parse("// @asd@\n"
            + "/** First doc sentence.\n"
            + " * B.\n"
            + " * @param key the key\n"
            + " * @keywords a,b, c\n"
            + " */\n"
            + "class A { }\n");
        Optional<ClassOrInterfaceDeclaration> classA = compilationUnit.getClassByName("A");
        assertTrue(classA.get().getJavadocComment().isPresent());
        assertEquals(" First doc sentence.\n * B.\n * @param key the key\n * @keywords a,b, c\n ",
            classA.get().getJavadocComment().get().getContent());
        assertEquals(" First doc sentence.\n * B.\n * @param key the key\n * @keywords a,b, c\n ",
            classA.get().getComment().get().getContent());

        assertTrue(classA.get().getJavadoc().isPresent());
        assertEquals(f("First doc sentence.%nB."),
            classA.get().getJavadoc().get().getDescription().toText());

        JavadocBlockTag bt = classA.get().getJavadoc().get().getBlockTags().get(0);
        assertEquals(JavadocBlockTag.Type.PARAM, bt.getType());
        assertEquals("key", bt.getName().get());
        assertEquals("the key", bt.getContent().toText());

        bt = classA.get().getJavadoc().get().getBlockTags().get(1);
        assertEquals(JavadocBlockTag.Type.UNKNOWN, bt.getType());
        assertFalse(bt.getName().isPresent());
        assertEquals("a,b, c", bt.getContent().toText());
    }

    @Test
    public void testGetClassByName() {
        // no package
        CompilationUnit compilationUnit = StaticJavaParser.parse("class A { }");
        Optional<ClassOrInterfaceDeclaration> classA = compilationUnit.getClassByName("A");
        assertTrue(classA.isPresent());

        // with package
        compilationUnit = StaticJavaParser.parse("package x; class A { }");
        classA = compilationUnit.getClassByName("x.A");
        assertFalse(classA.isPresent());
        classA = compilationUnit.getClassByName("A");
        assertTrue(classA.isPresent());
    }

    @Test
    public void test2() {
        CompilationUnit compilationUnit = StaticJavaParser.parse("class A { public int get() { return 123; } }");
        Optional<ClassOrInterfaceDeclaration> classA = compilationUnit.getClassByName("A");
        LOG.debug(classA.toString());
        assertEquals(1, classA.get().getMethodsBySignature("get").size());
    }

    @Test
    public void test3() {
        CompilationUnit compilationUnit = StaticJavaParser.parse("class A { public int get() { return abc; } }");
        Optional<ClassOrInterfaceDeclaration> classA = compilationUnit.getClassByName("A");
        LOG.debug(classA.toString());
        assertEquals(1, classA.get().getMethodsBySignature("get").size());
    }

    @Test
    public void testGenericVisitor() {
        CompilationUnit compilationUnit = StaticJavaParser
            .parse("class A { private boolean ok; public int get() { return abc; } }");

        GenericVisitor<Void, Object> visitor = new GenericVisitorAdapter<Void, Object>() {
            @Override
            public Void visit(MethodDeclaration n, Object arg) {
                LOG.info("method @ " + n.getRange().get());
                return super.visit(n, arg);
            }

            @Override
            public Void visit(FieldDeclaration n, Object arg) {
                LOG.info("field @ " + n.getRange().get());
                return super.visit(n, arg);
            }
        };

        compilationUnit.accept(visitor, null);
    }

    @Test
    public void testTreeVisitor() {
        CompilationUnit compilationUnit = StaticJavaParser
            .parse("class A { private boolean ok; public int get() { return abc; } }");

        TreeVisitor visitor = new TreeVisitor() {

            @Override
            public void process(Node node) {
                LOG.info(String.format(Locale.ROOT, "%s (%s-%s): %s", node.getClass().getName(),
                    node.getBegin().get().toString(), node.getEnd().get().toString(), node.toString()));
            }
        };

        visitor.visitPreOrder(compilationUnit);
    }
}
