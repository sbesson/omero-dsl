package dslplugin

import ome.dsl.velocity.HibernateGenerator
import org.apache.velocity.app.VelocityEngine
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class HibernateTask extends DefaultTask {

    @Input
    String profile = "psql"

    @Input
    VelocityEngine velocityEngine

    @Input
    File velocityFile

    @InputDirectory
    File mapFilesPath

    @OutputFile
    File outputFile

    @TaskAction
    def apply() {
        def generator = new HibernateGenerator.Builder()
                .setProfile(profile)
                .setTemplateFile(velocityFile)
                .setOmeXmlFiles(mapFilesPath)
                .setOutputFile(outputFile)
                .build()

        generator.setVelocityEngine(velocityEngine)
        generator.run()
    }

}
