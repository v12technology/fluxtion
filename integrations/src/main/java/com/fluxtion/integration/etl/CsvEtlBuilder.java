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

import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.event.DefaultEvent;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.ext.streaming.builder.util.FunctionGeneratorHelper;
import com.fluxtion.ext.text.api.annotation.ColumnName;
import com.fluxtion.ext.text.api.annotation.ConvertField;
import com.fluxtion.ext.text.api.annotation.OptionalField;
import com.fluxtion.ext.text.api.csv.RowProcessor;
import static com.fluxtion.ext.text.builder.csv.CsvToBeanBuilder.buildRowProcessor;
import static com.fluxtion.generator.compiler.DirOptions.JAVA_GENDIR_OUTPUT;
import static com.fluxtion.generator.compiler.DirOptions.TEST_DIR_OUTPUT;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.Modifier;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Log4j2
public class CsvEtlBuilder implements Lifecycle {

    private CsvLoadDefinition loadDefinition;
    private TypeSpec.Builder csvProcessorBuilder;
    private boolean testBuild;

    public CsvEtlPipeline buildWorkFlow(String definitionAsYaml) throws IOException, ClassNotFoundException {
        Yaml yamlParser = new Yaml();
        return buildWorkFlow(yamlParser.loadAs(definitionAsYaml, CsvLoadDefinition.class));
    }

    public CsvEtlPipeline buildWorkFlow(CsvLoadDefinition loadDefinition) throws IOException, ClassNotFoundException {
        //store somewhere!!
        this.loadDefinition = loadDefinition;
        RowProcessor<Object> generateCsvClass = generateCsvClass();
        CsvEtlPipeline pipeline = new CsvEtlPipeline();
        pipeline.setCsvProcessor(generateCsvClass);
        pipeline.setCsvProcessorClassName(generateCsvClass.getClass().getCanonicalName());
        pipeline.setDefintion(loadDefinition);
        pipeline.setId(loadDefinition.getId());
        return pipeline;
    }

    @Override
    public void init() {
        log.info("starting");
    }

    @Override
    public void tearDown() {
        log.info("stopping");
    }

    public boolean isTestBuild() {
        return testBuild;
    }

    public CsvEtlBuilder setTestBuild(boolean testBuild) {
        this.testBuild = testBuild;
        return this;
    }

    private RowProcessor<Object> generateCsvClass() throws IOException, ClassNotFoundException {
        final String className = loadDefinition.className();
        final String pkgName = loadDefinition.packageName();
        log.info("generating CSV class:'{}'", className);
        csvProcessorBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .superclass(ClassName.get(DefaultEvent.class));

//        csvProcessorBuilder.addAnnotation(Data.class);
        //fields
        loadDefinition.getColumns().stream().forEach(this::columnToField);
        loadDefinition.getDerived().stream().forEach(this::derivedToField);
        addToString();
        //
        addOnComplete();
        //build file
        TypeSpec nodeClass = csvProcessorBuilder.build();
        //license
        JavaFile javaFile = addLicense(
                JavaFile.builder(pkgName, nodeClass))
                .skipJavaLangImports(true)
                .build();
        //write and compile
        File sourcesDir = FunctionGeneratorHelper.sourcesDir(testBuild);
        javaFile.writeTo(sourcesDir);
        Class<Object> dataClass = FunctionGeneratorHelper.compile(new StringReader(javaFile.toString()), loadDefinition.getId(), testBuild);
        return buildRowProcessor(dataClass, loadDefinition.packageName(), testBuild ? TEST_DIR_OUTPUT : JAVA_GENDIR_OUTPUT);
    }

    private void columnToField(Column column) {
        FieldSpec.Builder field = FieldSpec.builder(column.typeName(), column.getName())
                .addModifiers(Modifier.PRIVATE);
        addBeanAccessor(column);
        if (column.getMapName() != null) {
            log.info("add mapped name:'{}'", column.getMapName());
            field.addAnnotation(AnnotationSpec.builder(ColumnName.class).addMember("value", "$S", column.getMapName()).build());
        }
        if (column.getFunction() != null) {
            String funName = "fun_" + column.getName();
            field.addAnnotation(AnnotationSpec.builder(ConvertField.class).addMember("value", "$S", funName).build());
            MethodSpec.Builder funSpec = MethodSpec.methodBuilder(funName)
                    .addParameter(ClassName.get(CharSequence.class), "input")
                    .returns(column.typeName())
                    .addModifiers(Modifier.PUBLIC);
            funSpec.addJavadoc("converter calculation for {@link #$L} field\n", column.getName());
            funSpec.addCode(column.getFunction().replace("\\n", "\n") + "\n");
            csvProcessorBuilder.addMethod(funSpec.build());
        }
        csvProcessorBuilder.addField(field.build());
    }

    private void addBeanAccessor(Column column) {
        String prefix = column.getType().equalsIgnoreCase("boolean") ? "is" : "get";
        String get = prefix + StringUtils.capitalize(column.getName());
        String set = "set" + StringUtils.capitalize(column.getName());
        MethodSpec.Builder getSpec = MethodSpec.methodBuilder(get)
                .returns(column.typeName())
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $L", column.getName());
        MethodSpec.Builder setSpec = MethodSpec.methodBuilder(set)
                .addParameter(column.typeName(), column.getName())
                .returns(void.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("this.$1L = $1L", column.getName());
        csvProcessorBuilder.addMethod(getSpec.build());
        csvProcessorBuilder.addMethod(setSpec.build());

    }

    private void derivedToField(DerivedColumn column) {
        FieldSpec.Builder field = FieldSpec.builder(column.typeName(), column.getName())
                .addModifiers(Modifier.PRIVATE);
        field.addAnnotation(AnnotationSpec.builder(OptionalField.class).build());
        String funName = "fun_" + column.getName();
        field.addAnnotation(AnnotationSpec.builder(ConvertField.class).addMember("value", "$S", funName).build());
        field.addJavadoc("derived field\n");
        csvProcessorBuilder.addField(field.build());
        //now add a method
        MethodSpec.Builder funSpec = MethodSpec.methodBuilder(funName)
                .addParameter(ClassName.get(CharSequence.class), "input")
                .returns(column.typeName())
                .addModifiers(Modifier.PUBLIC);
        funSpec.addJavadoc("derived calculation for {@link #$L} field\n", column.getName());
        funSpec.addCode(column.getFunction().replace("\\n", "\n") + "\n");
        csvProcessorBuilder.addMethod(funSpec.build());
    }

    private void addOnComplete() {
        final String postReadFunc = loadDefinition.getPostRecordFunction();
        if (postReadFunc != null && !postReadFunc.isEmpty()) {
            MethodSpec.Builder funSpec = MethodSpec.methodBuilder("postRecordRead")
                    .addAnnotation(AnnotationSpec.builder(OnEvent.class).build())
                    .addModifiers(Modifier.PUBLIC);
            funSpec.addJavadoc("operations post row read, before publishing record\n");
            funSpec.addCode(postReadFunc.replace("\\n", "\n") + "\n");
            csvProcessorBuilder.addMethod(funSpec.build());
        }
    }

    private void addToString() {
        List<String> propertyList = new ArrayList<>();
        loadDefinition.getColumns().stream().map(Column::getName).forEach(propertyList::add);
        loadDefinition.getDerived().stream().map(DerivedColumn::getName).forEach(propertyList::add);
        MethodSpec.Builder toStringSpec = MethodSpec.methodBuilder("toString")
                .returns(ClassName.get(String.class))
                .addModifiers(Modifier.PUBLIC)
                .addStatement("String out = \"$1L : {\"", loadDefinition.className());
        String comma = "";
        for (String string : propertyList) {
            toStringSpec.addStatement("out += \"$2L$1L: \" + $1L", string, comma);
            comma = ", ";
        }
        toStringSpec.addStatement("out += \"}\"");
        toStringSpec.addStatement("return out");
        csvProcessorBuilder.addMethod(toStringSpec.build());
    }

    public void startImport() {
    }

    private JavaFile.Builder addLicense(JavaFile.Builder builder) {
        builder.
                addFileComment("Copyright (C) 2018 V12 Technology Ltd.\n"
                        + "\n"
                        + "This program is free software: you can redistribute it and/or modify\n"
                        + "it under the terms of the Server Side Public License, version 1,\n"
                        + "as published by MongoDB, Inc.\n"
                        + "\n"
                        + "This program is distributed in the hope that it will be useful,\n"
                        + "but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
                        + "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"
                        + "Server Side License for more details.\n"
                        + "\n"
                        + "You should have received a copy of the Server Side Public License\n"
                        + "along with this program.  If not, see \n"
                        + "<http://www.mongodb.com/licensing/server-side-public-license>.");
        return builder;
    }

}
