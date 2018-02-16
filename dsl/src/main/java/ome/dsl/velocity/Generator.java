package ome.dsl.velocity;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class Generator implements Runnable {

    final Logger logger = LoggerFactory.getLogger(JavaGenerator.class);

    VelocityEngine velocityEngine;

    Generator() {
        velocityEngine = new VelocityEngine();
        /*
         * Configuration documentation:
         * http://velocity.apache.org/engine/1.7/developer-guide.html#configuration-examples
         */
        velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_INSTANCE, logger);
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
        velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, "true");
        velocityEngine.setProperty("file.resource.loader.class",
                FileResourceLoader.class.getName());
        velocityEngine.init();
    }
}
