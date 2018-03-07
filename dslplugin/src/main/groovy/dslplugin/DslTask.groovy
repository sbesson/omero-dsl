package dslplugin

import ome.dsl.SemanticType
import ome.dsl.velocity.JavaGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.*

class DslTask extends DefaultTask {

    @Input
    String profile = "psql"

    @InputFile
    File template

    @InputFiles
    FileTree omeXmlFiles

    @OutputDirectory
    File outputPath

    @Internal
    Closure formatOutput

    @Internal
    Properties velocityProps

    @TaskAction
    def apply() {
        // Create the code generator with the following options
        def builder = new JavaGenerator.Builder()
        builder.omeXmlFiles = omeXmlFiles as List
        builder.fileFormatter = new JavaGenerator.FileNameFormatter() {
            @Override
            String format(SemanticType t) {
                return formatOutput(t)
            }
        }
        builder.profile = profile
        builder.template = template
        builder.outputDir = outputPath
        builder.velocityProperties = velocityProps
        builder.build().run()
    }

}
