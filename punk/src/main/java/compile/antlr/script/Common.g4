lexer grammar Common;

// Keywords


VOID:               'void';
BOOLEAN:            'boolean';
BYTE:               'byte';
CHAR:               'char';
SHORT:              'short';
INT:                'int';
LONG:               'long';
DOUBLE:             'double';
FLOAT:              'float';
STRING:             'string';
CONST:              'const';
FINAL:              'final';

TRANSIENT:          'transient';
SYNCHRONIZED:       'synchronized';
VOLATILE:           'volatile';
STATIC:             'static';
NATIVE:             'native';
DEFAULT:            'default';
ABSTRACT:           'abstract';

THIS:               'this';
SUPER:              'super';
STRICTFP:           'strictfp';
NEW:                'new';
ASSERT:             'assert';
INSTANCEOF:         'instanceof';
THROW:              'throw';
THROWS:             'throws';
FUNCTION:           'function';
INTERFACE:          'interface';
CLASS:              'class';
ENUM:               'enum';
EXTENDS:            'extends';
IMPLEMENTS:         'implements';
PACKAGE:            'package';
PRIVATE:            'private';
PROTECTED:          'protected';
PUBLIC:             'public';
IMPORT:             'import';

IF:                 'if';
ELSE:               'else';
FOR:                'for';
SWITCH:             'switch';
CASE:               'case';
BREAK:              'break';
CONTINUE:           'continue';
DO:                 'do';
WHILE:              'while';
TRY:                'try';
CATCH:              'catch';
FINALLY:            'finally';
RETURN:             'return';
GOTO:               'goto';

// Literals

BOOL_LITERAL:       'true'
            |       'false'
            ;

// DECIMAL_LITERAL:    ('0' | [1-9] (Digits? | '_'+ Digits)) [lL]?;

DECIMAL_LITERAL:    ('0' | [1-9] ([0-9_]* [0-9])?) [lL]?;
HEX_LITERAL:        '0' [xX] [0-9a-fA-F] ([0-9a-fA-F_]* [0-9a-fA-F])? [lL]?;
OCT_LITERAL:        '0' '_'* [0-7] ([0-7_]* [0-7])? [lL]?;
BINARY_LITERAL:     '0' [bB] [01] ([01_]* [01])? [lL]?;

FLOAT_LITERAL:      (Digits '.' Digits? | '.' Digits) ExponentPart? [fFdD]?
             |       Digits (ExponentPart [fFdD]? | [fFdD])
             ;

HEX_FLOAT_LITERAL:  '0' [xX] (HexDigits '.'? | HexDigits? '.' HexDigits) [pP] [+-]? Digits [fFdD]?;

CHAR_LITERAL:       '\'' (~['\\\r\n] | EscapeSequence) '\'';

STRING_LITERAL:     '"' (~["\\\r\n] | EscapeSequence)* '"';

NULL_LITERAL:       'null';

// Identifiers

IDENTIFIER:         Letter LetterOrDigit*;

// Separators

LPAREN:             '(';
RPAREN:             ')';
LBRACE:             '{';
RBRACE:             '}';
LBRACK:             '[';
RBRACK:             ']';
SEMI:               ';';
COMMA:              ',';
DOT:                '.';

// Operators

ASSIGN:             '=';
GT:                 '>';
LT:                 '<';
BANG:               '!';
TILDE:              '~';
QUESTION:           '?';
COLON:              ':';
EQUAL:              '==';
LE:                 '<=';
GE:                 '>=';
NOTEQUAL:           '!=';
AND:                '&&';
OR:                 '||';
INC:                '++';
DEC:                '--';
ADD:                '+';
SUB:                '-';
MUL:                '*';
DIV:                '/';
BITAND:             '&';
BITOR:              '|';
CARET:              '^';
MOD:                '%';

ADD_ASSIGN:         '+=';
SUB_ASSIGN:         '-=';
MUL_ASSIGN:         '*=';
DIV_ASSIGN:         '/=';
AND_ASSIGN:         '&=';
OR_ASSIGN:          '|=';
XOR_ASSIGN:         '^=';
MOD_ASSIGN:         '%=';
LSHIFT_ASSIGN:      '<<=';
RSHIFT_ASSIGN:      '>>=';
URSHIFT_ASSIGN:     '>>>=';

// Java 8 tokens

ARROW:              '->';
COLONCOLON:         '::';

// Additional symbols not defined in the lexical specification

AT:                 '@';
ELLIPSIS:           '...';

// Whitespace and comments

WS:                 [ \t\r\n\u000C]+ -> channel(HIDDEN);
COMMENT:            '/*' .*? '*/'    -> channel(HIDDEN);
LINE_COMMENT:       '//' ~[\r\n]*    -> channel(HIDDEN);

// Fragment rules

fragment Digits
    : [0-9] ([0-9_]* [0-9])?
    ;

fragment HexDigit
    : [0-9a-fA-F]
    ;

fragment HexDigits
    : HexDigit ((HexDigit | '_')* HexDigit)?
    ;

fragment ExponentPart
    : [eE] [+-]? Digits
    ;

fragment EscapeSequence
    : '\\' [btnfr"'\\]
    | '\\' ([0-3]? [0-7])? [0-7]
    | '\\' 'u'+ HexDigit HexDigit HexDigit HexDigit
    ;

fragment LetterOrDigit
    : Letter
    | [0-9]
    ;

fragment Letter
    : [a-zA-Z$_] // these are the "java letters" below 0x7F
    | ~[\u0000-\u007F\uD800-\uDBFF] // covers all characters above 0x7F which are not a surrogate
    | [\uD800-\uDBFF] [\uDC00-\uDFFF] // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
    ;
