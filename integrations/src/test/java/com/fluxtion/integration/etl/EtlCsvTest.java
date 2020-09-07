/*
 * Copyright (c) 2020, V12 Technology Ltd.
 * All rights reserved.
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
package com.fluxtion.integration.etl;

import com.fluxtion.generator.compiler.OutputRegistry;
import com.fluxtion.integration.etl.Column;
import com.fluxtion.integration.eventflow.EventFlow;
import com.fluxtion.integration.eventflow.sources.DelimitedSource;
import com.squareup.javapoet.TypeName;
import java.io.IOException;
import java.io.StringReader;
import static org.hamcrest.Matchers.is;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
//import org.junit.Assert;
//import org.junit.jupiter.api.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class EtlCsvTest {

    @Test
    public void etlTest() throws IOException, ClassNotFoundException {
        System.setProperty("fluxtion.cacheDirectory", "src/test/fluxtion-cache/test1");
        CsvLoadDefinition def = new CsvLoadDefinition();
        def.setId("com.gregso.MyData");
        Column col = new Column();
        col.setName("age");
        col.setType("int");
        col.setMapName("F__NAME");
        def.addColumn(col);

        System.out.println("CsvLoadDefinition:\n" + def.toYaml());

        CsvEtlBuilder etl = new CsvEtlBuilder().setTestBuild(true);
        CsvEtlPipeline pipeline = etl.buildWorkFlow( def);
        String toYaml = pipeline.toYaml();
        System.out.println(toYaml);
    }
    

    @Test
    public void fromYaml() throws IOException, ClassNotFoundException, InterruptedException {

        System.setProperty("fluxtion.cacheDirectory", "src/test/fluxtion-cache/test1");
        System.out.println(OutputRegistry.INSTANCE.toString());
        String yaml = ""
                + "id: org.greg.Data1\n"
                + "columns:\n"
                + "- {name: age, type: int}\n"
                + "- {name: name, mapName: f__NAME, type: String}\n"
                + "- {name: lastName, type: String, function: 'return input.toString().toUpperCase();' }\n"
                + "derived:\n"
                + "- {name: halfAge, type: int, function: '"
                + "//some comments\n\n"
                + "return age/2;'}\n"
                + "postRecordFunction: '//no-op demo callback\n'"
                + "";
        CsvEtlBuilder etl = new CsvEtlBuilder().setTestBuild(true);
        CsvEtlPipeline pipeline = etl.buildWorkFlow(yaml);     
        //age,character,eventTime,F__NAME
        StringReader reader = new StringReader("age,f__NAME,lastName\n"
                + "22,feed,fgggf\n"
                + "30AAA000,tim,hfd8e\n"
                + "80,mary,blythe\n"
        );
        EventFlow.flow(new DelimitedSource(pipeline.getCsvProcessor(), reader, "limitFromCsv"))
                .first(System.out::println)
                .start();
        
        Thread.sleep(1000);
    }

    @Test
    public void colDefinition() {
        Column col = new Column();
        col.setType("int");
        Assert.assertThat(col.typeName(), is(TypeName.INT));
    }

}
