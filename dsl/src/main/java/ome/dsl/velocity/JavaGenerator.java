package ome.dsl.velocity;

import ome.dsl.SemanticType;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class JavaGenerator extends Generator {

    /**
     * Output structure for Java files e.g. 'package/class/name.java'
     */
    private final static String PKG_PLACEHOLDER = "{package-dir}";
    private final static String CLS_PLACEHOLDER = "{class-name}";
    private final static String JAVA_OUTPUT = PKG_PLACEHOLDER + "/" + CLS_PLACEHOLDER + ".java";

    /**
     * Folder to write velocity generated content
     */
    private File outputDir;

    private JavaGenerator(Builder builder) {
        this.profile = builder.profile;
        this.omeXmlFiles = builder.omeXmlFiles;
        this.templateName = builder.template;
        this.outputDir = builder.outputDir;
        this.velocity.init(builder.properties);
    }

    @Override
    public void run() {
        // Create list of semantic types from source files
        Collection<SemanticType> types = loadSemanticTypes(omeXmlFiles);
        if (types.isEmpty()) {
            return; // Skip when no files, otherwise we overwrite.
        }

        // Velocity process the semantic types
        for (SemanticType st : types) {
            VelocityContext vc = new VelocityContext();
            vc.put("type", st);

            Template template = velocity.getTemplate(templateName);
            File destination = prepareOutput(st);
            writeToFile(vc, template, destination);
        }
    }

    private File prepareOutput(SemanticType st) {
        String className = st.getShortname();
        String packageName = st.getPackage();

        String target = Paths.get(outputDir.getPath(), JavaGenerator.JAVA_OUTPUT).toString();
        target = target.replace(CLS_PLACEHOLDER, className);
        target = target.replace(PKG_PLACEHOLDER, packageName);

        return super.prepareOutput(target);
    }

    public static class Builder {
        String profile;
        String template;
        File outputDir;
        List<File> omeXmlFiles;
        Properties properties;

        public Builder setProfile(String profile) {
            this.profile = profile;
            return this;
        }

        public Builder setOmeXmlFiles(List<File> source) {
            this.omeXmlFiles = source;
            return this;
        }

        public Builder setTemplate(String template) {
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

        public Generator build() {
            return new JavaGenerator(this);
        }
    }
}
