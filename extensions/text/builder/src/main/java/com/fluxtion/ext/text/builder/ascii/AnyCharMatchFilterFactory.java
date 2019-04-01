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
import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.builder.node.NodeFactory;
import com.fluxtion.builder.node.NodeRegistry;
import com.fluxtion.ext.text.api.event.CharEvent;
import static com.fluxtion.ext.streaming.builder.factory.FunctionKeys.*;
import static com.fluxtion.ext.streaming.builder.factory.FunctionGeneratorHelper.*;
import com.fluxtion.ext.text.api.filter.AnyCharMatchFilter;
import com.fluxtion.ext.text.api.filter.SingleCharMatcher;
import static com.fluxtion.ext.text.builder.Templates.ANYCHAR_MATCH_FILTER;
import com.fluxtion.ext.streaming.builder.util.ImportMap;
import com.google.auto.service.AutoService;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 *
 * @author Greg Higgins
 */
@AutoService(NodeFactory.class)
public class AnyCharMatchFilterFactory implements NodeFactory<AnyCharMatchFilter> {

//    public static final String KEY_FILTER_ARRAY = "AnyCharMatchFilterFactory.KEY_FILTER_ARRAY";
    private static int count;
    private final HashMap<String, AnyCharMatchFilter> cache = new HashMap<>();
    private ImportMap importMap;

    @Override
    public AnyCharMatchFilter createNode(Map config, NodeRegistry registry) {
        importMap = ImportMap.newMap();
        importMap.addImport(EventHandler.class);
        importMap.addImport(AnyCharMatchFilter.class);
        importMap.addImport(CharEvent.class);
//        return createNodeOld(config, registry);
        return createNodeNew(config, registry);
    }

    public AnyCharMatchFilter createNodeOld(Map config, NodeRegistry registry) {
        try {
            String filterString = ((String) config.get(AnyCharMatchFilter.KEY_FILTER_ARRAY));
            GenerationContext generationConfig = GenerationContext.SINGLETON;

//            List<Character> charList = new ArrayList<>(new HashSet(Chars.asList(filterString.toCharArray())));
            List<EscapedChar> charList = EscapedChar.asDedupedList(filterString);
            AnyCharMatchFilter textMatcherPrototype = null;
            if (!cache.containsKey(filterString)) {
                VelocityContext ctx = new VelocityContext();
                final String genClassName = "AsciiAnyCharMatcher_" + count++;
                ctx.put(functionClass.name(), genClassName);
                ctx.put(filter.name(), filterString);
                ctx.put("charMatchSet", charList);
                Class newClass = generateAndCompile(textMatcherPrototype, ANYCHAR_MATCH_FILTER, generationConfig, ctx);
                textMatcherPrototype = (AnyCharMatchFilter) newClass.newInstance();
                cache.put(filterString, textMatcherPrototype);
            } else {
                textMatcherPrototype = cache.get(filterString);
            }
            return textMatcherPrototype;
        } catch (IOException | MethodInvocationException | ParseErrorException | ResourceNotFoundException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("unable to create node", ex);
        }
    }

    public AnyCharMatchFilter createNodeNew(Map config, NodeRegistry registry) {
        try {
            String filterString = ((String) config.get(AnyCharMatchFilter.KEY_FILTER_ARRAY));
            GenerationContext generationConfig = GenerationContext.SINGLETON;

//            List<Character> charList = new ArrayList<>(new HashSet(Chars.asList(filterString.toCharArray())));
            List<EscapedChar> charList = EscapedChar.asDedupedList(filterString);
            AnyCharMatchFilter textMatcherPrototype = null;
            if (!cache.containsKey(filterString)) {
                if (filterString.length() == 1) {
                    textMatcherPrototype = new SingleCharMatcher(filterString.charAt(0));
                } else {
                    VelocityContext ctx = new VelocityContext();
                    final String genClassName = "AsciiAnyCharMatcher_" + count++;
                    ctx.put(functionClass.name(), genClassName);
                    ctx.put(filter.name(), filterString);
                    ctx.put(imports.name(), importMap.asString());
                    
                    ctx.put("charMatchSet", charList);
                    Class newClass = generateAndCompile(textMatcherPrototype, ANYCHAR_MATCH_FILTER, generationConfig, ctx);
                    textMatcherPrototype = (AnyCharMatchFilter) newClass.newInstance();
                }
                cache.put(filterString, textMatcherPrototype);
            } else {
                textMatcherPrototype = cache.get(filterString);
            }
            return textMatcherPrototype;
        } catch (IOException | MethodInvocationException | ParseErrorException | ResourceNotFoundException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("unable to create node", ex);
        }
    }

}
