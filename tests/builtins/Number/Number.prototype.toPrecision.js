// errors
// must be called with numeric `this`
Test.expectError("TypeError", "Number.prototype.toPrecision requires that 'this' be a Number", () => Number.prototype.toPrecision.call(true));
Test.expectError("TypeError", "Number.prototype.toPrecision requires that 'this' be a Number", () => Number.prototype.toPrecision.call([]));
Test.expectError("TypeError", "Number.prototype.toPrecision requires that 'this' be a Number", () => Number.prototype.toPrecision.call({}));
Test.expectError("TypeError", "Number.prototype.toPrecision requires that 'this' be a Number", () => Number.prototype.toPrecision.call(Symbol("foo")));
Test.expectError("TypeError", "Number.prototype.toPrecision requires that 'this' be a Number", () => Number.prototype.toPrecision.call("bar"));
Test.expectError("TypeError", "Number.prototype.toPrecision requires that 'this' be a Number", () => Number.prototype.toPrecision.call(1n));

// precision must be coercible to a number
Test.expectError("TypeError", "Cannot convert a Symbol value to a number", () => (0).toPrecision(Symbol("foo")));
Test.expectError("TypeError", "Cannot convert a BigInt value to a number", () => (0).toPrecision(1n));

// out of range precision
Test.expectError("RangeError", "toPrecision() argument must be between 1 and 100", () => (0).toPrecision(-Infinity));
Test.expectError("RangeError", "toPrecision() argument must be between 1 and 100", () => (0).toPrecision(0));
Test.expectError("RangeError", "toPrecision() argument must be between 1 and 100", () => (0).toPrecision(101));
Test.expectError("RangeError", "toPrecision() argument must be between 1 and 100", () => (0).toPrecision(Infinity));

// special values
Test.expect("Infinity", Infinity.toPrecision(6));
Test.expect("-Infinity", (-Infinity).toPrecision(7));
Test.expect("NaN", NaN.toPrecision(8));
Test.expect("0", (0).toPrecision(1));
Test.expect("0.00", (0).toPrecision(3));
Test.expect("0.0000", (0).toPrecision(5));

// undefined precision yields plain number-to-string conversion
Test.expect("123", (123).toPrecision(undefined));
Test.expect("3.14", 3.14.toPrecision(undefined));

// formatted as exponential string
// exponent < -6
Test.expect("2.0000e-7", 0.0000002.toPrecision(5));
Test.expect("1.89e-9", 0.00000000189.toPrecision(3));
Test.expect("1.9e-9", 0.00000000189.toPrecision(2));

// exponent >= precision
Test.expect("1e+2", (100).toPrecision(1));
Test.expect("1.0e+2", (100).toPrecision(2));
Test.expect("1.23e+6", (1234589).toPrecision(3));
Test.expect("1.235e+6", (1234589).toPrecision(4));
Test.expect("1.2346e+6", (1234589).toPrecision(5));

// formatted without decimal
// exponent == precision - 1
Test.expect("1", (1).toPrecision(1));
Test.expect("123", (123).toPrecision(3));
Test.expect("123", 123.45.toPrecision(3));

// non-negative exponent
Test.expect("1.000", (1).toPrecision(4));
Test.expect("123.0", (123).toPrecision(4));
Test.expect("123.5", 123.45.toPrecision(4));
Test.expect(`3.${'0'.repeat(99)}`, (3).toPrecision(100));

// negative exponent
Test.expect("0.1", 0.1.toPrecision(1));
Test.expect("0.0123", 0.0123.toPrecision(3));
Test.expect("0.00123", 0.0012345.toPrecision(3));
Test.expect("0.001234", 0.0012345.toPrecision(4));