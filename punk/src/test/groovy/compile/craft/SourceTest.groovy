package compile.craft

import compile.craft.frontend.CharUtils
import compile.craft.frontend.Source
import groovy.util.logging.Slf4j
import spock.lang.Specification

@Slf4j
class SourceTest extends Specification {

    def "scan"() {
        given:
        def reader = new BufferedReader(new FileReader("input/file5.txt"))
        def source = new Source(reader)

        log.info("lineno: ${source.lineno}, column: ${source.column}, p: ${source.p}, c: ${source.c}, c: ${(int) source.c}")

        def c = source.c

        while (c != CharUtils.EOF) {
            source.advance()
            c = source.c
            log.info("lineno: ${source.lineno}, column: ${source.column}, p: ${source.p}, c: ${source.c}, c: ${(int) source.c}")
        }
    }
}
