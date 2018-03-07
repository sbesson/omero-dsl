package ome.dsl.velocity;

import ome.dsl.SemanticType;
import ome.dsl.SemanticTypeProcessor;
import ome.dsl.sax.MappingReader;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
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

    VelocityEngine velocity = new VelocityEngine();

    /**
     * Profile thing
     */
    String profile;

    /**
     * Collection of .ome.xml files to process
     */
    List<File> omeXmlFiles;

    /**
     * Velocity template file name
     */
    File template;

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

    String findTemplate() {
        String resPath = (String) velocity.getProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH);
        if (resPath != null && (resPath.isEmpty() || resPath.equals("."))) {
            return template.toString();
        } else {
            return template.getName();
        }
    }

    void writeToFile(VelocityContext vc, Template template, File destination) {
        try (BufferedWriter output = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(destination), StandardCharsets.UTF_8))) {
            template.merge(vc, output);
        } catch (Exception e) {
            logger.error("", e);
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
