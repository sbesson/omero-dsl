package dslplugin

import org.gradle.api.NamedDomainObjectContainer

class Dsl {
    NamedDomainObjectContainer<DslOperationJava> javaConfigs
    NamedDomainObjectContainer<DslOperationHibernate> hibernateConfigs
}
