package com.openmicroscopy

import ome.dsl.SemanticTypeProcessor
import ome.dsl.sax.MappingReader
import ome.dsl.velocity.JavaGenerator
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.IOFileFilter
import org.apache.commons.io.filefilter.WildcardFileFilter
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class DslTask extends DefaultTask {
    String profile = "psql"

    File mapFilesPath
    File velocityFile

    File outputPath
    String outputFileExtension

    @TaskAction
    def apply() {
        def generator = new JavaGenerator.Builder()
                .setProfile(profile)
                .setTemplateFile(velocityFile)
                .setSourceDir(mapFilesPath)
                .setOutputDir(outputPath)
                .build()

        generator.run()
    }


}
