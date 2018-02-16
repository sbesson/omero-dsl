package com.openmicroscopy

import ome.dsl.velocity.JavaGenerator
import org.apache.velocity.app.VelocityEngine
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class DslTask extends DefaultTask {

    String profile = "psql"
    String velocityFile
    File mapFilesPath
    File outputPath

    VelocityEngine velocityEngine

    @TaskAction
    def apply() {
        def generator = new JavaGenerator.Builder()
                .setProfile(profile)
                .setTemplateFile(velocityFile)
                .setSourceDir(mapFilesPath)
                .setOutputDir(outputPath)
                .build()
        generator.setVelocityEngine(velocityEngine)
        generator.run()
    }


}
