// TODO: Optional chaining tests
Test.parse("for (;;) /a+/");
Test.parse("for (a in / b/) {}");

Test.parse(`{ blah.blah; }\n/foo/`);
Test.expectError("SyntaxError", "Unexpected end of input", () => Test.parse("``/foo/"));
Test.expectError("SyntaxError", "Unexpected end of input", () => Test.parse("1/foo/"));
Test.parse("1/foo");

// TODO: ASI
// Test.parse("{} /foo/");
// Test.parse("{} /=/");
// Test.parse("{} /=a/");
// Test.parse("{} /* */ /=a/");
// Test.parse("{} /* /a/ */ /=a/");

Test.parse("(function () {} / 1)");
Test.parse("(function () {} / 1)");

Test.parse("+a++ / 1");
Test.parse("+a-- / 1");
Test.parse("a.in / b");
Test.parse("a.instanceof / b");

// TODO: Test.parse("async / b");
Test.parse("a.delete / b");
Test.parse("delete / b/");
Test.parse("a.in / b");
Test.parse("for (a in / b/) {}");
Test.parse("a.instanceof / b");
Test.parse("a instanceof / b/");
Test.parse("new / b/");
Test.parse("null / b");
Test.parse("for (a of / b/) {}");
Test.parse("a.return / b");
Test.parse("function foo() { return / b/ }");
Test.parse("throw / b/");
Test.parse("a.typeof / b");
Test.parse("a.void / b");
Test.parse("void / b/");

// TODO: Test.parse("await / b");
// TODO: Test.expectError("SyntaxError", "Unexpected end of input", () => Test.parse("await / b/"));
// TODO: Test.expectError("SyntaxError", "Unexpected end of input", () => Test.parse("async function foo() { await / b }"));
// TODO: Test.parse("async function foo() { await / b/ }");

// TODO: Test.parse("yield / b");
// TODO: Test.expectError("SyntaxError", "Unexpected end of input", () => Test.parse("yield / b/"));
// TODO: Test.expectError("SyntaxError", "Unexpected end of input", () => Test.parse("function* foo() { yield / b }"));
// TODO: Test.parse("function* foo() { yield / b/ }");

Test.parse("this / 1");
Test.expectError("SyntaxError", "Unexpected end of input", () => Test.parse("this / 1 /"));
Test.parse("this / 1 / 1");

