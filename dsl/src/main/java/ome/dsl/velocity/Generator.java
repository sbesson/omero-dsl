package ome.dsl.velocity;

import ome.dsl.SemanticType;
import ome.dsl.SemanticTypeProcessor;
import ome.dsl.sax.MappingReader;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

abstract class Generator implements Runnable {

    final Logger logger = LoggerFactory.getLogger(JavaGenerator.class);

    VelocityEngine velocityEngine;

    /**
     * Profile thing
     */
    String profile;

    /**
     * Collection of .ome.xml files to process
     */
    List<File> omeXmlFiles;

    /**
     * Velocity templateFile file
     */
    File templateFile;

    Generator() {
        velocityEngine = new VelocityEngine();
        /*
         * Configuration documentation:
         * http://velocity.apache.org/engine/1.7/developer-guide.html#configuration-examples
         */
        velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_INSTANCE, logger);
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
        velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, "true");
        velocityEngine.setProperty("file.resource.loader.class",
                FileResourceLoader.class.getName());
        velocityEngine.init();
    }

    List<SemanticType> loadSemanticTypes(Collection<File> files) {
        Map<String, SemanticType> typeMap = new HashMap<>();
        MappingReader sr = new MappingReader(profile);
        for (File file : files) {
            if (file.exists()) {
                typeMap.putAll(sr.parse(file));
            }
        }

        if (typeMap.isEmpty()) {
            return Collections.emptyList(); // Skip when no files, otherwise we overwrite.
        }

        return new SemanticTypeProcessor(profile, typeMap).call();
    }

    void writeToFile(VelocityContext vc, Template template, File destination) {
        try (BufferedWriter output = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(destination), StandardCharsets.UTF_8))) {
            template.merge(vc, output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    File prepareOutput(File target) {
        if (!target.exists()) {
            File parent = target.getParentFile();
            if (parent != null && !parent.exists()) {
                if (!target.getParentFile().mkdirs()) {
                    throw new RuntimeException("Failed to create file for output");
                }
            }
        }
        return target;
    }

    File prepareOutput(String target) {
        return prepareOutput(new File(target));
    }

}
