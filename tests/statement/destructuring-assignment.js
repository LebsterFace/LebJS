// Basic parsing functionality
{
	Test.parse("const [a, b] = array;");
	Test.parse("const [a, , b] = array;");
	Test.parse("const [a = aDefault, b] = array;");
	Test.parse("const [a, b, ...rest] = array;");
	Test.parse("const [a, , b, ...rest] = array;");
	Test.parse("const [a, b, ...{ pop, push }] = array;");
	Test.parse("const [a, b, ...[c, d]] = array;");

	Test.parse("const { a, b } = obj;");
	Test.parse("const { a: a1, b: b1 } = obj;");
	Test.parse("const { a: a1 = aDefault, b = bDefault } = obj;");
	Test.parse("const { a, b, ...rest } = obj;");
	Test.parse("const { a: a1, b: b1, ...rest } = obj;");

	Test.parse("[a, b] = array;")
	Test.parse("[a, , b] = array;")
	Test.parse("[a = aDefault, b] = array;")
	Test.parse("[a, b, ...rest] = array;")
	Test.parse("[a, , b, ...rest] = array;")
	Test.parse("[a, b, ...{ pop, push }] = array;")
	Test.parse("[a, b, ...[c, d]] = array;")

	Test.parse("({ a, b } = obj);")
	Test.parse("({ a: a1, b: b1 } = obj);")
	Test.parse("({ a: a1 = aDefault, b = bDefault } = obj);")
	Test.parse("({ a, b, ...rest } = obj);")
	Test.parse("({ a: a1, b: b1, ...rest } = obj);")
}

// With spaces
{
    let object = { 'hello world': 123 }
    let { 'hello world': v } = object
    Test.expect(123, v);
}

let obj = { a: 'a', b: 'b', c: [1, 2, 3] };
{
	let { a, b, c } = obj;
	Test.expect(a, obj.a);
	Test.expect(b, obj.b);
	Test.expect(c, obj.c);
}

{
	let a, b, c;
	({ a, b, c } = obj);
	Test.expect(a, obj.a);
	Test.expect(b, obj.b);
	Test.expect(c, obj.c);
}

{
	let { c: [a, b, c] } = obj;
	Test.expect(a, obj.c[0]);
	Test.expect(b, obj.c[1]);
	Test.expect(c, obj.c[2]);
}

{
	let a, b, c;
	({ c: [a, b, c] } = obj);
	Test.expect(a, obj.c[0]);
	Test.expect(b, obj.c[1]);
	Test.expect(c, obj.c[2]);
}

Test.expectError("EvalError", "Invalid left-hand side in assignment", () => {
	eval("(({ a, b, c }) = obj)")
})

Test.expectSyntaxError('Unexpected token "else"', "let { else } = { else: 1 }")