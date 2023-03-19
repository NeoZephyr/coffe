grammar Expr;

prog: stat+;
stat: ID '=' expr NEWLINE # assign
    | expr NEWLINE # printExpr
    | NEWLINE # blank
	;

expr:
	expr op=('+' | '-') expr # AddSub
	| expr op=('*' | '/') expr # MulDiv
	| '(' expr ')' # parens
	| ID # id
	| INT # int
	;

ID: [a-z]+;
INT: [0-9]+;
NEWLINE: '\r'? '\n';
WS: [ \t]+ -> skip; 

MUL: '*';
DIV: '/';
ADD: '+';
SUB: '-';