package com.fluxtion.ext.declarative.builder.filter2;

import static com.fluxtion.ext.declarative.builder.filter2.FilterBuilder.filter;
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
//        InprocessSepCompiler.sepTestInstance((t) -> {
//            MyDataHandler dh1 = t.addNode(new MyDataHandler("dh1"));
////            filter(NewFilterTest::validatePositive, dh1::getIntVal);
//        }, "", "");
    }
    
    public static boolean validatePositive(int d){
        return true;
    }

}
