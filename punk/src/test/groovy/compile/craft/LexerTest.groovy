package compile.craft

import spock.lang.Specification

class LexerTest extends Specification {

    def "scan number without exception"() {
        when:
        Lexer lexer = new Lexer(source)
        Token token = lexer.nextToken()

        then:
        kind == token.kind.name()
        lexeme == token.lexeme

        where:
        source                         | kind                     | lexeme

        // BINARY_LITERAL:     '0' [bB] [01] ([01_]* [01])? [lL]?;
        "0b0"                          | "BINARY_LITERAL"         | "0b0"
        "0b1"                          | "BINARY_LITERAL"         | "0b1"
        "0b000"                        | "BINARY_LITERAL"         | "0b000"
        "0b010"                        | "BINARY_LITERAL"         | "0b010"
        "0B0L"                         | "BINARY_LITERAL"         | "0B0L"
        "0b10___10l"                   | "BINARY_LITERAL"         | "0b10___10l"
        "0b000___0L"                   | "BINARY_LITERAL"         | "0b000___0L"
        "0b001___0L"                   | "BINARY_LITERAL"         | "0b001___0L"

        // OCT_LITERAL:        '0' '_'* [0-7] ([0-7_]* [0-7])? [lL]?;

        "0000"                         | "OCT_LITERAL"            | "0000"
        "0__007_2"                     | "OCT_LITERAL"            | "0__007_2"
        "0__7_0L"                      | "OCT_LITERAL"            | "0__7_0L"
        "07_0L"                        | "OCT_LITERAL"            | "07_0L"
        "0000l"                        | "OCT_LITERAL"            | "0000l"
        "0007l"                        | "OCT_LITERAL"            | "0007l"
        "0_00_77__0L"                  | "OCT_LITERAL"            | "0_00_77__0L"

        // HEX_LITERAL:        '0' [xX] [0-9a-fA-F] ([0-9a-fA-F_]* [0-9a-fA-F])? [lL]?;

        "0x0"                          | "HEX_LITERAL"            | "0x0"
        "0x000l"                       | "HEX_LITERAL"            | "0x000l"
        "0x0eeeL"                      | "HEX_LITERAL"            | "0x0eeeL"
        "0x000eee"                     | "HEX_LITERAL"            | "0x000eee"
        "0x1_E2E3e5"                   | "HEX_LITERAL"            | "0x1_E2E3e5"
        "0xe___ee_eL"                  | "HEX_LITERAL"            | "0xe___ee_eL"

        // DECIMAL_LITERAL:    ('0' | [1-9] (Digits? | '_'+ Digits)) [lL]?;

        "0L"                           | "DECIMAL_LITERAL"        | "0L"
        "0"                            | "DECIMAL_LITERAL"        | "0"
        "9l"                           | "DECIMAL_LITERAL"        | "9l"
        "99___9L"                      | "DECIMAL_LITERAL"        | "99___9L"
        "9___99___9L"                  | "DECIMAL_LITERAL"        | "9___99___9L"
        "9999"                         | "DECIMAL_LITERAL"        | "9999"
        "1_000_000"                    | "DECIMAL_LITERAL"        | "1_000_000"

        // HEX_FLOAT_LITERAL:  '0' [xX] (HexDigits '.'? | HexDigits? '.' HexDigits) [pP] [+-]? Digits [fFdD]?;

        "0x.fp012"                     | "HEX_FLOAT_LITERAL"      | "0x.fp012"
        "0x.f__f_fp-0012d"             | "HEX_FLOAT_LITERAL"      | "0x.f__f_fp-0012d"
        "0xf_ffP12d"                   | "HEX_FLOAT_LITERAL"      | "0xf_ffP12d"
        "0xf_f.P-12d"                  | "HEX_FLOAT_LITERAL"      | "0xf_f.P-12d"
        "0xf_f.f_fP+12"                | "HEX_FLOAT_LITERAL"      | "0xf_f.f_fP+12"
        "0x0.0000000fp-11"             | "HEX_FLOAT_LITERAL"      | "0x0.0000000fp-11"
        "0xf_ff.P12d"                  | "HEX_FLOAT_LITERAL"      | "0xf_ff.P12d"
        "0X0P0f"                       | "HEX_FLOAT_LITERAL"      | "0X0P0f"
        "0X0P0"                        | "HEX_FLOAT_LITERAL"      | "0X0P0"
        "0X0_0__123P0f"                | "HEX_FLOAT_LITERAL"      | "0X0_0__123P0f"
        "0XeP0f"                       | "HEX_FLOAT_LITERAL"      | "0XeP0f"
        "0X000.P0f"                    | "HEX_FLOAT_LITERAL"      | "0X000.P0f"
        "0X00e.P0f"                    | "HEX_FLOAT_LITERAL"      | "0X00e.P0f"
        "0X0e__0.0P0f"                 | "HEX_FLOAT_LITERAL"      | "0X0e__0.0P0f"
        "0X0e__0.0__0P0f"              | "HEX_FLOAT_LITERAL"      | "0X0e__0.0__0P0f"
        "0X0e__0.0__e0P-0__0f"         | "HEX_FLOAT_LITERAL"      | "0X0e__0.0__e0P-0__0f"
        "0X0e__0.0__e0P+0_1__0f"       | "HEX_FLOAT_LITERAL"      | "0X0e__0.0__e0P+0_1__0f"
        "0X0.0__e0P+0_1__0f"           | "HEX_FLOAT_LITERAL"      | "0X0.0__e0P+0_1__0f"
        "0X0.00P0f"                    | "HEX_FLOAT_LITERAL"      | "0X0.00P0f"
        "0X0.0eP0f"                    | "HEX_FLOAT_LITERAL"      | "0X0.0eP0f"
        "0X0.e__00P0f"                 | "HEX_FLOAT_LITERAL"      | "0X0.e__00P0f"
        "0X0.e__00__0P0f"              | "HEX_FLOAT_LITERAL"      | "0X0.e__00__0P0f"
        "0X0.e__00__e0P-0__0f"         | "HEX_FLOAT_LITERAL"      | "0X0.e__00__e0P-0__0f"
        "0X0e.0__00__e0P+0_1__0f"      | "HEX_FLOAT_LITERAL"      | "0X0e.0__00__e0P+0_1__0f"
        "0X.0__00__e0P-0_1__0F"        | "HEX_FLOAT_LITERAL"      | "0X.0__00__e0P-0_1__0F"
        "0X.0__00__e0P-0_1__0"         | "HEX_FLOAT_LITERAL"      | "0X.0__00__e0P-0_1__0"

        // FLOAT_LITERAL:      (Digits '.' Digits? | '.' Digits) ExponentPart? [fFdD]?;
        // FLOAT_LITERAL:      Digits (ExponentPart [fFdD]? | [fFdD]);
        // ExponentPart: [eE] [+-]? Digits

        "0f"                           | "FLOAT_LITERAL"          | "0f"
        "00f"                          | "FLOAT_LITERAL"          | "00f"
        "0__0_0f"                      | "FLOAT_LITERAL"          | "0__0_0f"
        "0001f"                        | "FLOAT_LITERAL"          | "0001f"
        "0e0f"                         | "FLOAT_LITERAL"          | "0e0f"
        "0e0"                          | "FLOAT_LITERAL"          | "0e0"
        "1e1"                          | "FLOAT_LITERAL"          | "1e1"
        "0_0e-0_0f"                    | "FLOAT_LITERAL"          | "0_0e-0_0f"
        "0_0e0_120f"                   | "FLOAT_LITERAL"          | "0_0e0_120f"
        "0001e0009"                    | "FLOAT_LITERAL"          | "0001e0009"
        "000012345e1"                  | "FLOAT_LITERAL"          | "000012345e1"
        "0_00___9900d"                 | "FLOAT_LITERAL"          | "0_00___9900d"
        ".0"                           | "FLOAT_LITERAL"          | ".0"
        ".0e0"                         | "FLOAT_LITERAL"          | ".0e0"
        ".0_000"                       | "FLOAT_LITERAL"          | ".0_000"
        ".0___0990"                    | "FLOAT_LITERAL"          | ".0___0990"
        ".000e0__0__0"                 | "FLOAT_LITERAL"          | ".000e0__0__0"
        ".000e0__0__0f"                | "FLOAT_LITERAL"          | ".000e0__0__0f"
        ".000e-0__9__0f"               | "FLOAT_LITERAL"          | ".000e-0__9__0f"
        ".9e-0__1_0f"                  | "FLOAT_LITERAL"          | ".9e-0__1_0f"
        "0__00."                       | "FLOAT_LITERAL"          | "0__00."
        "0__090."                      | "FLOAT_LITERAL"          | "0__090."
        "99__9."                       | "FLOAT_LITERAL"          | "99__9."
        "000.000"                      | "FLOAT_LITERAL"          | "000.000"
        "0__10.090"                    | "FLOAT_LITERAL"          | "0__10.090"
        "000__1_0.090"                 | "FLOAT_LITERAL"          | "000__1_0.090"
        "000__1_0.090e12"              | "FLOAT_LITERAL"          | "000__1_0.090e12"
        "0__10.090e1__00"              | "FLOAT_LITERAL"          | "0__10.090e1__00"
        "3.1415926"                    | "FLOAT_LITERAL"          | "3.1415926"
        "0.030"                        | "FLOAT_LITERAL"          | "0.030"
        "0.6"                          | "FLOAT_LITERAL"          | "0.6"
        ".0f"                          | "FLOAT_LITERAL"          | ".0f"
        ".0_000f"                      | "FLOAT_LITERAL"          | ".0_000f"
        ".0___0990f"                   | "FLOAT_LITERAL"          | ".0___0990f"
        ".000e0__0__0f"                | "FLOAT_LITERAL"          | ".000e0__0__0f"
        "0__00.f"                      | "FLOAT_LITERAL"          | "0__00.f"
        "0__090.f"                     | "FLOAT_LITERAL"          | "0__090.f"
        "99__9.f"                      | "FLOAT_LITERAL"          | "99__9.f"
        "000.000f"                     | "FLOAT_LITERAL"          | "000.000f"
        "0__10.090f"                   | "FLOAT_LITERAL"          | "0__10.090f"
        "000__1_0.090f"                | "FLOAT_LITERAL"          | "000__1_0.090f"
        "000__1_0.090e12f"             | "FLOAT_LITERAL"          | "000__1_0.090e12f"
        "0__10.090e1__00f"             | "FLOAT_LITERAL"          | "0__10.090e1__00f"
        "3.1415926f"                   | "FLOAT_LITERAL"          | "3.1415926f"
        "0.030f"                       | "FLOAT_LITERAL"          | "0.030f"
        "0.6f"                         | "FLOAT_LITERAL"          | "0.6f"
    }
}
