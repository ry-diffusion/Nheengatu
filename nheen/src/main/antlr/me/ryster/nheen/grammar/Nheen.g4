grammar Nheen;

/**
pacote Principal

inicio
    x: Inteiro = 2
    imprima("olá mundo!")
fim
**/


file
    : pacote inicio
    ;

inicio
    : 'inicio' statements 'fim'
    ;

pacote
    : 'pacote' ID
    ;

statements
    : (statement)*
    ;

statement
    : decl | expr
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
    | ID
    | functionCall
    ;

functionCall
    : ID '(' (expr (',' expr)*)? ')'
    ;

numero
    : NUMERO
    ;

texto
    : STRING
    ;

NUMERO
    : [0-9]+
    ;

// Exemplo: "olá mundo!" e 'olá mundo!'
STRING
    : '"' ~["]* '"'
    | '\'' ~[']* '\''
    ;

NEWLINE : [\r\n]+ -> skip ;
WS : [ \t] -> skip ;

// alphanumeric may include numbers and _
ID
    : [a-zA-Z] [a-zA-Z0-9_]*
    ;

