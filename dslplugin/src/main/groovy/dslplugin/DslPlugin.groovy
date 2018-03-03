package dslplugin

import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.RuntimeConstants
import org.gradle.api.Plugin
import org.gradle.api.Project

@SuppressWarnings("GrMethodMayBeStatic")
class DslPlugin implements Plugin<Project> {

    /**
     * Sets the group name for the DSLPlugin tasks to reside in.
     * i.e. In a terminal, call `./gradlew tasks` to list tasks in their groups in a terminal
     */
    final def GROUP = 'omero'

    @Override
    void apply(Project project) {
        setupDsl(project)
        configJavaOps(project)
    }

    void setupDsl(final Project project) {
        // Create velocity extensions
        project.extensions.create('velocity', VelocityExtension)

        // Create dsl extension
        project.extensions.create('dsl', DslExtension)

        // Named object container for java operations and
        // make them a child of dsl extension
        def javaContainer = project.container(DslOperationJava)
        project.dsl.extensions.add('java', javaContainer)

        // Do the same for hibernate operations
        def hibernateContainer = project.container(DslOperationHibernate)
        project.dsl.extensions.add('hibernate', hibernateContainer)
    }

    void configJavaOps(final Project project) {
        project.dsl.java.all({ operation ->
            def taskName = "process" + capitalizeName(operation.getName())

            // Create task and assign group name
            def dslTask = project.task(taskName, type: DslTask)
            dslTask.group = GROUP
            dslTask.description = 'parses ome.xml files and compiles velocity template'

            // Assign config after eval
            project.afterEvaluate {
                dslTask.mapFilesPath = operation.mapFilesPath
                dslTask.velocityFile = operation.velocityFile
                dslTask.outputPath = operation.outputPath
                dslTask.velocityEngine = configureVelocity(project)
            }

            // Ensure the dsltask runs before compileJava
            project.tasks.getByName("compileJava").dependsOn(taskName)
        })
    }

    /* void configHibernateOps(NamedDomainObjectContainer<DslOperationHibernate> container, Project project) {

         // Add the container instance to our project
         // with the name ome.
         project.extensions.add("dslhibernate", dslOperationContainer)

         dslOperationContainer.all({ operation ->
             def taskName = "process" + capitalizeName(operation.getName())

             // Create task and assign group name
             def dslTask = project.task(taskName, type: DslTask)
             dslTask.group = GROUP
             dslTask.description = 'parses ome.xml files and compiles velocity template'

             // Assign config after eval
             project.afterEvaluate {
                 dslTask.mapFilesPath = operation.mapFilesPath
                 dslTask.velocityFile = operation.velocityFile
                 dslTask.outputPath = operation.outputPath
                 dslTask.velocityEngine = configureVelocity(project)
             }

             // Ensure the dsltask runs before compileJava
             project.tasks.getByName("compileJava").dependsOn(taskName)
         })
     }*/

    static String capitalizeName(name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1)
    }

    static VelocityEngine configureVelocity(Project project) {
        def velocity = new VelocityEngine()

        project.velocity.resource_loader_class.each { k, v ->
            velocity.setProperty(k as String, v as String)
        }

        velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, project.velocity.resource_loader)
        velocity.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, project.velocity.file_resource_loader_path)
        velocity.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, project.velocity.file_resource_loader_cache)
        velocity.init()
        return velocity
    }

}

