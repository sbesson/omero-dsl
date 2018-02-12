package com.openmicroscopy

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project

class DslPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        NamedDomainObjectContainer<DslOperation> dslOperationContainer = project.container(DslOperation.class)
        project.extensions.add("ome", dslOperationContainer)

        // Create a task for each dsl operation
        dslOperationContainer.each { DslOperation dslOperation ->
            String env = dslOperation.getName()
            String capitalizedName = env.substring(0, 1).toUpperCase() + env.substring(1)
            String taskName = "process" + capitalizedName

            project.tasks.create(name: taskName, type: DslTask) {
                mapFileDir = dslOperation.mappingsPath
                velocityTemplateFile = dslOperation.velocityTemplateFile
                outputDir = dslOperation.outputPath
                outputFileExtension = dslOperation.outputFileExtension
            }
        }

        // DslExtension extension = project.getExtensions().create("dsl", DslExtension.class);
    }
}

