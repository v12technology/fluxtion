package com.fluxtion.ext.declarative.builder.filter2;

import static com.fluxtion.ext.declarative.builder.filter2.FilterBuilder.filterNew;
import com.fluxtion.ext.declarative.builder.helpers.MyDataHandler;
import com.fluxtion.generator.compiler.InprocessSepCompiler;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
public class NewFilterTest {

    @Test
    public void testInstanceFilter() throws IllegalAccessException, Exception {
        InprocessSepCompiler.sepTestInstance((t) -> {
            MyDataHandler dh1 = t.addNode(new MyDataHandler("dh1"));
            filterNew(new NewFilterTest()::validatePositive, dh1::getIntVal);
        }, "com.fluxtion.ext.declarative.builder.filter2", "Filter2Test1");
    }
    
    public  boolean validatePositive(int d){
        return true;
    }

}
