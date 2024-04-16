package com.fluxtion.compiler.generation.exportservice;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.ExportService;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

public class ExportServiceAndDataFlowTests extends MultipleSepTargetInProcessTest {
    public ExportServiceAndDataFlowTests(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void shouldWorkButFails_FixMe() {
        sep(c -> {
            MySvc_NonExporting parent = c.addNode(new MySvc_NonExporting(), "svc");
            MyTrigger myTrigger = c.addNode(new MyTrigger(parent), "trigger");
        });

        MySvc_NonExporting svc = getField("svc");
        MyTrigger myTrigger = getField("trigger");

        Assert.assertFalse(myTrigger.isTriggered());
        Assert.assertNull(svc.getConfig());

        MySvc exportedSvc = sep.getExportedService(MySvc.class);

        exportedSvc.configUpdate("XX");
        Assert.assertFalse(myTrigger.isTriggered());
        Assert.assertEquals("XX", svc.getConfig());

        exportedSvc.configUpdate("propagate");
        Assert.assertFalse(myTrigger.isTriggered());
        Assert.assertEquals("propagate", svc.getConfig());
    }

    @Test
    public void doesWork() {
//        writeSourceFile = true;
        sep(c -> {
            MySvc_Exporting parent = c.addNode(new MySvc_Exporting(), "svc");
            MyTrigger myTrigger = c.addNode(new MyTrigger(parent), "trigger");
        });
        MySvc_Exporting svc = getField("svc");
        MyTrigger myTrigger = getField("trigger");

        Assert.assertFalse(myTrigger.isTriggered());
        Assert.assertNull(svc.getConfig());

        MySvc exportedSvc = sep.getExportedService(MySvc.class);

        exportedSvc.configUpdate("XX");
        Assert.assertFalse(myTrigger.isTriggered());
        Assert.assertEquals("XX", svc.getConfig());

        exportedSvc.configUpdate("propagate");
        Assert.assertTrue(myTrigger.isTriggered());
        Assert.assertEquals("propagate", svc.getConfig());
    }

    @Data
    public static class MyTrigger {
        private final Object parent;
        private String in;
        private boolean triggered;

        @OnEventHandler
        public boolean stringIn(String in) {
            this.in = in;
            return true;
        }

        @OnTrigger
        public boolean triggering() {
            this.triggered = true;
            return true;
        }
    }

    @Data
    public static class MySvc_NonExporting implements @ExportService(propagate = false) MySvc {
        private String config;

        @Override
        public boolean configUpdate(String config) {
            this.config = config;
            return config.equals("propagate");
        }
    }

    @Data
    public static class MySvc_Exporting implements @ExportService MySvc {
        private String config;

        @Override
        public boolean configUpdate(String config) {
            this.config = config;
            return config.equals("propagate");
        }

    }

    public interface MySvc {
        boolean configUpdate(String config);
    }
}
