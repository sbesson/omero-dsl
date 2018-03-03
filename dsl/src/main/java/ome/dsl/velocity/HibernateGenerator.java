package ome.dsl.velocity;

import ome.dsl.SemanticType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class HibernateGenerator extends Generator {

    private static final String HIBERNATE_OUTPUT = "${resrc.dest}/hibernate.cfg.xml";

    /**
     * Folder to write velocity generated content
     */
    private File outputFile;

    private HibernateGenerator(Builder builder) {
        this.profile = builder.profile;
        this.sourceDir = builder.sourceDir;
        this.templateFile = builder.templateFile;
        this.outputFile = builder.outputFile;
    }

    @Override
    public void run() {
        // Load source files
        Collection<File> files = FileUtils.listFiles(sourceDir,
                new WildcardFileFilter("*.ome.xml"), null);

        // Create list of semantic types from source files
        List<SemanticType> types = loadSemanticTypes(files);
        if (types.isEmpty()) {
            return; // Skip when no files, otherwise we overwrite.
        }

        // Sort types by short name
        types.sort(Comparator.comparing(SemanticType::getShortname));

        // Velocity process the semantic types
        for (SemanticType st : types) {
            VelocityContext vc = new VelocityContext();
            vc.put("types", st);

            Template template = velocityEngine.getTemplate(templateFile);
            writeToFile(vc, template, prepareOutput(this.outputFile));
        }
    }

    public static class Builder extends Generator.Builder {
        File outputFile;

        public Generator.Builder setOutputDir(File outputDir) {
            this.outputFile = outputDir;
            return this;
        }

        @Override
        public Generator build() {
            return new HibernateGenerator(this);
        }
    }
}
