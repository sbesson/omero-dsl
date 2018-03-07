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

public class JavaGenerator {

    /**
     * Callback for formatting final filename
     */
    public interface FileNameFormatter {
        String format(SemanticType t);
    }

    /**
     * Output structure for Java files e.g. 'package/class/name.java'
     */
    private final static String PKG_PLACEHOLDER = "{package-dir}";
    private final static String CLS_PLACEHOLDER = "{class-name}";
    private final static String JAVA_OUTPUT = PKG_PLACEHOLDER + "/" + CLS_PLACEHOLDER + ".java";

    final Logger logger = LoggerFactory.getLogger(JavaGenerator.class);

    /**
     * Profile thing
     */
    private String profile;

    /**
     * Collection of .ome.xml files to process
     */
    private List<File> omeXmlFiles;

    /**
     * Velocity template file name
     */
    private File template;

    /**
     * Folder to write velocity generated content
     */
    private File outputDir;

    /**
     * callback for formatting output file name
     */
    private FileNameFormatter formatFileName;

    private VelocityEngine velocity = new VelocityEngine();

    private JavaGenerator(JavaGenerator.Builder builder) {
        this.profile = builder.profile;
        this.omeXmlFiles = builder.omeXmlFiles;
        this.template = builder.template;
        this.outputDir = builder.outputDir;
        this.formatFileName = builder.formatFileName;
        this.velocity.init(builder.properties);
    }

    public void run() {
        // Create list of semantic types from source files
        Collection<SemanticType> types = loadSemanticTypes(omeXmlFiles);
        if (types.isEmpty()) {
            return; // Skip when no files, otherwise we overwrite.
        }

        // Get the template file
        Template t = velocity.getTemplate(findTemplate());

        // Velocity process the semantic types
        for (SemanticType st : types) {
            VelocityContext vc = new VelocityContext();
            vc.put("type", st);

            String filename = formatFileName.format(st);
            File destination = new File(outputDir, filename);
            writeToFile(vc, t, prepareOutput(destination));
        }
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

    String findTemplate() {
        String resPath = (String) velocity.getProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH);
        if (resPath != null && (resPath.isEmpty() || resPath.equals("."))) {
            return template.toString();
        } else {
            return template.getName();
        }
    }

    public static class Builder {
        String profile;
        File template;
        File outputDir;
        Properties properties;
        FileNameFormatter formatFileName;
        List<File> omeXmlFiles;

        public Builder setProfile(String profile) {
            this.profile = profile;
            return this;
        }

        public Builder setOmeXmlFiles(List<File> source) {
            this.omeXmlFiles = source;
            return this;
        }

        public Builder setTemplate(File template) {
            this.template = template;
            return this;
        }

        public Builder setOutputDir(File outputDir) {
            this.outputDir = outputDir;
            return this;
        }

        public Builder setVelocityProperties(Properties p) {
            this.properties = p;
            return this;
        }

        public Builder setFileFormatter(FileNameFormatter callback) {
            this.formatFileName = callback;
            return this;
        }

        public JavaGenerator build() {
            return new JavaGenerator(this);
        }
    }
}
