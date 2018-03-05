package dslplugin

import ome.dsl.velocity.JavaGenerator
import org.apache.velocity.app.VelocityEngine
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class JavaTask extends DefaultTask {

    @Input
    String profile = "psql"

    @Input
    File velocityFile

    @InputFiles
    FileTree omeXmlFiles

    @OutputDirectory
    File outputPath

    VelocityEngine velocityEngine

    @TaskAction
    def apply() {
        def generator = new JavaGenerator.Builder()
                .setProfile(profile)
                .setOmeXmlFiles(omeXmlFiles as List)
                .setTemplateFile(velocityFile)
                .setOutputDir(outputPath)
                .build()

        generator.setVelocityEngine(velocityEngine)
        generator.run()
    }

}
