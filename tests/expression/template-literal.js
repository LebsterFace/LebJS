// plain literals with expression-like characters
Test.expect(`foo`, "foo");
Test.expect(`foo{`, "foo{");
Test.expect(`foo}`, "foo}");
Test.expect(`foo$`, "foo$");

// plain literals with escaped special characters
Test.expect(`foo\``, "foo`");
Test.expect(`foo\$`, "foo$");
Test.expect(`foo \${"bar"}`, 'foo ${"bar"}');

// literals in expressions
Test.expect(`foo ${undefined}`, "foo undefined");
Test.expect(`foo ${null}`, "foo null");
Test.expect(`foo ${5}`, "foo 5");
Test.expect(`foo ${true}`, "foo true");
Test.expect(`foo ${"bar"}`, "foo bar");

// objects in expressions
Test.expect(`foo ${{}}`, "foo [object Object]");
Test.expect(`foo ${{ bar: { baz: "qux" } }}`, "foo [object Object]");

// expressions at beginning of template literal
Test.expect(`${"foo"} bar baz`, "foo bar baz");
Test.expect(`${"foo bar baz"}`, "foo bar baz");

// multiple template literals
Test.expect(`foo ${"bar"} ${"baz"}`, "foo bar baz");

// variables in expressions
let a = 27;
Test.expect(`${a}`, "27");
Test.expect(`foo ${a}`, "foo 27");
Test.expect(`foo ${a ? "bar" : "baz"}`, "foo bar");
Test.expect(`foo ${(() => a)()}`, "foo 27");

// template literals in expressions
Test.expect(`foo ${`bar`}`, "foo bar");
Test.expect(`${`${`${`${"foo"}`} bar`}`}`, "foo bar");

// newline literals (not characters)
Test.expect(`foo
    bar`, "foo\r\n    bar");

// line continuation in literals (not characters)
Test.expect(`foo\
    bar`, "foo    bar");

// reference error from expressions
Test.expectError("ReferenceError", "b is not defined", () => `${b}`);