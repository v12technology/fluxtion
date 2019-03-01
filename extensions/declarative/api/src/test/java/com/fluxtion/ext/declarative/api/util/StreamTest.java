package com.fluxtion.ext.declarative.api.util;

import com.fluxtion.ext.declarative.api.MergingWrapper;
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.ext.declarative.api.stream.NodeWrapper;
import org.junit.Test;

/**
 *
 * @author gregp
 */
public class StreamTest {

    @Test
    public void testStream() {
        Wrapper<Data> dwrapper = new Wrapper<Data>(){
            @Override
            public Data event() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Class<Data> eventClass() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        
//        dwrapper.filter(StreamTest::isZero, Data::getVal);
    }

    
    @Test
    public void wrapperTest(){
        MergingWrapper<Data> merger = new MergingWrapper<>(Data.class);
        NodeWrapper<Data> d = null;
        NodeWrapper<SubData> s = null;
        
        merger.mergeWrappers(d, s);
    }
    
    
    public static class Data {

        public int getVal() {
            return 1;
        }
    }

    public static class SubData extends Data{}
    
    public static boolean isZero(double im) {
        return im == 0;
    }
}
