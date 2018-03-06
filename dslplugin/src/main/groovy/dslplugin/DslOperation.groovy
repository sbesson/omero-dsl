package dslplugin

import org.gradle.api.file.FileTree

class DslOperation {
    final String name

    String templateName

    FileTree omeXmlFiles

    DslOperation(String name) {
        this.name = name
    }
}
