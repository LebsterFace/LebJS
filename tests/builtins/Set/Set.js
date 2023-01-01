// constructor properties
Test.expect(0, Set.length);
Test.expect("Set", Set.name);

// errors
// invalid array iterators
[-100, Infinity, NaN, {}, 152].forEach(value => {
    Test.expectError("TypeError", "is not iterable", () => new Set(value));
});

// called without new
Test.expectError("TypeError", "Set constructor must be called with `new`", () => Set());

// normal behavior
Test.expect("object", typeof new Set());

// constructor with single array argument
const a = new Set([0, 1, 2]);
Test.expect(true, a instanceof Set);
Test.expect(3, a.size);