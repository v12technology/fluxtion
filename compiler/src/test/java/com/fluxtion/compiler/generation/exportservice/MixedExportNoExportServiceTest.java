package com.fluxtion.compiler.generation.exportservice;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.ExportService;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.node.NamedNode;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

public class MixedExportNoExportServiceTest extends MultipleSepTargetInProcessTest {


    public MixedExportNoExportServiceTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void triggerMixedPropagate() {
        sep(c -> {
            c.addNode(new MyTrigger(new Updater_B(new Updater_A())));
        });
        Update exportedSvc = sep.getExportedService(Update.class);
        exportedSvc.update("TEST");

        Updater_A a = getField("updater_A");
        Updater_B b = getField("updater_B");
        MyTrigger trigger = getField("myTrigger");

        Assert.assertEquals(1, a.getUpdateCount());
        Assert.assertEquals(1, b.getUpdateCount());
        Assert.assertEquals(1, b.getTriggerCount());
        Assert.assertEquals(1, trigger.getTriggerCount());
    }

    public interface Update {
        boolean update(String in);
    }

    @Data
    public static class Updater_A implements @ExportService Update, NamedNode {
        int updateCount;

        @Override
        public boolean update(String in) {
            System.out.println("Updater_A::update - " + in);
            updateCount++;
            return true;
        }

        @Override
        public String getName() {
            return "updater_A";
        }
    }


    @Data
    public static class Updater_B implements @ExportService(propagate = false) Update, NamedNode {

        private final Updater_A updaterA;
        int updateCount;
        int triggerCount;

        @Override
        public boolean update(String in) {
            updateCount++;
            return true;
        }

        @OnTrigger
        public boolean triggered() {
            triggerCount++;
            return true;
        }

        @Override
        public String getName() {
            return "updater_B";
        }
    }

    @Data
    public static class MyTrigger implements NamedNode {
        private final Updater_B updaterB;
        int triggerCount;

        @OnTrigger
        public boolean triggered() {
            triggerCount++;
            return true;
        }

        @Override
        public String getName() {
            return "myTrigger";
        }
    }
}
