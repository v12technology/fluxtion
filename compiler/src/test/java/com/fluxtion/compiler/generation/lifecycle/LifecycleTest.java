package com.fluxtion.compiler.generation.lifecycle;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.*;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class LifecycleTest extends MultipleSepTargetInProcessTest {
    public LifecycleTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void startStopTest() {
//        writeSourceFile = true;
        ArrayList<String> cbList = new ArrayList<>();
        sep(c -> {
            c.addNode(new Bottom(new Top()));
        });
        init();
        onEvent(cbList);
        Assert.assertTrue(cbList.isEmpty());

        start();
        MatcherAssert.assertThat(cbList, Matchers.contains("top-start", "bottom-start"));

        stop();
        MatcherAssert.assertThat(cbList, Matchers.contains("top-start", "bottom-start", "bottom-stop", "top-stop"));
    }

    @Test
    public void startCompleteStopTest() {
//        writeSourceFile = true;
        ArrayList<String> cbList = new ArrayList<>();
        sep(c -> {
            c.addNode(new Bottom(new Top()));
        });
        init();
        onEvent(cbList);
        Assert.assertTrue(cbList.isEmpty());
        start();
        MatcherAssert.assertThat(cbList, Matchers.contains("top-start", "bottom-start"));

        startComplete();
        MatcherAssert.assertThat(cbList, Matchers.contains("top-start", "bottom-start", "top-startComplete", "bottom-startComplete"));

        stop();
        MatcherAssert.assertThat(cbList, Matchers.contains("top-start", "bottom-start", "top-startComplete", "bottom-startComplete", "bottom-stop", "top-stop"));
    }


    public static class Top {
        private List<String> invokeList;

        @OnEventHandler
        public boolean addList(ArrayList<String> invokeList) {
            this.invokeList = invokeList;
            return false;
        }

        @OnTrigger
        public boolean triggered() {
            return true;
        }

        @Start
        public void start() {
            invokeList.add("top-start");
        }

        @StartComplete
        public void startComplete() {
            invokeList.add("top-startComplete");
        }

        @Stop
        public void stop() {
            invokeList.add("top-stop");
        }

    }


    public static class Bottom {
        private final Top top;
        private List<String> invokeList;

        public Bottom(@AssignToField("top") Top top) {
            this.top = top;
        }

        @OnEventHandler
        public boolean addList(ArrayList<String> invokeList) {
            this.invokeList = invokeList;
            return false;
        }

        @OnTrigger
        public boolean triggered() {
            return true;
        }

        @Start
        public void start() {
            invokeList.add("bottom-start");
        }

        @StartComplete
        public void startComplete() {
            invokeList.add("bottom-startComplete");
        }

        @Stop
        public void stop() {
            invokeList.add("bottom-stop");
        }
    }
}
