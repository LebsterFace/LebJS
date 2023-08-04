Test.expect(0, Number.prototype.valueOf.length);
Test.expect(42, Number.prototype.valueOf.call(42));

// must be called with numeric `this`
Test.expectError("TypeError", "Number.prototype.valueOf requires that 'this' be a Number", () => Number.prototype.valueOf.call(true));
Test.expectError("TypeError", "Number.prototype.valueOf requires that 'this' be a Number", () => Number.prototype.valueOf.call([]));
Test.expectError("TypeError", "Number.prototype.valueOf requires that 'this' be a Number", () => Number.prototype.valueOf.call({}));
Test.expectError("TypeError", "Number.prototype.valueOf requires that 'this' be a Number", () => Number.prototype.valueOf.call(Symbol("foo")));
Test.expectError("TypeError", "Number.prototype.valueOf requires that 'this' be a Number", () => Number.prototype.valueOf.call("bar"));
Test.expectError("TypeError", "Number.prototype.valueOf requires that 'this' be a Number", () => Number.prototype.valueOf.call(1n));