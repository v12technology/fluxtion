package com.fluxtion.runtime.server.service.admin;

import com.fluxtion.runtime.annotations.feature.Experimental;

import java.util.List;
import java.util.function.Consumer;

@Experimental
public interface Admin {

    void registerCommand(String name, Consumer<List<String>> command);

    void registerCommand(String name, AdminFunction command);
}
