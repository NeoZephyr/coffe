grammar Sequence;

stat: 'while' '(' expr ')' stat // directly recursive
    | block // indirectly recursive
    ;

block: '{' stat* '}' ; // match block of statements in curlies