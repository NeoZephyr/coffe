grammar Dependency;

expr: expr '(' exprList? ')' // func call like f(), f(x), f(1,2)
    | expr '[' expr ']' // array index like a[i], a[i][j]
    | ID '[' expr ']' // a[1], a[b[1]], a[(2*b[1])]
    | '(' expr ')' // (1), (a[1]), (((1))), (2*a[1])
    | INT
    ;

exprList: expr (',' expr)*;
ID: [a-z][a-z0-9]*;
INT: [0-9]+;