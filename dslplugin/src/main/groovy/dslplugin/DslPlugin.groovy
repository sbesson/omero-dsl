package dslplugin

import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.RuntimeConstants
import org.gradle.api.Plugin
import org.gradle.api.Project

class DslPlugin implements Plugin<Project> {

    /**
     * Sets the group name for the DSLPlugin tasks to reside in.
     * i.e. In a terminal, call `./gradlew tasks` to list tasks in their groups in a terminal
     */
    final def GROUP = 'omero'

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

            // Create task and assign group name
            def dslTask = project.tasks.create(taskName, DslTask)
            dslTask.group = GROUP

            // Assign config after eval
            project.afterEvaluate {
                dslTask.mapFilesPath = operation.mapFilesPath
                dslTask.velocityFile = operation.velocityFile
                dslTask.outputPath = operation.outputPath
                dslTask.velocityEngine = configureVelocity(project)
            }
        })
    }

    static VelocityEngine configureVelocity(Project project) {
        def velocity = new VelocityEngine()

        project.velocity.resource_loader_class.each { String k, String v ->
            velocity.setProperty(k, v)
        }

        velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, project.velocity.resource_loader)
        velocity.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, project.velocity.file_resource_loader_path)
        velocity.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, project.velocity.file_resource_loader_cache)
        velocity.init()
        return velocity
    }

}

