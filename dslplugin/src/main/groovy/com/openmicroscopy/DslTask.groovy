package com.openmicroscopy

import ome.dsl.SemanticTypeProcessor
import ome.dsl.Utils
import ome.dsl.sax.MappingReader
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class DslTask extends DefaultTask {
    String profile = "psql"

    File mappingsPath
    File velocityTemplateFile

    File outputPath
    String outputFileExtension

    @TaskAction
    def apply() {
        // Load template files
        def files = Utils.getFilesInRes(mappingsPath.toString(), "*.ome.xml")

        // Parse template files to obtain complete type map
        def typeMap = new HashMap<>()
        def sr = new MappingReader(profile)
        for (File file : files) {
            if (file.exists()) {
                typeMap.putAll(sr.parse(file));
            }
        }

        // Process type map and convert to a list of types for velocity engine
        def types = new SemanticTypeProcessor(profile, typeMap)
                .call()

        // return new SemanticTypeProcessor(profile, typeMap).call();
    }


}
