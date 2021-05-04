/*
 * #%L
 * evaluation
 * %%
 * Copyright (C) 2016 Github jjYBdx4IL Projects
 * %%
 * #L%
 */
package org.openjdk.jmh;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.BenchmarkResult;
import org.openjdk.jmh.results.IterationResult;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.results.format.ResultFormatFactory;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.runner.options.VerboseMode;

import testgroup.RequiresIsolatedVM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Github jjYBdx4IL Projects
 *
 */
@Category(RequiresIsolatedVM.class)
public class RunnerTest {

    private static final Logger LOG = Logger.getLogger(RunnerTest.class);

    @Test
    public void testRunner() throws RunnerException {
    	assumeTrue(SystemUtils.IS_OS_LINUX);
    	
        // or set forks(0), https://github.com/melix/jmh-gradle-plugin/issues/103
        System.setProperty("jmh.separateClasspathJAR", "true");
        
        Options opt = new OptionsBuilder()
            // Specify which benchmarks to run.
            // You can be more specific if you'd like to run only one benchmark
            // per test.
            .include(this.getClass().getName() + ".*")
            // Set the following options as needed
            .mode(Mode.AverageTime)
            .timeUnit(TimeUnit.MICROSECONDS)
            .warmupTime(TimeValue.milliseconds(100))
            .warmupIterations(2)
            .measurementTime(TimeValue.milliseconds(100))
            .measurementIterations(10)
            .threads(2)
            .forks(1)
            .shouldFailOnError(true)
            .shouldDoGC(true)
            .verbosity(VerboseMode.SILENT)
            // .jvmArgs("-XX:+UnlockDiagnosticVMOptions", "-XX:+PrintInlining")
            // .addProfiler(WinPerfAsmProfiler.class)
            .build();

        Runner r = new Runner(opt);
        RunResult result = r.runSingle();
        assertNotNull(result);
        
        ResultFormatFactory.getInstance(ResultFormatType.TEXT, System.out).writeOut(Arrays.asList(result));
        ResultFormatFactory.getInstance(ResultFormatType.JSON, System.out).writeOut(Arrays.asList(result));
        ResultFormatFactory.getInstance(ResultFormatType.CSV, System.out).writeOut(Arrays.asList(result));
        ResultFormatFactory.getInstance(ResultFormatType.SCSV, System.out).writeOut(Arrays.asList(result));
        
        for (BenchmarkResult br : result.getBenchmarkResults()) {
            LOG.info(br.getPrimaryResult());
            for (IterationResult res : br.getIterationResults()) {
                LOG.info(res.getPrimaryResult());
            }
        }
    }

    // The JMH samples are the best documentation for how to use it
    // http://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
    @State(Scope.Thread)
    public static class BenchmarkState {
        List<Integer> list;

        @Setup(Level.Trial)
        public void initialize() {

            Random rand = new Random();

            list = new ArrayList<>();
            for (int i = 0; i < 1000; i++)
                list.add(rand.nextInt());
        }
    }

    @Benchmark
    public void benchmark1(BenchmarkState state, Blackhole bh) {

        List<Integer> list = state.list;

        for (int i = 0; i < 1000; i++)
            bh.consume(list.get(i));
    }
}
