package com.github.javaparser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

import java.util.Optional;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class JavaParserTest {

    private static final Logger LOG = LoggerFactory.getLogger(JavaParserTest.class);

    @Test
    public void test() {
        CompilationUnit compilationUnit = JavaParser.parse("class A { }");
        Optional<ClassOrInterfaceDeclaration> classA = compilationUnit.getClassByName("A");
        LOG.debug(classA.toString());
    }

    @Test
    public void test2() {
        CompilationUnit compilationUnit = JavaParser.parse("class A { public int get() { return 123; } }");
        Optional<ClassOrInterfaceDeclaration> classA = compilationUnit.getClassByName("A");
        LOG.debug(classA.toString());
        assertEquals(1, classA.get().getMethodsBySignature("get").size());
    }

    @Test
    public void test3() {
        CompilationUnit compilationUnit = JavaParser.parse("class A { public int get() { return abc; } }");
        Optional<ClassOrInterfaceDeclaration> classA = compilationUnit.getClassByName("A");
        LOG.debug(classA.toString());
        assertEquals(1, classA.get().getMethodsBySignature("get").size());
    }

    @Test
    public void testGenericVisitor() {
        CompilationUnit compilationUnit = JavaParser
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
}
