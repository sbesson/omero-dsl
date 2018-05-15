#OMERO DSL Plugin

The OMERO DSL plugin for Gradle provides a plugin named `dsl`.
This plugin manages the reading of `*.ome.xml` mappings and compilation of `.vm` velocity templates.

### Build

To build the plugin run:
```shell
$./gradlew build
```

To publish the plugin run:
```shell
$./gradlew publishToMavenLocal
```

### Usage

Include the following to the top of your _build.gradle_ file:

```gradle
buildscript {
    repositories {
        jcenter()
        mavenLocal()
    }

    dependencies {
        classpath 'org.openmicroscopy:dslplugin:1.0'
    }
} 

apply plugin: 'org.openmicroscopy.dslplugin'
```

### Configuring plugin

The omero-dsl plugin introduces the `dsl {}` block to define configuration options for
_Velocity_ and _SemanticType_ processing.  

In order to configure the [VelocityEngine](http://velocity.apache.org), add the `velocity` 
extension to your _build.gradle_ file, for example:

```gradle
dsl {
    velocity {
        resource_loader = 'file'
        resource_loader_class = ['file.resource.loader.class': 'org.apache.velocity.runtime.resource.loader.FileResourceLoader']
        file_resource_loader_path = 'src/main/resources/templates'
        file_resource_loader_cache = false
    }
}
```

To configure and add a compilation, add a block, named to whatever suits your need, to your _build.gradle_ file, 
for example:

```gradle
dsl {
    javaModels {
        template = "object.vm"
        outputPath = project.file('src/generated/java')
        omeXmlFiles = project.fileTree(dir: "src/main/resources/mappings", include: '**/*.ome.xml')
        formatOutput = { st ->
            "${st.getPackage()}/${st.getShortname()}.java"
        }
    }
}
```
This will add a task `processJavaModels` under the group `omero` in gradle _(to print a list of available gradle
tasks run `./gradlew tasks` in a terminal)_. If you are using Intellij, refresh the _Gradle Toolbar_ and the
task will appear in the list once the IDE completes its work.

Multiple configurations can be added. For example:

```gradle
dsl {
    javaModels {
        velocityFile = 'object.vm'
        mapFilesPath = file('src/main/resources/mappings')
        outputPath = file('src/main/java-generated')
    }
    
    hibernate {
        template = "cfg.vm"
        outFile = project.file('src/generated/resources/hibernate.cfg.xml')
        omeXmlFiles = project.fileTree(dir: "src/main/resources/mappings", include: '**/*.ome.xml')
    }
}
```

### Gradle Task

Additional configurations to the `dsl` extension add a new task 

| Type      | Description                                       |
| --------- | ------------------------------------------------- |
| DslTask   | Generates Java source from ome.xml and .vm files  |

If, like in the examples above, you create configurations `javaModels` and `sqlModels`, these tasks will run
before `compileJava`.

| Task name   | Depends On        |
| ----------- | ----------------- |
| compileJava | processJavaModels |
| compileJava | processSqlModels  |
