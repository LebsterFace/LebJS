# **LittleLang**
> ## **Simple programming language, strongly inspired by JavaScript**
>
> *Written in Java*
# Features
 - No loops
 - No conditionals
 - Very untested

# Programs
## __`print.js`__
```js
print("Hello world!");
```
### AST:
```
CallExpression:
  Callee:
    Identifier: 'print'
  Arguments:
    StringLiteral: 'Hello world!'
```
### Output:
```
Hello world!
```

## __`expressions.js`__
```js
print("Expect 11:");
print(4 * 2 + 3);
print("Expect 20:");
print(4 * (2 + 3));
```
### AST:
```
CallExpression:
  Callee:
    Identifier: 'print'
  Arguments:
    StringLiteral: 'Expect 11:'
CallExpression:
  Callee:
    Identifier: 'print'
  Arguments:
    BinaryExpression:
      BinaryExpression:
        Number: 4.0
          BinaryOp: Multiply
        Number: 2.0
        BinaryOp: Add
      Number: 3.0
CallExpression:
  Callee:
    Identifier: 'print'
  Arguments:
    StringLiteral: 'Expect 20:'
CallExpression:
  Callee:
    Identifier: 'print'
  Arguments:
    BinaryExpression:
      Number: 4.0
        BinaryOp: Multiply
      BinaryExpression:
        Number: 2.0
          BinaryOp: Add
        Number: 3.0
```
### Output:
```
Expect 11:
11.0
Expect 20:
20.0
```
