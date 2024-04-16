package com.fluxtion.compiler.generation.exportservice;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.ExportService;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import lombok.Data;
import org.junit.Test;

public class ExportServiceAndDataFlowTests extends MultipleSepTargetInProcessTest {
    public ExportServiceAndDataFlowTests(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void shouldWorkButFails_FixMe() {
        writeSourceFile = true;
        sep(new NyTrigger(new MySvc_NonExporting()));
    }

    @Test
    public void doesWork() {
        writeSourceFile = true;
        sep(new NyTrigger(new MySvc_Exporting()));
    }

    @Data
    public static class NyTrigger {
        private final Object parent;

        @OnEventHandler
        public boolean stringIn(String in) {
            return true;
        }

        @OnTrigger
        public boolean triggering() {
            return true;
        }
    }

    public static class MySvc_NonExporting implements @ExportService(propagate = false) MySvc {
        @Override
        public boolean configUpdate(String config) {
            return false;
        }

    }

    public static class MySvc_Exporting implements @ExportService MySvc {
        @Override
        public boolean configUpdate(String config) {
            return false;
        }

    }

    public interface MySvc {
        boolean configUpdate(String config);
    }
}
