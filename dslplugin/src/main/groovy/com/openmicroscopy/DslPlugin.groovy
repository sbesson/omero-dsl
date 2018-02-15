package com.openmicroscopy

import org.gradle.api.Plugin
import org.gradle.api.Project

class DslPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        // Create velocity extensions
        project.extensions.create('velocity', VelocityExtension)

        // NamedDomainObjectContainer<DslOperationJava> dslOperationContainer = project.container(DslOperationJava.class)
        def dslOperationContainer = project.container(DslOperationJava.class)

        // Add the container instance to our project
        // with the name ome.
        project.extensions.add("dsljava", dslOperationContainer)

        dslOperationContainer.all({ DslOperationJava operation ->
            def env = operation.getName()
            def capitalizedName = env.substring(0, 1).toUpperCase() + env.substring(1)
            def taskName = "process" + capitalizedName
            def dslTask = project.tasks.create(taskName, DslTask)

            project.afterEvaluate {
                dslTask.mapFilesPath = operation.mapFilesPath
                dslTask.velocityFile = operation.velocityFile
                dslTask.outputPath = operation.outputPath
            }
        })
    }
}

