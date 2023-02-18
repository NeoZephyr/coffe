package jubi.config

import spock.lang.Specification

class ConfigBuilderTest extends Specification {

    def "int config"() {
        given:
        def entry = new ConfigBuilder("jubi.int.conf")
                .intConf()
                .createWithDefault(10)
        expect:
        entry.key() == "jubi.int.conf"
        entry.defaultValue() == 10
    }

    def "double config"() {
        given:
        def entry = new ConfigBuilder("jubi.double.conf")
                .doubleConf()
                .createWithDefault(3.14)
        expect:
        entry.key() == "jubi.double.conf"
        entry.defaultValue() == 3.14
    }

    def "boolean config"() {
        given:
        def entry = new ConfigBuilder("jubi.boolean.conf")
                .booleanConf()
                .createWithDefault(false)
        expect:
        entry.key() == "jubi.boolean.conf"
        !entry.defaultValue()
    }
}