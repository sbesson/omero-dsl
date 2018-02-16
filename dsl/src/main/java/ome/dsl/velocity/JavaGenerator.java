package ome.dsl.velocity;

import ome.dsl.SemanticType;
import ome.dsl.SemanticTypeProcessor;
import ome.dsl.sax.MappingReader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JavaGenerator extends Generator {

    /**
     * Output structure for Java files e.g. 'package/class/name.java'
     */
    private final static String PKG_PLACEHOLDER = "{package-dir}";
    private final static String CLS_PLACEHOLDER = "{class-name}";
    private final static String JAVA_OUTPUT = PKG_PLACEHOLDER + "/" + CLS_PLACEHOLDER + ".java";

    /**
     * Profile thing
     */
    private String profile;

    /**
     * Collection of .ome.xml files to process
     */
    private File sourceDir;

    /**
     * Velocity templateFile file
     */
    private String templateFile;

    /**
     * Folder to write velocity generated content
     */
    private File outputDir;

    private JavaGenerator(Builder builder) {
        this.profile = builder.profile;
        this.sourceDir = builder.sourceDir;
        this.templateFile = builder.templateFile;
        this.outputDir = builder.outputDir;
    }

    public void setVelocityEngine(VelocityEngine ve) {
        this.velocityEngine = ve;
    }

    @Override
    public void run() {
        // Load source files
        Collection<File> files = FileUtils.listFiles(sourceDir,
                new WildcardFileFilter("*.ome.xml"), null);

        // Create list of semantic types from source files
        Collection<SemanticType> types = loadSemanticTypes(files);
        if (types.isEmpty()) {
            return; // Skip when no files, otherwise we overwrite.
        }

        // Velocity process the semantic types
        for (SemanticType st : types) {
            VelocityContext vc = new VelocityContext();
            vc.put("type", st);

            Template template = velocityEngine.getTemplate(templateFile);
            File destination = prepareOutput(st);
            writeToFile(vc, template, destination);
        }
    }

    private Collection<SemanticType> loadSemanticTypes(Collection<File> files) {
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

    private File prepareOutput(SemanticType st) throws RuntimeException {
        String className = st.getShortname();
        String packageName = st.getPackage();

        String target = Paths.get(outputDir.getPath(), JavaGenerator.JAVA_OUTPUT).toString();
        target = target.replace(CLS_PLACEHOLDER, className);
        target = target.replace(PKG_PLACEHOLDER, packageName);

        File file = new File(target);
        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                if (!file.getParentFile().mkdirs()) {
                    throw new RuntimeException("Failed to create file for output");
                }
            }
        }
        return file;
    }

    private void writeToFile(VelocityContext vc, Template template, File destination) {
        try (BufferedWriter output = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(destination), StandardCharsets.UTF_8))) {
            template.merge(vc, output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Builder {

        private String profile;
        private File outputDir;
        private File sourceDir;
        private String templateFile;

        public Builder setProfile(String profile) {
            this.profile = profile;
            return this;
        }

        public Builder setOutputDir(File outputDir) {
            this.outputDir = outputDir;
            return this;
        }

        public Builder setSourceDir(File sourceDir) {
            this.sourceDir = sourceDir;
            return this;
        }

        public Builder setTemplateFile(String templateFile) {
            this.templateFile = templateFile;
            return this;
        }

        public JavaGenerator build() {
            return new JavaGenerator(this);
        }
    }


}
