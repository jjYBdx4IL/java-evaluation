/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package com.helger.jcodemodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.github.jjYBdx4IL.utils.env.Maven;
import com.helger.jcodemodel.writer.JCMWriter;
import com.helger.jcodemodel.writer.SingleStreamCodeWriter;
import com.sun.codemodel.ExternalTestEnum;

/**
 * More: https://github.com/phax/jcodemodel/tree/master/src/test/java/com/helger/jcodemodel
 * 
 * @author Github jjYBdx4IL Projects
 */
public class CodeModelTest {

    private static File outDir = Maven.getTempTestDir(CodeModelTest.class);
    private JCodeModel cm = null;
    private JCMWriter writer = null;

    @Before
    public void beforeTest() throws IOException {
        FileUtils.cleanDirectory(outDir);
        cm = new JCodeModel();
        writer = new JCMWriter(cm);
        writer.setNewLine("\n");
        writer.setCharset(StandardCharsets.UTF_8);
    }

    @SuppressWarnings("unused")
    @Test
    public void testFileCodeWriter() throws JClassAlreadyExistsException, IOException {
        final JDefinedClass jDefindedClass = cm._class(JMod.PUBLIC, "org.sand.Test1", EClassType.CLASS);
        final JDefinedClass jDefindedClass2 = cm._class(JMod.PUBLIC, "org.sand.sub.Test2", EClassType.CLASS);
        writer.build(outDir);

        assertEquals("package org.sand;\n"
            + "\n"
            + "public class Test1 {\n"
            + "}\n",
            IOUtils.toString(new File(outDir, "org" + File.separator + "sand" + File.separator + "Test1.java").toURI(),
                StandardCharsets.UTF_8));
        assertEquals("package org.sand.sub;\n"
            + "\n"
            + "public class Test2 {\n"
            + "}\n",
            IOUtils.toString(
                new File(outDir,
                    "org" + File.separator + "sand" + File.separator + "sub" + File.separator + "Test2.java").toURI(),
                StandardCharsets.UTF_8));
    }

    @SuppressWarnings("unused")
    @Test
    public void testMethodBodyAndArrayAccess()
        throws ClassNotFoundException, JClassAlreadyExistsException, IOException {
        final JDefinedClass jDefindedClass = cm._class(JMod.PUBLIC, "org.sand.pit", EClassType.CLASS);
        final JMethod jmethod = jDefindedClass.method(JMod.PUBLIC, void.class, "testMethod");
        final JBlock jblock = jmethod.body();

        final JAtomInt equalsZero = JExpr.lit(0);
        final JVar jvarIndex = jblock.decl(JMod.FINAL, cm.parseType("int"), "arrayIndex", equalsZero);

        final JAtomInt getArraySize = JExpr.lit(100);
        final AbstractJClass wildcardClass = cm.ref("java.lang.Class");
        final JArray newClassArray = JExpr.newArray(wildcardClass, getArraySize);
        final JVar jvar = jblock.decl(JMod.FINAL, wildcardClass.array(), "parameterTypes", newClassArray);

        final JArrayCompRef theArray = JExpr.ref("parameterTypes").component(jvarIndex);
        jblock.assign(theArray, JExpr._null());
        writer.build(new SingleStreamCodeWriter(System.out));
    }

    @Test
    public void testInnerClass() throws JClassAlreadyExistsException, IOException {
        JDefinedClass outerClass = cm._class("org.test.DaTestClass");
        final JDefinedClass innerClass = outerClass._class("InnerClass");
        innerClass.method(JMod.PUBLIC, outerClass, "getOuter");
        writer.build(new SingleStreamCodeWriter(System.out));
    }

    @Test
    public void testPackageComment() throws IOException {
        cm._package("foo").javadoc().add("PackageComment for foo package.");
        writer.build(new SingleStreamCodeWriter(System.out));
    }

    @Test
    public void testClassComment() throws JClassAlreadyExistsException, IOException {
        JDefinedClass cls = cm._class("ClassWithComment");
        cls.javadoc().add("some class comment.");
        writer.build(new SingleStreamCodeWriter(System.out));
    }

    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidClassName() throws JClassAlreadyExistsException, IOException {
        JDefinedClass cls = cm._class("Class With");
    }

    @Test
    public void testDiamond() throws JClassAlreadyExistsException, IOException {
        JDefinedClass cls = cm._class("DiamondTest");

        AbstractJClass string = cm.ref(String.class);
        AbstractJClass hashmap = cm.ref(HashMap.class).narrow(string, string);
        cls.field(JMod.PUBLIC, hashmap, "map", JExpr._new(hashmap));

        writer.build(new SingleStreamCodeWriter(System.out));
    }

    @SuppressWarnings("unused")
    @Test
    public void testDuplicateClass() throws JClassAlreadyExistsException, IOException {
        JDefinedClass cls = cm._class("DuplicateClass");
        try {
            cls = cm._class("DuplicateClass");
            fail();
        } catch (JClassAlreadyExistsException ex) {
        }
        writer.build(new SingleStreamCodeWriter(System.out));
    }

    @Test
    public void testDuplicateEnumConstant() throws JClassAlreadyExistsException, IOException {
        JDefinedClass cls = cm.rootPackage()._class(JMod.PUBLIC, "MyEnum", EClassType.ENUM);
        cls.enumConstant("ONE").arg(JExpr.lit(true));
        cls.enumConstant("ONE").arg(JExpr.lit(true));
        writer.build(new SingleStreamCodeWriter(System.out));
    }

    @SuppressWarnings("unused")
    @Test
    public void testDuplicateMemberVariable() throws JClassAlreadyExistsException, IOException {
        JDefinedClass cls = cm._class("DuplicateMemberVariable");
        JFieldVar var = cls.field(JMod.PROTECTED | JMod.FINAL, String.class, "value", JExpr._null());
        try {
            var = cls.field(JMod.PROTECTED | JMod.FINAL, cm.INT, "value");
            fail();
        } catch (IllegalArgumentException ex) {
        }
        writer.build(new SingleStreamCodeWriter(System.out));
    }

    @SuppressWarnings("unused")
    @Test
    public void testDuplicateMethod() throws JClassAlreadyExistsException, IOException {
        JDefinedClass cls = cm._class("DuplicateMethod");
        JMethod m1 = cls.method(JMod.PROTECTED | JMod.FINAL, String.class, "value");
        JMethod m2 = cls.method(JMod.PROTECTED | JMod.FINAL, String.class, "value");
        writer.build(new SingleStreamCodeWriter(System.out));
    }

    @Test
    public void testEnum() throws JClassAlreadyExistsException, IOException {
        JDefinedClass cls = cm.rootPackage()._class(JMod.PUBLIC, "MyEnum", EClassType.ENUM);
        cls.enumConstant("ONE");
        cls.enumConstant("TWO");
        cls.enumConstant("THREE");
        writer.build(new SingleStreamCodeWriter(System.out));
    }

    @Test
    public void testEnumWithConstructor() throws Exception {
        JDefinedClass definedClass = cm.rootPackage()._class(JMod.PUBLIC, "MyEnumWithValues", EClassType.ENUM);

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

        writer.build(new SingleStreamCodeWriter(System.out));
    }

    // fixed codemodel bug:
    @Test
    public void testEnumSwitch() throws Exception {
        JDefinedClass cls = cm.rootPackage()._class(JMod.PUBLIC, "EnumSwitch", EClassType.ENUM);

        JMethod toStringMethod = cls.method(JMod.PUBLIC, String.class, "toString");
        toStringMethod.annotate(Override.class);
        JSwitch sw = toStringMethod.body()._switch(JExpr._this());

        String enumConstName = "AA";
        JEnumConstant enumConst = cls.enumConstant(enumConstName);
        sw._case(enumConst);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.build(new SingleStreamCodeWriter(baos));

        String s = baos.toString();
        assertTrue(s + "does not contain \"case EnumSwitch.AA:\"", s.contains("case AA:"));
    }

    // fixed codemodel bug:
    @Test
    public void testEnumSwitch2() throws Exception {
        JDefinedClass cls = cm.rootPackage()._class(JMod.PUBLIC, "EnumSwitch", EClassType.ENUM);

        JMethod toStringMethod = cls.method(JMod.PUBLIC, String.class, "toString");
        toStringMethod.annotate(Override.class);
        JSwitch sw = toStringMethod.body()._switch(JExpr._this());

        JEnumConstant enumConstAA = cls.enumConstant("AA");

        sw._case(enumConstAA);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.build(new SingleStreamCodeWriter(baos));

        String s = baos.toString();
        assertTrue(s + "does not contain \"case EnumSwitch.AA:\"", s.contains("case AA:"));
    }

    @Test
    public void testClassHierarchy() throws JClassAlreadyExistsException, IOException {
        JDefinedClass animalClass = cm._class("Animal");
        JDefinedClass dogClass = cm._class("Dog");
        dogClass._extends(animalClass);
        writer.build(new SingleStreamCodeWriter(System.out));
    }

    @Test
    public void testConstructorWithArgs() throws IOException, JClassAlreadyExistsException {
        JDefinedClass cls = cm._class("ConstructorWithArgs");
        JDefinedClass typeClass = cls._class("ConstructorArgumentType");
        JMethod constructor = cls.constructor(JMod.PUBLIC);
        constructor.param(typeClass, "dimension");
        writer.build(new SingleStreamCodeWriter(System.out));
    }

    @Test
    public void testGenToStringMethod() throws IOException, JClassAlreadyExistsException {
        JDefinedClass cls = cm._class("ConstructorWithEnumArgs");
        JDefinedClass enumClass = cm.rootPackage()._class(JMod.PUBLIC, "MyEnum", EClassType.ENUM);

        JMethod constructor = cls.constructor(JMod.PUBLIC);
        JVar param1 = constructor.param(enumClass, "arg0");
        JVar param2 = constructor.param(enumClass, "arg1");
        JFieldVar fieldVar1 = cls.field(JMod.PUBLIC | JMod.FINAL, enumClass, "arg0");
        JFieldVar fieldVar2 = cls.field(JMod.PUBLIC | JMod.FINAL, enumClass, "arg1");
        constructor.body().assign(JExpr._this().ref(fieldVar1), param1);
        constructor.body().assign(JExpr._this().ref(fieldVar2), param2);

        JMethod toStringMethod = cls.method(JMod.PUBLIC, String.class, "toString");
        toStringMethod.annotate(Override.class);
        AbstractJClass stringBuilder = cm.ref(StringBuilder.class);
        JVar sbVar = toStringMethod.body().decl(stringBuilder, "sb", JExpr._new(stringBuilder));
        toStringMethod.body().add(JExpr.invoke(sbVar, "append").arg(JExpr.lit(cls.name() + " [")));
        toStringMethod.body().add(JExpr.invoke(sbVar, "append").arg(fieldVar1));
        toStringMethod.body().add(JExpr.invoke(sbVar, "append").arg(JExpr.lit(", ")));
        toStringMethod.body().add(JExpr.invoke(sbVar, "append").arg(fieldVar2));
        toStringMethod.body().add(JExpr.invoke(sbVar, "append").arg(JExpr.lit("]")));
        toStringMethod.body()._return(sbVar.invoke("toString"));

        writer.build(new SingleStreamCodeWriter(System.out));
    }

    @Test
    public void testInterface() throws Exception {
        JDefinedClass cls = cm.rootPackage()._class(JMod.PUBLIC, "SomeInterface", EClassType.INTERFACE);

        JMethod toStringMethod = cls.method(JMod.PUBLIC, String.class, "toString");
        toStringMethod.annotate(Override.class);

        writer.build(new SingleStreamCodeWriter(System.out));
    }

    @Test
    public void testEnumExternalRef() throws JClassAlreadyExistsException, IOException {
        JDefinedClass cls = cm._class("TestEnumExternalRef");
        AbstractJClass externalEnum = cm.ref(ExternalTestEnum.class);
        cls.field(
            JMod.PROTECTED | JMod.FINAL,
            ExternalTestEnum.class,
            "value",
            externalEnum.staticRef(ExternalTestEnum.TWO.name())
        );
        writer.build(new SingleStreamCodeWriter(System.out));
    }
}
