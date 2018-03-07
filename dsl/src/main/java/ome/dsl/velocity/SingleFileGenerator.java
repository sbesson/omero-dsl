package ome.dsl.velocity;

import ome.dsl.SemanticType;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.util.Comparator;
import java.util.List;

public class SingleFileGenerator extends Generator {

    /**
     * Folder to write velocity generated content
     */
    private File outFile;


    private SingleFileGenerator(Builder builder) {
        super(builder);
        this.outFile = builder.outFile;
    }

    public void run() {
        // Create list of semantic types from source files
        List<SemanticType> types = loadSemanticTypes(omeXmlFiles);
        if (types.isEmpty()) {
            return; // Skip when no files, otherwise we overwrite.
        }

        // Sort types by short name
        types.sort(Comparator.comparing(SemanticType::getShortname));

        /// Put all types in velocity context
        VelocityContext vc = new VelocityContext();
        vc.put("types", types);

        // Get the template file
        Template t = velocity.getTemplate(findTemplate());
        writeToFile(vc, t, prepareOutput(outFile));
    }

    public static class Builder extends Generator.Builder {
        private File outFile;

        public Builder setOutFile(File outFile) {
            this.outFile = outFile;
            return this;
        }

        @Override
        public SingleFileGenerator build() {
            return new SingleFileGenerator(this);
        }
    }

}
