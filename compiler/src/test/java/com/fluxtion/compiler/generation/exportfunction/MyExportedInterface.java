package com.fluxtion.compiler.generation.exportfunction;

import java.util.List;

public interface MyExportedInterface {

    void updatedDetails(String s, int y);
    
    void complexCallBack(List<String> s, int y);

    void complexCallBackDouble(List<Double> s, int y);
}
