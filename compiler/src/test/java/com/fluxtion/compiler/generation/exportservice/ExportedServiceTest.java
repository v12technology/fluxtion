package com.fluxtion.compiler.generation.exportservice;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.ExportService;

public class ExportedServiceTest extends MultipleSepTargetInProcessTest {

    public ExportedServiceTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    public interface MyService {

    }

    public static class MyExportingServiceNode implements @ExportService MyService {

    }
}
