/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.compiler;

import com.fluxtion.builder.annotation.ClassProcessor;
import com.fluxtion.generator.compiler.AnnotationCompiler;
import java.net.URL;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author gregp
 */
public class AnnotationCompilerTest {
    
    @Test
    public void testAnnotationLoading(){
        AnnotationCompiler acp = new AnnotationCompiler();
        MyClassProcessor.invokeCount = 0;
        acp.accept(null);
        Assert.assertThat(MyClassProcessor.invokeCount, is(1));
    }
    
    public static class MyClassProcessor implements ClassProcessor{
        public static int invokeCount = 0;
        @Override
        public void process(URL classPath) {
            invokeCount++;
        }
        
    }
}
