grammar Sequence;

file : (row '\n')* ; // sequence with a '\n' terminator

row : field (',' field)* ; // sequence with a ',' separator

field : INT ; // assume fields are just integers

INT : [0-9]+ ;