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
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.generator;

import com.fluxtion.generator.compiler.SepCompiler;
import com.fluxtion.generator.compiler.SepCompilerConfig;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import net.openhft.compiler.CompilerUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the SEP compiler.
 *
 * <pre>
 * fluxtion.configClass
 * fluxtion.templateSep
 * fluxtion.templateDebugSep
 * fluxtion.nodeNamingClass
 * fluxtion.filterNamingClass
 *
 * fluxtion.supportDirtyFiltering
 * fluxtion.assignNonPublicMembers
 *
 * fluxtion.generateDebugPrep
 * fluxtion.generateTestDecorator
 *
 * fluxtion.className
 * fluxtion.packageName
 * fluxtion.outputDirectory
 * fluxtion.resourcesOutputDirectory
 *
 * fluxtion.rootFactoryClass
 * fluxtion.yamlFactoryConfig
 * </pre>
 *
 * @author greg
 */
public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    private static Options options;
    private static DefaultParser parser;
    private static CommandLine cmdLine;
    private static URLClassLoader loader;

    public static void main(String[] args) {
        LOG.debug("running gnerator application");
        printErrorAndExit(buildCommandLine(args));
        printErrorAndExit(buildClasspath());
        printErrorAndExit(buildSepProcessor());
    }

    public static Options addDefaultOptions(Options extendedptions) {
        if (options == null) {
            options = new Options();
            //classpath
            options.addOption(Option.builder("cp").longOpt("classPath").valueSeparator(';').argName("classpath").desc("Classpath fluxtion uses to find application classes referenced in the SEPBuilder class. Separator char ';'.").required(true).hasArgs().build());
            //general config
            options.addOption(Option.builder("cc").longOpt("configClass").hasArg().desc("The SEPConfig class fluxtion will use to construct the SEP.").argName("class name").required(false).build());
            options.addOption(Option.builder("st").longOpt("sepTemplate").hasArg().desc("Override the template file fluxtion will use to generate the SEP.").argName("velocity file").required(false).build());
            options.addOption(Option.builder("dt").longOpt("sepDebugTemplate").hasArg().desc("Override the debug template file fluxtion will use to generate the debug SEP.").argName("velocity file").required(false).build());
            options.addOption(Option.builder("nc").longOpt("nodeNamingClass").hasArg().desc("Override default naming strategy for variable names in generated SEP.").argName("class name").required(false).build());
            options.addOption(Option.builder("fc").longOpt("filterNamingClass").hasArg().desc("Override default naming strategy for native filters names in generated SEP.").argName("class name").required(false).build());
            //specific of generation formatting
            options.addOption(Option.builder("df").longOpt("supportDirtyFiltering").hasArg().desc("If set at least one parent must return true from an event handling method for an event to propogate to dependent nodes.").required(false).build());
            options.addOption(Option.builder("pa").longOpt("assignPrivate").hasArg().desc("If set generated SEP will attempt to use reflection style assignment for private non-transient members").required(false).build());
            //output artifacts to creat
            options.addOption(Option.builder("gd").longOpt("generateDescription").hasArg().desc("Generate a meta-data description of SEP.").required(false).build());
            options.addOption(Option.builder("gb").longOpt("generateDebugPrep").hasArg().desc("Generate a debug version oi the SEP for using with fluxtion debug tool.").required(false).build());
            options.addOption(Option.builder("bc").longOpt("buildClasses").hasArg(true).desc("Compile classes from generated source.").required(false).build());
            options.addOption(Option.builder("fs").longOpt("formatSource").hasArg(true).desc("Format generated source.").required(false).build());
//        options.addOption(Option.builder("generateTestDecorator").hasArg(false).desc("Generate a test decorator for use with the SEP").required(false).build());
            //output locations
            options.addOption(Option.builder("oc").longOpt("outClass").hasArg().desc("Simple class name for the generated SEP. This class is the entry point for event processing.").argName("class name").required(true).build());
            options.addOption(Option.builder("op").longOpt("outPackage").hasArg().desc("Package name for the generated SEP.").argName("package name").required(true).build());
            options.addOption(Option.builder("od").longOpt("outDirectory").hasArg().desc("The directory where the SEP will generate source artifacts.").argName("directory").required(false).build());
            options.addOption(Option.builder("bd").longOpt("buildDirectory").hasArg().desc("The directory where the SEP will compile").argName("directory").required(false).build());
            options.addOption(Option.builder("or").longOpt("outResDirectory").hasArg().desc("The directory where the SEP will generate non runtime artifacts, such as debug information.").argName("directory").required(false).build());
            //factoy config
            options.addOption(Option.builder("rf").longOpt("rootFactoryClass").hasArg().desc("The root factory SEP will use when using factory generation.").argName("class name").required(false).build());
            options.addOption(Option.builder("yc").longOpt("yamlFactoryConfig").hasArg().desc("Yaml file for multiple factory configurations in a single SEP.").argName("yml file").required(false).build());
            options.addOption(Option.builder("d").longOpt("debug").hasArg(false).desc("debug log output").argName("yml file").build());
            //  
        }
        if(extendedptions!=null){
            extendedptions.getOptions().stream().forEach(o -> options.addOption(o));
        }
        return options;
    }

    private static Pair<Boolean, String> buildSepProcessor() {
        LOG.debug("buildSepProcessor");
        MutablePair<Boolean, String> result = new MutablePair<>(Boolean.TRUE, "");
        SepCompilerConfig cfg = buildCompilerConfig();
        SepCompiler compiler = new SepCompiler();
        try {
            compiler.compile(cfg);
        } catch (Exception ex) {
            ex.printStackTrace();
            LOG.debug("error compiling", ex);
            result.left = false;
            result.right = "could not compile :" + cmdLine.getOptionValue("configClass") + " error masg:" + ex.getMessage();
        }
        return result;
    }

    private static Pair<Boolean, String> buildClasspath() {
        LOG.debug("buildingClasspath");
        MutablePair<Boolean, String> result = new MutablePair<>(Boolean.TRUE, "");
        String[] cpArray = cmdLine.getOptionValues("cp");
        URL[] urls = new URL[cpArray.length];
        for (int i = 0; i < cpArray.length; i++) {
            try {
                File file = new File(cpArray[i]);
                if (file.isDirectory()) {
                    urls[i] = file.toURI().toURL();
                } else {
                    urls[i] = new URL("jar:" + new File(cpArray[i]).toURI().toURL() + "!/");
                }
            } catch (MalformedURLException ex) {
                LOG.debug("error building classpath", ex);
                result.left = false;
                result.right = "could not load jar file:" + cpArray[i] + " error masg:" + ex.getMessage();
                return result;
            }
            CompilerUtils.addClassPath(cpArray[i]);
        }

        LOG.debug("user classpath URL list:" + Arrays.toString(urls));
        loader = URLClassLoader.newInstance(urls, Main.class.getClassLoader());
        try {
            Class<?> loadClass = loader.loadClass(cmdLine.getOptionValue("configClass"));
        } catch (ClassNotFoundException ex) {
            result.left = false;
            result.right = "could not load builder class :" + cmdLine.getOptionValue("configClass") + " error masg:" + ex.getMessage();
            return result;
        }
        return result;
    }

    private static SepCompilerConfig buildCompilerConfig() {
        SepCompilerConfig config = new SepCompilerConfig();
        config.setAssignNonPublicMembers(bool("pa", config.isAssignNonPublicMembers()));
        config.setBuildOutputdirectory(val("bd", "output/classes"));
        config.setClassLoader(loader);
        config.setClassName(val("oc", "SepProcessor"));
        config.setConfigClass(val("cc", config.getConfigClass()));
        //not configured
        config.setFilterNamingClass(val("fc", null));
        //not configured
//        config.setGenerateDebugPrep(bool("gd", config.isGenerateDebugPrep()));
        //TODO separate sep diagram and debugger generation - by default always generate images
//        config.setGenerateDebugPrep(true);
        config.setGenerateDebugPrep(bool("gb", config.isGenerateDebugPrep()));
        config.setGenerateDescription(bool("gd", config.isGenerateDescription()));
        //not configured
        config.setGenerateTestDecorator(bool("gt", config.isGenerateTestDecorator()));
        config.setCompileSource(bool("bc", config.isCompileSource()));
        config.setFormatSource(bool("fs", config.isFormatSource()));
        config.setNodeNamingClass(val("nc", null));
        config.setOutputDirectory(val("od", "output/source"));
        config.setPackageName(val("op", "sep.generated"));
        config.setResourcesOutputDirectory(val("or", "output/resources"));
        config.setRootFactoryClass(val("rf", null));
        config.setSupportDirtyFiltering(bool("df", config.isSupportDirtyFiltering()));
        config.setTemplateDebugSep(val("dt", config.getTemplateDebugSep()));
        config.setTemplateSep(val("st", config.getTemplateSep()));
        config.setYamlFactoryConfig(val("yc", null));
        LOG.debug(config.toString());
        return config;
    }

    private static String val(String key, String defaultVal) {
        String val = cmdLine.getOptionValue(key);
        return val == null ? defaultVal : val;
    }

    private static boolean bool(String key, boolean defaultVal) {
        String val = cmdLine.getOptionValue(key);
        return val == null ? defaultVal : Boolean.valueOf(val);
    }

    private static Pair<Boolean, String> buildCommandLine(String[] args) {
        MutablePair<Boolean, String> result = new MutablePair<>(Boolean.TRUE, "");
        LOG.debug("Command line args:" + Arrays.toString(args));
        addDefaultOptions(null);
        parser = new DefaultParser();
        try {
            cmdLine = parser.parse(options, args);
        } catch (ParseException parseException) {
            System.out.println("Problem parsing command line, " + parseException.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("fluxtion-generate", options, true);
            result.left = false;
            return result;
        }
        return result;
    }

    private static void printErrorAndExit(Pair<Boolean, String> result) {
        if (result.getKey()) {
            return;
        } else if (result.getRight() != null && result.getRight().length() > 0) {
            System.err.println(result.getRight());
        }
        System.exit(-1);
    }
}
