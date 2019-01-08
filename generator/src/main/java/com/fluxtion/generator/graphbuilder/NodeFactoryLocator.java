package com.fluxtion.generator.graphbuilder;

import com.fluxtion.builder.generation.GenerationContext;
import com.googlecode.gentyref.GenericTypeReflector;
import com.fluxtion.builder.node.NodeFactory;
import com.fluxtion.builder.node.NodeRegistry;
import com.fluxtion.generator.model.CbMethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Constructs a graph of nodes from a root node, using a set of factories to
 * construct dependencies.
 *
 * @author greg
 */
public class NodeFactoryLocator {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeFactoryLocator.class);

    public static final Map<Class, CbMethodHandle> findCallbackByPackage(String rootPackage) throws Exception {
        Map<Class, CbMethodHandle> class2Factory = new HashMap<>();
        Reflections reflections = new Reflections(rootPackage);
        Set<Class<? extends NodeFactory>> subTypes = reflections.getSubTypesOf(NodeFactory.class);
        LOGGER.debug("search root:{} subtypes of NodeFactory:{}", rootPackage, subTypes);
        for (Class<? extends NodeFactory> clazz : subTypes) {
            NodeFactory obj = clazz.newInstance();
            Method createMethod = clazz.getMethod("createNode", Map.class, NodeRegistry.class);
            ParameterizedType paramType = (ParameterizedType) GenericTypeReflector.getExactSuperType(clazz, NodeFactory.class);
            Class targetClass = (Class) paramType.getActualTypeArguments()[0];
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("type:" + clazz.getCanonicalName() + " building:" + targetClass);
            }
            class2Factory.put(targetClass, new CbMethodHandle(createMethod, obj, "node_factory_" + targetClass.getName()));
        }
        return class2Factory;
    }

    public static final Set<Class<? extends NodeFactory>> findFactoryByPackage(String rootPackage) throws Exception {
        Reflections reflections;
        if (GenerationContext.SINGLETON != null && GenerationContext.SINGLETON.getClassLoader() != null) {
            LOGGER.debug("using custom class loader to search for factories");
//            reflections = new Reflections(rootPackage, (GenerationContext.SINGLETON.getClassLoader()));
            ClassLoader loader = GenerationContext.SINGLETON.getClassLoader();
            ConfigurationBuilder configBuilder = new ConfigurationBuilder()
                    .filterInputsBy(FilterBuilder.parsePackages("+com"))
                    .addClassLoaders(loader, NodeFactory.class.getClassLoader())
                    .addUrls(ClasspathHelper.forPackage(rootPackage));
            if(loader instanceof URLClassLoader){
                configBuilder.addUrls(((URLClassLoader)loader).getURLs());
            }
            reflections = new Reflections(configBuilder);
        } else {
            if (GenerationContext.SINGLETON == null) {
                LOGGER.debug("GenerationContext is null using default class loader to search for factories");
            } else {
                LOGGER.debug("custom class loader is null using default class loader to search for factories");
            }
            reflections = new Reflections(rootPackage);
        }
        Set<Class<? extends NodeFactory>> subTypes = reflections.getSubTypesOf(NodeFactory.class);
        LOGGER.debug("package search root:{}, found NodeFactory:{}", rootPackage, subTypes);
        return subTypes;
    }

}
