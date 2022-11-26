package com.fluxtion.compiler.builder.callback;

import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.builder.factory.NodeRegistry;
import com.fluxtion.runtime.callback.DirtyStateMonitor;
import com.fluxtion.runtime.callback.DirtyStateMonitor.DirtyStateMonitorImp;
import com.google.auto.service.AutoService;

import java.util.Map;

@AutoService(NodeFactory.class)
public class DirtyStateMonitorFactory implements NodeFactory<DirtyStateMonitor> {

    public static final DirtyStateMonitor INSTANCE = new DirtyStateMonitorImp();

    @Override
    public DirtyStateMonitor createNode(Map<String, Object> config, NodeRegistry registry) {
        return INSTANCE;
    }
}
