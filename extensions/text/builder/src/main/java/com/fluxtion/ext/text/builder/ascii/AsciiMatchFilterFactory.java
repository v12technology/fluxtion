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
package com.fluxtion.ext.text.builder.ascii;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.builder.node.NodeFactory;
import com.fluxtion.builder.node.NodeRegistry;
import static com.fluxtion.ext.streaming.builder.util.FunctionGeneratorHelper.generateAndCompile;
import static com.fluxtion.ext.streaming.builder.util.FunctionKeys.filter;
import static com.fluxtion.ext.streaming.builder.util.FunctionKeys.functionClass;
import static com.fluxtion.ext.streaming.builder.util.FunctionKeys.imports;
import com.fluxtion.ext.streaming.builder.util.ImportMap;
import com.fluxtion.ext.text.api.event.CharEvent;
import com.fluxtion.ext.text.api.filter.AsciiMatchFilter;
import static com.fluxtion.ext.text.builder.Templates.CHAR_MATCH_FILTER;
import com.google.auto.service.AutoService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 *
 * @author Greg Higgins
 */
@AutoService(NodeFactory.class)
public class AsciiMatchFilterFactory implements NodeFactory<AsciiMatchFilter> {

//    public static final String KEY_FILTER_STRING = "AsciiMatchFilterFactory.KEY_FILTER_STRING";
    private static int count;
    private final HashMap<String, AsciiMatchFilter> cache = new HashMap<>();

    @Override
    public AsciiMatchFilter createNode(Map config, NodeRegistry registry) {
        ImportMap importMap = ImportMap.newMap();
        importMap.addImport(EventHandler.class);
        importMap.addImport(OnEvent.class);
        importMap.addImport(AsciiMatchFilter.class);
        importMap.addImport(CharEvent.class);
        try {
            String filterString = (String) config.get(AsciiMatchFilter.KEY_FILTER_STRING);
            AsciiMatchFilter textMatcherPrototype = null;
            if (filterString != null && !cache.containsKey(filterString)) {
                VelocityContext ctx = new VelocityContext();
                final String genClassName = "AsciiMatcher_" + count++;
                ctx.put(functionClass.name(), genClassName);
                ctx.put(filter.name(), filterString);

                List<SequenceVariable> sequenceVariables = new ArrayList<>();
                List<SequenceVariable> sequenceVariables2 = new ArrayList<>();
                int i = 0;
                for (char c : filterString.toCharArray()) {

                    SequenceVariable seqVar = new SequenceVariable();
                    seqVar.actualVariable = "actual_" + i;
                    seqVar.expectedValue = "" + c;
                    seqVar.expectedVariable = "expected_" + i;
                    if (!sequenceVariables.contains(seqVar)) {
                        sequenceVariables2.add(seqVar);
                    }
                    sequenceVariables.add(seqVar);
//                    sequenceSet.add(seqVar);
                    i++;

                }

                final SequenceVariable lastSeqVar = sequenceVariables.get(i - 1);
                sequenceVariables2.get(sequenceVariables2.indexOf(lastSeqVar)).lastChar = true;
                lastSeqVar.lastChar = true;
//                sequenceSet.remove(lastSeqVar);
//                sequenceSet.add(lastSeqVar);
                ctx.put("sequenceVariableList", sequenceVariables);
//                ctx.put("sequenceVariableSet", sequenceSet);
                ctx.put("sequenceVariableSet", sequenceVariables2);
                ctx.put(imports.name(), importMap.asString());
                GenerationContext generationConfig = GenerationContext.SINGLETON;
                Class newClass = generateAndCompile(textMatcherPrototype, CHAR_MATCH_FILTER, generationConfig, ctx);
                textMatcherPrototype = (AsciiMatchFilter) newClass.newInstance();
                cache.put(filterString, textMatcherPrototype);
            } else {
                textMatcherPrototype = cache.get(filterString);
            }
            return textMatcherPrototype;
        } catch (IOException | MethodInvocationException | ParseErrorException | ResourceNotFoundException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("unable to create node", ex);
        }
    }

    @Data
    @EqualsAndHashCode(of = {"expectedValue"})
    public static class SequenceVariable {

        public String expectedVariable;
        public String actualVariable;
        public String expectedValue;
        public boolean lastChar = false;

        public String getExpectedValueValidJava() {
            char c = expectedValue.charAt(0);
            if (Character.isJavaIdentifierPart(c)) {
                return getExpectedValue();
            }
            return "" + (int) c;
        }
    }

}
