/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 - 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package tests.javalimits;

import java.util.Random;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JEnumConstant;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;
import com.sun.codemodel.writer.FileCodeWriter;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class LimitsTest extends JavaCodeTestBase {

    @SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(LimitsTest.class.getName());
    
    /**
     * There are a few limits, for example regarding the static initializer code. The following example tries
     * to circumvent that by some degree by moving the value association from the constructor/initialization
     * to the toString() method.
     *
     * @throws Exception
     */
    @SuppressWarnings("unused")
	@Test
    public void testEnumLimits() throws Exception {
        JCodeModel cm = new JCodeModel();
        JDefinedClass cls = cm.rootPackage()._class(JMod.PUBLIC, OUTPUT_CLASS_NAME, ClassType.ENUM);

//        JMethod toStringMethod = cls.method(JMod.PUBLIC, String.class, "toString");
//        JSwitch sw = toStringMethod.body()._switch(JExpr._this());
        for (int i = 0; i < 10000; i++) {
            String enumConstName = "_" + i;
            JEnumConstant enumConst = cls.enumConstant(enumConstName);
//            JCase cse = sw._case(JExpr.ref(enumConstName));
//            cse.body()._return(JExpr.lit(Integer.toString(i)));
        }
//        toStringMethod.body()._throw(JExpr._new(cm.parseType(IllegalStateException.class.getName())));

        cm.build(new FileCodeWriter(outDir));
        //cm.build(new SingleStreamCodeWriter(System.out));
        testJavac(false, "error: code too large");
    }

    @Test
    public void testMethodLimits() throws Exception {
        JCodeModel cm = new JCodeModel();
        JDefinedClass cls = cm._class(OUTPUT_CLASS_NAME);

        for (int i = 0; i < 22000; i++) {
            String methodName = "_" + i;
            JMethod m = cls.method(JMod.PUBLIC, String.class, methodName);
            m.body()._return(JExpr.lit(Integer.toString(i)));
        }

        cm.build(new FileCodeWriter(outDir));
        testJavac(false, "error: too many constants");
        //cm.build(new SingleStreamCodeWriter(System.out));

    }

    @Test
    public void testFunctionLimits() throws Exception {
        JCodeModel cm = new JCodeModel();
        JDefinedClass cls = cm._class(OUTPUT_CLASS_NAME);

        for (int i = 0; i < 22000; i++) {
            String methodName = "_" + i;
            JMethod m = cls.method(JMod.PUBLIC | JMod.STATIC, String.class, methodName);
            m.body()._return(JExpr.lit(Integer.toString(i)));
        }

        cm.build(new FileCodeWriter(outDir));
        testJavac(false, "error: too many constants");
        //cm.build(new SingleStreamCodeWriter(System.out));

    }

    @Test
    public void testTypesafeEnumReplacementLimits() throws Exception {
        JCodeModel cm = new JCodeModel();
        JDefinedClass cls = cm._class(OUTPUT_CLASS_NAME);

        Random r = new Random(0);
        JFieldVar valueField = cls.field(JMod.PRIVATE, String.class, "value");

        JMethod constructor = cls.constructor(JMod.PRIVATE);
        JVar cArg = constructor.param(String.class, "arg");
        constructor.body().assign(JExpr._this().ref(valueField), cArg);

        for (int i = 0; i < 22000; i++) {
            String methodName = "_____________" + i;
            JMethod m = cls.method(JMod.PUBLIC | JMod.STATIC, cls, methodName);
            m.body()._return(JExpr._new(cls).arg(Integer.toString(i)+r.nextLong()));
        }

        cm.build(new FileCodeWriter(outDir));
        testJavac(false, "error: too many constants");
        //cm.build(new SingleStreamCodeWriter(System.out));

    }

    @SuppressWarnings("unused")
	@Test
    public void testStaticFieldLimits() throws Exception {
        JCodeModel cm = new JCodeModel();
        JDefinedClass cls = cm._class(OUTPUT_CLASS_NAME);

        for (int i = 0; i < 11000; i++) {
            String varName = "_" + i;
            JFieldVar m = cls.field(JMod.PUBLIC | JMod.STATIC, String.class,
                    varName, JExpr.lit(Integer.toString(i)));
        }

        cm.build(new FileCodeWriter(outDir));
        testJavac(false, "error: code too large");
        //cm.build(new SingleStreamCodeWriter(System.out));

    }

}
