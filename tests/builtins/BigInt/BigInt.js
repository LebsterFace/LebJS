// correct behavior
// basic functionality
Test.expect(1, BigInt.length);
Test.expect("BigInt", BigInt.name);

// constructor with numbers
Test.expect(0n, BigInt(0));
Test.expect(1n, BigInt(1));
Test.expect(1n, BigInt(+1));
Test.expect(-1n, BigInt(-1));
Test.expect(123n, BigInt(123n));

// constructor with strings
Test.expect(0n, BigInt(""));
Test.expect(0n, BigInt("0"));
Test.expect(1n, BigInt("1"));
Test.expect(1n, BigInt("+1"));
Test.expect(-1n, BigInt("-1"));
Test.expect(-1n, BigInt("-1"));
Test.expect(42n, BigInt("42"));
Test.expect(100n, BigInt("  \n  00100  \n  "));
Test.expect(3323214327642987348732109829832143298746432437532197321n, BigInt("3323214327642987348732109829832143298746432437532197321"));

// constructor with objects
Test.expect(0n, BigInt([]));

// base-2 strings
Test.expect(0n, BigInt("0b0"));
Test.expect(0n, BigInt("0B0"));
Test.expect(1n, BigInt("0b1"));
Test.expect(1n, BigInt("0B1"));
Test.expect(2n, BigInt("0b10"));
Test.expect(2n, BigInt("0B10"));
Test.expect(1267650600228229401496703205375n, BigInt(`0b${"1".repeat(100)}`));

// base-8 strings
Test.expect(0n, BigInt("0o0"));
Test.expect(0n, BigInt("0O0"));
Test.expect(1n, BigInt("0o1"));
Test.expect(1n, BigInt("0O1"));
Test.expect(7n, BigInt("0o7"));
Test.expect(7n, BigInt("0O7"));
Test.expect(8n, BigInt("0o10"));
Test.expect(8n, BigInt("0O10"));
Test.expect(1267650600228229401496703205375n, BigInt(`0o1${"7".repeat(33)}`));

// base-16 strings
Test.expect(0n, BigInt("0x0"));
Test.expect(0n, BigInt("0X0"));
Test.expect(1n, BigInt("0x1"));
Test.expect(1n, BigInt("0X1"));
Test.expect(15n, BigInt("0xf"));
Test.expect(15n, BigInt("0Xf"));
Test.expect(16n, BigInt("0x10"));
Test.expect(16n, BigInt("0X10"));
Test.expect(1267650600228229401496703205375n, BigInt(`0x${"f".repeat(25)}`));

// only coerces value once
let calls = 0;
const value = {
	[Symbol.toPrimitive]() {
		Test.expect(0, calls);
		++calls;
		return "123";
	},
};

Test.expect(123n, BigInt(value));
Test.expect(1, calls);

// errors
// cannot be constructed with "new"
Test.expectError("TypeError", "BigInt is not a constructor", () => new BigInt());

// invalid arguments
Test.expectError("TypeError", "Cannot convert null to a BigInt", () => BigInt(null));
Test.expectError("TypeError", "Cannot convert undefined to a BigInt", () => BigInt(undefined));
Test.expectError("TypeError", "Cannot convert Symbol() to a BigInt", () => BigInt(Symbol()));
Test.expectError("SyntaxError", "Cannot convert 'foo' to a BigInt", () => BigInt("foo"));
Test.expectError("SyntaxError", "Cannot convert '123n' to a BigInt", () => BigInt("123n"));
Test.expectError("SyntaxError", "Cannot convert '1+1' to a BigInt", () => BigInt("1+1"));
Test.expectError("SyntaxError", "Cannot convert '[object Object]' to a BigInt", () => BigInt({}));

// invalid numeric arguments
Test.expectError("RangeError", '1.23 cannot be converted to BigInt because it is not an integer', () => BigInt(1.23));
Test.expectError("RangeError", 'Infinity cannot be converted to BigInt because it is not an integer', () => BigInt(Infinity));
Test.expectError("RangeError", '-Infinity cannot be converted to BigInt because it is not an integer', () => BigInt(-Infinity));
Test.expectError("RangeError", 'NaN cannot be converted to BigInt because it is not an integer', () => BigInt(NaN));

// invalid string for base
Test.expectError("SyntaxError", "Cannot convert '0b' to a BigInt", () => BigInt("0b"));
Test.expectError("SyntaxError", "Cannot convert '0b2' to a BigInt", () => BigInt("0b2"));
Test.expectError("SyntaxError", "Cannot convert '0B02' to a BigInt", () => BigInt("0B02"));
Test.expectError("SyntaxError", "Cannot convert '-0b1' to a BigInt", () => BigInt("-0b1"));
Test.expectError("SyntaxError", "Cannot convert '-0B1' to a BigInt", () => BigInt("-0B1"));
Test.expectError("SyntaxError", "Cannot convert '0o' to a BigInt", () => BigInt("0o"));
Test.expectError("SyntaxError", "Cannot convert '0o8' to a BigInt", () => BigInt("0o8"));
Test.expectError("SyntaxError", "Cannot convert '0O08' to a BigInt", () => BigInt("0O08"));
Test.expectError("SyntaxError", "Cannot convert '-0o1' to a BigInt", () => BigInt("-0o1"));
Test.expectError("SyntaxError", "Cannot convert '-0O1' to a BigInt", () => BigInt("-0O1"));
Test.expectError("SyntaxError", "Cannot convert '0x' to a BigInt", () => BigInt("0x"));
Test.expectError("SyntaxError", "Cannot convert '0xg' to a BigInt", () => BigInt("0xg"));
Test.expectError("SyntaxError", "Cannot convert '0X0g' to a BigInt", () => BigInt("0X0g"));
Test.expectError("SyntaxError", "Cannot convert '-0x1' to a BigInt", () => BigInt("-0x1"));
Test.expectError("SyntaxError", "Cannot convert '-0X1' to a BigInt", () => BigInt("-0X1"));
Test.expectError("SyntaxError", "Cannot convert 'a' to a BigInt", () => BigInt("a"));
Test.expectError("SyntaxError", "Cannot convert '-1a' to a BigInt", () => BigInt("-1a"));