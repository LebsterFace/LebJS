# LittleLang
> ## **Simple programming language, strongly inspired by JavaScript**
>
> *Written in Java*
## Features
 - No loops
 - No conditionals
 - Very untested

## Programs
### `print.js`
```js
let myName = "Lebster"
print(myName)
```
### Output:
```
------- TOKENS -------
Token[type=Let, value=let, start=0, endPos=3]
Token[type=Identifier, value=myName, start=4, endPos=10]
Token[type=Assign, value==, start=11, endPos=12]
Token[type=StringLiteral, value=Lebster, start=13, endPos=22]
Token[type=Identifier, value=print, start=24, endPos=29]
Token[type=LParen, value=(, start=29, endPos=30]
Token[type=Identifier, value=myName, start=30, endPos=36]
Token[type=RParen, value=), start=36, endPos=37]
------- PROGRAM DUMP -------
VariableDeclaration:
  VariableDeclarator 'myName':
    StringLiteral: 'Lebster'
CallExpression 'print':
  Identifier: 'myName'
------- EXECUTION -------
Lebster
------- LAST VALUE -------
undefined
------- VARIABLES -------
Dictionary:
'print': NativeFunction
'myName': StringLiteral: 'Lebster'
------- [[ END ]] -------
```
