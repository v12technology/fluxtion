package com.fluxtion.runtime.callback;

import com.fluxtion.runtime.annotations.PushReference;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.node.EventHandlerNode;
import com.fluxtion.runtime.node.NamedNode;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExportFunctionTrigger implements EventHandlerNode, NamedNode {
    @PushReference
    private List<Object> functionPointerList = new ArrayList<>();
    private transient Class<?> cbClass;

    private final transient Object event;

    private transient final String name;

    @SneakyThrows
    public ExportFunctionTrigger(@AssignToField("cbClass") Class<?> cbClass) {
        this.cbClass = cbClass;
        this.event = cbClass.getDeclaredConstructor().newInstance();
        this.name = "handler" + cbClass.getSimpleName();
    }

    @SneakyThrows
    public ExportFunctionTrigger() {
        this.cbClass = ExportFunctionTriggerEvent.exportFunctionCbList.remove(0);
        this.event = cbClass.getDeclaredConstructor().newInstance();
        this.name = "handler" + cbClass.getSimpleName();
    }

    @Override
    public boolean onEvent(Object e) {
        return true;
    }

    public Object getEvent() {
        return event;
    }

    public List<Object> getFunctionPointerList() {
        return functionPointerList;
    }

    public void setFunctionPointerList(List<Object> functionPointerList) {
        this.functionPointerList = functionPointerList;
    }

    @Override
    @SneakyThrows
    public final Class<?> eventClass() {
        if (cbClass == null) {
            cbClass = InstanceCallbackEvent.cbClassList.remove(0);
        }
        return cbClass;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExportFunctionTrigger that = (ExportFunctionTrigger) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
