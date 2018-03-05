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

    final def velocity = new VelocityEngine()

    @Override
    void apply(Project project) {
        setupDsl(project)
        configureVelocity(project)
        configJavaTasks(project)
        // configHibernateTasks(project)
    }

    void setupDsl(final Project project) {
        // Create the dsl extension
        project.extensions.create('dsl', Dsl)

        // Create velocity inner extension for dsl
        project.dsl.extensions.create('velocity', VelocityExtension)

        // Add NamedDomainObjectContainer for java configs
        project.dsl.extensions.add("java", project.container(DslOperationJava))

        // Add NamedDomainObjectContainer for hibernate configs
        project.dsl.extensions.add("hibernate", project.container(DslOperationHibernate))
    }

    void configJavaTasks(final Project project) {
        project.dsl.java.all {
            // Create an object instance to hold our delegate (this)
            // type to pass into inner closures
            def javaInfo = delegate
            def taskName = "process" + name.capitalize()

            // Create task and assign group name
            def dslTask = project.task(taskName, type: JavaTask) {
                group = GROUP
                description = 'parses ome.xml files and compiles velocity template'
            }

            // Assign property values to task inputs
            project.afterEvaluate {
                dslTask.omeXmlFiles = javaInfo.omeXmlFiles
                dslTask.velocityFile = javaInfo.velocityFile
                dslTask.outputPath = javaInfo.outputPath
                dslTask.velocityEngine = velocity
            }

            // Ensure the dsltask runs before compileJava
            project.tasks.getByName("compileJava").dependsOn(taskName)
        }
    }

    void configHibernateTasks(final Project project) {
        project.dsl.hibernate.all {
            // Create an object instance to hold our delegate (this)
            // type to pass into inner closures
            def javaInfo = delegate
            def taskName = "process" + name.capitalize()

            // Create task and assign group name
            /*def dslTask = project.task(taskName, type: JavaTask) {
                group = GROUP
                description = 'parses ome.xml files and compiles velocity template'
            }

            // Assign property values to task inputs
            project.afterEvaluate {
                dslTask.omeXmlFiles = javaInfo.omeXmlFiles
                dslTask.velocityFile = javaInfo.velocityFile
                dslTask.outputPath = javaInfo.outputPath
                dslTask.velocityEngine = velocity
            }*/

            // Ensure the dsltask runs before compileJava
            project.tasks.getByName("compileJava").dependsOn(taskName)
        }
    }

    void configureVelocity(Project project) {
        project.afterEvaluate {
            velocity.setProperty(RuntimeConstants.RESOURCE_LOADER,
                    project.dsl.velocity.resource_loader)

            velocity.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
                    project.dsl.velocity.file_resource_loader_path)

            velocity.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE,
                    project.dsl.velocity.file_resource_loader_cache)

            project.dsl.velocity.resource_loader_class.each { k, v ->
                velocity.setProperty(k as String, v as String)
            }

            velocity.init()
        }
    }

}

