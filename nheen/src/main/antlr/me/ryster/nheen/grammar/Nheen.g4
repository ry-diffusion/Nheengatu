grammar Nheen;

parse
  :  line* EOF
  ;

line
  :  Space* (keyValue | LineBreak)
  ;

keyValue
  :  key separatorAndValue eol
  ;

key
  :  keyChar+
  ;

keyChar
  :  AlphaNum
  ;

separatorAndValue
  :  (Space | Equals) chars+
  ;

chars
  :  AlphaNum
  |  Space
  |  Equals
  ;

eol
  :  LineBreak
  |  EOF
  ;

Equals
  : '='
  ;

LineBreak
  :  '\r'? '\n'
  |  '\r'
  ;

Space
  :  ' '
  |  '\t'
  |  '\f'
  ;

AlphaNum
  :  'a'..'z'
  |  'A'..'Z'
  |  '0'..'9'
  ;