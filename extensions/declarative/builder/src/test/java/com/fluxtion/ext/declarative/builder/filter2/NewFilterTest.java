package com.fluxtion.ext.declarative.builder.filter2;

import com.fluxtion.builder.generation.NodeNameProducer;
import com.fluxtion.builder.node.SEPConfig;
import static com.fluxtion.ext.declarative.builder.event.EventSelect.select;
import com.fluxtion.ext.declarative.builder.helpers.MyDataHandler;
import com.fluxtion.generator.compiler.InprocessSepCompiler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static com.fluxtion.ext.declarative.builder.filter2.FilterBuilder.filter;
import com.fluxtion.ext.declarative.builder.helpers.DataEvent;
import com.fluxtion.ext.declarative.builder.util.LambdaReflection.SerializableFunction;
import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 *
 * @author V12 Technology Ltd.
 */
public class NewFilterTest implements NodeNameProducer {

    
    @Test
    public void testWrapperFilter() throws IllegalAccessException, Exception{
//        InprocessSepCompiler.sepTestInstance((SEPConfig t) -> {
//            select(DataEvent.class)
//                    .filter(positive(), DataEvent::getValue);
//        }, "com.fluxtion.ext.declarative.builder.filter_wrapper", "WrapperFilter");        
    }
    
    @Test
    public void testInstanceFilter() throws IllegalAccessException, Exception {
        InprocessSepCompiler.sepTestInstance((t) -> {

            try {
                MyDataHandler dh1 = t.addNode(new MyDataHandler("dh1"));
                Method method = MyDataHandler.class.getDeclaredMethod("getIntVal");
                filter(positive(), dh1::getIntVal).build();
                filter(NumericValidator::validateDataHandler, dh1).build();
                filter(gt(200.87), dh1, method).build();
//                filter(gt(86.788), dh1::getIntVal).build();
//                filter(gt(34), dh1::getDoubleVal).build();
                filter(lt(34), dh1::getDoubleVal).build();
//                filter(gt(34.4556), dh1::getDoubleVal).build();
            } catch (Exception ex) {
                Logger.getLogger(NewFilterTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }, "com.fluxtion.ext.declarative.builder.filter2", "Filter2Test_Specific");
    }

    public static SerializableFunction gt(double test) {
        return (SerializableFunction<Double, Boolean>) new NumericValidator(test)::greaterThan;
    }

    public static SerializableFunction lt(int test) {
        return (SerializableFunction<Integer, Boolean>) new NumericValidator(test)::lessThan;
    }

    public static SerializableFunction positive() {
        return (SerializableFunction<Integer, Boolean>) NumericValidator::positiveInt;
    }

    @Override
    public String mappedNodeName(Object nodeToMap) {
        if (nodeToMap instanceof NumericValidator) {
            NumericValidator val = (NumericValidator)nodeToMap;
            String suffix = "" + val.limit;
            if(val.doubleLimit!=0){
                suffix = "" + val.doubleLimit;
            }
            return "numberTest_" + suffix.replace(".", "_");
        }
        return null;
    }

    @Override
    public int priority() {
        return 500;
    }

    public static class NumericValidator {

        public int limit;
        public double doubleLimit;

        public NumericValidator() {
        }

        public NumericValidator(double doubleLimit) {
            this.doubleLimit = doubleLimit;
        }

        public NumericValidator(int limit) {
            this.limit = limit;
        }

        public static boolean validateDataHandler(MyDataHandler dh) {
            return true;
        }

        public static boolean positiveInt(int d) {
            return d > 0;
        }

        public boolean greaterThan(int d) {
            return d > limit;
        }

        public boolean greaterThan(double d) {
            return d > doubleLimit;
        }

        public boolean lessThan(int d) {
            return d < limit;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 73 * hash + this.limit;
            hash = 73 * hash + (int) (Double.doubleToLongBits(this.doubleLimit) ^ (Double.doubleToLongBits(this.doubleLimit) >>> 32));
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final NumericValidator other = (NumericValidator) obj;
            if (this.limit != other.limit) {
                return false;
            }
            if (Double.doubleToLongBits(this.doubleLimit) != Double.doubleToLongBits(other.doubleLimit)) {
                return false;
            }
            return true;
        }
        
        
    }

}
