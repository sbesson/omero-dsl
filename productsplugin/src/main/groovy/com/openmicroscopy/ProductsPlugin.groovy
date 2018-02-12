package com.openmicroscopy

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project

class ProductsPlugin implements Plugin<Project> {
    void apply(final Project project) {
        // Create NamedDomainObjectContainer instance for
        // a collection of Product objects
        NamedDomainObjectContainer<Product> productContainer =
                project.container(Product)

        // Add the container instance to our project
        // with the name products.
        project.extensions.add('products', productContainer)

        // Simple task to show the Product objects
        // that are created by Gradle using
        // the DSL syntax in our build file.
        project.task('reportProducts') << {
            def products = project.extensions.getByName('products')

            products.all {
                // A Product instance is the delegate
                // for this closure.
                println "$name costs $price"
            }
        }
    }
}

class Product {
    // We need a name property
    // so the object can be created
    // by Gradle using a DSL.
    String name

    BigDecimal price

    Product(final String name) {
        this.name = name
    }
}