package ome.dsl.velocity;

import ome.dsl.SemanticType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;

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

    private File prepareOutput(SemanticType st) {
        String className = st.getShortname();
        String packageName = st.getPackage();

        String target = Paths.get(outputDir.getPath(), JavaGenerator.JAVA_OUTPUT).toString();
        target = target.replace(CLS_PLACEHOLDER, className);
        target = target.replace(PKG_PLACEHOLDER, packageName);

        return super.prepareOutput(target);
    }

    public static class Builder extends Generator.Builder {
        File outputDir;

        public Generator.Builder setOutputDir(File outputDir) {
            this.outputDir = outputDir;
            return this;
        }

        @Override
        public Generator build() {
            return new JavaGenerator(this);
        }
    }
}
