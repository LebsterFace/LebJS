// errors
// must be called with numeric `this`
Test.expectError("TypeError", "This method requires that 'this' be a Number", () => Number.prototype.toExponential.call(true));
Test.expectError("TypeError", "This method requires that 'this' be a Number", () => Number.prototype.toExponential.call([]));
Test.expectError("TypeError", "This method requires that 'this' be a Number", () => Number.prototype.toExponential.call({}));
Test.expectError("TypeError", "This method requires that 'this' be a Number", () => Number.prototype.toExponential.call(Symbol("foo")));
Test.expectError("TypeError", "This method requires that 'this' be a Number", () => Number.prototype.toExponential.call("bar"));
// TODO: Test.expectError("TypeError", "This method requires that 'this' be a Number", () => Number.prototype.toExponential.call(1n));

// fraction digits must be coercible to a number
Test.expectError("TypeError", "Cannot convert symbol to number", () => (0).toExponential(Symbol("foo")));
// TODO: Test.expectError("TypeError", "Cannot convert BigInt to number", () => (0).toExponential(1n));

// out of range fraction digits
Test.expectError("RangeError", "Fraction Digits must be an integer no less than 0, and no greater than 100", () => (0).toExponential(-Infinity));
Test.expectError("RangeError", "Fraction Digits must be an integer no less than 0, and no greater than 100", () => (0).toExponential(-1));
Test.expectError("RangeError", "Fraction Digits must be an integer no less than 0, and no greater than 100", () => (0).toExponential(101));
Test.expectError("RangeError", "Fraction Digits must be an integer no less than 0, and no greater than 100", () => (0).toExponential(Infinity));

// special values
Test.expect("Infinity", (Infinity).toExponential(6));
Test.expect("-Infinity", (-Infinity).toExponential(7));
Test.expect("NaN", (NaN).toExponential(8));
Test.expect("0e+0", (0).toExponential(0));
Test.expect("0.0e+0", (0).toExponential(1));
Test.expect("0.000e+0", (0).toExponential(3));

// zero exponent
Test.expect("1e+0", (1).toExponential(0));
Test.expect("5.0e+0", (5).toExponential(1));
Test.expect("9.000e+0", (9).toExponential(3));
Test.expect(`3.${'0'.repeat(100, (3).toExponential(100))}e+0`);

// positive exponent
Test.expect("1e+1", (12).toExponential(0));
Test.expect("3.5e+2", (345).toExponential(1));
Test.expect("6.789e+3", (6789).toExponential(3));

// negative exponent
Test.expect("1e-1", 0.12.toExponential(0));
Test.expect("3.5e-2", 0.0345.toExponential(1));
Test.expect("6.789e-3", 0.006789.toExponential(3));

// undefined precision
Test.expect("1.23456e+2", 123.456.toExponential());
Test.expect("1.3e+1", (13).toExponential());
Test.expect("1e+2", (100).toExponential());
Test.expect("3.45e+2", (345).toExponential());
Test.expect("6.789e+3", (6789).toExponential());
Test.expect("1.3e-1", 0.13.toExponential());
Test.expect("3.45e-2", 0.0345.toExponential());
Test.expect("6.789e-3", 0.006789.toExponential());
Test.expect("1.1e-32", 1.1e-32.toExponential());
Test.expect("1.23456e+2", 123.456.toExponential());