package jubi.config

import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils
import spock.lang.Specification
import spock.lang.Unroll

import static jubi.config.JubiConf.ENGINE_OPEN_RETRY_WAIT
import static jubi.config.JubiConf.SERVER_PRINCIPAL

@Slf4j
class JubiConfTest extends Specification {

    def setup() {
        log.info(">>>>> setup")
    }

    def cleanup() {
        log.info(">>>>> cleanup")
    }

    /**
     * 全局前置方法
     */
    def setupSpec() {
        log.info("##### setupSpec")
    }

    /**
     * 全局后置方法
     */
    def cleanupSpec() {
        log.info("##### cleanupSpec")
    }

    // given, when, then, where

    @Unroll
    def "#a to power of #b should be #c"() {
        expect:
        Math.pow(a, b) == c
        where:
        a | b | c
        1 | 1 | 1
        2 | 2 | 4
        3 | 2 | 9
    }

    def "set and unset"() {
        given:
        def conf = new JubiConf()
        def key = "jubi.conf.foo"

        when:
        conf.set(key, "bar")

        then:
        conf.get(key) == "bar"

        when:
        conf.unset(key)

        then:
        StringUtils.isBlank(conf.get(key))
    }

    def "jubi conf defaults"() {
        given:
        def conf = new JubiConf()

        expect:
        conf.get(SERVER_PRINCIPAL) == null
        conf.get(ENGINE_OPEN_RETRY_WAIT) == 10
    }

    def "jubi conf no sys defaults"() {
        given:
        def key = "jubi.conf.foo"
        System.setProperty(key, "bar")

        expect:
        new JubiConf().get(key) == null
        new JubiConf().loadSysDefaults().get(key) == "bar"
        new JubiConf().loadSysDefaults().getAll().containsKey(key)
    }

    def "jubi conf default config file"() {
        given:
        def conf = new JubiConf().loadFileDefaults()

        expect:
        conf.get("spark.jubi.bar") == "bar"
        conf.get("spark.user.test") == "test1"
        conf.get("___user0___.spark.user.test") == "test2"
        conf.get("jubi") == "tobi"
        conf.get("jubi.foo") == null
    }

    def "set and unset conf"() {
        given:
        def conf = new JubiConf()

        when:
        def key = "jubi.conf.foo"
        conf.set(key, "bar")
        conf.set(ENGINE_OPEN_RETRY_WAIT, 32L)

        then:
        conf.get(key) == "bar"
        conf.get(ENGINE_OPEN_RETRY_WAIT) == 32L

        when:
        conf.setIfMissing(ENGINE_OPEN_RETRY_WAIT, 64L)
        conf.setIfMissing(SERVER_PRINCIPAL, "bar")

        then:
        conf.get(ENGINE_OPEN_RETRY_WAIT) == 32L
        conf.get(SERVER_PRINCIPAL) == "bar"

        when:
        conf.unset(key)

        then:
        conf.get(key) == null
    }
}
