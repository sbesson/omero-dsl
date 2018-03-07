package dslplugin

import ome.dsl.SemanticType
import ome.dsl.velocity.MultiFileGenerator
import ome.dsl.velocity.SingleFileGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class DslTask extends DefaultTask {

    @Input
    String profile = "psql"

    @InputFile
    File template

    @InputFiles
    FileTree omeXmlFiles

    @OutputDirectory
    @Optional
    File outputPath

    @OutputFile
    @Optional
    File outFile

    @Internal
    Closure formatOutput

    @Internal
    Properties velocityProps

    @TaskAction
    def apply() {
        def builder = outputPath != null ? createMultiFileGen() : createSingleFileGen()
        builder.omeXmlFiles = omeXmlFiles as List
        builder.profile = profile
        builder.template = template
        builder.velocityProperties = velocityProps
        builder.build().run()
    }

    MultiFileGenerator.Builder createMultiFileGen() {
        def mb = new MultiFileGenerator.Builder()
        mb.outputDir = outputPath
        mb.fileFormatter = new MultiFileGenerator.FileNameFormatter() {
            @Override
            String format(SemanticType t) {
                return formatOutput(t)
            }
        }
        return mb
    }

    SingleFileGenerator.Builder createSingleFileGen() {
        def b = new SingleFileGenerator.Builder()
        b.outFile = outFile
        return b
    }


}
