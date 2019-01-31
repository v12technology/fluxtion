package com.fluxtion.generator.graphbuilder;

import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.builder.node.NodeFactory;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;
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
    
    public static final Set<Class<? extends NodeFactory>> nodeFactorySet() throws Exception {
        LOGGER.debug("new NodeFactory locator - ignoring package");
        ServiceLoader<NodeFactory> loadServices;
        Set<Class<? extends NodeFactory>> subTypes = new HashSet<>();
        if (GenerationContext.SINGLETON != null && GenerationContext.SINGLETON.getClassLoader() != null) {
            LOGGER.debug("using custom class loader to search for factories");
            loadServices = ServiceLoader.load(NodeFactory.class, GenerationContext.SINGLETON.getClassLoader());
        } else {
            loadServices = ServiceLoader.load(NodeFactory.class);
        }
        loadServices.forEach((t) -> {
            subTypes.add(t.getClass());
        });
        LOGGER.debug("loaded NodeFactory services:{}", subTypes);
        return subTypes;
    }

}
