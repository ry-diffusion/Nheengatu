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
    : 'pacote' Identifier
    ;

statements
    : (statement)*
    ;

statement
    : decl | expr
    ;

decl
    : Identifier ':' tipo '=' expr
    ;

tipo
    : 'Inteiro'
    | 'Texto'
    | 'Real'
    ;

expr
    : numero
    | texto
    | variableReference
    | functionCall
    ;

functionCall
    : Identifier '(' (expr (',' expr)*)? ')'
    ;

numero
    : Number
    ;

texto
    : Text
    ;

variableReference
    : Identifier
    ;

Number
    : [0-9]+
    ;

// Exemplo: "olá mundo!" e 'olá mundo!'
Text
    : '"' ~["]* '"'
    | '\'' ~[']* '\''
    ;

NEWLINE : [\r\n]+ -> skip ;
WS : [ \t] -> skip ;

// alphanumeric may include numbers and _
Identifier
    : [a-zA-Z] [a-zA-Z0-9_]*
    ;
