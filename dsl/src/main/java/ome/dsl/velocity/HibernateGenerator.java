package ome.dsl.velocity;

import ome.dsl.SemanticType;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import java.io.File;
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
        this.omeXmlFiles = builder.omeXmlFiles;
        this.templateFile = builder.templateFile;
        this.outputFile = builder.outputFile;
    }

    @Override
    public void run() {
        // Create list of semantic types from source files
        List<SemanticType> types = loadSemanticTypes(omeXmlFiles);
        if (types.isEmpty()) {
            return; // Skip when no files, otherwise we overwrite.
        }

        // Sort types by short name
        types.sort(Comparator.comparing(SemanticType::getShortname));

        // Velocity process the semantic types
        for (SemanticType st : types) {
            VelocityContext vc = new VelocityContext();
            vc.put("types", st);

            Template template = velocityEngine.getTemplate(templateFile.toString());
            writeToFile(vc, template, prepareOutput(this.outputFile));
        }
    }

    public static class Builder {
        String profile;
        File templateFile;
        File outputFile;
        List<File> omeXmlFiles;

        public Builder setProfile(String profile) {
            this.profile = profile;
            return this;
        }

        public Builder setOmeXmlFiles(List<File> omeXmlFiles) {
            this.omeXmlFiles = omeXmlFiles;
            return this;
        }

        public Builder setTemplateFile(File template) {
            this.templateFile = template;
            return this;
        }

        public Builder setOutputFile(File outputFile) {
            this.outputFile = outputFile;
            return this;
        }

        public Generator build() {
            return new HibernateGenerator(this);
        }
    }
}
