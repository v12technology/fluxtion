package com.fluxtion.ext.text.builder.csv;

import com.fluxtion.builder.annotation.ClassProcessor;
import com.fluxtion.builder.annotation.Disabled;
import com.fluxtion.ext.text.api.annotation.CsvMarshaller;
import com.fluxtion.generator.Generator;
import com.google.auto.service.AutoService;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author gregp
 */
@AutoService(ClassProcessor.class)
public class CsvAnnotationBeanBuilder implements ClassProcessor {

    private final Logger LOGGER = LoggerFactory.getLogger(CsvAnnotationBeanBuilder.class.getName());
    private File generatedDir;
    private File resourceDir;
    private File rootDir;
    private boolean warmup = true;

    @Override
    public void outputDirectories(File rootDir, File output, File resourceDir) {
        this.rootDir = rootDir;
        this.generatedDir = output;
        this.resourceDir = resourceDir;
    }
    
    @Override
    public void process(URL classPath) {
        if (warmup) {
            new Thread(Generator::warmupCompiler).start();
            warmup = false;
        }
        try {
            File fin = new File(classPath.toURI());
            LOGGER.info("CsvAnnotationBeanBuilder scanning url:'{}' for CSVMarshaller annotations", fin);
            try (ScanResult scanResult = new ClassGraph()
                    .enableAllInfo()
                    .overrideClasspath(fin)
                    .scan()) {
                ClassInfoList csvList = scanResult.getClassesWithAnnotation(CsvMarshaller.class.getCanonicalName());
                for (ClassInfo csvClassInfo : csvList) {
                    final Class<?> csvClass = csvClassInfo.loadClass();
                    AnnotationInfo annotationInfo = csvClassInfo.getAnnotationInfo(Disabled.class.getCanonicalName());
                    if (annotationInfo == null) {
                        LOGGER.info("Fluxtion generating CSV marshaller for:" + csvClass.getCanonicalName());
                        AnnotationParameterValueList params = csvClassInfo.getAnnotationInfo(CsvMarshaller.class.getCanonicalName()).getParameterValues();
                        Object pkgNameVal =  params.get("packageName");
                        String pkgName = pkgNameVal==null?csvClassInfo.getPackageName():pkgNameVal.toString();
                        CsvToBeanBuilder beanBuilder = CsvToBeanBuilder.nameSpace(pkgName);
                        if (generatedDir != null && resourceDir != null) {
                            beanBuilder.setOutputDirs(generatedDir.getCanonicalPath(), resourceDir.getCanonicalPath());
                        }
                        beanBuilder.mapBean(csvClassInfo.getSimpleName(), csvClass);
                    } else {
                        LOGGER.info("disabled Fluxtion CSV generation for:" + csvClass.getCanonicalName());
                    }
                }
            } catch (Exception ex) {
                LOGGER.error("problem generating csv marshallers", ex);
            }

        } catch (URISyntaxException ex) {
            LOGGER.error("problem generating csv marshallers", ex);
        }
        LOGGER.info("CsvAnnotationBeanBuilder completed");
    }

}
