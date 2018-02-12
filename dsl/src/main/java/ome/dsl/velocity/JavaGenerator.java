package ome.dsl.velocity;

import ome.dsl.SemanticType;
import ome.dsl.SemanticTypeProcessor;
import ome.dsl.Utils;
import ome.dsl.sax.MappingReader;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JavaGenerator extends Generator {

    private final static String PROFILE = "psql";

    /**
     * Velocity template file for generating java classes
     */
    private final static String TEMPLATE_FILE = "object.vm";

    /**
     * Output structure for Java files e.g. 'package/class/name.java'
     */
    private final static String PKG_PLACEHOLDER = "{package-dir}";
    private final static String CLS_PLACEHOLDER = "{class-name}";
    private final static String JAVA_OUTPUT = PKG_PLACEHOLDER + "/" + CLS_PLACEHOLDER + ".java";

    /**
     * Folder to dump generated java files into
     */
    private final static String OUTPUT_FOLDER = "../model/src/main/java-generated/";

    @Override
    public void run() {
        Collection<SemanticType> types = loadSemanticTypes();
        if (types.isEmpty()) {
            return; // Skip when no files, otherwise we overwrite.
        }

        for (SemanticType st : types) {
            VelocityContext vc = new VelocityContext();
            vc.put("type", st);

            Template template = velocityEngine.getTemplate(TEMPLATE_FILE);
            File destination = prepareOutput(st);
            writeToFile(vc, template, destination);
        }
    }

    private Collection<SemanticType> loadSemanticTypes() {
        Collection<File> files = Utils.getFilesInRes("mappings/",
                "*.ome.xml");
        Map<String, SemanticType> typeMap = new HashMap<>();

        MappingReader sr = new MappingReader(PROFILE);
        for (File file : files) {
            if (file.exists()) {
                typeMap.putAll(sr.parse(file));
            }
        }

        if (typeMap.isEmpty()) {
            return Collections.emptyList(); // Skip when no files, otherwise we overwrite.
        }

        return new SemanticTypeProcessor(PROFILE, typeMap).call();
    }

    private File prepareOutput(SemanticType st) throws RuntimeException {
        String className = st.getShortname();
        String packageName = st.getPackage();

        String target = OUTPUT_FOLDER + JavaGenerator.JAVA_OUTPUT;
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
}
