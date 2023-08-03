let a = 10;
Test.expect(a, 10);
let b = a + a;
Test.expect(b, 20);
let c = b + a;
Test.expect(c, 30);
Test.expectError("ReferenceError", "foo is not defined", () => foo(bar));
Test.expectError("ReferenceError", "bar is not defined", () => "Not Callable"(bar));