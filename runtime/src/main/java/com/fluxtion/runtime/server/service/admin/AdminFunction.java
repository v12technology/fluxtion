package com.fluxtion.runtime.server.service.admin;

import com.fluxtion.runtime.annotations.feature.Experimental;

import java.io.PrintStream;
import java.util.List;

@Experimental
public interface AdminFunction {

    void processAdminCommand(List<String> commands, PrintStream output, PrintStream errOutput);
}
