package ome.dsl.velocity;

import ome.dsl.SemanticType;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.util.Collection;

public class MultiFileGenerator extends Generator {

    /**
     * Callback for formatting final filename
     */
    public interface FileNameFormatter {
        String format(SemanticType t);
    }

    /**
     * Folder to write velocity generated content
     */
    private File outputDir;

    /**
     * callback for formatting output file name
     */
    private FileNameFormatter formatFileName;

    private MultiFileGenerator(Builder builder) {
        super(builder);
        this.outputDir = builder.outputDir;
        this.formatFileName = builder.formatFileName;
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

    public static class Builder extends Generator.Builder {
        private File outputDir;
        private FileNameFormatter formatFileName;

        public Builder setOutputDir(File outputDir) {
            this.outputDir = outputDir;
            return this;
        }

        public Builder setFileFormatter(FileNameFormatter callback) {
            this.formatFileName = callback;
            return this;
        }

        public MultiFileGenerator build() {
            return new MultiFileGenerator(this);
        }
    }
}
