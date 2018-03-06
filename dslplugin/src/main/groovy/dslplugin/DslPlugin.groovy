package dslplugin

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
        setupDsl(project)
        configJavaTasks(project)
        configHibernateTasks(project)
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
        project.dsl.java.all { info ->
            // Create an object instance to hold our delegate (this)
            // type to pass into inner closures
            def taskName = "process${info.name.capitalize()}"

            // Create task and assign group name
            def jtask = project.task(taskName, type: JavaTask) {
                group = GROUP
                description = 'parses ome.xml files and compiles velocity template'
            }

            // Assign property values to task inputs
            project.afterEvaluate {
                jtask.velocityProps = configureVelocity(project)
                jtask.omeXmlFiles = info.omeXmlFiles
                jtask.templateName = info.templateName
                jtask.outputPath = info.outputPath
            }

            // Ensure the dsltask runs before compileJava
            project.tasks.getByName("compileJava").dependsOn(taskName)
        }
    }

    void configHibernateTasks(final Project project) {
        project.dsl.hibernate.all {
            // Create an object instance to hold our delegate (this)
            // type to pass into inner closures
            def info = delegate
            def taskName = "process${name.capitalize()}"

            // Create task and assign group name
            def htask = project.task(taskName, type: HibernateTask) {
                group = GROUP
                description = 'parses ome.xml files and compiles velocity template'
            }

            // Assign property values to task inputs
            project.afterEvaluate {
                htask.velocityProps = configureVelocity(project)
                htask.omeXmlFiles = info.omeXmlFiles
                htask.templateName = info.templateName
                htask.outFile = info.outFile
            }

            // Ensure the dsltask runs before compileJava
            project.tasks.getByName("compileJava").dependsOn(taskName)
        }
    }

    static Properties configureVelocity(Project project) {
        final def props = new Properties()
        final def extension = project.dsl.velocity

        props.setProperty(RuntimeConstants.RUNTIME_LOG_NAME,
                project.getLogger().getClass().getName())

        if (extension.hasProperty('resource_loader')) {
            props.setProperty(RuntimeConstants.RESOURCE_LOADER,
                    extension.resource_loader as String)
        }

        if (extension.hasProperty('file_resource_loader_path')) {
            props.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
                    extension.file_resource_loader_path as String)
        }

        if (extension.hasProperty('file_resource_loader_cache')) {
            props.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE,
                    extension.file_resource_loader_cache as String)
        }

        extension.resource_loader_class.each { k, v ->
            props.setProperty(k as String, v as String)
        }

        return props
    }

}

