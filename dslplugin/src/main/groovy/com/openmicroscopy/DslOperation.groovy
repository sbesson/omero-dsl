package com.openmicroscopy

class DslOperation {

    final String name
    File mappingsPath
    File velocityTemplateFile

    File outputPath
    String outputFileExtension

    DslOperation(String name) {
        this.name = name
    }
}
