package com.github.javaparser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.typesystem.Type;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.jjYBdx4IL.utils.env.Maven;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class JavaSymbolSolverTest {

    private static final Logger LOG = LoggerFactory.getLogger(JavaSymbolSolverTest.class);
    private static final File TEMP_DIR = Maven.getTempTestDir(JavaSymbolSolverTest.class);
    
    @Before
    public void before() throws IOException {
        FileUtils.cleanDirectory(TEMP_DIR);
    }
    
    @Ignore // not working
    @Test
    public void test() throws FileNotFoundException, IOException {
        String classContent = "class A { public int get() { return 123; } }";
        File classFile = new File(TEMP_DIR, "A.java");
        
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        combinedTypeSolver.add(new JavaParserTypeSolver(TEMP_DIR));
//        combinedTypeSolver.add(new JavaParserTypeSolver(new File("src/test/resources/javaparser_src/generated")));

        FileUtils.write(classFile, classContent, "UTF-8");

        CompilationUnit compilationUnit = JavaParser.parse(classFile);
        Optional<ClassOrInterfaceDeclaration> classA = compilationUnit.getClassByName("A");
        LOG.debug(classA.toString());
        assertEquals(1, classA.get().getMethodsBySignature("get").size());
        
        Node node = classA.get().getParentNodeForChildren();
        Type typeOfTheNode = JavaParserFacade.get(combinedTypeSolver).getType(node);
        
        assertNotNull(typeOfTheNode);
    }

}
