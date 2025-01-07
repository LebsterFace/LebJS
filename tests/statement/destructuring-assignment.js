// Basic parsing functionality
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

// With spaces
let object = { 'hello world': 123 };
let { 'hello world': v, fake, fakeButDefault = 'hi' } = object;
Test.expect(123, v);
Test.expect(undefined, fake);
Test.expect('hi', fakeButDefault);

let obj = { a: 'a', b: 'b', c: [1, 2, 3] };
{
	let { a, b, c } = obj;
	Test.expect(a, obj.a);
	Test.expect(b, obj.b);
	Test.expect(c, obj.c);
}

{
    let a, foo;
    ({ a, foo = 1 } = obj);
    Test.expect(a, obj.a);
    Test.expect(foo, 1);
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

{
	let object = { bar: 'hi' };
	([ object.bar ] = [ 999 ]);
	Test.expectEqual({ bar: 999 }, object);
}

Test.expectError("SyntaxError", "Invalid left-hand side in assignment", () => Test.parse("(({ a, b, c }) = obj)"));
Test.expectError("SyntaxError", "Unexpected token 'else'", () => Test.parse("let { else } = { else: 1 };"));
Test.expectError("SyntaxError", 'Invalid shorthand property initializer', () => Test.parse("let bar = { foo = 1 };"))
Test.expectError("SyntaxError", 'Invalid shorthand property initializer', () => Test.parse("let { foo = 1 } = (1, 2, { a: 1, b: 2, c: 3, foo = 1 });"))