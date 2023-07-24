Test.expect(1, Number.prototype.toFixed.length);

Test.expect("0.00000", (0).toFixed(5));
Test.expect("Infinity", Infinity.toFixed(6));
Test.expect("-Infinity", (-Infinity).toFixed(7));
Test.expect("NaN", NaN.toFixed(8));
Test.expect("12.816", 12.81646112.toFixed(3));
Test.expect("84.2300", 84.23.toFixed(4));
Test.expect("3.00003", 3.00003.toFixed(5));
Test.expect("0.0001", 0.00006.toFixed(4));

// Numbers >= 1e+21
Test.expect("1e+21", 1e21.toFixed(5));
Test.expect("1e+22", 1e22.toFixed(0));

// undefined, null and NaN are treated as 0 due to toFixed using ToIntegerOrInfinity.
Test.expect("1", 1.1.toFixed(undefined));
Test.expect("1", 1.1.toFixed(null));
Test.expect("1", 1.1.toFixed(NaN));

// decimal fixed digits gets converted to int
Test.expect("30.5", 30.521.toFixed(1.9));
Test.expect("30.52", 30.521.toFixed(2.2));

// errors
// must be called with numeric `this`
Test.expectError("TypeError", "This method requires that 'this' be a Number", () => Number.prototype.toFixed.call(true));
Test.expectError("TypeError", "This method requires that 'this' be a Number", () => Number.prototype.toFixed.call([]));
Test.expectError("TypeError", "This method requires that 'this' be a Number", () => Number.prototype.toFixed.call({}));
Test.expectError("TypeError", "This method requires that 'this' be a Number", () => Number.prototype.toFixed.call(Symbol("foo")));
Test.expectError("TypeError", "This method requires that 'this' be a Number", () => Number.prototype.toFixed.call("bar"));
// TODO: Test.expectError("TypeError", "This method requires that 'this' be a Number", () => Number.prototype.toFixed.call(1n));

// fixed digits RangeError
Test.expectError("RangeError", "toFixed() digits argument must be between 0 and 100", () => (0).toFixed(-Infinity));
Test.expectError("RangeError", "toFixed() digits argument must be between 0 and 100", () => (0).toFixed(-5));
Test.expectError("RangeError", "toFixed() digits argument must be between 0 and 100", () => (0).toFixed(105));
Test.expectError("RangeError", "toFixed() digits argument must be between 0 and 100", () => (0).toFixed(Infinity));