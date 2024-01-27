lexer grammar XML;

OPEN : '<' -> pushMode(INSIDE) ;
COMMENT : '<!--' .*? '-->' -> skip ;
EntityRef : '&' [a-z]+ ';' ;
TEXT : ~('<'|'&')+ ;

mode INSIDE;

CLOSE : '>' -> popMode ;
SLASH_CLOSE : '/>' -> popMode ;
EQUALS : '=' ;
STRING : '"' .*? '"' ;
SlashName : '/' Name ;
Name : ALPHA (ALPHA|DIGIT)* ;
S : [ \t\r\n] -> skip ;

// helper rule used only by other lexical rules, not a token
fragment
ALPHA : [a-zA-Z] ;

fragment
DIGIT : [0-9] ;