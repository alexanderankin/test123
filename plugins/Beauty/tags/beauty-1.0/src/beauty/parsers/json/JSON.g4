/** Taken from "The Definitive ANTLR 4 Reference" by Terence Parr */

// Derived from http://json.org

// To regenerate the antlr files (lexer, parser, tokens, etc) run the 
// AntlrSideKickPlugin and use the "Generate files" action with this file being
// the active file in the view.

// This is the same grammar as used in the json sidekick, the only difference is
// the package name.

grammar JSON;

@header {
    package beauty.parsers.json;
}

json:   object
    |   array
    ;

object
    :   LBRACE pair (',' pair)* RBRACE
    |   LBRACE RBRACE // empty object
    ;
    
pair:   STRING ':' value ;

array
    :   LSQUARE value (',' value)* RSQUARE
    |   LSQUARE RSQUARE // empty array
    ;

value
    :   STRING
    |   NUMBER
    |   object  // recursion
    |   array   // recursion
    ;

STRING :  DQUOTE (ESC | ~["\\])* DQUOTE ;

fragment ESC :   '\\' (["\\/bfnrt] | UNICODE) ;
fragment UNICODE : 'u' HEX HEX HEX HEX ;
fragment HEX : [0-9a-fA-F] ;

NUMBER
    :   '-'? INT '.' [0-9]+ EXP? // 1.35, 1.35E-9, 0.3, -4.5
    |   '-'? INT EXP             // 1e10 -3e4
    |   '-'? INT                 // -3, 45
    |   'true'
    |   'false'
    |   'null'
    ;

fragment INT :   '0' | [1-9] [0-9]* ; // no leading zeros
fragment EXP :   [Ee] [+\-]? INT ; // \- since - means "range" inside [...]

WS  :   [ \t\n\r]+ -> skip ;

LBRACE  :   '{' ;
RBRACE  :   '}' ;
LSQUARE :   '[' ;
RSQUARE :   ']' ;
DQUOTE  :   '"' ;
