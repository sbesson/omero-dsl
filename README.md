#OMERO DSL Plugin

The OMERO DSL plugin for Gradle provides a plugin named `velocity` and `dsljava`.
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
        classpath 'com.openmicroscopy:dslplugin:1.0'
    }
} 

apply plugin: 'com.openmicroscopy.dslplugin'
```

### Configuring plugin

In order to configure the [VelocityEngine](http://velocity.apache.org), add the `velocity` 
extension to your _build.gradle_ file, for example:

```gradle
velocity {
    resource_loader = 'file'
    resource_loader_class = ['file.resource.loader.class': 'org.apache.velocity.runtime.resource.loader.FileResourceLoader']
    file_resource_loader_path = 'src/main/resources/templates'
    file_resource_loader_cache = false
}
```

To configure and add a compilation, add the 'dsljava' extension to your _build.gradle_ file, for example:

```gradle
dsljava {
    javaModels {
        velocityFile = 'object.vm'
        mapFilesPath = file('src/main/resources/mappings')
        outputPath = file('src/main/java-generated')
    }
}
```
This will add a task `javaModels` under the group `omero` in gradle. To print a list of available gradle
tasks run `./gradlew tasks` in a terminal. If you are using Intellj, refresh the _Gradle Toolbar_ and the
task will appear in the list once the IDE completes its work.

For additional compilations, the above `dslplugin` supports multiple configuration objects. For example:

```gradle
dsljava {
    javaModels {
        velocityFile = 'object.vm'
        mapFilesPath = file('src/main/resources/mappings')
        outputPath = file('src/main/java-generated')
    }
    
    sqlModels {
        velocityFile = 'sql.vm'
        mapFilesPath = file('src/main/resources/mappings')
        outputPath = file('src/main/sql-generated')
    }
}
```

### Gradle Task

Additional configurations to the `dsljava` extension add a new task 

| Type      | Description                                       |
| --------- | ------------------------------------------------- |
| DslTask   | Generates Java source from ome.xml and .vm files  |

If, like in the examples above, you create configurations `javaModels` and `sqlModels`, these tasks will run
before `compileJava`.

| Task name   | Depends On        |
| ----------- | ----------------- |
| compileJava | processJavaModels |
| compileJava | processSqlModels  |