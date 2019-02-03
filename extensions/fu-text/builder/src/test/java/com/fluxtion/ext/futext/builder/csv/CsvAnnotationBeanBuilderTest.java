/* 
 * Copyright (C) 2018 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.futext.builder.csv;

import com.fluxtion.ext.futext.api.annotation.CsvMarshaller;
import com.fluxtion.generator.compiler.ClassProcessorDispatcher;
import com.fluxtion.generator.util.BaseSepTest;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import org.junit.Test;

/**
 *
 * @author gregp
 */
public class CsvAnnotationBeanBuilderTest extends BaseSepTest{
    
    @Test
    public void testCsvAnnotation() throws MalformedURLException, IOException{
        ClassProcessorDispatcher acp = new ClassProcessorDispatcher();
        File f = new File("./target/test-classes");
        System.out.println(f.getCanonicalPath());
        acp.accept(f.toURI().toURL(), null);
    }
    
    
    
    @CsvMarshaller
    public static class MyBean{
        
        private String name;


        public String getName() {
            return name;
        }


        public void setName(String name) {
            this.name = name;
        }

    }
}
