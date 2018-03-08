package dslplugin

import org.gradle.api.file.FileTree

class DslOperation {
    final String name

    String template

    FileTree omeXmlFiles

    File outputPath

    File outFile

    Closure formatOutput

    DslOperation(String name) {
        this.name = name
    }
}
