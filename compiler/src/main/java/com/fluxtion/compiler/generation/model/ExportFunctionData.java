package com.fluxtion.compiler.generation.model;

import com.fluxtion.runtime.callback.ExportFunctionTrigger;

import java.util.ArrayList;
import java.util.List;

public class ExportFunctionData {

    private final String publicMethodName;
    private final ExportFunctionTrigger exportFunctionTrigger = new ExportFunctionTrigger();
    private final List<CbMethodHandle> functionCallBackList = new ArrayList<>();
    private boolean exportedInterface = false;

    public ExportFunctionData(String publicMethodName) {
        this.publicMethodName = publicMethodName;
    }

    public String getPublicMethodName() {
        return publicMethodName;
    }

    public ExportFunctionTrigger getExportFunctionTrigger() {
        return exportFunctionTrigger;
    }

    public List<CbMethodHandle> getFunctionCallBackList() {
        return functionCallBackList;
    }

    public void addCbMethodHandle(CbMethodHandle cbMethodHandle) {
        functionCallBackList.add(cbMethodHandle);
        exportFunctionTrigger.getFunctionPointerList().add(cbMethodHandle.getInstance());
    }

    public boolean isBooleanReturn() {
        for (int i = 0, functionCallBackListSize = functionCallBackList.size(); i < functionCallBackListSize; i++) {
            CbMethodHandle cbMethodHandle = functionCallBackList.get(i);
            if (cbMethodHandle.getMethod().getReturnType() == boolean.class) {
                return true;
            }
        }
        return false;
    }

    public void setExportedInterface(boolean exportedInterface) {
        this.exportedInterface = exportedInterface;
    }

    public boolean isExportedInterface() {
        return exportedInterface;
    }
}
