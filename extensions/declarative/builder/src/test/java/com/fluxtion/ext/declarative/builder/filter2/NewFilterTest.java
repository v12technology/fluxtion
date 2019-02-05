package com.fluxtion.ext.declarative.builder.filter2;

import static com.fluxtion.ext.declarative.builder.filter2.FilterBuilder.filterNew;
import static com.fluxtion.ext.declarative.builder.filter2.FilterBuilder.filterNum;
import com.fluxtion.ext.declarative.builder.helpers.MyDataHandler;
import com.fluxtion.generator.compiler.InprocessSepCompiler;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
public class NewFilterTest {

    @Test
    public void testInstanceFilter() throws IllegalAccessException, Exception {
        InprocessSepCompiler.sepTestInstance((t) -> {
            
            try {
                MyDataHandler dh1 = t.addNode(new MyDataHandler("dh1"));
                Method method = MyDataHandler.class.getDeclaredMethod("getIntVal");
//                filterNew(new NewFilterTest()::validatePositive, dh1::getIntVal);
                filterNew(new NewFilterTest()::validatePositive, dh1, method);
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(NewFilterTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(NewFilterTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }, "com.fluxtion.ext.declarative.builder.filter2", "Filter2Test_Specific");
    }

    @Test
    public void testNumberInstanceFilter() throws IllegalAccessException, Exception {
        InprocessSepCompiler.sepTestInstance((t) -> {
            MyDataHandler dh1 = t.addNode(new MyDataHandler("dh1"));
            filterNum(new NewFilterTest()::validatePositive, dh1::getDoubleVal);
        }, "com.fluxtion.ext.declarative.builder.filter2", "Filter2Test_Numeric");
    }
    
    public  boolean validatePositive(int d){
        return true;
    }
    
}
