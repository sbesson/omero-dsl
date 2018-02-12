package com.openmicroscopy

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project

class DslPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        // NamedDomainObjectContainer<DslOperation> dslOperationContainer = project.container(DslOperation.class)
        def dslOperationContainer = project.container(DslOperation.class)

        // Add the container instance to our project
        // with the name ome.
        project.extensions.add("ome", dslOperationContainer)


        dslOperationContainer.all({ DslOperation dslOperation ->
            def env = dslOperation.getName()
            def capitalizedName = env.substring(0, 1).toUpperCase() + env.substring(1)
            def taskName = "process" + capitalizedName
            def dslTask = project.tasks.create(taskName, DslTask)

            project.afterEvaluate {
                dslTask.mappingsPath = dslOperation.mappingsPath
                println dslTask.mappingsPath
            }
        })
    }
}

