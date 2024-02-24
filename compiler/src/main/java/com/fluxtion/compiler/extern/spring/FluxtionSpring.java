package com.fluxtion.compiler.extern.spring;

import com.fluxtion.compiler.EventProcessorConfig;
import com.fluxtion.compiler.Fluxtion;
import com.fluxtion.compiler.FluxtionCompilerConfig;
import com.fluxtion.compiler.generation.RuntimeConstants;
import com.fluxtion.runtime.EventProcessor;
import com.fluxtion.runtime.audit.Auditor;
import com.fluxtion.runtime.partition.LambdaReflection;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableConsumer;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Provides utility functions to build Fluxtion {@link EventProcessor} using a spring {@link ApplicationContext} to define
 * the object instances managed by Fluxtion.
 */
public class FluxtionSpring {

    private final static Logger LOGGER = LoggerFactory.getLogger(FluxtionSpring.class);
    private final ApplicationContext context;
    private Consumer<EventProcessorConfig> configCustomizer = c -> {
    };

    public FluxtionSpring(String springFile) {
        this(new FileSystemXmlApplicationContext(springFile));
        LOGGER.debug("loading spring springFile:{}", springFile);
    }

    public FluxtionSpring(String springFile, Consumer<EventProcessorConfig> configCustomizer) {
        this(new FileSystemXmlApplicationContext(springFile), configCustomizer);
        LOGGER.debug("loading spring springFile:{}", springFile);
    }

    public FluxtionSpring(ApplicationContext context, Consumer<EventProcessorConfig> configCustomizer) {
        this.context = context;
        this.configCustomizer = configCustomizer;
    }

    public FluxtionSpring(ApplicationContext context) {
        this.context = context;
    }

    public static EventProcessor<?> compileAot(File springFile, String className, String packageName) {
        return new FluxtionSpring(springFile.toURI().toString())._compileAot(
                c -> {
                    c.setClassName(className);
                    c.setPackageName(packageName);
                });
    }

    @SneakyThrows
    public static EventProcessor<?> compileAot(ClassLoader classLoader, File springFile, String className, String packageName) {
        ClassUtils.overrideThreadContextClassLoader(classLoader);
        return new FluxtionSpring(springFile.toURI().toString())._compileAot(c -> {
            c.setClassName(className);
            c.setPackageName(packageName);
            c.setCompileSource(false);
            String overrideOutputDirectory = System.getProperty(RuntimeConstants.OUTPUT_DIRECTORY);
            if (overrideOutputDirectory != null && !overrideOutputDirectory.isEmpty()) {
                c.setOutputDirectory(overrideOutputDirectory);
            }
            String overrideResourceDirectory = System.getProperty(RuntimeConstants.RESOURCES_DIRECTORY);
            if (overrideResourceDirectory != null && !overrideResourceDirectory.isEmpty()) {
                c.setResourcesOutputDirectory(overrideResourceDirectory);
            }
        });
    }

    public static EventProcessor<?> compileAot(
            Path springFile,
            SerializableConsumer<FluxtionCompilerConfig> compilerConfig) {
        FluxtionSpring fluxtionSpring = new FluxtionSpring(springFile.toAbsolutePath().toUri().toString());
        return fluxtionSpring._compileAot(compilerConfig);
    }

    public static EventProcessor<?> compileAot(
            Path springFile,
            Consumer<EventProcessorConfig> configCustomizer,
            SerializableConsumer<FluxtionCompilerConfig> compilerConfig) {
        return new FluxtionSpring(springFile.toAbsolutePath().toUri().toString(), configCustomizer)._compileAot(compilerConfig);
    }

    public static EventProcessor<?> compileAot(
            ApplicationContext context,
            SerializableConsumer<FluxtionCompilerConfig> compilerConfig) {
        return new FluxtionSpring(context)._compileAot(compilerConfig);
    }

    public static EventProcessor<?> compileAot(ApplicationContext context, String className, String packageName) {
        return new FluxtionSpring(context)._compileAot(c -> {
            c.setClassName(className);
            c.setPackageName(packageName);
        });
    }

    public static EventProcessor<?> compileAot(
            ApplicationContext context,
            Consumer<EventProcessorConfig> configCustomizer,
            SerializableConsumer<FluxtionCompilerConfig> compilerConfig) {
        return new FluxtionSpring(context, configCustomizer)._compileAot(compilerConfig);
    }

    public static EventProcessor<?> compile(Path springFile) {
        FluxtionSpring fluxtionSpring = new FluxtionSpring(springFile.toAbsolutePath().toUri().toString());
        return fluxtionSpring._compile();
    }

    public static EventProcessor<?> compile(Path springFile, Consumer<EventProcessorConfig> configCustomizer) {
        return new FluxtionSpring(springFile.toAbsolutePath().toUri().toString(), configCustomizer)._compile();
    }

    public static EventProcessor<?> compile(ApplicationContext context) {
        return new FluxtionSpring(context)._compile();
    }

    public static EventProcessor<?> compile(ApplicationContext context, Consumer<EventProcessorConfig> configCustomizer) {
        return new FluxtionSpring(context, configCustomizer)._compile();
    }

    public static EventProcessor<?> interpret(Path springFile) {
        FluxtionSpring fluxtionSpring = new FluxtionSpring(springFile.toAbsolutePath().toUri().toString());
        return fluxtionSpring._interpret();
    }

    public static EventProcessor<?> interpret(Path springFile, Consumer<EventProcessorConfig> configCustomizer) {
        return new FluxtionSpring(springFile.toAbsolutePath().toUri().toString(), configCustomizer)._interpret();
    }

    public static EventProcessor<?> interpret(ApplicationContext context) {
        return new FluxtionSpring(context)._interpret();
    }

    public static EventProcessor<?> interpret(ApplicationContext context, Consumer<EventProcessorConfig> configCustomizer) {
        return new FluxtionSpring(context, configCustomizer)._interpret();
    }

    private EventProcessor<?> _compileAot(LambdaReflection.SerializableConsumer<FluxtionCompilerConfig> compilerConfig) {
        return Fluxtion.compile(this::addNodes, compilerConfig);
    }

    private EventProcessor<?> _compile() {
        return Fluxtion.compile(this::addNodes);
    }

    private EventProcessor<?> _interpret() {
        return Fluxtion.interpret(this::addNodes);
    }

    private void addNodes(EventProcessorConfig config) {
        LOGGER.debug("loading spring context:{}", context);
        List<Auditor> auditorMap = new ArrayList<>();
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            Object bean = context.getBean(beanDefinitionName);
            if (bean instanceof FluxtionSpringConfig) {
                FluxtionSpringConfig springConfig = (FluxtionSpringConfig) bean;
                auditorMap.addAll(springConfig.getAuditors());
                config.addEventAudit(springConfig.getLogLevel());
            }
        }

        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            Object bean = context.getBean(beanDefinitionName);
            if (!(bean instanceof FluxtionSpringConfig)) {
                if (bean instanceof Auditor && auditorMap.contains(bean)) {
                    LOGGER.debug("adding auditor:{} to fluxtion", beanDefinitionName);
                    config.addAuditor((Auditor) bean, beanDefinitionName);
                } else {
                    LOGGER.debug("adding bean:{} to fluxtion", beanDefinitionName);
                    config.addNode(bean, beanDefinitionName);
                }
            }
        }
        configCustomizer.accept(config);
    }
}
