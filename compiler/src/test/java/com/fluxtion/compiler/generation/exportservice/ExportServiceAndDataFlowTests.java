package com.fluxtion.compiler.generation.exportservice;

import com.fluxtion.compiler.builder.dataflow.DataFlow;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.ExportService;
import com.fluxtion.runtime.annotations.NoPropagateFunction;
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
    public void nonExportingParentService() {
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

        exportedSvc.configUpdate("propagate_nonExporting");
        Assert.assertFalse(myTrigger.isTriggered());
        Assert.assertEquals("propagate_nonExporting", svc.getConfig());
    }

    @Test
    public void nonExportingMethodsParentService() {
//        writeOutputsToFile(true);
        sep(c -> {
            MySvc_NonExportingMethods parent = c.addNode(new MySvc_NonExportingMethods(), "svc");
            MyTrigger myTrigger = c.addNode(new MyTrigger(parent), "trigger");
        });

        MySvc_NonExportingMethods svc = getField("svc");
        MyTrigger myTrigger = getField("trigger");

        Assert.assertFalse(myTrigger.isTriggered());
        Assert.assertNull(svc.getConfig());

        MySvc exportedSvc = sep.getExportedService(MySvc.class);

        exportedSvc.configUpdate("XX");
        Assert.assertFalse(myTrigger.isTriggered());
        Assert.assertEquals("XX", svc.getConfig());

        exportedSvc.configUpdate("propagate_nonExporting");
        Assert.assertFalse(myTrigger.isTriggered());
        Assert.assertEquals("propagate_nonExporting", svc.getConfig());
    }

    @Test
    public void exportingParentService() {
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

    @Test
    public void exportingAndNonExportingParentService() {
//        writeSourceFile = true;
        sep(c -> {
            MySvc_Exporting parent = c.addNode(new MySvc_Exporting(), "svc_exporting");
            MySvc_NonExporting parent2 = c.addNode(new MySvc_NonExporting(), "svc_nonExporting");
            MyTrigger myTrigger = c.addNode(new MyTrigger(parent), "trigger");
            myTrigger.setParent2(parent2);
        });
        MySvc_Exporting svc_exporting = getField("svc_exporting");
        MySvc_NonExporting svc_nonExporting = getField("svc_nonExporting");
        MyTrigger myTrigger = getField("trigger");

        Assert.assertFalse(myTrigger.isTriggered());
        Assert.assertNull(svc_exporting.getConfig());

        MySvc exportedSvc = sep.getExportedService(MySvc.class);

        exportedSvc.configUpdate("XX");
        Assert.assertFalse(myTrigger.isTriggered());
        Assert.assertEquals("XX", svc_exporting.getConfig());
        Assert.assertEquals("XX", svc_nonExporting.getConfig());

        exportedSvc.configUpdate("propagate");
        Assert.assertTrue(myTrigger.isTriggered());
        Assert.assertEquals("propagate", svc_exporting.getConfig());
        Assert.assertEquals("propagate", svc_nonExporting.getConfig());

        myTrigger.setTriggered(false);
        exportedSvc.configUpdate("propagate_nonExporting");
        Assert.assertFalse(myTrigger.isTriggered());
        Assert.assertEquals("propagate_nonExporting", svc_exporting.getConfig());
        Assert.assertEquals("propagate_nonExporting", svc_nonExporting.getConfig());
        Assert.assertFalse(myTrigger.isTriggered());
    }

    @Test
    public void dataFlowToNonExportedService() {
//        writeOutputsToFile(true);
        sep(c -> {
            DataFlow.subscribe(String.class).id("stringHandler")
                    .map(new StringValidator()::validate).id("mapStringValidator_validate")
                    .map(new MySvc_NonExportingMethods()::configUpdate).id("nonExportingMethods_configUpdate")
                    .sink("results");
        });

        final boolean[] result = new boolean[]{true};
        sep.addSink("results", (Boolean b) -> {
            result[0] = b;
        });

        onEvent("XXXX");
        Assert.assertFalse(result[0]);

        onEvent("propagate_nonExporting");
        Assert.assertTrue(result[0]);
    }


    @Data
    public static class MyTrigger {
        private final Object parent;
        private Object parent2;
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
    public static class MySvc_Exporting implements @ExportService MySvc {
        private String config;

        @Override
        public boolean configUpdate(String config) {
            this.config = config;
            return config.equals("propagate");
        }
    }

    @Data
    public static class MySvc_NonExporting implements @ExportService(propagate = false) MySvc {

        private String config;

        @Override
        public boolean configUpdate(String config) {
            this.config = config;
            return config.equals("propagate_nonExporting");
        }
    }

    @Data
    public static class MySvc_NonExportingMethods implements @ExportService MySvc {
        private String config;

        @Override
        @NoPropagateFunction
        public boolean configUpdate(String config) {
            this.config = config;
            return config.equals("propagate_nonExporting");
        }
    }

    public interface MySvc {
        boolean configUpdate(String config);
    }

    public static class StringValidator {
        public String validate(String in) {
            return in;
        }
    }
}
