package com.github.javaparser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
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
}
