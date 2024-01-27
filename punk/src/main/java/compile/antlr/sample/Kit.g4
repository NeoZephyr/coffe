grammar Kit;

FOR : 'for' ;

ID : [a-zA-Z]+ ; // does NOT match 'for'

FLOAT: DIGIT+ '.' DIGIT* // match 1. 39. 3.14159 etc...
    | '.' DIGIT+ // match .1 .14159
    ;

STRING: '"' (ESC|.)*? '"' ;

LINE_COMMENT : '//' .*? '\r'? '\n' -> skip ; // Match "//" stuff '\n'

COMMENT : '/*' .*? '*/' -> skip ; // Match "/*" stuff "*/"

WS : [ \t\r\n]+ -> skip ; // match 1-or-more whitespace but discard

// helper rule, not a token
// used only by other lexical rules
// could not reference DIGIT from a parser rule
fragment
DIGIT : [0-9] ; // match single digit

// ANTLR itself needs to escape the escape character
// so that’s why we need \\ to specify the backslash character
ESC : '\\' [btnr"\\] ; // \b, \t, \n etc...