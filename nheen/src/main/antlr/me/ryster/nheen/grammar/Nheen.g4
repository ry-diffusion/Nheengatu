grammar Nheen;

/**
pacote Principal

inicio
    x := 2
    imprima("olá mundo!")
fim
**/


file
    : pacote inicio
    ;

inicio
    : 'inicio' statements 'fim'
    ;

se
    : 'se' expr 'então' statements  'fim'
    | 'se' expr 'então' statements senão 'fim'
    ;

senão
    : 'senão' statements
    ;

pacote
    : 'pacote' Identifier
    ;

statements
    : (statement)*
    ;

statement
    : decl | expr | se
    ;

decl
    : Identifier ':=' expr
    ;

expr
    : value
    | operatorChain
    ;


operatorChain
    : value operator value (operator value)*
    ;

value
    : numberLiteral
    | functionCall
    | textLiteral
    | variableReference
    | expressionParen
    ;

expressionParen
    : '(' expr ')'
    ;

operator
    : '!' | '+' | '-' | '*' | '/' | '==' | '!=' | '<' | '<=' | '>' | '>='
    ;

functionCall
    : Identifier '(' (expr (',' expr)*)? ')'
    ;

numberLiteral
    : Number
    ;

textLiteral
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
