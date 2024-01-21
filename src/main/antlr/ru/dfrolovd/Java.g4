grammar Java;
@header {
    package ru.dfrolovd;
}

file
    : package? importLine* class EOF
    ;

package
    : 'package' packageName ';'
    ;

importLine
    :   'import' STATIC? importName ('.' '*')? ';'
    ;

class
    :   classModifier* classDeclaration
    ;

classModifier
    :   'public'
    |   'protected'
    |   'abstract'
    |   'static'
    ;

classDeclaration
    : 'class' className '{' classBody '}'
    ;

classBody
    : classBodyMember*
    ;

classBodyMember
    : classMethod
    | classField
    ;

classMethod
    :   modifier? type methodName methodParameters methodBody
    ;

classField
    :   modifier? constant? variableLine ';'
    ;

modifier
    : 'public'
    | 'private'
    | 'protected'
    ;

constant
    : constants +
    ;

constants
    : 'static'
    | 'final'
    ;

methodParameters
    :   '(' methodParametersDeclaration? ')'
    ;

methodParametersDeclaration
    :   type variableName (',' methodParametersDeclaration)?
    ;

methodBody
    : '{' instruction* '}' ;

variableLine
    :   type variableName ('=' expression)?
    ;

instruction
    :   variableLine ';'
    |   statement
    ;

type
    :   primitiveType ('[' ']')*
    |   className ('[' ']')*
    |   'void'
    ;

statement
    :   return
    |   if
    |   while
    |   for
    |   expression ';'
    ;

return
    : 'return' expression? ';'
    ;

if
    : 'if' '(' expression ')' methodBody ('else' methodBody)?
    ;

while
    : 'while' '(' expression ')' methodBody
    ;

for
    : 'for' '(' variableLine ';' expression ';' expression ')' methodBody
    ;

expression
    :   functionExpression
    |   literal
    |   prefixUnatyOp expression
    |   expression binaryOp expression
    |   expression suffixUnaryOp
    ;

binaryOp
    : '+' | '-' | '*' | '/' | '==' | '!=' | '<=' | '>=' | '<' | '>' | '||' | '&&'
    ;

prefixUnatyOp
    : '-'
    ;

suffixUnaryOp
    : '++' | '--'
    ;

functionExpression
    : className ('.' functionCall) +
    | functionCall ('.' functionCall) *
    ;

functionCall
    : methodName '(' (expression (',' expression)*)? ')'
    ;

literal
    :   numberLiteral
    |   variableName
    |   stringLiteral
    |   'null'
    ;

primitiveType
    :   'char'
    |   'short'
    |   'int'
    |   'long'
    |   'float'
    |   'double'
    ;

methodName : WORD ;
variableName : WORD ('[' ']')* ;
packageName: WORD ('.' WORD) *;
importName: WORD ('.' WORD) *;
className: WORD ;
numberLiteral: NUMBER;
stringLiteral: '"' (WORD)+ '"';

STATIC: 'static' ;
WORD : ([a-zA-Z0-9])+ ;
NUMBER: [0-9]+ ('.' [0-9]+)? ;
WS: [ \t\n\r]+ -> skip ;