package com.fluxtion.generator.util;

import com.fluxtion.generator.model.CbMethodHandle;
import com.fluxtion.generator.model.SimpleEventProcessorModel;
import com.fluxtion.generator.model.TopologicallySortedDependencyGraph;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public abstract class BaseModelTest {

    protected TopologicallySortedDependencyGraph graph;
    protected SimpleEventProcessorModel eventProcessorModel;

    @SneakyThrows
    protected void buildModel(Object root, Object... nodes){
        requireNonNull(root, "must provide at least one node to build a model to test");
        ArrayList<Object> list = new ArrayList<>();
        list.add(root);
        if(Objects.nonNull(nodes)){
            Collections.addAll(list, nodes);
        }
        graph = new TopologicallySortedDependencyGraph(list);
        eventProcessorModel = new SimpleEventProcessorModel(graph);
        eventProcessorModel.generateMetaModel();
    }

    public TopologicallySortedDependencyGraph getGraph() {
        return graph;
    }

    public SimpleEventProcessorModel getEventProcessorModel() {
        return eventProcessorModel;
    }

    /**
     * list of call back methods in topological order
     * @return list of call back methods in topological order
     */
    @NotNull
    protected List<Method> getCallbackMethods() {
        return getEventProcessorModel().getDispatchMapForGraph().stream()
                .map(CbMethodHandle::getMethod)
                .collect(Collectors.toList());
    }
}
