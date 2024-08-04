grammar Nheen;

/**
inicio
    x: Inteiro := 2
    imprima("olá mundo!")
fim
**/

inicio
    : 'inicio' statements 'fim'
    ;

statements
    : (statement)*
    ;

statement
    : decl | builtin
    ;

decl
    : ID ':' tipo '=' expr
    ;

tipo
    : 'Inteiro'
    | 'Texto'
    | 'Real'
    ;

expr
    : numero
    | texto
    ;

numero
    : NUMERO
    ;

texto
    : STRING
    ;

builtin
    : 'imprima' '(' expr ')'
    | 'leia' '(' ID ')'
    ;

NUMERO
    : [0-9]+
    ;

STRING
    : '"' ~["]* '"'
    ;

NEWLINE : [\r\n]+ -> skip ;
WS : [ \t] -> skip ;
ID: [a-zA-Z]+ ;