let x = 1;
Test.expect(x, eval('x'));
eval("let y = 2;")
Test.expectError("ReferenceError", "y is not defined", () => y);