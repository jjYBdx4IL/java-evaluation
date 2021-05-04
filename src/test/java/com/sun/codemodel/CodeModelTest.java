/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package com.sun.codemodel;

import com.github.jjYBdx4IL.utils.env.Maven;
import com.sun.codemodel.writer.FileCodeWriter;
import com.sun.codemodel.writer.SingleStreamCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

/**
 * @see
 * <a href="https://java.net/projects/codemodel/sources/svn/show/tags/codemodel-project-2.6/codemodel/src/test/java/com/sun/codemodel/tests">https://java.net/.../codemodel/tests</a>
 * @see <a href="http://www.programcreek.com/java-api-examples/index.php?api=com.sun.codemodel.JCodeModel">more examples</a>
 * @author Github jjYBdx4IL Projects
 */
public class CodeModelTest {

    private static File outDir = Maven.getTempTestDir(CodeModelTest.class);

    @Before
    public void beforeTest() throws IOException {
        FileUtils.cleanDirectory(outDir);
    }

    @SuppressWarnings("unused")
	@Test
    public void testFileCodeWriter() throws JClassAlreadyExistsException, IOException {
        JCodeModel cm = new JCodeModel();
        final JDefinedClass jDefindedClass = cm._class(JMod.PUBLIC, "org.sand.Test1", ClassType.CLASS);
        final JDefinedClass jDefindedClass2 = cm._class(JMod.PUBLIC, "org.sand.sub.Test2", ClassType.CLASS);
        cm.build(new FileCodeWriter(outDir));

        assertEquals("\npackage org.sand;\n"
                + "\n"
                + "\n"
                + "public class Test1 {\n"
                + "\n"
                + "\n"
                + "}\n", IOUtils.toString(new File(outDir, "org" + File.separator + "sand" + File.separator + "Test1.java").toURI(), "UTF-8").replace("\r\n", "\n"));
        assertEquals("\npackage org.sand.sub;\n"
                + "\n"
                + "\n"
                + "public class Test2 {\n"
                + "\n"
                + "\n"
                + "}\n", IOUtils.toString(new File(outDir, "org" + File.separator + "sand" + File.separator + "sub" + File.separator + "Test2.java").toURI(), "UTF-8").replace("\r\n", "\n"));
    }

    @SuppressWarnings("unused")
	@Test
    public void testMethodBodyAndArrayAccess() throws ClassNotFoundException, JClassAlreadyExistsException, IOException {
        JCodeModel cm = new JCodeModel();
        final JDefinedClass jDefindedClass = cm._class(JMod.PUBLIC, "org.sand.pit", ClassType.CLASS);
        final JMethod jmethod = jDefindedClass.method(JMod.PUBLIC, void.class, "testMethod");
        final JBlock jblock = jmethod.body();

        final JExpression equalsZero = JExpr.lit(0);
        final JVar jvarIndex = jblock.decl(JMod.FINAL, cm.parseType("int"), "arrayIndex", equalsZero);

        final JExpression getArraySize = JExpr.lit(100);
        final JClass wildcardClass = cm.ref("java.lang.Class");
        final JArray newClassArray = JExpr.newArray(wildcardClass, getArraySize);
        final JVar jvar = jblock.decl(JMod.FINAL, wildcardClass.array(), "parameterTypes", newClassArray);

        final JAssignmentTarget theArray = JExpr.ref("parameterTypes").component(jvarIndex);
        jblock.assign(theArray, JExpr._null());
        cm.build(new SingleStreamCodeWriter(System.out));
    }

    @Test
    public void testInnerClass() throws JClassAlreadyExistsException, IOException {
        JCodeModel cm = new JCodeModel();
        JDefinedClass outerClass = cm._class("org.test.DaTestClass");
        final JDefinedClass innerClass = outerClass._class("InnerClass");
        innerClass.method(JMod.PUBLIC, outerClass, "getOuter");
        cm.build(new SingleStreamCodeWriter(System.out));
    }

    @Test
    public void testPackageComment() throws IOException {
        JCodeModel cm = new JCodeModel();
        cm._package("foo").javadoc().add("PackageComment for foo package.");
        cm.build(new SingleStreamCodeWriter(System.out));
    }

    @Test
    public void testClassComment() throws JClassAlreadyExistsException, IOException {
        JCodeModel cm = new JCodeModel();
        JDefinedClass cls = cm._class("ClassWithComment");
        cls.javadoc().add("some class comment.");
        cm.build(new SingleStreamCodeWriter(System.out));
    }

    @SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
    public void testInvalidClassName() throws JClassAlreadyExistsException, IOException {
        JCodeModel cm = new JCodeModel();
        JDefinedClass cls = cm._class("Class With");
    }

    @SuppressWarnings("unused")
	@Test
    public void testDuplicateClass() throws JClassAlreadyExistsException, IOException {
        JCodeModel cm = new JCodeModel();
        JDefinedClass cls = cm._class("DuplicateClass");
        try {
            cls = cm._class("DuplicateClass");
            fail();
        } catch (JClassAlreadyExistsException ex) {
        }
        cm.build(new SingleStreamCodeWriter(System.out));
    }

    @Test
    public void testDuplicateEnumConstant() throws JClassAlreadyExistsException, IOException {
        JCodeModel cm = new JCodeModel();
        JDefinedClass cls = cm.rootPackage()._class(JMod.PUBLIC, "MyEnum", ClassType.ENUM);
        cls.enumConstant("ONE").arg(JExpr.lit(true));
        cls.enumConstant("ONE").arg(JExpr.lit(true));
        cm.build(new SingleStreamCodeWriter(System.out));
    }

    @SuppressWarnings("unused")
	@Test
    public void testDuplicateMemberVariable() throws JClassAlreadyExistsException, IOException {
        JCodeModel cm = new JCodeModel();
        JDefinedClass cls = cm._class("DuplicateMemberVariable");
        JFieldVar var = cls.field(JMod.PROTECTED | JMod.FINAL, String.class, "value", JExpr._null());
        try {
            var = cls.field(JMod.PROTECTED | JMod.FINAL, cm.INT, "value");
            fail();
        } catch (IllegalArgumentException ex) {
        }
        cm.build(new SingleStreamCodeWriter(System.out));
    }

    @SuppressWarnings("unused")
	@Test
    public void testDuplicateMethod() throws JClassAlreadyExistsException, IOException {
        JCodeModel cm = new JCodeModel();
        JDefinedClass cls = cm._class("DuplicateMethod");
        JMethod m1 = cls.method(JMod.PROTECTED | JMod.FINAL, String.class, "value");
        JMethod m2 = cls.method(JMod.PROTECTED | JMod.FINAL, String.class, "value");
        cm.build(new SingleStreamCodeWriter(System.out));
    }

    @Test
    public void testEnum() throws JClassAlreadyExistsException, IOException {
        JCodeModel cm = new JCodeModel();
        JDefinedClass cls = cm.rootPackage()._class(JMod.PUBLIC, "MyEnum", ClassType.ENUM);
        cls.enumConstant("ONE");
        cls.enumConstant("TWO");
        cls.enumConstant("THREE");
        cm.build(new SingleStreamCodeWriter(System.out));
    }

    @Test
    public void testEnumWithConstructor() throws Exception {
        JCodeModel cm = new JCodeModel();
        JDefinedClass definedClass = cm.rootPackage()._class(JMod.PUBLIC, "MyEnumWithValues", ClassType.ENUM);

        JFieldVar field1 = definedClass.field(JMod.PRIVATE | JMod.FINAL, String.class, "column");
        JFieldVar field2 = definedClass.field(JMod.PRIVATE | JMod.FINAL, Boolean.class, "filterable");
        JFieldVar field3 = definedClass.field(JMod.PRIVATE | JMod.FINAL, Boolean.class, "includeInHavingClause");

        JMethod constructor = definedClass.constructor(JMod.PRIVATE);
        JVar param1 = constructor.param(String.class, "column");
        JVar param2 = constructor.param(Boolean.class, "filterable");
        JVar param3 = constructor.param(Boolean.class, "includeInHavingClause");

        JBlock body = constructor.body();
        body.assign(JExpr._this().ref(field1), param1);
        body.assign(JExpr._this().ref(field2), param2);
        body.assign(JExpr._this().ref(field3), param3);

        JEnumConstant enumMonth = definedClass.enumConstant("MONTH");
        enumMonth.arg(JExpr.lit("month"));
        enumMonth.arg(JExpr.lit(true));
        enumMonth.arg(JExpr.lit(false));

        JEnumConstant enumDay = definedClass.enumConstant("DAY");
        enumDay.arg(JExpr.lit("day"));
        enumDay.arg(JExpr.lit(false));
        enumDay.arg(JExpr.lit(true));

        cm.build(new SingleStreamCodeWriter(System.out));
    }

    // codemodel bug:
    @SuppressWarnings("unused")
	@Test
    public void testEnumSwitch() throws Exception {
        JCodeModel cm = new JCodeModel();
        JDefinedClass cls = cm.rootPackage()._class(JMod.PUBLIC, "EnumSwitch", ClassType.ENUM);

        JMethod toStringMethod = cls.method(JMod.PUBLIC, String.class, "toString");
        toStringMethod.annotate(Override.class);
        JSwitch sw = toStringMethod.body()._switch(JExpr._this());

        String enumConstName = "AA";
        JEnumConstant enumConst = cls.enumConstant(enumConstName);
        JCase cse = sw._case(enumConst);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        cm.build(new SingleStreamCodeWriter(baos));

        assertTrue(baos.toString().contains("case EnumSwitch.AA:"));
    }

    @Test
    public void testClassHierarchy() throws JClassAlreadyExistsException, IOException {
        JCodeModel cm = new JCodeModel();
        JDefinedClass animalClass = cm._class("Animal");
        JDefinedClass dogClass = cm._class("Dog");
        dogClass._extends(animalClass);
        cm.build(new SingleStreamCodeWriter(System.out));
    }

    @Test
    public void testConstructorWithArgs() throws IOException, JClassAlreadyExistsException {
        JCodeModel cm = new JCodeModel();
        JDefinedClass cls = cm._class("ConstructorWithArgs");
        JDefinedClass typeClass = cls._class("ConstructorArgumentType");
        JMethod constructor = cls.constructor(JMod.PUBLIC);
        constructor.param(typeClass, "dimension");
        cm.build(new SingleStreamCodeWriter(System.out));
    }

    @Test
    public void testGenToStringMethod() throws IOException, JClassAlreadyExistsException {
        JCodeModel cm = new JCodeModel();
        JDefinedClass cls = cm._class("ConstructorWithEnumArgs");
        JDefinedClass enumClass = cm.rootPackage()._class(JMod.PUBLIC, "MyEnum", ClassType.ENUM);

        JMethod constructor = cls.constructor(JMod.PUBLIC);
        JVar param1 = constructor.param(enumClass, "arg0");
        JVar param2 = constructor.param(enumClass, "arg1");
        JFieldVar fieldVar1 = cls.field(JMod.PUBLIC | JMod.FINAL, enumClass, "arg0");
        JFieldVar fieldVar2 = cls.field(JMod.PUBLIC | JMod.FINAL, enumClass, "arg1");
        constructor.body().assign(JExpr._this().ref(fieldVar1), param1);
        constructor.body().assign(JExpr._this().ref(fieldVar2), param2);

        JMethod toStringMethod = cls.method(JMod.PUBLIC, String.class, "toString");
        toStringMethod.annotate(Override.class);
        JClass stringBuilder = cm.ref(StringBuilder.class);
        JVar sbVar = toStringMethod.body().decl(stringBuilder, "sb", JExpr._new(stringBuilder));
        toStringMethod.body().invoke(sbVar, "append").arg(JExpr.lit(cls.name() + " ["));
        toStringMethod.body().invoke(sbVar, "append").arg(fieldVar1);
        toStringMethod.body().invoke(sbVar, "append").arg(JExpr.lit(", "));
        toStringMethod.body().invoke(sbVar, "append").arg(fieldVar2);
        toStringMethod.body().invoke(sbVar, "append").arg(JExpr.lit("]"));
        toStringMethod.body()._return(sbVar.invoke("toString"));

        cm.build(new SingleStreamCodeWriter(System.out));
    }

    @Test
    public void testInterface() throws Exception {
        JCodeModel cm = new JCodeModel();
        JDefinedClass cls = cm.rootPackage()._class(JMod.PUBLIC, "SomeInterface", ClassType.INTERFACE);

        JMethod toStringMethod = cls.method(JMod.PUBLIC, String.class, "toString");
        toStringMethod.annotate(Override.class);
        
        cm.build(new SingleStreamCodeWriter(System.out));
    }

	@Test
    public void testEnumExternalRef() throws JClassAlreadyExistsException, IOException {
        JCodeModel cm = new JCodeModel();
        JDefinedClass cls = cm._class("TestEnumExternalRef");
        JClass externalEnum = cm.ref(ExternalTestEnum.class);
        cls.field(
        		JMod.PROTECTED | JMod.FINAL,
        		ExternalTestEnum.class,
        		"value",
        		externalEnum.staticRef(ExternalTestEnum.TWO.name())
        		);
        cm.build(new SingleStreamCodeWriter(System.out));
    }
}
