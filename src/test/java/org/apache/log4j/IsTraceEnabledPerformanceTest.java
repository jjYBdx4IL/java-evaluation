package org.apache.log4j;

import static java.util.logging.Level.FINEST;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
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
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Category(RequiresIsolatedVM.class)
public class IsTraceEnabledPerformanceTest {

    private static final Logger LOG = Logger.getLogger(IsTraceEnabledPerformanceTest.class.getName());
    private static final java.util.logging.Logger JUL = java.util.logging.Logger
        .getLogger(IsTraceEnabledPerformanceTest.class.getName());
    private static final int OPS_PER_INVOCATION = 1000;

    /**
     * Result: the use of isTraceEnabled() speeds up suppressed logging stmts by
     * a factor of 6 to 7 if the log msg gets composited from strings of which
     * one is not final static.
     */

    @Benchmark
    public void testLog4jFinalConcatDisabled(BenchmarkState state, Blackhole bh) {
        for (int i = 0; i < OPS_PER_INVOCATION; i++) {
            if (LOG.isTraceEnabled()) {
                LOG.log(Level.TRACE, "some msg" + state.finalStr);
            }
        }

        bh.consume(state.list.get(0));
    }

    @Benchmark
    public void testLog4jConcatDisabled(BenchmarkState state, Blackhole bh) {
        for (int i = 0; i < OPS_PER_INVOCATION; i++) {
            if (LOG.isTraceEnabled()) {
                LOG.log(Level.TRACE, "some msg" + state.str);
            }
        }

        bh.consume(state.list.get(0));
    }

    @Benchmark
    public void testLog4jFinalConcat(BenchmarkState state, Blackhole bh) {
        for (int i = 0; i < OPS_PER_INVOCATION; i++) {
            LOG.log(Level.TRACE, "some msg" + state.finalStr);
        }

        bh.consume(state.list.get(0));
    }

    @Benchmark
    public void testLog4jConcat(BenchmarkState state, Blackhole bh) {
        for (int i = 0; i < OPS_PER_INVOCATION; i++) {
            LOG.log(Level.TRACE, "some msg" + state.str);
        }

        bh.consume(state.list.get(0));
    }

    @Benchmark
    public void testLog4jFinalStaticConcat(BenchmarkState state, Blackhole bh) {
        for (int i = 0; i < OPS_PER_INVOCATION; i++) {
            LOG.log(Level.TRACE, "some msg" + BenchmarkState.FINAL_STATIC_STR);
        }

        bh.consume(state.list.get(0));
    }

    @Benchmark
    public void testLog4jStaticConcat(BenchmarkState state, Blackhole bh) {
        for (int i = 0; i < OPS_PER_INVOCATION; i++) {
            LOG.log(Level.TRACE, "some msg" + BenchmarkState.STATIC_STR);
        }

        bh.consume(state.list.get(0));
    }

    @Benchmark
    public void testLog4jNoConcat(BenchmarkState state, Blackhole bh) {
        for (int i = 0; i < OPS_PER_INVOCATION; i++) {
            LOG.log(Level.TRACE, "some msg");
        }

        bh.consume(state.list.get(0));
    }

    @Benchmark
    public void testLog4jFinalStaticConcatDisabled(BenchmarkState state, Blackhole bh) {
        for (int i = 0; i < OPS_PER_INVOCATION; i++) {
            if (LOG.isTraceEnabled()) {
                LOG.log(Level.TRACE, "some msg" + BenchmarkState.FINAL_STATIC_STR);
            }
        }

        bh.consume(state.list.get(0));
    }

    @Benchmark
    public void testLog4jStaticConcatDisabled(BenchmarkState state, Blackhole bh) {
        for (int i = 0; i < OPS_PER_INVOCATION; i++) {
            if (LOG.isTraceEnabled()) {
                LOG.log(Level.TRACE, "some msg" + BenchmarkState.STATIC_STR);
            }
        }

        bh.consume(state.list.get(0));
    }

    @Benchmark
    public void testLog4jNoConcatDisabled(BenchmarkState state, Blackhole bh) {
        for (int i = 0; i < OPS_PER_INVOCATION; i++) {
            if (LOG.isTraceEnabled()) {
                LOG.log(Level.TRACE, "some msg");
            }
        }

        bh.consume(state.list.get(0));
    }

    @Benchmark
    public void testJulFinalConcatDisabled(BenchmarkState state, Blackhole bh) {
        for (int i = 0; i < OPS_PER_INVOCATION; i++) {
            if (JUL.isLoggable(FINEST)) {
                JUL.log(FINEST, "some msg" + state.finalStr);
            }
        }

        bh.consume(state.list.get(0));
    }

    @Benchmark
    public void testJulConcatDisabled(BenchmarkState state, Blackhole bh) {
        for (int i = 0; i < OPS_PER_INVOCATION; i++) {
            if (JUL.isLoggable(FINEST)) {
                JUL.log(FINEST, "some msg" + state.str);
            }
        }

        bh.consume(state.list.get(0));
    }

    @Benchmark
    public void testJulFinalConcat(BenchmarkState state, Blackhole bh) {
        for (int i = 0; i < OPS_PER_INVOCATION; i++) {
            JUL.log(FINEST, "some msg" + state.finalStr);
        }

        bh.consume(state.list.get(0));
    }

    @Benchmark
    public void testJulConcat(BenchmarkState state, Blackhole bh) {
        for (int i = 0; i < OPS_PER_INVOCATION; i++) {
            JUL.log(FINEST, "some msg" + state.str);
        }

        bh.consume(state.list.get(0));
    }

    @Benchmark
    public void testJulFinalStaticConcat(BenchmarkState state, Blackhole bh) {
        for (int i = 0; i < OPS_PER_INVOCATION; i++) {
            JUL.log(FINEST, "some msg" + BenchmarkState.FINAL_STATIC_STR);
        }

        bh.consume(state.list.get(0));
    }

    @Benchmark
    public void testJulStaticConcat(BenchmarkState state, Blackhole bh) {
        for (int i = 0; i < OPS_PER_INVOCATION; i++) {
            JUL.log(FINEST, "some msg" + BenchmarkState.STATIC_STR);
        }

        bh.consume(state.list.get(0));
    }

    @Benchmark
    public void testJulNoConcat(BenchmarkState state, Blackhole bh) {
        for (int i = 0; i < OPS_PER_INVOCATION; i++) {
            JUL.log(FINEST, "some msg");
        }

        bh.consume(state.list.get(0));
    }

    @Benchmark
    public void testJulFinalStaticConcatDisabled(BenchmarkState state, Blackhole bh) {
        for (int i = 0; i < OPS_PER_INVOCATION; i++) {
            if (JUL.isLoggable(FINEST)) {
                JUL.log(FINEST, "some msg" + BenchmarkState.FINAL_STATIC_STR);
            }
        }

        bh.consume(state.list.get(0));
    }

    @Benchmark
    public void testJulStaticConcatDisabled(BenchmarkState state, Blackhole bh) {
        for (int i = 0; i < OPS_PER_INVOCATION; i++) {
            if (JUL.isLoggable(FINEST)) {
                JUL.log(FINEST, "some msg" + BenchmarkState.STATIC_STR);
            }
        }

        bh.consume(state.list.get(0));
    }

    @Benchmark
    public void testJulNoConcatDisabled(BenchmarkState state, Blackhole bh) {
        for (int i = 0; i < OPS_PER_INVOCATION; i++) {
            if (JUL.isLoggable(FINEST)) {
                JUL.log(FINEST, "some msg");
            }
        }

        bh.consume(state.list.get(0));
    }

    @Benchmark
    public void testJustLoop(BenchmarkState state, Blackhole bh) {
        for (int i = 0; i < OPS_PER_INVOCATION; i++) {
        }

        bh.consume(state.list.get(0));
    }

    // The JMH samples are the best documentation for how to use it
    // http://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
    @State(Scope.Thread)
    public static class BenchmarkState {
        List<Integer> list;
        String str = "some string";
        final String finalStr = "test string";
        static final String FINAL_STATIC_STR = "test string";
        static String STATIC_STR = "some string";

        @Setup(org.openjdk.jmh.annotations.Level.Trial)
        public void initialize() {

            Random rand = new Random();

            list = new ArrayList<>();
            for (int i = 0; i < 1000; i++)
                list.add(rand.nextInt());
        }
    }

    @Test
    public void testRunner() throws RunnerException {
    	assumeTrue(SystemUtils.IS_OS_LINUX);
    	
        // or set forks(0),
        // https://github.com/melix/jmh-gradle-plugin/issues/103
        System.setProperty("jmh.separateClasspathJAR", "true");

        Options opt = new OptionsBuilder()
            .include(this.getClass().getName() + ".*")
            .mode(Mode.AverageTime)
            .timeUnit(TimeUnit.NANOSECONDS)
            .warmupTime(TimeValue.milliseconds(100))
            .warmupIterations(1)
            .measurementTime(TimeValue.milliseconds(100))
            .measurementIterations(1)
            .threads(1)
            .forks(1)
            .shouldFailOnError(true)
            .shouldDoGC(true)
            .operationsPerInvocation(OPS_PER_INVOCATION)
            .verbosity(VerboseMode.SILENT)
            .build();

        Runner r = new Runner(opt);
        Collection<RunResult> results = r.run();
        assertNotNull(results);
        ResultFormatFactory.getInstance(ResultFormatType.TEXT, System.out).writeOut(results);
    }

}
