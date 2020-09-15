package com.fluxtion.integration;

import static com.fluxtion.builder.generation.GenerationContext.DEFAULT_CLASSLOADER;
import com.fluxtion.generator.compiler.OutputRegistry;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log4j2
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        DEFAULT_CLASSLOADER = OutputRegistry.INSTANCE.getClassLoader();;
        log.info("default classloader: '{}'", DEFAULT_CLASSLOADER);
        log.info("java.io.tmpdir: '{}'", System.getProperty("java.io.tmpdir"));
        log.debug("testing DEBUG message");
    }

}
