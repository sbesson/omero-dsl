package dslplugin

import ome.dsl.velocity.HibernateGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.*

class HibernateTask extends DefaultTask {

    @Input
    String profile = "psql"

    @InputFile
    File template

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
                .setTemplate(template)
                .setOutput(outFile)
                .setVelocityProperties(velocityProps)
                .build()

        generator.run()
    }

}
