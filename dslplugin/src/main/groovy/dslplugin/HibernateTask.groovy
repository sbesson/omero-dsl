package dslplugin

import ome.dsl.velocity.HibernateGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class HibernateTask extends DefaultTask {

    @Input
    String profile = "psql"

    @Input
    String templateName

    @OutputFile
    File outFile

    @InputFiles
    FileTree omeXmlFiles

    Properties velocityProps

    @TaskAction
    def apply() {
        def generator = new HibernateGenerator.Builder()
                .setProfile(profile)
                .setOmeXmlFiles(omeXmlFiles as List)
                .setTemplate(templateName)
                .setOutput(outFile)
                .setVelocityProperties(velocityProps)
                .build()

        generator.run()
    }

}
